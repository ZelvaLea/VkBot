package sunmisc.vk.client.commands;

import sunmisc.vk.client.events.longpoll.NewMessageEvent;

import java.util.Collections;
import java.util.Set;

public interface Command {
    String name();

    default Set<String> alias() {
        return Collections.emptySet();
    }

    String description();

    void execute(String[] args, NewMessageEvent event);

}