package zelvalea.bot.commands;

import zelvalea.bot.events.EventHandler;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class CommandHandler {

    private final ConcurrentMap<String, Command> commandMap
            = new ConcurrentHashMap<>();

    public CommandHandler(EventHandler handler) {
        handler.registerEvent(new CommandListener(this));
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
}
