package com.test.mpr.entity

import jakarta.persistence.*
import java.time.LocalDateTime

@MappedSuperclass
abstract class BaseEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    open val id: Long? = null,
    
    @Column(name = "created_at", nullable = false, updatable = false)
    open val createdAt: LocalDateTime = LocalDateTime.now(),
    
    @Column(name = "updated_at")
    open val updatedAt: LocalDateTime? = null
) {
    @PreUpdate
    fun preUpdate() {
        // this would be handled by the entity manager in a real implementation
        // for now, we'll rely on manual setting of updatedAt
    }
} 