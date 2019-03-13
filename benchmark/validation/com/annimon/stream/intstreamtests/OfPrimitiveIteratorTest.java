package com.annimon.stream.intstreamtests;


import com.annimon.stream.IntStream;
import com.annimon.stream.iterator.PrimitiveIterator;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public final class OfPrimitiveIteratorTest {
    @Test
    public void testStreamOfPrimitiveIterator() {
        int[] expected = new int[]{ 0, 1 };
        IntStream stream = IntStream.of(new PrimitiveIterator.OfInt() {
            int index = 0;

            @Override
            public boolean hasNext() {
                return (index) < 2;
            }

            @Override
            public int nextInt() {
                return (index)++;
            }
        });
        Assert.assertThat(stream.toArray(), CoreMatchers.is(expected));
    }

    @Test(expected = NullPointerException.class)
    public void testStreamOfPrimitiveIteratorNull() {
        IntStream.of(((PrimitiveIterator.OfInt) (null)));
    }
}

