package com.skillbox.socialnetwork.main.model;

import com.skillbox.socialnetwork.main.model.enumerated.ReadStatus;
import lombok.Data;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.Date;

@Entity
@Data
@Table(name = "messages")
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private Date time;

    @ManyToOne
    @JoinColumn(name = "author_id")
    private Person author;

    @ManyToOne
    @JoinColumn(name = "recipient_id")
    private Person recipient;

    @Type(type = "text")
    private String messageText;

    @Enumerated(value = EnumType.STRING)
    private ReadStatus readStatus;

    @ManyToOne
    @JoinColumn(name = "dialog_id")
    private Dialog dialog;
}
