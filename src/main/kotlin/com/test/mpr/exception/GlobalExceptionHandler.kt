package com.test.mpr.exception

import com.test.mpr.dto.ApiResponse
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.FieldError
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.context.request.WebRequest
import java.time.LocalDateTime
import java.util.*

@RestControllerAdvice
class GlobalExceptionHandler {
    
    private val logger = LoggerFactory.getLogger(GlobalExceptionHandler::class.java)
    
    @ExceptionHandler(BaseException::class)
    fun handleBaseException(ex: BaseException, request: WebRequest): ResponseEntity<ApiResponse<ErrorResponse>> {
        val errorResponse = ex.getErrorResponse()
        
        logger.error("BaseException occurred - Reference: ${errorResponse.reference}, Error: ${ex.message}", ex)
        
        return ResponseEntity.status(ex.httpStatus)
            .body(ApiResponse.error(
                message = ex.message ?: "An error occurred",
                errors = listOf(errorResponse.message)
            ))
    }
    
    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationException(ex: MethodArgumentNotValidException, request: WebRequest): ResponseEntity<ApiResponse<ErrorResponse>> {
        val errors = ex.bindingResult.fieldErrors.map { fieldError: FieldError ->
            "${fieldError.field}: ${fieldError.defaultMessage}"
        }
        
        val reference = generateReference()
        logger.error("ValidationException occurred - Reference: $reference, Errors: $errors", ex)
        
        return ResponseEntity.badRequest()
            .body(ApiResponse.error(
                message = "Validation failed",
                errors = errors
            ))
    }
    
    @ExceptionHandler(IllegalArgumentException::class)
    fun handleIllegalArgumentException(ex: IllegalArgumentException, request: WebRequest): ResponseEntity<ApiResponse<ErrorResponse>> {
        val reference = generateReference()
        logger.error("IllegalArgumentException occurred - Reference: $reference, Error: ${ex.message}", ex)
        
        return ResponseEntity.badRequest()
            .body(ApiResponse.error(
                message = "Invalid request",
                error = ex.message ?: "Invalid argument provided"
            ))
    }
    
    @ExceptionHandler(IllegalStateException::class)
    fun handleIllegalStateException(ex: IllegalStateException, request: WebRequest): ResponseEntity<ApiResponse<ErrorResponse>> {
        val reference = generateReference()
        logger.error("IllegalStateException occurred - Reference: $reference, Error: ${ex.message}", ex)
        
        return ResponseEntity.status(HttpStatus.CONFLICT)
            .body(ApiResponse.error(
                message = "Request conflict",
                error = ex.message ?: "Invalid state"
            ))
    }
    
    @ExceptionHandler(Exception::class)
    fun handleGenericException(ex: Exception, request: WebRequest): ResponseEntity<ApiResponse<ErrorResponse>> {
        val reference = generateReference()
        logger.error("Unexpected exception occurred - Reference: $reference, Error: ${ex.message}", ex)
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(ApiResponse.error(
                message = "Internal server error",
                error = "An unexpected error occurred. Reference: $reference"
            ))
    }
    
    private fun generateReference(): String = "REF-${UUID.randomUUID().toString().substring(0, 8).uppercase()}"
}