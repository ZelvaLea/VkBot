package sunmisc.vk.client.events.longpoll;

import sunmisc.vk.client.events.Event;
import sunmisc.vk.client.model.messages.Message;

public class NewMessageEvent implements Event {
    private Message message;

    public Message getMessage() {
        return message;
    }
}
