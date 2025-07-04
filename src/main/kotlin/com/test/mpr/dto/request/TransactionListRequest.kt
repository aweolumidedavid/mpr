package com.test.mpr.dto

import com.test.mpr.entity.enums.TransactionStatus
import java.time.LocalDateTime

data class TransactionListRequest(
    val merchantId: Long? = null,
    val status: TransactionStatus? = null,
    val startDate: LocalDateTime? = null,
    val endDate: LocalDateTime? = null,
    val page: Int = 0,
    val size: Int = 20
) 