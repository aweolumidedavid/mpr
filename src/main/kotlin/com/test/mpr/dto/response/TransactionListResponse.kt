package com.test.mpr.dto

data class TransactionListResponse(
    val transactions: List<TransactionResponse>,
    val totalElements: Long,
    val totalPages: Int,
    val currentPage: Int,
    val pageSize: Int
) 