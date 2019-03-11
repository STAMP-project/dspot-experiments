package com.annimon.stream.doublestreamtests;


import com.annimon.stream.DoubleStream;
import com.annimon.stream.function.DoubleToLongFunction;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public final class MapToLongTest {
    @Test
    public void testMapToLong() {
        DoubleToLongFunction mapper = new DoubleToLongFunction() {
            @Override
            public long applyAsLong(double value) {
                return ((long) (value * 10000000000L));
            }
        };
        Assert.assertThat(DoubleStream.of(0.2, 0.3, 0.004).mapToLong(mapper).toArray(), Matchers.is(new long[]{ 2000000000L, 3000000000L, 40000000L }));
    }
}

