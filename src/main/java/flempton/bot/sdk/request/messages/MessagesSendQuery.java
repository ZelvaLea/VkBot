package flempton.bot.sdk.request.messages;

import flempton.bot.sdk.request.Request;

public final class MessagesSendQuery
        extends Request<Integer> {

    public MessagesSendQuery(String text, int peer_id, int random_id) {
        super("messages.send");
        addParam("message", text);
        addParam("peer_id", peer_id);
        addParam("random_id", random_id);
    }
}
