package sunmisc.vk.client.request;

import sunmisc.vk.client.request.annotated.ParamsBody;

import java.util.Optional;

public class VkMethod implements Input, Head.VkHead {

    private final Object request;
    private final String accessToken;

    public VkMethod(Object request, String accessToken) {

        this.request = request;
        this.accessToken = accessToken;
    }

    @Override
    public Optional<byte[]> body() throws Exception {
        return new ConcatBody(
                new ParamsBody(request),
                new TokenBody(accessToken)
        ).body();
    }
}
