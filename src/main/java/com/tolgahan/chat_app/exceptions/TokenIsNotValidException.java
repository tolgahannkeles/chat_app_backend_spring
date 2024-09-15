package com.tolgahan.chat_app.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value= HttpStatus.BAD_REQUEST, reason="Bearer Token cannot be identified.")  // 404
public class TokenIsNotValidException extends RuntimeException {
}
