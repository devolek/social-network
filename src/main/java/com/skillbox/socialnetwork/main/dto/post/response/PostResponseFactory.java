package com.skillbox.socialnetwork.main.dto.post.response;

import com.skillbox.socialnetwork.main.dto.comment.response.CommentResponseFactory;
import com.skillbox.socialnetwork.main.dto.person.response.PersonResponseFactory;
import com.skillbox.socialnetwork.main.dto.universal.BaseResponse;
import com.skillbox.socialnetwork.main.dto.universal.BaseResponseList;
import com.skillbox.socialnetwork.main.dto.universal.ResponseFactory;
import com.skillbox.socialnetwork.main.model.Post;
import com.skillbox.socialnetwork.main.model.Tag;
import com.skillbox.socialnetwork.main.model.enumerated.PostType;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class PostResponseFactory {
    public static BaseResponse getSinglePost(Post post) {
        return ResponseFactory.getBaseResponse(postToDto(post));
    }

    public static BaseResponseList getPostsList(List<Post> posts, int total, int offset, int limit) {
        return ResponseFactory.getBaseResponseList(
                posts.stream()
                        .filter(post -> post.getTime().before(new Date()))
                        .map(PostResponseFactory::postToDto)
                        .collect(Collectors.toList()),
                total, offset, limit);
    }

    public static BaseResponseList getPostsListWithLimit(List<Post> posts, int offset, int limit) {
        return ResponseFactory.getBaseResponseListWithLimit(
                posts.stream()
                        .filter(post -> post.getTime().before(new Date()))
                        .map(PostResponseFactory::postToDto)
                        .collect(Collectors.toList()),
                offset, limit);
    }

    private static PostResponseDto postToDto(Post post) {
        return new PostResponseDto(
                post.getId(),
                post.getTime().getTime(),
                PersonResponseFactory.getPersonDto(post.getAuthor()),
                post.getTitle(),
                post.getPostText(),
                post.getIsBlocked(),
                post.getLikes().size(),
                //@TODO: Возвращать тут комментарии
                CommentResponseFactory.getCommentList(post.getComments()),
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
