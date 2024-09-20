package com.tolgahan.chat_app.response;

import lombok.Data;

import java.util.Date;
import java.util.UUID;
@Data
public class ErrorResponse<T> {
    private UUID id;
    private Date timestamp;
    private T error;

    public ErrorResponse(T error) {
        this.id = UUID.randomUUID();
        this.timestamp = new Date();
        this.error = error;
    }
}
