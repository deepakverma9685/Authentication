package com.pebel.authentication.model

import jakarta.persistence.*
import java.util.*

@Entity
@Table(name = "roles")
data class Role(
    @Id
    @GeneratedValue
    val id: UUID? = null,

    @Column(nullable = false, unique = true, length = 50)
    val name: String,

    @Column(length = 255)
    val description: String? = null
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as Role
        return name == other.name
    }

    override fun hashCode(): Int {
        return name.hashCode()
    }
}
