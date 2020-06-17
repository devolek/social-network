package com.skillbox.socialnetwork.main.controller;

import com.skillbox.socialnetwork.main.dto.like.request.LikeRequest;
import com.skillbox.socialnetwork.main.dto.like.response.IsLikeDto;
import com.skillbox.socialnetwork.main.dto.like.response.LikeResponseFactory;
import com.skillbox.socialnetwork.main.dto.universal.BaseResponse;
import com.skillbox.socialnetwork.main.dto.universal.ResponseFactory;
import com.skillbox.socialnetwork.main.model.PostLike;
import com.skillbox.socialnetwork.main.service.AuthService;
import com.skillbox.socialnetwork.main.service.LikeService;
import com.skillbox.socialnetwork.main.service.PersonService;
import com.skillbox.socialnetwork.main.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@RestController
public class LikeRestController {

    private final AuthService authService;
    private final PostService postService;
    private final LikeService likeService;
    private final PersonService personService;

    @Autowired
    public LikeRestController(AuthService authService, PostService postService, LikeService likeService, PersonService personService) {
        this.authService = authService;
        this.postService = postService;
        this.likeService = likeService;
        this.personService = personService;
    }

    @PutMapping("/api/v1/likes")
    public ResponseEntity putLike(@RequestHeader(name = "Authorization") String token,
                                  @RequestBody LikeRequest request) {
        if (authService.isAuthorized(token)) {
            PostLike like = new PostLike();
            like.setPerson(authService.getAuthorizedUser(token));
            like.setPost(postService.findById(request.getId()));
            like.setTime(new Date());
            likeService.save(like);
            return ResponseEntity.status(HttpStatus.OK).body(LikeResponseFactory.getLikeDto(postService.findById(request.getId())));
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ResponseFactory.getErrorResponse("invalid request", "unauthorized"));
    }

    @GetMapping("/api/v1/likes") //Пока что реализовал получение лайков только у постов
    public ResponseEntity getLikes(@RequestHeader(name = "Authorization") String token,
                                   @RequestParam(name = "item_id") Integer id,
                                   @RequestParam String type
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(LikeResponseFactory.getLikeDto(postService.findById(id)));
    }

    @GetMapping("/api/v1/liked") //Пока что реализовал получение лайков только у постов
    public BaseResponse isLiked(@RequestHeader(name = "Authorization") String token,
                                @RequestParam(name = "user_id") Integer userId,
                                @RequestParam(name = "item_id") Integer itemId,
                                @RequestParam String type
    ) {
        return new BaseResponse(
                new IsLikeDto(likeService.isLiked(
                        postService.findById(itemId), personService.findById(userId))
                )
        );
    }

    @DeleteMapping("api/v1/likes")
    public ResponseEntity deleteLike(@RequestHeader(name = "Authorization") String token,
                                     @RequestParam(name = "item_id") Integer itemId,
                                     @RequestParam String type) {
        return ResponseEntity.status(HttpStatus.OK).body(likeService.delete(
                itemId,
                authService.getAuthorizedUser(token)
        ));
    }
}