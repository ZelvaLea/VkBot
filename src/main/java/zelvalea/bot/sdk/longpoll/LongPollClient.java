package zelvalea.bot.sdk.longpoll;

import com.google.gson.*;
import zelvalea.bot.events.AbstractEvent;
import zelvalea.bot.events.Event;
import zelvalea.bot.events.EventHandler;
import zelvalea.bot.events.messages.NewMessageEvent;
import zelvalea.bot.sdk.TransportClient;
import zelvalea.bot.sdk.objects.LongPollEvent;

import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public record LongPollClient(
        TransportClient httpClient,
        EventHandler eventHandler
) {
    private static final String LP_QUERY =
            "%s?act=a_check&key=%s&ts=%s&wait=%d";
    private static final int WAIT_TIME = 25;
    private static final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(LongPollEvent.class, new EventDeserializer())
            .create();

    public CompletableFuture<LongPollClient.LongPollResponse> postEvents(
            String server,
            String key,
            int timestamp) {
        URI uri = URI.create(String.format(LP_QUERY,
                server, key, timestamp, WAIT_TIME
        ));
        HttpRequest hr = HttpRequest
                .newBuilder(uri)
                .GET()
                .build();
        var h = httpClient.client()
                .sendAsync(hr, HttpResponse.BodyHandlers.ofString())
                .thenApply(r -> GSON.fromJson(r.body(), LongPollResponse.class));

        // ForkJoinPool executor for event handling is nice
        h.thenAcceptAsync(r -> r
                .getEvents()
                .forEach(x -> eventHandler.callEvent(x.object())
        ));
        return h;
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
        private List<LongPollEvent> updates;

        public int getTimestamp() {
            return ts;
        }

        public List<LongPollEvent> getEvents() {
            return updates;
        }
    }
}
