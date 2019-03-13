package com.annimon.stream.function;


import IndexedIntPredicate.Util;
import com.annimon.stream.Functions;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests {@code IndexedIntPredicate}.
 *
 * @see IndexedIntPredicate
 */
public class IndexedIntPredicateTest {
    @Test
    public void testPrivateConstructor() throws Exception {
        Assert.assertThat(Util.class, hasOnlyPrivateConstructors());
    }

    @Test
    public void testEven() {
        Assert.assertTrue(IndexedIntPredicateTest.areIndexAndValueEven.test(10, 10));
        Assert.assertFalse(IndexedIntPredicateTest.areIndexAndValueEven.test(5, 10));
        Assert.assertFalse(IndexedIntPredicateTest.areIndexAndValueEven.test(5, 5));
    }

    @Test
    public void testWrap() {
        IndexedIntPredicate predicate = Util.wrap(Functions.remainderInt(2));
        Assert.assertTrue(predicate.test(0, 50));
        Assert.assertFalse(predicate.test(2, 55));
        Assert.assertFalse(predicate.test(9, 9));
    }

    private static final IndexedIntPredicate areIndexAndValueEven = new IndexedIntPredicate() {
        @Override
        public boolean test(int index, int value) {
            return ((index % 2) == 0) && ((value % 2) == 0);
        }
    };
}

