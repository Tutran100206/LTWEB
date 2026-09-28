package com.example.demo.service;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.*;
import com.nimbusds.jwt.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import java.text.ParseException;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;

@Service
public class JwtService {
    private final byte[] secret;
    private final long expirationTime;
    public JwtService(@Value("${security.jwt.secret-key}") String secretKey,
                      @Value("${security.jwt.expiration-time}") long expirationTime) {
        secret = Base64.getDecoder().decode(secretKey);
        if (secret.length < 32) throw new IllegalArgumentException("JWT secret must contain at least 32 random bytes (Base64)");
        if (expirationTime < 1000) throw new IllegalArgumentException("JWT expiration must be at least 1000 milliseconds");
        this.expirationTime = expirationTime;
    }
    public long getExpirationTime() { return expirationTime; }
    public String generateToken(UserDetails user) {
        Instant now = Instant.now();
        JWTClaimsSet claims = new JWTClaimsSet.Builder().subject(user.getUsername())
            .issueTime(Date.from(now)).expirationTime(Date.from(now.plusMillis(expirationTime))).build();
        SignedJWT jwt = new SignedJWT(new JWSHeader.Builder(JWSAlgorithm.HS256).type(JOSEObjectType.JWT).build(), claims);
        try {
            jwt.sign(new MACSigner(secret));
            return jwt.serialize();
        } catch (JOSEException ex) { throw new IllegalStateException("Cannot sign JWT", ex); }
    }
    // Parsing and signature verification happen exactly once per request in the filter.
    public JWTClaimsSet extractClaims(String token) {
        try {
            SignedJWT jwt = SignedJWT.parse(token);
            if (!JWSAlgorithm.HS256.equals(jwt.getHeader().getAlgorithm()) || !jwt.verify(new MACVerifier(secret)))
                throw new BadCredentialsException("JWT token is invalid");
            JWTClaimsSet claims = jwt.getJWTClaimsSet();
            if (claims.getSubject() == null || claims.getSubject().isBlank()
                || claims.getIssueTime() == null || claims.getExpirationTime() == null
                || !claims.getExpirationTime().after(claims.getIssueTime())
                || claims.getIssueTime().after(new Date())
                || (claims.getNotBeforeTime() != null && claims.getNotBeforeTime().after(new Date())))
                throw new BadCredentialsException("JWT claims are invalid");
            return claims;
        } catch (ParseException | JOSEException | IllegalArgumentException ex) {
            throw new BadCredentialsException("JWT token is invalid", ex);
        }
    }
    public JWTClaimsSet validateToken(String token) {
        JWTClaimsSet claims = extractClaims(token);
        if (!claims.getExpirationTime().after(new Date()))
            throw new BadCredentialsException("JWT token has expired");
        return claims;
    }
    public String extractUsername(String token) { return validateToken(token).getSubject(); }
    public boolean isTokenExpired(String token) {
        return !extractClaims(token).getExpirationTime().after(new Date());
    }
    public boolean isTokenValid(String token, UserDetails user) {
        try { return isTokenValid(validateToken(token), user); }
        catch (BadCredentialsException ex) { return false; }
    }
    public boolean isTokenValid(JWTClaimsSet claims, UserDetails user) {
        return claims.getSubject().equals(user.getUsername()) && claims.getExpirationTime().after(new Date())
            && user.isEnabled() && user.isAccountNonLocked() && user.isAccountNonExpired() && user.isCredentialsNonExpired();
    }
}
