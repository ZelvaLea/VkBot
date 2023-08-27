package sunmisc.vk.client.model;

import sunmisc.vk.client.events.Event;

public record LongPollEvent(String type, Event object) { }
