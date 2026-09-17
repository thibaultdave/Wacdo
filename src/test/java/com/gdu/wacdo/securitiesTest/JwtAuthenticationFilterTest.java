package com.gdu.wacdo.securitiesTest;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.gdu.wacdo.builders.ErrorResponseBuilder;
import com.gdu.wacdo.dto.ErrorResponseDTO;
import com.gdu.wacdo.securities.JwtAuthenticationFilter;
import com.gdu.wacdo.securities.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class JwtAuthenticationFilterTest {

    private JwtService jwtService;
    private UserDetailsService userDetailsService;
    private ErrorResponseBuilder errorResponseBuilder;
    private FilterChain filterChain;

    private JwtAuthenticationFilter filter;

    @BeforeEach
    void setUp() {

        jwtService = mock(JwtService.class);
        userDetailsService = mock(UserDetailsService.class);
        errorResponseBuilder = mock(ErrorResponseBuilder.class);
        filterChain = mock(FilterChain.class);

        filter = new JwtAuthenticationFilter(
                jwtService,
                userDetailsService,
                errorResponseBuilder
        );

        SecurityContextHolder.clearContext();
    }

    @Test
    void doFilter_shouldContinueChain_whenAuthorizationHeaderIsMissing() throws Exception {

        MockHttpServletRequest request = new MockHttpServletRequest();

        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilter(
                request,
                response,
                filterChain
        );

        verify(filterChain).doFilter(request, response);

        verifyNoInteractions(jwtService);
        verifyNoInteractions(userDetailsService);
    }

    @Test
    void doFilter_shouldContinueChain_whenAuthorizationHeaderIsNotBearer() throws Exception {

        MockHttpServletRequest request = new MockHttpServletRequest();

        request.addHeader(
                "Authorization",
                "Basic abc123"
        );

        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilter(
                request,
                response,
                filterChain
        );

        verify(filterChain).doFilter(request, response);

        verifyNoInteractions(jwtService);
        verifyNoInteractions(userDetailsService);
    }

    @Test
    void doFilter_shouldAuthenticateUser_whenTokenIsValid() throws Exception {

        String token = "valid-token";
        String email = "jean.dupont@test.com";

        UserDetails userDetails = User
                .withUsername(email)
                .password("password")
                .roles("USER")
                .build();

        MockHttpServletRequest request = new MockHttpServletRequest();

        request.addHeader(
                "Authorization",
                "Bearer " + token
        );

        MockHttpServletResponse response = new MockHttpServletResponse();

        when(jwtService.extractEmail(token))
                .thenReturn(email);

        when(userDetailsService.loadUserByUsername(email))
                .thenReturn(userDetails);

        when(jwtService.isTokenValid(token, userDetails))
                .thenReturn(true);

        filter.doFilter(
                request,
                response,
                filterChain
        );

        assertNotNull(
                SecurityContextHolder
                        .getContext()
                        .getAuthentication()
        );

        assertEquals(
                email,
                SecurityContextHolder
                        .getContext()
                        .getAuthentication()
                        .getName()
        );

        verify(filterChain).doFilter(request, response);
    }

    @Test
    void doFilter_shouldReturn401_whenTokenIsExpired() throws Exception {

        doFilter_shouldReturn401(
                "expired-token",
                "JWT token has expired.",
                "error.jwt.expired",
                new TokenExpiredException(
                        "JWT token has expired.",
                        null
                )
        );
    }

    @Test
    void doFilter_shouldReturn401_whenTokenIsInvalid() throws Exception {

        doFilter_shouldReturn401(
                "invalid-token",
                "Invalid JWT token.",
                "error.jwt.invalid",
                new JWTVerificationException(
                        "Invalid JWT token."
                )
        );
    }

    void doFilter_shouldReturn401(
            String token,
            String errorMessage,
            String errorMessageKey,
            JWTVerificationException exception
    )  throws Exception {

        int status = HttpServletResponse.SC_UNAUTHORIZED;

        MockHttpServletRequest request = new MockHttpServletRequest();

        request.addHeader(
                "Authorization",
                "Bearer " + token
        );

        MockHttpServletResponse response = new MockHttpServletResponse();

        ErrorResponseDTO errorResponse =
                new ErrorResponseDTO(
                        status,
                        errorMessage
                );

        when(jwtService.extractEmail(token))
                .thenThrow(exception);

        when(errorResponseBuilder.build(
                status,
                errorMessageKey
        )).thenReturn(errorResponse);

        filter.doFilter(
                request,
                response,
                filterChain
        );

        assertEquals(
                status,
                response.getStatus()
        );

        verify(errorResponseBuilder).build(
                status,
                errorMessageKey
        );

        verify(filterChain, never())
                .doFilter(request, response);

        assertNull(
                SecurityContextHolder
                        .getContext()
                        .getAuthentication()
        );
    }
}