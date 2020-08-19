package com.skillbox.socialnetwork.main.service;

import com.skillbox.socialnetwork.main.model.Person;

import java.util.List;
import java.util.Map;

public interface FriendsService {
    List<Person> getFriends(Person person, String name);

    List<Person> getFriendRequest(Person person, String name);

    String addFriend(Person srcPerson, Person dstPerson);

    Map<Person, String> isFriend(Person srcPerson, List<Person> dstPersonList);

    String deleteFriend(Person owner, int deletedFriendId);

    List<Person> getRecommendations(Person person);
}