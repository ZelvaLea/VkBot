package sunmisc.vk.client.dynamo.longpoll;

import sunmisc.vk.client.model.longpoll.LongPollServerResponse;
import sunmisc.vk.client.request.annotated.Request;
import sunmisc.vk.client.request.annotations.ParameterName;
import sunmisc.vk.client.request.annotations.RequestProperties;

@RequestProperties(route = "groups.getLongPollServer")
public record GroupLongPollServerRequest(
        @ParameterName("group_id")
        int groupId

) implements Request<LongPollServerResponse> { }

