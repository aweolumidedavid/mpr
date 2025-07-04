package com.test.mpr.dto

import com.fasterxml.jackson.annotation.JsonInclude
import java.time.LocalDateTime

@JsonInclude(JsonInclude.Include.NON_NULL)
data class ApiResponse<T>(
    val success: Boolean,
    val message: String,
    val data: T? = null,
    val errors: List<String>? = null,
    val timestamp: LocalDateTime = LocalDateTime.now()
) {
    companion object {
        fun <T> success(data: T, message: String = "Success"): ApiResponse<T> {
            return ApiResponse(success = true, message = message, data = data)
        }
        
        fun <T> error(message: String, errors: List<String>? = null): ApiResponse<T> {
            return ApiResponse(success = false, message = message, errors = errors)
        }
        
        fun <T> error(message: String, error: String): ApiResponse<T> {
            return ApiResponse(success = false, message = message, errors = listOf(error))
        }
    }
} 