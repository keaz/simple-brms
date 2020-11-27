package com.kzone.brms.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class CommonRuleCreateException extends RuntimeException {
    public CommonRuleCreateException(String message) {
        super(message);
    }
}
