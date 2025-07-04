package com.test.mpr.dto

import com.test.mpr.entity.enums.TransactionStatus
import java.math.BigDecimal
import java.time.LocalDateTime

data class TransactionResponse(
    val id: Long,
    val amount: BigDecimal,
    val currency: String,
    val status: TransactionStatus,
    val merchantRef: String,
    val internalRef: String,
    val fee: BigDecimal,
    val merchantId: Long,
    val settlementBatchId: Long?,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime?
) 