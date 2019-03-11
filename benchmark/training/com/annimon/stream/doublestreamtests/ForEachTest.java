package com.annimon.stream.doublestreamtests;


import com.annimon.stream.DoubleStream;
import com.annimon.stream.function.DoubleConsumer;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public final class ForEachTest {
    @Test
    public void testForEach() {
        final double[] expected = new double[]{ 1.2, 3.456 };
        DoubleStream.of(1.2, 3.456).forEach(new DoubleConsumer() {
            private int index = 0;

            @Override
            public void accept(double value) {
                Assert.assertThat(value, Matchers.closeTo(expected[((index)++)], 1.0E-4));
            }
        });
    }

    @Test
    public void testForEachOnEmptyStream() {
        DoubleStream.empty().forEach(new DoubleConsumer() {
            @Override
            public void accept(double value) {
                Assert.fail();
            }
        });
    }
}

