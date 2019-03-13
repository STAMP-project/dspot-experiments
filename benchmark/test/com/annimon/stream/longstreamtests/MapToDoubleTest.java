package com.annimon.stream.longstreamtests;


import com.annimon.stream.DoubleStream;
import com.annimon.stream.LongStream;
import com.annimon.stream.function.LongToDoubleFunction;
import com.annimon.stream.test.hamcrest.DoubleStreamMatcher;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public final class MapToDoubleTest {
    @Test
    @SuppressWarnings("unchecked")
    public void testMapToDouble() {
        DoubleStream stream = LongStream.rangeClosed(2, 4).mapToDouble(new LongToDoubleFunction() {
            @Override
            public double applyAsDouble(long value) {
                return value / 10.0;
            }
        });
        Assert.assertThat(stream, DoubleStreamMatcher.elements(Matchers.array(Matchers.closeTo(0.2, 1.0E-5), Matchers.closeTo(0.3, 1.0E-5), Matchers.closeTo(0.4, 1.0E-5))));
    }
}

