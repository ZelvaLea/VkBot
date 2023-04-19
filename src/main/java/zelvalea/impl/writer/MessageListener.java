package zelvalea.impl.writer;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;
import zelvalea.bot.events.EventLabel;
import zelvalea.bot.events.Listener;
import zelvalea.bot.events.longpoll.NewMessageEvent;
import zelvalea.bot.sdk.response.messages.Message;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.StringJoiner;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MessageListener implements Listener {
    private static final Logger LOGGER = Logger.getLogger("chat");

    private static final Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .create();
    private final ReentrantLock lock = new ReentrantLock();

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

                boolean acquired = lock.tryLock();
                if (acquired) {
                    try {
                        for (Answer a = first, p = a; ; p = a) {
                            if (a == null ||
                                    (a = a.next) == null)
                                break;
                            else
                                cache.put(
                                        p.joiner.toString(),
                                        a.joiner.toString());
                        }
                        first = last;
                    } finally {
                        lock.unlock();
                    }
                }
                if (acquired) {
                    try {
                        GSON.toJson(new Sentences(cache), new FileWriter(file));
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
        if (!text.isEmpty()) {
            int peerId = msg.getFromId();
            LOGGER.log(Level.INFO, peerId + " : " + text);

            lock.lock();
            try {
                Answer prev = last;
                if (prev == null)
                    first = last = new Answer(peerId, text);
                else if (prev.id == peerId)
                    prev.joiner.add(text);
                else
                    last = prev.next = new Answer(peerId, text);
            } finally {
                lock.unlock();
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
