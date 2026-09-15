package com.gdu.wacdo.exceptions;

import lombok.Getter;

@Getter
public abstract class ResourceException extends RuntimeException {

    private final Object[] args;

    protected ResourceException(String messageKey, Object... args) {
        super(messageKey);
        this.args = args;
    }
}