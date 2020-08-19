package com.skillbox.socialnetwork.main.dto.friends.response;

import com.skillbox.socialnetwork.main.dto.universal.Dto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class IsFriendDto implements Dto {
    private int user_id;
    private String status;
}
