package org.elasticsearch.hadoop.rest.bulk.handler;


import HandlerResult.HANDLED;
import HandlerResult.PASS;
import java.util.concurrent.TimeUnit;
import org.junit.Assert;
import org.junit.Test;


public class BulkWriteErrorCollectorTest {
    BulkWriteErrorCollector collector;

    @Test
    public void retry() throws Exception {
        Assert.assertEquals(HANDLED, collector.retry());
        Assert.assertEquals(true, collector.receivedRetries());
        Assert.assertEquals(null, collector.getAndClearMessage());
        Assert.assertEquals(null, collector.getAndClearRetryValue());
        Assert.assertEquals(0L, collector.getDelayTimeBetweenRetries());
        Assert.assertEquals(false, collector.receivedRetries());
    }

    @Test
    public void backoffAndRetry() throws Exception {
        Assert.assertEquals(HANDLED, collector.backoffAndRetry(100L, TimeUnit.MILLISECONDS));
        Assert.assertEquals(true, collector.receivedRetries());
        Assert.assertEquals(null, collector.getAndClearMessage());
        Assert.assertEquals(null, collector.getAndClearRetryValue());
        Assert.assertEquals(100L, collector.getDelayTimeBetweenRetries());
        Assert.assertEquals(false, collector.receivedRetries());
    }

    @Test
    public void retry1() throws Exception {
        Assert.assertEquals(HANDLED, collector.retry(new byte[]{ 0, 1, 2, 3, 4, 5 }));
        Assert.assertEquals(true, collector.receivedRetries());
        Assert.assertEquals(null, collector.getAndClearMessage());
        Assert.assertArrayEquals(new byte[]{ 0, 1, 2, 3, 4, 5 }, collector.getAndClearRetryValue());
        Assert.assertEquals(0L, collector.getDelayTimeBetweenRetries());
        Assert.assertEquals(false, collector.receivedRetries());
    }

    @Test
    public void backoffAndRetry1() throws Exception {
        Assert.assertEquals(HANDLED, collector.backoffAndRetry(new byte[]{ 0, 1, 2, 3, 4, 5 }, 100L, TimeUnit.MILLISECONDS));
        Assert.assertEquals(true, collector.receivedRetries());
        Assert.assertEquals(null, collector.getAndClearMessage());
        Assert.assertArrayEquals(new byte[]{ 0, 1, 2, 3, 4, 5 }, collector.getAndClearRetryValue());
        Assert.assertEquals(100L, collector.getDelayTimeBetweenRetries());
        Assert.assertEquals(false, collector.receivedRetries());
    }

    @Test
    public void pass() throws Exception {
        Assert.assertEquals(PASS, collector.pass("Pass reason"));
        Assert.assertEquals(false, collector.receivedRetries());
        Assert.assertEquals("Pass reason", collector.getAndClearMessage());
        Assert.assertEquals(null, collector.getAndClearRetryValue());
        Assert.assertEquals(0L, collector.getDelayTimeBetweenRetries());
    }
}

