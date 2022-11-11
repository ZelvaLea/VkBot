package zelvalea.bot.sdk;

import com.google.gson.Gson;
import zelvalea.bot.sdk.request.Request;
import zelvalea.bot.sdk.response.Response;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;

public class TransportClient {
    private static final String API_VERSION = "5.131";
    private static final String API_URL = "https://api.vk.com/method/";
    private static final Gson GSON = new Gson();

    private final HttpClient client;
    private final String accessToken;

    public TransportClient(HttpClient client, String accessToken) {
        this.client = client;
        this.accessToken = accessToken;
    }

    public <R extends Response<?>> CompletableFuture<R> sendRequestAsync(Request<R> request) {

        HttpRequest hr = HttpRequest
                .newBuilder(URI.create(API_URL + request.getMethod()))
                .headers("User-Agent", "VKAndroidApp/7.35")
                .POST(HttpRequest.BodyPublishers.ofString(
                        request.toBody() +
                                "&access_token="+accessToken+"&v="+API_VERSION
                ))
                .build();

        return client
                .sendAsync(hr, HttpResponse.BodyHandlers.ofString())
                .thenApply(r -> GSON.fromJson(r.body(), request.getResponseType()));
    }

    public HttpClient getHttpClient() {
        return client;
    }
}
