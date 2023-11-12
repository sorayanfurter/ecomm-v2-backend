package com.project.ecommv2backend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.UNAUTHORIZED)
public class UserDoesNotHavePermission extends RuntimeException {



    public  UserDoesNotHavePermission (String message) {
        super(message);
    }
}
