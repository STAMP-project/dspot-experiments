package io.searchbox.client.http;


import ESIntegTestCase.ClusterScope;
import ESIntegTestCase.Scope;
import io.searchbox.client.JestClientFactory;
import io.searchbox.client.JestResult;
import io.searchbox.indices.Stats;
import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.elasticsearch.test.ESIntegTestCase;
import org.junit.Test;
import org.littleshoot.proxy.HttpProxyServer;


/**
 *
 *
 * @author cihat keser
 */
@ClusterScope(scope = Scope.TEST, numDataNodes = 0)
public class JestHttpClientSystemWideProxyIntegrationTest extends ESIntegTestCase {
    private static final int PROXY_PORT = 8790;

    private AtomicInteger numProxyRequests = new AtomicInteger(0);

    private JestClientFactory factory = new JestClientFactory();

    private HttpProxyServer server = null;

    private static String nonProxyHostsDefault;

    private static String proxyHostDefault;

    private static String proxyPortDefault;

    private static String useSystemProxiesDefault;

    @Test
    public void testConnectionThroughDefaultProxy() throws IOException, InterruptedException, ExecutionException {
        internalCluster().ensureAtLeastNumDataNodes(1);
        assertEquals("All nodes in cluster should have HTTP endpoint exposed", 1, cluster().httpAddresses().length);
        // test sync execution
        factory.setHttpClientConfig(build());
        try (JestHttpClient jestClient = ((JestHttpClient) (factory.getObject()))) {
            assertNotNull(jestClient);
            JestResult result = jestClient.execute(new Stats.Builder().build());
            assertTrue(result.getErrorMessage(), result.isSucceeded());
            assertEquals(1, numProxyRequests.intValue());
        }
        // test async execution
        factory.setHttpClientConfig(build());
        try (JestHttpClient jestClient = ((JestHttpClient) (factory.getObject()))) {
            assertNotNull(jestClient);
            final CountDownLatch actionExecuted = new CountDownLatch(1);
            jestClient.executeAsync(new Stats.Builder().build(), new io.searchbox.client.JestResultHandler<JestResult>() {
                @Override
                public void completed(JestResult result) {
                    actionExecuted.countDown();
                }

                @Override
                public void failed(Exception ex) {
                    throw new RuntimeException(ex);
                }
            });
            boolean finishedAsync = actionExecuted.await(2, TimeUnit.SECONDS);
            if (!finishedAsync) {
                fail("Execution took too long to complete");
            }
            assertEquals(2, numProxyRequests.intValue());
        }
    }
}

