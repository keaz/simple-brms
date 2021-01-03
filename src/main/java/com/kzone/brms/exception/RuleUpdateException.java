package com.kzone.brms.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST)
public class RuleUpdateException extends RuntimeException {
    public RuleUpdateException(String message) {
        super(message);
    }
}
