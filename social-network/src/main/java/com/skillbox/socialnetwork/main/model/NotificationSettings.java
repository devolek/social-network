package com.skillbox.socialnetwork.main.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;

@Entity
@Data
@Table(name = "notification_settings")
@EqualsAndHashCode
public class NotificationSettings {

    public NotificationSettings() {

    }

    public NotificationSettings(
            Person person,
            boolean postNotification,
            boolean postCommentNotification,
            boolean commentCommentNotification,
            boolean friendRequestNotification,
            boolean friendBirthdayNotification,
            boolean messageNotification) {
        this.person = person;
        this.postNotification = postNotification;
        this.postCommentNotification = postCommentNotification;
        this.commentCommentNotification = commentCommentNotification;
        this.friendRequestNotification = friendRequestNotification;
        this.friendBirthdayNotification = friendBirthdayNotification;
        this.messageNotification = messageNotification;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @OneToOne
    @JoinColumn(name = "person_id", referencedColumnName = "id")
    private Person person;

    @Column(name = "POST")
    private boolean postNotification;

    @Column(name = "POST_COMMENT")
    private boolean postCommentNotification;

    @Column(name = "COMMENT_COMMENT")
    private boolean commentCommentNotification;

    @Column(name = "FRIEND_REQUEST")
    private boolean friendRequestNotification;

    @Column(name = "FRIEND_BIRTHDAY")
    private boolean friendBirthdayNotification;

    @Column(name = "MESSAGE")
    private boolean messageNotification;
}
