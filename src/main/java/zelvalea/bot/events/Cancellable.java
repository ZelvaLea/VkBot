package zelvalea.bot.events;

public interface Cancellable {
    boolean isCancelled();

    void cancel(boolean flag);
}