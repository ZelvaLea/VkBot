package sunmisc.malibu.request.methods.longpoll;

import sunmisc.malibu.model.longpoll.LongPollServerResponse;
import sunmisc.malibu.request.annotated.Request;
import sunmisc.malibu.request.annotations.ParameterName;
import sunmisc.malibu.request.annotations.RequestProperties;

@RequestProperties(route = "groups.getLongPollServer")
public record GroupLongPollServerRequest(
        @ParameterName("group_id")
        int groupId

) implements Request<LongPollServerResponse> { }

