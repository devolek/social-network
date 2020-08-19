package com.skillbox.socialnetwork.main.service;

import com.skillbox.socialnetwork.main.dto.like.request.LikeRequest;
import com.skillbox.socialnetwork.main.dto.universal.BaseResponse;
import com.skillbox.socialnetwork.main.dto.universal.Dto;
import com.skillbox.socialnetwork.main.model.Person;
import com.skillbox.socialnetwork.main.model.Post;
import com.skillbox.socialnetwork.main.model.PostLike;

public interface LikeService {
    Dto save(PostLike like);

    Boolean isLiked(Post post, Person person);

    PostLike getLike(Integer id);

    Dto delete(Integer id, Integer postId, String type, Person person);

    BaseResponse putLike(LikeRequest request, Person authorizedUser);
}
