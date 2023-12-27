package sunmisc.vk.client.request;


import java.util.stream.Stream;

@FunctionalInterface
public interface Head {

    Stream<Header> headers();

    record Header(String name, String value) { }


    interface VkHead extends Head {

        @Override
        default Stream<Header> headers() {
            return Stream.of(
                    new Header("User-Agent", "VKAndroidApp/7.35")
            );
        }
    }
}
