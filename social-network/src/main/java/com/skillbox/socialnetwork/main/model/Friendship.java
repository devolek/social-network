package com.skillbox.socialnetwork.main.model;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Data
@Table(name = "friendships")
@AllArgsConstructor
@RequiredArgsConstructor
@NoArgsConstructor
public class Friendship {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NonNull
    @NotNull
    @ManyToOne
    @JoinColumn(name = "status_id")
    private FriendshipStatus status;

    @NonNull
    @NotNull
    @ManyToOne
    @JoinColumn(name = "src_person_id")
    private Person srcPerson;

    @NonNull
    @NotNull
    @ManyToOne
    @JoinColumn(name = "dst_person_id")
    private Person dstPerson;
}
