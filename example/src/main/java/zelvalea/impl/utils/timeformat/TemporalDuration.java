package zelvalea.impl.utils.timeformat;


import zelvalea.impl.text.Text;

import java.time.Duration;

public final class TemporalDuration implements Text {
    private static final TimeFormatter[] DEFAULT_FORMAT = {
            TimeFormatter.DAYS,
            TimeFormatter.HOURS,
            TimeFormatter.MINUTES,
    };
    private final Duration duration;

    public TemporalDuration(Duration duration) {
        this.duration = duration;
    }

    public String toFormat(TimeFormatter... timeFormatter) {
        StringBuilder buf = new StringBuilder();
        for(TimeFormatter formatter : timeFormatter) {
            int x = formatter.to(duration);
            if (x == 0) {
                continue;
            }
            buf.append(' ').append(x).append(' ');
            if ((x % 100 / 10) == 1) {
                buf.append(formatter.getPlural());
                continue;
            }

            int y = x % 10;
            if (y == 1) {
                buf.append(formatter.getSingular());
            } else if (y != 0 && y <= 4) {
                buf.append(formatter.getOther());
            } else {
                buf.append(formatter.getPlural());
            }
        }
        return buf.toString();
    }
    @Override
    public String asString() {
        return toFormat(DEFAULT_FORMAT);
    }
    @Override
    public String toString() {
        return toFormat();
    }


}