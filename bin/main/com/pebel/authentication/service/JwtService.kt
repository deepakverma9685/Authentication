package com.pebel.authentication.service

import com.pebel.authentication.exception.InvalidTokenException
import com.pebel.authentication.model.RefreshToken
import com.pebel.authentication.model.User
import com.pebel.authentication.repository.RefreshTokenRepository
import io.jsonwebtoken.*
import io.jsonwebtoken.security.Keys
import io.jsonwebtoken.security.SignatureException
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Service
import java.time.Instant
import java.util.*
import javax.crypto.SecretKey

@Service
class JwtService(
    private val jwtSigningKey: SecretKey,
    private val userDetailsService: UserDetailsService,
    private val refreshTokenRepository: RefreshTokenRepository,
    @Value("\${app.jwt.accessTokenExpirationMs}")
    val accessTokenExpirationMs: Long,
    @Value("\${app.jwt.refreshTokenExpirationMs}")
    private val refreshTokenExpirationMs: Long
) {
    companion object {
        private val logger = LoggerFactory.getLogger(JwtService::class.java)
    }

    /**
     * Generates a JWT access token for the given authentication
     */
    fun generateAccessToken(authentication: Authentication): String {
        val now = Date.from(Instant.now())
        val expiryDate = Date.from(Instant.now().plusMillis(accessTokenExpirationMs))
        
        return Jwts.builder()
            .setSubject(authentication.name)
            .claim("roles", authentication.authorities.map { it.authority })
            .setIssuedAt(now)
            .setExpiration(expiryDate)
            .signWith(jwtSigningKey, SignatureAlgorithm.HS512)
            .compact()
    }

    /**
     * Generates and persists a refresh token for the user
     */
    fun generateRefreshToken(authentication: Authentication, user: User): RefreshToken {
        val now = Instant.now()
        val expiresAt = now.plusMillis(refreshTokenExpirationMs)

        val refreshToken = RefreshToken(
            user = user,
            token = UUID.randomUUID().toString(),
            expiresAt = expiresAt
        )

        return refreshTokenRepository.save(refreshToken)
    }

    /**
     * Validates a JWT token and returns the username if valid
     */
    fun validateTokenAndGetUsername(token: String): String? {
        return try {
            val claims = Jwts.parserBuilder()
                .setSigningKey(jwtSigningKey)
                .build()
                .parseClaimsJws(token)
                .body
            
            claims.subject
        } catch (ex: SignatureException) {
            logger.error("Invalid JWT signature: ${ex.message}")
            null
        } catch (ex: MalformedJwtException) {
            logger.error("Invalid JWT token: ${ex.message}")
            null
        } catch (ex: ExpiredJwtException) {
            logger.error("JWT token is expired: ${ex.message}")
            null
        } catch (ex: UnsupportedJwtException) {
            logger.error("JWT token is unsupported: ${ex.message}")
            null
        } catch (ex: IllegalArgumentException) {
            logger.error("JWT claims string is empty: ${ex.message}")
            null
        }
    }

    /**
     * Refreshes an access token using a valid refresh token
     */
    fun refreshAccessToken(refreshToken: String): Pair<String, RefreshToken>? {
        return refreshTokenRepository.findByToken(refreshToken)
            ?.takeIf { it.expiresAt.isAfter(Instant.now()) && !it.revoked }
            ?.let { token ->
                val userDetails = userDetailsService.loadUserByUsername(token.user.email)
                val authentication = UsernamePasswordAuthenticationToken(
                    userDetails, null, userDetails.authorities
                )

                // Generate new access token
                val newAccessToken = generateAccessToken(authentication)

                // Rotate refresh token (optional: can reuse the same one with extended expiry)
                refreshTokenRepository.delete(token)
                val newRefreshToken = generateRefreshToken(authentication, token.user)

                Pair(newAccessToken, newRefreshToken)
            }
    }
}