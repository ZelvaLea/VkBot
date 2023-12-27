package sunmiscv.impl;

import sunmiscv.impl.text.Concat;
import sunmiscv.impl.text.Text;
import sunmiscv.impl.text.TemporalDuration;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Iterator;

public class CyclicStatusIterator implements Iterator<Text> {

    @Override
    public boolean hasNext() {
        return true;
    }

    @Override
    public Text next() {
        LocalDateTime time = LocalDateTime.now();

        int year = time.getYear() + 1;
        Duration duration = Duration.between(time,
                LocalDateTime.of(year, 1, 1, 0, 0));

        return new Concat(
                () -> "\uD83D\uDC7BДо Нового Года осталось: ",
                new TemporalDuration(duration),
                () -> "\uD83D\uDC7B"

        );
    }
}
