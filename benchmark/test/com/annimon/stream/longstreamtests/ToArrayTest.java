package com.annimon.stream.longstreamtests;


import com.annimon.stream.LongStream;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public final class ToArrayTest {
    @Test
    public void testToArray() {
        Assert.assertThat(LongStream.of(12L, 32L, 22L, 9L).toArray(), Matchers.is(new long[]{ 12L, 32L, 22L, 9L }));
    }

    @Test
    public void testToArrayOnEmptyStream() {
        Assert.assertThat(LongStream.empty().toArray().length, Matchers.is(0));
    }
}

