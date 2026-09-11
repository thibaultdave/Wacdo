package com.gdu.wacdo.factories;

import com.gdu.wacdo.dto.CollaboratorRequestDTO;
import com.gdu.wacdo.dto.CollaboratorResponseDTO;

import java.time.LocalDate;

public class CollaboratorDTOTestFactory {

    private static CollaboratorRequestDTO createTestCollaboratorRequestDTO(
            String name,
            String firstName,
            String email,
            String firstHireDate,
            boolean admin,
            String password
    ) {
        CollaboratorRequestDTO request = new CollaboratorRequestDTO();
        request.setName(name);
        request.setFirstName(firstName);
        request.setEmail(email);
        request.setFirstHireDate(LocalDate.parse(firstHireDate));
        request.setAdmin(admin);
        request.setPassword(password);
        return request;
    }

    public static CollaboratorRequestDTO createGenericCollaboratorRequestDTO() {
        return createTestCollaboratorRequestDTO(
                "Dupont",
                "Jean",
                "jean.dupont@test.com",
                "2000-10-10",
                false,
                "password123"
        );
    }

    public static CollaboratorRequestDTO createUpdatedCollaboratorRequestDTO() {
        return createTestCollaboratorRequestDTO(
                "Bouchard",
                "Gérard",
                "gerard.bouchard@test.com",
                "2010-10-10",
                true,
                "password456"
        );
    }

    public static CollaboratorRequestDTO createFailedCollaboratorRequestDTO() {
        return createTestCollaboratorRequestDTO(
                "",
                "",
                "invalid-email",
                "2099-01-01",
                false,
                "abc"
        );
    }

    private static CollaboratorResponseDTO createTestCollaboratorResponseDTO(
            long id,
            String name,
            String firstName,
            String email,
            String firstHireDate,
            boolean admin
    ) {
        CollaboratorResponseDTO response = new CollaboratorResponseDTO();
        response.setId(id);
        response.setName(name);
        response.setFirstName(firstName);
        response.setEmail(email);
        response.setFirstHireDate(LocalDate.parse(firstHireDate));
        response.setAdmin(admin);
        return response;
    }

    public static  CollaboratorResponseDTO createGenericCollaboratorResponseDTO() {
        return createTestCollaboratorResponseDTO(
                1L,
                "Dupont",
                "Jean",
                "jean.dupont@test.com",
                "2000-10-10",
                false
        );
    }

    public static CollaboratorResponseDTO createUpdatedCollaboratorResponseDTO() {
        return createTestCollaboratorResponseDTO(
                1L,
                "Bouchard",
                "Gérard",
                "gerard.bouchard@test.com",
                "2010-10-10",
                true
        );
    }
}