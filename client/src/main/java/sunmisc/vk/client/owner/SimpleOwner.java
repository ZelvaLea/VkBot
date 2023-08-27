package sunmisc.vk.client.owner;

public record SimpleOwner(
        String accessToken,
        int id
) implements Owner {}
