package com.netflix.discovery;


import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


/**
 *
 *
 * @author David Liu
 */
public class InstanceInfoReplicatorTest {
    private final int burstSize = 2;

    private final int refreshRateSeconds = 2;

    private DiscoveryClient discoveryClient;

    private InstanceInfoReplicator replicator;

    @Test
    public void testOnDemandUpdate() throws Throwable {
        Assert.assertTrue(replicator.onDemandUpdate());
        Thread.sleep(10);// give some time for execution

        Assert.assertTrue(replicator.onDemandUpdate());
        Thread.sleep(((1000 * (refreshRateSeconds)) / 2));
        Assert.assertTrue(replicator.onDemandUpdate());
        Thread.sleep(10);
        Mockito.verify(discoveryClient, Mockito.times(3)).refreshInstanceInfo();
        Mockito.verify(discoveryClient, Mockito.times(1)).register();
    }

    @Test
    public void testOnDemandUpdateRateLimiting() throws Throwable {
        Assert.assertTrue(replicator.onDemandUpdate());
        Thread.sleep(10);// give some time for execution

        Assert.assertTrue(replicator.onDemandUpdate());
        Thread.sleep(10);// give some time for execution

        Assert.assertFalse(replicator.onDemandUpdate());
        Thread.sleep(10);
        Mockito.verify(discoveryClient, Mockito.times(2)).refreshInstanceInfo();
        Mockito.verify(discoveryClient, Mockito.times(1)).register();
    }

    @Test
    public void testOnDemandUpdateResetAutomaticRefresh() throws Throwable {
        replicator.start(0);
        Thread.sleep(((1000 * (refreshRateSeconds)) / 2));
        Assert.assertTrue(replicator.onDemandUpdate());
        Thread.sleep(((1000 * (refreshRateSeconds)) + 50));
        Mockito.verify(discoveryClient, Mockito.times(3)).refreshInstanceInfo();// 1 initial refresh, 1 onDemand, 1 auto

        Mockito.verify(discoveryClient, Mockito.times(1)).register();// all but 1 is no-op

    }

    @Test
    public void testOnDemandUpdateResetAutomaticRefreshWithInitialDelay() throws Throwable {
        replicator.start((1000 * (refreshRateSeconds)));
        Assert.assertTrue(replicator.onDemandUpdate());
        Thread.sleep(((1000 * (refreshRateSeconds)) + 100));
        Mockito.verify(discoveryClient, Mockito.times(2)).refreshInstanceInfo();// 1 onDemand, 1 auto

        Mockito.verify(discoveryClient, Mockito.times(1)).register();// all but 1 is no-op

    }
}

