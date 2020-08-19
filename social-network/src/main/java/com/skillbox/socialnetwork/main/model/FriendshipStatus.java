package com.skillbox.socialnetwork.main.model;

import com.skillbox.socialnetwork.main.model.enumerated.FriendshipCode;
import lombok.*;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Data
@Table(name = "friendship_statuses")
@RequiredArgsConstructor
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FriendshipStatus {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NonNull
    private Date time;
    @NonNull
    @Enumerated(value = EnumType.STRING)
    private FriendshipCode code;

    @OneToMany(mappedBy = "status")
    private List<Friendship> friendships;
}
