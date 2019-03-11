package com.annimon.stream.longstreamtests;


import com.annimon.stream.LongStream;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public final class OfLongTest {
    @Test
    public void testStreamOfLong() {
        Assert.assertThat(LongStream.of(1234), elements(Matchers.arrayContaining(1234L)));
    }
}

