package zelvalea.impl;

import zelvalea.bot.Bot;
import zelvalea.bot.actor.GroupActor;
import zelvalea.impl.user.StatusUpdater;

import java.net.http.HttpClient;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class BotStarter {

    private static final String ACCESS_TOKEN = "todo: access_token";
    private static final int ID = 0; // todo: user id;


    public static void main(String[] args) {
        HttpClient httpClient = HttpClient.newHttpClient();

        Bot user = new Bot(httpClient, new GroupActor(ACCESS_TOKEN, ID));

        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

        scheduler.scheduleAtFixedRate(new StatusUpdater(user), 0, 1, TimeUnit.MINUTES);

        user.start();
    }
}
