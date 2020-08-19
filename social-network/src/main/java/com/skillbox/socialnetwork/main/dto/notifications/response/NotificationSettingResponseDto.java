package com.skillbox.socialnetwork.main.dto.notifications.response;

import com.skillbox.socialnetwork.main.dto.universal.Dto;
import com.skillbox.socialnetwork.main.model.enumerated.NotificationCode;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NotificationSettingResponseDto implements Dto {

    private NotificationCode type;

    private Boolean enable;
}
