package sunmisc.vk.client.request;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public interface Request<R> {
    default Type responseType() {
        for (Type t : getClass().getGenericInterfaces()) {
            if (t instanceof ParameterizedType pt &&
                    pt.getRawType() == Request.class)
                return pt.getActualTypeArguments()[0];
        }

        throw new InternalError("response type not found");
    }
}
