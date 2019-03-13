package com.netflix.astyanax.retry;


import org.junit.Assert;
import org.junit.Test;


public final class BoundedExponentialBackoffTest {
    @Test
    public void testSleepTimeNeverNegative() throws IllegalAccessException, NoSuchFieldException {
        BoundedExponentialBackoff backoff = new BoundedExponentialBackoff(500, 5000, (-1));
        for (int i = 0; i < 1000; i++) {
            ExponentialBackoffTest.setAttemptCount(backoff, i);
            Assert.assertTrue((("Backoff at retry " + i) + " was not positive"), ((backoff.getSleepTimeMs()) >= 0));
        }
    }
}

