package com.skillbox.socialnetwork.main.service;

public interface EmailService {

    void sendSimpleMessage(
            String to,
            String text,
            String subject
    );

    void sendPasswordRecovery(
            String to,
            String name,
            String link
    );

    void sendActivationLink(
            String to,
            String name,
            String link
    );

    void sendSimpleMessageUsingTemplate(
            String to,
            String... templateModel
    );

    void sendMessageWithAttachment(
            String to,
            String subject,
            String text,
            String pathToAttachment
    );
}
