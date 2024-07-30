package com.pawar.todo.register.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class PermissionNotFoundException extends Exception{
	
	public PermissionNotFoundException(String message) {
        super(message);
    }
	
	
}
