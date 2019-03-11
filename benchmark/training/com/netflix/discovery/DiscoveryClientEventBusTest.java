package com.netflix.discovery;


import MediaType.APPLICATION_JSON_TYPE;
import com.netflix.discovery.junit.resource.DiscoveryClientResource;
import com.netflix.discovery.shared.Applications;
import com.netflix.discovery.shared.transport.EurekaHttpClient;
import com.netflix.discovery.shared.transport.EurekaHttpResponse;
import com.netflix.discovery.shared.transport.SimpleEurekaHttpServer;
import com.netflix.discovery.util.EurekaEntityFunctions;
import com.netflix.discovery.util.InstanceInfoGenerator;
import com.netflix.eventbus.spi.EventBus;
import com.netflix.eventbus.spi.Subscribe;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mockito;


/**
 *
 *
 * @author David Liu
 */
public class DiscoveryClientEventBusTest {
    private static final EurekaHttpClient requestHandler = Mockito.mock(EurekaHttpClient.class);

    private static SimpleEurekaHttpServer eurekaHttpServer;

    @Rule
    public DiscoveryClientResource discoveryClientResource = // we don't need the registration thread for status change
    DiscoveryClientResource.newBuilder().withRegistration(false).withRegistryFetch(true).connectWith(DiscoveryClientEventBusTest.eurekaHttpServer).build();

    @Test
    public void testStatusChangeEvent() throws Exception {
        final CountDownLatch eventLatch = new CountDownLatch(1);
        final List<StatusChangeEvent> receivedEvents = new ArrayList<StatusChangeEvent>();
        EventBus eventBus = discoveryClientResource.getEventBus();
        eventBus.registerSubscriber(new Object() {
            @Subscribe
            public void consume(StatusChangeEvent event) {
                receivedEvents.add(event);
                eventLatch.countDown();
            }
        });
        Applications initialApps = EurekaEntityFunctions.toApplications(discoveryClientResource.getMyInstanceInfo());
        Mockito.when(DiscoveryClientEventBusTest.requestHandler.getApplications()).thenReturn(EurekaHttpResponse.anEurekaHttpResponse(200, initialApps).type(APPLICATION_JSON_TYPE).build());
        discoveryClientResource.getClient();// Activates the client

        Assert.assertThat(eventLatch.await(10, TimeUnit.SECONDS), CoreMatchers.is(true));
        Assert.assertThat(receivedEvents.size(), CoreMatchers.is(CoreMatchers.equalTo(1)));
        Assert.assertThat(receivedEvents.get(0), CoreMatchers.is(CoreMatchers.notNullValue()));
    }

    @Test
    public void testCacheRefreshEvent() throws Exception {
        InstanceInfoGenerator instanceGen = InstanceInfoGenerator.newBuilder(2, "testApp").build();
        // Initial full fetch
        Applications initialApps = instanceGen.takeDelta(1);
        Mockito.when(DiscoveryClientEventBusTest.requestHandler.getApplications()).thenReturn(EurekaHttpResponse.anEurekaHttpResponse(200, initialApps).type(APPLICATION_JSON_TYPE).build());
        discoveryClientResource.getClient();// Activates the client

        // Delta update
        Applications delta = instanceGen.takeDelta(1);
        Mockito.when(DiscoveryClientEventBusTest.requestHandler.getDelta()).thenReturn(EurekaHttpResponse.anEurekaHttpResponse(200, delta).type(APPLICATION_JSON_TYPE).build());
        Assert.assertThat(discoveryClientResource.awaitCacheUpdate(10, TimeUnit.SECONDS), CoreMatchers.is(true));
    }
}

