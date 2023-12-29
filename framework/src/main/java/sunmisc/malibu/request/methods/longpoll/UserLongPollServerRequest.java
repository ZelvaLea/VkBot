package sunmisc.malibu.request.methods.longpoll;

import sunmisc.malibu.model.longpoll.LongPollServerResponse;
import sunmisc.malibu.request.annotated.Request;
import sunmisc.malibu.request.annotations.ParameterName;
import sunmisc.malibu.request.annotations.RequestProperties;

@RequestProperties(route = "messages.getLongPollServer")
public record UserLongPollServerRequest(
        @ParameterName("group_id")
        int groupId,

        @ParameterName("lp_version")
        int lp_version // 12

) implements Request<LongPollServerResponse> { }
