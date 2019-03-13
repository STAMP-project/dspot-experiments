package water;


import org.junit.Assert;
import org.junit.Test;


public class ScopeTest extends TestUtil {
    @Test
    public void testTrackGeneric() {
        final Key<ScopeTest.DummyKeyed> k = Key.make();
        try {
            Scope.enter();
            DKV.put(Scope.track_generic(new ScopeTest.DummyKeyed(k)));
            Scope.exit();
            Assert.assertNull("DKV value should be automatically removed", DKV.get(k));
        } finally {
            DKV.remove(k);
        }
    }

    private static final class DummyKeyed extends Keyed<ScopeTest.DummyKeyed> {
        public DummyKeyed() {
            super();
        }

        private DummyKeyed(Key<ScopeTest.DummyKeyed> key) {
            super(key);
        }
    }
}

