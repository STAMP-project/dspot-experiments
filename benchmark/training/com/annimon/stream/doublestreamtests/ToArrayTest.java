package com.annimon.stream.doublestreamtests;


import com.annimon.stream.DoubleStream;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public final class ToArrayTest {
    @Test
    public void testToArray() {
        Assert.assertThat(DoubleStream.of(0.012, 10.347, 3.039, 19.84, 100.0).toArray(), Matchers.is(new double[]{ 0.012, 10.347, 3.039, 19.84, 100.0 }));
    }

    @Test
    public void testToArrayOnEmptyStream() {
        Assert.assertThat(DoubleStream.empty().toArray().length, Matchers.is(0));
    }
}

