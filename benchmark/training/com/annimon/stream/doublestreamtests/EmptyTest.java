package com.annimon.stream.doublestreamtests;


import com.annimon.stream.DoubleStream;
import org.junit.Assert;
import org.junit.Test;


public final class EmptyTest {
    @Test
    public void testStreamEmpty() {
        Assert.assertThat(DoubleStream.empty(), isEmpty());
    }
}

