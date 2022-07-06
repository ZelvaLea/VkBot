package zelvalea.bot.commands;

import zelvalea.bot.events.messages.NewMessageEvent;

import java.util.Set;

public interface Command {
    String getName();

    Set<String> getAlias();

    String getDescription();

    boolean execute(String[] args, NewMessageEvent event);

}