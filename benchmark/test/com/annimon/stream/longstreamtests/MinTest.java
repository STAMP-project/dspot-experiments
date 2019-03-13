package com.annimon.stream.longstreamtests;


import com.annimon.stream.LongStream;
import org.junit.Assert;
import org.junit.Test;


public final class MinTest {
    @Test
    public void testMin() {
        Assert.assertThat(LongStream.of(100, 20, 3).min(), hasValue(3L));
        Assert.assertThat(LongStream.empty().min(), isEmpty());
    }
}

