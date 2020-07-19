package com.skillbox.socialnetwork.main.dto.dialog;

import com.skillbox.socialnetwork.main.dto.dialog.response.DialogDto;
import com.skillbox.socialnetwork.main.dto.dialog.response.MessageDto;
import com.skillbox.socialnetwork.main.dto.universal.BaseResponseList;
import com.skillbox.socialnetwork.main.dto.universal.Dto;
import com.skillbox.socialnetwork.main.model.Dialog;
import com.skillbox.socialnetwork.main.model.Message;
import com.skillbox.socialnetwork.main.model.Person;
import com.skillbox.socialnetwork.main.model.enumerated.ReadStatus;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

public class DialogFactory {

    public static BaseResponseList getDialogs(List<Dialog> dialogs, Person user, int offset, int limit) {
        return new BaseResponseList(
                dialogs.size(),
                offset,
                limit,
                formatDialogs(dialogs, user, limit, offset)
        );
    }

    private static List<Dto> formatDialogs(List<Dialog> dialogs, Person user, int offset, int limit) {
        return dialogs
                .stream()
                .map(dialog -> new DialogDto(dialog.getId(),
                        (int) dialog
                                .getMessages()
                                .stream()
                                .filter(message -> message.getReadStatus() == ReadStatus.SENT)
                                .count(),
                        dialog
                                .getMessages()
                                .size() > 0 ? formatMessage(dialog
                                .getMessages()
                                .stream()
                                .max(Comparator.comparing(Message::getTime))
                                .get(), user) : null))
                .collect(Collectors.toList());
    }

    public static BaseResponseList getMessages(List<Message> messageList, Person user, int offset, int limit) {
        return new BaseResponseList(
                messageList.size(),
                offset,
                limit,
                messageList.size() > 0 ? formatMessages(messageList, user, offset, limit) : null
        );
    }

    private static List<Dto> formatMessages(List<Message> messages, Person user, int offset, int limit) {
        try {
            return getElementsInRange(messages
                            .stream()
                            .map(message -> message != null ? formatMessage(message, user) : null)
                            .collect(toList()),
                    offset, limit);
        } catch (NullPointerException e) {

        }
        return null;
    }

    private static List<Dto> getElementsInRange(List<Dto> list, int offset, int limit) {
        int lastElementIndex = offset + limit;
        int lastPostIndex = list.size();
        if (lastPostIndex >= offset) {//если есть элементы входящие в нужный диапазон
            if (lastElementIndex <= lastPostIndex) {//если все элементы с нужными индексами есть в листе
                return list.subList(offset, lastElementIndex);
            } else {//если не хватает элементов, то в посты записываем остаток, считая от offset
                return list.subList(offset, lastPostIndex);
            }
        } else {
            return new ArrayList<>();
        }
    }

    public static Dto formatMessage(Message message, Person user) {
        return new MessageDto(
                message.getId(),
                message
                        .getTime()
                        .getTime(),
                message
                        .getAuthor(),
                message
                        .getRecipient(),
                message.getMessageText(),
                message.getReadStatus(),
                message.getAuthor().getId().equals(user.getId()));
    }

    private static Dto formatDialog(Dialog dialog) {

        Message lastMessage = dialog.getMessages()
                .stream()
                .sorted(Comparator.comparing(Message::getTime).reversed())
                .findFirst()
                .orElse(null);

        return new DialogDto(
                dialog.getId(),
                (int) dialog
                        .getMessages()
                        .stream()
                        .filter(message -> message.getReadStatus() == ReadStatus.SENT)
                        .count(),

                dialog.getMessages().size() > 0 ? formatMessage(lastMessage, lastMessage.getAuthor()) : null
        );
    }
}
