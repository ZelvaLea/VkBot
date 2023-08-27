package sunmisc.vk.client.model.messages;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Message(
        int id,
        int date,
        int peer_id,
        int from_id,
        String text
) { }