package com.skillbox.socialnetwork.main.dto.post.response;

import com.skillbox.socialnetwork.main.dto.comment.response.CommentResponseFactory;
import com.skillbox.socialnetwork.main.dto.person.response.PersonResponseFactory;
import com.skillbox.socialnetwork.main.dto.universal.BaseResponse;
import com.skillbox.socialnetwork.main.dto.universal.BaseResponseList;
import com.skillbox.socialnetwork.main.dto.universal.Dto;
import com.skillbox.socialnetwork.main.dto.universal.ResponseFactory;
import com.skillbox.socialnetwork.main.model.Person;
import com.skillbox.socialnetwork.main.model.Post;
import com.skillbox.socialnetwork.main.model.PostLike;
import com.skillbox.socialnetwork.main.model.Tag;
import com.skillbox.socialnetwork.main.model.enumerated.PostType;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class PostResponseFactory {

    public static BaseResponse getSinglePost(Post post, Person person) {
        return ResponseFactory.getBaseResponse(postToDto(post, person));
    }

    public static BaseResponseList getPostsList(List<Post> posts, int total, int offset, int limit, Person person) {
        return ResponseFactory.getBaseResponseList(
                posts.stream()
                        .filter(post -> post.getTime().before(new Date()))
                        .map(post -> PostResponseFactory.postToDto(post, person))
                        .collect(Collectors.toList()),
                total, offset, limit);
    }

    public static BaseResponseList getPostsListWithLimit(List<Post> posts, int offset, int limit, Person person) {
        List<Dto> postsDto = new ArrayList<>();
        for (Post post : posts) {
            if (post.getTime().before(new Date()))
                postsDto.add(PostResponseFactory.postToDto(post, person));
        }

        return ResponseFactory.getBaseResponseListWithLimit(
                posts.stream()
                        .filter(post -> post.getTime().before(new Date()))
                        .map(post -> PostResponseFactory.postToDto(post, person))
                        .collect(Collectors.toList()),
                offset, limit);
    }

    private static PostResponseDto postToDto(Post post, Person person) {
        return new PostResponseDto(
                post.getId(),
                post.getTime().getTime(),
                PersonResponseFactory.getPersonDto(post.getAuthor()),
                post.getTitle(),
                post.getPostText(),
                post.getIsBlocked(),
                post.getLikes().stream().map(PostLike::getPerson).collect(Collectors.toList()).contains(person),
                post.getLikes().size(),
                CommentResponseFactory.getCommentList(post.getComments(), null, person),
                post.getTime().before(new Date()) ? PostType.POSTED : PostType.QUEUED,
                post.getTags() != null
                        ? post.getTags()
                        .stream()
                        .map(Tag::getTag)
                        .collect(Collectors.toList())
                        : new ArrayList<>()
        );
    }
}
