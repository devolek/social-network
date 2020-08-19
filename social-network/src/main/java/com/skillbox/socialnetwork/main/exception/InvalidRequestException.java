package com.skillbox.socialnetwork.main.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class InvalidRequestException extends RuntimeException {
    private String error;
    public InvalidRequestException(String message){
        super(message);
        error = "invalid_request";
    }
}
