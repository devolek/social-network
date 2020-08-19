package com.skillbox.socialnetwork.main.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "dialogs")
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Dialog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(
            name = "dialog2person",
            joinColumns = {@JoinColumn(name = "dialog_id", referencedColumnName = "id")},
            inverseJoinColumns = {@JoinColumn(name = "person_id", referencedColumnName = "id")}
    )
    private List<Person> people;
    @OneToMany(mappedBy = "dialog")
    @ToString.Exclude
    private List<Message> messages;
    @CreationTimestamp
    private Date creationDate;
    private boolean isFrozen;
}
