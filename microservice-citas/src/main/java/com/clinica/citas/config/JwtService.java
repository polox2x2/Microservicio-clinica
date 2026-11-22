package com.clinica.citas.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;

// Emite y valida tokens JWT
@Service
public class JwtService {

    private static final long INTERNAL_TOKEN_TTL_MS = 300_000L;

    @Value("${security.jwt.secret}")
    private String secret;

    @Value("${security.jwt.expiration-ms:86400000}")
    private long expirationMs;

    public long getExpirationMs() {
        return expirationMs;
    }

    public String generateToken(Long usuarioId, String email, String rol) {
        return buildToken(email, Map.of("usuarioId", usuarioId, "rol", rol), expirationMs);
    }

    public String generateInternalServiceToken() {
        return buildToken("internal-service", Map.of("usuarioId", 0L, "rol", "ADMIN"), INTERNAL_TOKEN_TTL_MS);
    }

    public String extractEmail(String token) {
        return parseClaims(token).getSubject();
    }

    public String extractRol(String token) {
        return parseClaims(token).get("rol", String.class);
    }

    public boolean isTokenValid(String token) {
        try {
            Claims claims = parseClaims(token);
            return claims.getExpiration().after(new Date());
        } catch (JwtException | IllegalArgumentException ex) {
            return false;
        }
    }

    private String buildToken(String subject, Map<String, Object> extraClaims, long ttlMs) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + ttlMs);
        return Jwts.builder()
                .claims(extraClaims)
                .subject(subject)
                .issuedAt(now)
                .expiration(expiry)
                .signWith(getSigningKey())
                .compact();
    }

    private Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }
}
