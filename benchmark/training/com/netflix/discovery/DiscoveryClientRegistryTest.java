package com.netflix.discovery;


import InstanceStatus.DOWN;
import InstanceStatus.UP;
import MediaType.APPLICATION_JSON_TYPE;
import com.netflix.appinfo.DataCenterInfo;
import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.junit.resource.DiscoveryClientResource;
import com.netflix.discovery.shared.Applications;
import com.netflix.discovery.shared.transport.EurekaHttpClient;
import com.netflix.discovery.shared.transport.EurekaHttpResponse;
import com.netflix.discovery.shared.transport.SimpleEurekaHttpServer;
import com.netflix.discovery.util.EurekaEntityFunctions;
import com.netflix.discovery.util.InstanceInfoGenerator;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


/**
 *
 *
 * @author Nitesh Kant
 */
public class DiscoveryClientRegistryTest {
    private static final String TEST_LOCAL_REGION = "us-east-1";

    private static final String TEST_REMOTE_REGION = "us-west-2";

    private static final String TEST_REMOTE_ZONE = "us-west-2c";

    private static final EurekaHttpClient requestHandler = Mockito.mock(EurekaHttpClient.class);

    private static SimpleEurekaHttpServer eurekaHttpServer;

    @Rule
    public DiscoveryClientResource discoveryClientResource = DiscoveryClientResource.newBuilder().withRegistration(false).withRegistryFetch(true).withRemoteRegions(DiscoveryClientRegistryTest.TEST_REMOTE_REGION).connectWith(DiscoveryClientRegistryTest.eurekaHttpServer).build();

    @Test
    public void testGetByVipInLocalRegion() throws Exception {
        Applications applications = InstanceInfoGenerator.newBuilder(4, "app1", "app2").build().toApplications();
        InstanceInfo instance = applications.getRegisteredApplications("app1").getInstances().get(0);
        Mockito.when(DiscoveryClientRegistryTest.requestHandler.getApplications(DiscoveryClientRegistryTest.TEST_REMOTE_REGION)).thenReturn(EurekaHttpResponse.anEurekaHttpResponse(200, applications).type(APPLICATION_JSON_TYPE).build());
        List<InstanceInfo> result = discoveryClientResource.getClient().getInstancesByVipAddress(instance.getVIPAddress(), false);
        Assert.assertThat(result.size(), CoreMatchers.is(CoreMatchers.equalTo(2)));
        Assert.assertThat(result.get(0).getVIPAddress(), CoreMatchers.is(CoreMatchers.equalTo(instance.getVIPAddress())));
    }

    @Test
    public void testGetAllKnownRegions() throws Exception {
        prepareRemoteRegionRegistry();
        EurekaClient client = discoveryClientResource.getClient();
        Set<String> allKnownRegions = client.getAllKnownRegions();
        Assert.assertThat(allKnownRegions.size(), CoreMatchers.is(CoreMatchers.equalTo(2)));
        Assert.assertThat(allKnownRegions, CoreMatchers.hasItem(DiscoveryClientRegistryTest.TEST_REMOTE_REGION));
    }

    @Test
    public void testAllAppsForRegions() throws Exception {
        prepareRemoteRegionRegistry();
        EurekaClient client = discoveryClientResource.getClient();
        Applications appsForRemoteRegion = client.getApplicationsForARegion(DiscoveryClientRegistryTest.TEST_REMOTE_REGION);
        Assert.assertThat(EurekaEntityFunctions.countInstances(appsForRemoteRegion), CoreMatchers.is(CoreMatchers.equalTo(4)));
        Applications appsForLocalRegion = client.getApplicationsForARegion(DiscoveryClientRegistryTest.TEST_LOCAL_REGION);
        Assert.assertThat(EurekaEntityFunctions.countInstances(appsForLocalRegion), CoreMatchers.is(CoreMatchers.equalTo(4)));
    }

