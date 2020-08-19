package com.skillbox.socialnetwork.main.service.impl;

import com.skillbox.socialnetwork.main.aspect.MethodLogWithTime;
import com.skillbox.socialnetwork.main.dto.like.request.LikeRequest;
import com.skillbox.socialnetwork.main.dto.like.response.LikeResponseDto;
import com.skillbox.socialnetwork.main.dto.universal.BaseResponse;
import com.skillbox.socialnetwork.main.dto.universal.Dto;
import com.skillbox.socialnetwork.main.dto.universal.ResponseFactory;
import com.skillbox.socialnetwork.main.model.*;
import com.skillbox.socialnetwork.main.model.enumerated.NotificationCode;
import com.skillbox.socialnetwork.main.repository.CommentLikeRepository;
import com.skillbox.socialnetwork.main.repository.CommentRepository;
import com.skillbox.socialnetwork.main.repository.LikeRepository;
import com.skillbox.socialnetwork.main.repository.PostRepository;
import com.skillbox.socialnetwork.main.service.LikeService;
import com.skillbox.socialnetwork.main.service.NotificationService;
import com.skillbox.socialnetwork.main.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class LikeServiceImpl implements LikeService {
    private final LikeRepository postLikeRepository;
    private final CommentLikeRepository commentLikeRepository;
    private final PostRepository postRepository;
    private final NotificationService notificationService;
    private final PostService postService;
    private final CommentRepository commentRepository;

    @Autowired
    public LikeServiceImpl(LikeRepository likeRepository, CommentLikeRepository commentLikeRepository, PostRepository postRepository, NotificationService notificationService, PostService postService, CommentRepository commentRepository)
    {
        this.postLikeRepository = likeRepository;
        this.commentLikeRepository = commentLikeRepository;
        this.postRepository = postRepository;
        this.notificationService = notificationService;
        this.postService = postService;
        this.commentRepository = commentRepository;
    }

    @Override
    public Dto save(PostLike like)
    {
        if (!isLiked(like.getPost(), like.getPerson()))
        {
            postLikeRepository.save(like);
        }
        notificationService.addNotification(like.getPerson(), like.getPost()
                .getAuthor(), NotificationCode.LIKE, "Пользователь " + like.getPerson() + " оценил вашу запись.");

        return new LikeResponseDto(like.getPost().getLikes().size(), like.getPost().getLikes().stream()
                .map(PostLike::getPerson).map(Person::getId).collect(Collectors.toList()));
    }

    @Override
    @MethodLogWithTime(userAuth = true, fullMessage = "Like check")
    public Boolean isLiked(Post post, Person person)
    {
        return post.getLikes().stream().map(PostLike::getPerson).collect(Collectors.toList()).contains(person);
    }

    @Override
    public PostLike getLike(Integer id)
    {
        return postLikeRepository.findPostLikeById(id);
    }

    @Override
    @MethodLogWithTime(userAuth = true, fullMessage = "Like removed")
    public Dto delete(Integer id, Integer postId, String type, Person person)
    {
        switch (type)
        {
            case "Post":
                Post post = postRepository.findPostById(id);
                PostLike postLike = postLikeRepository.findPostLikeByPostAndPerson(post, person);
                postLikeRepository.delete(postLike);
                return new LikeResponseDto(post.getLikes().size(), new ArrayList<>());
            case "Comment":
                PostComment comment = commentRepository.findPostCommentById(id);
                CommentLike commentLike = commentLikeRepository.findCommentLikeByCommentAndPerson(comment, person);
                commentLikeRepository.delete(commentLike);
                return new LikeResponseDto(comment.getCommentLikes().size(), new ArrayList<>());
        }
        return null;
    }

    @Override
    @MethodLogWithTime(userAuth = true, fullMessage = "Post or Comment was liked")
    public BaseResponse putLike(LikeRequest request, Person person)
    {
        switch (request.getType())
        {
            case "Post":
                Post post = postService.findById(request.getId());
                postLikeRepository.save(
                        PostLike.builder()
                                .person(person)
                                .post(post)
                                .time(new Date())
                                .build());
                notificationService.addNotification(person,
                        post.getAuthor(), NotificationCode.LIKE, "Пользователь "+person.getLastName()+" "+person.getFirstName()+" оценил ваш пост.");
                return ResponseFactory.responseOk();
            case "Comment":
                Optional<PostComment> comment = commentRepository.findById(request.getId());
                commentLikeRepository.save(
                        CommentLike.builder()
                                .person(person)
                                .comment(comment.get())
                                .time(new Date())
                                .build());
                notificationService.addNotification(person,
                        comment.get().getAuthor(), NotificationCode.LIKE, "Пользователь "+person.getLastName()+" "+person.getFirstName()+" оценил ваш комментарий.");
                return ResponseFactory.responseOk();
        }
        return null;
    }
}
