package sunmisc.malibu.longpoll;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import sunmisc.malibu.Methods;
import sunmisc.malibu.events.AbstractEvent;
import sunmisc.malibu.events.Event;
import sunmisc.malibu.events.Events;
import sunmisc.malibu.events.longpoll.NewMessageEvent;
import sunmisc.malibu.request.methods.longpoll.GroupLongPollServerRequest;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class GroupLongPoll implements LongPoll {
    private static final int WAIT_TIME = 25;
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

    public GroupLongPoll(Methods methods, Events events, int id) {
        this.methods = methods;
        this.events = events;
        this.id = id;
    }
    private LongPollResponse
    postEvents(HttpClient client,
               String server,
               Map<String,Object> params) throws IOException, InterruptedException {
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

        var response = client
                .send(hr, HttpResponse.BodyHandlers.ofString());
        try {
            LongPollResponse resp = MAPPER.readValue(
                    response.body(),
                    LongPollResponse.class);
            resp.updates()
                    .stream()
                    .parallel()
                    .forEach(t -> events.fire(t.object()));
            return resp;
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void start(Exit exit) {
        Thread.ofPlatform().daemon().start(() -> {
            try {
                var response = methods
                        .invoke(new GroupLongPollServerRequest(id));
                postFire(exit, response.server(),
                        response.key(),
                        response.timestamp());
            } catch (Exception e) {
                throw new IllegalStateException("A connection error has occurred", e);
            }
        });
    }

    private void postFire(Exit exit, String server, String key, int ts)
            throws IOException, InterruptedException {
        if (exit.ready()) return;
        HttpClient client = HttpClient.newHttpClient();
        var result = postEvents(client, server, Map.of(
                "key", key,
                "ts", ts,
                "wait", WAIT_TIME,
                "act", "a_check"));
        postFire(exit, server, key, result.timestamp());
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

    private record LongPollEvent(String type, Event object) { }

}