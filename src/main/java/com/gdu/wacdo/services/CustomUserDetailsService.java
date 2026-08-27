package com.gdu.wacdo.services;

import com.gdu.wacdo.constants.CollaboratorRoles;
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

        Collaborator collaborator = collaboratorRepository.findByEmail(email)
                        .orElseThrow(() -> new EmailNotFoundException(
                                ExceptionMessages.NO_COLLABORATOR_WITH_EMAIL, email
                        ));

        String role = collaborator.isAdmin()
                ? CollaboratorRoles.ADMIN_ROLE
                : CollaboratorRoles.USER_ROLE;

        return User.builder()
                .username(collaborator.getEmail())
                .password(collaborator.getPassword())
                .roles(role)
                .build();
    }
}