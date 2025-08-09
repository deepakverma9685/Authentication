package com.pebel.authentication.service

import com.pebel.authentication.model.User
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Service

@Service
@ConditionalOnProperty(name = ["app.mail.enabled"], havingValue = "false", matchIfMissing = true)
class MockMailService : MailService {
    companion object {
        private val logger = LoggerFactory.getLogger(MockMailService::class.java)
    }

    override fun sendVerificationEmail(user: User) {
        logger.info("📧 [MOCK] Verification email would be sent to: ${user.email}")
        logger.info("📧 [MOCK] Email content: Welcome ${user.firstName}! Click the link to verify your email.")
    }

    override fun sendPasswordResetEmail(user: User, token: String) {
        logger.info("📧 [MOCK] Password reset email would be sent to: ${user.email}")
        logger.info("📧 [MOCK] Reset token: $token")
    }
}
