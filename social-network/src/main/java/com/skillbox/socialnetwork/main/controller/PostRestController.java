package com.skillbox.socialnetwork.main.controller;

import com.skillbox.socialnetwork.main.dto.comment.request.CommentRequest;
import com.skillbox.socialnetwork.main.dto.post.request.UpdatePostRequestDto;
import com.skillbox.socialnetwork.main.dto.universal.Dto;
import com.skillbox.socialnetwork.main.dto.universal.Response;
import com.skillbox.socialnetwork.main.dto.universal.ResponseFactory;
import com.skillbox.socialnetwork.main.security.jwt.JwtUser;
import com.skillbox.socialnetwork.main.service.AuthService;
import com.skillbox.socialnetwork.main.service.CommentService;
import com.skillbox.socialnetwork.main.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
public class PostRestController {
    private final PostService postService;
    private final CommentService commentService;
    private final AuthService authService;

    @Autowired
    public PostRestController(PostService postService, CommentService commentService, AuthService authService) {
        this.postService = postService;
        this.commentService = commentService;
        this.authService = authService;
    }

    @GetMapping("/api/v1/post")
    public ResponseEntity<?> getPosts(
            @AuthenticationPrincipal JwtUser user,
            @RequestParam(defaultValue = "") String text,
            @RequestParam(name = "date_from", required = false, defaultValue = "946684800000") Long dateFrom,       //1 января 2000 года, если не указано иное
            @RequestParam(name = "date_to", required = false, defaultValue = "4102444800000") Long dateTo,          //1 января 2100 года, если не указано иное
            @RequestParam(name = "author", required = false, defaultValue = "") String author,
            @RequestParam(name = "tags", required = false, defaultValue = "") String tags,
            @RequestParam(required = false, defaultValue = "0") Integer offset,
            @RequestParam(required = false, defaultValue = "20") Integer itemPerPage
    ) {
        return ResponseEntity.ok(postService.searchPosts(text, dateFrom, dateTo, author, tags, offset, itemPerPage, user.getId()));
    }

    @GetMapping("/api/v1/post/{id}")
    public ResponseEntity<?> getPost(@AuthenticationPrincipal JwtUser user,
                                     @PathVariable int id) {
        return ResponseEntity.ok(postService.getPost(id, user.getId()));
    }

    @PutMapping("api/v1/post/{id}")
    public ResponseEntity<?> updatePost(
            @AuthenticationPrincipal JwtUser user,
            @PathVariable int id,
            @RequestParam(name = "publish_date", required = false) Long publishDate,
            @RequestBody UpdatePostRequestDto request
    ) {
        return ResponseEntity.ok(postService.editPost(id, publishDate, request, user.getId()));
    }

    @DeleteMapping("api/v1/post/{id}")
    public ResponseEntity<?> deletePost(@PathVariable int id) {
        return ResponseEntity.ok(postService.deletePost(id));
    }

    @PostMapping("api/v1/post/{id}/comments")
    public ResponseEntity<?> addComment(
            @AuthenticationPrincipal JwtUser user,
            @PathVariable int id,
            @RequestBody CommentRequest request) {
            Dto result = commentService.addComment(id, request, user.getId());
            return result != null ? ResponseEntity.status(HttpStatus.OK)
                    .body(ResponseFactory.getBaseResponse(result))
                    : new ResponseEntity("Bad request", HttpStatus.BAD_REQUEST);
    }

    @GetMapping("api/v1/post/{id}/comments")
    public ResponseEntity<?> getComments(
            @PathVariable int id,
            @RequestHeader(name = "Authorization") String token) {
        Response result = commentService.getComments(id, authService.getAuthorizedUser(token));
        return result != null ? ResponseEntity.status(HttpStatus.OK)
                .body(result)
                : new ResponseEntity("Bad request", HttpStatus.BAD_REQUEST);
    }

    @PutMapping("api/v1/post/{id}/comments/{comment_id}")
    public ResponseEntity<?> putComment(
            @PathVariable Integer id,
            @PathVariable(name = "comment_id") Integer commentId,
            @RequestBody CommentRequest request) {
            Dto result = commentService.updateComment(commentId, request);
            return result != null ? ResponseEntity.status(HttpStatus.OK)
                    .body(ResponseFactory.getBaseResponse(result))
                    : new ResponseEntity("Bad request", HttpStatus.BAD_REQUEST);
    }

    @DeleteMapping("api/v1/post/{id}/comments/{comment_id}")
    public ResponseEntity<?> delComment(
            @PathVariable Integer id,
            @PathVariable(name = "comment_id") Integer commentId) {
            Dto result = commentService.deleteComment(commentId);
            return result != null ? ResponseEntity.status(HttpStatus.OK)
                    .body(ResponseFactory.getBaseResponse(result))
                    : new ResponseEntity("Bad request", HttpStatus.BAD_REQUEST);

    }
}

