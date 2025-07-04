package com.test.mpr.exception

import org.springframework.http.HttpStatus
import java.time.LocalDateTime

abstract class BaseException(
    override val message: String,
    val errorCode: String,
    val httpStatus: HttpStatus,
    val timestamp: LocalDateTime = LocalDateTime.now()
) : RuntimeException(message) {
    
    abstract fun getErrorResponse(): ErrorResponse
}

data class ErrorResponse(
    val timestamp: LocalDateTime,
    val status: Int,
    val error: String,
    val message: String,
    val errorCode: String,
    val reference: String
)