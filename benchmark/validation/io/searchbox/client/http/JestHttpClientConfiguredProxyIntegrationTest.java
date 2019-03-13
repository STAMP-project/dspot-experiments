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
public class JestHttpClientConfiguredProxyIntegrationTest extends ESIntegTestCase {
    private static final int PROXY_PORT = 8770;

    private AtomicInteger numProxyRequests = new AtomicInteger(0);

    private JestClientFactory factory = new JestClientFactory();

    private HttpProxyServer server = null;

    @Test
    public void testConnectionThroughConfiguredProxy() throws IOException, InterruptedException, ExecutionException {
        internalCluster().ensureAtLeastNumDataNodes(1);
        assertEquals("All nodes in cluster should have HTTP endpoint exposed", 1, cluster().httpAddresses().length);
        // sanity check - assert we cant connect without configuring proxy
        factory.setHttpClientConfig(build());
        try (JestHttpClient customJestClient = ((JestHttpClient) (factory.getObject()))) {
            JestResult result = customJestClient.execute(new Stats.Builder().build());
            assertTrue(result.getErrorMessage(), result.isSucceeded());
            assertEquals(0, numProxyRequests.intValue());
        }
        // test sync execution
        factory.setHttpClientConfig(build());
        try (JestHttpClient customJestClient = ((JestHttpClient) (factory.getObject()))) {
            JestResult result = customJestClient.execute(new Stats.Builder().build());
            assertTrue(result.getErrorMessage(), result.isSucceeded());
            assertEquals(1, numProxyRequests.intValue());
        }
        // test async execution
        factory.setHttpClientConfig(build());
        try (JestHttpClient customJestClient = ((JestHttpClient) (factory.getObject()))) {
            final CountDownLatch actionExecuted = new CountDownLatch(1);
            customJestClient.executeAsync(new Stats.Builder().build(), new io.searchbox.client.JestResultHandler<JestResult>() {
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

