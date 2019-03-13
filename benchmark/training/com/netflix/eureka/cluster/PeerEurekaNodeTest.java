package com.netflix.eureka.cluster;


import ASGStatus.DISABLED;
import Action.Cancel;
import Action.DeleteStatusOverride;
import Action.Heartbeat;
import Action.Register;
import Action.StatusUpdate;
import InstanceStatus.DOWN;
import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.shared.transport.ClusterSampleData;
import com.netflix.eureka.cluster.protocol.ReplicationInstance;
import com.netflix.eureka.registry.PeerAwareInstanceRegistry;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import static com.netflix.eureka.cluster.TestableHttpReplicationClient.RequestType.AsgStatusUpdate;
import static com.netflix.eureka.cluster.TestableHttpReplicationClient.RequestType.Batch;


/**
 *
 *
 * @author Tomasz Bak
 */
public class PeerEurekaNodeTest {
    private static final int BATCH_SIZE = 10;

    private static final long MAX_BATCHING_DELAY_MS = 10;

    private final PeerAwareInstanceRegistry registry = Mockito.mock(PeerAwareInstanceRegistry.class);

    private final TestableHttpReplicationClient httpReplicationClient = new TestableHttpReplicationClient();

    private final InstanceInfo instanceInfo = ClusterSampleData.newInstanceInfo(1);

    private PeerEurekaNode peerEurekaNode;

    @Test
    public void testRegistrationBatchReplication() throws Exception {
        createPeerEurekaNode().register(instanceInfo);
        ReplicationInstance replicationInstance = expectSingleBatchRequest();
        Assert.assertThat(replicationInstance.getAction(), CoreMatchers.is(CoreMatchers.equalTo(Register)));
    }

    @Test
    public void testCancelBatchReplication() throws Exception {
        createPeerEurekaNode().cancel(instanceInfo.getAppName(), instanceInfo.getId());
        ReplicationInstance replicationInstance = expectSingleBatchRequest();
        Assert.assertThat(replicationInstance.getAction(), CoreMatchers.is(CoreMatchers.equalTo(Cancel)));
    }

    @Test
    public void testHeartbeatBatchReplication() throws Throwable {
        createPeerEurekaNode().heartbeat(instanceInfo.getAppName(), instanceInfo.getId(), instanceInfo, null, false);
        ReplicationInstance replicationInstance = expectSingleBatchRequest();
        Assert.assertThat(replicationInstance.getAction(), CoreMatchers.is(CoreMatchers.equalTo(Heartbeat)));
    }

    @Test
    public void testHeartbeatReplicationFailure() throws Throwable {
        httpReplicationClient.withNetworkStatusCode(200, 200);
        httpReplicationClient.withBatchReply(404);// Not found, to trigger registration

        createPeerEurekaNode().heartbeat(instanceInfo.getAppName(), instanceInfo.getId(), instanceInfo, null, false);
        // Heartbeat replied with an error
        ReplicationInstance replicationInstance = expectSingleBatchRequest();
        Assert.assertThat(replicationInstance.getAction(), CoreMatchers.is(CoreMatchers.equalTo(Heartbeat)));
        // Second, registration task is scheduled
        replicationInstance = expectSingleBatchRequest();
        Assert.assertThat(replicationInstance.getAction(), CoreMatchers.is(CoreMatchers.equalTo(Register)));
    }

    @Test
    public void testHeartbeatWithInstanceInfoFromPeer() throws Throwable {
        InstanceInfo instanceInfoFromPeer = ClusterSampleData.newInstanceInfo(2);
        httpReplicationClient.withNetworkStatusCode(200);
        httpReplicationClient.withBatchReply(400);
        httpReplicationClient.withInstanceInfo(instanceInfoFromPeer);
        // InstanceInfo in response from peer will trigger local registry call
        createPeerEurekaNode().heartbeat(instanceInfo.getAppName(), instanceInfo.getId(), instanceInfo, null, false);
        expectRequestType(Batch);
        // Check that registry has instanceInfo from peer
        Mockito.verify(registry, Mockito.timeout(1000).times(1)).register(instanceInfoFromPeer, true);
    }

    @Test
    public void testAsgStatusUpdate() throws Throwable {
        createPeerEurekaNode().statusUpdate(instanceInfo.getASGName(), DISABLED);
        Object newAsgStatus = expectRequestType(AsgStatusUpdate);
        Assert.assertThat(newAsgStatus, CoreMatchers.is(CoreMatchers.equalTo(((Object) (DISABLED)))));
    }

    @Test
    public void testStatusUpdateBatchReplication() throws Throwable {
        createPeerEurekaNode().statusUpdate(instanceInfo.getAppName(), instanceInfo.getId(), DOWN, instanceInfo);
        ReplicationInstance replicationInstance = expectSingleBatchRequest();
        Assert.assertThat(replicationInstance.getAction(), CoreMatchers.is(CoreMatchers.equalTo(StatusUpdate)));
    }

    @Test
    public void testDeleteStatusOverrideBatchReplication() throws Throwable {
        createPeerEurekaNode().deleteStatusOverride(instanceInfo.getAppName(), instanceInfo.getId(), instanceInfo);
        ReplicationInstance replicationInstance = expectSingleBatchRequest();
        Assert.assertThat(replicationInstance.getAction(), CoreMatchers.is(CoreMatchers.equalTo(DeleteStatusOverride)));
    }
}

