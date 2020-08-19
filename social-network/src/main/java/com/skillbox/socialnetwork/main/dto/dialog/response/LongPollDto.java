package com.skillbox.socialnetwork.main.dto.dialog.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LongPollDto {
    private String key;
    private String server;
    private Long ts;//?
}
