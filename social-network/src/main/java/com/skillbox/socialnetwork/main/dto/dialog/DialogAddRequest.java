package com.skillbox.socialnetwork.main.dto.dialog;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.skillbox.socialnetwork.main.dto.universal.Dto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.ArrayList;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class DialogAddRequest implements Dto {
    @JsonProperty("users_ids")
    private ArrayList<Integer> userIds;
}
