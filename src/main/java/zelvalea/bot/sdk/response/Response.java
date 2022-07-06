package zelvalea.bot.sdk.response;

import zelvalea.bot.sdk.objects.ApiError;

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
