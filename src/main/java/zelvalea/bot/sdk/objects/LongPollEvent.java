package zelvalea.bot.sdk.objects;

import zelvalea.bot.events.Event;

public record LongPollEvent(String type, Event object) { }
