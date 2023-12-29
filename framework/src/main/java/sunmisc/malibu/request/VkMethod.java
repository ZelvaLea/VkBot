package sunmisc.malibu.request;

import java.util.Optional;

public class VkMethod implements Input, VkHead {

    private final Body origin;
    private final String accessToken;

    public VkMethod(Body origin, String accessToken) {
        this.origin = origin;
        this.accessToken = accessToken;
    }

    @Override
    public Optional<byte[]> body() throws Exception {
        return new ConcatBody(
                origin,
                new TokenBody(accessToken)
        ).body();
    }
}
