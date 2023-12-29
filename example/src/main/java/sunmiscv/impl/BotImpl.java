package sunmiscv.impl;

import sunmiscv.impl.text.Text;
import sunmisc.malibu.VkMethods;
import sunmisc.malibu.request.methods.status.StatusSetQuery;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.http.HttpClient;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Properties;
import java.util.concurrent.*;

public class BotImpl {

    private final String accessToken;

    public BotImpl(String accessToken) {
        this.accessToken = accessToken;
    }

    public static void main(String[] args) throws ExecutionException, InterruptedException {

        Properties properties = new Properties();

        try (InputStream inputStream =
                     BotImpl.class.getResourceAsStream(
                             "/config.properties")) {
            properties.load(inputStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        String token = properties.getProperty("access_token");
        new BotImpl(token).start();
    }

    public void start() throws ExecutionException, InterruptedException {
        HttpClient httpClient = HttpClient.newHttpClient();

        VkMethods methods = new VkMethods(accessToken, httpClient);

        Iterator<Text> itr = new CyclicStatusIterator();

        try (ScheduledExecutorService scheduled =
                     Executors.newScheduledThreadPool(1)) {
            scheduled.scheduleWithFixedDelay(() -> {

                        try {
                            methods.invoke(new StatusSetQuery(itr.next().asString()));
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    }, 0, 1, TimeUnit.MINUTES
            ).get();
        }
    }

}