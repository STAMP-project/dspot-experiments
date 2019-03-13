package water.nbhm;


import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import junit.framework.TestCase;


public class NonBlockingHashMapLongZeroKeyConcurrentGetPutIfAbsentTest extends TestCase {
    private static final int N_THREADS = 4;

    private static final int ITERATIONS = 100000;

    private ExecutorService service;

    public void test_empty_map_key_0() throws Exception {
        empty_map_test(0);
    }

    public void test_empty_map_key_123() throws Exception {
        empty_map_test(123);
    }

    private static class PutIfAbsent implements Callable<String> {
        private final NonBlockingHashMapLong<String> map;

        private final String newValue;

        private final long key;

        public PutIfAbsent(NonBlockingHashMapLong<String> map, long key, String newValue) {
            this.map = map;
            this.newValue = newValue;
            this.key = key;
        }

        @Override
        public String call() throws Exception {
            String value = map.get(key);
            if (value == null) {
                value = newValue;
                String tmp = map.putIfAbsent(key, value);
                if (tmp != null) {
                    return tmp;
                }
            }
            return value;
        }
    }
}

