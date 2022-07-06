package zelvalea.bot;

import zelvalea.bot.actor.Actor;
import zelvalea.bot.commands.CommandHandler;
import zelvalea.bot.events.EventHandler;
import zelvalea.bot.sdk.TransportClient;
import zelvalea.bot.sdk.longpoll.LongPollClient;
import zelvalea.bot.sdk.request.longpoll.GroupLongPollServerRequest;

import java.net.http.HttpClient;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Bot {
    public static final Logger LOGGER = Logger.getLogger("Bot");
    private final EventHandler eventHandler;
    private final CommandHandler commandHandler;
    private final LongPollClient longPoll;
    private final TransportClient httpTransport;
    private final Actor actor;

    public Bot(Actor actor) {
        this.actor = actor;
        this.httpTransport = new TransportClient(
                HttpClient.newBuilder().build(),
                actor.accessToken()
        );
        this.eventHandler = new EventHandler();
        this.commandHandler = new CommandHandler(eventHandler);
        this.longPoll = new LongPollClient(httpTransport, eventHandler);
    }

    private void postFire() {
        var d =
                httpTransport.sendRequest(new GroupLongPollServerRequest(actor.id()));
        d.whenComplete((r, t) -> {
            if (t != null) {
                LOGGER.log(Level.SEVERE, "A connection error has occurred");
                postFire();
            } else {
                var response = r.getResponse();
                tryFire(response.getServer(),
                        response.getKey(),
                        response.getTs()
                );
            }
        });
    }

    private void tryFire(String server, String key, int ts) {
        longPoll.postEvents(server, key, ts)
                .whenComplete((r,t) -> {
                    if (t == null)
                        tryFire(server,key,r.getTimestamp());
                    else
                        postFire();
                });
    }

    public void start() throws InterruptedException {
        postFire();
        Thread.currentThread().join();
    }

    public CommandHandler getCommandHandler() {
        return commandHandler;
    }

    public EventHandler getEventHandler() {
        return eventHandler;
    }

    public LongPollClient getLongPoll() {
        return longPoll;
    }

    public TransportClient getHttpTransport() {
        return httpTransport;
    }

    public Actor getActor() {
        return actor;
    }
}
