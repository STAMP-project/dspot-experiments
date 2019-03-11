package com.baeldung.algorithms.roundedup;


import org.junit.Assert;
import org.junit.Test;


public class RoundUpToHundredUnitTest {
    @Test
    public void givenInput_whenRound_thenRoundUpToTheNearestHundred() {
        Assert.assertEquals("Rounded up to hundred", 100, RoundUpToHundred.round(99));
        Assert.assertEquals("Rounded up to three hundred ", 300, RoundUpToHundred.round(200.2));
        Assert.assertEquals("Returns same rounded value", 400, RoundUpToHundred.round(400));
    }
}

