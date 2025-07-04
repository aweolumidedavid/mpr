package com.test.mpr.repository

import com.test.mpr.entity.Transaction
import com.test.mpr.entity.enums.TransactionStatus
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
interface TransactionRepository : JpaRepository<Transaction, Long> {

    fun findByInternalRef(internalRef: String): Transaction?

    fun findByMerchantRef(merchantRef: String): Transaction?

    fun findByMerchantId(merchantId: Long, pageable: Pageable): Page<Transaction>

    fun findByMerchantIdAndStatus(merchantId: Long, status: TransactionStatus, pageable: Pageable): Page<Transaction>

    @Query(
        """ SELECT t FROM Transaction t 
    WHERE t.merchant.id = :merchantId 
    AND (:status IS NULL OR t.status = :status)
    AND (COALESCE(:startDate, CAST('1970-01-01' AS timestamp)) <= t.createdAt)
    AND (COALESCE(:endDate, CAST('9999-12-31' AS timestamp)) >= t.createdAt)
    ORDER BY t.createdAt DESC"""
    )
    fun findByMerchantIdAndFilters(
        @Param("merchantId") merchantId: Long,
        @Param("status") status: TransactionStatus?,
        @Param("startDate") startDate: LocalDateTime?,
        @Param("endDate") endDate: LocalDateTime?,
        pageable: Pageable
    ): Page<Transaction>

    @Query(
        """
        SELECT t FROM Transaction t 
        WHERE t.merchant.id = :merchantId 
        AND t.status = :status 
        AND t.settlementBatch IS NULL
        ORDER BY t.createdAt ASC
    """
    )
    fun findUnsettledTransactionsByMerchantAndStatus(
        @Param("merchantId") merchantId: Long,
        @Param("status") status: TransactionStatus
    ): List<Transaction>

    @Query(
        """
        SELECT COUNT(t) FROM Transaction t 
        WHERE t.merchant.id = :merchantId 
        AND t.status = :status 
        AND t.settlementBatch IS NULL
    """
    )
    fun countUnsettledTransactionsByMerchantAndStatus(
        @Param("merchantId") merchantId: Long,
        @Param("status") status: TransactionStatus
    ): Long
} 