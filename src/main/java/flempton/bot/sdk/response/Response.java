package flempton.bot.sdk.response;

import flempton.bot.sdk.objects.ApiError;

public class Response<V> {
    private V response;
    private ApiError error;


    public ApiError getError() {return error;}

    public V getResponse() {return response;}

    @Override
    public String toString() {
        return error != null ? error.toString() : String.valueOf(response);
    }
}
