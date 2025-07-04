package com.test.mpr.dto

import java.math.BigDecimal
import java.time.LocalDateTime

data class SettlementSummaryResponse(
    val merchantId: Long,
    val totalBatches: Int,
    val totalAmount: BigDecimal,
    val totalFee: BigDecimal,
    val totalTransactions: Int,
    val lastSettlementDate: LocalDateTime?
)