package com.kzone.brms.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class FailedToCreateSourceFile extends RuntimeException {
    public FailedToCreateSourceFile(String message) {
        super(message);
    }
}
