package sunmisc.vk.client.handle;

public interface FieldAccessor<T> {

    <R> R get(Object o);

    T property();
}
