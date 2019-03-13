package com.annimon.stream.streamtests;


import com.annimon.stream.Functions;
import com.annimon.stream.Stream;
import org.junit.Assert;
import org.junit.Test;


public final class NoneMatchTest {
    @Test
    public void testNoneMatchWithFalseResult() {
        boolean match = Stream.range(0, 10).noneMatch(Functions.remainder(2));
        Assert.assertFalse(match);
    }

    @Test
    public void testNoneMatchWithTrueResult() {
        boolean match = Stream.of(2, 3, 5, 8, 13).noneMatch(Functions.remainder(10));
        Assert.assertTrue(match);
    }
}

