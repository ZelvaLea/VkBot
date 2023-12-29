package sunmisc.malibu.events.longpoll;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import sunmisc.malibu.events.Event;
import sunmisc.malibu.model.messages.Message;

@JsonIgnoreProperties(ignoreUnknown = true)
public record NewMessageEvent(
        Message message
) implements Event { }
