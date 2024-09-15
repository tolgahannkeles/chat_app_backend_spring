package com.tolgahan.chat_app.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InsufficientArgumentException extends RuntimeException {
    public InsufficientArgumentException(String message) {
        super(message);
    }
}
