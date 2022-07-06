package zelvalea.bot.sdk.response.longpoll;

import zelvalea.bot.sdk.response.Response;

public class GroupLongPollServerResponse
        extends Response<GroupLongPollServerResponse.GroupLongPollServerData> {

    public static final class GroupLongPollServerData {
        private String key;

        private String server;

        private String ts;


        public String getKey() {return key;}

        public String getServer() {return server;}

        public String getTs() {return ts;}
    }
}
