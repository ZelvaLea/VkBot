package sunmisc.malibu.events;

public interface Cancellable {

    boolean cancelled();

    void cancel();
}