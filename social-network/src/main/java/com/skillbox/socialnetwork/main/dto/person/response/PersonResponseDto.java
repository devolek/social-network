package com.skillbox.socialnetwork.main.dto.person.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.skillbox.socialnetwork.main.dto.universal.Dto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PersonResponseDto implements Dto {
    protected int id;
    @JsonProperty("first_name")
    protected String firstName;
    @JsonProperty("last_name")
    protected String lastName;
    @JsonProperty("reg_date")
    protected Long regDate;
    @JsonProperty("birth_date")
    protected Long birthDate;
    protected String email;
    protected String phone;
    protected String photo;
    protected String about;
    protected String city;
    protected String country;
    @JsonProperty("is_blocked")
    protected Boolean isBlocked;
    @JsonProperty("are_you_blocked")
    protected Boolean areYouBlocked;
    @JsonProperty("last_online_time")
    protected Long lastOnlineTime;
    @JsonProperty("messages_permission")
    protected String messagesPermission;
    @JsonProperty("is_friend")
    protected Boolean isFriend;
    @JsonProperty("is_me")
    private Boolean isMe;
}
