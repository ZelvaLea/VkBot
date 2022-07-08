package zelvalea.bot.sdk.request.status;

import zelvalea.bot.sdk.request.Request;

public final class StatusSetQuery
        extends Request<Integer> {

    public StatusSetQuery(String text) {
        super("status.set");
        addParam("text", text);
    }
    public StatusSetQuery groupId(int value) {
        addParam("group_id", value);
        return this;
    }
}
