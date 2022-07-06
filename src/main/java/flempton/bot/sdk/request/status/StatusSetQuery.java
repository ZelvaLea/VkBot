package flempton.bot.sdk.request.status;

import flempton.bot.sdk.request.Request;

public final class StatusSetQuery
        extends Request<Integer> {

    public StatusSetQuery() {
        super("status.set");
    }
    public StatusSetQuery text(String value) {
        addParam("text", value);
        return this;
    }
    public StatusSetQuery groupId(int value) {
        addParam("group_id", value);
        return this;
    }
}
