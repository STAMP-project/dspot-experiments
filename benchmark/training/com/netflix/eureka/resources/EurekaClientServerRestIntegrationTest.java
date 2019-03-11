package com.netflix.eureka.resources;


import Action.Register;
import InstanceStatus.DOWN;
import InstanceStatus.UNKNOWN;
import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.shared.transport.EurekaHttpClient;
import com.netflix.discovery.shared.transport.EurekaHttpResponse;
import com.netflix.discovery.shared.transport.TransportClientFactory;
import com.netflix.discovery.util.InstanceInfoGenerator;
import com.netflix.eureka.EurekaServerConfig;
import com.netflix.eureka.cluster.protocol.ReplicationInstance;
import com.netflix.eureka.cluster.protocol.ReplicationInstanceResponse;
import com.netflix.eureka.cluster.protocol.ReplicationListResponse;
import com.netflix.eureka.transport.JerseyReplicationClient;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;
import org.eclipse.jetty.server.Server;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test REST layer of client/server communication. This test instantiates fully configured Jersey container,
 * which is essential to verifying content encoding/decoding with different format types (JSON vs XML, compressed vs
 * uncompressed).
 *
 * @author Tomasz Bak
 */
public class EurekaClientServerRestIntegrationTest {
    private static final String[] EUREKA1_WAR_DIRS = new String[]{ "build/libs", "eureka-server/build/libs" };

    private static final Pattern WAR_PATTERN = Pattern.compile("eureka-server.*.war");

    private static EurekaServerConfig eurekaServerConfig;

    private static Server server;

    private static TransportClientFactory httpClientFactory;

    private static EurekaHttpClient jerseyEurekaClient;

    private static JerseyReplicationClient jerseyReplicationClient;

    /**
     * We do not include ASG data to prevent server from consulting AWS for its status.
     */
    private static final InstanceInfoGenerator infoGenerator = InstanceInfoGenerator.newBuilder(10, 2).withAsg(false).build();

    private static final Iterator<InstanceInfo> instanceInfoIt = EurekaClientServerRestIntegrationTest.infoGenerator.serviceIterator();

    private static String eurekaServiceUrl;

    @Test
    public void testRegistration() throws Exception {
        InstanceInfo instanceInfo = EurekaClientServerRestIntegrationTest.instanceInfoIt.next();
        EurekaHttpResponse<Void> httpResponse = EurekaClientServerRestIntegrationTest.jerseyEurekaClient.register(instanceInfo);
        Assert.assertThat(httpResponse.getStatusCode(), CoreMatchers.is(CoreMatchers.equalTo(204)));
    }

    @Test
    public void testHeartbeat() throws Exception {
        // Register first
        InstanceInfo instanceInfo = EurekaClientServerRestIntegrationTest.instanceInfoIt.next();
        EurekaClientServerRestIntegrationTest.jerseyEurekaClient.register(instanceInfo);
        // Now send heartbeat
        EurekaHttpResponse<InstanceInfo> heartBeatResponse = EurekaClientServerRestIntegrationTest.jerseyReplicationClient.sendHeartBeat(instanceInfo.getAppName(), instanceInfo.getId(), instanceInfo, null);
        Assert.assertThat(heartBeatResponse.getStatusCode(), CoreMatchers.is(CoreMatchers.equalTo(200)));
        Assert.assertThat(heartBeatResponse.getEntity(), CoreMatchers.is(CoreMatchers.nullValue()));
    }

    @Test
    public void testMissedHeartbeat() throws Exception {
        InstanceInfo instanceInfo = EurekaClientServerRestIntegrationTest.instanceInfoIt.next();
        // Now send heartbeat
        EurekaHttpResponse<InstanceInfo> heartBeatResponse = EurekaClientServerRestIntegrationTest.jerseyReplicationClient.sendHeartBeat(instanceInfo.getAppName(), instanceInfo.getId(), instanceInfo, null);
        Assert.assertThat(heartBeatResponse.getStatusCode(), CoreMatchers.is(CoreMatchers.equalTo(404)));
    }

