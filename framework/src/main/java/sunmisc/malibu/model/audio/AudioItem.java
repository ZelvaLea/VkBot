package sunmisc.malibu.model.audio;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record AudioItem(
        String artist,
        int id,
        @JsonProperty("owner_id")
        int ownerId,
        String title,
        int duration,
        @JsonProperty("access_key")
        String accessKey,
        String url,
        Integer date,
        @JsonProperty("album_id")
        Integer albumId,
        @JsonProperty("genre_id")
        Integer genreId,
        String performer
) { }
