package com.annimon.stream.function;


import IndexedDoublePredicate.Util;
import com.annimon.stream.Functions;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests {@code IndexedDoublePredicate}.
 *
 * @see IndexedDoublePredicate
 */
public class IndexedDoublePredicateTest {
    @Test
    public void testPrivateConstructor() throws Exception {
        Assert.assertThat(Util.class, hasOnlyPrivateConstructors());
    }

    @Test
    public void testEven() {
        Assert.assertTrue(IndexedDoublePredicateTest.areIndexAndValueGreaterThan20.test(30, 40.0));
        Assert.assertFalse(IndexedDoublePredicateTest.areIndexAndValueGreaterThan20.test(0, 2.0));
        Assert.assertFalse(IndexedDoublePredicateTest.areIndexAndValueGreaterThan20.test(20, 20.0));
    }

    @Test
    public void testWrap() {
        IndexedDoublePredicate predicate = Util.wrap(IndexedDoublePredicateTest.greaterThan20);
        Assert.assertTrue(predicate.test(42, 30.0));
        Assert.assertFalse(predicate.test(19, 18.0));
        Assert.assertFalse(predicate.test(20, 20.0));
    }

    private static final DoublePredicate greaterThan20 = Functions.greaterThan(20.0);

    private static final IndexedDoublePredicate areIndexAndValueGreaterThan20 = new IndexedDoublePredicate() {
        @Override
        public boolean test(int index, double value) {
            return (IndexedDoublePredicateTest.greaterThan20.test(index)) && (IndexedDoublePredicateTest.greaterThan20.test(value));
        }
    };
}

