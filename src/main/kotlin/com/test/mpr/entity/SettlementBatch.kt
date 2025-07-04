package com.test.mpr.entity

import jakarta.persistence.*
import java.math.BigDecimal
import java.time.LocalDateTime

@Entity
@Table(name = "settlement_batches")
data class SettlementBatch(
    override val id: Long? = null,
    
    @Column(name = "batch_ref", nullable = false, unique = true)
    val batchRef: String,
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "merchant_id", nullable = false)
    val merchant: Merchant,
    
    @Column(name = "total_amount", nullable = false, precision = 19, scale = 2)
    val totalAmount: BigDecimal,
    
    @OneToMany(mappedBy = "settlementBatch", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    val transactions: MutableList<Transaction> = mutableListOf(),
    
    override val createdAt: LocalDateTime = LocalDateTime.now(),
    
    override val updatedAt: LocalDateTime? = null
) : BaseEntity(id, createdAt, updatedAt) 