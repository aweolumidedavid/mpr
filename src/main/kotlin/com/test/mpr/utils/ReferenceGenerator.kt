package com.test.mpr.utils

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

object ReferenceGenerator {
    
    private val formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss")
    
    fun generateInternalRef(): String {
        val timestamp = LocalDateTime.now().format(formatter)
        val uuid = UUID.randomUUID().toString().replace("-", "").substring(0, 8).uppercase()
        return "TXN${timestamp}${uuid}"
    }
    
    fun generateBatchRef(): String {
        val timestamp = LocalDateTime.now().format(formatter)
        val uuid = UUID.randomUUID().toString().replace("-", "").substring(0, 6).uppercase()
        return "BATCH${timestamp}${uuid}"
    }
    
    fun generateMerchantRef(): String {
        val timestamp = LocalDateTime.now().format(formatter)
        val uuid = UUID.randomUUID().toString().replace("-", "").substring(0, 6).uppercase()
        return "MERCH${timestamp}${uuid}"
    }
} 