package zelvalea.bot.sdk.request.longpoll;

import zelvalea.bot.sdk.request.Request;
import zelvalea.bot.sdk.response.longpoll.GroupLongPollServerResponse;

public final class GroupLongPollServerRequest
        extends Request<GroupLongPollServerResponse> {
    public GroupLongPollServerRequest(int groupId) {
        super("groups.getLongPollServer");
        addParam("group_id", groupId);
    }
}
