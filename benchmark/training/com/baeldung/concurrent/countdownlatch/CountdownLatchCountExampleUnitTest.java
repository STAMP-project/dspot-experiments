package com.baeldung.concurrent.countdownlatch;


import org.junit.Assert;
import org.junit.Test;


public class CountdownLatchCountExampleUnitTest {
    @Test
    public void whenCountDownLatch_completed() {
        CountdownLatchCountExample ex = new CountdownLatchCountExample(2);
        boolean isCompleted = ex.callTwiceInSameThread();
        Assert.assertTrue(isCompleted);
    }
}

