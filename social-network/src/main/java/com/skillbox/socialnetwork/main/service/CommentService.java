package com.skillbox.socialnetwork.main.service;

import com.skillbox.socialnetwork.main.dto.comment.request.CommentRequest;
import com.skillbox.socialnetwork.main.dto.universal.Dto;
import com.skillbox.socialnetwork.main.dto.universal.Response;
import com.skillbox.socialnetwork.main.model.Person;
import com.skillbox.socialnetwork.main.model.PostComment;

public interface CommentService {
    PostComment findById(Integer id);

    PostComment save(PostComment comment);

    Dto addComment(int postId, CommentRequest request, int authorId);

    Dto updateComment(int commentId, CommentRequest request);

    Dto deleteComment(int commentId);

    Response getComments(int postId, Person person);
}