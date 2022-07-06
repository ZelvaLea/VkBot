package zelvalea.impl.commands;

import zelvalea.bot.Bot;
import zelvalea.bot.commands.Command;
import zelvalea.bot.events.messages.NewMessageEvent;
import zelvalea.bot.sdk.request.messages.MessagesSendQuery;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadLocalRandom;

public record FlipCommand(Bot bot) implements Command {
    @Override
    public CompletableFuture<Integer> execute(String[] args, NewMessageEvent event) {
        ThreadLocalRandom r = ThreadLocalRandom.current();

        return bot.getHttpTransport().sendRequest(new MessagesSendQuery(
                r.nextBoolean() ? "Орел" : "Решка",
                event.getMessage().getPeerId(),
                r.nextInt())
        );
    }
    @Override public String getName() {return "flip";}

    @Override public String getDescription() {return "toss a coin";}

}
