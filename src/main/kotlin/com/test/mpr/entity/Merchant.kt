package com.test.mpr.entity

import com.test.mpr.entity.enums.MerchantStatus
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "merchants")
data class Merchant(
    override val id: Long? = null,
    
    @Column(name = "business_name", nullable = false)
    val businessName: String,
    
    @Column(name = "email", nullable = false, unique = true)
    val email: String,
    
    @Column(name = "settlement_account", nullable = false)
    val settlementAccount: String,
    
    @Column(name = "merchant_ref", nullable = false, unique = true)
    val merchantRef: String,
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    val status: MerchantStatus = MerchantStatus.ACTIVE,
    
    override val createdAt: LocalDateTime = LocalDateTime.now(),
    
    override val updatedAt: LocalDateTime? =  LocalDateTime.now(),
) : BaseEntity(id, createdAt, updatedAt)
