package water;


import H2O.CLOUD;
import Key.HIDDEN_USER_KEY;
import org.junit.Test;
import water.H2O.H2OCountedCompleter;
import water.util.IcedInt;
import water.util.IcedInt.AtomicIncrementAndGet;

import static H2O.SELF;


/**
 * Created by tomasnykodym on 10/5/15.
 *
 * Test that our DKV follows java mm.
 */
public class DKVTest extends TestUtil {
    private static class TestMM extends MRTask<DKVTest.TestMM> {
        final Key[] _keys;

        public TestMM(Key[] keys) {
            _keys = keys;
        }

        @Override
        public void setupLocal() {
            final H2OCountedCompleter barrier = new H2OCountedCompleter() {
                @Override
                public void compute2() {
                }
            };
            barrier.addToPendingCount(((_keys.length) - 1));
            for (Key k : _keys)
                DKV.get(k);

            for (int i = 0; i < (_keys.length); ++i) {
                final Key fk = _keys[i];
                new AtomicIncrementAndGet(new water.H2O.H2OCallback<AtomicIncrementAndGet>(barrier) {
                    @Override
                    public void callback(AtomicIncrementAndGet f) {
                        final int lb = f._val;
                        for (H2ONode node : CLOUD._memary) {
                            if (node != (SELF)) {
                                barrier.addToPendingCount(1);
                                new RPC(node, new DTask() {
                                    @Override
                                    public void compute2() {
                                        IcedInt val = DKV.getGet(fk);
                                        if ((val._val) < lb)
                                            throw new IllegalArgumentException(((("DKV seems to be in inconsistent state after TAtomic, value lower than expected, expected at least " + lb) + ", got ") + (val._val)));

                                        tryComplete();
                                    }
                                }).addCompleter(barrier).call();
                            }
                        }
                    }
                }).fork(_keys[i]);
            }
            barrier.join();
        }
    }

    /**
     * Test for recently discovered bug in DKV where updates sometimes failed to wait for all invalidates to the given key.
     *
     * Test that DKV puts (Tatomic updates) are globally visible after the tatomic task gets back.
     *
     * Makes a Key per node, caches it on all other nodes and then performs atomic updates followed by global visibility check.
     * Fails if the update is not globally visible.
     */
    @Test
    public void testTatomic() {
        try {
            final Key[] keys = new Key[CLOUD.size()];
            for (int r = 0; r < 20; ++r) {
                System.out.println(("iteration " + r));
                try {
                    // byte rf, byte systemType, boolean hint, H2ONode... replicas
                    for (int i = 0; i < (keys.length); ++i)
                        DKV.put((keys[i] = Key.make(((byte) (1)), HIDDEN_USER_KEY, true, CLOUD._memary[i])), new IcedInt(0));

                    doAllNodes();
                } finally {
                    for (Key k : keys)
                        if (k != null)
                            DKV.remove(k);


                }
            }
        } finally {
            // H2O.orderlyShutdown();
        }
    }

    class Bytes extends Iced<DKVTest.Bytes> {
        public byte[] _b;

        Bytes(byte[] b) {
            _b = b;
        }
    }
}

