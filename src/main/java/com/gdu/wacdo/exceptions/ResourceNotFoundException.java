package com.gdu.wacdo.exceptions;

import lombok.Getter;

@Getter
public class ResourceNotFoundException extends RuntimeException {

    private final Object[] args;

    public ResourceNotFoundException(
            String messageKey,
            Object... args
    ) {
        super(messageKey);
        this.args = args;
    }

}