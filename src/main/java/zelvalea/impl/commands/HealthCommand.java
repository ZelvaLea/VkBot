package zelvalea.impl.commands;

import zelvalea.bot.Bot;
import zelvalea.bot.commands.Command;
import zelvalea.bot.events.messages.NewMessageEvent;
import zelvalea.bot.sdk.request.messages.MessagesSendQuery;

import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadLocalRandom;

public record HealthCommand(Bot bot) implements Command {
    private static final int NCPU = Runtime.getRuntime().availableProcessors();
    @Override
    public CompletableFuture<Integer> execute(String[] args, NewMessageEvent event) {
        StringBuilder sb = new StringBuilder();
        sb.append("Common:\n");
        sb.append("- available_processors: ").append(NCPU).append('\n');
        sb.append("- workers: ").append(Thread.activeCount()).append('\n');
        sb.append("Memory:\n");
        sb.append("- use: ").append(parseMemorySize(Runtime.getRuntime().freeMemory())).append('\n');
        sb.append("- capacity: ").append(parseMemorySize(Runtime.getRuntime().totalMemory())).append('\n');
        sb.append("- max capacity: ").append(parseMemorySize(Runtime.getRuntime().maxMemory()));
        return bot.getHttpTransport().sendRequestAsync(new MessagesSendQuery(
                sb.toString(),
                event.getMessage().getPeerId(),
                ThreadLocalRandom.current().nextInt()
        ));
    }

    static String parseMemorySize(long bytes) {
        return (bytes / 1024 / 1024) + "MB";
    }

    @Override public String getName() {return "health";}
    @Override public String getDescription() {return "platform health information";}
    @Override public Set<String> getAlias() {return Set.of("lag","memory");}
}
