package zelvalea.bot.sdk.request.longpoll;

import zelvalea.bot.sdk.request.Request;
import zelvalea.bot.sdk.response.longpoll.LongPollServerResponse;

public class UserLongPollServerRequest
        extends Request<LongPollServerResponse> {
    public UserLongPollServerRequest(int groupId) {
        super("messages.getLongPollServer");
        addParam("lp_version", 12);
       // addParam("group_id", groupId);
    }
}
