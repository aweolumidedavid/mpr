package com.test.mpr.entity.enums

enum class MerchantStatus(val value: String) {
    ACTIVE("ACTIVE"),
    INACTIVE("INACTIVE");

    companion object {
        fun fromValue(value: String): MerchantStatus {
            return values().find { it.value == value }
                ?: throw IllegalArgumentException("Unknown MerchantStatus value: $value")
        }
    }
}
