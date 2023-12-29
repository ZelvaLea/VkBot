package sunmisc.malibu.events;

public interface Events {

    void subscribe(Listener listener) throws Exception;


    void unsubscribe(Listener listener) throws Exception;


    void fire(Event... events);
}
