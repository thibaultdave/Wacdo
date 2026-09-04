package com.gdu.wacdo.builders;

import com.gdu.wacdo.dto.ErrorResponseDTO;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
public class ErrorResponseBuilder {

    private final MessageSource messageSource;

    public ErrorResponseBuilder(
            MessageSource messageSource
    ) {
        this.messageSource = messageSource;
    }

    public ErrorResponseDTO build(
            int status,
            String messageKey,
            Object... args
    ) {

        String message = messageSource.getMessage(
                messageKey,
                args,
                LocaleContextHolder.getLocale()
        );

        return new ErrorResponseDTO(
                status,
                message
        );
    }
}