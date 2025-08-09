package com.pebel.authentication.repository

import com.pebel.authentication.model.PasswordResetToken
import org.springframework.data.jpa.repository.JpaRepository
import java.time.Instant
import java.util.UUID

interface PasswordResetTokenRepository : JpaRepository<PasswordResetToken, UUID> {
    fun findByToken(token: String): PasswordResetToken?
    fun deleteByUserId(userId: UUID)
    fun deleteAllByExpiresAtBefore(expiryDate: Instant)
}