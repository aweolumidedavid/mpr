package com.test.mpr.service

import com.test.mpr.dto.InitiateTransactionRequest
import com.test.mpr.dto.PaymentResponse
import com.test.mpr.dto.PaymentStatus
import com.test.mpr.dto.TransactionListRequest
import com.test.mpr.dto.TransactionListResponse
import com.test.mpr.dto.TransactionResponse
import com.test.mpr.entity.Transaction
import com.test.mpr.entity.enums.TransactionStatus
import com.test.mpr.exception.*
import com.test.mpr.repository.TransactionRepository
import com.test.mpr.utils.ReferenceGenerator
import com.test.mpr.utils.TransactionLockManager
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal

@Service
class TransactionService(
    private val transactionRepository: TransactionRepository,
    private val merchantService: MerchantService,
    private val transactionLockManager: TransactionLockManager
) {
    
    companion object {
        private val FEE_PERCENTAGE = BigDecimal("0.015")
        private val MAX_FEE = BigDecimal("200.00")
    }
    
    @Transactional
    fun initiateTransaction(request: InitiateTransactionRequest): TransactionResponse {
        // fetch merchant
        val merchant = merchantService.getActiveMerchantById(request.merchantId)

        // check if transaction already exists in database
        transactionRepository.findByMerchantRef(request.merchantRef)?.let {
            throw TransactionAlreadyExistsException(request.merchantRef)
        }

        return transactionLockManager.executeWithLockAndCleanup(request.merchantRef) {

            val fee = calculateFee(request.amount)
            val internalRef = ReferenceGenerator.generateInternalRef()

            val transaction = Transaction(
                amount = request.amount,
                currency = request.currency,
                status = TransactionStatus.INITIATED,
                merchantRef = request.merchantRef,
                internalRef = internalRef,
                fee = fee,
                merchant = merchant
            )
            
            try {
                val customerDebitResponse = simulateCustomerDebit(request.amount, fee, request.currency)

                val transactionStatus = when (customerDebitResponse.status) {
                    PaymentStatus.SUCCESS -> TransactionStatus.SUCCESS
                    PaymentStatus.FAILED, PaymentStatus.DECLINED, 
                    PaymentStatus.INSUFFICIENT_FUNDS, PaymentStatus.INVALID_ACCOUNT,
                    PaymentStatus.TIMEOUT, PaymentStatus.GATEWAY_ERROR -> TransactionStatus.FAILED
                }
                
                // update existing transaction with final status and save
                val updatedTransaction = transaction.copy(
                    status = transactionStatus,
                    internalRef = transaction.internalRef
                )
                val savedTransaction = transactionRepository.save(updatedTransaction)
                
                return@executeWithLockAndCleanup mapToResponse(savedTransaction)
                
            } catch (e: Exception) {
                /**
                 * please note that this only saves
                 * a failed transaction if it hasn't
                 * already been saved
                 */
                val failedTransaction = transaction.copy(status = TransactionStatus.FAILED)
                transactionRepository.save(failedTransaction)
                throw e
            }
        }
    }
    
    fun getTransactions(request: TransactionListRequest): TransactionListResponse {
        val pageable: Pageable = PageRequest.of(request.page, request.size)
        val merchant = request.merchantId?.let { merchantService.getActiveMerchantById(it) }
        val page = if (merchant?.id != null) {
            transactionRepository.findByMerchantIdAndFilters(
                merchantId = merchant.id!!,
                status = request.status,
                startDate = request.startDate,
                endDate = request.endDate,
                pageable = pageable
            )
        } else {
            Page.empty(pageable)
        }
        
        return TransactionListResponse(
            transactions = page.content.map { mapToResponse(it) },
            totalElements = page.totalElements,
            totalPages = page.totalPages,
            currentPage = page.number,
            pageSize = page.size
        )
    }
    
    fun getTransactionByInternalRef(internalRef: String): TransactionResponse {
        val transaction = transactionRepository.findByInternalRef(internalRef)
            ?: throw TransactionNotFoundException(internalRef)
        return mapToResponse(transaction)
    }
    
    private fun calculateFee(amount: BigDecimal): BigDecimal {
        val calculatedFee = amount * FEE_PERCENTAGE
        return if (calculatedFee > MAX_FEE) MAX_FEE else calculatedFee
    }
    
    private fun simulateCustomerDebit(amount: BigDecimal, fee: BigDecimal, currency: String): PaymentResponse {
        if (amount <= BigDecimal.ZERO) {
            throw InvalidTransactionAmountException(amount.toString())
        }
        
        Thread.sleep(100)

        /**
         * different mocks for testing
         */
        return when {
            amount > BigDecimal("10000") -> {
                PaymentResponse(
                    success = false,
                    status = PaymentStatus.DECLINED,
                    amount = amount,
                    currency = currency,
                    fee = fee,
                    message = "Transaction declined due to high amount",
                    gatewayReference = ReferenceGenerator.generateInternalRef(),
                    errorCode = "AMOUNT_LIMIT_EXCEEDED"
                )
            }
            amount < BigDecimal("10") -> {
                PaymentResponse(
                    success = false,
                    status = PaymentStatus.INSUFFICIENT_FUNDS,
                    amount = amount,
                    currency = currency,
                    fee = fee,
                    message = "Insufficient funds in customer account",
                    gatewayReference = ReferenceGenerator.generateInternalRef(),
                    errorCode = "INSUFFICIENT_FUNDS"
                )
            }
            else -> {
                PaymentResponse(
                    success = true,
                    status = PaymentStatus.SUCCESS,
                    transactionId = ReferenceGenerator.generateInternalRef(),
                    amount = amount,
                    currency = currency,
                    fee = fee,
                    message = "Payment processed successfully",
                    gatewayReference = ReferenceGenerator.generateInternalRef()
                )
            }
        }
    }
    
    private fun mapToResponse(transaction: Transaction): TransactionResponse {
        return TransactionResponse(
            id = transaction.id!!,
            amount = transaction.amount,
            currency = transaction.currency,
            status = transaction.status,
            merchantRef = transaction.merchantRef,
            internalRef = transaction.internalRef,
            fee = transaction.fee,
            merchantId = transaction.merchant.id!!,
            settlementBatchId = transaction.settlementBatch?.id,
            createdAt = transaction.createdAt,
            updatedAt = transaction.updatedAt
        )
    }
} 