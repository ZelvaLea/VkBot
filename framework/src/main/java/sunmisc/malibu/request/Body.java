package sunmisc.malibu.request;

import java.util.Optional;

@FunctionalInterface
public interface Body {

    Optional<byte[]> body() throws Exception;


    class ConcatBody implements Body {

        private final Body left, right;

        public ConcatBody(Body left, Body right) {
            this.left = left;
            this.right = right;
        }

        @Override
        public Optional<byte[]> body() throws Exception {
            return left.body().flatMap(array1 -> {
                try {
                    return right.body().map(array2 -> {
                        int a = array1.length, b = array2.length;
                        byte[] alloc = new byte[a + b];

                        System.arraycopy(array1, 0, alloc, 0, a);
                        System.arraycopy(array2, 0, alloc, a, b);

                        return alloc;
                    });
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
        }
    }
}
