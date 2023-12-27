package sunmisc.vk.client.longpoll;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import sunmisc.vk.client.Methods;
import sunmisc.vk.client.dynamo.longpoll.GroupLongPollServerRequest;
import sunmisc.vk.client.events.AbstractEvent;
import sunmisc.vk.client.events.Event;
import sunmisc.vk.client.events.Events;
import sunmisc.vk.client.events.longpoll.NewMessageEvent;
import sunmisc.vk.client.model.LongPollEvent;

import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class GroupLongPollClient implements LongPollClient {
    private static final int WAIT_TIME = 25;
    private static final Logger LOGGER
            = Logger.getLogger("GroupLongPollClient");
    private static final ObjectMapper MAPPER;

    static {
        ObjectMapper m = new ObjectMapper();

        SimpleModule module = new SimpleModule();
        module.addDeserializer(LongPollEvent.class, new EventDeserializer());
        m.registerModules(module, new JavaTimeModule());
        MAPPER = m;
    }
    private final Events events;
    private final int id;
    private final Methods methods;
    private volatile boolean interrupted;

    public GroupLongPollClient(Methods methods,
                               Events events, int id) {
        this.methods = methods;
        this.events = events;
        this.id = id;
    }
    private CompletableFuture<LongPollResponse>
    postEvents(HttpClient client, String server, Map<String,Object> params) {
        String parse = params
                .entrySet()
                .stream()
                .map(e -> e.getKey() + '=' + e.getValue())
                .collect(Collectors.joining("&"));

        URI uri = URI.create(server + '?' + parse);

        HttpRequest hr = HttpRequest
                .newBuilder(uri)
                .GET()
                .build();

        return client
                .sendAsync(hr, HttpResponse.BodyHandlers.ofString())
                // ForkJoinPool executor for event handling is nice
                .thenApplyAsync(response -> {
                    try {
                        LongPollResponse resp = MAPPER.readValue(
                                response.body(),
                                LongPollResponse.class);
                        resp.updates()
                                .forEach(t -> events.fire(t.object()));
                        return resp;
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                });
    }

    @Override
    public void start() {
        if (isInterrupted())
            throw new IllegalStateException("client has been closed");

        try {
            var response = methods
                    .send(new GroupLongPollServerRequest(id));
            postFire(response.server(),
                    response.key(),
                    response.timestamp());
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE,
                    "A connection error has occurred ", e);
            start();
        }
    }

    private void postFire(String server, String key, int ts) {
        if (!isInterrupted()) {
            HttpClient client = HttpClient.newHttpClient();
            postEvents(client, server, Map.of(
                    "key", key,
                    "ts", ts,
                    "wait", WAIT_TIME,
                    "act", "a_check")
            ).whenComplete((r, t) -> {
                if (t != null) {
                    LOGGER.log(Level.WARNING, "Trying to get a server ", t);
                    start();
                } else
                    postFire(server, key, r.timestamp());
            });
        }
    }
    public boolean isInterrupted() {
        return (boolean) INTERRUPTED.getAcquire(this);
    }
    @Override
    public void close() {
        INTERRUPTED.setRelease(this, true);
    }

    private static class EventDeserializer
            extends JsonDeserializer<LongPollEvent> {

        static final Map<String, Class<? extends Event>> MAP_EVENTS = Map.of(
                "message_new", NewMessageEvent.class
        );

        @Override
        public LongPollEvent deserialize(
                JsonParser jsonParser,
                DeserializationContext context
        ) throws IOException {

            JsonNode jsonNode = jsonParser.getCodec().readTree(jsonParser);

            String eName = jsonNode.get("type").asText();

            Class<? extends Event> eType =
                    MAP_EVENTS.getOrDefault(eName, AbstractEvent.class);

            return new LongPollEvent(
                    eName,
                    context.readTreeAsValue(jsonNode.get("object"), eType)
            );
        }
    }
    private record LongPollResponse(
            @JsonProperty("ts")
            int timestamp,
            List<LongPollEvent> updates
    ) { }


    private static final VarHandle INTERRUPTED;
    static {
        try {
            MethodHandles.Lookup l = MethodHandles.lookup();
            INTERRUPTED = l.findVarHandle(GroupLongPollClient.class,
                    "interrupted", boolean.class);
        } catch (ReflectiveOperationException e) {
            throw new ExceptionInInitializerError(e);
        }
    }
}