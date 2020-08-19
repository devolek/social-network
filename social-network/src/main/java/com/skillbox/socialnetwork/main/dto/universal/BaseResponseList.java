package com.skillbox.socialnetwork.main.dto.universal;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
public class BaseResponseList implements Response{
    private String error;
    private long timestamp;
    private Integer total;
    private Integer offset;
    private Integer perPage;
    private List<? extends Dto> data;

    public BaseResponseList(List<? extends Dto> data) {
        error = "string";
        timestamp = new Date().getTime();
        this.data = data;
    }

    public BaseResponseList(Integer total, Integer offset, Integer perPage, List<Dto> data) {
        error = "string";
        timestamp = new Date().getTime();
        this.total = total;
        this.offset = offset;
        this.perPage = perPage;
        this.data = data;
    }
}
