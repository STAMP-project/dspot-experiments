package com.annimon.stream.intstreamtests;


import com.annimon.stream.IntStream;
import org.junit.Assert;
import org.junit.Test;


public final class EmptyTest {
    @Test
    public void testStreamEmpty() {
        Assert.assertEquals(0, IntStream.empty().count());
        Assert.assertEquals(0, IntStream.empty().iterator().nextInt());
    }
}

