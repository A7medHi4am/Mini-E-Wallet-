package com.example.miniewallet.common.security;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {

    @Value("${app.jwt.secret}")
    private String secret;

    @Value("${app.jwt.expiration-ms}")
    private long expirationMs;

    public String generateToken(UserPrincipal principal) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + expirationMs);
        return Jwts.builder()
                .subject(principal.getUsername())
                .claim("role", principal.getRole().name())
                .issuedAt(now)
                .expiration(expiry)
                .signWith(key())
                .compact();
    }

    public String extractUsername(String token) {
        return parseClaims(token).getSubject();
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        try {
            Claims claims = parseClaims(token);
            return claims.getSubject().equals(userDetails.getUsername())
                    && claims.getExpiration().after(new Date());
        } catch (JwtException e) {
            return false;
        }
    }

    private Claims parseClaims(String token) {
        return Jwts.parser().verifyWith(key()).build().parseSignedClaims(token).getPayload();
    }

    private SecretKey key() {
        return Keys.hmacShaKeyFor(resolveSecretKeyBytes());
    }

    private byte[] resolveSecretKeyBytes() {
        try {
            byte[] decoded = Base64.getDecoder().decode(secret);
            if (decoded.length >= 32) {
                return decoded;
            }
        } catch (IllegalArgumentException ignored) {
            // not a Base64 encoded secret, fall back to raw bytes below
        }

        byte[] rawBytes = secret.getBytes(StandardCharsets.UTF_8);
        if (rawBytes.length >= 32) {
            return rawBytes;
        }

        return sha256(rawBytes);
    }

    private byte[] sha256(byte[] input) {
        try {
            return MessageDigest.getInstance("SHA-256").digest(input);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 algorithm is required for JWT secret derivation", e);
        }
    }
}
