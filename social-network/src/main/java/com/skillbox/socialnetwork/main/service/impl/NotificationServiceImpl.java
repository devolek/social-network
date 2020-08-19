package com.skillbox.socialnetwork.main.service.impl;


import com.skillbox.socialnetwork.main.aspect.MethodLogWithTime;
import com.skillbox.socialnetwork.main.dto.notifications.request.NotificationSettingRequestDto;
import com.skillbox.socialnetwork.main.dto.notifications.response.NotificationResponseFactory;
import com.skillbox.socialnetwork.main.dto.notifications.response.NotificationSettingResponseDto;
import com.skillbox.socialnetwork.main.dto.universal.BaseResponse;
import com.skillbox.socialnetwork.main.dto.universal.BaseResponseList;
import com.skillbox.socialnetwork.main.dto.universal.ResponseFactory;
import com.skillbox.socialnetwork.main.model.Friendship;
import com.skillbox.socialnetwork.main.model.Notification;
import com.skillbox.socialnetwork.main.model.NotificationSettings;
import com.skillbox.socialnetwork.main.model.Person;
import com.skillbox.socialnetwork.main.model.enumerated.NotificationCode;
import com.skillbox.socialnetwork.main.model.enumerated.ReadStatus;
import com.skillbox.socialnetwork.main.repository.FriendshipRepository;
import com.skillbox.socialnetwork.main.repository.NotificationRepository;
import com.skillbox.socialnetwork.main.repository.NotificationSettingsRepository;
import com.skillbox.socialnetwork.main.repository.PersonRepository;
import com.skillbox.socialnetwork.main.service.NotificationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static com.skillbox.socialnetwork.main.util.LastOnlineTimeAdjuster.refreshLastOnlineTime;

@Service
public class NotificationServiceImpl implements NotificationService {

    private final long ONE_DAY_MILLIS = 86400000;
    private final PersonRepository personRepository;
    private final NotificationRepository notificationRepository;
    private final NotificationSettingsRepository notificationSettingsRepository;
    private final FriendshipRepository friendshipRepository;

    @Autowired
    public NotificationServiceImpl(PersonRepository personRepository, NotificationRepository notificationRepository, NotificationSettingsRepository notificationSettingsRepository, FriendshipRepository friendshipRepository)
    {
        this.personRepository = personRepository;
        this.notificationRepository = notificationRepository;
        this.notificationSettingsRepository = notificationSettingsRepository;
        this.friendshipRepository = friendshipRepository;
    }

    //По умолчанию данных в БД по поводу настроек оповещений нет, поэтому используем дефолтные значения false

    @Override
    @MethodLogWithTime(userAuth = true, fullMessage = "Notification settings loaded")
    public BaseResponseList getNotificationSettings(int userId)
    {
        List<NotificationSettingResponseDto> settingsList = new ArrayList<>();
        NotificationSettings personSettings = notificationSettingsRepository.findByPersonId(userId);
        if (personSettings != null)
        {
            settingsList.add(new NotificationSettingResponseDto(NotificationCode.POST, personSettings
                    .isPostNotification()));
            settingsList.add(new NotificationSettingResponseDto(NotificationCode.POST_COMMENT, personSettings
                    .isPostCommentNotification()));
            settingsList.add(new NotificationSettingResponseDto(NotificationCode.COMMENT_COMMENT, personSettings
                    .isCommentCommentNotification()));
            settingsList.add(new NotificationSettingResponseDto(NotificationCode.FRIEND_REQUEST, personSettings
                    .isFriendRequestNotification()));
            settingsList.add(new NotificationSettingResponseDto(NotificationCode.FRIEND_BIRTHDAY, personSettings
                    .isFriendBirthdayNotification()));
            settingsList.add(new NotificationSettingResponseDto(NotificationCode.MESSAGE, personSettings
                    .isMessageNotification()));
        } else
        {
            settingsList.add(new NotificationSettingResponseDto(NotificationCode.POST, false));
            settingsList.add(new NotificationSettingResponseDto(NotificationCode.POST_COMMENT, false));
            settingsList.add(new NotificationSettingResponseDto(NotificationCode.COMMENT_COMMENT, false));
            settingsList.add(new NotificationSettingResponseDto(NotificationCode.FRIEND_REQUEST, false));
            settingsList.add(new NotificationSettingResponseDto(NotificationCode.FRIEND_BIRTHDAY, false));
            settingsList.add(new NotificationSettingResponseDto(NotificationCode.MESSAGE, false));
        }
        return new BaseResponseList(settingsList);
    }

