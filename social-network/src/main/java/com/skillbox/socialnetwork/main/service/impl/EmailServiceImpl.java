package com.skillbox.socialnetwork.main.service.impl;

import com.skillbox.socialnetwork.main.aspect.MethodLogWithTime;
import com.skillbox.socialnetwork.main.service.EmailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.File;

@Service("EmailService")
public class EmailServiceImpl implements EmailService {

    private JavaMailSender emailSender;
    private SimpleMailMessage template;
    private String projectName;

    @Autowired
    public EmailServiceImpl(
            @Qualifier("getJavaMailSender") JavaMailSender emailSender,
            SimpleMailMessage template,
            @Value("${project.name}") String projectName
    ) {
        this.emailSender = emailSender;
        this.template = template;
        this.projectName = projectName;
    }

    @Override
    @MethodLogWithTime(fullMessage = "Email message sent")
    public void sendSimpleMessage(String to, String text, String subject) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setText(text);
            message.setSubject(subject);

            emailSender.send(message);
        } catch (MailException exception) {
            exception.printStackTrace();
        }
    }

    @Override
    @MethodLogWithTime(fullMessage = "Email sent using template")
    public void sendSimpleMessageUsingTemplate(String to, String... templateModel) {
        String text = String.format(template.getText(), templateModel);
        sendSimpleMessage(to, text, projectName);
    }

    @Override
    @MethodLogWithTime(fullMessage = "Account activation link sent")
    public void sendActivationLink(String to, String name, String link) {
        template.setText("Добрый день, %s!\nПерейдите по ссылке и войдите в аккаунт, чтобы активировать его:\n%s");
        String text = String.format(template.getText(), name, link);
        sendSimpleMessage(to, text, "Активация аккаунта " + projectName);
    }

    @Override
    @MethodLogWithTime(fullMessage = "Password recovery link sent")
    public void sendPasswordRecovery(String to, String name, String link) {
        String text = String.format(template.getText(), name, link);
        sendSimpleMessage(to, text, "Запрос восстановления пароля " + projectName);
    }

    @Override
    @MethodLogWithTime(fullMessage = "Email message with an attachment sent")
    public void sendMessageWithAttachment(String to, String subject, String text, String pathToAttachment) {
        try {
            MimeMessage message = emailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(text);

            FileSystemResource file = new FileSystemResource(new File(pathToAttachment));
            helper.addAttachment("Invoice", file);

            message.setSubject(projectName);
            emailSender.send(message);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
}
