package com.pebel.authentication.service

import com.pebel.authentication.dto.request.*
import com.pebel.authentication.dto.response.*
import com.pebel.authentication.exception.*
import com.pebel.authentication.model.*
import com.pebel.authentication.repository.*
import org.slf4j.LoggerFactory
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.time.Duration
import java.time.Instant
import java.util.*

@Service
class AuthService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val jwtService: JwtService,
    private val mailService: MailService,
    private val passwordResetTokenRepository: PasswordResetTokenRepository
) {
    companion object {
        private val logger = LoggerFactory.getLogger(AuthService::class.java)
    }

    /**
     * Registers a new user with the system
     */
    fun register(registrationRequest: RegistrationRequest): User {
        // Validate email is not already in use
        if (userRepository.existsByEmail(registrationRequest.email)) {
            throw EmailAlreadyExistsException("Email ${registrationRequest.email} is already in use")
        }

        // Create new user
        val user = User(
            email = registrationRequest.email,
            passwordHash = passwordEncoder.encode(registrationRequest.password),
            firstName = registrationRequest.firstName,
            lastName = registrationRequest.lastName,
            isActive = true,
            isEmailVerified = false
        )

        // Save user
        val savedUser = userRepository.save(user)

        // Send verification email (async)
        mailService.sendVerificationEmail(savedUser)

        return savedUser
    }

    /**
     * Authenticates a user and returns JWT tokens
     */
    fun authenticate(loginRequest: LoginRequest): AuthResponse {
        // Find user by email
        val user = userRepository.findByEmail(loginRequest.email)
            ?: throw BadCredentialsException("Invalid email or password")

        // Verify password
        if (!passwordEncoder.matches(loginRequest.password, user.passwordHash)) {
            throw BadCredentialsException("Invalid email or password")
        }

        // Check if account is active
        if (!user.isActive) {
            throw AccountDisabledException("Account is disabled")
        }

        // Create authentication object
        val authentication = UsernamePasswordAuthenticationToken(
            user.id.toString(),
            null,
            user.roles.map { SimpleGrantedAuthority(it.name) }
        )

        // Generate tokens
        val accessToken = jwtService.generateAccessToken(authentication)
        val refreshToken = jwtService.generateRefreshToken(authentication, user)

        return AuthResponse(
            accessToken = accessToken,
            refreshToken = refreshToken.token,
            expiresIn = jwtService.accessTokenExpirationMs
        )
    }

    /**
     * Initiates password reset process by generating a token and sending email
     */
    fun initiatePasswordReset(email: String) {
        val user = userRepository.findByEmail(email)
            ?: throw UserNotFoundException("User with email $email not found")

        // Create and save password reset token
        val resetToken = PasswordResetToken(
            user = user,
            token = UUID.randomUUID().toString(),
            expiresAt = Instant.now().plus(Duration.ofHours(24))
        )

        passwordResetTokenRepository.save(resetToken)

        // Send password reset email
        mailService.sendPasswordResetEmail(user, resetToken.token)
    }

    /**
     * Completes password reset process by validating token and updating password
     */
    fun completePasswordReset(resetRequest: PasswordResetRequest) {
        // Find valid token
        val resetToken = passwordResetTokenRepository.findByToken(resetRequest.token)
            ?.takeIf { it.expiresAt.isAfter(Instant.now()) && !it.isUsed }
            ?: throw InvalidTokenException("Invalid or expired password reset token")

        // Find user
        val user = resetToken.user

        // Create new user with updated password
        val updatedUser = user.copy(
            passwordHash = passwordEncoder.encode(resetRequest.newPassword)
        )
        userRepository.save(updatedUser)

        // Mark token as used
        resetToken.isUsed = true
        passwordResetTokenRepository.save(resetToken)
    }
}
