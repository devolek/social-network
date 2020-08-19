package com.skillbox.socialnetwork.main.dto.profile.request;

import com.skillbox.socialnetwork.main.dto.universal.Dto;
import lombok.Data;

@Data
public class ContactSupportRequestDto implements Dto {
    private String email;
    private String message;
    private String name;
}
