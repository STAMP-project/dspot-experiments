package com.annimon.stream.doublestreamtests;


import com.annimon.stream.DoubleStream;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public final class CountTest {
    @Test
    public void testCount() {
        Assert.assertThat(DoubleStream.of(0.1, 0.02, 0.003).count(), Matchers.is(3L));
        Assert.assertThat(DoubleStream.empty().count(), Matchers.is(0L));
    }
}

