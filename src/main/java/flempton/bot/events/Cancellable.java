package flempton.bot.events;

public interface Cancellable {
    boolean isCancelled();

    void cancel(boolean flag);
}