    @Test
    public void testCacheRefreshSingleAppForLocalRegion() throws Exception {
        InstanceInfoGenerator instanceGen = InstanceInfoGenerator.newBuilder(2, "testApp").build();
        Applications initialApps = instanceGen.takeDelta(1);
        String vipAddress = initialApps.getRegisteredApplications().get(0).getInstances().get(0).getVIPAddress();
        DiscoveryClientResource vipClientResource = discoveryClientResource.fork().withVipFetch(vipAddress).build();
        // Take first portion
        Mockito.when(DiscoveryClientRegistryTest.requestHandler.getVip(vipAddress, DiscoveryClientRegistryTest.TEST_REMOTE_REGION)).thenReturn(EurekaHttpResponse.anEurekaHttpResponse(200, initialApps).type(APPLICATION_JSON_TYPE).build());
        EurekaClient vipClient = vipClientResource.getClient();
        Assert.assertThat(EurekaEntityFunctions.countInstances(vipClient.getApplications()), CoreMatchers.is(CoreMatchers.equalTo(1)));
        // Now second one
        Mockito.when(DiscoveryClientRegistryTest.requestHandler.getVip(vipAddress, DiscoveryClientRegistryTest.TEST_REMOTE_REGION)).thenReturn(EurekaHttpResponse.anEurekaHttpResponse(200, instanceGen.toApplications()).type(APPLICATION_JSON_TYPE).build());
        Assert.assertThat(vipClientResource.awaitCacheUpdate(5, TimeUnit.SECONDS), CoreMatchers.is(true));
        Assert.assertThat(EurekaEntityFunctions.countInstances(vipClient.getApplications()), CoreMatchers.is(CoreMatchers.equalTo(2)));
    }

    @Test
    public void testEurekaClientPeriodicHeartbeat() throws Exception {
        DiscoveryClientResource registeringClientResource = discoveryClientResource.fork().withRegistration(true).withRegistryFetch(false).build();
        InstanceInfo instance = registeringClientResource.getMyInstanceInfo();
        Mockito.when(DiscoveryClientRegistryTest.requestHandler.register(ArgumentMatchers.any(InstanceInfo.class))).thenReturn(EurekaHttpResponse.status(204));
        Mockito.when(DiscoveryClientRegistryTest.requestHandler.sendHeartBeat(instance.getAppName(), instance.getId(), null, null)).thenReturn(EurekaHttpResponse.anEurekaHttpResponse(200, InstanceInfo.class).build());
        registeringClientResource.getClient();// Initialize

        Mockito.verify(DiscoveryClientRegistryTest.requestHandler, Mockito.timeout((5 * 1000)).atLeast(2)).sendHeartBeat(instance.getAppName(), instance.getId(), null, null);
    }

    @Test
    public void testEurekaClientPeriodicCacheRefresh() throws Exception {
        InstanceInfoGenerator instanceGen = InstanceInfoGenerator.newBuilder(3, 1).build();
        Applications initialApps = instanceGen.takeDelta(1);
        // Full fetch
        Mockito.when(DiscoveryClientRegistryTest.requestHandler.getApplications(DiscoveryClientRegistryTest.TEST_REMOTE_REGION)).thenReturn(EurekaHttpResponse.anEurekaHttpResponse(200, initialApps).type(APPLICATION_JSON_TYPE).build());
        EurekaClient client = discoveryClientResource.getClient();
        Assert.assertThat(EurekaEntityFunctions.countInstances(client.getApplications()), CoreMatchers.is(CoreMatchers.equalTo(1)));
        // Delta 1
        Mockito.when(DiscoveryClientRegistryTest.requestHandler.getDelta(DiscoveryClientRegistryTest.TEST_REMOTE_REGION)).thenReturn(EurekaHttpResponse.anEurekaHttpResponse(200, instanceGen.takeDelta(1)).type(APPLICATION_JSON_TYPE).build());
        Assert.assertThat(discoveryClientResource.awaitCacheUpdate(5, TimeUnit.SECONDS), CoreMatchers.is(true));
        // Delta 2
        Mockito.when(DiscoveryClientRegistryTest.requestHandler.getDelta(DiscoveryClientRegistryTest.TEST_REMOTE_REGION)).thenReturn(EurekaHttpResponse.anEurekaHttpResponse(200, instanceGen.takeDelta(1)).type(APPLICATION_JSON_TYPE).build());
        Assert.assertThat(discoveryClientResource.awaitCacheUpdate(5, TimeUnit.SECONDS), CoreMatchers.is(true));
        Assert.assertThat(EurekaEntityFunctions.countInstances(client.getApplications()), CoreMatchers.is(CoreMatchers.equalTo(3)));
    }

