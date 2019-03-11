/**
 * Copyright 2015 Netflix, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.netflix.discovery.shared.transport;


import ApplicationsResolver.ApplicationsSource;
import HttpHeaders.CONTENT_TYPE;
import com.netflix.appinfo.ApplicationInfoManager;
import com.netflix.appinfo.EurekaInstanceConfig;
import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.EurekaClientConfig;
import com.netflix.discovery.shared.Applications;
import com.netflix.discovery.shared.resolver.ClosableResolver;
import com.netflix.discovery.shared.resolver.ClusterResolver;
import com.netflix.discovery.shared.resolver.EurekaEndpoint;
import com.netflix.discovery.shared.resolver.aws.ApplicationsResolver;
import com.netflix.discovery.shared.resolver.aws.AwsEndpoint;
import com.netflix.discovery.shared.resolver.aws.EurekaHttpResolver;
import com.netflix.discovery.shared.resolver.aws.TestEurekaHttpResolver;
import com.netflix.discovery.shared.transport.jersey.Jersey1TransportClientFactories;
import com.netflix.discovery.util.EurekaEntityComparators;
import com.netflix.discovery.util.InstanceInfoGenerator;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientRequest;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.filter.ClientFilter;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


/**
 *
 *
 * @author Tomasz Bak
 */
public class EurekaHttpClientsTest {
    private static final InstanceInfo MY_INSTANCE = InstanceInfoGenerator.newBuilder(1, "myApp").build().first();

    private final EurekaInstanceConfig instanceConfig = Mockito.mock(EurekaInstanceConfig.class);

    private final ApplicationInfoManager applicationInfoManager = new ApplicationInfoManager(instanceConfig, EurekaHttpClientsTest.MY_INSTANCE);

    private final EurekaHttpClient writeRequestHandler = Mockito.mock(EurekaHttpClient.class);

    private final EurekaHttpClient readRequestHandler = Mockito.mock(EurekaHttpClient.class);

    private EurekaClientConfig clientConfig;

    private EurekaTransportConfig transportConfig;

    private SimpleEurekaHttpServer writeServer;

    private SimpleEurekaHttpServer readServer;

    private ClusterResolver<EurekaEndpoint> clusterResolver;

    private EurekaHttpClientFactory clientFactory;

    private String readServerURI;

    private final InstanceInfoGenerator instanceGen = InstanceInfoGenerator.newBuilder(2, 1).build();

    @Test
    public void testCanonicalClient() throws Exception {
        Applications apps = instanceGen.toApplications();
        Mockito.when(writeRequestHandler.getApplications()).thenReturn(EurekaHttpResponse.anEurekaHttpResponse(302, Applications.class).headers("Location", ((readServerURI) + "/v2/apps")).build());
        Mockito.when(readRequestHandler.getApplications()).thenReturn(EurekaHttpResponse.anEurekaHttpResponse(200, apps).headers(CONTENT_TYPE, "application/json").build());
        EurekaHttpClient eurekaHttpClient = clientFactory.newClient();
        EurekaHttpResponse<Applications> result = eurekaHttpClient.getApplications();
        Assert.assertThat(result.getStatusCode(), CoreMatchers.is(CoreMatchers.equalTo(200)));
        Assert.assertThat(EurekaEntityComparators.equal(result.getEntity(), apps), CoreMatchers.is(true));
    }

