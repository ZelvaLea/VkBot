package sunmiscv.impl.text;

import java.time.Duration;

public enum TimeFormatter {
    DAYS("дней", "день", "дня") {
        @Override
        public int to(Duration d) {
            return (int)d.toDaysPart();
        }
    },
    HOURS("часов", "час", "часа") {
        @Override
        public int to(Duration d) {
            return d.toHoursPart();
        }
    },
    MINUTES("минут", "минута", "минуты") {
        @Override
        public int to(Duration d) {
            return d.toMinutesPart();
        }
    },
    SECONDS("секунд", "секунда", "секунды") {
        @Override
        public int to(Duration d) {
            return d.toSecondsPart();
        }
    };

    private final String plural, singular, other;

    TimeFormatter(String plural, String singular, String other) {
        this.plural = plural;
        this.singular = singular;
        this.other = other;
    }

    public String getPlural() {
        return plural;
    }

    public String getSingular() {
        return singular;
    }

    public String getOther() {
        return other;
    }
    public abstract int to(Duration d);
}