package com.baeldung.concurrent.countdownlatch;


import org.junit.Assert;
import org.junit.Test;


public class CountdownLatchResetExampleUnitTest {
    @Test
    public void whenCountDownLatch_noReset() {
        CountdownLatchResetExample ex = new CountdownLatchResetExample(7, 20);
        int lineCount = ex.countWaits();
        Assert.assertTrue((lineCount <= 7));
    }
}

