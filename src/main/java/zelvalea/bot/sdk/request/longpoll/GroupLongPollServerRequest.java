package zelvalea.bot.sdk.request.longpoll;

import zelvalea.bot.sdk.request.Request;
import zelvalea.bot.sdk.response.longpoll.LongPollServerResponse;

public final class GroupLongPollServerRequest
        extends Request<LongPollServerResponse> {

    public GroupLongPollServerRequest(int groupId) {
        super("groups.getLongPollServer");
        addParam("group_id", groupId);
    }

}
