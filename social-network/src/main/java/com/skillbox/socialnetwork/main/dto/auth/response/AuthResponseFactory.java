package com.skillbox.socialnetwork.main.dto.auth.response;

import com.skillbox.socialnetwork.main.dto.universal.BaseResponse;
import com.skillbox.socialnetwork.main.dto.universal.ResponseFactory;
import com.skillbox.socialnetwork.main.model.Person;

public class AuthResponseFactory {

    public static BaseResponse getAuthResponse(Person person, String token) {
        return ResponseFactory.getBaseResponse(
                new AuthPersonResponseDto(
                        person.getId(),
                        person.getFirstName(),
                        person.getLastName(),

                        person.getRegDate().getTime(),
                        person.getBirthDate() != null ?
                                person.getBirthDate().getTime() : null,
                        person.getEmail(),
                        person.getPhone(),
                        person.getPhoto(),
                        person.getAbout(),
                        person.getCity(),
                        person.getCountry(),
                        person.getIsBlocked(),
                        person.getLastOnlineTime().getTime(),
                        person.getMessagesPermission().toString(),
                        token
                )
        );
    }


}