    @Test
    public void testCompositeBootstrapResolver() throws Exception {
        Applications applications = InstanceInfoGenerator.newBuilder(5, "eurekaWrite", "someOther").build().toApplications();
        Applications applications2 = InstanceInfoGenerator.newBuilder(2, "eurekaWrite", "someOther").build().toApplications();
        String vipAddress = applications.getRegisteredApplications("eurekaWrite").getInstances().get(0).getVIPAddress();
        // setup client config to use fixed root ips for testing
        Mockito.when(clientConfig.shouldUseDnsForFetchingServiceUrls()).thenReturn(false);
        Mockito.when(clientConfig.getEurekaServerServiceUrls(ArgumentMatchers.anyString())).thenReturn(Arrays.asList("http://foo:0"));// can use anything here

        Mockito.when(clientConfig.getRegion()).thenReturn("us-east-1");
        Mockito.when(transportConfig.getWriteClusterVip()).thenReturn(vipAddress);
        Mockito.when(transportConfig.getAsyncExecutorThreadPoolSize()).thenReturn(4);
        Mockito.when(transportConfig.getAsyncResolverRefreshIntervalMs()).thenReturn(400);
        Mockito.when(transportConfig.getAsyncResolverWarmUpTimeoutMs()).thenReturn(400);
        ApplicationsResolver.ApplicationsSource applicationsSource = Mockito.mock(ApplicationsSource.class);
        // second time
        // first time
        Mockito.when(applicationsSource.getApplications(ArgumentMatchers.anyInt(), ArgumentMatchers.eq(TimeUnit.SECONDS))).thenReturn(null).thenReturn(applications).thenReturn(null);// subsequent times

        EurekaHttpClient mockHttpClient = Mockito.mock(EurekaHttpClient.class);
        Mockito.when(mockHttpClient.getVip(ArgumentMatchers.eq(vipAddress))).thenReturn(EurekaHttpResponse.anEurekaHttpResponse(200, applications).build()).thenReturn(EurekaHttpResponse.anEurekaHttpResponse(200, applications2).build());// contains diff number of servers

        TransportClientFactory transportClientFactory = Mockito.mock(TransportClientFactory.class);
        Mockito.when(transportClientFactory.newClient(ArgumentMatchers.any(EurekaEndpoint.class))).thenReturn(mockHttpClient);
        ClosableResolver<AwsEndpoint> resolver = null;
        try {
            resolver = EurekaHttpClients.compositeBootstrapResolver(clientConfig, transportConfig, transportClientFactory, applicationInfoManager.getInfo(), applicationsSource);
            List endpoints = resolver.getClusterEndpoints();
            Assert.assertThat(endpoints.size(), CoreMatchers.equalTo(applications.getInstancesByVirtualHostName(vipAddress).size()));
            // wait for the second cycle that hits the app source
            Mockito.verify(applicationsSource, Mockito.timeout(3000).times(2)).getApplications(ArgumentMatchers.anyInt(), ArgumentMatchers.eq(TimeUnit.SECONDS));
            endpoints = resolver.getClusterEndpoints();
            Assert.assertThat(endpoints.size(), CoreMatchers.equalTo(applications.getInstancesByVirtualHostName(vipAddress).size()));
            // wait for the third cycle that triggers the mock http client (which is the third resolver cycle)
            // for the third cycle we have mocked the application resolver to return null data so should fall back
            // to calling the remote resolver again (which should return applications2)
            Mockito.verify(mockHttpClient, Mockito.timeout(3000).times(3)).getVip(ArgumentMatchers.anyString());
            endpoints = resolver.getClusterEndpoints();
            Assert.assertThat(endpoints.size(), CoreMatchers.equalTo(applications2.getInstancesByVirtualHostName(vipAddress).size()));
        } finally {
            if (resolver != null) {
                resolver.shutdown();
            }
        }
    }