    @Test
    public void testGetInvalidVIP() throws Exception {
        Applications applications = InstanceInfoGenerator.newBuilder(1, "testApp").build().toApplications();
        Mockito.when(DiscoveryClientRegistryTest.requestHandler.getApplications(DiscoveryClientRegistryTest.TEST_REMOTE_REGION)).thenReturn(EurekaHttpResponse.anEurekaHttpResponse(200, applications).type(APPLICATION_JSON_TYPE).build());
        EurekaClient client = discoveryClientResource.getClient();
        Assert.assertThat(EurekaEntityFunctions.countInstances(client.getApplications()), CoreMatchers.is(CoreMatchers.equalTo(1)));
        List<InstanceInfo> instancesByVipAddress = client.getInstancesByVipAddress("XYZ", false);
        Assert.assertThat(instancesByVipAddress.isEmpty(), CoreMatchers.is(true));
    }

    @Test
    public void testGetInvalidVIPForRemoteRegion() throws Exception {
        prepareRemoteRegionRegistry();
        EurekaClient client = discoveryClientResource.getClient();
        List<InstanceInfo> instancesByVipAddress = client.getInstancesByVipAddress("XYZ", false, DiscoveryClientRegistryTest.TEST_REMOTE_REGION);
        Assert.assertThat(instancesByVipAddress.isEmpty(), CoreMatchers.is(true));
    }

    @Test
    public void testGetByVipInRemoteRegion() throws Exception {
        prepareRemoteRegionRegistry();
        EurekaClient client = discoveryClientResource.getClient();
        String vipAddress = EurekaEntityFunctions.takeFirst(client.getApplicationsForARegion(DiscoveryClientRegistryTest.TEST_REMOTE_REGION)).getVIPAddress();
        List<InstanceInfo> instancesByVipAddress = client.getInstancesByVipAddress(vipAddress, false, DiscoveryClientRegistryTest.TEST_REMOTE_REGION);
        Assert.assertThat(instancesByVipAddress.size(), CoreMatchers.is(CoreMatchers.equalTo(2)));
        InstanceInfo instance = instancesByVipAddress.iterator().next();
        Assert.assertThat(instance.getVIPAddress(), CoreMatchers.is(CoreMatchers.equalTo(vipAddress)));
    }

    @Test
    public void testAppsHashCodeAfterRefresh() throws Exception {
        InstanceInfoGenerator instanceGen = InstanceInfoGenerator.newBuilder(2, "testApp").build();
        // Full fetch with one item
        InstanceInfo first = instanceGen.first();
        Applications initial = EurekaEntityFunctions.toApplications(first);
        Mockito.when(DiscoveryClientRegistryTest.requestHandler.getApplications(DiscoveryClientRegistryTest.TEST_REMOTE_REGION)).thenReturn(EurekaHttpResponse.anEurekaHttpResponse(200, initial).type(APPLICATION_JSON_TYPE).build());
        EurekaClient client = discoveryClientResource.getClient();
        Assert.assertThat(client.getApplications().getAppsHashCode(), CoreMatchers.is(CoreMatchers.equalTo("UP_1_")));
        // Delta with one add
        InstanceInfo second = new InstanceInfo.Builder(instanceGen.take(1)).setStatus(DOWN).build();
        Applications delta = EurekaEntityFunctions.toApplications(second);
        delta.setAppsHashCode("DOWN_1_UP_1_");
        Mockito.when(DiscoveryClientRegistryTest.requestHandler.getDelta(DiscoveryClientRegistryTest.TEST_REMOTE_REGION)).thenReturn(EurekaHttpResponse.anEurekaHttpResponse(200, delta).type(APPLICATION_JSON_TYPE).build());
        Assert.assertThat(discoveryClientResource.awaitCacheUpdate(5, TimeUnit.SECONDS), CoreMatchers.is(true));
        Assert.assertThat(client.getApplications().getAppsHashCode(), CoreMatchers.is(CoreMatchers.equalTo("DOWN_1_UP_1_")));
    }

