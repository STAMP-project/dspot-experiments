package com.annimon.stream.doublestreamtests;


import com.annimon.stream.DoubleStream;
import com.annimon.stream.iterator.PrimitiveIterator;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public final class OfPrimitiveIteratorTest {
    @Test
    @SuppressWarnings("unchecked")
    public void testStreamOfPrimitiveIterator() {
        DoubleStream stream = DoubleStream.of(new PrimitiveIterator.OfDouble() {
            private int index = 0;

            @Override
            public boolean hasNext() {
                return (index) < 3;
            }

            @Override
            public double nextDouble() {
                (index)++;
                return (index) + 0.0021;
            }
        });
        Assert.assertThat(stream, elements(Matchers.arrayContaining(Matchers.closeTo(1.0021, 1.0E-5), Matchers.closeTo(2.0021, 1.0E-5), Matchers.closeTo(3.0021, 1.0E-5))));
    }

    @Test(expected = NullPointerException.class)
    public void testStreamOfPrimitiveIteratorNull() {
        DoubleStream.of(((PrimitiveIterator.OfDouble) (null)));
    }
}

