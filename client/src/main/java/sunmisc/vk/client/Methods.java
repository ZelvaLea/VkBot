package sunmisc.vk.client;

import sunmisc.vk.client.request.annotated.Request;

public interface Methods extends AutoCloseable {


    <R> R send(Request<R> request) throws Exception;
}
