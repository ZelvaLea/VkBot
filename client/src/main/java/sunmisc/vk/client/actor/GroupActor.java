package sunmisc.vk.client.actor;

public record GroupActor(
        String accessToken,
        int id
) implements Actor {}
