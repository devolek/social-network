package com.skillbox.socialnetwork.main.dto.notifications.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.skillbox.socialnetwork.main.dto.universal.Dto;
import com.skillbox.socialnetwork.main.model.Person;
import com.skillbox.socialnetwork.main.model.enumerated.NotificationCode;
import com.skillbox.socialnetwork.main.model.enumerated.ReadStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NotificationResponseDto implements Dto {

    private int id;

    @JsonProperty("sent_time")
    private long sentTime;

    @JsonProperty("event_type")
    private NotificationCode type;

    @JsonProperty("entity_author")
    private Person entityAuthor;

    private String info;

    @JsonProperty("read_status")
    private ReadStatus readStatus;
}
