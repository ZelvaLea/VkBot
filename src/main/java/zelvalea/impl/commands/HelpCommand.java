package zelvalea.impl.commands;

import zelvalea.bot.Bot;
import zelvalea.bot.commands.Command;
import zelvalea.bot.events.messages.NewMessageEvent;
import zelvalea.bot.sdk.request.messages.MessagesSendQuery;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadLocalRandom;

public record HelpCommand(Bot bot) implements Command {

    @Override
    public CompletableFuture<Integer> execute(String[] args, NewMessageEvent event) {
        StringBuilder builder = new StringBuilder();
        // cache it?
        bot.getCommandHandler()
                .getCommandMap()
                .forEach((k,v) -> {
                    String desc = v.getDescription();
                    if (desc == null || desc.isEmpty())
                        return;
                    builder.append('/').append(k)
                            .append(" - ").append(desc)
                            .append('\n');
                });
        return bot.getHttpTransport().sendRequest(new MessagesSendQuery(
                builder.toString(),
                event.getMessage().getPeerId(),
                ThreadLocalRandom.current().nextInt()
        ));
    }
    @Override public String getName() {return "help";}

    @Override public String getDescription() {return "help on commands";}
}