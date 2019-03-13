package com.annimon.stream.doublestreamtests;


import com.annimon.stream.DoubleStream;
import org.junit.Assert;
import org.junit.Test;


public final class FindLastTest {
    @Test
    public void testFindLast() {
        Assert.assertThat(DoubleStream.of(0.012, 10.347, 3.039, 19.84, 100.0).findLast(), hasValue(100.0));
        Assert.assertThat(DoubleStream.of(100.0).findLast(), hasValue(100.0));
        Assert.assertThat(DoubleStream.empty().findLast(), isEmpty());
    }
}

