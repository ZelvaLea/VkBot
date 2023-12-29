package sunmisc.malibu.request;


import java.util.stream.Stream;

@FunctionalInterface
public interface Head {

    Stream<Header> headers();

    record Header(String name, String value) { }


}
