package com.skillbox.socialnetwork.main.dto.auth.response;

import com.skillbox.socialnetwork.main.dto.person.response.PersonResponseDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class AuthPersonResponseDto extends PersonResponseDto {
    private String token;

    public AuthPersonResponseDto(
            int id,
            String firstName,
            String lastName,
            Long regDate,
            Long birthDate,
            String email,
            String phone,
            String photo,
            String about,
            String city,
            String country,
            Boolean isBlocked,
            Long lastOnlineTime,
            String messagesPermission,
            String token
    ) {
        super(id, firstName, lastName, regDate, birthDate, email,
                phone, photo, about, city, country, isBlocked, false,
                lastOnlineTime, messagesPermission, false, false);
        this.token = token;
    }

}
