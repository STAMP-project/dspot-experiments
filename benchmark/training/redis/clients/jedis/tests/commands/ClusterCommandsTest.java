package redis.clients.jedis.tests.commands;


import Reset.HARD;
import Reset.SOFT;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.tests.HostAndPortUtil;
import redis.clients.jedis.tests.JedisTestBase;
import redis.clients.jedis.tests.utils.JedisClusterTestUtil;


public class ClusterCommandsTest extends JedisTestBase {
    private static Jedis node1;

    private static Jedis node2;

    private HostAndPort nodeInfo1 = HostAndPortUtil.getClusterServers().get(0);

    private HostAndPort nodeInfo2 = HostAndPortUtil.getClusterServers().get(1);

    @Test
    public void testClusterSoftReset() {
        ClusterCommandsTest.node1.clusterMeet("127.0.0.1", nodeInfo2.getPort());
        Assert.assertTrue(((ClusterCommandsTest.node1.clusterNodes().split("\n").length) > 1));
        ClusterCommandsTest.node1.clusterReset(SOFT);
        Assert.assertEquals(1, ClusterCommandsTest.node1.clusterNodes().split("\n").length);
    }

    @Test
    public void testClusterHardReset() {
        String nodeId = JedisClusterTestUtil.getNodeId(ClusterCommandsTest.node1.clusterNodes());
        ClusterCommandsTest.node1.clusterReset(HARD);
        String newNodeId = JedisClusterTestUtil.getNodeId(ClusterCommandsTest.node1.clusterNodes());
        Assert.assertNotEquals(nodeId, newNodeId);
    }

    @Test
    public void clusterSetSlotImporting() {
        ClusterCommandsTest.node2.clusterAddSlots(6000);
        String[] nodes = ClusterCommandsTest.node1.clusterNodes().split("\n");
        String nodeId = nodes[0].split(" ")[0];
        String status = ClusterCommandsTest.node1.clusterSetSlotImporting(6000, nodeId);
        Assert.assertEquals("OK", status);
    }

    @Test
    public void clusterNodes() {
        String nodes = ClusterCommandsTest.node1.clusterNodes();
        Assert.assertTrue(((nodes.split("\n").length) > 0));
    }

    @Test
    public void clusterMeet() {
        String status = ClusterCommandsTest.node1.clusterMeet("127.0.0.1", nodeInfo2.getPort());
        Assert.assertEquals("OK", status);
    }

    @Test
    public void clusterAddSlots() {
        String status = ClusterCommandsTest.node1.clusterAddSlots(1, 2, 3, 4, 5);
        Assert.assertEquals("OK", status);
    }

    @Test
    public void clusterDelSlots() {
        ClusterCommandsTest.node1.clusterAddSlots(900);
        String status = ClusterCommandsTest.node1.clusterDelSlots(900);
        Assert.assertEquals("OK", status);
    }

    @Test
    public void clusterInfo() {
        String info = ClusterCommandsTest.node1.clusterInfo();
        Assert.assertNotNull(info);
    }

    @Test
    public void clusterGetKeysInSlot() {
        ClusterCommandsTest.node1.clusterAddSlots(500);
        List<String> keys = ClusterCommandsTest.node1.clusterGetKeysInSlot(500, 1);
        Assert.assertEquals(0, keys.size());
    }

    @Test
    public void clusterSetSlotNode() {
        String[] nodes = ClusterCommandsTest.node1.clusterNodes().split("\n");
        String nodeId = nodes[0].split(" ")[0];
        String status = ClusterCommandsTest.node1.clusterSetSlotNode(10000, nodeId);
        Assert.assertEquals("OK", status);
    }

    @Test
    public void clusterSetSlotMigrating() {
        ClusterCommandsTest.node1.clusterAddSlots(5000);
        String[] nodes = ClusterCommandsTest.node1.clusterNodes().split("\n");
        String nodeId = nodes[0].split(" ")[0];
        String status = ClusterCommandsTest.node1.clusterSetSlotMigrating(5000, nodeId);
        Assert.assertEquals("OK", status);
    }

    @Test
    public void clusterSlots() {
        // please see cluster slot output format from below commit
        // @see:
        // https://github.com/antirez/redis/commit/e14829de3025ffb0d3294e5e5a1553afd9f10b60
        String status = ClusterCommandsTest.node1.clusterAddSlots(3000, 3001, 3002);
        Assert.assertEquals("OK", status);
        status = ClusterCommandsTest.node2.clusterAddSlots(4000, 4001, 4002);
        Assert.assertEquals("OK", status);
        List<Object> slots = ClusterCommandsTest.node1.clusterSlots();
        Assert.assertNotNull(slots);
        Assert.assertTrue(((slots.size()) > 0));
        for (Object slotInfoObj : slots) {
            List<Object> slotInfo = ((List<Object>) (slotInfoObj));
            Assert.assertNotNull(slots);
            Assert.assertTrue(((slots.size()) >= 2));
            Assert.assertTrue(((slotInfo.get(0)) instanceof Long));
            Assert.assertTrue(((slotInfo.get(1)) instanceof Long));
            if ((slots.size()) > 2) {
                // assigned slots
                Assert.assertTrue(((slotInfo.get(2)) instanceof List));
            }
        }
    }
}

