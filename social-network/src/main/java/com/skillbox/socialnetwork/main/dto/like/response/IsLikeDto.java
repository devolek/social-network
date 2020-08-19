package com.skillbox.socialnetwork.main.dto.like.response;

import com.skillbox.socialnetwork.main.dto.universal.Dto;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class IsLikeDto implements Dto {
    private Boolean likes;
}
