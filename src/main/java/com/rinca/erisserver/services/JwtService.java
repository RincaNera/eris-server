package com.rinca.erisserver.services;

import com.rinca.erisserver.models.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class JwtService {
    private final String secretKey;
    private final long expirationTime;

    public JwtService(
            @Value("${security.jwt.secret-key}") String secretKey,
            @Value("${security.jwt.expiration-time}")long expirationTime
    ) {
        this.secretKey = secretKey;
        this.expirationTime = expirationTime;
    }

    public String generateToken(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put(
                "roles",
                user
                        .getAuthorities().
                        stream()
                        .map(GrantedAuthority::getAuthority)
                        .toList()
        );
        Date now = new Date();
        Date expiry =  new Date(now.getTime() + expirationTime);

        return Jwts.builder()
                .claims(claims)
                .subject(user.getId().toString())
                .issuedAt(now)
                .expiration(expiry)
                .signWith(getSigningKey(secretKey))
                .compact();
    }

    public Long extractUsername(String token) {
        return Long.valueOf(extractClaim(token, Claims::getSubject));
    }

    public Collection<GrantedAuthority> extractRoles(String token) {
        List<String> roles = extractClaim(token, c -> c.get("roles", List.class));

        return roles
                .stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toCollection(HashSet::new));
    }

    public boolean isTokenValid(String token) {
        return !isTokenExpired(token);
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey(secretKey))
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private boolean isTokenExpired(String token) {
        final Date exp = extractClaim(token, Claims::getExpiration);
        return exp.before(new Date());
    }

    private SecretKey getSigningKey(String key) {
        byte[] keyBytes = Decoders.BASE64.decode(key);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
