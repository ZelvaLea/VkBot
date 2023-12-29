package sunmisc.malibu.model.audio;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ListAudios(int count, List<AudioItem> items) { }
