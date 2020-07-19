package com.skillbox.socialnetwork.main.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Data
@Table(name = "posts")
public class Post{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private Date time;

    @ManyToOne
    @JoinColumn(name = "author_id")
    private Person author;

    private String title;

    @Type(type = "text")
    private String postText;

    private Boolean isBlocked;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(
            name = "posts2tags",
            joinColumns = {@JoinColumn(name = "post_id", referencedColumnName = "id")},
            inverseJoinColumns = {@JoinColumn(name = "tag_id", referencedColumnName = "id")}
    )
    private List<Tag> tags;

    @OneToMany(mappedBy = "post")
    private List<PostLike> likes;

    @OneToMany(mappedBy = "post")
    private List<PostFile> files;

    @OneToMany(mappedBy = "post")
    private List<PostComment> comments;

    @OneToMany(mappedBy = "post")
    private List<BlockHistory> blockHistories;

    @Override
    public String toString() {
        return getId() + " " + title + " " + postText;
    }
}
