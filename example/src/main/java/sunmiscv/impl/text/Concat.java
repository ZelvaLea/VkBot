package sunmiscv.impl.text;

import java.util.Arrays;
import java.util.stream.Collectors;

public class Concat implements Text {

    private final Text[] texts;

    public Concat(Text... texts) {
        this.texts = texts;
    }


    @Override
    public String asString() {

        return Arrays.stream(texts).map(x -> {
            try {
                return x.asString();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }).collect(Collectors.joining());
    }
}
