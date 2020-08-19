package com.skillbox.socialnetwork.main.dto.comment.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.skillbox.socialnetwork.main.dto.universal.Dto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentDto implements Dto {
    @JsonProperty("parent_id")
    private int parentId;
    @JsonProperty("comment_text")
    private String text;
    private int id;
    @JsonProperty("post_id")
    private int postId;
    private long time;
    @JsonProperty("author")
    private Dto author;
    @JsonProperty("is_blocked")
    private boolean blocked;

    @JsonProperty("sub_comments")
    private List<Dto> subComments;

    @JsonProperty("like_count")
    private int likeCount;

    @JsonProperty("is_my_like")
    private boolean isMyLike;
}
