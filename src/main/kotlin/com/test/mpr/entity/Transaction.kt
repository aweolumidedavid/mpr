package com.test.mpr.entity

import com.test.mpr.entity.enums.TransactionStatus
import jakarta.persistence.*
import java.math.BigDecimal
import java.time.LocalDateTime

@Entity
@Table(name = "transactions")
data class Transaction(
    override val id: Long? = null,
    
    @Column(name = "amount", nullable = false, precision = 19, scale = 2)
    val amount: BigDecimal,
    
    @Column(name = "currency", nullable = false, length = 3)
    val currency: String,
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    val status: TransactionStatus = TransactionStatus.INITIATED,
    
    @Column(name = "merchant_ref", nullable = false)
    val merchantRef: String,
    
    @Column(name = "internal_ref", nullable = false, unique = true)
    val internalRef: String,
    
    @Column(name = "fee", nullable = false, precision = 19, scale = 2)
    val fee: BigDecimal,
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "merchant_id", nullable = false)
    val merchant: Merchant,
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "settlement_batch_id")
    val settlementBatch: SettlementBatch? = null,
    
    override val createdAt: LocalDateTime = LocalDateTime.now(),
    
    override val updatedAt: LocalDateTime? = null
) : BaseEntity(id, createdAt, updatedAt) 