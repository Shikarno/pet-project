package com.petproject.service.exception;

import lombok.Getter;

@Getter
public class UserNotFoundException extends RuntimeException{

    public UserNotFoundException(String userName, Throwable cause) {
        super(cause);
        this.userName = userName;
    }

    private String userName;

}
