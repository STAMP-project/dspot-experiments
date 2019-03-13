package dev.morphia;


import dev.morphia.testutil.TestEntity;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Uwe Schaefer, (us@thomas-daily.de)
 */
public class TestGetByKeys extends TestBase {
    @Test
    public final void testGetByKeys() {
        final TestGetByKeys.A a1 = new TestGetByKeys.A();
        final TestGetByKeys.A a2 = new TestGetByKeys.A();
        final Iterable<Key<TestGetByKeys.A>> keys = getDs().save(Arrays.asList(a1, a2));
        final List<TestGetByKeys.A> reloaded = getDs().getByKeys(keys);
        final Iterator<TestGetByKeys.A> i = reloaded.iterator();
        Assert.assertNotNull(i.next());
        Assert.assertNotNull(i.next());
        Assert.assertFalse(i.hasNext());
    }

    public static class A extends TestEntity {
        private String foo = "bar";
    }
}

