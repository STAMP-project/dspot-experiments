package com.annimon.stream.doublestreamtests;


import com.annimon.stream.DoubleStream;
import org.junit.Assert;
import org.junit.Test;


public final class FindFirstTest {
    @Test
    public void testFindFirst() {
        Assert.assertThat(DoubleStream.of(0.012, 10.347, 3.039, 19.84, 100.0).findFirst(), hasValue(0.012));
        Assert.assertThat(DoubleStream.empty().findFirst(), isEmpty());
    }
}

