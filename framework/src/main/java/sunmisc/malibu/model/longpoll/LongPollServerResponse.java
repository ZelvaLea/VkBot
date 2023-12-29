package sunmisc.malibu.model.longpoll;

import com.fasterxml.jackson.annotation.JsonProperty;

public record LongPollServerResponse(
        String key,
        String server,
        @JsonProperty("ts")
        int timestamp
) { }