package water;


import H2O.SELF;
import java.util.ArrayList;
import java.util.Map;
import junit.framework.TestCase;
import org.junit.Test;
import water.util.IcedDouble;
import water.util.IcedInt;


public class KeySnapshotTest extends TestUtil {
    public KeySnapshotTest() {
        super(5);
    }

    @Test
    public void testGlobalKeySet() {
        ArrayList<Key> madeKeys = new ArrayList<>();
        try {
            Futures fs = new Futures();
            for (int i = 0; i < 100; ++i) {
                Key k = Key.make(("key" + i));
                madeKeys.add(k);
                DKV.put(k, new IcedInt(i), fs, true);
            }
            for (int i = 0; i < 100; ++i) {
                Key k = Key.make(SELF);
                madeKeys.add(k);
                DKV.put(k, new IcedInt(i), fs, true);
            }
            fs.blockForPending();
            Key[] keys = KeySnapshot.globalSnapshot().keys();
            TestCase.assertEquals(100, keys.length);
        } finally {
            for (Key k : madeKeys)
                DKV.remove(k);

        }
    }

    @Test
    public void testLocalKeySet() {
        ArrayList<Key> madeKeys = new ArrayList<>();
        int homeKeys = 0;
        Futures fs = new Futures();
        try {
            for (int i = 0; i < 200; ++i) {
                Key k = Key.make(("key" + i));
                madeKeys.add(k);
                DKV.put(k, new IcedInt(i), fs, true);
                if (k.home())
                    ++homeKeys;

                k = Key.make(SELF);
                madeKeys.add(k);
                DKV.put(k, new IcedInt(i), fs, true);
            }
            fs.blockForPending();
            Key[] keys = KeySnapshot.localSnapshot().keys();
            TestCase.assertEquals(homeKeys, keys.length);
            for (Key k : keys)
                TestCase.assertTrue(k.home());

        } finally {
            for (Key k : madeKeys)
                DKV.remove(k);

        }
    }

    @Test
    public void testFetchAll() {
        Key[] userKeys = new Key[200];
        Key[] systemKeys = new Key[200];
        Futures fs = new Futures();
        try {
            for (int i = 0; i < ((userKeys.length) >> 1); ++i) {
                DKV.put((userKeys[i] = Key.make(("key" + i))), new IcedInt(i), fs, false);
                systemKeys[i] = Key.make(SELF);
                DKV.put(systemKeys[i], new Value(systemKeys[i], new IcedInt(i)));
            }
            for (int i = (userKeys.length) >> 1; i < (userKeys.length); ++i) {
                DKV.put((userKeys[i] = Key.make(("key" + i))), new IcedDouble(i), fs, false);
                systemKeys[i] = Key.make(SELF);
                DKV.put(systemKeys[i], new Value(systemKeys[i], new IcedDouble(i)));
            }
            fs.blockForPending();
            KeySnapshot s = KeySnapshot.globalSnapshot();
            Map<String, Iced> all = s.fetchAll(Iced.class, true);
            TestCase.assertTrue(all.isEmpty());
            all = s.fetchAll(Iced.class);
            TestCase.assertEquals(userKeys.length, all.size());
            Map<String, IcedInt> ints = s.fetchAll(IcedInt.class);
            Map<String, IcedDouble> doubles = s.fetchAll(IcedDouble.class);
            TestCase.assertEquals(((userKeys.length) >> 1), ints.size());
            TestCase.assertEquals(((userKeys.length) >> 1), doubles.size());
        } finally {
            for (int i = 0; i < (userKeys.length); ++i) {
                if ((userKeys[i]) != null)
                    DKV.remove(userKeys[i]);

                if ((systemKeys[i]) != null)
                    DKV.remove(systemKeys[i]);

            }
        }
    }
}

