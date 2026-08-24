package com.gdu.wacdo.exceptions;

import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class EmailNotFoundException extends UsernameNotFoundException {

    public EmailNotFoundException(String message, String id) {
        super(message + id);
    }
}