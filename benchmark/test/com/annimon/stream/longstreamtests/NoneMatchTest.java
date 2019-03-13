package com.annimon.stream.longstreamtests;


import com.annimon.stream.Functions;
import com.annimon.stream.LongStream;
import org.junit.Assert;
import org.junit.Test;


public final class NoneMatchTest {
    @Test
    public void testNoneMatch() {
        Assert.assertFalse(LongStream.of(3, 10, 19, 4, 50).noneMatch(Functions.remainderLong(2)));
        Assert.assertFalse(LongStream.of(10, 4, 50).noneMatch(Functions.remainderLong(2)));
        Assert.assertTrue(LongStream.of(3, 19).noneMatch(Functions.remainderLong(2)));
        Assert.assertTrue(LongStream.empty().noneMatch(Functions.remainderLong(2)));
    }
}

