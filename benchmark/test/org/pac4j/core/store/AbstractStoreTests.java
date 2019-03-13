package org.pac4j.core.store;


import org.junit.Assert;
import org.junit.Test;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.util.Executable;
import org.pac4j.core.util.TestsConstants;
import org.pac4j.core.util.TestsHelper;


/**
 * Test a store.
 *
 * @author Jerome Leleu
 * @since 2.0.0
 */
public abstract class AbstractStoreTests<S extends Store> implements TestsConstants {
    @Test
    public void testSetRemoveGet() {
        final S store = buildStore();
        store.set(TestsConstants.KEY, TestsConstants.VALUE);
        Assert.assertEquals(TestsConstants.VALUE, store.get(TestsConstants.KEY).get());
        store.remove(TestsConstants.KEY);
        Assert.assertFalse(store.get(TestsConstants.KEY).isPresent());
    }

    @Test
    public void testSetExpiredGet() {
        final S store = buildStore();
        store.set(TestsConstants.KEY, TestsConstants.VALUE);
        Assert.assertEquals(TestsConstants.VALUE, store.get(TestsConstants.KEY).get());
        try {
            Thread.sleep(2000);
        } catch (final InterruptedException e) {
            throw new RuntimeException(e);
        }
        Assert.assertFalse(store.get(TestsConstants.KEY).isPresent());
    }

    @Test
    public void testSetNullValue() {
        final S store = buildStore();
        store.set(TestsConstants.KEY, TestsConstants.VALUE);
        Assert.assertEquals(TestsConstants.VALUE, store.get(TestsConstants.KEY).get());
        store.set(TestsConstants.KEY, null);
        Assert.assertFalse(store.get(TestsConstants.KEY).isPresent());
    }

    @Test
    public void testMissingObject() {
        final S store = buildStore();
        Assert.assertFalse(store.get(TestsConstants.KEY).isPresent());
    }

    @Test
    public void testNullKeyGet() {
        final S store = buildStore();
        TestsHelper.expectException(() -> get(null), TechnicalException.class, "key cannot be null");
    }

    @Test
    public void testNullKeySet() {
        final S store = buildStore();
        TestsHelper.expectException(() -> set(null, TestsConstants.VALUE), TechnicalException.class, "key cannot be null");
    }

    @Test
    public void testNullKeyRemove() {
        final S store = buildStore();
        TestsHelper.expectException(() -> remove(null), TechnicalException.class, "key cannot be null");
    }
}

