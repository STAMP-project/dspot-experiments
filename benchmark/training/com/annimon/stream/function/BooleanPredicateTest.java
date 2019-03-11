package com.annimon.stream.function;


import BooleanPredicate.Util;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests {@code BooleanPredicate}.
 */
public class BooleanPredicateTest {
    @Test
    public void testPrivateConstructor() throws Exception {
        Assert.assertThat(Util.class, hasOnlyPrivateConstructors());
    }

    @Test
    public void testFalseOr() {
        Assert.assertFalse(BooleanPredicateTest.falseOr.test(false));
        Assert.assertTrue(BooleanPredicateTest.falseOr.test(true));
    }

    @Test
    public void testTrueAnd() {
        Assert.assertFalse(BooleanPredicateTest.trueAnd.test(false));
        Assert.assertTrue(BooleanPredicateTest.trueAnd.test(true));
    }

    @Test
    public void testIdentity() {
        BooleanPredicate identity = Util.identity();
        Assert.assertThat(identity.test(false), Matchers.is(false));
    }

    @Test
    public void testAndPredicate() {
        BooleanPredicate predicate = Util.and(BooleanPredicateTest.falseOr, BooleanPredicateTest.trueAnd);
        Assert.assertFalse(predicate.test(false));
        Assert.assertTrue(predicate.test(true));
    }

    @Test
    public void testOrPredicate() {
        BooleanPredicate predicate = Util.or(BooleanPredicateTest.falseOr, BooleanPredicateTest.trueAnd);
        Assert.assertFalse(predicate.test(false));
        Assert.assertTrue(predicate.test(true));
    }

    @Test
    public void testXorPredicate() {
        BooleanPredicate predicate = Util.xor(BooleanPredicateTest.falseOr, BooleanPredicateTest.trueAnd);
        Assert.assertFalse(predicate.test(false));
        Assert.assertFalse(predicate.test(true));
    }

    @Test
    public void testNegatePredicate() {
        BooleanPredicate trueAndNegated = Util.negate(BooleanPredicateTest.trueAnd);
        Assert.assertTrue(trueAndNegated.test(false));
        Assert.assertFalse(trueAndNegated.test(true));
    }

    private static final BooleanPredicate falseOr = new BooleanPredicate() {
        @Override
        public boolean test(boolean value) {
            return false || value;
        }
    };

    private static final BooleanPredicate trueAnd = new BooleanPredicate() {
        @Override
        public boolean test(boolean value) {
            return true && value;
        }
    };
}

