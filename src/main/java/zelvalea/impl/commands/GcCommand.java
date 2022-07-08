package zelvalea.impl.commands;

import zelvalea.bot.Bot;
import zelvalea.bot.commands.Command;
import zelvalea.bot.events.messages.NewMessageEvent;
import zelvalea.bot.sdk.request.messages.MessagesSendQuery;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadLocalRandom;

public record GcCommand(Bot bot) implements Command {
    @Override
    public String getName() {
        return "gc";
    }

    @Override
    public String getDescription() {
        return "Runs the garbage collector in the JVM";
    }

    @Override
    public CompletableFuture<?> execute(String[] args, NewMessageEvent event) {
        System.gc();
        return bot.getHttpTransport().sendRequest(new MessagesSendQuery(
                "garbage collection completed",
                event.getMessage().getPeerId(),
                ThreadLocalRandom.current().nextInt()
        ));
    }
}
