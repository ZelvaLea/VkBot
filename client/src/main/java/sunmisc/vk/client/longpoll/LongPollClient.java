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
import sunmisc.vk.client.events.AbstractEvent;
import sunmisc.vk.client.events.Event;
import sunmisc.vk.client.events.EventHandler;
import sunmisc.vk.client.events.longpoll.NewMessageEvent;
import sunmisc.vk.client.model.LongPollEvent;
import sunmisc.vk.client.utils.SequentialScope;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class LongPollClient {
    private static final ObjectMapper MAPPER;

    static {
        ObjectMapper m = new ObjectMapper();
        SimpleModule module = new SimpleModule();
        module.addDeserializer(LongPollEvent.class, new EventDeserializer());
        m.registerModules(module, new JavaTimeModule());
        MAPPER = m;
    }

    private final HttpClient client;
    private final EventHandler eventHandler;
    private final SequentialScope scope;

    public LongPollClient(HttpClient client,
                          EventHandler eventHandler,
                          SequentialScope scope) {
        this.client = client;
        this.eventHandler = eventHandler;
        this.scope = scope;
    }
    public CompletableFuture<LongPollClient.LongPollResponse> postEvents(
            String server,
            Map<String,Object> params
    ) {
        String parse = params
                .entrySet()
                .stream()
                .map((e) -> e.getKey() + '=' + e.getValue())
                .collect(Collectors.joining("&"));

        URI uri = URI.create(server + '?' + parse);

        HttpRequest hr = HttpRequest
                .newBuilder(uri)
                .GET()
                .build();

        return scope.runOrSchedule(() -> client
                .sendAsync(hr, HttpResponse.BodyHandlers.ofString())
                // ForkJoinPool executor for event handling is nice
                .thenApplyAsync(response -> {
                    try {
                        System.out.println(response.body());
                        var res_obj
                                = MAPPER.readValue(response.body(), LongPollResponse.class);
                        res_obj
                                .updates()
                                .forEach(t -> eventHandler.fire(t.object()));
                        return res_obj;
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                }));
    }

    private static class EventDeserializer
             extends JsonDeserializer<LongPollEvent> {

        static final Map<String, Class<? extends Event>> MAP_EVENTS = Map.of(
                "message_new", NewMessageEvent.class
        );

        @Override
        public LongPollEvent deserialize(
                JsonParser jsonParser,
                DeserializationContext context)
                throws IOException {

            JsonNode jsonNode = jsonParser.getCodec().readTree(jsonParser);

            String eName = jsonNode.get("type").asText();

            Class<? extends Event> eType =
                    MAP_EVENTS.getOrDefault(eName, AbstractEvent.class);
            return new LongPollEvent(
                    eName,
                    context.readValue(jsonNode.get("object").traverse(), eType)
            );
        }
    }
    public record LongPollResponse(
            @JsonProperty("ts")
            int timestamp,
            List<LongPollEvent> updates
    ) { }
}
