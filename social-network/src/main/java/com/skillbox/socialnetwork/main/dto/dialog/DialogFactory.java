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
                                .filter(message -> message.getAuthor() != user)
                                .count(),
                        dialog
                                .getMessages()
                                .size() > 0 ? formatMessage(dialog
                                .getMessages()
                                .stream()
                                .max(Comparator.comparing(Message::getTime))
                                .get(), user) : null,
                        dialog.isFrozen()))
                .collect(Collectors.toList());
    }

    public static BaseResponseList getMessages(List<Message> messageList, Person user, int offset, int limit, int fromMessageId) {
        return new BaseResponseList(
                messageList.size(),
                offset,
                limit,
                messageList.size() > 0 ? formatMessages(messageList, user) : new ArrayList<>()
        );
    }

    private static List<Dto> formatMessages(List<Message> messages, Person user) {
        try {
            return messages.stream().map(message -> formatMessage(message, user))
                    .collect(toList());
        } catch (NullPointerException e) {
            e.getMessage();
        }
        return null;
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
}
