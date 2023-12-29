package sunmisc.malibu;

import sunmisc.malibu.request.Input;
import sunmisc.malibu.response.Response;

public interface Wire {

    Response get(Input request) throws Exception;

    Response post(Input request) throws Exception;

    Response delete(Input request) throws Exception;

    Response patch(Input request) throws Exception;


}
