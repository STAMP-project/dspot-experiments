package com.netflix.eureka.resources;


import Status.CONFLICT;
import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.shared.transport.ClusterSampleData;
import com.netflix.eureka.EurekaServerContext;
import com.netflix.eureka.cluster.protocol.ReplicationInstance;
import com.netflix.eureka.cluster.protocol.ReplicationList;
import javax.ws.rs.core.Response;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


/**
 *
 *
 * @author Tomasz Bak
 */
public class PeerReplicationResourceTest {
    private final ApplicationResource applicationResource = Mockito.mock(ApplicationResource.class);

    private final InstanceResource instanceResource = Mockito.mock(InstanceResource.class);

    private EurekaServerContext serverContext;

    private PeerReplicationResource peerReplicationResource;

    private final InstanceInfo instanceInfo = ClusterSampleData.newInstanceInfo(0);

    @Test
    public void testRegisterBatching() throws Exception {
        ReplicationList replicationList = new ReplicationList(newReplicationInstanceOf(Action.Register, instanceInfo));
        Response response = peerReplicationResource.batchReplication(replicationList);
        PeerReplicationResourceTest.assertStatusOkReply(response);
        Mockito.verify(applicationResource, Mockito.times(1)).addInstance(instanceInfo, "true");
    }

    @Test
    public void testCancelBatching() throws Exception {
        Mockito.when(instanceResource.cancelLease(ArgumentMatchers.anyString())).thenReturn(Response.ok().build());
        ReplicationList replicationList = new ReplicationList(newReplicationInstanceOf(Action.Cancel, instanceInfo));
        Response response = peerReplicationResource.batchReplication(replicationList);
        PeerReplicationResourceTest.assertStatusOkReply(response);
        Mockito.verify(instanceResource, Mockito.times(1)).cancelLease("true");
    }

    @Test
    public void testHeartbeat() throws Exception {
        Mockito.when(instanceResource.renewLease(ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.anyString())).thenReturn(Response.ok().build());
        ReplicationInstance replicationInstance = newReplicationInstanceOf(Action.Heartbeat, instanceInfo);
        Response response = peerReplicationResource.batchReplication(new ReplicationList(replicationInstance));
        PeerReplicationResourceTest.assertStatusOkReply(response);
        Mockito.verify(instanceResource, Mockito.times(1)).renewLease("true", replicationInstance.getOverriddenStatus(), instanceInfo.getStatus().name(), Long.toString(replicationInstance.getLastDirtyTimestamp()));
    }

    @Test
    public void testConflictResponseReturnsTheInstanceInfoInTheResponseEntity() throws Exception {
        Mockito.when(instanceResource.renewLease(ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.anyString())).thenReturn(Response.status(CONFLICT).entity(instanceInfo).build());
        ReplicationInstance replicationInstance = newReplicationInstanceOf(Action.Heartbeat, instanceInfo);
        Response response = peerReplicationResource.batchReplication(new ReplicationList(replicationInstance));
        PeerReplicationResourceTest.assertStatusIsConflict(response);
        PeerReplicationResourceTest.assertResponseEntityExist(response);
    }

    @Test
    public void testStatusUpdate() throws Exception {
        Mockito.when(instanceResource.statusUpdate(ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.anyString())).thenReturn(Response.ok().build());
        ReplicationInstance replicationInstance = newReplicationInstanceOf(Action.StatusUpdate, instanceInfo);
        Response response = peerReplicationResource.batchReplication(new ReplicationList(replicationInstance));
        PeerReplicationResourceTest.assertStatusOkReply(response);
        Mockito.verify(instanceResource, Mockito.times(1)).statusUpdate(replicationInstance.getStatus(), "true", Long.toString(replicationInstance.getLastDirtyTimestamp()));
    }

    @Test
    public void testDeleteStatusOverride() throws Exception {
        Mockito.when(instanceResource.deleteStatusUpdate(ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.anyString())).thenReturn(Response.ok().build());
        ReplicationInstance replicationInstance = newReplicationInstanceOf(Action.DeleteStatusOverride, instanceInfo);
        Response response = peerReplicationResource.batchReplication(new ReplicationList(replicationInstance));
        PeerReplicationResourceTest.assertStatusOkReply(response);
        Mockito.verify(instanceResource, Mockito.times(1)).deleteStatusUpdate("true", replicationInstance.getStatus(), Long.toString(replicationInstance.getLastDirtyTimestamp()));
    }
}

