package com.skillbox.socialnetwork.main.exception.user.input;

import lombok.Data;

@Data
public class UserException extends RuntimeException {
    private String errorName;
    public UserException(String errorName, String message){
        super(message);
        this.errorName = errorName;
    }
}
