package com.kzone.brms.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;


@ResponseStatus(HttpStatus.BAD_REQUEST)
public class RuleSetExistsException extends RuntimeException {

    public RuleSetExistsException(String message){
        super(message);
    }

}
