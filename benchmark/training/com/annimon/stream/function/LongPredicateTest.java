package com.annimon.stream.function;


import LongPredicate.Util;
import com.annimon.stream.Functions;
import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests {@code LongPredicate}.
 */
public class LongPredicateTest {
    @Test
    public void testLessThan100() {
        Assert.assertTrue(LongPredicateTest.lessThan100.test(10L));
        Assert.assertFalse(LongPredicateTest.lessThan100.test(1000L));
    }

    @Test
    public void testIsEven() {
        Assert.assertTrue(LongPredicateTest.isEven.test(10000000054L));
        Assert.assertFalse(LongPredicateTest.isEven.test(10000000055L));
    }

    @Test
    public void testAndPredicate() {
        LongPredicate predicate = Util.and(LongPredicateTest.lessThan100, LongPredicateTest.isEven);
        Assert.assertTrue(predicate.test(50));
        Assert.assertFalse(predicate.test(55));
        Assert.assertFalse(predicate.test(1002));
    }

    @Test
    public void testOrPredicate() {
        LongPredicate predicate = Util.or(LongPredicateTest.lessThan100, LongPredicateTest.isEven);
        Assert.assertTrue(predicate.test(50));
        Assert.assertTrue(predicate.test(55));
        Assert.assertTrue(predicate.test(1002));
        Assert.assertFalse(predicate.test(1001));
    }

    @Test
    public void testXorPredicate() {
        LongPredicate predicate = Util.xor(LongPredicateTest.lessThan100, LongPredicateTest.isEven);
        Assert.assertFalse(predicate.test(50));
        Assert.assertTrue(predicate.test(55));
        Assert.assertTrue(predicate.test(1002));
        Assert.assertFalse(predicate.test(1001));
    }

    @Test
    public void testNegatePredicate() {
        LongPredicate isOdd = Util.negate(LongPredicateTest.isEven);
        Assert.assertTrue(isOdd.test(55));
        Assert.assertFalse(isOdd.test(56));
    }

    @Test
    public void testSafe() {
        LongPredicate predicate = Util.safe(new LongPredicateTest.UnsafePredicate());
        Assert.assertTrue(predicate.test(40L));
        Assert.assertFalse(predicate.test(15L));
        Assert.assertFalse(predicate.test((-5L)));
    }

    @Test
    public void testSafeWithResultIfFailed() {
        LongPredicate predicate = Util.safe(new LongPredicateTest.UnsafePredicate(), true);
        Assert.assertTrue(predicate.test(40L));
        Assert.assertFalse(predicate.test(15L));
        Assert.assertTrue(predicate.test((-5L)));
    }

    @Test
    public void testPrivateConstructor() throws Exception {
        Assert.assertThat(Util.class, hasOnlyPrivateConstructors());
    }

    private static final LongPredicate isEven = Functions.remainderLong(2);

    private static final LongPredicate lessThan100 = new LongPredicate() {
        @Override
        public boolean test(long value) {
            return value < 100;
        }
    };

    private static class UnsafePredicate implements ThrowableLongPredicate<Throwable> {
        @Override
        public boolean test(long value) throws IOException {
            if (value < 0) {
                throw new IOException();
            }
            return (value % 2) == 0;
        }
    }
}

