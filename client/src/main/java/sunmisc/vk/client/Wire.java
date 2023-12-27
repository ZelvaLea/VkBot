package sunmisc.vk.client;

import sunmisc.vk.client.request.Input;
import sunmisc.vk.client.response.Response;

public interface Wire {

    Response patch(Input request) throws Exception;

    Response post(Input request) throws Exception;

    Response delete(Input request) throws Exception;

    Response get(Input request) throws Exception;


}
