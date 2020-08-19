package com.skillbox.socialnetwork.main.dto.dialog.response;

import com.skillbox.socialnetwork.main.dto.person.response.PersonResponseDto;
import com.skillbox.socialnetwork.main.dto.universal.Dto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MessagesDto implements Dto {
    private int count;
    private List<MessageDto> messages;
    private List<PersonResponseDto> profiles;
}
