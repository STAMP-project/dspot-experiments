package com.annimon.stream.longstreamtests;


import com.annimon.stream.Functions;
import com.annimon.stream.LongStream;
import org.junit.Assert;
import org.junit.Test;


public final class AllMatchTest {
    @Test
    public void testAllMatch() {
        Assert.assertFalse(LongStream.of(3, 10, 19, 4, 50).allMatch(Functions.remainderLong(2)));
        Assert.assertTrue(LongStream.of(10, 4, 50).allMatch(Functions.remainderLong(2)));
        Assert.assertFalse(LongStream.of(3, 19).allMatch(Functions.remainderLong(2)));
        Assert.assertTrue(LongStream.empty().allMatch(Functions.remainderLong(2)));
    }
}

