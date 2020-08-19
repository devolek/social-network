package com.skillbox.socialnetwork.main.dto.universal;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;

@AllArgsConstructor
@Data
public class BaseResponse implements Response {
    private String error;
    private Long timestamp;
    private Dto data;

    public BaseResponse(Dto data) {
        error = "string";
        timestamp = new Date().getTime();
        this.data = data;
    }
}
