package com.gdu.wacdo.securitiesTest;

import com.auth0.jwt.exceptions.TokenExpiredException;
import com.gdu.wacdo.securities.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    private JwtService jwtService;

    @BeforeEach
    void setUp() {

        jwtService = new JwtService();

        ReflectionTestUtils.setField(
                jwtService,
                "secretKey",
                "the-secret-key-is-not-a-lie"
        );

        ReflectionTestUtils.setField(
                jwtService,
                "expirationTime",
                3600000L
        );
    }

    @Test
    void generateToken_shouldGenerateValidToken() {

        String token = jwtService.generateToken(
                "jean.dupont@test.com"
        );

        assertNotNull(token);
        assertFalse(token.isBlank());
    }

    @Test
    void extractEmail_shouldReturnEmailFromToken() {

        String email = "jean.dupont@test.com";

        String token = jwtService.generateToken(email);

        String extractedEmail = jwtService.extractEmail(token);

        assertEquals(email, extractedEmail);
    }

    @Test
    void isTokenValid_shouldReturnTrue_whenTokenBelongsToUser() {

        String email = "jean.dupont@test.com";

        String token = jwtService.generateToken(email);

        UserDetails userDetails = User
                .withUsername(email)
                .password("password")
                .roles("USER")
                .build();

        assertTrue(
                jwtService.isTokenValid(token, userDetails)
        );
    }

    @Test
    void isTokenValid_shouldReturnFalse_whenTokenDoesNotBelongToUser() {

        String token = jwtService.generateToken(
                "jean.dupont@test.com"
        );

        UserDetails userDetails = User
                .withUsername("gerard.bouchard@test.com")
                .password("password")
                .roles("USER")
                .build();

        assertFalse(
                jwtService.isTokenValid(token, userDetails)
        );
    }

    @Test
    void extractEmail_shouldThrowException_whenTokenIsExpired() {

        ReflectionTestUtils.setField(
                jwtService,
                "expirationTime",
                -1000L
        );

        String token = jwtService.generateToken(
                "jean.dupont@test.com"
        );

        assertThrows(
                TokenExpiredException.class,
                () -> jwtService.extractEmail(token)
        );
    }
}