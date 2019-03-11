package com.annimon.stream.doublestreamtests;


import com.annimon.stream.DoubleStream;
import com.annimon.stream.function.DoubleToIntFunction;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public final class MapToIntTest {
    @Test
    public void testMapToInt() {
        DoubleToIntFunction mapper = new DoubleToIntFunction() {
            @Override
            public int applyAsInt(double value) {
                return ((int) (value * 10));
            }
        };
        Assert.assertThat(DoubleStream.of(0.2, 0.3, 0.4).mapToInt(mapper).toArray(), Matchers.is(new int[]{ 2, 3, 4 }));
    }
}

