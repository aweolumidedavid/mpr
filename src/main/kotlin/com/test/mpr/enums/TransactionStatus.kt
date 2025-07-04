package com.test.mpr.entity.enums

enum class TransactionStatus(val value: String) {
    INITIATED("INITIATED"),
    SUCCESS("SUCCESS"),
    FAILED("FAILED");

    companion object {
        fun fromValue(value: String): TransactionStatus {
            return values().find { it.value == value }
                ?: throw IllegalArgumentException("Unknown TransactionStatus value: $value")
        }
    }
}
