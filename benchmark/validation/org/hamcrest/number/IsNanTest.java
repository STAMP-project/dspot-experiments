package org.hamcrest.number;


import org.hamcrest.AbstractMatcherTest;
import org.hamcrest.Matcher;
import org.junit.Test;


public final class IsNanTest {
    @Test
    public void copesWithNullsAndUnknownTypes() {
        Matcher<Double> matcher = IsNaN.notANumber();
        AbstractMatcherTest.assertNullSafe(matcher);
        AbstractMatcherTest.assertUnknownTypeSafe(matcher);
    }

    @Test
    public void matchesNaN() {
        AbstractMatcherTest.assertMatches(IsNaN.notANumber(), Double.NaN);
    }

    @Test
    public void doesNotMatchDoubleValue() {
        AbstractMatcherTest.assertDoesNotMatch(IsNaN.notANumber(), 1.25);
    }

    @Test
    public void doesNotMatchInfinity() {
        AbstractMatcherTest.assertDoesNotMatch(IsNaN.notANumber(), Double.POSITIVE_INFINITY);
    }

    @Test
    public void describesItself() {
        AbstractMatcherTest.assertDescription("a double value of NaN", IsNaN.notANumber());
    }

    @Test
    public void describesAMismatch() {
        AbstractMatcherTest.assertMismatchDescription("was <1.25>", IsNaN.notANumber(), 1.25);
    }
}

