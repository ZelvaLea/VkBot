package zelvalea.impl.writer;

import com.fasterxml.jackson.databind.ObjectMapper;
import sunmisc.vk.client.events.EventLabel;
import sunmisc.vk.client.events.Listener;
import sunmisc.vk.client.events.longpoll.NewMessageEvent;
import sunmisc.vk.client.model.messages.Message;

import java.io.File;
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

    private static final ObjectMapper MAPPER = new ObjectMapper();
    private final ReentrantLock lock = new ReentrantLock();

    private final Map<String,String> accumulate;

    private Answer first, last;

    public MessageListener(File file) {
        Map<String, String> map;

        try {
            Sentences sentences = MAPPER.readValue(file, Sentences.class);
            map = sentences.sentences;
        } catch (IOException e) {
            map = new HashMap<>();
        }

        accumulate = map;
        try (ScheduledExecutorService pusher =
                     Executors.newScheduledThreadPool(1)) {
            pusher.scheduleWithFixedDelay(() -> {
                if (lock.tryLock()) {
                    try {
                        for (Answer a = first, p = a; ; p = a) {
                            if (a == null ||
                                    (a = a.next) == null)
                                break;
                            else
                                accumulate.put(
                                        p.joiner.toString(),
                                        a.joiner.toString());
                        }
                        first = last;
                    } finally {
                        lock.unlock();
                    }
                    try {
                        MAPPER.writeValue(file, new Sentences(accumulate));
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }, 1, 1, TimeUnit.MINUTES);
        }
    }


    @EventLabel
    public void onMsg(NewMessageEvent e) {
        Message msg = e.message();
        String text = msg.text();
        if (!text.isEmpty()) {
            int peerId = msg.from_id();
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

    private record Sentences(Map<String, String> sentences) { }
}
