package com.pebel.authentication.controller

import com.pebel.authentication.dto.request.*
import com.pebel.authentication.dto.response.*
import com.pebel.authentication.exception.InvalidTokenException
import com.pebel.authentication.service.AuthService
import com.pebel.authentication.service.JwtService
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/auth")
class AuthController(
    private val authService: AuthService,
    private val jwtService: JwtService
) {
    @PostMapping("/register")
    fun register(@Valid @RequestBody request: RegistrationRequest): ResponseEntity<ApiResponse<UserResponse>> {
        val user = authService.register(request)
        return ResponseEntity.ok(
            ApiResponse.success(user.toResponse(), "User registered successfully")
        )
    }

    @PostMapping("/login")
    fun login(@Valid @RequestBody request: LoginRequest): ResponseEntity<ApiResponse<AuthResponse>> {
        val authResponse = authService.authenticate(request)
        return ResponseEntity.ok(
            ApiResponse.success(authResponse, "Login successful")
        )
    }

    @PostMapping("/refresh")
    fun refreshToken(@Valid @RequestBody request: RefreshTokenRequest): ResponseEntity<ApiResponse<AuthResponse>> {
        val (newAccessToken, newRefreshToken) = jwtService.refreshAccessToken(request.refreshToken)
            ?: throw InvalidTokenException("Invalid refresh token")

        val authResponse = AuthResponse(
            accessToken = newAccessToken,
            refreshToken = newRefreshToken.token,
            expiresIn = jwtService.accessTokenExpirationMs
        )

        return ResponseEntity.ok(
            ApiResponse.success(authResponse, "Token refreshed successfully")
        )
    }

    @PostMapping("/forgot-password")
    fun forgotPassword(@Valid @RequestBody request: ForgotPasswordRequest): ResponseEntity<ApiResponse<Void>> {
        authService.initiatePasswordReset(request.email)
        return ResponseEntity.ok(
            ApiResponse.success("Password reset email sent if account exists")
        )
    }

    @PostMapping("/reset-password")
    fun resetPassword(@Valid @RequestBody request: PasswordResetRequest): ResponseEntity<ApiResponse<Void>> {
        authService.completePasswordReset(request)
        return ResponseEntity.ok(
            ApiResponse.success("Password reset successfully")
        )
    }
}
