package com.annimon.stream.longstreamtests;


import com.annimon.stream.LongStream;
import org.junit.Assert;
import org.junit.Test;


public final class MaxTest {
    @Test
    public void testMax() {
        Assert.assertThat(LongStream.of(100, 20, 3).max(), hasValue(100L));
        Assert.assertThat(LongStream.empty().max(), isEmpty());
    }
}

