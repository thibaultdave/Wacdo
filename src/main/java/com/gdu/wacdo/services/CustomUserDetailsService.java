package com.gdu.wacdo.services;

import com.gdu.wacdo.constants.ExceptionMessages;
import com.gdu.wacdo.entities.Collaborator;
import com.gdu.wacdo.exceptions.EmailNotFoundException;
import com.gdu.wacdo.repositories.CollaboratorRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    private final CollaboratorRepository collaboratorRepository;

    public CustomUserDetailsService(
            CollaboratorRepository collaboratorRepository) {
        this.collaboratorRepository = collaboratorRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email)
            throws UsernameNotFoundException {

        Collaborator collaborator =
                collaboratorRepository.findByEmail(email)
                        .orElseThrow(() -> new EmailNotFoundException(
                                ExceptionMessages.EMAIL_NOT_FOUND, email
                        ));

        return User.builder()
                .username(collaborator.getEmail())
                .password(collaborator.getPassword())
                .roles(collaborator.isAdmin() ? "ADMIN" : "USER")
                .build();
    }
}