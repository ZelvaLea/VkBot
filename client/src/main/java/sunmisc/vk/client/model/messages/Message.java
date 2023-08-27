package sunmisc.vk.client.model.messages;

public record Message(
        int id,
        int date,
        int peer_id,
        int from_id,
        String text
) { }