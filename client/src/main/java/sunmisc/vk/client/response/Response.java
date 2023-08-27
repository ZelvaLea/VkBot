package sunmisc.vk.client.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import sunmisc.vk.client.model.ApiError;

@JsonIgnoreProperties
public record Response<R>(R response, ApiError error) { }
