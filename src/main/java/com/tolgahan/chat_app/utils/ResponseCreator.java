package com.tolgahan.chat_app.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;

public class ResponseCreator {

    private static final ObjectMapper objectMapper = new ObjectMapper();  // Add ObjectMapper

    private static Map<String, Object> createResponseMap(String key, Object value) {
        Map<String, Object> map = new HashMap<>();
        map.put(key, value);
        return map;
    }

    private static String convertToJson(Object object) throws JsonProcessingException {
        return objectMapper.writeValueAsString(object);
    }

    public static ResponseEntity<String> badRequest(String message) {
        try {
            return ResponseEntity.badRequest()
                    .header("Content-Type", "application/json")
                    .body(convertToJson(createResponseMap("message", message)));
        } catch (JsonProcessingException e) {
            return ResponseEntity.badRequest()
                    .header("Content-Type", "application/json")
                    .body(createResponseMap("message", message).toString());
        }
    }

    public static ResponseEntity<String> ok(Object output) {
        try {
            String json = convertToJson(createResponseMap("response", output));
            return ResponseEntity.ok()
                    .header("Content-Type", "application/json")
                    .body(json);
        } catch (JsonProcessingException e) {
            return ResponseEntity.ok()
                    .header("Content-Type", "application/json")
                    .body(createResponseMap("response", output).toString());
        }
    }

    public static ResponseEntity<String> internalServerError(String message) {
        try {
            return ResponseEntity.internalServerError()
                    .header("Content-Type", "application/json")
                    .body(convertToJson(createResponseMap("message", message)));
        } catch (JsonProcessingException e) {
            return ResponseEntity.internalServerError()
                    .header("Content-Type", "application/json")
                    .body(createResponseMap("message", message).toString());
        }
    }

    public static ResponseEntity<String> notFound() {
        return ResponseEntity.notFound()
                .header("Content-Type", "application/json")
                .build();
    }

    public static ResponseEntity<String> unauthorized(String message) {
        try {
            return ResponseEntity.status(401)
                    .header("Content-Type", "application/json")
                    .body(convertToJson(createResponseMap("message", message)));
        } catch (JsonProcessingException e) {
            return ResponseEntity.internalServerError()
                    .header("Content-Type", "application/json")
                    .body(createResponseMap("message", message).toString());
        }
    }

    public static ResponseEntity<String> forbidden(String message) throws JsonProcessingException {
        return ResponseEntity.status(403)
                .header("Content-Type", "application/json")
                .body(convertToJson(createResponseMap("message", message)));
    }

    public static ResponseEntity<String> created(Object output) throws JsonProcessingException {
        return ResponseEntity.status(201)
                .header("Content-Type", "application/json")
                .body(convertToJson(createResponseMap("response", output)));
    }

    public static ResponseEntity<String> noContent() {
        return ResponseEntity.noContent().build();
    }

    public static ResponseEntity<String> conflict(String message) throws JsonProcessingException {
        return ResponseEntity.status(409)
                .header("Content-Type", "application/json")
                .body(convertToJson(createResponseMap("message", message)));
    }

    public static ResponseEntity<String> unsupportedMediaType(String message) throws JsonProcessingException {
        return ResponseEntity.status(415)
                .header("Content-Type", "application/json")
                .body(convertToJson(createResponseMap("message", message)));
    }

    public static ResponseEntity<String> unprocessableEntity(String message) throws JsonProcessingException {
        return ResponseEntity.status(422)
                .header("Content-Type", "application/json")
                .body(convertToJson(createResponseMap("message", message)));
    }

    public static ResponseEntity<String> tooManyRequests(String message) throws JsonProcessingException {
        return ResponseEntity.status(429)
                .header("Content-Type", "application/json")
                .body(convertToJson(createResponseMap("message", message)));
    }

    public static ResponseEntity<String> internalServerError() {
        try {
            return ResponseEntity.internalServerError()
                    .header("Content-Type", "application/json")
                    .body(convertToJson(new HashMap<String, Object>()));
        } catch (JsonProcessingException e) {
            // Handle the exception if needed
            return ResponseEntity.internalServerError().build();
        }
    }
}
