package com.skillbox.socialnetwork.main.service;

import com.skillbox.socialnetwork.main.dto.notifications.request.NotificationSettingRequestDto;
import com.skillbox.socialnetwork.main.dto.universal.BaseResponse;
import com.skillbox.socialnetwork.main.dto.universal.BaseResponseList;
import com.skillbox.socialnetwork.main.model.Person;
import com.skillbox.socialnetwork.main.model.enumerated.NotificationCode;

public interface NotificationService {
    BaseResponseList getNotificationSettings(int userId);

    BaseResponse changeNotificationSetting(int userId, NotificationSettingRequestDto settingDto);

    BaseResponseList getUserNotifications(int userId, int offset, int limit);

    BaseResponse markNotificationsAsRead(int userId, Integer notificationId, Boolean all);

    void addNotification(Person src, Person dst, NotificationCode code, String message);

}
