package com.annimon.stream.longstreamtests;


import com.annimon.stream.LongStream;
import com.annimon.stream.iterator.PrimitiveIterator;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public final class OfPrimitiveIteratorTest {
    @Test
    public void testStreamOfPrimitiveIterator() {
        LongStream stream = LongStream.of(new PrimitiveIterator.OfLong() {
            private int index = 0;

            @Override
            public boolean hasNext() {
                return (index) < 3;
            }

            @Override
            public long nextLong() {
                return ++(index);
            }
        });
        Assert.assertThat(stream, elements(Matchers.arrayContaining(1L, 2L, 3L)));
    }

    @Test(expected = NullPointerException.class)
    public void testStreamOfPrimitiveIteratorNull() {
        LongStream.of(((PrimitiveIterator.OfLong) (null)));
    }
}

