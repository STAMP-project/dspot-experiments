package com.netflix.discovery.shared.resolver.aws;


import com.netflix.discovery.EurekaClientConfig;
import com.netflix.discovery.shared.Applications;
import com.netflix.discovery.shared.resolver.aws.ApplicationsResolver.ApplicationsSource;
import com.netflix.discovery.shared.transport.EurekaTransportConfig;
import java.util.List;
import java.util.concurrent.TimeUnit;
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
public class ApplicationsResolverTest {
    private final EurekaClientConfig clientConfig = Mockito.mock(EurekaClientConfig.class);

    private final EurekaTransportConfig transportConfig = Mockito.mock(EurekaTransportConfig.class);

    private final ApplicationsSource applicationsSource = Mockito.mock(ApplicationsSource.class);

    private Applications applications;

    private String vipAddress;

    private ApplicationsResolver resolver;

    @Test
    public void testHappyCase() {
        Mockito.when(applicationsSource.getApplications(ArgumentMatchers.anyInt(), ArgumentMatchers.eq(TimeUnit.SECONDS))).thenReturn(applications);
        List<AwsEndpoint> endpoints = resolver.getClusterEndpoints();
        Assert.assertThat(endpoints.size(), CoreMatchers.equalTo(applications.getInstancesByVirtualHostName(vipAddress).size()));
    }

    @Test
    public void testVipDoesNotExist() {
        vipAddress = "doNotExist";
        Mockito.when(transportConfig.getReadClusterVip()).thenReturn(vipAddress);
        resolver = // recreate the resolver as desired config behaviour has changed
        new ApplicationsResolver(clientConfig, transportConfig, applicationsSource, transportConfig.getReadClusterVip());
        Mockito.when(applicationsSource.getApplications(ArgumentMatchers.anyInt(), ArgumentMatchers.eq(TimeUnit.SECONDS))).thenReturn(applications);
        List<AwsEndpoint> endpoints = resolver.getClusterEndpoints();
        Assert.assertThat(endpoints.isEmpty(), CoreMatchers.is(true));
    }

    @Test
    public void testStaleData() {
        Mockito.when(applicationsSource.getApplications(ArgumentMatchers.anyInt(), ArgumentMatchers.eq(TimeUnit.SECONDS))).thenReturn(null);// stale contract

        List<AwsEndpoint> endpoints = resolver.getClusterEndpoints();
        Assert.assertThat(endpoints.isEmpty(), CoreMatchers.is(true));
    }
}

