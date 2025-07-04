package com.test.mpr.utils

import com.test.mpr.exception.TransactionInProgressException
import org.slf4j.LoggerFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Component
import java.util.concurrent.TimeUnit

@Component
class TransactionLockManager(
    private val redisTemplate: RedisTemplate<String, String>
) {
    
    private val logger = LoggerFactory.getLogger(TransactionLockManager::class.java)
    
    companion object {
        private const val REDIS_KEY_PREFIX = "transaction:"
        private const val LOCK_TIMEOUT_MINUTES = 5L
    }
    
    /**
     * this executes a transaction block with automatic lock management
     * it checks if transaction is in progress, acquires lock, executes block, and then cleans up
     */
    fun <T> executeWithLock(
        merchantRef: String,
        block: () -> T
    ): T {
        val redisKey = "$REDIS_KEY_PREFIX$merchantRef"
        
        /**
        * check if transaction is already in progress
        */
        val existingTransaction = redisTemplate.opsForValue().get(redisKey)
        if (existingTransaction != null) {
            logger.warn("Transaction with merchant reference '$merchantRef' is already in progress")
            throw TransactionInProgressException(merchantRef)
        }
        
        /**
        * acquire the lock with timeout
        */
        val lockAcquired = redisTemplate.opsForValue().setIfAbsent(redisKey, "LOCKED", LOCK_TIMEOUT_MINUTES, TimeUnit.MINUTES) ?: false
        if (!lockAcquired) {
            logger.warn("Failed to acquire lock for transaction with merchant reference '$merchantRef'")
            throw TransactionInProgressException(merchantRef)
        }
        
        try {
            logger.info("executing transaction block for merchant reference '$merchantRef'")
            val result = block()
            
            /**
            * mark as completed
            */
            redisTemplate.opsForValue().set(redisKey, "COMPLETED", LOCK_TIMEOUT_MINUTES, TimeUnit.MINUTES)
            logger.info("Transaction completed successfully for merchant reference '$merchantRef'")
            
            return result
            
        } catch (e: Exception) {
            /**
            * Mark as failed
            */
            redisTemplate.opsForValue().set(redisKey, "FAILED", LOCK_TIMEOUT_MINUTES, TimeUnit.MINUTES)
            logger.error("Transaction failed for merchant reference '$merchantRef': ${e.message}", e)
            throw e
            
        } finally {
            /**
            * clean up the lock after a short delay to allow for idempotency checks
            * the key will expire automatically after LOCK_TIMEOUT_MINUTES
            */
            logger.debug("Transaction processing completed for merchant reference '$merchantRef'")
        }
    }
    
    /**
     * executes a transaction block with immediate cleanup after completion
     * this is used when i want to remove the lock immediately after processing
     */
    fun <T> executeWithLockAndCleanup(
        merchantRef: String,
        block: () -> T
    ): T {
        val redisKey = "$REDIS_KEY_PREFIX$merchantRef"
        
        /*
        * check if transaction is already in progress
        */
        val existingTransaction = redisTemplate.opsForValue().get(redisKey)
        if (existingTransaction != null) {
            logger.warn("Transaction with merchant reference '$merchantRef' is already in progress")
            throw TransactionInProgressException(merchantRef)
        }
        
        /*
        * acquire lock with timeout
        */
        val lockAcquired = redisTemplate.opsForValue().setIfAbsent(redisKey, "LOCKED", LOCK_TIMEOUT_MINUTES, TimeUnit.MINUTES) ?: false
        if (!lockAcquired) {
            logger.warn("Failed to acquire lock for transaction with merchant reference '$merchantRef'")
            throw TransactionInProgressException(merchantRef)
        }
        
        try {
            logger.info("Executing transaction block for merchant reference '$merchantRef'")
            val result = block()
            
            logger.info("Transaction completed successfully for merchant reference '$merchantRef'")
            return result
            
        } catch (e: Exception) {
            logger.error("Transaction failed for merchant reference '$merchantRef': ${e.message}", e)
            throw e
            
        } finally {
            /*
            * clean up the lock by deleting the set key
            */
            redisTemplate.delete(redisKey)
            logger.info("Lock cleaned up for merchant reference '$merchantRef'")
        }
    }
} 