package com.test.mpr.dto

import jakarta.validation.constraints.DecimalMin
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import java.math.BigDecimal

data class InitiateTransactionRequest(
    @field:NotNull(message = "Amount is required")
    @field:DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    val amount: BigDecimal,
    
    @field:NotNull(message = "Currency is required")
    @field:Size(min = 3, max = 3, message = "Currency must be 3 characters")
    val currency: String,
    
    @field:NotNull(message = "Merchant ID is required")
    val merchantId: Long,
    
    @field:NotNull(message = "Merchant reference is required")
    val merchantRef: String
) 