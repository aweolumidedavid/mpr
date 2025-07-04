package com.test.mpr.exception

import org.springframework.http.HttpStatus
import java.util.*

class SettlementBatchNotFoundException(batchRef: String) : BaseException(
    message = "Settlement batch with reference $batchRef not found",
    errorCode = "SETTLEMENT_BATCH_NOT_FOUND",
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

class NoUnsettledTransactionsException(merchantId: Long) : BaseException(
    message = "No unsettled transactions found for merchant $merchantId",
    errorCode = "NO_UNSETTLED_TRANSACTIONS",
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

class SettlementProcessingException(message: String) : BaseException(
    message = message,
    errorCode = "SETTLEMENT_PROCESSING_ERROR",
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