package sunmisc.vk.client.dynamo.status;

import sunmisc.vk.client.request.annotated.Request;
import sunmisc.vk.client.request.annotations.ParameterName;
import sunmisc.vk.client.request.annotations.RequestProperties;

@RequestProperties(route = "status.set")
public record StatusSetQuery(
        @ParameterName("text")
        String text

/*        @JsonInclude(JsonInclude.Include.NON_EMPTY)
        @ParameterName("group_id")
        Integer targetId*/
) implements Request<Integer> { }
