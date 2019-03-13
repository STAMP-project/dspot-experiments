package com.netflix.discovery.shared.resolver.aws;


import com.netflix.discovery.EurekaClientConfig;
import com.netflix.discovery.shared.Application;
import com.netflix.discovery.shared.Applications;
import com.netflix.discovery.shared.transport.EurekaHttpClient;
import com.netflix.discovery.shared.transport.EurekaHttpClientFactory;
import com.netflix.discovery.shared.transport.EurekaHttpResponse;
import com.netflix.discovery.shared.transport.EurekaTransportConfig;
import java.util.List;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


/**
 *
 *
 * @author David Liu
 */
public class EurekaHttpResolverTest {
    private final EurekaClientConfig clientConfig = Mockito.mock(EurekaClientConfig.class);

    private final EurekaTransportConfig transportConfig = Mockito.mock(EurekaTransportConfig.class);

    private final EurekaHttpClientFactory clientFactory = Mockito.mock(EurekaHttpClientFactory.class);

    private final EurekaHttpClient httpClient = Mockito.mock(EurekaHttpClient.class);

    private Applications applications;

    private String vipAddress;

    private EurekaHttpResolver resolver;

    @Test
    public void testHappyCase() {
        List<AwsEndpoint> endpoints = resolver.getClusterEndpoints();
        Assert.assertThat(endpoints.size(), CoreMatchers.equalTo(applications.getInstancesByVirtualHostName(vipAddress).size()));
        Mockito.verify(httpClient, Mockito.times(1)).shutdown();
    }

    @Test
    public void testNoValidDataFromRemoteServer() {
        Applications newApplications = new Applications();
        for (Application application : applications.getRegisteredApplications()) {
            if (!(application.getInstances().get(0).getVIPAddress().equals(vipAddress))) {
                newApplications.addApplication(application);
            }
        }
        Mockito.when(httpClient.getVip(vipAddress)).thenReturn(EurekaHttpResponse.anEurekaHttpResponse(200, newApplications).build());
        List<AwsEndpoint> endpoints = resolver.getClusterEndpoints();
        Assert.assertThat(endpoints.isEmpty(), CoreMatchers.is(true));
        Mockito.verify(httpClient, Mockito.times(1)).shutdown();
    }

    @Test
    public void testErrorResponseFromRemoteServer() {
        Mockito.when(httpClient.getVip(vipAddress)).thenReturn(EurekaHttpResponse.anEurekaHttpResponse(500, ((Applications) (null))).build());
        List<AwsEndpoint> endpoints = resolver.getClusterEndpoints();
        Assert.assertThat(endpoints.isEmpty(), CoreMatchers.is(true));
        Mockito.verify(httpClient, Mockito.times(1)).shutdown();
    }
}

