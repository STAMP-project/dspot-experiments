package com.annimon.stream.longstreamtests;


import com.annimon.stream.Functions;
import com.annimon.stream.LongStream;
import org.junit.Assert;
import org.junit.Test;


public final class AnyMatchTest {
    @Test
    public void testAnyMatch() {
        Assert.assertTrue(LongStream.of(3, 10, 19, 4, 50).anyMatch(Functions.remainderLong(2)));
        Assert.assertTrue(LongStream.of(10, 4, 50).anyMatch(Functions.remainderLong(2)));
        Assert.assertFalse(LongStream.of(3, 19).anyMatch(Functions.remainderLong(2)));
        Assert.assertFalse(LongStream.empty().anyMatch(Functions.remainderLong(2)));
    }
}

