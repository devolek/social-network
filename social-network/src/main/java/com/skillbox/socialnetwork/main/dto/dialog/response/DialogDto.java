package com.skillbox.socialnetwork.main.dto.dialog.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.skillbox.socialnetwork.main.dto.universal.Dto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DialogDto implements Dto {
    private int id;
    @JsonProperty("unread_count")
    private int unreadCount;
    @JsonProperty("last_message")
    private Dto lastMessage;
    @JsonProperty("is_frozen")
    private boolean isFrozen;
}
