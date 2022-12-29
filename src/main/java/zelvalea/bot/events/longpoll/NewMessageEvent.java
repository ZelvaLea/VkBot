package zelvalea.bot.events.longpoll;

import zelvalea.bot.events.Event;
import zelvalea.bot.sdk.response.messages.Message;

public class NewMessageEvent implements Event {
    private Message message;

    public Message getMessage() {
        return message;
    }
}
