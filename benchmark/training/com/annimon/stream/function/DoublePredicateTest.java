package com.annimon.stream.function;


import DoublePredicate.Util;
import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests {@code DoublePredicate}.
 */
public class DoublePredicateTest {
    @Test
    public void testLessThanPI() {
        Assert.assertTrue(DoublePredicateTest.lessThanPI.test(2.01));
        Assert.assertFalse(DoublePredicateTest.lessThanPI.test(6.0));
    }

    @Test
    public void testIsCloseToPI() {
        Assert.assertTrue(DoublePredicateTest.isCloseToPI.test(3.14159));
        Assert.assertFalse(DoublePredicateTest.isCloseToPI.test(3.14));
    }

    @Test
    public void testAndPredicate() {
        DoublePredicate predicate = Util.and(DoublePredicateTest.lessThanPI, DoublePredicateTest.isCloseToPI);
        Assert.assertTrue(predicate.test(3.1415));
        Assert.assertFalse(predicate.test(3.14));
        Assert.assertFalse(predicate.test(3.16));
    }

    @Test
    public void testOrPredicate() {
        DoublePredicate predicate = Util.or(DoublePredicateTest.lessThanPI, DoublePredicateTest.isCloseToPI);
        Assert.assertTrue(predicate.test(0));
        Assert.assertTrue(predicate.test(3.1415));
        Assert.assertTrue(predicate.test(3.1416));
        Assert.assertFalse(predicate.test(10.01));
    }

    @Test
    public void testXorPredicate() {
        DoublePredicate predicate = Util.xor(DoublePredicateTest.lessThanPI, DoublePredicateTest.isCloseToPI);
        Assert.assertFalse(predicate.test(3.1415));
        Assert.assertTrue(predicate.test(2));
        Assert.assertTrue(predicate.test(3.1416));
        Assert.assertFalse(predicate.test(10.01));
    }

    @Test
    public void testNegatePredicate() {
        DoublePredicate greaterOrEqualsThanPI = Util.negate(DoublePredicateTest.lessThanPI);
        Assert.assertTrue(greaterOrEqualsThanPI.test(Math.PI));
        Assert.assertTrue(greaterOrEqualsThanPI.test(8.8005353535));
        Assert.assertFalse(greaterOrEqualsThanPI.test(2.11));
    }

    @Test
    public void testSafe() {
        DoublePredicate predicate = Util.safe(new DoublePredicateTest.UnsafePredicate());
        Assert.assertTrue(predicate.test(40.0));
        Assert.assertFalse(predicate.test(3.0));
        Assert.assertFalse(predicate.test((-5.0)));
    }

    @Test
    public void testSafeWithResultIfFailed() {
        DoublePredicate predicate = Util.safe(new DoublePredicateTest.UnsafePredicate(), true);
        Assert.assertTrue(predicate.test(40.0));
        Assert.assertFalse(predicate.test(3.0));
        Assert.assertTrue(predicate.test((-5.0)));
    }

    @Test
    public void testPrivateConstructor() throws Exception {
        Assert.assertThat(Util.class, hasOnlyPrivateConstructors());
    }

    private static final DoublePredicate lessThanPI = new DoublePredicate() {
        @Override
        public boolean test(double value) {
            return value < (Math.PI);
        }
    };

    private static final DoublePredicate isCloseToPI = new DoublePredicate() {
        @Override
        public boolean test(double value) {
            return (Math.abs(((Math.PI) - value))) < 1.0E-4;
        }
    };

    private static class UnsafePredicate implements ThrowableDoublePredicate<Throwable> {
        @Override
        public boolean test(double value) throws IOException {
            if (value < 0) {
                throw new IOException();
            }
            return value > (Math.PI);
        }
    }
}

