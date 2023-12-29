package sunmisc.malibu.request.methods.audio;

import sunmisc.malibu.model.audio.ListAudios;
import sunmisc.malibu.request.annotated.Request;
import sunmisc.malibu.request.annotations.ParameterName;
import sunmisc.malibu.request.annotations.RequestProperties;

import static java.util.Objects.requireNonNull;

public final class AudioGetRequest {

    private AudioGetRequest() {}

    public static Builder newBuilder() {
        return new Builder();
    }

    public final static class Builder {
        private Integer ownerId;

        private int offset;
        private int count = Integer.MAX_VALUE;

        private Builder() { }


        public Builder ownerId(int ownerId) {
            this.ownerId = ownerId;
            return this;
        }
        public Builder offset(int offset) {
            this.offset = offset;
            return this;
        }
        public Builder count(int count) {
            this.count = count;
            return this;
        }
        public Request<ListAudios> build() {
            @RequestProperties(route = "audio.get")
            record AudioGetData(
                    @ParameterName("owner_id")
                    int ownerId,
                    @ParameterName("offset")
                    int offset,
                    @ParameterName("count")
                    int count
            ) implements Request<ListAudios> { }

            return new AudioGetData(
                    requireNonNull(ownerId),
                    offset,
                    count);
        }
    }
}
