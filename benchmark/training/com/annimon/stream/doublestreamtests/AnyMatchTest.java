package com.annimon.stream.doublestreamtests;


import com.annimon.stream.DoubleStream;
import com.annimon.stream.Functions;
import org.junit.Assert;
import org.junit.Test;


public final class AnyMatchTest {
    @Test
    public void testAnyMatch() {
        Assert.assertTrue(DoubleStream.of(0.012, 10.347, 3.039, 19.84, 100.0).anyMatch(Functions.greaterThan(Math.PI)));
        Assert.assertTrue(DoubleStream.of(10.347, 19.84, 100.0).anyMatch(Functions.greaterThan(Math.PI)));
        Assert.assertFalse(DoubleStream.of(0.012, 3.039).anyMatch(Functions.greaterThan(Math.PI)));
        Assert.assertFalse(DoubleStream.empty().anyMatch(Functions.greaterThan(Math.PI)));
    }
}

