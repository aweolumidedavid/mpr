package com.test.mpr.controller

import com.test.mpr.dto.*
import com.test.mpr.service.TransactionService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.time.LocalDateTime

@RestController
@RequestMapping("/api/v1/transactions")
@Tag(name = "Transaction Management", description = "APIs for managing transactions")
class TransactionController(
    private val transactionService: TransactionService
) {
    
    @PostMapping("/debit/initiate")
    @Operation(summary = "Initiate a transaction", description = "Initiates a new transaction with idempotency support")
    fun initiateTransaction(@Valid @RequestBody request: InitiateTransactionRequest): ResponseEntity<ApiResponse<TransactionResponse>> {
        val transaction = transactionService.initiateTransaction(request)
        return ResponseEntity.ok(ApiResponse.success(transaction, "Transaction initiated successfully"))
    }
    
    @GetMapping
    @Operation(summary = "List transactions", description = "Retrieves paginated list of transactions with optional filtering")
    fun getTransactions(
        @RequestParam(required = false) merchantId: Long?,
        @RequestParam(required = false) status: String?,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) startDate: LocalDateTime?,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) endDate: LocalDateTime?,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "20") size: Int
    ): ResponseEntity<ApiResponse<TransactionListResponse>> {
        val request = TransactionListRequest(
            merchantId = merchantId,
            status = status?.let { com.test.mpr.entity.enums.TransactionStatus.fromValue(it) },
            startDate = startDate,
            endDate = endDate,
            page = page,
            size = size
        )
        
        val transactions = transactionService.getTransactions(request)
        return ResponseEntity.ok(ApiResponse.success(transactions, "Transactions retrieved successfully"))
    }
    
    @GetMapping("/{internalRef}")
    @Operation(summary = "Get transaction by internal reference", description = "Retrieves transaction details by internal reference")
    fun getTransaction(@PathVariable internalRef: String): ResponseEntity<ApiResponse<TransactionResponse>> {
        val transaction = transactionService.getTransactionByInternalRef(internalRef)
        return ResponseEntity.ok(ApiResponse.success(transaction, "Transaction retrieved successfully"))
    }
} 