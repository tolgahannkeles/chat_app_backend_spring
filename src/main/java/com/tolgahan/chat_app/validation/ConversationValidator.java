package com.tolgahan.chat_app.validation;

import com.tolgahan.chat_app.exceptions.InvalidArgumentException;

public class ConversationValidator {

    public static void validateTitle(String title) {
        if (title == null || title.isEmpty()) {
            throw new InvalidArgumentException("Title cannot be empty");
        }
    }
}
