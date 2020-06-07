package com.skillbox.socialnetwork.main.dto.profile;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.skillbox.socialnetwork.main.dto.person.response.PersonDto;
import com.skillbox.socialnetwork.main.dto.universal.ResponseDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WallDto implements ResponseDto {
    private PersonDto author;
    private String title;
    @JsonProperty("post_text")
    private String text;
    @JsonProperty("is_blocked")
    private Boolean blocked;
    private Integer likes;
    private List comments;
    private String type;
}