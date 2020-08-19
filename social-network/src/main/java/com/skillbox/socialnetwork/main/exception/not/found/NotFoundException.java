package com.skillbox.socialnetwork.main.exception.not.found;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class NotFoundException extends RuntimeException {
    private String errorName;
    public NotFoundException(String errorName, String message){
        super(message);
        this.errorName = errorName;
    }
}
