package com.test.mpr.dto

import java.math.BigDecimal
import java.time.LocalDateTime

data class SettlementBatchResponse(
    val id: Long,
    val batchRef: String,
    val merchantId: Long,
    val totalAmount: BigDecimal,
    val totalFee: BigDecimal,
    val transactionCount: Int,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime?
) 