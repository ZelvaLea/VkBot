package sunmisc.malibu.request;

import java.nio.charset.StandardCharsets;
import java.util.Optional;

public class TokenBody implements Body {
    private static final String API_VERSION = "5.199";
    private final String accessToken;

    public TokenBody(String accessToken) {
        this.accessToken = accessToken;
    }

    @Override
    public Optional<byte[]> body() {
        return Optional.of(String
                .format("&access_token=%s&v=%s", accessToken, API_VERSION)
                .getBytes(StandardCharsets.UTF_8)
        );
    }

}
