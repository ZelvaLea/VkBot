package flempton.bot.sdk.request.longpoll;

import flempton.bot.sdk.request.Request;
import flempton.bot.sdk.response.longpoll.GroupLongPollServerResponse;

public final class GroupLongPollServerRequest
        extends Request<GroupLongPollServerResponse> {
    public GroupLongPollServerRequest(int groupId) {
        super("groups.getLongPollServer");
        addParam("group_id", groupId);
    }
}
