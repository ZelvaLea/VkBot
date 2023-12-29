package sunmisc.malibu.request.methods.status;

import sunmisc.malibu.request.annotated.Request;
import sunmisc.malibu.request.annotations.ParameterName;
import sunmisc.malibu.request.annotations.RequestProperties;

@RequestProperties(route = "status.set")
public record StatusSetQuery(
        @ParameterName("text")
        String text
) implements Request<Integer> { }
