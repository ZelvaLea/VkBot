package zelvalea.impl.writer;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;
import zelvalea.bot.events.EventLabel;
import zelvalea.bot.events.Listener;
import zelvalea.bot.events.longpoll.NewMessageEvent;
import zelvalea.bot.sdk.response.messages.Message;

import java.io.*;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.StringJoiner;
import java.util.concurrent.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MessageListener implements Listener {
    private static final Logger LOGGER = Logger.getLogger("chat");

    private static final Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .create();
    private final Object lock = new Object();

    private final Map<String,String> cache;

    private Answer first, last;

    public MessageListener(File file) {
        Map<String, String> map;
        try (JsonReader reader = new JsonReader(new FileReader(file))) {
            Sentences sentences = GSON.fromJson(reader, Sentences.class);
            map = sentences.sentences;
        } catch (IOException e) {
            map = new HashMap<>();
        }

        cache = map;
        try (ScheduledExecutorService pusher =
                     Executors.newScheduledThreadPool(1)) {
            pusher.scheduleWithFixedDelay(() -> {
                synchronized (lock) {
                    for (Answer a = first, p; ; ) {
                        p = a;
                        if (a == null || (a = a.next) == null) {
                            break;
                        } else {
                            cache.put(p.joiner.toString(), a.joiner.toString());
                        }
                    }
                    first = last;

                    try {
                        Files.writeString(file.toPath(), GSON.toJson(new Sentences(cache)));
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }, 1, 1, TimeUnit.MINUTES);
        }
    }


    @EventLabel
    public void onMsg(NewMessageEvent e) {
        Message msg = e.getMessage();
        String text = msg.getText();
        if (text.isEmpty()) return;
        int peerId = msg.getFromId();
        LOGGER.log(Level.INFO, peerId + " : " + text);
        synchronized (lock) {
            Answer prev = last;
            if (prev == null) {
                first = last = new Answer(peerId, text);
            } else if (prev.id == peerId) {
                prev.joiner.add(text);
            } else {
                last = prev.next = new Answer(peerId, text);
            }
        }
    }

    private static class Answer {
        private final int id;
        private final StringJoiner joiner;

        private Answer next;

        public Answer(int id, String text) {
            this.id = id;
            StringJoiner joiner = new StringJoiner(" ");
            joiner.add(text);
            this.joiner = joiner;
        }
    }

    private static class Sentences {
        private Map<String, String> sentences;

        public Sentences() {}

        public Sentences(Map<String,String> sentences) {
            this.sentences = sentences;
        }

        public Map<String, String> getSentences() {
            return sentences;
        }
    }
}
