package sunmiscv.impl;

import sunmiscv.impl.text.Concat;
import sunmiscv.impl.text.Text;
import sunmiscv.impl.text.UntilNewYear;

import java.util.Iterator;
import java.util.concurrent.ThreadLocalRandom;

public class CyclicStatusIterator implements Iterator<Text> {

    @Override
    public boolean hasNext() {
        return true;
    }

    @Override
    public Text next() {
        boolean suck = ThreadLocalRandom
                .current()
                .nextInt(0, 100) > 75;

        return suck ?
                () -> "Let’s celebrate and suck some dick"
                : new Concat(
                () -> "\uD83D\uDC7BДо Нового Года осталось: ",
                new UntilNewYear(),
                () -> "\uD83D\uDC7B");
    }

}
