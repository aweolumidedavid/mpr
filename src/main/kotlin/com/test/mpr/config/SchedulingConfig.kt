package com.test.mpr.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration
import java.time.ZoneId

@Configuration
@ConfigurationProperties(prefix = "process.schedule")
data class SchedulingConfig(
    var interval: String = "0 0 9,18 * * *",
    var enabled: Boolean = true,
    var timezone: String = "UTC"
) {
    fun getZoneId(): ZoneId = ZoneId.of(timezone)
    
    fun isSchedulingEnabled(): Boolean = enabled
} 