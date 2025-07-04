package com.test.mpr.dto

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank

data class CreateMerchantRequest(
    @field:NotBlank(message = "Business name is required")
    val businessName: String,
    
    @field:NotBlank(message = "Email is required")
    @field:Email(message = "Invalid email format")
    val email: String,
    
    @field:NotBlank(message = "Settlement account is required")
    val settlementAccount: String
) 