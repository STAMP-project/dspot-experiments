package water;


import java.util.Arrays;
import org.junit.Test;


public class AtomicTest extends TestUtil {
    // Simple wrapper class defining an array-of-keys that is serializable.
    private static class Ary extends Iced {
        public final Key[] _keys;

        Ary(Key[] keys) {
            _keys = keys;
        }
    }

    private static class Append {
        private static void append(Key keys, final Key k) {
            new Atomic() {
                @Override
                public Value atomic(Value val) {
                    AtomicTest.Ary ks = (val == null) ? new AtomicTest.Ary(new Key[0]) : ((AtomicTest.Ary) (val.get()));
                    Key[] keys = Arrays.copyOf(ks._keys, ((ks._keys.length) + 1));
                    keys[((keys.length) - 1)] = k;
                    return new Value(_key, new AtomicTest.Ary(keys));
                }
            }.invoke(keys);
        }
    }

    @Test
    public void testBasic() {
        doBasic(makeKey("basic", false));
    }

    @Test
    public void testBasicRemote() {
        doBasic(makeKey("basicRemote", true));
    }

    @Test
    public void testLarge() {
        doLarge(makeKey("large", false));
    }

    @Test
    public void testLargeRemote() {
        doLarge(makeKey("largeRemote", true));
    }
}

