package com.netflix.discovery.shared.resolver;


import com.netflix.discovery.shared.resolver.aws.SampleCluster;
import com.netflix.discovery.shared.transport.EurekaTransportConfig;
import java.util.ArrayList;
import java.util.List;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


/**
 *
 *
 * @author David Liu
 */
public class AsyncResolverTest {
    private final EurekaTransportConfig transportConfig = Mockito.mock(EurekaTransportConfig.class);

    private final ClusterResolver delegateResolver = Mockito.mock(ClosableResolver.class);

    private AsyncResolver resolver;

    @Test
    public void testHappyCase() {
        List delegateReturns1 = new ArrayList(SampleCluster.UsEast1a.builder().withServerPool(2).build());
        List delegateReturns2 = new ArrayList(SampleCluster.UsEast1b.builder().withServerPool(3).build());
        Mockito.when(delegateResolver.getClusterEndpoints()).thenReturn(delegateReturns1).thenReturn(delegateReturns2);
        List endpoints = resolver.getClusterEndpoints();
        Assert.assertThat(endpoints.size(), CoreMatchers.equalTo(delegateReturns1.size()));
        Mockito.verify(delegateResolver, Mockito.times(1)).getClusterEndpoints();
        Mockito.verify(resolver, Mockito.times(1)).doWarmUp();
        // try again, should be async
        endpoints = resolver.getClusterEndpoints();
        Assert.assertThat(endpoints.size(), CoreMatchers.equalTo(delegateReturns1.size()));
        Mockito.verify(delegateResolver, Mockito.times(1)).getClusterEndpoints();
        Mockito.verify(resolver, Mockito.times(1)).doWarmUp();
        // wait for the next async update cycle
        Mockito.verify(delegateResolver, Mockito.timeout(1000).times(2)).getClusterEndpoints();
        endpoints = resolver.getClusterEndpoints();
        Assert.assertThat(endpoints.size(), CoreMatchers.equalTo(delegateReturns2.size()));
        Mockito.verify(delegateResolver, Mockito.times(2)).getClusterEndpoints();
        Mockito.verify(resolver, Mockito.times(1)).doWarmUp();
    }

    @Test
    public void testDelegateFailureAtWarmUp() {
        Mockito.when(delegateResolver.getClusterEndpoints()).thenReturn(null);
        // override the scheduling which will be triggered immediately if warmUp fails (as is intended).
        // do this to avoid thread race conditions for a more predictable test
        Mockito.doNothing().when(resolver).scheduleTask(ArgumentMatchers.anyLong());
        List endpoints = resolver.getClusterEndpoints();
        Assert.assertThat(endpoints.isEmpty(), CoreMatchers.is(true));
        Mockito.verify(delegateResolver, Mockito.times(1)).getClusterEndpoints();
        Mockito.verify(resolver, Mockito.times(1)).doWarmUp();
    }
}

