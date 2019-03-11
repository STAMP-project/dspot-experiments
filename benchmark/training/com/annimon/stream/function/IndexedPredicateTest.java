package com.annimon.stream.function;


import IndexedPredicate.Util;
import com.annimon.stream.Functions;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests {@code IndexedPredicate}.
 *
 * @see com.annimon.stream.function.IndexedPredicate
 */
public class IndexedPredicateTest {
    @Test
    public void testPrivateConstructor() throws Exception {
        Assert.assertThat(Util.class, hasOnlyPrivateConstructors());
    }

    @Test
    public void testEven() {
        Assert.assertTrue(IndexedPredicateTest.areIndexAndValueEven.test(10, 10));
        Assert.assertFalse(IndexedPredicateTest.areIndexAndValueEven.test(5, 10));
        Assert.assertFalse(IndexedPredicateTest.areIndexAndValueEven.test(5, 5));
    }

    @Test
    public void testWrap() {
        IndexedPredicate<Integer> predicate = Util.wrap(Functions.remainder(2));
        Assert.assertTrue(predicate.test(0, 50));
        Assert.assertFalse(predicate.test(2, 55));
        Assert.assertFalse(predicate.test(9, 9));
    }

    private static final IndexedPredicate<Integer> areIndexAndValueEven = new IndexedPredicate<Integer>() {
        @Override
        public boolean test(int index, Integer value) {
            return ((index % 2) == 0) && ((value % 2) == 0);
        }
    };
}

