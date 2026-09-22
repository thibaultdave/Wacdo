package com.gdu.wacdo.servicesTest;

import com.gdu.wacdo.exceptions.ResourceNotFoundException;
import com.gdu.wacdo.repositories.CollaboratorRepository;
import com.gdu.wacdo.services.CustomUserDetailsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
public class CustomUserDetailsServiceTest {

    @Mock
    private CollaboratorRepository collaboratorRepository;

    private CustomUserDetailsService userDetailsService;

    @BeforeEach
    void setUp() {
        userDetailsService = new CustomUserDetailsService(collaboratorRepository);
    }

    @Test
    void loadUserByUsername_shouldThrowException_whenEmailNotFound() {
        String email = "notFound@waldo.com";

        when(collaboratorRepository.findByEmail(email))
                .thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> userDetailsService.loadUserByUsername(email)
        );

        assertEquals(
                "error.collaborator.email-not-found",
                exception.getMessage()
        );

        verify(collaboratorRepository).findByEmail(email);
    }
}