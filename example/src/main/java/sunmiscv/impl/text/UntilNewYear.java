package sunmiscv.impl.text;

import java.time.Duration;
import java.time.LocalDateTime;

public class UntilNewYear implements Text {

    @Override
    public String asString() {
        LocalDateTime time = LocalDateTime.now();

        int year = time.getYear() + 1;
        Duration duration = Duration.between(time,
                LocalDateTime.of(year,
                        1, 1, 0, 0));

        return new TemporalDuration(duration).asString();
    }
}
