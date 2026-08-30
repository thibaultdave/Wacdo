package com.gdu.wacdo.securities;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gdu.wacdo.constants.ExceptionMessages;
import com.gdu.wacdo.dto.ErrorResponseDTO;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    public JwtAuthenticationFilter(
            JwtService jwtService,
            UserDetailsService userDetailsService) {

        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        final String token = authHeader.substring(7);

        try {
            String email = jwtService.extractEmail(token);

            UserDetails userDetails = userDetailsService.loadUserByUsername(email);

            if (jwtService.isTokenValid(token, userDetails) && SecurityContextHolder
                    .getContext()
                    .getAuthentication() == null
            ) {
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities()
                        );

                authentication.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );
                SecurityContextHolder
                        .getContext()
                        .setAuthentication(authentication);
            }

        } catch (TokenExpiredException e) {
            logger.debug(ExceptionMessages.EXPIRED_JWT_TOKEN, e);

            SecurityContextHolder.clearContext();

            sendErrorResponse(
                    response,
                    HttpServletResponse.SC_UNAUTHORIZED,
                    ExceptionMessages.EXPIRED_JWT_TOKEN
            );

            return;

        } catch (JWTVerificationException e) {
            logger.debug(ExceptionMessages.INVALID_JWT_TOKEN, e);

            SecurityContextHolder.clearContext();

            sendErrorResponse(
                    response,
                    HttpServletResponse.SC_UNAUTHORIZED,
                    ExceptionMessages.INVALID_JWT_TOKEN
            );

            return;
        }

        filterChain.doFilter(request, response);
    }

    private void sendErrorResponse(
            HttpServletResponse response,
            int status,
            String message
    ) throws IOException {

        ErrorResponseDTO error = new ErrorResponseDTO(
                status,
                message
        );

        response.setStatus(status);
        response.setContentType("application/json");

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();

        objectMapper.writeValue(
                response.getWriter(),
                error
        );
    }
}