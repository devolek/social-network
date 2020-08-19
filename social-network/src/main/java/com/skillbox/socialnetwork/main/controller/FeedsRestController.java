package com.skillbox.socialnetwork.main.controller;

import com.skillbox.socialnetwork.main.service.AuthService;
import com.skillbox.socialnetwork.main.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FeedsRestController {

    private final PostService postService;
    private final AuthService authService;

    @Autowired
    public FeedsRestController(PostService postService, AuthService authService) {
        this.postService = postService;
        this.authService = authService;
    }

    @GetMapping("/api/v1/feeds")
    public ResponseEntity getFeeds(
            @RequestHeader(name = "Authorization") String token,
            @RequestParam(name = "query", required = false, defaultValue = "") String query,
            @RequestParam(name = "offset", required = false, defaultValue = "0") Integer offset,
            @RequestParam(name = "itemPerPage", required = false, defaultValue = "20") Integer limit

    ) {
        return ResponseEntity.status(HttpStatus.OK).body(postService.feeds(offset, limit, authService.getAuthorizedUser(token)));
    }
}
