package com.skillbox.socialnetwork.main.dto.notifications.response;

import com.skillbox.socialnetwork.main.dto.universal.BaseResponseList;
import com.skillbox.socialnetwork.main.dto.universal.ResponseFactory;
import com.skillbox.socialnetwork.main.model.Notification;

import java.util.List;
import java.util.stream.Collectors;

public class NotificationResponseFactory {

    public static BaseResponseList getNotifications(List<Notification> notificationList, int offset, int limit) {
        return ResponseFactory.getBaseResponseListWithLimit(
                notificationList.stream().map(NotificationResponseFactory::notificationToDto).collect(Collectors.toList()),
                offset,
                limit);
    }

    private static NotificationResponseDto notificationToDto(Notification notification) {
        return new NotificationResponseDto(
                notification.getId(),
                notification.getSentTime().getTime(),
                notification.getType(),
                notification.getEntityAuthor(),
                notification.getInfo(),
                notification.getReadStatus()
        );
    }
}
