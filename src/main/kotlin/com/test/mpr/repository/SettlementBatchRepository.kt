package com.test.mpr.repository

import com.test.mpr.entity.SettlementBatch
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
interface SettlementBatchRepository : JpaRepository<SettlementBatch, Long> {
    
    fun findByBatchRef(batchRef: String): SettlementBatch?
    
    fun findByMerchantId(merchantId: Long): List<SettlementBatch>
    
    @Query("""
        SELECT sb FROM SettlementBatch sb 
        WHERE sb.merchant.id = :merchantId 
        ORDER BY sb.createdAt DESC
    """)
    fun findByMerchantIdOrderByCreatedAtDesc(@Param("merchantId") merchantId: Long): List<SettlementBatch>
    
    @Query("""
        SELECT COUNT(sb) FROM SettlementBatch sb 
        WHERE sb.merchant.id = :merchantId
    """)
    fun countByMerchantId(@Param("merchantId") merchantId: Long): Long
    
    @Query("""
        SELECT MAX(sb.createdAt) FROM SettlementBatch sb 
        WHERE sb.merchant.id = :merchantId
    """)
    fun findLastSettlementDateByMerchantId(@Param("merchantId") merchantId: Long): LocalDateTime?
} 