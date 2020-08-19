package com.skillbox.socialnetwork.main.dto.auth.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequestDto {
    private String email;
    @JsonProperty("passwd1")
    private String password1;
    @JsonProperty("passwd2")
    private String password2;
    private String firstName;
    private String lastName;
    private String code;
}
