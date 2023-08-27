package zelvalea.impl.utils.timeformat;


import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

public final class TemporalDuration {
    private static final TimeFormatter[] DEFAULT_FORMAT = {
            TimeFormatter.DAYS,
            TimeFormatter.HOURS,
            TimeFormatter.MINUTES,
    };

    private Duration duration;

    public TemporalDuration(Duration duration) {
        if(duration == null) {
            return;
        }
        this.duration = duration;
    }
    public static TemporalDuration of(int month, int day, int hour, int minute) {
        LocalDateTime startInclusive = LocalDateTime.now();
        LocalDateTime endExclusive = LocalDateTime.of(startInclusive.getYear(), month, day, hour, minute);
        if(startInclusive.toEpochSecond(ZoneOffset.UTC) > endExclusive.toEpochSecond(ZoneOffset.UTC)) {
            endExclusive = endExclusive.plusYears(1);
        }
        return new TemporalDuration(Duration.between(startInclusive, endExclusive));
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
    public String toFormat() {
        return toFormat(DEFAULT_FORMAT);
    }
    @Override
    public String toString() {
        return toFormat();
    }
}