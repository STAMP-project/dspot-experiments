package com.annimon.stream.function;


import IndexedLongPredicate.Util;
import com.annimon.stream.Functions;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests {@code IndexedLongPredicate}.
 *
 * @see IndexedLongPredicate
 */
public class IndexedLongPredicateTest {
    @Test
    public void testPrivateConstructor() throws Exception {
        Assert.assertThat(Util.class, hasOnlyPrivateConstructors());
    }

    @Test
    public void testEven() {
        Assert.assertTrue(IndexedLongPredicateTest.areIndexAndValueEven.test(10, 10L));
        Assert.assertFalse(IndexedLongPredicateTest.areIndexAndValueEven.test(5, 10L));
        Assert.assertFalse(IndexedLongPredicateTest.areIndexAndValueEven.test(5, 5L));
    }

    @Test
    public void testWrap() {
        IndexedLongPredicate predicate = Util.wrap(Functions.remainderLong(2));
        Assert.assertTrue(predicate.test(0, 50L));
        Assert.assertFalse(predicate.test(2, 55L));
        Assert.assertFalse(predicate.test(9, 9L));
    }

    private static final IndexedLongPredicate areIndexAndValueEven = new IndexedLongPredicate() {
        @Override
        public boolean test(int index, long value) {
            return ((index % 2) == 0) && ((value % 2) == 0);
        }
    };
}

