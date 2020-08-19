package com.skillbox.socialnetwork.main.dto.person.response;


import com.skillbox.socialnetwork.main.dto.universal.BaseResponse;
import com.skillbox.socialnetwork.main.dto.universal.BaseResponseList;
import com.skillbox.socialnetwork.main.dto.universal.ResponseFactory;
import com.skillbox.socialnetwork.main.model.Person;

import java.util.List;
import java.util.stream.Collectors;

public class PersonResponseFactory {
    public static BaseResponse getPerson(Person person) {
        return ResponseFactory.getBaseResponse(getPersonDto(person));
    }

    public static BaseResponseList getPersons(List<Person> people, int offset, int limit) {
        return ResponseFactory.getBaseResponseListWithLimit(people.stream()
                        .map(PersonResponseFactory::getPersonDto)
                        .collect(Collectors.toList()),
                offset, limit);
    }

    public static PersonResponseDto getPersonDto(Person person) {
        return new PersonResponseDto(
                person.getId(),
                person.getFirstName(),
                person.getLastName(),
                person.getRegDate().getTime(),
                person.getBirthDate() != null ? person.getBirthDate().getTime() : null,
                person.getEmail(),
                person.getPhone(),
                person.getPhoto(),
                person.getAbout(),
                person.getCity(),
                person.getCountry(),
                person.getIsBlocked(),
                person.getAreYouBlocked(),
                person.getLastOnlineTime() != null ? person.getLastOnlineTime().getTime() : null,
                person.getMessagesPermission().toString(),
                false,
                false
        );
    }

    public static PersonResponseDto getPersonWithFriendshipDto(Person person, boolean isFriend) {
        return new PersonResponseDto(
                person.getId(),
                person.getFirstName(),
                person.getLastName(),
                person.getRegDate().getTime(),
                person.getBirthDate() != null ? person.getBirthDate().getTime() : null,
                person.getEmail(),
                person.getPhone(),
                person.getPhoto(),
                person.getAbout(),
                person.getCity(),
                person.getCountry(),
                person.getIsBlocked(),
                person.getAreYouBlocked(),
                person.getLastOnlineTime() != null ? person.getLastOnlineTime().getTime() : null,
                person.getMessagesPermission().toString()
                , isFriend,
                false
        );
    }

    public static PersonResponseDto getPersonById(Person person, boolean isFriend, boolean isMyProfile) {
        return new PersonResponseDto(
                person.getId(),
                person.getFirstName(),
                person.getLastName(),
                person.getRegDate().getTime(),
                person.getBirthDate() != null ? person.getBirthDate().getTime() : null,
                person.getEmail(),
                person.getPhone(),
                person.getPhoto(),
                person.getAbout(),
                person.getCity(),
                person.getCountry(),
                person.getIsBlocked(),
                person.getAreYouBlocked(),
                person.getLastOnlineTime() != null ? person.getLastOnlineTime().getTime() : null,
                person.getMessagesPermission().toString(),
                isFriend,
                isMyProfile
        );
    }

}
