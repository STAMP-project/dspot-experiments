package com.annimon.stream.function;


import IntPredicate.Util;
import com.annimon.stream.IntStream;
import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests {@link IntPredicate}
 */
public class IntPredicateTest {
    private static final IntPredicate alwaysTrue = new IntPredicate() {
        @Override
        public boolean test(int value) {
            return true;
        }
    };

    private static final IntPredicate alwaysFalse = new IntPredicate() {
        @Override
        public boolean test(int value) {
            return false;
        }
    };

    private static final IntPredicate odd = new IntPredicate() {
        @Override
        public boolean test(int value) {
            return (value % 2) != 0;
        }
    };

    private static final IntPredicate even = new IntPredicate() {
        @Override
        public boolean test(int value) {
            return (value % 2) == 0;
        }
    };

    private static final IntPredicate divBy3 = new IntPredicate() {
        @Override
        public boolean test(int value) {
            return (value % 3) == 0;
        }
    };

    @Test
    public void testAnd() {
        IntPredicate doubleTrue = Util.and(IntPredicateTest.alwaysTrue, IntPredicateTest.alwaysTrue);
        IntPredicate trueFalse = Util.and(IntPredicateTest.alwaysTrue, IntPredicateTest.alwaysFalse);
        IntPredicate falseTrue = Util.and(IntPredicateTest.alwaysFalse, IntPredicateTest.alwaysTrue);
        IntPredicate falseFalse = Util.and(IntPredicateTest.alwaysFalse, IntPredicateTest.alwaysFalse);
        Assert.assertTrue(doubleTrue.test(1));
        Assert.assertFalse(trueFalse.test(1));
        Assert.assertFalse(falseTrue.test(1));
        Assert.assertFalse(falseFalse.test(1));
        IntPredicate evenOdd = Util.and(IntPredicateTest.even, IntPredicateTest.odd);
        Assert.assertFalse(IntStream.of(1, 2, 3, 4, 5, 6).filter(evenOdd).findFirst().isPresent());
        IntPredicate oddAndDivBy3 = Util.and(IntPredicateTest.odd, IntPredicateTest.divBy3);
        Assert.assertEquals(2, IntStream.of(1, 2, 3, 4, 5, 6, 7, 8, 9).filter(oddAndDivBy3).count());
        IntPredicate evenAndDivBy3 = Util.and(IntPredicateTest.even, IntPredicateTest.divBy3);
        Assert.assertEquals(1, IntStream.of(1, 2, 3, 4, 5, 6, 7, 8, 9).filter(evenAndDivBy3).count());
    }

    @Test
    public void testOr() {
        IntPredicate doubleTrue = Util.or(IntPredicateTest.alwaysTrue, IntPredicateTest.alwaysTrue);
        IntPredicate trueFalse = Util.or(IntPredicateTest.alwaysTrue, IntPredicateTest.alwaysFalse);
        IntPredicate falseTrue = Util.or(IntPredicateTest.alwaysFalse, IntPredicateTest.alwaysTrue);
        IntPredicate falseFalse = Util.or(IntPredicateTest.alwaysFalse, IntPredicateTest.alwaysFalse);
        Assert.assertTrue(doubleTrue.test(1));
        Assert.assertTrue(trueFalse.test(1));
        Assert.assertTrue(falseTrue.test(1));
        Assert.assertFalse(falseFalse.test(1));
        IntPredicate evenOdd = Util.or(IntPredicateTest.even, IntPredicateTest.odd);
        Assert.assertEquals(6, IntStream.of(1, 2, 3, 4, 5, 6).filter(evenOdd).count());
        IntPredicate oddOrDivBy3 = Util.or(IntPredicateTest.odd, IntPredicateTest.divBy3);
        Assert.assertEquals(6, IntStream.of(1, 2, 3, 4, 5, 6, 7, 8, 9).filter(oddOrDivBy3).count());
        IntPredicate evenOrDivBy3 = Util.or(IntPredicateTest.even, IntPredicateTest.divBy3);
        Assert.assertEquals(6, IntStream.of(1, 2, 3, 4, 5, 6, 7, 8, 9).filter(evenOrDivBy3).count());
    }

    @Test
    public void testXor() {
        IntPredicate doubleTrue = Util.xor(IntPredicateTest.alwaysTrue, IntPredicateTest.alwaysTrue);
        IntPredicate trueFalse = Util.xor(IntPredicateTest.alwaysTrue, IntPredicateTest.alwaysFalse);
        IntPredicate falseTrue = Util.xor(IntPredicateTest.alwaysFalse, IntPredicateTest.alwaysTrue);
        IntPredicate falseFalse = Util.xor(IntPredicateTest.alwaysFalse, IntPredicateTest.alwaysFalse);
        Assert.assertFalse(doubleTrue.test(1));
        Assert.assertTrue(trueFalse.test(1));
        Assert.assertTrue(falseTrue.test(1));
        Assert.assertFalse(falseFalse.test(1));
        IntPredicate evenxOdd = Util.xor(IntPredicateTest.even, IntPredicateTest.odd);
        Assert.assertEquals(6, IntStream.of(1, 2, 3, 4, 5, 6).filter(evenxOdd).count());
        IntPredicate oddXorDivBy3 = Util.xor(IntPredicateTest.odd, IntPredicateTest.divBy3);
        Assert.assertEquals(4, IntStream.of(1, 2, 3, 4, 5, 6, 7, 8, 9).filter(oddXorDivBy3).count());
        IntPredicate evenXorDivBy3 = Util.xor(IntPredicateTest.even, IntPredicateTest.divBy3);
        Assert.assertEquals(5, IntStream.of(1, 2, 3, 4, 5, 6, 7, 8, 9).filter(evenXorDivBy3).count());
    }

    @Test
    public void testNegate() {
        IntPredicate negateTrue = Util.negate(IntPredicateTest.alwaysTrue);
        IntPredicate negateFalse = Util.negate(IntPredicateTest.alwaysFalse);
        Assert.assertFalse(negateTrue.test(1));
        Assert.assertTrue(negateFalse.test(1));
        IntPredicate notDivBy3 = Util.negate(IntPredicateTest.divBy3);
        Assert.assertEquals(IntStream.of(1, 2, 3, 4, 5, 6, 7, 8, 9).filter(notDivBy3).count(), 6);
    }

    @Test
    public void testSafe() {
        IntPredicate predicate = Util.safe(new IntPredicateTest.UnsafePredicate());
        Assert.assertTrue(predicate.test(40));
        Assert.assertFalse(predicate.test(15));
        Assert.assertFalse(predicate.test((-5)));
    }

    @Test
    public void testSafeWithResultIfFailed() {
        IntPredicate predicate = Util.safe(new IntPredicateTest.UnsafePredicate(), true);
        Assert.assertTrue(predicate.test(40));
        Assert.assertFalse(predicate.test(15));
        Assert.assertTrue(predicate.test((-5)));
    }

    @Test
    public void testPrivateUtilConstructor() {
        Assert.assertThat(Util.class, hasOnlyPrivateConstructors());
    }

    private static class UnsafePredicate implements ThrowableIntPredicate<Throwable> {
        @Override
        public boolean test(int value) throws IOException {
            if (value < 0) {
                throw new IOException();
            }
            return (value % 2) == 0;
        }
    }
}

