package com.annimon.stream.doublestreamtests;


import com.annimon.stream.DoubleStream;
import org.junit.Assert;
import org.junit.Test;


public final class MaxTest {
    @Test
    public void testMax() {
        Assert.assertThat(DoubleStream.of(0.1, 0.02, 0.003).max(), hasValue(0.1));
        Assert.assertThat(DoubleStream.empty().max(), isEmpty());
    }
}

