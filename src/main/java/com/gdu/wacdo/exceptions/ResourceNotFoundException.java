package com.gdu.wacdo.exceptions;

public class ResourceNotFoundException extends ResourceException {

    public ResourceNotFoundException(String messageKey, Object... args) {
        super(messageKey, args);
    }
}