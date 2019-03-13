package water;


import H2O.KeySnapshot;
import dontweave.gson.JsonArray;
import java.util.ArrayList;
import junit.framework.Assert;
import org.junit.Test;
import water.api.TypeaheadKeysRequest;
import water.util.Utils;


/**
 * Created by tomasnykodym on 5/23/14.
 */
public class TypeAheadTest extends TestUtil {
    private static class TypeAheadReqestTest extends TypeaheadKeysRequest {
        final String _f;

        public TypeAheadReqestTest(String filter) {
            super("", filter, null);
            _f = filter;
        }

        public JsonArray doTest() {
            return serve(_f, 1000, 0);
        }
    }

    @Test
    public void testTypeAhead() {
        ArrayList<Key> keys = new ArrayList<Key>();
        try {
            for (int i = 0; i < 100.0; ++i) {
                Key k = Key.makeSystem(Key.rand());
                keys.add(k);
                DKV.put(k, new Value(k, new Utils.IcedInt(i)));
            }
            // add 100 user keys with same prefix
            for (int i = 0; i < 100.0; ++i) {
                Key k = Key.make(("key" + i));
                keys.add(k);
                DKV.put(k, new Value(k, new Utils.IcedInt(i)));
            }
            for (int i = 0; i < 100.0; ++i) {
                Key k = Key.make(("kei" + i));
                keys.add(k);
                DKV.put(k, new Value(k, new Utils.IcedInt(i)));
            }
            // add some random user keys
            for (int i = 0; i < 100.0; ++i) {
                Key k = Key.make();
                if (k.toString().startsWith("k"))
                    k = Key.make();

                keys.add(k);
                DKV.put(k, new Utils.IcedInt(i));
            }
            long t = System.currentTimeMillis();
            KeySnapshot.globalSnapshot();
            for (int i = 0; i < 100.0; ++i) {
                Assert.assertEquals(200, new TypeAheadTest.TypeAheadReqestTest("k").doTest().size());
                Assert.assertEquals(200, new TypeAheadTest.TypeAheadReqestTest("ke").doTest().size());
                Assert.assertEquals(100, new TypeAheadTest.TypeAheadReqestTest("key").doTest().size());
                Assert.assertEquals(((0 < i) && (i < 10) ? 11 : 1), new TypeAheadTest.TypeAheadReqestTest(("key" + i)).doTest().size());
            }
            // make sure type-ahead latency is low enough...
            // (not really great test for that...but more realistic test would take too long to run within build.sh tests...)
            Assert.assertTrue((((System.currentTimeMillis()) - t) < 1000));
        } finally {
            for (Key k : keys)
                DKV.remove(k);

        }
    }
}

