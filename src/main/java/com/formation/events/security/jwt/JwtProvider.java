package com.formation.events.security.jwt;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import com.formation.events.security.CustomUserPrincipal;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtProvider {

  @Value("${app.jwt.secret}")
  private String jwtSecret;

  @Value("${app.jwt.expiration}")
  private int jwtExpirationInMs;

  private Key getSigningKey() {
    return Keys.hmacShaKeyFor(jwtSecret.getBytes());
  }

  public String generateToken(Authentication authentication) {
    CustomUserPrincipal userPrincipal = (CustomUserPrincipal) authentication.getPrincipal();

    Date now = new Date();
    Date expiryDate = new Date(now.getTime() + jwtExpirationInMs);

    Map<String, Object> claims = new HashMap<>();
    claims.put("email", userPrincipal.getEmail());

    return Jwts.builder()
        .claims(claims)
        .subject(Long.toString(userPrincipal.getId()))
        .issuedAt(now)
        .expiration(expiryDate)
        .signWith(getSigningKey())
        .compact();
  }

  public String getUserByEmail(String token) {
    return getPayload(token).get("email", String.class);
  }

  public boolean isExpirated(String token) {
    return getPayload(token).getExpiration().before(new Date());
  }

  private Claims getPayload(String token) {
    return (Claims) Jwts.parser()
        .verifyWith((SecretKey) getSigningKey())
        .build()
        .parse(token)
        .getPayload();
  }

}
