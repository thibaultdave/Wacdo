package com.gdu.wacdo.exceptions;

    public class ResourceAlreadyExistsException extends ResourceException {

    public ResourceAlreadyExistsException(String messageKey, Object... args) {
        super(messageKey, args);
    }
}