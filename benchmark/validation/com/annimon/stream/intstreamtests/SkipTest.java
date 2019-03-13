package com.annimon.stream.intstreamtests;


import com.annimon.stream.IntStream;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public final class SkipTest {
    @Test
    public void testSkip() {
        Assert.assertEquals(0, IntStream.empty().skip(2).count());
        Assert.assertEquals(5, IntStream.range(10, 20).skip(5).count());
        Assert.assertEquals(10, IntStream.range(10, 20).skip(0).count());
        Assert.assertEquals(0, IntStream.range(10, 20).skip(10).count());
        Assert.assertEquals(0, IntStream.range(10, 20).skip(20).count());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSkipNegative() {
        IntStream.empty().skip((-5));
    }

    @Test
    public void testSkipZero() {
        IntStream.of(1, 2).skip(0).custom(assertElements(Matchers.arrayContaining(1, 2)));
    }

    @Test
    public void testSkipMoreThanCount() {
        IntStream.range(0, 10).skip(2).limit(5).custom(assertElements(Matchers.arrayContaining(2, 3, 4, 5, 6)));
    }
}

