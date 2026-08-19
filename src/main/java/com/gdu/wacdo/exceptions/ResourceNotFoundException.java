package com.gdu.wacdo.exceptions;

public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message, Long id) {
        super(message + id);
    }
}