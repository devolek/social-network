package com.skillbox.socialnetwork.main.service;

import com.skillbox.socialnetwork.main.dto.post.request.UpdatePostRequestDto;
import com.skillbox.socialnetwork.main.dto.universal.BaseResponse;
import com.skillbox.socialnetwork.main.dto.universal.BaseResponseList;
import com.skillbox.socialnetwork.main.model.Person;
import com.skillbox.socialnetwork.main.model.Post;

public interface PostService {
    Post findById(Integer id);

    Post save(Post post);

    BaseResponseList feeds(int offset, int limit, Person person);

    BaseResponse getPost(int id, int personId);

    BaseResponse editPost(int id, Long publishDate, UpdatePostRequestDto request, int personId);

    BaseResponse deletePost(int id);

    BaseResponseList searchPosts(String text, Long dateFrom, Long dateTo, String author, String tags, int offset, int limit, int personId);
}
