package com.sleekbyte.tailor.utils;


import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;


/**
 * Tests for {@link Pair}.
 */
@RunWith(MockitoJUnitRunner.class)
public final class PairTest {
    Pair pair;

    @Test
    public void testUnequalPairs() {
        Pair candidatePair = new Pair(1, 2);
        Assert.assertFalse(pair.equals(candidatePair));
    }

    @Test
    public void testEqualPairs() {
        Pair candidatePair = new Pair("Left", "Right");
        Assert.assertEquals(pair, candidatePair);
    }
}

