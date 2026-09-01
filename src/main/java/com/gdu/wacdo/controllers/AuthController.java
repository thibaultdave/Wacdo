package com.gdu.wacdo.controllers;

import com.gdu.wacdo.dto.CollaboratorRequestDTO;
import com.gdu.wacdo.dto.CollaboratorResponseDTO;
import com.gdu.wacdo.dto.LoginRequestDTO;
import com.gdu.wacdo.dto.LoginResponseDTO;
import com.gdu.wacdo.securities.JwtService;
import com.gdu.wacdo.services.CollaboratorService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final CollaboratorService collaboratorService;

    public AuthController(
            AuthenticationManager authenticationManager,
            JwtService jwtService,
            CollaboratorService collaboratorService
    ) {

        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.collaboratorService = collaboratorService;
    }

    @PostMapping("/login")
    public LoginResponseDTO login(@RequestBody LoginRequestDTO dto) {

        Authentication authentication = authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(
                                dto.getEmail(),
                                dto.getPassword()
                        )
                );

        String token = jwtService.generateToken(
                        authentication.getName()
                );

        return new LoginResponseDTO(token);
    }
// TODO set this to be more robust
    @PostMapping("/setup-admin")
    public CollaboratorResponseDTO setupAdmin(
            @RequestBody CollaboratorRequestDTO dto
    ) {
        return collaboratorService.createInitialAdmin(dto);
    }
}