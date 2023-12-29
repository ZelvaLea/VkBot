package sunmisc.malibu.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ApiError(
        int error_code,
        String error_msg
) { }
