package com.test.mpr.service

import com.test.mpr.config.SchedulingConfig
import com.test.mpr.dto.SettlementBatchResponse
import com.test.mpr.dto.SettlementSummaryResponse
import com.test.mpr.entity.SettlementBatch
import com.test.mpr.entity.enums.TransactionStatus
import com.test.mpr.exception.NoUnsettledTransactionsException
import com.test.mpr.exception.SettlementBatchNotFoundException
import com.test.mpr.exception.SettlementProcessingException
import com.test.mpr.repository.SettlementBatchRepository
import com.test.mpr.repository.TransactionRepository
import com.test.mpr.utils.ReferenceGenerator
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class SettlementService(
    private val settlementBatchRepository: SettlementBatchRepository,
    private val transactionRepository: TransactionRepository,
    private val merchantService: MerchantService,
    private val schedulingConfig: SchedulingConfig
) {
    
    private val logger = LoggerFactory.getLogger(SettlementService::class.java)
    
    companion object {
        /***
         * process 5 transactions per batch,
         * to handle millions of transactions for given time,
         * we can kafka to process the jobs
         */
        private const val BATCH_SIZE = 5 
    }
    
    @Scheduled(cron = "\${process.schedule.interval}", zone = "\${process.schedule.timezone}")
    @Transactional
    fun processSettlements() {
        if (!schedulingConfig.isSchedulingEnabled()) {
            logger.info("Settlement processing is disabled via configuration")
            return
        }

        logger.info("Starting scheduled settlement processing with cron: ${schedulingConfig.interval}")
        
        try {
            val activeMerchants = merchantService.getAllActiveMerchants()
            logger.info("Found ${activeMerchants.size} active merchants for settlement processing")
            
            for (merchant in activeMerchants) {
                try {
                    processSettlementsForMerchant(merchant.id!!)
                    logger.info("Successfully processed settlements for merchant: ${merchant.businessName}")
                } catch (e: Exception) {
                    logger.error("Failed to process settlements for merchant ${merchant.businessName}: ${e.message}", e)
                }
            }
            
            logger.info("Completed scheduled settlement processing")
        } catch (e: Exception) {
            logger.error("Error during scheduled settlement processing: ${e.message}", e)
        }
    }
    
    @Transactional
    fun processSettlementsForMerchant(merchantId: Long) {
        logger.info("Processing settlements for merchant ID: $merchantId")

        val unsettledTransactions = transactionRepository.findUnsettledTransactionsByMerchantAndStatus(
            merchantId = merchantId,
            status = TransactionStatus.SUCCESS
        )
        
        if (unsettledTransactions.isEmpty()) {
            logger.info("No unsettled transactions found for merchant ID: $merchantId")
            throw NoUnsettledTransactionsException(merchantId)
        }
        
        logger.info("Found ${unsettledTransactions.size} unsettled transactions for merchant ID: $merchantId")

        val batches = unsettledTransactions.chunked(BATCH_SIZE)
        logger.info("Processing ${batches.size} batches for merchant ID: $merchantId")
        
        for ((index, batch) in batches.withIndex()) {
            try {
                createSettlementBatch(merchantId, batch)
                logger.info("Successfully created settlement batch ${index + 1}/${batches.size} for merchant ID: $merchantId")
            } catch (e: Exception) {
                logger.error("Failed to create settlement batch ${index + 1}/${batches.size} for merchant ID: $merchantId: ${e.message}", e)
                throw e
            }
        }
        
        logger.info("Completed settlement processing for merchant ID: $merchantId")
    }
    
    private fun createSettlementBatch(merchantId: Long, transactions: List<com.test.mpr.entity.Transaction>): SettlementBatchResponse {
        try {
            val merchant = merchantService.getActiveMerchantById(merchantId)
            val batchRef = ReferenceGenerator.generateBatchRef()
            
            val totalAmount = transactions.sumOf { it.amount }
            /**
             * totalFee is credited to the platform
             */
            val totalFee = transactions.sumOf { it.fee }
            
            val settlementBatch = SettlementBatch(
                batchRef = batchRef,
                merchant = merchant,
                totalAmount = totalAmount,
                transactions = mutableListOf()
            )
            
            val savedBatch = settlementBatchRepository.save(settlementBatch)
            
            /**
             * update transactions with settlement batch reference
             */
            transactions.forEach { transaction ->
                val updatedTransaction = transaction.copy(settlementBatch = savedBatch)
                transactionRepository.save(updatedTransaction)
            }
            
            return mapToResponse(savedBatch, transactions.size)
        } catch (e: Exception) {
            throw SettlementProcessingException("Failed to create settlement batch for merchant $merchantId: ${e.message}")
        }
    }
    
    fun getSettlementSummary(merchantId: Long): SettlementSummaryResponse {
        merchantService.getActiveMerchantById(merchantId)
        
        val batches = settlementBatchRepository.findByMerchantIdOrderByCreatedAtDesc(merchantId)
        val totalBatches = settlementBatchRepository.countByMerchantId(merchantId)
        val lastSettlementDate = settlementBatchRepository.findLastSettlementDateByMerchantId(merchantId)
        
        val totalAmount = batches.sumOf { it.totalAmount }
        val totalFee = batches.sumOf { batch ->
            batch.transactions.sumOf { it.fee }
        }
        val totalTransactions = batches.sumOf { it.transactions.size }
        
        return SettlementSummaryResponse(
            merchantId = merchantId,
            totalBatches = totalBatches.toInt(),
            totalAmount = totalAmount,
            totalFee = totalFee,
            totalTransactions = totalTransactions,
            lastSettlementDate = lastSettlementDate
        )
    }
    
    fun getSettlementBatches(merchantId: Long): List<SettlementBatchResponse> {
        merchantService.getActiveMerchantById(merchantId)
        val batches = settlementBatchRepository.findByMerchantIdOrderByCreatedAtDesc(merchantId)
        return batches.map { mapToResponse(it, it.transactions.size) }
    }
    
    private fun mapToResponse(batch: SettlementBatch, transactionCount: Int): SettlementBatchResponse {
        return SettlementBatchResponse(
            id = batch.id!!,
            batchRef = batch.batchRef,
            merchantId = batch.merchant.id!!,
            totalAmount = batch.totalAmount,
            totalFee = batch.transactions.sumOf { it.fee },
            transactionCount = transactionCount,
            createdAt = batch.createdAt,
            updatedAt = batch.updatedAt
        )
    }
} 