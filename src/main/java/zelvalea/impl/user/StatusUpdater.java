package zelvalea.impl.user;

import zelvalea.bot.Bot;
import zelvalea.bot.sdk.request.status.StatusSetQuery;

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
        int len = STATUS.length;
        return STATUS[index++ % len];
    }
}
