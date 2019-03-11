package com.annimon.stream.longstreamtests;


import com.annimon.stream.LongStream;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public final class SumTest {
    @Test
    public void testSum() {
        Assert.assertThat(LongStream.of(100, 20, 3).sum(), Matchers.is(123L));
        Assert.assertThat(LongStream.empty().sum(), Matchers.is(0L));
    }
}

