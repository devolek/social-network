package com.skillbox.socialnetwork.main.dto.profile.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.skillbox.socialnetwork.main.dto.person.response.PersonResponseDto;
import com.skillbox.socialnetwork.main.dto.universal.Dto;
import com.skillbox.socialnetwork.main.model.enumerated.PostType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WallResponseDto implements Dto {
    private int id;
    private long time;
    private PersonResponseDto author;
    private String title;
    @JsonProperty("post_text")
    private String text;
    @JsonProperty("is_blocked")
    private Boolean blocked;
    @JsonProperty("my_like")
    private boolean myLike;
    private Integer likes;
    private List<Dto> comments;
    private PostType type;
    private List<String> tags;
}
