package com.annimon.stream.streamtests;


import com.annimon.stream.Stream;
import org.junit.Assert;
import org.junit.Test;


public final class NullsOnlyTest {
    @Test
    public void testNullsOnly() {
        final long nullsAmount = Stream.range(0, 10).map(new com.annimon.stream.function.Function<Integer, String>() {
            @Override
            public String apply(Integer integer) {
                return (integer % 3) == 0 ? null : "";
            }
        }).nullsOnly().count();
        Assert.assertEquals(4, nullsAmount);
    }
}

