package com.gdu.wacdo.securities;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class JwtService {

    // TODO place secret key in application.properties
    private final String SECRET_KEY = "ma-cle-secrete-tres-longue-a-remplacer";
    private final long EXPIRATION_TIME = 1000 * 60 * 60; // 1 hour

    public String generateToken(String email) {

        Algorithm algorithm =
                Algorithm.HMAC256(SECRET_KEY);

        return JWT.create()
                .withSubject(email)
                .withIssuedAt(new Date())
                .withExpiresAt(
                        new Date(
                                System.currentTimeMillis() + EXPIRATION_TIME
                        )
                )
                .sign(algorithm);
    }

    public String extractEmail(String token) {
        Algorithm algorithm = Algorithm.HMAC256(SECRET_KEY);

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