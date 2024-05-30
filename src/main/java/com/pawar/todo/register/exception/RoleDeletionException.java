package com.pawar.todo.register.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class RoleDeletionException extends Exception{
	
	public RoleDeletionException(String message, Exception e) {
        super(message);
    }
	
	
}
