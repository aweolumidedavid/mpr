package com.test.mpr.exception

import org.springframework.http.HttpStatus
import java.util.*

class TransactionNotFoundException(internalRef: String) : BaseException(
    message = "Transaction with internal reference $internalRef not found",
    errorCode = "TRANSACTION_NOT_FOUND",
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

class TransactionAlreadyExistsException(merchantRef: String) : BaseException(
    message = "Transaction with merchant reference $merchantRef already exists",
    errorCode = "TRANSACTION_ALREADY_EXISTS",
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

class TransactionInProgressException(merchantRef: String) : BaseException(
    message = "Transaction with merchant reference $merchantRef is already in progress",
    errorCode = "TRANSACTION_IN_PROGRESS",
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

class InvalidTransactionAmountException(amount: String) : BaseException(
    message = "Invalid transaction amount: $amount",
    errorCode = "INVALID_TRANSACTION_AMOUNT",
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

class TransactionProcessingException(message: String) : BaseException(
    message = message,
    errorCode = "TRANSACTION_PROCESSING_ERROR",
    httpStatus = HttpStatus.INTERNAL_SERVER_ERROR
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