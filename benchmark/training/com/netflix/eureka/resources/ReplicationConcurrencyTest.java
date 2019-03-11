package com.netflix.eureka.resources;


import com.netflix.appinfo.ApplicationInfoManager;
import com.netflix.appinfo.InstanceInfo;
import com.netflix.appinfo.MyDataCenterInstanceConfig;
import com.netflix.discovery.DefaultEurekaClientConfig;
import com.netflix.discovery.EurekaClient;
import com.netflix.eureka.DefaultEurekaServerConfig;
import com.netflix.eureka.EurekaServerContext;
import com.netflix.eureka.cluster.PeerEurekaNodes;
import com.netflix.eureka.registry.PeerAwareInstanceRegistry;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;
import org.mockito.Mockito;


/**
 * A pseudo mock test to test concurrent scenarios to do with registration and replication of cluster setups
 * with 1+ eureka servers
 *
 * @author David Liu
 */
public class ReplicationConcurrencyTest {
    private String id;

    private String appName;

    private InstanceInfo instance1;

    private InstanceInfo instance2;

    private ReplicationConcurrencyTest.MockServer server1;

    private ReplicationConcurrencyTest.MockServer server2;

    private InstanceInfo server1Sees;

    private InstanceInfo server2Sees;

    /**
     * this test tests a scenario where multiple registration and update requests for a single client is sent to
     * different eureka servers before replication can occur between them
     */
    @Test
    public void testReplicationWithRegistrationAndUpdateOnDifferentServers() throws Exception {
        // now simulate server1 (delayed) replication to server2.
        // without batching this is done by server1 making a REST call to the register endpoint of server2 with
        // replication=true
        server2.applicationResource.addInstance(instance1, "true");
        // verify that server2's "newer" info is (or is not) overridden
        // server2 should still see instance2 even though server1 tried to replicate across server1
        InstanceInfo newServer2Sees = server2.registry.getInstanceByAppAndId(appName, id);
        MatcherAssert.assertThat(newServer2Sees.getStatus(), CoreMatchers.equalTo(instance2.getStatus()));
        // now let server2 replicate to server1
        server1.applicationResource.addInstance(newServer2Sees, "true");
        // verify that server1 now have the updated info from server2
        InstanceInfo newServer1Sees = server1.registry.getInstanceByAppAndId(appName, id);
        MatcherAssert.assertThat(newServer1Sees.getStatus(), CoreMatchers.equalTo(instance2.getStatus()));
    }

    private static class MockServer {
        public final ApplicationResource applicationResource;

        public final PeerReplicationResource replicationResource;

        public final PeerAwareInstanceRegistry registry;

        public MockServer(String appName, PeerEurekaNodes peerEurekaNodes) throws Exception {
            ApplicationInfoManager infoManager = new ApplicationInfoManager(new MyDataCenterInstanceConfig());
            DefaultEurekaServerConfig serverConfig = Mockito.spy(new DefaultEurekaServerConfig());
            DefaultEurekaClientConfig clientConfig = new DefaultEurekaClientConfig();
            ServerCodecs serverCodecs = new DefaultServerCodecs(serverConfig);
            EurekaClient eurekaClient = Mockito.mock(EurekaClient.class);
            Mockito.doReturn("true").when(serverConfig).getExperimental("registry.registration.ignoreIfDirtyTimestampIsOlder");
            this.registry = new com.netflix.eureka.registry.PeerAwareInstanceRegistryImpl(serverConfig, clientConfig, serverCodecs, eurekaClient);
            this.registry.init(peerEurekaNodes);
            this.applicationResource = new ApplicationResource(appName, serverConfig, registry);
            EurekaServerContext serverContext = Mockito.mock(EurekaServerContext.class);
            Mockito.when(serverContext.getServerConfig()).thenReturn(serverConfig);
            Mockito.when(serverContext.getRegistry()).thenReturn(registry);
            this.replicationResource = new PeerReplicationResource(serverContext);
        }
    }
}

