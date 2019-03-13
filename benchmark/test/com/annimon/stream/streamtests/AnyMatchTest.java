package com.annimon.stream.streamtests;


import com.annimon.stream.Functions;
import com.annimon.stream.Stream;
import org.junit.Assert;
import org.junit.Test;


public final class AnyMatchTest {
    @Test
    public void testAnyMatchWithTrueResult() {
        boolean match = Stream.range(0, 10).anyMatch(Functions.remainder(2));
        Assert.assertTrue(match);
    }

    @Test
    public void testAnyMatchWithFalseResult() {
        boolean match = Stream.of(2, 3, 5, 8, 13).anyMatch(Functions.remainder(10));
        Assert.assertFalse(match);
    }
}

