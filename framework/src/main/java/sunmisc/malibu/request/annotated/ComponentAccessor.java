package sunmisc.malibu.request.annotated;

import java.util.function.Function;

interface ComponentAccessor<R, P>
        extends Function<R, Object> {

    P result();
}
