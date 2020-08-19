package com.skillbox.socialnetwork.main.service.impl;

import com.skillbox.socialnetwork.main.aspect.MethodLogWithTime;
import com.skillbox.socialnetwork.main.exception.not.found.PersonNotFoundException;
import com.skillbox.socialnetwork.main.model.Friendship;
import com.skillbox.socialnetwork.main.model.FriendshipStatus;
import com.skillbox.socialnetwork.main.model.Person;
import com.skillbox.socialnetwork.main.model.enumerated.FriendshipCode;
import com.skillbox.socialnetwork.main.model.enumerated.NotificationCode;
import com.skillbox.socialnetwork.main.repository.FriendshipRepository;
import com.skillbox.socialnetwork.main.repository.FriendshipStatusRepo;
import com.skillbox.socialnetwork.main.service.FriendsService;
import com.skillbox.socialnetwork.main.service.NotificationService;
import com.skillbox.socialnetwork.main.service.PersonService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class FriendsServiceImpl implements FriendsService {
    private final FriendshipRepository friendshipRepository;
    private final FriendshipStatusRepo friendshipStatusRepo;
    private final NotificationService notificationService;
    private final PersonService personService;

    @Autowired
    public FriendsServiceImpl(FriendshipRepository friendshipRepository, FriendshipStatusRepo friendshipStatusRepo, NotificationService notificationService, PersonService personService) {
        this.friendshipRepository = friendshipRepository;
        this.friendshipStatusRepo = friendshipStatusRepo;
        this.notificationService = notificationService;
        this.personService = personService;
    }

    @Override
    @MethodLogWithTime(userAuth = true, fullMessage = "Friend list loaded")
    public List<Person> getFriends(Person person, String name) {
        return friendshipRepository.findAllFriends(person).stream()
                .map(Friendship::getDstPerson)
                .filter(f -> f.getFirstName().contains(name)
                        || f.getLastName().contains(name))
                .collect(Collectors.toList());
    }

    @Override
    @MethodLogWithTime(userAuth = true, fullMessage = "Friend request loaded")
    public List<Person> getFriendRequest(Person person, String name) {
        return friendshipRepository.findAllRequests(person).stream()
                .map(Friendship::getSrcPerson)
                .filter(f -> f.getFirstName().contains(name)
                        || f.getLastName().contains(name))
                .collect(Collectors.toList());
    }

    @Override
    @MethodLogWithTime(userAuth = true, fullMessage = "Friend recommendations loaded")
    public List<Person> getRecommendations(Person person) {
        List<Person> myFriends = getFriends(person, "");
        List<Person> peopleFromCity = person.getCity().equals("")
                ? new ArrayList<>()
                : personService.findByCity(person.getCity());

        Set<Person> recommendedFriends = new HashSet<>();
        myFriends.stream().map(person1 -> getFriends(person1, "")).forEach(l -> recommendedFriends.addAll(l));

        recommendedFriends.addAll(peopleFromCity);

        if (recommendedFriends.isEmpty())
            recommendedFriends.addAll(personService.getAll());

        if (recommendedFriends.contains(person)) recommendedFriends.remove(person);

        for (Person friend : myFriends) {
            if (recommendedFriends.contains(friend)) recommendedFriends.remove(friend);
        }

        List<Person> result = new ArrayList<>();
        result.addAll(recommendedFriends);
        return result;
    }

    @Override
    @MethodLogWithTime(userAuth = true, fullMessage = "New friend added")
    public String addFriend(Person srcPerson, Person dstPerson) {
        /*
        Добавление в друзья: Текущий пользователь srcPerson, целевой пользователь dstPerson.
        Принятие запроса в друзья: Тот, кто подтверждает или отклонияет запрос в друзья srcPerson,
        тот, кто отправлял этот запрос - dstPerson
         */
        Friendship friendshipSrc = friendshipRepository.findNonBLockedRelation(srcPerson.getId(), dstPerson.getId());
        Friendship friendshipDst = friendshipRepository.findNonBLockedRelation(dstPerson.getId(), srcPerson.getId());
        if (friendshipDst != null && (friendshipDst.getStatus().getCode().equals(FriendshipCode.REQUEST)
                || friendshipDst.getStatus().getCode().equals(FriendshipCode.SUBSCRIBED))) {
            friendshipDst.getStatus().setCode(FriendshipCode.FRIEND);
            friendshipDst.getStatus().setTime(new Date());
            if (friendshipSrc == null) {
                friendshipSrc = new Friendship(new FriendshipStatus(), srcPerson, dstPerson);
            }
            friendshipSrc.getStatus().setCode(FriendshipCode.FRIEND);
            friendshipSrc.getStatus().setTime(new Date());
            friendshipStatusRepo.save(friendshipDst.getStatus());
            friendshipStatusRepo.save(friendshipSrc.getStatus());
            friendshipRepository.save(friendshipSrc);
            friendshipRepository.save(friendshipDst);

            //Notification
            notificationService.addNotification(srcPerson, dstPerson, NotificationCode.FRIEND_REQUEST,
                    srcPerson.getFirstName() + " " + srcPerson.getLastName() + " добавил Вас в друзья.");
        } else if (friendshipSrc != null) {
            if (friendshipSrc.getStatus().getCode().equals(FriendshipCode.REQUEST)) {
                return "";
            }
            if (friendshipSrc.getStatus().getCode().equals(FriendshipCode.DECLINED)) {
                return "";
            }
            if (friendshipSrc.getStatus().getCode().equals(FriendshipCode.BLOCKED)) {
                return "";
            }
        } else {
            FriendshipStatus status = new FriendshipStatus(new Date(), FriendshipCode.REQUEST);
            friendshipSrc = new Friendship(status, srcPerson, dstPerson);
            friendshipStatusRepo.save(status);
            friendshipRepository.save(friendshipSrc);

            notificationService.addNotification(srcPerson, dstPerson, NotificationCode.FRIEND_REQUEST,
                    srcPerson.getFirstName() + " " + srcPerson.getLastName() + " добавил Вас в друзья.");
        }
        return "ok";
    }


    @Override
    @MethodLogWithTime(fullMessage = "Friend check")
    public Map<Person, String> isFriend(Person srcPerson, List<Person> dstPersonList) {
        Map<Person, String> isFriendMap = new HashMap<>();
        for (Person dstPerson : dstPersonList) {
            Friendship friendshipSrc = friendshipRepository
                    .findRelation(srcPerson, dstPerson, FriendshipCode.FRIEND);
            if (friendshipSrc != null) {
                isFriendMap.put(dstPerson, friendshipSrc.getStatus().getCode().toString());
            }
        }
        return isFriendMap;
    }

    @Override
    @MethodLogWithTime(userAuth = true, fullMessage = "Person removed from friends list")
    public String deleteFriend(Person owner, int deletedFriendId) {
        Friendship relationsSrc = friendshipRepository.findNonBLockedRelation(owner.getId(), deletedFriendId);
        Friendship relationsDst = friendshipRepository.findNonBLockedRelation(deletedFriendId, owner.getId());
        if (relationsSrc != null
                && relationsSrc.getStatus().getCode().equals(FriendshipCode.FRIEND)) {
            relationsSrc.getStatus().setCode(FriendshipCode.DECLINED);
            relationsSrc.getStatus().setTime(new Date());
            friendshipStatusRepo.save(relationsSrc.getStatus());
            try {
                personService.findById(deletedFriendId);
            }
            catch (PersonNotFoundException e) {
                friendshipRepository.delete(relationsSrc);
                friendshipRepository.delete(relationsDst);
                friendshipStatusRepo.delete(relationsSrc.getStatus());
                friendshipStatusRepo.delete(relationsDst.getStatus());
                return "ok";
            }
            relationsDst.getStatus().setCode(FriendshipCode.SUBSCRIBED);
            relationsDst.getStatus().setTime(new Date());
            friendshipStatusRepo.save(relationsDst.getStatus());
        }
        return "ok";
    }
}
