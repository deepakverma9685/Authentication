package com.pebel.authentication.dto.response

import java.time.Instant
import java.util.UUID

data class UserResponse(
    val id: UUID?,
    val email: String,
    val firstName: String,
    val lastName: String?,
    val isEmailVerified: Boolean,
    val createdAt: Instant?
)