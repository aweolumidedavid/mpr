package com.test.mpr.dto

import java.math.BigDecimal
import java.time.LocalDateTime

data class PaymentResponse(
    val success: Boolean,
    val status: PaymentStatus,
    val transactionId: String? = null,
    val amount: BigDecimal,
    val currency: String,
    val fee: BigDecimal,
    val message: String,
    val timestamp: LocalDateTime = LocalDateTime.now(),
    val gatewayReference: String? = null,
    val errorCode: String? = null
)