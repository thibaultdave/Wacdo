package com.gdu.wacdo.securities;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.expiration}")
    private long expirationTime;

    public String generateToken(String email) {

        Algorithm algorithm =
                Algorithm.HMAC256(secretKey);

        return JWT.create()
                .withSubject(email)
                .withIssuedAt(new Date())
                .withExpiresAt(
                        new Date(
                                System.currentTimeMillis() + expirationTime
                        )
                )
                .sign(algorithm);
    }

    public String extractEmail(String token) {
        Algorithm algorithm = Algorithm.HMAC256(secretKey);

        return JWT.require(algorithm)
                .build()
                .verify(token)
                .getSubject();
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        String email = extractEmail(token);

        return email.equals(userDetails.getUsername());
    }
}