    @Test
    public void testApplyDeltaWithBadInstanceInfoDataCenterInfoAsNull() throws Exception {
        InstanceInfoGenerator instanceGen = InstanceInfoGenerator.newBuilder(2, "testApp").build();
        // Full fetch with one item
        InstanceInfo first = instanceGen.first();
        Applications initial = EurekaEntityFunctions.toApplications(first);
        Mockito.when(DiscoveryClientRegistryTest.requestHandler.getApplications(DiscoveryClientRegistryTest.TEST_REMOTE_REGION)).thenReturn(EurekaHttpResponse.anEurekaHttpResponse(200, initial).type(APPLICATION_JSON_TYPE).build());
        EurekaClient client = discoveryClientResource.getClient();
        Assert.assertThat(client.getApplications().getAppsHashCode(), CoreMatchers.is(CoreMatchers.equalTo("UP_1_")));
        // Delta with one add
        InstanceInfo second = setInstanceId("foo1").setStatus(DOWN).setDataCenterInfo(null).build();
        InstanceInfo third = setInstanceId("foo2").setStatus(UP).setDataCenterInfo(new DataCenterInfo() {
            @Override
            public Name getName() {
                return null;
            }
        }).build();
        Applications delta = EurekaEntityFunctions.toApplications(second, third);
        delta.setAppsHashCode("DOWN_1_UP_2_");
        Mockito.when(DiscoveryClientRegistryTest.requestHandler.getDelta(DiscoveryClientRegistryTest.TEST_REMOTE_REGION)).thenReturn(EurekaHttpResponse.anEurekaHttpResponse(200, delta).type(APPLICATION_JSON_TYPE).build());
        Assert.assertThat(discoveryClientResource.awaitCacheUpdate(5, TimeUnit.SECONDS), CoreMatchers.is(true));
        Assert.assertThat(client.getApplications().getAppsHashCode(), CoreMatchers.is(CoreMatchers.equalTo("DOWN_1_UP_2_")));
    }

    @Test
    public void testEurekaClientPeriodicCacheRefreshForDelete() throws Exception {
        InstanceInfoGenerator instanceGen = InstanceInfoGenerator.newBuilder(3, 1).build();
        Applications initialApps = instanceGen.takeDelta(2);
        Applications deltaForDelete = instanceGen.takeDeltaForDelete(true, 1);
        Mockito.when(DiscoveryClientRegistryTest.requestHandler.getApplications(DiscoveryClientRegistryTest.TEST_REMOTE_REGION)).thenReturn(EurekaHttpResponse.anEurekaHttpResponse(200, initialApps).type(APPLICATION_JSON_TYPE).build());
        EurekaClient client = discoveryClientResource.getClient();
        Assert.assertThat(EurekaEntityFunctions.countInstances(client.getApplications()), CoreMatchers.is(CoreMatchers.equalTo(2)));
        Mockito.when(DiscoveryClientRegistryTest.requestHandler.getDelta(DiscoveryClientRegistryTest.TEST_REMOTE_REGION)).thenReturn(EurekaHttpResponse.anEurekaHttpResponse(200, deltaForDelete).type(APPLICATION_JSON_TYPE).build());
        Assert.assertThat(discoveryClientResource.awaitCacheUpdate(5, TimeUnit.SECONDS), CoreMatchers.is(true));
        Assert.assertThat(client.getApplications().getRegisteredApplications().size(), CoreMatchers.is(CoreMatchers.equalTo(1)));
        Assert.assertThat(EurekaEntityFunctions.countInstances(client.getApplications()), CoreMatchers.is(CoreMatchers.equalTo(1)));
    }

    @Test
    public void testEurekaClientPeriodicCacheRefreshForDeleteAndNoApplication() throws Exception {
        InstanceInfoGenerator instanceGen = InstanceInfoGenerator.newBuilder(3, 1).build();
        Applications initialApps = instanceGen.takeDelta(1);
        Applications deltaForDelete = instanceGen.takeDeltaForDelete(true, 1);
        Mockito.when(DiscoveryClientRegistryTest.requestHandler.getApplications(DiscoveryClientRegistryTest.TEST_REMOTE_REGION)).thenReturn(EurekaHttpResponse.anEurekaHttpResponse(200, initialApps).type(APPLICATION_JSON_TYPE).build());
        EurekaClient client = discoveryClientResource.getClient();
        Assert.assertThat(EurekaEntityFunctions.countInstances(client.getApplications()), CoreMatchers.is(CoreMatchers.equalTo(1)));
        Mockito.when(DiscoveryClientRegistryTest.requestHandler.getDelta(DiscoveryClientRegistryTest.TEST_REMOTE_REGION)).thenReturn(EurekaHttpResponse.anEurekaHttpResponse(200, deltaForDelete).type(APPLICATION_JSON_TYPE).build());
        Assert.assertThat(discoveryClientResource.awaitCacheUpdate(5, TimeUnit.SECONDS), CoreMatchers.is(true));
        Assert.assertEquals(client.getApplications().getRegisteredApplications(), new ArrayList());
    }
}

