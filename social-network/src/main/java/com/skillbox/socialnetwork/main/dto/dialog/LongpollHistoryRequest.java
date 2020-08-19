package com.skillbox.socialnetwork.main.dto.dialog;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LongpollHistoryRequest {
    private long ts;
    private int pts;
    @JsonProperty("preview_length")
    private int previewLength;
    private int onlines;
    @JsonProperty("events_limit")
    private int eventsLimit;
    @JsonProperty("msgs_limit")
    private int messagesLimit;
    @JsonProperty("max_msg_id")
    private int maxMessageId;
}
