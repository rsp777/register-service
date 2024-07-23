package com.pawar.todo.register.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class PermissionAssignmentException extends RuntimeException {
    public PermissionAssignmentException(String message, Throwable cause) {
        super(message, cause);
    }
}

