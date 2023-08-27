package sunmisc.vk.client.request.longpoll;

import sunmisc.vk.client.annotations.ParameterName;
import sunmisc.vk.client.annotations.RequestProperties;
import sunmisc.vk.client.model.longpoll.LongPollServerResponse;
import sunmisc.vk.client.request.Request;

@RequestProperties(route = "messages.getLongPollServer")
public record UserLongPollServerRequest(
        @ParameterName("group_id")
        int groupId,

        @ParameterName("lp_version")
        int lp_version // 12

) implements Request<LongPollServerResponse> { }
