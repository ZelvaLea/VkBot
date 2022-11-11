package zelvalea.bot;

import zelvalea.bot.actor.Actor;
import zelvalea.bot.commands.CommandHandler;
import zelvalea.bot.events.EventHandler;
import zelvalea.bot.sdk.TransportClient;
import zelvalea.bot.sdk.longpoll.LongPollClient;
import zelvalea.bot.sdk.request.longpoll.GroupLongPollServerRequest;
import zelvalea.bot.utils.SequentialScope;

import java.net.http.HttpClient;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Bot {
    private static final int WAIT_TIME = 25;
    public static final Logger LOGGER = Logger.getLogger("Bot");
    private final EventHandler eventHandler;
    private final CommandHandler commandHandler;
    private final LongPollClient longPoll;
    private final TransportClient httpTransport;
    private final Actor actor;

    private final SequentialScope scope
            = new SequentialScope();

    public Bot(HttpClient httpClient, Actor actor) {
        this.actor = actor;
        this.httpTransport = new TransportClient(
                httpClient,
                actor.accessToken()
        );
        this.eventHandler = new EventHandler();
        this.commandHandler = new CommandHandler(eventHandler);
        this.longPoll = new LongPollClient(
                httpClient,
                eventHandler,
                scope
        );
    }

    public void start() { tryFire(); }

    public CommandHandler getCommandHandler() { return commandHandler; }

    public EventHandler getEventHandler() { return eventHandler; }

    public LongPollClient getLongPoll() { return longPoll; }

    public TransportClient getHttpTransport() { return httpTransport; }

    public Actor getActor() { return actor; }

    public SequentialScope getScope() { return scope; }

    private void tryFire() {
        httpTransport
                .sendRequestAsync(new GroupLongPollServerRequest(actor.id()))
                .whenComplete((r,t) -> {
                    if (t != null) {
                        LOGGER.log(Level.SEVERE, "A connection error has occurred");
                        tryFire();
                    } else {
                        var response
                                = r.getResponse();
                        postFire(response.getServer(),
                                response.getKey(),
                                response.getTs()
                        );
                    }
                });
    }

    private void postFire(String server, String key, int ts) {
        longPoll.postEvents(server, Map.of(
                "key", key,
                "ts", ts,
                "wait", WAIT_TIME,
                "act", "a_check")
        ).whenComplete((r,t) -> {
            if (t != null) {
                tryFire();
            } else {
                postFire(server, key, r.timestamp());
            }
        });
    }
}
