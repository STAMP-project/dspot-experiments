package io.searchbox.client;


import ESIntegTestCase.ClusterScope;
import ESIntegTestCase.Scope;
import io.searchbox.client.http.JestHttpClient;
import io.searchbox.cluster.Health;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.test.ESIntegTestCase;
import org.junit.Test;


/**
 *
 *
 * @author cihat keser
 */
@ClusterScope(scope = Scope.TEST, numDataNodes = 0)
public class JestClientFactoryIntegrationTest extends ESIntegTestCase {
    JestClientFactory factory = new JestClientFactory();

    @Test
    public void testDiscovery() throws IOException, InterruptedException {
        // wait for 4 active nodes
        internalCluster().ensureAtLeastNumDataNodes(4);
        assertEquals("All nodes in cluster should have HTTP endpoint exposed", 4, cluster().httpAddresses().length);
        factory.setHttpClientConfig(discoveryEnabled(true).discoveryFrequency(500L, TimeUnit.MILLISECONDS).build());
        try (JestHttpClient jestClient = ((JestHttpClient) (factory.getObject()))) {
            assertNotNull(jestClient);
            // wait for NodeChecker to do the discovery
            Thread.sleep(3000);
            assertEquals("All 4 nodes should be discovered and be in the client's server list", 4, jestClient.getServerPoolSize());
            internalCluster().ensureAtMostNumDataNodes(3);
            int numServers = 0;
            int retries = 0;
            while ((numServers != 3) && (retries < 30)) {
                numServers = jestClient.getServerPoolSize();
                retries++;
                Thread.sleep(1000);
            } 
            assertEquals("Only 3 nodes should be in Jest's list", 3, jestClient.getServerPoolSize());
        }
    }

    @Test
    public void testDiscoveryWithFiltering() throws IOException, InterruptedException {
        // wait for 3 active nodes
        internalCluster().ensureAtLeastNumDataNodes(3);
        // spin up two more client nodes with additional attributes
        Settings settings = // put some arbitrary attribute to filter by
        // for example, a client node
        Settings.builder().put(internalCluster().getDefaultSettings()).put("node.master", false).put("node.data", false).put("node.attr.type", "aardvark").build();
        String clientNode1 = internalCluster().startNode(settings);
        String clientNode2 = internalCluster().startNode(settings);
        assertNotEquals("client nodes should be different", clientNode1, clientNode2);
        assertEquals("All nodes in cluster should have HTTP endpoint exposed", 5, cluster().httpAddresses().length);
        factory.setHttpClientConfig(discoveryEnabled(true).discoveryFilter("type:aardvark").discoveryFrequency(500L, TimeUnit.MILLISECONDS).build());
        try (JestHttpClient jestClient = ((JestHttpClient) (factory.getObject()))) {
            assertNotNull(jestClient);
            // wait for NodeChecker to do the discovery
            Thread.sleep(3000);
            assertEquals("Only 2 nodes should be discovered and be in the client's server list", 2, jestClient.getServerPoolSize());
        }
    }

    @Test
    public void testIdleConnectionReaper() throws Exception {
        internalCluster().ensureAtLeastNumDataNodes(3);
        assertEquals("All nodes in cluster should have HTTP endpoint exposed", 3, cluster().httpAddresses().length);
        factory.setHttpClientConfig(discoveryEnabled(true).discoveryFrequency(100L, TimeUnit.MILLISECONDS).maxConnectionIdleTime(1500L, TimeUnit.MILLISECONDS).maxTotalConnection(75).defaultMaxTotalConnectionPerRoute(75).build());
        try (JestHttpClient jestClient = ((JestHttpClient) (factory.getObject()))) {
            assertNotNull(jestClient);
            Thread.sleep(300L);// Allow nodechecker to do it's thing and use at least one connection in the pool

            // Ask for the cluster health just to use some connections
            int maxPoolSize = getPoolSize(jestClient);
            for (int x = 0; x < 5; ++x) {
                jestClient.execute(new Health.Builder().build());
                maxPoolSize = Math.max(maxPoolSize, getPoolSize(jestClient));
            }
            Thread.sleep(3200);// Allow cxn reaper a chance to do it's thing

            int newPoolSize = getPoolSize(jestClient);
            // The new pool size should be much less than the maxPoolSize since the idle connection reaper will have run
            // twice in the time between maxPoolSize's last calculation and now.  There should really only be 1-2 connections
            // in the pool at this point since our idle timeout is set so low for this test.
            assertTrue((maxPoolSize > newPoolSize));
        }
    }

    @Test
    public void testNoIdleConnectionReaper() throws Exception {
        internalCluster().ensureAtLeastNumDataNodes(3);
        assertEquals("All nodes in cluster should have HTTP endpoint exposed", 3, cluster().httpAddresses().length);
        factory.setHttpClientConfig(discoveryEnabled(true).discoveryFrequency(100L, TimeUnit.MILLISECONDS).maxTotalConnection(75).defaultMaxTotalConnectionPerRoute(75).build());
        try (JestHttpClient jestClient = ((JestHttpClient) (factory.getObject()))) {
            assertNotNull(jestClient);
            Thread.sleep(300L);// Allow nodechecker to do it's thing and use at least one connection in the pool

            // Ask for the cluster health just to use some connections and create a little white noise
            int maxPoolSize = getPoolSize(jestClient);
            for (int x = 0; x < 5; ++x) {
                jestClient.execute(new Health.Builder().build());
                maxPoolSize = Math.max(maxPoolSize, getPoolSize(jestClient));
            }
            Thread.sleep(3000);// Allow for a quiesce period of no activity (except for nodechecker)

            int newPoolSize = getPoolSize(jestClient);
            // These two values being equal proves that connections returned to the pool stick around for some non-zero
            // duration of time while they wait to be re-leased.  It's impractical to prove in an integration test that they
            // can in fact stay around for over an hour without ever being used (by which time the server has most certainly
            // closed the connection).
            assertEquals(maxPoolSize, newPoolSize);
        }
    }
}

