package sunmisc.malibu.longpoll;

public interface Exit {

    Exit NEVER = () -> false;

    boolean ready();
}
