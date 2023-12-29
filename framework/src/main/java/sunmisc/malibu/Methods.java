package sunmisc.malibu;

import sunmisc.malibu.request.annotated.Request;

public interface Methods extends AutoCloseable {


    <R> R invoke(Request<R> request) throws Exception;
}
