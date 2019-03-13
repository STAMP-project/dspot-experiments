package com.netflix.eureka.transport;


import ASGStatus.ENABLED;
import InstanceStatus.DOWN;
import Status.CONFLICT;
import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.shared.transport.ClusterSampleData;
import com.netflix.discovery.shared.transport.EurekaHttpResponse;
import com.netflix.eureka.DefaultEurekaServerConfig;
import com.netflix.eureka.EurekaServerConfig;
import com.netflix.eureka.resources.ServerCodecs;
import org.hamcrest.CoreMatchers;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.mockserver.client.server.MockServerClient;
import org.mockserver.junit.MockServerRule;


/**
 * Ideally we would test client/server REST layer together as an integration test, where server side has mocked
 * service layer. Right now server side REST has to much logic, so this test would be equal to testing everything.
 * Here we test only client side REST communication.
 *
 * @author Tomasz Bak
 */
public class Jersey2ReplicationClientTest {
    @Rule
    public MockServerRule serverMockRule = new MockServerRule(this);

    private MockServerClient serverMockClient;

    private Jersey2ReplicationClient replicationClient;

    private final EurekaServerConfig config = new DefaultEurekaServerConfig();

    private final ServerCodecs serverCodecs = new com.netflix.eureka.resources.DefaultServerCodecs(config);

    private final InstanceInfo instanceInfo = ClusterSampleData.newInstanceInfo(1);

    @Test
    public void testRegistrationReplication() throws Exception {
        serverMockClient.when(request().withMethod("POST").withHeader(header(PeerEurekaNode.HEADER_REPLICATION, "true")).withPath(("/eureka/v2/apps/" + (instanceInfo.getAppName())))).respond(response().withStatusCode(200));
        EurekaHttpResponse<Void> response = replicationClient.register(instanceInfo);
        Assert.assertThat(response.getStatusCode(), Is.is(CoreMatchers.equalTo(200)));
    }

    @Test
    public void testCancelReplication() throws Exception {
        serverMockClient.when(request().withMethod("DELETE").withHeader(header(PeerEurekaNode.HEADER_REPLICATION, "true")).withPath(((("/eureka/v2/apps/" + (instanceInfo.getAppName())) + '/') + (instanceInfo.getId())))).respond(response().withStatusCode(204));
        EurekaHttpResponse<Void> response = replicationClient.cancel(instanceInfo.getAppName(), instanceInfo.getId());
        Assert.assertThat(response.getStatusCode(), Is.is(CoreMatchers.equalTo(204)));
    }

    @Test
    public void testHeartbeatReplicationWithNoResponseBody() throws Exception {
        serverMockClient.when(request().withMethod("PUT").withHeader(header(PeerEurekaNode.HEADER_REPLICATION, "true")).withPath(((("/eureka/v2/apps/" + (instanceInfo.getAppName())) + '/') + (instanceInfo.getId())))).respond(response().withStatusCode(200));
        EurekaHttpResponse<InstanceInfo> response = replicationClient.sendHeartBeat(instanceInfo.getAppName(), instanceInfo.getId(), instanceInfo, DOWN);
        Assert.assertThat(response.getStatusCode(), Is.is(CoreMatchers.equalTo(200)));
        Assert.assertThat(response.getEntity(), Is.is(CoreMatchers.nullValue()));
    }

    @Test
    public void testHeartbeatReplicationWithResponseBody() throws Exception {
        InstanceInfo remoteInfo = new InstanceInfo(this.instanceInfo);
        remoteInfo.setStatus(DOWN);
        byte[] responseBody = Jersey2ReplicationClientTest.toGzippedJson(remoteInfo);
        serverMockClient.when(request().withMethod("PUT").withHeader(header(PeerEurekaNode.HEADER_REPLICATION, "true")).withPath(((("/eureka/v2/apps/" + (this.instanceInfo.getAppName())) + '/') + (this.instanceInfo.getId())))).respond(response().withStatusCode(CONFLICT.getStatusCode()).withHeader(header("Content-Type", MediaType.APPLICATION_JSON)).withHeader(header("Content-Encoding", "gzip")).withBody(responseBody));
        EurekaHttpResponse<InstanceInfo> response = replicationClient.sendHeartBeat(this.instanceInfo.getAppName(), this.instanceInfo.getId(), this.instanceInfo, null);
        Assert.assertThat(response.getStatusCode(), Is.is(CoreMatchers.equalTo(CONFLICT.getStatusCode())));
        Assert.assertThat(response.getEntity(), Is.is(CoreMatchers.notNullValue()));
    }

    @Test
    public void testAsgStatusUpdateReplication() throws Exception {
        serverMockClient.when(request().withMethod("PUT").withHeader(header(PeerEurekaNode.HEADER_REPLICATION, "true")).withPath((("/eureka/v2/asg/" + (instanceInfo.getASGName())) + "/status"))).respond(response().withStatusCode(200));
        EurekaHttpResponse<Void> response = replicationClient.statusUpdate(instanceInfo.getASGName(), ENABLED);
        Assert.assertThat(response.getStatusCode(), Is.is(CoreMatchers.equalTo(200)));
    }

    @Test
    public void testStatusUpdateReplication() throws Exception {
        serverMockClient.when(request().withMethod("PUT").withHeader(header(PeerEurekaNode.HEADER_REPLICATION, "true")).withPath((((("/eureka/v2/apps/" + (instanceInfo.getAppName())) + '/') + (instanceInfo.getId())) + "/status"))).respond(response().withStatusCode(200));
        EurekaHttpResponse<Void> response = replicationClient.statusUpdate(instanceInfo.getAppName(), instanceInfo.getId(), DOWN, instanceInfo);
        Assert.assertThat(response.getStatusCode(), Is.is(CoreMatchers.equalTo(200)));
    }

    @Test
    public void testDeleteStatusOverrideReplication() throws Exception {
        serverMockClient.when(request().withMethod("DELETE").withHeader(header(PeerEurekaNode.HEADER_REPLICATION, "true")).withPath((((("/eureka/v2/apps/" + (instanceInfo.getAppName())) + '/') + (instanceInfo.getId())) + "/status"))).respond(response().withStatusCode(204));
        EurekaHttpResponse<Void> response = replicationClient.deleteStatusOverride(instanceInfo.getAppName(), instanceInfo.getId(), instanceInfo);
        Assert.assertThat(response.getStatusCode(), Is.is(CoreMatchers.equalTo(204)));
    }
}

