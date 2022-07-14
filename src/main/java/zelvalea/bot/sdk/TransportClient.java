package zelvalea.bot.sdk;

import com.google.gson.Gson;
import zelvalea.bot.sdk.request.Request;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;

public record TransportClient(HttpClient client, String accessToken) {
    private static final String API_VERSION = "5.131";
    private static final String API_URL = "https://api.vk.com/method/";
    private static final Gson GSON = new Gson();

    public <R> CompletableFuture<R> sendRequestAsync(Request<R> request) {
        HttpRequest hr = buildRequest(request);
        return client
                .sendAsync(hr, HttpResponse.BodyHandlers.ofString())
                .thenApply(r -> GSON.fromJson(r.body(), request.getResponseType()));
    }

    /*public <R> R sendRequest(Request<R> request) {
        HttpRequest hr = buildRequest(request);
        try {
            var r = client
                    .send(hr, HttpResponse.BodyHandlers.ofString());
            return GSON.fromJson(r.body(), request.getResponseType());
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }*/
    <R> HttpRequest buildRequest(Request<R> request) {
        return HttpRequest
                .newBuilder(URI.create(API_URL + request.getMethod()))
                .POST(HttpRequest.BodyPublishers.ofString(
                        request.toBody() +
                                "&access_token="+accessToken+"&v="+API_VERSION
                ))
                .build();
    }
}
