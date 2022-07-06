package flempton.bot.events.messages;

import flempton.bot.events.Event;
import flempton.bot.sdk.response.messages.Message;

public class NewMessageEvent implements Event {
    private Message message;

    public Message getMessage() {
        return message;
    }
}
