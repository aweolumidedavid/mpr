package com.test.mpr.repository

import com.test.mpr.entity.Merchant
import com.test.mpr.entity.enums.MerchantStatus
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface MerchantRepository : JpaRepository<Merchant, Long> {
    
    fun findByEmail(email: String): Merchant?
    
    fun findByStatus(status: MerchantStatus): List<Merchant>
    
    @Query("SELECT m FROM Merchant m WHERE m.status = :status AND m.id = :merchantId")
    fun findByStatusAndId(@Param("status") status: MerchantStatus, @Param("merchantId") merchantId: Long): Merchant?
} 