    @Test
    public void testCanonicalResolver() throws Exception {
        Mockito.when(clientConfig.getEurekaServerURLContext()).thenReturn("context");
        Mockito.when(clientConfig.getRegion()).thenReturn("region");
        Mockito.when(transportConfig.getAsyncExecutorThreadPoolSize()).thenReturn(3);
        Mockito.when(transportConfig.getAsyncResolverRefreshIntervalMs()).thenReturn(400);
        Mockito.when(transportConfig.getAsyncResolverWarmUpTimeoutMs()).thenReturn(400);
        Applications applications = InstanceInfoGenerator.newBuilder(5, "eurekaRead", "someOther").build().toApplications();
        String vipAddress = applications.getRegisteredApplications("eurekaRead").getInstances().get(0).getVIPAddress();
        ApplicationsResolver.ApplicationsSource applicationsSource = Mockito.mock(ApplicationsSource.class);
        // first time
        Mockito.when(applicationsSource.getApplications(ArgumentMatchers.anyInt(), ArgumentMatchers.eq(TimeUnit.SECONDS))).thenReturn(null).thenReturn(applications);// subsequent times

        EurekaHttpClientFactory remoteResolverClientFactory = Mockito.mock(EurekaHttpClientFactory.class);
        EurekaHttpClient httpClient = Mockito.mock(EurekaHttpClient.class);
        Mockito.when(remoteResolverClientFactory.newClient()).thenReturn(httpClient);
        Mockito.when(httpClient.getVip(vipAddress)).thenReturn(EurekaHttpResponse.anEurekaHttpResponse(200, applications).build());
        EurekaHttpResolver remoteResolver = Mockito.spy(new TestEurekaHttpResolver(clientConfig, transportConfig, remoteResolverClientFactory, vipAddress));
        Mockito.when(transportConfig.getReadClusterVip()).thenReturn(vipAddress);
        ApplicationsResolver localResolver = Mockito.spy(new ApplicationsResolver(clientConfig, transportConfig, applicationsSource, transportConfig.getReadClusterVip()));
        ClosableResolver resolver = null;
        try {
            resolver = EurekaHttpClients.compositeQueryResolver(remoteResolver, localResolver, clientConfig, transportConfig, applicationInfoManager.getInfo());
            List endpoints = resolver.getClusterEndpoints();
            Assert.assertThat(endpoints.size(), CoreMatchers.equalTo(applications.getInstancesByVirtualHostName(vipAddress).size()));
            Mockito.verify(remoteResolver, Mockito.times(1)).getClusterEndpoints();
            Mockito.verify(localResolver, Mockito.times(1)).getClusterEndpoints();
            // wait for the second cycle that hits the app source
            Mockito.verify(applicationsSource, Mockito.timeout(3000).times(2)).getApplications(ArgumentMatchers.anyInt(), ArgumentMatchers.eq(TimeUnit.SECONDS));
            endpoints = resolver.getClusterEndpoints();
            Assert.assertThat(endpoints.size(), CoreMatchers.equalTo(applications.getInstancesByVirtualHostName(vipAddress).size()));
            Mockito.verify(remoteResolver, Mockito.times(1)).getClusterEndpoints();
            Mockito.verify(localResolver, Mockito.times(2)).getClusterEndpoints();
        } finally {
            if (resolver != null) {
                resolver.shutdown();
            }
        }
    }

    @Test
    public void testAddingAdditionalFilters() throws Exception {
        EurekaHttpClientsTest.TestFilter testFilter = new EurekaHttpClientsTest.TestFilter();
        Collection<ClientFilter> additionalFilters = Arrays.<ClientFilter>asList(testFilter);
        TransportClientFactory transportClientFactory = new Jersey1TransportClientFactories().newTransportClientFactory(clientConfig, additionalFilters, EurekaHttpClientsTest.MY_INSTANCE);
        EurekaHttpClient client = transportClientFactory.newClient(clusterResolver.getClusterEndpoints().get(0));
        client.getApplication("foo");
        Assert.assertThat(testFilter.await(30, TimeUnit.SECONDS), CoreMatchers.is(true));
    }

    private static class TestFilter extends ClientFilter {
        private final CountDownLatch latch = new CountDownLatch(1);

        @Override
        public ClientResponse handle(ClientRequest cr) throws ClientHandlerException {
            latch.countDown();
            return Mockito.mock(ClientResponse.class);
        }

        public boolean await(long timeout, TimeUnit unit) throws Exception {
            return latch.await(timeout, unit);
        }
    }
}

