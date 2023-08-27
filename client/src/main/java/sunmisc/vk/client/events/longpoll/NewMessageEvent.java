package sunmisc.vk.client.events.longpoll;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import sunmisc.vk.client.events.Event;
import sunmisc.vk.client.model.messages.Message;

@JsonIgnoreProperties(ignoreUnknown = true)
public record NewMessageEvent(
        Message message
) implements Event { }