    @Test
    public void testCancelForEntryThatExists() throws Exception {
        // Register first
        InstanceInfo instanceInfo = EurekaClientServerRestIntegrationTest.instanceInfoIt.next();
        EurekaClientServerRestIntegrationTest.jerseyEurekaClient.register(instanceInfo);
        // Now cancel
        EurekaHttpResponse<Void> httpResponse = EurekaClientServerRestIntegrationTest.jerseyEurekaClient.cancel(instanceInfo.getAppName(), instanceInfo.getId());
        Assert.assertThat(httpResponse.getStatusCode(), CoreMatchers.is(CoreMatchers.equalTo(200)));
    }

    @Test
    public void testCancelForEntryThatDoesNotExist() throws Exception {
        // Now cancel
        InstanceInfo instanceInfo = EurekaClientServerRestIntegrationTest.instanceInfoIt.next();
        EurekaHttpResponse<Void> httpResponse = EurekaClientServerRestIntegrationTest.jerseyEurekaClient.cancel(instanceInfo.getAppName(), instanceInfo.getId());
        Assert.assertThat(httpResponse.getStatusCode(), CoreMatchers.is(CoreMatchers.equalTo(404)));
    }

    @Test
    public void testStatusOverrideUpdateAndDelete() throws Exception {
        // Register first
        InstanceInfo instanceInfo = EurekaClientServerRestIntegrationTest.instanceInfoIt.next();
        EurekaClientServerRestIntegrationTest.jerseyEurekaClient.register(instanceInfo);
        // Now override status
        EurekaHttpResponse<Void> overrideUpdateResponse = EurekaClientServerRestIntegrationTest.jerseyEurekaClient.statusUpdate(instanceInfo.getAppName(), instanceInfo.getId(), DOWN, instanceInfo);
        Assert.assertThat(overrideUpdateResponse.getStatusCode(), CoreMatchers.is(CoreMatchers.equalTo(200)));
        InstanceInfo fetchedInstance = EurekaClientServerRestIntegrationTest.expectInstanceInfoInRegistry(instanceInfo);
        Assert.assertThat(fetchedInstance.getStatus(), CoreMatchers.is(CoreMatchers.equalTo(DOWN)));
        // Now remove override
        EurekaHttpResponse<Void> deleteOverrideResponse = EurekaClientServerRestIntegrationTest.jerseyEurekaClient.deleteStatusOverride(instanceInfo.getAppName(), instanceInfo.getId(), instanceInfo);
        Assert.assertThat(deleteOverrideResponse.getStatusCode(), CoreMatchers.is(CoreMatchers.equalTo(200)));
        fetchedInstance = EurekaClientServerRestIntegrationTest.expectInstanceInfoInRegistry(instanceInfo);
        Assert.assertThat(fetchedInstance.getStatus(), CoreMatchers.is(CoreMatchers.equalTo(UNKNOWN)));
    }

    @Test
    public void testBatch() throws Exception {
        InstanceInfo instanceInfo = EurekaClientServerRestIntegrationTest.instanceInfoIt.next();
        ReplicationInstance replicationInstance = ReplicationInstance.replicationInstance().withAction(Register).withAppName(instanceInfo.getAppName()).withId(instanceInfo.getId()).withInstanceInfo(instanceInfo).withLastDirtyTimestamp(System.currentTimeMillis()).withStatus(instanceInfo.getStatus().name()).build();
        EurekaHttpResponse<ReplicationListResponse> httpResponse = EurekaClientServerRestIntegrationTest.jerseyReplicationClient.submitBatchUpdates(new com.netflix.eureka.cluster.protocol.ReplicationList(replicationInstance));
        Assert.assertThat(httpResponse.getStatusCode(), CoreMatchers.is(CoreMatchers.equalTo(200)));
        List<ReplicationInstanceResponse> replicationListResponse = httpResponse.getEntity().getResponseList();
        Assert.assertThat(replicationListResponse.size(), CoreMatchers.is(CoreMatchers.equalTo(1)));
        Assert.assertThat(replicationListResponse.get(0).getStatusCode(), CoreMatchers.is(CoreMatchers.equalTo(200)));
    }
}

