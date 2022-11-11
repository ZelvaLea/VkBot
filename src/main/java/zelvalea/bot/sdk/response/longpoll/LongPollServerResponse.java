package zelvalea.bot.sdk.response.longpoll;

import zelvalea.bot.sdk.response.Response;

public final class LongPollServerResponse
        extends Response<LongPollServerResponse.LongPollServerData> {

    public final class LongPollServerData {
        private String key;

        private String server;

        private int ts;


        public String getKey() { return key; }

        public String getServer() { return server; }

        public int getTs() { return ts; }

        @Override
        public String toString() {
            return "LongPollServerData{" +
                    "key='" + key + '\'' +
                    ", server='" + server + '\'' +
                    ", ts=" + ts +
                    '}';
        }
    }
}
