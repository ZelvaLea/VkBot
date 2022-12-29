package zelvalea.bot.sdk.longpoll;

import com.google.gson.*;
import zelvalea.bot.events.AbstractEvent;
import zelvalea.bot.events.Event;
import zelvalea.bot.events.EventHandler;
import zelvalea.bot.events.longpoll.NewMessageEvent;
import zelvalea.bot.sdk.model.LongPollEvent;
import zelvalea.bot.utils.SequentialScope;

import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class LongPollClient {
    private static final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(LongPollEvent.class, new EventDeserializer())
            .create();
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
                    var res_obj =
                            GSON.fromJson(response.body(), LongPollResponse.class);
                    res_obj
                            .events()
                            .forEach(t -> eventHandler.fire(t.object()));
                    return res_obj;
                }));
    }

    private static class EventDeserializer
            implements JsonDeserializer<LongPollEvent> {
        static final Map<String, Class<? extends Event>> MAP_EVENTS = Map.of(
                "message_new", NewMessageEvent.class
        );

        @Override
        public LongPollEvent deserialize(
                JsonElement jsonElement,
                Type type,
                JsonDeserializationContext context)
                throws JsonParseException {

            JsonObject ajo = jsonElement.getAsJsonObject();

            String eName = ajo.get("type").getAsString();

            Class<? extends Event> eType =
                    MAP_EVENTS.getOrDefault(eName, AbstractEvent.class);
            return new LongPollEvent(
                    eName,
                    context.deserialize(ajo.get("object"), eType)
            );
        }
    }
    public static final class LongPollResponse {
        private int ts;
        private LinkedList<LongPollEvent> updates;

        public int timestamp() {
            return ts;
        }

        public List<LongPollEvent> events() {
            return updates;
        }
    }
}
