package io.searchbox.cluster;


import ElasticsearchVersion.UNKNOWN;
import com.google.gson.Gson;
import io.searchbox.cluster.reroute.RerouteAllocateReplica;
import io.searchbox.cluster.reroute.RerouteCancel;
import io.searchbox.cluster.reroute.RerouteCommand;
import io.searchbox.cluster.reroute.RerouteMove;
import java.util.LinkedList;
import java.util.List;
import org.json.JSONException;
import org.junit.Assert;
import org.junit.Test;
import org.skyscreamer.jsonassert.JSONAssert;


public class RerouteTest {
    @Test
    public void reroute() throws JSONException {
        List<RerouteCommand> moveCommands = new LinkedList<RerouteCommand>();
        moveCommands.add(new RerouteMove("index1", 1, "node1", "node2"));
        moveCommands.add(new RerouteCancel("index2", 1, "node2", true));
        moveCommands.add(new RerouteAllocateReplica("index3", 1, "node3"));
        Reroute reroute = build();
        Assert.assertEquals("/_cluster/reroute", reroute.getURI(UNKNOWN));
        Assert.assertEquals("POST", reroute.getRestMethodName());
        String expectedData = "{ \"commands\": [" + ((("{ \"move\": { \"index\": \"index1\", \"shard\": 1, \"from_node\": \"node1\", \"to_node\": \"node2\" } }, " + "{ \"cancel\": { \"index\": \"index2\", \"shard\": 1, \"node\": \"node2\", \"allow_primary\": true } },") + "{ \"allocate_replica\": { \"index\": \"index3\", \"shard\": 1, \"node\": \"node3\" } }") + "] }");
        JSONAssert.assertEquals(expectedData, reroute.getData(new Gson()), false);
    }
}

