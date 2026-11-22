package com.clinica.ecommerce.config;

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

// Valida JWT y emite token interno
@Service
public class JwtService {

    private static final long INTERNAL_TOKEN_TTL_MS = 300_000L;

    @Value("${security.jwt.secret}")
    private String secret;

    public String generateInternalServiceToken() {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + INTERNAL_TOKEN_TTL_MS);
        return Jwts.builder()
                .claims(Map.of("usuarioId", 0L, "rol", "ADMIN"))
                .subject("internal-service")
                .issuedAt(now)
                .expiration(expiry)
                .signWith(getSigningKey())
                .compact();
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
