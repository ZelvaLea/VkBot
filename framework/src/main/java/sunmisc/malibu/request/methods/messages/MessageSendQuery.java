package sunmisc.malibu.request.methods.messages;

import sunmisc.malibu.request.annotated.Request;
import sunmisc.malibu.request.annotations.ParameterName;
import sunmisc.malibu.request.annotations.RequestProperties;

import static java.util.Objects.requireNonNull;

public final class MessageSendQuery {
    private MessageSendQuery() {}


    public static Builder newBuilder() {
        return new Builder();
    }

    public final static class Builder {
        private String text;
        private Integer peer_id;
        private int random_id;

        private Builder() {
        }

        public Builder text(String text) {
            this.text = text;
            return this;
        }

        public Builder peerId(int peer_id) {
            this.peer_id = peer_id;
            return this;
        }

        public Builder randomId(int id) {
            this.random_id = id;
            return this;
        }

        public Request<Integer> build() {
            @RequestProperties(route = "messages.send")
            record MessagesSendQueryData(
                    @ParameterName("message")
                    String text,
                    @ParameterName("peer_id")
                    int peer_id,
                    @ParameterName("random_id")
                    int random_id
            ) implements Request<Integer> {
            }

            return new MessagesSendQueryData(
                    requireNonNull(text),
                    requireNonNull(peer_id),
                    random_id);
        }
    }

}