package com.kzone.brms.exception;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class GitTagAlreadyExists extends GenericGitException {

    public GitTagAlreadyExists(String message) {
        super(message);
    }
    public GitTagAlreadyExists(Exception e) {
        super(e);
    }
}
