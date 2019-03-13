package com.annimon.stream.longstreamtests;


import com.annimon.stream.LongStream;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public final class OfArrayTest {
    @Test
    public void testStreamOfLongs() {
        Assert.assertThat(LongStream.of(32, 28), elements(Matchers.arrayContaining(32L, 28L)));
    }

    @Test(expected = NullPointerException.class)
    public void testStreamOfLongsNull() {
        LongStream.of(((long[]) (null)));
    }

    @Test
    public void testStreamOfEmptyArray() {
        LongStream.of(new long[0]).custom(assertIsEmpty());
    }
}

