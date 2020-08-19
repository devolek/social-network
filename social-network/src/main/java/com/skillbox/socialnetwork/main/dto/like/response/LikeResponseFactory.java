package com.skillbox.socialnetwork.main.dto.like.response;

import com.skillbox.socialnetwork.main.dto.universal.BaseResponse;
import com.skillbox.socialnetwork.main.dto.universal.ResponseFactory;
import com.skillbox.socialnetwork.main.model.Person;
import com.skillbox.socialnetwork.main.model.Post;
import com.skillbox.socialnetwork.main.model.PostLike;

import java.util.stream.Collectors;

public class LikeResponseFactory {
    public static BaseResponse getLikeDto(Post post) {
        return ResponseFactory.getBaseResponse(
                new LikeResponseDto(post.getLikes().size(),
                        post.getLikes().stream()
                                .map(PostLike::getPerson).map(Person::getId).collect(Collectors.toList()))
        );
    }

    public static BaseResponse isLiked(Boolean result) {
        return ResponseFactory.getBaseResponse(new IsLikeDto(result));
    }
}