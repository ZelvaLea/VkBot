package zelvalea.bot.commands;

import zelvalea.bot.events.messages.NewMessageEvent;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public interface Command {
    String getName();

    default Set<String> getAlias() {
        return Collections.emptySet();
    }

    String getDescription();

    CompletableFuture<?> execute(String[] args, NewMessageEvent event);

}