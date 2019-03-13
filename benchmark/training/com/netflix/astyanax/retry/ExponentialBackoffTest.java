package com.netflix.astyanax.retry;


import org.junit.Assert;
import org.junit.Test;


public final class ExponentialBackoffTest {
    @Test
    public void testSleepTimeNeverNegative() throws IllegalAccessException, NoSuchFieldException {
        ExponentialBackoff backoff = new ExponentialBackoff(500, (-1));
        for (int i = 22; i < 1000; i++) {
            ExponentialBackoffTest.setAttemptCount(backoff, i);
            Assert.assertTrue((("Backoff at retry " + i) + " was not positive"), ((backoff.getSleepTimeMs()) >= 0));
        }
    }
}

