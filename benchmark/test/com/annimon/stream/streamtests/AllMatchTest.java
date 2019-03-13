package com.annimon.stream.streamtests;


import com.annimon.stream.Functions;
import com.annimon.stream.Stream;
import org.junit.Assert;
import org.junit.Test;


public final class AllMatchTest {
    @Test
    public void testAllMatchWithFalseResult() {
        boolean match = Stream.range(0, 10).allMatch(Functions.remainder(2));
        Assert.assertFalse(match);
    }

    @Test
    public void testAllMatchWithTrueResult() {
        boolean match = Stream.of(2, 4, 6, 8, 10).allMatch(Functions.remainder(2));
        Assert.assertTrue(match);
    }
}

