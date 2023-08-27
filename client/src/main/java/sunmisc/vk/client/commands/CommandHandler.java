package sunmisc.vk.client.commands;

import sunmisc.vk.client.events.EventHandler;
import sunmisc.vk.client.events.EventLabel;
import sunmisc.vk.client.events.EventPriority;
import sunmisc.vk.client.events.Listener;
import sunmisc.vk.client.events.longpoll.NewMessageEvent;
import sunmisc.vk.client.model.messages.Message;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class CommandHandler {

    private final ConcurrentMap<String, Command> commandMap
            = new ConcurrentHashMap<>();
    private final EventHandler eventHandler;

    public CommandHandler(EventHandler eventHandler) {
        this.eventHandler = eventHandler;
        eventHandler.registerListener(new CommandListener());
    }

    public void registerCommand(Command command) {
        commandMap.put(command.getName(), command);
        Set<String> alias = command.getAlias();
        if (alias == null)
            return;
        alias.forEach(a -> commandMap.put(a, command));
    }

    public Optional<Command> getCommand(String name) {
        return Optional.ofNullable(commandMap.get(name));
    }

    public Map<String, Command> getCommandMap() {
        return Collections.unmodifiableMap(commandMap);
    }
    private class CommandListener implements Listener {
        private static final int CMD_MASK = 1 << '/' | 1 << '!';

        @EventLabel(priority = EventPriority.HIGHEST)
        public void onMessage(NewMessageEvent event) {
            Message msg = event.getMessage();
            String text = msg.text();
            if (text.length() < 2 ||
                    (CMD_MASK >> text.charAt(0) & 1) == 0) {
                return;
            }
            String[] args = text.substring(1).split(" ");
            getCommand(args[0]).ifPresent(cmd -> {
                int len = args.length;
                String[] args0 = new String[len - 1];
                System.arraycopy(args, 1, args0, 0, len - 1);
                cmd.execute(args0, event);
                // eventHandler.callEvent(new CommandEvent());
            });
        }
    }
}
