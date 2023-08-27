package zelvalea.impl.user;

import sunmisc.vk.client.Bot;
import sunmisc.vk.client.request.status.StatusSetQuery;
import zelvalea.impl.utils.timeformat.TemporalDuration;

import java.util.concurrent.ThreadLocalRandom;

public class StatusUpdater implements Runnable {

    static final String[] STATUS = {
            "Phantom Reference",
            "Hello, World!"
    };
    private int index;

    private final Bot bot;

    public StatusUpdater(Bot bot) {
        this.bot = bot;
    }

    @Override
    public void run() {
        bot.httpTransport()
                .sendAsync(new StatusSetQuery(tryAdvance1())).whenComplete((e,r)->{
                    System.out.println(e + " " + r);
                    return;
                });
    }
    String tryAdvance() {
        int x = ThreadLocalRandom.current().nextInt(0, 100);
        int y = ThreadLocalRandom.current().nextInt(0, 100);
        return x + " + " + y + " = " + (x + y);
    }
    String tryAdvance1() {
        return "\uD83D\uDE44До Нового Года осталось: "+
                TemporalDuration.of(1, 1, 0,0)+"\uD83D\uDE44";
    }


    String tryAdvance0() {
        int len = STATUS.length;
        return STATUS[index++ % len];
    }
}
