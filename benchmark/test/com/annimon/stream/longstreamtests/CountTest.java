package com.annimon.stream.longstreamtests;


import com.annimon.stream.LongStream;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public final class CountTest {
    @Test
    public void testCount() {
        Assert.assertThat(LongStream.of(100, 20, 3).count(), Matchers.is(3L));
        Assert.assertThat(LongStream.empty().count(), Matchers.is(0L));
    }
}

