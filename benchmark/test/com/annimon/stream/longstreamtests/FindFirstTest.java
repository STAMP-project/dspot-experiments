package com.annimon.stream.longstreamtests;


import com.annimon.stream.LongStream;
import org.junit.Assert;
import org.junit.Test;


public final class FindFirstTest {
    @Test
    public void testFindFirst() {
        Assert.assertThat(LongStream.of(3, 10, 19, 4, 50).findFirst(), hasValue(3L));
        Assert.assertThat(LongStream.empty().findFirst(), isEmpty());
    }
}

