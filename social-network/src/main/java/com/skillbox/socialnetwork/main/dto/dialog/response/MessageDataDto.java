package com.skillbox.socialnetwork.main.dto.dialog.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.skillbox.socialnetwork.main.dto.universal.Dto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MessageDataDto implements Dto {
    @JsonProperty("id")
    private int dialogId;
    @JsonProperty("unread_amount")
    private int unreadCount;
    @JsonProperty("last_message")
    private MessageDto lastMessage;
}