    @Override
    @MethodLogWithTime(userAuth = true, fullMessage = "Notification settings edited")
    public BaseResponse changeNotificationSetting(int userId, NotificationSettingRequestDto settingDto)
    {
        refreshLastOnlineTime(personRepository.findPersonById(userId));
        NotificationSettings notificationSettings = notificationSettingsRepository.findByPersonId(userId);
        if (notificationSettings == null)
        {
            notificationSettings = new NotificationSettings();
            notificationSettings.setPerson(personRepository.findPersonById(userId));
        }
        boolean flag = settingDto.getEnable();
        switch (settingDto.getNotificationType().name())
        {
            case "POST":
                notificationSettings.setPostNotification(flag);
                break;
            case "POST_COMMENT":
                notificationSettings.setPostCommentNotification(flag);
                break;
            case "COMMENT_COMMENT":
                notificationSettings.setCommentCommentNotification(flag);
                break;
            case "FRIEND_REQUEST":
                notificationSettings.setFriendRequestNotification(flag);
                break;
            case "FRIEND_BIRTHDAY":
                notificationSettings.setFriendBirthdayNotification(flag);
                break;
            case "MESSAGE":
                notificationSettings.setMessageNotification(flag);
                break;
        }
        notificationSettingsRepository.save(notificationSettings);
        return ResponseFactory.responseOk();
    }

    @Override
    public BaseResponseList getUserNotifications(int userId, int offset, int limit)
    {
        Person user = personRepository.findPersonById(userId);
        refreshLastOnlineTime(user);
        getBirthdays(user);
        List<Notification> notifications = user.getNotifications();
        notifications.sort(Comparator.comparing(Notification::getSentTime).reversed());
        return NotificationResponseFactory.getNotifications(notifications, offset, limit);
    }

    @Override
    @MethodLogWithTime(userAuth = true, fullMessage = "Notification was marked as read")
    public BaseResponse markNotificationsAsRead(int userId, Integer notificationId, Boolean all)
    {
        Person person = personRepository.findPersonById(userId);

        //удаляем прочитанные уведомления старше одного дня
        Date now = new Date();
        Date oneDayBefore = new Date(now.getTime() - ONE_DAY_MILLIS);
        List<Notification> notificationsToDelete = person.getNotifications()
                .stream()
                .filter(n -> n.getReadStatus().equals(ReadStatus.READ))
                .filter(n -> n.getSentTime().before(oneDayBefore))
                .collect(Collectors.toList());
        for (Notification n : notificationsToDelete)
        {
            notificationRepository.deleteNotification(n);
        }

        //помечаем уведомления прочитанными
        List<Notification> notifications = person.getNotifications()
                .stream()
                .filter(n -> n.getReadStatus().equals(ReadStatus.SENT))
                .peek(n -> n.setReadStatus(ReadStatus.READ))
                .collect(Collectors.toList());
        notificationRepository.saveAll(notifications);
        return ResponseFactory.responseOk();
    }

    @Override
    @MethodLogWithTime(fullMessage = "Notification created")
    public void addNotification(Person src, Person dst, NotificationCode code, String message)
    {
        NotificationSettings settings = notificationSettingsRepository.findByPersonId(dst.getId());
        boolean settingEnabled = false;
        switch (code.name()) {
            case "POST": settingEnabled = settings.isPostNotification(); break;
            case "POST_COMMENT": settingEnabled = settings.isPostCommentNotification(); break;
            case "COMMENT_COMMENT": settingEnabled = settings.isCommentCommentNotification(); break;
            case "FRIEND_REQUEST": settingEnabled = settings.isFriendRequestNotification(); break;
            case "FRIEND_BIRTHDAY": settingEnabled = settings.isFriendBirthdayNotification(); break;
            case "MESSAGE": settingEnabled = settings.isMessageNotification(); break;
            case "LIKE":
            case "ACTIVATION":
                settingEnabled = true; break;
        }

        if (settingEnabled && !src.getId().equals(dst.getId())) {
            Notification notification = Notification.builder()
                    .person(dst)
                    .entityAuthor(src)
                    .type(code)
                    .info(message)
                    .sentTime(new Date())
                    .readStatus(ReadStatus.SENT)
                    .build();
            notificationRepository.save(notification);
        }
    }

    public void getBirthdays(Person user)
    {
        List<Friendship> friendships = friendshipRepository.findAllFriends(user);
        friendships.stream().map(Friendship::getDstPerson).filter(person -> person.getBirthDate()!=null).filter(person -> {
            LocalDate birthday = person.getBirthDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            LocalDate today = LocalDate.now();
            return birthday.getDayOfMonth() == today.getDayOfMonth() && birthday.getMonth() == today.getMonth();
        }).forEach(person -> addNotification(person, user, NotificationCode.FRIEND_BIRTHDAY, "У пользователя "+person.getFirstName()+" "+person.getLastName()+" сегодня день рождения."));
    }
}
