package com.test.mpr.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.config.Customizer.withDefaults

@Configuration
@EnableWebSecurity
@EnableConfigurationProperties(SecurityProperties::class)
class SecurityConfig(private val securityProperties: SecurityProperties) {
    
    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .csrf { it.disable() }
            .authorizeHttpRequests { authz ->
                authz
                    .requestMatchers(*securityProperties.permittedPaths.toTypedArray()).permitAll()
                    .anyRequest().authenticated()
            }
            .httpBasic(withDefaults())
        
        return http.build()
    }
}

@ConfigurationProperties(prefix = "app.security")
data class SecurityProperties(
    val permittedPaths: List<String> = listOf()
) 