package sunmisc.vk.client.request.status;

import sunmisc.vk.client.annotations.ParameterName;
import sunmisc.vk.client.annotations.RequestProperties;
import sunmisc.vk.client.request.Request;

@RequestProperties(route = "status.set")
public record StatusSetQuery(
        @ParameterName("text")
        String text

/*        @JsonInclude(JsonInclude.Include.NON_EMPTY)
        @ParameterName("group_id")
        Integer targetId*/
) implements Request<Integer> { }
