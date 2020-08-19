package com.skillbox.socialnetwork.main.service.impl;

import com.skillbox.socialnetwork.main.aspect.MethodLogWithTime;
import com.skillbox.socialnetwork.main.dto.dialog.DialogAddRequest;
import com.skillbox.socialnetwork.main.dto.dialog.DialogFactory;
import com.skillbox.socialnetwork.main.dto.dialog.LongpollHistoryRequest;
import com.skillbox.socialnetwork.main.dto.dialog.response.*;
import com.skillbox.socialnetwork.main.dto.universal.BaseResponse;
import com.skillbox.socialnetwork.main.dto.universal.BaseResponseList;
import com.skillbox.socialnetwork.main.dto.universal.ResponseFactory;
import com.skillbox.socialnetwork.main.model.Dialog;
import com.skillbox.socialnetwork.main.model.Message;
import com.skillbox.socialnetwork.main.model.Person;
import com.skillbox.socialnetwork.main.model.enumerated.NotificationCode;
import com.skillbox.socialnetwork.main.model.enumerated.ReadStatus;
import com.skillbox.socialnetwork.main.repository.DialogRepository;
import com.skillbox.socialnetwork.main.repository.MessageRepository;
import com.skillbox.socialnetwork.main.service.DialogService;
import com.skillbox.socialnetwork.main.service.NotificationService;
import com.skillbox.socialnetwork.main.service.PersonService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

@Service
public class DialogServiceImpl implements DialogService {

    private final MessageRepository messageRepository;
    private final PersonService personService;
    private final DialogRepository dialogRepository;
    private final NotificationService notificationService;

    @Autowired
    public DialogServiceImpl(MessageRepository messageRepository, PersonService personService, DialogRepository dialogRepository, NotificationService notificationService)
    {
        this.messageRepository = messageRepository;
        this.personService = personService;
        this.dialogRepository = dialogRepository;
        this.notificationService = notificationService;
    }

    @Override
    public BaseResponseList getDialogs(String query, int offset, int limit, Person person)
    {
        List<Dialog> dialogList = dialogRepository.getDialogs(person, query);
        dialogList.sort(Comparator.comparing(Dialog::getMessages, (d1, d2) -> {
            Date date1 = d1.stream()
                    .max(Comparator.comparing(Message::getTime))
                    .get().getTime();
            Date date2 = d2.stream()
                    .max(Comparator.comparing(Message::getTime))
                    .get().getTime();
            return date2.compareTo(date1);
        }));
        return DialogFactory.getDialogs(dialogList, person, offset, limit);
    }

    @Override
    @MethodLogWithTime(userAuth = true, fullMessage = "New dialog created")
    public BaseResponse addDialog(DialogAddRequest request, Person currentUser)
    {
        AtomicInteger dialogId = new AtomicInteger();

        boolean dialogAlreadyExists = currentUser.getDialogs().stream()
                .anyMatch(dialog ->{
                    if(dialog.getPeople().contains(personService.findById(request.getUserIds().get(0))) && dialog.getPeople().contains(currentUser)){
                        dialogId.set(dialog.getId());
                        return true;
                    }
                    return false;
                });
        if (dialogAlreadyExists)
        {
            return new BaseResponse(new IdDto(dialogId.get()));
        } else
        {//if not exists
            Dialog d = new Dialog();
            d.setMessages(new ArrayList<>());
            Dialog dialog = dialogRepository.save(d);
            List<Person> result = request
                    .getUserIds()
                    .stream()
                    .map(personService::findById)
                    .collect(Collectors.toList());
            result.add(currentUser);
            dialog.setPeople(result);
            Dialog saved = dialogRepository.save(dialog);
            System.out.println("People"+saved.getPeople());
            //dialog initializer
            Message message = new Message();
            message.setMessageText("Пользователь " + currentUser.getFirstName() + " " + currentUser
                    .getLastName() + " начал чат.");
            message.setAuthor(currentUser);
            message.setRecipient(personService.findById(request.getUserIds().get(0)));
            message.setReadStatus(ReadStatus.SENT);
            message.setTime(new Date());
            message.setDialog(saved);
            messageRepository.save(message);
            saved.getMessages().add(message);
            saved = dialogRepository.save(saved);

            return new BaseResponse(new IdDto(saved.getId()));
        }
    }

    @Override
    public BaseResponse getUnreadMessages(Person person)
    {
        return new BaseResponse(new CountDto(dialogRepository.countUnreadMessages(person)));
    }

    @Override
    @MethodLogWithTime(userAuth = true, fullMessage = "Dialog deleted")
    public BaseResponse deleteDialog(int dialogId)
    {
        dialogRepository.deleteById(dialogId);
        return new BaseResponse(new IdDto(dialogId));
    }

    @Override
    public BaseResponse addUsersToDialog(DialogAddRequest request)
    {
        return addDialog(request, null);
    }

