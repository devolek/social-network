package com.skillbox.socialnetwork.main.controller;

import com.skillbox.socialnetwork.main.dto.friends.response.FriendsResponseFactory;
import com.skillbox.socialnetwork.main.dto.person.response.PersonResponseFactory;
import com.skillbox.socialnetwork.main.model.Person;
import com.skillbox.socialnetwork.main.security.jwt.JwtUser;
import com.skillbox.socialnetwork.main.service.AuthService;
import com.skillbox.socialnetwork.main.service.FriendsService;
import com.skillbox.socialnetwork.main.service.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1")
public class FriendsController {
    private final FriendsService friendsService;
    private final PersonService personService;
    private final AuthService authService;

    @Autowired
    public FriendsController(FriendsService friendsService, PersonService personService, AuthService authService) {
        this.friendsService = friendsService;
        this.personService = personService;
        this.authService = authService;
    }

    @GetMapping("/friends")
    public ResponseEntity<?> getFriends(
            @AuthenticationPrincipal JwtUser user,
            @RequestParam(required = false, defaultValue = "") String name,
            @RequestParam(required = false, defaultValue = "0") Integer offset,
            @RequestParam(required = false, defaultValue = "20") Integer itemPerPage
    ) {
        List<Person> friends = friendsService
                .getFriends(personService.findById(user.getId()), name);
        return ResponseEntity.ok().body(PersonResponseFactory.getPersons(friends, offset, itemPerPage));
    }

    @GetMapping("/friends/request")
    public ResponseEntity<?> getFriendRequest(
            @AuthenticationPrincipal JwtUser user,
            @RequestParam(required = false, defaultValue = "") String name,
            @RequestParam(required = false, defaultValue = "0") Integer offset,
            @RequestParam(required = false, defaultValue = "20") Integer itemPerPage
    ) {
        List<Person> request = friendsService
                .getFriendRequest(personService.findById(user.getId()), name);
        return ResponseEntity.ok().body(PersonResponseFactory.getPersons(request, offset, itemPerPage));
    }

    @GetMapping("/friends/recommendations")
    public ResponseEntity<?> gerRecommendations(
            @AuthenticationPrincipal JwtUser user,
            @RequestParam(required = false, defaultValue = "0") Integer offset,
            @RequestParam(required = false, defaultValue = "20") Integer itemPerPage
    ) {
        return ResponseEntity.ok().body(PersonResponseFactory.getPersons(
                friendsService.getRecommendations(personService.findById(user.getId())),
                offset,
                itemPerPage));
    }

    @PostMapping("/friends/{id}")
    public ResponseEntity<?> addFriend(
            @AuthenticationPrincipal JwtUser user,
            @PathVariable Integer id) {
        String message = friendsService.addFriend(personService.findById(user.getId()), personService.findById(id));
        if (message.equals("ok")) {
            return ResponseEntity.ok().body(FriendsResponseFactory.getMessage(message));
        } else {
            return ResponseEntity.badRequest().body(FriendsResponseFactory.getErrorMessage());
        }
    }


    @DeleteMapping("/friends/{id}")
    public ResponseEntity<?> deleteFriend(@AuthenticationPrincipal JwtUser user, @PathVariable Integer id) {
        return ResponseEntity.ok().body(friendsService.deleteFriend(
                personService.findById(user.getId()),
                id
        ));
    }

    @PostMapping("is/friends")
    public ResponseEntity<?> isFriend(
            @AuthenticationPrincipal JwtUser user,
            @RequestParam List<Integer> idList
    ) {
        return ResponseEntity.ok().body(FriendsResponseFactory
                .getIsFriendDtoList(friendsService.isFriend(personService.findById(user.getId()),
                        idList.stream().map(personService::findById).collect(Collectors.toList()))));

    }
}
