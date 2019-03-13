package io.searchbox.cluster;


import ESIntegTestCase.ClusterScope;
import ESIntegTestCase.Scope;
import io.searchbox.client.JestResult;
import io.searchbox.cluster.reroute.RerouteMove;
import io.searchbox.common.AbstractIntegrationTest;
import java.io.IOException;
import org.junit.Test;


@ClusterScope(scope = Scope.TEST, numDataNodes = 3)
public class RerouteIntegrationTest extends AbstractIntegrationTest {
    static final String INDEX = "reroute";

    @Test
    public void move() throws IOException, InterruptedException {
        int shardToReroute = 0;
        String fromNode = getNodeOfPrimaryShard(RerouteIntegrationTest.INDEX, shardToReroute);
        String toNode = getAvailableNodeForShard(RerouteIntegrationTest.INDEX, shardToReroute);
        RerouteMove rerouteMove = new RerouteMove(RerouteIntegrationTest.INDEX, shardToReroute, fromNode, toNode);
        JestResult result = client.execute(build());
        assertTrue(result.getErrorMessage(), result.isSucceeded());
        waitUntilPrimaryShardInNode(shardToReroute, toNode);
    }
}

