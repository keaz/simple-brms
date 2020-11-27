package com.kzone.brms.exception;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class GenericGitException extends RuntimeException {

    public GenericGitException(String message) {
        super(message);
    }
    public GenericGitException(Exception e) {
        super(e);
    }
}
