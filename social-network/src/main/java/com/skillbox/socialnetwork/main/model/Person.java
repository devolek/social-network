package com.skillbox.socialnetwork.main.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.skillbox.socialnetwork.main.model.enumerated.Permission;
import com.skillbox.socialnetwork.main.model.enumerated.Status;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Data
@Table(name = "persons")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Person {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @JsonProperty("first_name")
    private String firstName;

    @JsonProperty("last_name")
    private String lastName;

    @CreationTimestamp
    private Date regDate;

    @Column(name = "birth_date")
    private Date birthDate;

    @Column(name = "e_mail", unique = true)
    private String email;

    private String phone;

    @ToString.Exclude
    @JsonIgnore
    private String password;

    private String photo;

    private String about;

    private String city;

    private String country;

    private String confirmationCode;

    @Enumerated(EnumType.STRING)
    private Status status;

    private Boolean isApproved;

    @Enumerated(EnumType.STRING)
    private Permission messagesPermission;

    private Date lastOnlineTime;

    private Boolean isBlocked;

    @JsonProperty("are_you_blocked")
    private Boolean areYouBlocked;

    @ManyToMany(fetch = FetchType.EAGER)
    @JsonIgnore
    @ToString.Exclude
    @JoinTable(name = "user_roles",
            joinColumns = {@JoinColumn(name = "user_id", referencedColumnName = "id")},
            inverseJoinColumns = {@JoinColumn(name = "role_id", referencedColumnName = "id")})
    private List<Role> roles;

    @OneToMany(mappedBy = "person", cascade = CascadeType.ALL)
    @JsonIgnore
    @ToString.Exclude
    private List<BlockHistory> blockHistories;

    @OneToMany(mappedBy = "srcPerson", cascade = CascadeType.ALL)
    @JsonIgnore
    @ToString.Exclude
    private List<Friendship> friendshipsSrc;

    @OneToMany(mappedBy = "dstPerson", cascade = CascadeType.ALL)
    @JsonIgnore
    @ToString.Exclude
    private List<Friendship> friendshipsDst;

    @OneToMany(mappedBy = "author", cascade = CascadeType.ALL)
    @JsonIgnore
    @ToString.Exclude
    private List<Message> sentMessages;

    @OneToMany(mappedBy = "recipient", cascade = CascadeType.ALL)
    @JsonIgnore
    @ToString.Exclude
    private List<Message> recipientMessages;

    @OneToMany(mappedBy = "author", cascade = CascadeType.ALL)
    @JsonIgnore
    @ToString.Exclude
    private List<Post> posts;

    @OneToMany(mappedBy = "person", cascade = CascadeType.ALL)
    @JsonIgnore
    @ToString.Exclude
    private List<PostLike> likes;

    @OneToMany(mappedBy = "author", cascade = CascadeType.ALL)
    @JsonIgnore
    @ToString.Exclude
    private List<PostComment> comments;

    @OneToMany(mappedBy = "person", cascade = CascadeType.ALL)
    @JsonIgnore
    @ToString.Exclude
    private List<Notification> notifications;

    @OneToOne(mappedBy = "person")
    @JsonIgnore
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private NotificationSettings notificationSettings;

    @OneToMany(mappedBy = "entityAuthor")
    @JsonIgnore
    @ToString.Exclude
    private List<Notification> sentNotifications;

    @OneToMany(mappedBy = "person")
    @JsonIgnore
    @ToString.Exclude
    private List<CommentLike> commentLikes;

    @ManyToMany(mappedBy = "people")
    @JsonIgnore
    private List<Dialog> dialogs;

    @Override
    public String toString() {
        return id + " " + firstName + " " + lastName;
    }
}