package com.java.security;

import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtTokenProvider {
	@Value("${app.jwt-secret}")
	private String jwtSecret;

	@Value("${app.jwt-expiration-milliseconds}")
	private long jwtExpirationDate;

	// Generate JWT token
	public String generateToken(Authentication authentication) {

		String username = authentication.getName();

		Date currentDate = new Date();

		Date expireDate = new Date(currentDate.getTime() + jwtExpirationDate);

		return Jwts.builder().subject(username).issuedAt(currentDate).expiration(expireDate).signWith(key()).compact();
	}

	// Generate signing key
	private SecretKey key() {

		return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
	}

	// Get username from JWT token
	public String getUsername(String token) {

		Claims claims = Jwts.parser().verifyWith(key()).build().parseSignedClaims(token).getPayload();

		return claims.getSubject();
	}

	// Validate JWT token
	public boolean validateToken(String token) {

		try {

			Jwts.parser().verifyWith(key()).build().parseSignedClaims(token);

			return true;

		} catch (JwtException | IllegalArgumentException e) {

			return false;
		}
	}

}
