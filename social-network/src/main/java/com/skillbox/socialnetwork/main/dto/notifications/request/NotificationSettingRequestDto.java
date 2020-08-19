package com.skillbox.socialnetwork.main.dto.notifications.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.skillbox.socialnetwork.main.dto.universal.Dto;
import com.skillbox.socialnetwork.main.model.enumerated.NotificationCode;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NotificationSettingRequestDto implements Dto {

    @JsonProperty("notification_type")
    private NotificationCode notificationType;

    private Boolean enable;
}
