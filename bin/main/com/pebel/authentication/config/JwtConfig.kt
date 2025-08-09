package com.pebel.authentication.config

import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import javax.crypto.SecretKey

@Configuration
class JwtConfig {
    @Value("\${app.jwt.secret}")
    lateinit var jwtSecret: String

    @Value("\${app.jwt.accessTokenExpirationMs}")
    var jwtExpirationMs: Long = 0

    @Value("\${app.jwt.refreshTokenExpirationMs}")
    var refreshExpirationMs: Long = 0

    @Bean
    fun jwtSigningKey(): SecretKey {
        return Keys.hmacShaKeyFor(jwtSecret.toByteArray())
    }
}