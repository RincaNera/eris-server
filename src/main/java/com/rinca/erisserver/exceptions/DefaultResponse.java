package com.rinca.erisserver.exceptions;

import java.time.LocalDateTime;

public record DefaultResponse(
    String code,
    String message,
    LocalDateTime timestamp
) {
    public DefaultResponse(String code, String message) {
        this(code, message, LocalDateTime.now());
    }
}
