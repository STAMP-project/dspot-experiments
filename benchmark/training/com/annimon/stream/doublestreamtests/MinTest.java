package com.annimon.stream.doublestreamtests;


import com.annimon.stream.DoubleStream;
import org.junit.Assert;
import org.junit.Test;


public final class MinTest {
    @Test
    public void testMin() {
        Assert.assertThat(DoubleStream.of(0.1, 0.02, 0.003).min(), hasValue(0.003));
        Assert.assertThat(DoubleStream.empty().min(), isEmpty());
    }
}

