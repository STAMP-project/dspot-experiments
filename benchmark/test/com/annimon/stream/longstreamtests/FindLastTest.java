package com.annimon.stream.longstreamtests;


import com.annimon.stream.LongStream;
import org.junit.Assert;
import org.junit.Test;


public final class FindLastTest {
    @Test
    public void testFindLast() {
        Assert.assertThat(LongStream.of(3, 10, 19, 4, 50).findLast(), hasValue(50L));
        Assert.assertThat(LongStream.of(50).findLast(), hasValue(50L));
        Assert.assertThat(LongStream.empty().findLast(), isEmpty());
    }
}

