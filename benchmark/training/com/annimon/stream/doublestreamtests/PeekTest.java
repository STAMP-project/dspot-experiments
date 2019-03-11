package com.annimon.stream.doublestreamtests;


import com.annimon.stream.DoubleStream;
import com.annimon.stream.function.DoubleConsumer;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public final class PeekTest {
    @Test
    public void testPeekOnEmptyStream() {
        DoubleStream.empty().peek(new DoubleConsumer() {
            @Override
            public void accept(double value) {
                Assert.fail();
            }
        }).custom(assertIsEmpty());
    }

    @Test
    public void testPeek() {
        final double[] expected = new double[]{ 1.2, 3.456 };
        Assert.assertThat(DoubleStream.of(1.2, 3.456).peek(new DoubleConsumer() {
            private int index = 0;

            @Override
            public void accept(double value) {
                Assert.assertThat(value, Matchers.closeTo(expected[((index)++)], 1.0E-4));
            }
        }).count(), Matchers.is(2L));
    }
}

