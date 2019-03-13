package org.jctools.util;


import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class RangeUtilTest {
    @Test(expected = IllegalArgumentException.class)
    public void checkPositiveMustFailIfArgumentIsZero() {
        RangeUtil.checkPositive(0, "var");
    }

    @Test(expected = IllegalArgumentException.class)
    public void checkPositiveMustFailIfArgumentIsLessThanZero() {
        RangeUtil.checkPositive((-1), "var");
    }

    @Test
    public void checkPositiveMustPassIfArgumentIsGreaterThanZero() {
        final long n = 1;
        final long actual = RangeUtil.checkPositive(n, "var");
        Assert.assertThat(actual, CoreMatchers.is(CoreMatchers.equalTo(n)));
    }

    @Test(expected = IllegalArgumentException.class)
    public void checkPositiveOrZeroMustFailIfArgumentIsNegative() {
        RangeUtil.checkPositiveOrZero((-1), "var");
    }

    @Test
    public void checkPositiveOrZeroMustPassIfArgumentIsZero() {
        final int n = 0;
        final int actual = RangeUtil.checkPositiveOrZero(n, "var");
        Assert.assertThat(actual, CoreMatchers.is(CoreMatchers.equalTo(n)));
    }

    @Test
    public void checkPositiveOrZeroMustPassIfArgumentIsGreaterThanZero() {
        final int n = 1;
        final int actual = RangeUtil.checkPositiveOrZero(n, "var");
        Assert.assertThat(actual, CoreMatchers.is(CoreMatchers.equalTo(n)));
    }

    @Test(expected = IllegalArgumentException.class)
    public void checkLessThanMustFailIfArgumentIsGreaterThanExpected() {
        RangeUtil.checkLessThan(1, 0, "var");
    }

    @Test(expected = IllegalArgumentException.class)
    public void checkLessThanMustFailIfArgumentIsEqualToExpected() {
        final int n = 1;
        final int actual = RangeUtil.checkLessThan(1, 1, "var");
        Assert.assertThat(actual, CoreMatchers.is(CoreMatchers.equalTo(n)));
    }

    @Test
    public void checkLessThanMustPassIfArgumentIsLessThanExpected() {
        final int n = 0;
        final int actual = RangeUtil.checkLessThan(n, 1, "var");
        Assert.assertThat(actual, CoreMatchers.is(CoreMatchers.equalTo(n)));
    }

    @Test(expected = IllegalArgumentException.class)
    public void checkLessThanOrEqualMustFailIfArgumentIsGreaterThanExpected() {
        RangeUtil.checkLessThanOrEqual(1, 0, "var");
    }

    @Test
    public void checkLessThanOrEqualMustPassIfArgumentIsEqualToExpected() {
        final int n = 1;
        final int actual = RangeUtil.checkLessThanOrEqual(n, 1, "var");
        Assert.assertThat(actual, CoreMatchers.is(CoreMatchers.equalTo(n)));
    }

    @Test
    public void checkLessThanOrEqualMustPassIfArgumentIsLessThanExpected() {
        final int n = 0;
        final int actual = RangeUtil.checkLessThanOrEqual(n, 1, "var");
        Assert.assertThat(actual, CoreMatchers.is(CoreMatchers.equalTo(n)));
    }

    @Test(expected = IllegalArgumentException.class)
    public void checkGreaterThanOrEqualMustFailIfArgumentIsLessThanExpected() {
        RangeUtil.checkGreaterThanOrEqual(0, 1, "var");
    }

    @Test
    public void checkGreaterThanOrEqualMustPassIfArgumentIsEqualToExpected() {
        final int n = 1;
        final int actual = RangeUtil.checkGreaterThanOrEqual(n, 1, "var");
        Assert.assertThat(actual, CoreMatchers.is(CoreMatchers.equalTo(n)));
    }

    @Test
    public void checkGreaterThanOrEqualMustPassIfArgumentIsGreaterThanExpected() {
        final int n = 1;
        final int actual = RangeUtil.checkGreaterThanOrEqual(n, 0, "var");
        Assert.assertThat(actual, CoreMatchers.is(CoreMatchers.equalTo(n)));
    }
}

