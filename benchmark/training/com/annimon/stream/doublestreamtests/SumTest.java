package com.annimon.stream.doublestreamtests;


import com.annimon.stream.DoubleStream;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public final class SumTest {
    @Test
    public void testSum() {
        Assert.assertThat(DoubleStream.of(0.1, 0.02, 0.003).sum(), Matchers.closeTo(0.123, 1.0E-4));
        Assert.assertThat(DoubleStream.empty().sum(), Matchers.is(0.0));
    }
}

