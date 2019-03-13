package io.searchbox.cluster;


import ESIntegTestCase.ClusterScope;
import ESIntegTestCase.Scope;
import io.searchbox.client.JestResult;
import io.searchbox.common.AbstractIntegrationTest;
import java.io.IOException;
import org.junit.Test;


/**
 *
 *
 * @author cihat keser
 */
@ClusterScope(scope = Scope.SUITE, numDataNodes = 2)
public class NodesHotThreadsIntegrationTest extends AbstractIntegrationTest {
    @Test
    public void allNodesHotThreads() throws IOException {
        String firstNode = internalCluster().getNodeNames()[0];
        String secondNode = internalCluster().getNodeNames()[1];
        JestResult result = client.execute(new NodesHotThreads.Builder().build());
        assertTrue(result.getErrorMessage(), result.isSucceeded());
        assertTrue(result.getJsonString().contains("interval=500ms"));
        assertNodePresent(result, firstNode);
        assertNodePresent(result, secondNode);
    }

    @Test
    public void singleNodeHotThreads() throws IOException {
        String firstNode = internalCluster().getNodeNames()[0];
        String secondNode = internalCluster().getNodeNames()[1];
        JestResult result = client.execute(new NodesHotThreads.Builder().addNode(firstNode).build());
        assertTrue(result.getErrorMessage(), result.isSucceeded());
        assertNodePresent(result, firstNode);
        assertNodeMissing(result, secondNode);
    }

    @Test
    public void singleNodeHotThreadsWithCustomInterval() throws IOException {
        String firstNode = internalCluster().getNodeNames()[0];
        String secondNode = internalCluster().getNodeNames()[1];
        JestResult result = client.execute(new NodesHotThreads.Builder().addNode(firstNode).interval("100ms").build());
        assertTrue(result.getErrorMessage(), result.isSucceeded());
        String rawJson = result.getJsonString();
        assertTrue(rawJson, rawJson.contains("interval=100ms"));
        assertNodePresent(result, firstNode);
        assertNodeMissing(result, secondNode);
    }
}

