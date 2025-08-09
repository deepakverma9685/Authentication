package com.pebel.authentication.model

import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.time.Instant
import java.util.*

@MappedSuperclass
abstract class BaseEntity(
    @Id
    @GeneratedValue
    open val id: UUID? = null,

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    open val createdAt: Instant? = null,

    @UpdateTimestamp
    @Column(name = "updated_at")
    open val updatedAt: Instant? = null
)
