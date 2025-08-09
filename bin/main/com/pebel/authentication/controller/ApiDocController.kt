package com.pebel.authentication.controller

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api")
class ApiDocController {

    @GetMapping("/docs")
    fun getApiDocumentation(): ResponseEntity<Map<String, Any>> {
        val apiDocs = mapOf(
            "title" to "Authentication API",
            "version" to "1.0.0",
            "description" to "JWT-based Authentication and Authorization API",
            "baseUrl" to "http://localhost:8080",
            "endpoints" to listOf(
                mapOf(
                    "method" to "POST",
                    "path" to "/api/v1/auth/register",
                    "description" to "Register a new user",
                    "requestBody" to mapOf(
                        "firstName" to "string (required)",
                        "lastName" to "string (optional)",
                        "email" to "string (required, valid email)",
                        "password" to "string (required, min 8 chars)"
                    ),
                    "response" to "User object with ID and email verification status"
                ),
                mapOf(
                    "method" to "POST",
                    "path" to "/api/v1/auth/login",
                    "description" to "Authenticate user and get JWT tokens",
                    "requestBody" to mapOf(
                        "email" to "string (required)",
                        "password" to "string (required)"
                    ),
                    "response" to "JWT access token, refresh token, and expiration"
                ),
                mapOf(
                    "method" to "POST",
                    "path" to "/api/v1/auth/refresh",
                    "description" to "Refresh JWT access token",
                    "requestBody" to mapOf(
                        "refreshToken" to "string (required)"
                    ),
                    "response" to "New access token and refresh token"
                ),
                mapOf(
                    "method" to "POST",
                    "path" to "/api/v1/auth/forgot-password",
                    "description" to "Initiate password reset process",
                    "requestBody" to mapOf(
                        "email" to "string (required)"
                    ),
                    "response" to "Success message (email sent if account exists)"
                ),
                mapOf(
                    "method" to "POST",
                    "path" to "/api/v1/auth/reset-password",
                    "description" to "Complete password reset with token",
                    "requestBody" to mapOf(
                        "token" to "string (required, from email)",
                        "newPassword" to "string (required, min 8 chars)"
                    ),
                    "response" to "Success message"
                )
            ),
            "adminUrls" to mapOf(
                "h2Console" to "http://localhost:8080/h2-console/",
                "actuatorHealth" to "http://localhost:8080/actuator/health",
                "apiDocumentation" to "http://localhost:8080/api/docs"
            ),
            "authentication" to mapOf(
                "type" to "Bearer Token (JWT)",
                "header" to "Authorization: Bearer <token>",
                "tokenExpiry" to "24 hours",
                "refreshTokenExpiry" to "7 days"
            )
        )
        
        return ResponseEntity.ok(apiDocs)
    }
    
    @GetMapping("/health")
    fun healthCheck(): ResponseEntity<Map<String, String>> {
        return ResponseEntity.ok(mapOf(
            "status" to "UP",
            "timestamp" to java.time.Instant.now().toString(),
            "database" to "H2 (in-memory)",
            "profile" to "h2"
        ))
    }
}
