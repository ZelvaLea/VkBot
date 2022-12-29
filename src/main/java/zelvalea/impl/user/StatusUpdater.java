package zelvalea.impl.user;

import zelvalea.bot.Bot;
import zelvalea.bot.sdk.request.status.StatusSetQuery;
import zelvalea.impl.utils.timeformat.TemporalDuration;

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
        bot.getHttpTransport()
                .sendRequestAsync(new StatusSetQuery(tryAdvance()));
    }
    String tryAdvance() {
        return "\uD83D\uDE44До Нового Года осталось: "+
                TemporalDuration.of(1, 1, 0,0)+"\uD83D\uDE44";
    }

    String tryAdvance0() {
        int len = STATUS.length;
        return STATUS[index++ % len];
    }
}
