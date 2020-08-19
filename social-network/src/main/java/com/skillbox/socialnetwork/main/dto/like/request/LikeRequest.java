package com.skillbox.socialnetwork.main.dto.like.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.skillbox.socialnetwork.main.dto.universal.Dto;
import lombok.Data;

@Data
public class LikeRequest implements Dto {
    @JsonProperty("item_id")
    private int id;
    private String type;
}
