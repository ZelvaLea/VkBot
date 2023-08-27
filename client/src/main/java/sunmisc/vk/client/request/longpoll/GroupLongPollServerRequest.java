package sunmisc.vk.client.request.longpoll;

import sunmisc.vk.client.annotations.ParameterName;
import sunmisc.vk.client.annotations.RequestProperties;
import sunmisc.vk.client.model.longpoll.LongPollServerResponse;
import sunmisc.vk.client.request.Request;

@RequestProperties(route = "groups.getLongPollServer")
public record GroupLongPollServerRequest(
        @ParameterName("group_id")
        int groupId

) implements Request<LongPollServerResponse> { }

