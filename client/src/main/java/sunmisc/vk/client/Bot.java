package sunmisc.vk.client;

import sunmisc.vk.client.commands.CommandHandler;
import sunmisc.vk.client.events.EventHandler;
import sunmisc.vk.client.longpoll.GroupLongPollClient;
import sunmisc.vk.client.owner.Owner;

import java.net.http.HttpClient;

public class Bot implements AutoCloseable {
    private final EventHandler eventHandler;
    private final CommandHandler commandHandler;
    private final GroupLongPollClient longPoll;
    private final HttpApiClient httpTransport;

    public Bot(HttpClient httpClient, Owner owner) {
        this.httpTransport = new HttpApiClient(
                httpClient,
                owner.accessToken()
        );
        this.eventHandler = new EventHandler();
        this.commandHandler = new CommandHandler(eventHandler);
        this.longPoll = new GroupLongPollClient(
                httpClient,
                this,
                owner,
                eventHandler
        );
    }

    public void start() {
        longPoll.tryFire();
    }

    public CommandHandler commandHandler() {
        return commandHandler;
    }

    public EventHandler eventHandler() {
        return eventHandler;
    }

    public HttpApiClient httpTransport() {
        return httpTransport;
    }

    @Override
    public void close() throws Exception {
        longPoll.close();
        httpTransport.close();
    }
}
