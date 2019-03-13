package com.annimon.stream.doublestreamtests;


import com.annimon.stream.DoubleStream;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public final class IteratorTest {
    @Test
    public void testIterator() {
        Assert.assertThat(DoubleStream.of(1.0123).iterator().nextDouble(), Matchers.closeTo(1.0123, 1.0E-4));
        Assert.assertThat(DoubleStream.empty().iterator().hasNext(), Matchers.is(false));
        Assert.assertThat(DoubleStream.empty().iterator().nextDouble(), Matchers.is(0.0));
    }
}

