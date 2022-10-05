package zelvalea.bot.sdk.request.messages;

import zelvalea.bot.sdk.request.Request;
import zelvalea.bot.sdk.response.Response;

public final class MessagesSendQuery
        extends Request<Response<Integer>> {

    public MessagesSendQuery(String text, int peer_id, int random_id) {
        super("messages.send");
        addParam("message", text);
        addParam("peer_id", peer_id);
        addParam("random_id", random_id);
    }
}
