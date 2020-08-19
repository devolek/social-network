package com.skillbox.socialnetwork.main.dto.post.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class AddPostRequestDto {
    private String title;
    @JsonProperty("post_text")
    private String text;
    private List<String> tags;
}
