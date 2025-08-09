package com.pebel.authentication.service

import com.pebel.authentication.model.User
import jakarta.mail.internet.MimeMessage
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.stereotype.Service
import org.thymeleaf.TemplateEngine
import org.thymeleaf.context.Context
import java.util.UUID

@Service
@ConditionalOnProperty(name = ["app.mail.enabled"], havingValue = "true")
class SmtpMailService(
    private val javaMailSender: JavaMailSender,
    @Value("\${app.mail.from}") private val fromEmail: String,
    @Value("\${app.base-url}") private val baseUrl: String,
    private val templateEngine: TemplateEngine
) : MailService {
    override fun sendVerificationEmail(user: User) {
        val verificationUrl = "$baseUrl/api/v1/auth/verify-email?token=${generateVerificationToken(user)}"

        val context = Context().apply {
            setVariable("name", user.firstName)
            setVariable("verificationUrl", verificationUrl)
        }

        val content = templateEngine.process("email/verification", context)

        val message = MimeMessageHelper(javaMailSender.createMimeMessage()).apply {
            setFrom(fromEmail)
            setTo(user.email)
            setSubject("Verify your email address")
            setText(content, true)
        }

        javaMailSender.send(message.mimeMessage)
    }

    override fun sendPasswordResetEmail(user: User, token: String) {
        val resetUrl = "$baseUrl/reset-password?token=$token"

        val context = Context().apply {
            setVariable("name", user.firstName)
            setVariable("resetUrl", resetUrl)
        }

        val content = templateEngine.process("email/password-reset", context)

        val message = MimeMessageHelper(javaMailSender.createMimeMessage()).apply {
            setFrom(fromEmail)
            setTo(user.email)
            setSubject("Password reset request")
            setText(content, true)
        }

        javaMailSender.send(message.mimeMessage)
    }

    private fun generateVerificationToken(user: User): String {
        // Implementation similar to JWT token generation
        // In a real implementation, this would generate a proper verification token
        return "${user.id}-${UUID.randomUUID()}"
    }
}
