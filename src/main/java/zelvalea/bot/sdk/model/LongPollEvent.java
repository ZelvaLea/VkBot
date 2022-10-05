package zelvalea.bot.sdk.model;

import zelvalea.bot.events.Event;

public record LongPollEvent(String type, Event object) { }
