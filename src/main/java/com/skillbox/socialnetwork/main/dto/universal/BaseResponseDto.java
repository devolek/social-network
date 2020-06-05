package com.skillbox.socialnetwork.main.dto.universal;

import com.skillbox.socialnetwork.main.dto.ResponseDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@Data
public class BaseResponseDto extends ErrorResponseDto {
    private Long timestamp;
    private ResponseDto data;

    public BaseResponseDto(){
        super("string");
        timestamp = new Date().getTime();
        data = new DataDto("ok");
    }


    public BaseResponseDto(String error, Long timestamp, ResponseDto data) {
        super(error);
        this.timestamp = timestamp;
        this.data = data;
    }
}