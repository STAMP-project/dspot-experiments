package com.netflix.concurrency.limits.limit;


import java.util.concurrent.TimeUnit;
import junit.framework.Assert;
import org.junit.Test;


public class VegasLimitTest {
    @Test
    public void largeLimitIncrease() {
        VegasLimit limit = VegasLimit.newBuilder().initialLimit(10000).maxConcurrency(20000).build();
        limit.onSample(0, TimeUnit.SECONDS.toNanos(10), 5000, false);
        Assert.assertEquals(10000, limit.getLimit());
        limit.onSample(0, TimeUnit.SECONDS.toNanos(10), 6000, false);
        Assert.assertEquals(10024, limit.getLimit());
    }

    @Test
    public void increaseLimit() {
        VegasLimit limit = VegasLimitTest.create();
        limit.onSample(0, TimeUnit.MILLISECONDS.toNanos(10), 10, false);
        Assert.assertEquals(10, limit.getLimit());
        limit.onSample(0, TimeUnit.MILLISECONDS.toNanos(10), 11, false);
        Assert.assertEquals(16, limit.getLimit());
    }

    @Test
    public void decreaseLimit() {
        VegasLimit limit = VegasLimitTest.create();
        limit.onSample(0, TimeUnit.MILLISECONDS.toNanos(10), 10, false);
        Assert.assertEquals(10, limit.getLimit());
        limit.onSample(0, TimeUnit.MILLISECONDS.toNanos(50), 11, false);
        Assert.assertEquals(9, limit.getLimit());
    }

    @Test
    public void noChangeIfWithinThresholds() {
        VegasLimit limit = VegasLimitTest.create();
        limit.onSample(0, TimeUnit.MILLISECONDS.toNanos(10), 10, false);
        Assert.assertEquals(10, limit.getLimit());
        limit.onSample(0, TimeUnit.MILLISECONDS.toNanos(14), 14, false);
        Assert.assertEquals(10, limit.getLimit());
    }

    @Test
    public void decreaseSmoothing() {
        VegasLimit limit = VegasLimit.newBuilder().decrease(( current) -> current / 2).smoothing(0.5).initialLimit(100).maxConcurrency(200).build();
        // Pick up first min-rtt
        limit.onSample(0, TimeUnit.MILLISECONDS.toNanos(10), 100, false);
        Assert.assertEquals(100, limit.getLimit());
        // First decrease
        limit.onSample(0, TimeUnit.MILLISECONDS.toNanos(20), 100, false);
        Assert.assertEquals(75, limit.getLimit());
        // Second decrease
        limit.onSample(0, TimeUnit.MILLISECONDS.toNanos(20), 100, false);
        Assert.assertEquals(56, limit.getLimit());
    }

    @Test
    public void decreaseWithoutSmoothing() {
        VegasLimit limit = VegasLimit.newBuilder().decrease(( current) -> current / 2).initialLimit(100).maxConcurrency(200).build();
        // Pick up first min-rtt
        limit.onSample(0, TimeUnit.MILLISECONDS.toNanos(10), 100, false);
        Assert.assertEquals(100, limit.getLimit());
        // First decrease
        limit.onSample(0, TimeUnit.MILLISECONDS.toNanos(20), 100, false);
        Assert.assertEquals(50, limit.getLimit());
        // Second decrease
        limit.onSample(0, TimeUnit.MILLISECONDS.toNanos(20), 100, false);
        Assert.assertEquals(25, limit.getLimit());
    }
}

