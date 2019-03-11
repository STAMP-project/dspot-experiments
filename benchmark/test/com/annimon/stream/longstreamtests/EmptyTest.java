package com.annimon.stream.longstreamtests;


import com.annimon.stream.LongStream;
import org.junit.Assert;
import org.junit.Test;


public final class EmptyTest {
    @Test
    public void testStreamEmpty() {
        Assert.assertThat(LongStream.empty(), isEmpty());
    }
}

