package sunmisc.vk.client.events;

public interface Cancellable {
    boolean isCancelled();

    void cancel(boolean flag);
}