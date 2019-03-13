package com.annimon.stream.function;


import Predicate.Util;
import com.annimon.stream.Functions;
import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests {@code Predicate}.
 *
 * @see com.annimon.stream.function.Predicate
 */
public class PredicateTest {
    @Test
    public void testLessThan100() {
        Assert.assertTrue(PredicateTest.lessThan100.test(10));
        Assert.assertFalse(PredicateTest.lessThan100.test(1000));
    }

    @Test
    public void testIsEven() {
        Assert.assertTrue(PredicateTest.isEven.test(54));
        Assert.assertFalse(PredicateTest.isEven.test(55));
    }

    @Test
    public void testAndPredicate() {
        Predicate<Integer> predicate = Util.and(PredicateTest.lessThan100, PredicateTest.isEven);
        Assert.assertTrue(predicate.test(50));
        Assert.assertFalse(predicate.test(55));
        Assert.assertFalse(predicate.test(1002));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testAndMultiplePredicates() {
        Predicate<Integer> predicate = Util.and(new Predicate<Object>() {
            @Override
            public boolean test(Object value) {
                return value != null;
            }
        }, PredicateTest.lessThan100, PredicateTest.isEven, new Predicate<Number>() {
            @Override
            public boolean test(Number value) {
                return (value.intValue()) > 0;
            }
        }, new Predicate<Integer>() {
            @Override
            public boolean test(Integer value) {
                return (50 <= value) && (value <= 60);
            }
        });
        Assert.assertTrue(predicate.test(50));
        Assert.assertFalse(predicate.test(55));
        Assert.assertFalse(predicate.test(null));
        Assert.assertFalse(predicate.test((-56)));
        Assert.assertTrue(predicate.test(60));
        Assert.assertFalse(predicate.test(62));
        Assert.assertFalse(predicate.test(1002));
    }

    @Test
    public void testOrPredicate() {
        Predicate<Integer> predicate = Util.or(PredicateTest.lessThan100, PredicateTest.isEven);
        Assert.assertTrue(predicate.test(50));
        Assert.assertTrue(predicate.test(55));
        Assert.assertTrue(predicate.test(1002));
        Assert.assertFalse(predicate.test(1001));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testOrMultiplePredicates() {
        Predicate<Integer> predicate = Util.or(new Predicate<Object>() {
            @Override
            public boolean test(Object value) {
                return value == null;
            }
        }, PredicateTest.lessThan100, PredicateTest.isEven, new Predicate<Number>() {
            @Override
            public boolean test(Number value) {
                return (value.intValue()) > 2000;
            }
        }, new Predicate<Integer>() {
            @Override
            public boolean test(Integer value) {
                return (50 <= value) && (value <= 60);
            }
        });
        Assert.assertTrue(predicate.test(50));
        Assert.assertTrue(predicate.test(55));
        Assert.assertTrue(predicate.test((-56)));
        Assert.assertTrue(predicate.test(60));
        Assert.assertTrue(predicate.test(1002));
        Assert.assertTrue(predicate.test(null));
        Assert.assertFalse(predicate.test(1001));
        Assert.assertFalse(predicate.test(1001));
        Assert.assertTrue(predicate.test(2001));
    }

    @Test
    public void testXorPredicate() {
        Predicate<Integer> predicate = Util.xor(PredicateTest.lessThan100, PredicateTest.isEven);
        Assert.assertFalse(predicate.test(50));
        Assert.assertTrue(predicate.test(55));
        Assert.assertTrue(predicate.test(1002));
        Assert.assertFalse(predicate.test(1001));
    }

    @Test
    public void testNegatePredicate() {
        Predicate<Integer> isOdd = Util.negate(PredicateTest.isEven);
        Assert.assertTrue(isOdd.test(55));
        Assert.assertFalse(isOdd.test(56));
    }

    @Test
    public void testNotNullPredicate() {
        Predicate<Object> predicate = Util.notNull();
        Assert.assertFalse(predicate.test(null));
        Assert.assertTrue(predicate.test(new Object()));
    }

    @Test
    public void testSafe() {
        Predicate<Integer> predicate = Util.safe(PredicateTest.throwablePredicate);
        Assert.assertTrue(predicate.test(40));
        Assert.assertFalse(predicate.test(60));
        Assert.assertFalse(predicate.test((-5)));
    }

    @Test
    public void testSafeWithResultIfFailed() {
        Predicate<Integer> predicate = Util.safe(PredicateTest.throwablePredicate, true);
        Assert.assertTrue(predicate.test(40));
        Assert.assertFalse(predicate.test(60));
        Assert.assertTrue(predicate.test((-5)));
    }

    @Test
    public void testPrivateConstructor() throws Exception {
        Assert.assertThat(Util.class, hasOnlyPrivateConstructors());
    }

    private static final Predicate<Integer> lessThan100 = new Predicate<Integer>() {
        @Override
        public boolean test(Integer value) {
            return value < 100;
        }
    };

    private static final Predicate<Integer> isEven = Functions.remainder(2);

    private static final ThrowablePredicate<Integer, Throwable> throwablePredicate = new ThrowablePredicate<Integer, Throwable>() {
        @Override
        public boolean test(Integer value) throws Throwable {
            if (value < 0) {
                throw new IOException();
            }
            return value < 50;
        }
    };
}

