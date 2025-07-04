package com.test.mpr.dto

import com.test.mpr.entity.enums.MerchantStatus
import java.time.LocalDateTime

data class MerchantResponse(
    val id: Long,
    val businessName: String,
    val email: String,
    val settlementAccount: String,
    val merchantRef: String,
    val status: MerchantStatus,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime?
) 