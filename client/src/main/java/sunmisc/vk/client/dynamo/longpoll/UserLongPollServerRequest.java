package sunmisc.vk.client.dynamo.longpoll;

import sunmisc.vk.client.model.longpoll.LongPollServerResponse;
import sunmisc.vk.client.request.annotated.Request;
import sunmisc.vk.client.request.annotations.ParameterName;
import sunmisc.vk.client.request.annotations.RequestProperties;

@RequestProperties(route = "messages.getLongPollServer")
public record UserLongPollServerRequest(
        @ParameterName("group_id")
        int groupId,

        @ParameterName("lp_version")
        int lp_version // 12

) implements Request<LongPollServerResponse> { }
