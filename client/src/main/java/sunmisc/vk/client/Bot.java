package sunmisc.vk.client;

import sunmisc.vk.client.actor.Actor;
import sunmisc.vk.client.commands.CommandHandler;
import sunmisc.vk.client.events.EventHandler;
import sunmisc.vk.client.longpoll.LongPollClient;
import sunmisc.vk.client.request.longpoll.GroupLongPollServerRequest;
import sunmisc.vk.client.utils.SequentialScope;

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

    public CommandHandler commandHandler() { return commandHandler; }

    public EventHandler eventHandler() { return eventHandler; }

    public LongPollClient longPoll() { return longPoll; }

    public TransportClient httpTransport() { return httpTransport; }

    public Actor actor() { return actor; }

    public SequentialScope scope() { return scope; }

    private void tryFire() {
        httpTransport
                .sendAsync(new GroupLongPollServerRequest(actor.id()))
                .whenComplete((response,t) -> {
                    if (t != null) {
                        LOGGER.log(Level.SEVERE, "A connection error has occurred ", t);
                        tryFire();
                    } else {
                        postFire(response.server(),
                                response.key(),
                                response.timestamp()
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
            if (t != null)
                tryFire();
            else
                postFire(server, key, r.timestamp());
        });
    }
}