    @Override
    public BaseResponse deleteUsersFromDialog(int dialogId, DialogAddRequest request)
    {
//        Dialog dialog = dialogRepository
//                .findById(dialogId)
//                .orElse(null);
//        request
//                .getUserIds()
//                .stream()
//                .map(personService::findById)
//                .collect(Collectors.toList())
//                .forEach(o -> dialogRepository.deleteUserFromDialog(o, dialog));
        return new BaseResponse(request);
    }

    @Override
    public BaseResponse inviteUserToDialog(int id)
    {//Как линк сгенерировать??
        return null;
    }

    @Override
    public BaseResponse joinDialog(int id, LinkDto linkDto)
    {//аналогично
        return null;
    }

    @Override
    public BaseResponseList getMessagesFromDialog(int id, String query, int offset, int limit, int fromMessageId, Person currentUser)
    {
        Dialog dialog = dialogRepository.findById(id).orElse(null);
        if (dialog != null)
        {
            List<Message> list = messageRepository.getAllByMessageTextContainingAndDialog(query, dialog);
            int lastElementIndex = list.size();
            int firstElementIndex = list.size() - limit;
            if (offset != 0)
            {
                Message fromMessage = messageRepository.findById(fromMessageId).get();
                int indexOfFromMessage = list.indexOf(fromMessage);
                list = list.subList(0, indexOfFromMessage);
                lastElementIndex = list.size();
                firstElementIndex = lastElementIndex - limit;

            }
            if (firstElementIndex >= 0)
            {
                list = list.subList(firstElementIndex, lastElementIndex);
            } else if (lastElementIndex > 0)
            {
                list = list.subList(0, lastElementIndex);
            } else list = new ArrayList<>();
            messageRepository.saveAll(list
                    .stream()
                    .filter(message -> message.getRecipient().equals(currentUser))
                    .peek(message -> message.setReadStatus(ReadStatus.READ))
                    .collect(toList()));
            return DialogFactory.getMessages(list, currentUser, offset, limit, fromMessageId);
        } else
        {
            return null;
        }
    }

    @Override
    @MethodLogWithTime(userAuth = true, fullMessage = "New message added", exceptionMessage = "Dialog cannot be null")
    public BaseResponse addMessage(int dialogId, MessageTextDto request, Person user)
    {
        Dialog dialog = dialogRepository.findById(dialogId).orElse(null);
        if (dialog == null)
        {
            throw new NullPointerException("Dialog cannot be null");
        }
        Message message = new Message();
        Person dstPerson = dialog.getPeople().stream().filter(person -> !person.equals(user))
                .findFirst().get();
        message.setAuthor(user);
        message.setRecipient(dstPerson);
        message.setTime(new Date());
        message.setMessageText(request.getText());
        message.setReadStatus(ReadStatus.SENT);
        message.setDialog(dialog);

        //уведомление о сообщении
        notificationService.addNotification(user, dstPerson, NotificationCode.MESSAGE, message.getMessageText());

        return new BaseResponse(DialogFactory.formatMessage(messageRepository.save(message), user));
    }

    @Override
    @MethodLogWithTime(userAuth = true, fullMessage = "Message deleted")
    public BaseResponse deleteMessage(int dialogId, int messageId)
    {
        messageRepository.deleteById(messageId);
        return new BaseResponse(new MessageIdDto(messageId));
    }

    @Override
    @MethodLogWithTime(userAuth = true, fullMessage = "Message edited")
    public BaseResponse editMessage(int dialogId, int messageId, MessageTextDto messageText, Person user)
    {
        Message message = messageRepository.findById(messageId).orElse(null);
        if (message == null)
        {
            throw new NullPointerException("Message not with id:" + messageId + " found");
        }
        message.setMessageText(messageText.getText());

        return new BaseResponse(DialogFactory.formatMessage(messageRepository.save(message), user));
    }

    @Override
    public BaseResponse recoverMessage(int dialogId, int messageId)
    {
        return null;//Как?
    }

    @Override
    @MethodLogWithTime(userAuth = true, fullMessage = "Message marked as read")
    public BaseResponse markMessageAsRead(int dialogId, int messageId, Person person)
    {
        Message message = messageRepository.findById(messageId).orElse(null);
        if (message == null)
            throw new NullPointerException("Message with id: " + messageId + " not found");
        message.setReadStatus(ReadStatus.READ);
        messageRepository.save(message);
        return ResponseFactory.responseOk();
    }

    @Override
    public BaseResponse getUserActivityStatus(int dialogId, int userId)
    {
        return null;
    }

    @Override
    public BaseResponse changeUserActivityStatus(int dialogId, int userId)
    {
        return null;
    }

    @Override
    public BaseResponse getLongPollCredentials()
    {
        return null;
    }

    @Override
    public BaseResponse getLongPollHistory(LongpollHistoryRequest request)
    {
        return null;
    }
}
