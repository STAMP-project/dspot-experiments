package com.annimon.stream.intstreamtests;


import com.annimon.stream.IntStream;
import com.annimon.stream.function.IntSupplier;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public final class LimitTest {
    @Test
    public void testLimit() {
        Assert.assertEquals(3, IntStream.of(1, 2, 3, 4, 5, 6).limit(3).count());
        Assert.assertEquals(6, IntStream.generate(new IntSupplier() {
            int current = 42;

            @Override
            public int getAsInt() {
                current = ((current) + (current)) << 1;
                return current;
            }
        }).limit(6).count());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testLimitNegative() {
        IntStream.of(42).limit((-1)).count();
    }

    @Test
    public void testLimitZero() {
        Assert.assertEquals(0, IntStream.of(1, 2).limit(0).count());
    }

    @Test
    public void testLimitMoreThanCount() {
        Assert.assertThat(IntStream.range(0, 5).limit(15).count(), Matchers.is(5L));
    }
}

