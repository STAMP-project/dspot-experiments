package com.netflix.discovery;


import MediaType.APPLICATION_JSON_TYPE;
import com.netflix.discovery.junit.resource.DiscoveryClientResource;
import com.netflix.discovery.shared.Applications;
import com.netflix.discovery.shared.transport.EurekaHttpClient;
import com.netflix.discovery.shared.transport.EurekaHttpResponse;
import com.netflix.discovery.shared.transport.SimpleEurekaHttpServer;
import com.netflix.discovery.util.EurekaEntityFunctions;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mockito;


public class EurekaEventListenerTest {
    private static final EurekaHttpClient requestHandler = Mockito.mock(EurekaHttpClient.class);

    private static SimpleEurekaHttpServer eurekaHttpServer;

    @Rule
    public DiscoveryClientResource discoveryClientResource = DiscoveryClientResource.newBuilder().withRegistration(true).withRegistryFetch(true).connectWith(EurekaEventListenerTest.eurekaHttpServer).build();

    static class CapturingEurekaEventListener implements EurekaEventListener {
        private volatile EurekaEvent event;

        @Override
        public void onEvent(EurekaEvent event) {
            this.event = event;
        }
    }

    @Test
    public void testCacheRefreshEvent() throws Exception {
        EurekaEventListenerTest.CapturingEurekaEventListener listener = new EurekaEventListenerTest.CapturingEurekaEventListener();
        Applications initialApps = EurekaEntityFunctions.toApplications(discoveryClientResource.getMyInstanceInfo());
        Mockito.when(EurekaEventListenerTest.requestHandler.getApplications()).thenReturn(EurekaHttpResponse.anEurekaHttpResponse(200, initialApps).type(APPLICATION_JSON_TYPE).build());
        DiscoveryClient client = ((DiscoveryClient) (discoveryClientResource.getClient()));
        client.registerEventListener(listener);
        client.refreshRegistry();
        Assert.assertNotNull(listener.event);
        Assert.assertThat(listener.event, CoreMatchers.is(CoreMatchers.instanceOf(CacheRefreshedEvent.class)));
    }
}

