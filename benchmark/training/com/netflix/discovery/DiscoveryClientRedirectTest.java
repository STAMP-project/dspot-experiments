package com.netflix.discovery;


import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.junit.resource.DiscoveryClientResource;
import com.netflix.discovery.shared.Application;
import com.netflix.discovery.shared.Applications;
import com.netflix.discovery.util.EurekaEntityFunctions;
import com.netflix.discovery.util.InstanceInfoGenerator;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import org.junit.Rule;
import org.junit.Test;
import org.mockserver.client.server.MockServerClient;
import org.mockserver.junit.MockServerRule;
import org.mockserver.matchers.Times;
import org.mockserver.model.Header;


/**
 *
 *
 * @author Tomasz Bak
 */
public class DiscoveryClientRedirectTest {
    static class MockClientHolder {
        MockServerClient client;
    }

    private final InstanceInfo myInstanceInfo = InstanceInfoGenerator.takeOne();

    @Rule
    public MockServerRule redirectServerMockRule = new MockServerRule(this);

    private MockServerClient redirectServerMockClient;

    private DiscoveryClientRedirectTest.MockClientHolder targetServerMockClient = new DiscoveryClientRedirectTest.MockClientHolder();

    @Rule
    public MockServerRule targetServerMockRule = new MockServerRule(targetServerMockClient);

    @Rule
    public DiscoveryClientResource registryFetchClientRule = DiscoveryClientResource.newBuilder().withRegistration(false).withRegistryFetch(true).withPortResolver(new Callable<Integer>() {
        @Override
        public Integer call() throws Exception {
            return redirectServerMockRule.getHttpPort();
        }
    }).withInstanceInfo(myInstanceInfo).build();

    @Rule
    public DiscoveryClientResource registeringClientRule = DiscoveryClientResource.newBuilder().withRegistration(true).withRegistryFetch(false).withPortResolver(new Callable<Integer>() {
        @Override
        public Integer call() throws Exception {
            return redirectServerMockRule.getHttpPort();
        }
    }).withInstanceInfo(myInstanceInfo).build();

    private String targetServerBaseUri;

    private final InstanceInfoGenerator dataGenerator = InstanceInfoGenerator.newBuilder(2, 1).withMetaData(true).build();

    @Test
    public void testClientQueryFollowsRedirectsAndPinsToTargetServer() throws Exception {
        Applications fullFetchApps = dataGenerator.takeDelta(1);
        String fullFetchJson = DiscoveryClientRedirectTest.toJson(fullFetchApps);
        Applications deltaFetchApps = dataGenerator.takeDelta(1);
        String deltaFetchJson = DiscoveryClientRedirectTest.toJson(deltaFetchApps);
        redirectServerMockClient.when(request().withMethod("GET").withPath("/eureka/v2/apps/")).respond(response().withStatusCode(302).withHeader(new Header("Location", ((targetServerBaseUri) + "/eureka/v2/apps/"))));
        targetServerMockClient.client.when(request().withMethod("GET").withPath("/eureka/v2/apps/")).respond(response().withStatusCode(200).withHeader(new Header("Content-Type", "application/json")).withBody(fullFetchJson));
        targetServerMockClient.client.when(request().withMethod("GET").withPath("/eureka/v2/apps/delta")).respond(response().withStatusCode(200).withHeader(new Header("Content-Type", "application/json")).withBody(deltaFetchJson));
        final EurekaClient client = registryFetchClientRule.getClient();
        DiscoveryClientRedirectTest.await(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                List<Application> applicationList = client.getApplications().getRegisteredApplications();
                return (!(applicationList.isEmpty())) && ((applicationList.get(0).getInstances().size()) == 2);
            }
        }, 1, TimeUnit.MINUTES);
        redirectServerMockClient.verify(request().withMethod("GET").withPath("/eureka/v2/apps/"), exactly(1));
        redirectServerMockClient.verify(request().withMethod("GET").withPath("/eureka/v2/apps/delta"), exactly(0));
        targetServerMockClient.client.verify(request().withMethod("GET").withPath("/eureka/v2/apps/"), exactly(1));
        targetServerMockClient.client.verify(request().withMethod("GET").withPath("/eureka/v2/apps/delta"), atLeast(1));
    }

    @Test
    public void testClientFallsBackToOriginalServerOnError() throws Exception {
        Applications fullFetchApps1 = dataGenerator.takeDelta(1);
        String fullFetchJson1 = DiscoveryClientRedirectTest.toJson(fullFetchApps1);
        Applications fullFetchApps2 = EurekaEntityFunctions.mergeApplications(fullFetchApps1, dataGenerator.takeDelta(1));
        String fullFetchJson2 = DiscoveryClientRedirectTest.toJson(fullFetchApps2);
        redirectServerMockClient.when(request().withMethod("GET").withPath("/eureka/v2/apps/")).respond(response().withStatusCode(302).withHeader(new Header("Location", ((targetServerBaseUri) + "/eureka/v2/apps/"))));
        targetServerMockClient.client.when(request().withMethod("GET").withPath("/eureka/v2/apps/"), Times.exactly(1)).respond(response().withStatusCode(200).withHeader(new Header("Content-Type", "application/json")).withBody(fullFetchJson1));
        targetServerMockClient.client.when(request().withMethod("GET").withPath("/eureka/v2/apps/delta"), Times.exactly(1)).respond(response().withStatusCode(500));
        redirectServerMockClient.when(request().withMethod("GET").withPath("/eureka/v2/apps/delta")).respond(response().withStatusCode(200).withHeader(new Header("Content-Type", "application/json")).withBody(fullFetchJson2));
        final EurekaClient client = registryFetchClientRule.getClient();
        DiscoveryClientRedirectTest.await(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                List<Application> applicationList = client.getApplications().getRegisteredApplications();
                return (!(applicationList.isEmpty())) && ((applicationList.get(0).getInstances().size()) == 2);
            }
        }, 1, TimeUnit.MINUTES);
        redirectServerMockClient.verify(request().withMethod("GET").withPath("/eureka/v2/apps/"), exactly(1));
        redirectServerMockClient.verify(request().withMethod("GET").withPath("/eureka/v2/apps/delta"), exactly(1));
        targetServerMockClient.client.verify(request().withMethod("GET").withPath("/eureka/v2/apps/"), exactly(1));
        targetServerMockClient.client.verify(request().withMethod("GET").withPath("/eureka/v2/apps/delta"), exactly(1));
    }
}

