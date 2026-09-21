package com.gdu.wacdo.factories;

import com.gdu.wacdo.dto.LoginRequestDTO;

public class LoginTestFactory {

    private static LoginRequestDTO createTestLoginRequestDTO(
            String email,
            String password
    ) {
        LoginRequestDTO request = new LoginRequestDTO();
        request.setEmail(email);
        request.setPassword(password);
        return request;
    }

    public static LoginRequestDTO createAdminLoginRequestDTO() {
        return createTestLoginRequestDTO(
                "gerard.bouchard@test.com",
                "password456"
        );
    }

    public static LoginRequestDTO createUserLoginRequestDTO() {
        return createTestLoginRequestDTO(
                "jean.dupont@test.com",
                "password123"
        );
    }
}