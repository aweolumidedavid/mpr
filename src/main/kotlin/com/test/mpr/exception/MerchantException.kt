package com.test.mpr.exception

import org.springframework.http.HttpStatus
import java.util.*

class MerchantNotFoundException(merchantId: Long) : BaseException(
    message = "Merchant with id $merchantId not found",
    errorCode = "MERCHANT_NOT_FOUND",
    httpStatus = HttpStatus.NOT_FOUND
) {
    override fun getErrorResponse(): ErrorResponse {
        return ErrorResponse(
            timestamp = timestamp,
            status = httpStatus.value(),
            error = httpStatus.reasonPhrase,
            message = message,
            errorCode = errorCode,
            reference = generateReference()
        )
    }
}

class MerchantEmailAlreadyExistsException(email: String) : BaseException(
    message = "Merchant with email $email already exists",
    errorCode = "MERCHANT_EMAIL_EXISTS",
    httpStatus = HttpStatus.CONFLICT
) {
    override fun getErrorResponse(): ErrorResponse {
        return ErrorResponse(
            timestamp = timestamp,
            status = httpStatus.value(),
            error = httpStatus.reasonPhrase,
            message = message,
            errorCode = errorCode,
            reference = generateReference()
        )
    }
}

class MerchantInactiveException(merchantId: Long) : BaseException(
    message = "Merchant with id $merchantId is not active",
    errorCode = "MERCHANT_INACTIVE",
    httpStatus = HttpStatus.BAD_REQUEST
) {
    override fun getErrorResponse(): ErrorResponse {
        return ErrorResponse(
            timestamp = timestamp,
            status = httpStatus.value(),
            error = httpStatus.reasonPhrase,
            message = message,
            errorCode = errorCode,
            reference = generateReference()
        )
    }
}

private fun generateReference(): String = "REF-${UUID.randomUUID().toString().substring(0, 8).uppercase()}"