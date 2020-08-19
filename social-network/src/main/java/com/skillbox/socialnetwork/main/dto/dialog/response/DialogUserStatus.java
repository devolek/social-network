package com.skillbox.socialnetwork.main.dto.dialog.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.skillbox.socialnetwork.main.dto.universal.Dto;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DialogUserStatus implements Dto {
    private boolean online;
    @JsonProperty("last_online")
    private Long lastOnline;
}
