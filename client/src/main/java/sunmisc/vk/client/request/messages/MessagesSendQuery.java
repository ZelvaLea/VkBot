package sunmisc.vk.client.request.messages;

import sunmisc.vk.client.annotations.ParameterName;
import sunmisc.vk.client.annotations.RequestProperties;
import sunmisc.vk.client.request.Request;

@RequestProperties(route = "messages.send")
public record MessagesSendQuery(
        @ParameterName("message")
        String text,
        @ParameterName("peer_id")
        int peer_id,

        @ParameterName("random_id")
        int random_id
) implements Request<Integer> {
}
