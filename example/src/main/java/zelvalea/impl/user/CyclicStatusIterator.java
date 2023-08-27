package zelvalea.impl.user;

import java.util.Iterator;

public class CyclicStatusIterator implements Iterator<String> {
    private static final String[] statuses = {
            "В одиночестве на кеш-лайн",
            "Ocean side suicide"
    };
    private int cursor;

    @Override
    public boolean hasNext() {
        return true;
    }

    @Override
    public String next() {
        int len = statuses.length;
        return statuses[cursor++ % len];
    }
}
