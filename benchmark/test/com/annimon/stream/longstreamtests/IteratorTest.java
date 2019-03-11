package com.annimon.stream.longstreamtests;


import com.annimon.stream.LongStream;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public final class IteratorTest {
    @Test
    public void testIterator() {
        Assert.assertThat(LongStream.of(1234567L).iterator().nextLong(), Matchers.is(1234567L));
        Assert.assertThat(LongStream.empty().iterator().hasNext(), Matchers.is(false));
        Assert.assertThat(LongStream.empty().iterator().nextLong(), Matchers.is(0L));
    }
}

