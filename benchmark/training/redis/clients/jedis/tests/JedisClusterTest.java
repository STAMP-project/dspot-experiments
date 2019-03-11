package redis.clients.jedis.tests;


import Reset.SOFT;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.junit.Assert;
import org.junit.Test;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.tests.utils.ClientKillerUtil;
import redis.clients.jedis.tests.utils.JedisClusterTestUtil;
import redis.clients.util.ClusterNodeInformationParser;
import redis.clients.util.JedisClusterCRC16;


public class JedisClusterTest extends Assert {
    private static Jedis node1;

    private static Jedis node2;

    private static Jedis node3;

    private static Jedis node4;

    private static Jedis nodeSlave2;

    private String localHost = "127.0.0.1";

    private HostAndPort nodeInfo1 = HostAndPortUtil.getClusterServers().get(0);

    private HostAndPort nodeInfo2 = HostAndPortUtil.getClusterServers().get(1);

    private HostAndPort nodeInfo3 = HostAndPortUtil.getClusterServers().get(2);

    private HostAndPort nodeInfo4 = HostAndPortUtil.getClusterServers().get(3);

    private HostAndPort nodeInfoSlave2 = HostAndPortUtil.getClusterServers().get(4);

    protected Logger log = Logger.getLogger(getClass().getName());

    @Test(expected = JedisMovedDataException.class)
    public void testThrowMovedException() {
        JedisClusterTest.node1.set("foo", "bar");
    }

    @Test
    public void testMovedExceptionParameters() {
        try {
            JedisClusterTest.node1.set("foo", "bar");
        } catch (JedisMovedDataException jme) {
            Assert.assertEquals(12182, jme.getSlot());
            Assert.assertEquals(new HostAndPort("127.0.0.1", 7381), jme.getTargetNode());
            return;
        }
        Assert.fail();
    }

    @Test(expected = JedisAskDataException.class)
    public void testThrowAskException() {
        int keySlot = JedisClusterCRC16.getSlot("test");
        String node3Id = JedisClusterTestUtil.getNodeId(JedisClusterTest.node3.clusterNodes());
        JedisClusterTest.node2.clusterSetSlotMigrating(keySlot, node3Id);
        JedisClusterTest.node2.get("test");
    }

    @Test
    public void testDiscoverNodesAutomatically() {
        Set<HostAndPort> jedisClusterNode = new HashSet<HostAndPort>();
        jedisClusterNode.add(new HostAndPort("127.0.0.1", 7379));
        JedisCluster jc = new JedisCluster(jedisClusterNode);
        Assert.assertEquals(3, jc.getClusterNodes().size());
        JedisCluster jc2 = new JedisCluster(new HostAndPort("127.0.0.1", 7379));
        Assert.assertEquals(3, jc2.getClusterNodes().size());
    }

    @Test
    public void testCalculateConnectionPerSlot() {
        Set<HostAndPort> jedisClusterNode = new HashSet<HostAndPort>();
        jedisClusterNode.add(new HostAndPort("127.0.0.1", 7379));
        JedisCluster jc = new JedisCluster(jedisClusterNode);
        jc.set("foo", "bar");
        jc.set("test", "test");
        Assert.assertEquals("bar", JedisClusterTest.node3.get("foo"));
        Assert.assertEquals("test", JedisClusterTest.node2.get("test"));
        JedisCluster jc2 = new JedisCluster(new HostAndPort("127.0.0.1", 7379));
        jc2.set("foo", "bar");
        jc2.set("test", "test");
        Assert.assertEquals("bar", JedisClusterTest.node3.get("foo"));
        Assert.assertEquals("test", JedisClusterTest.node2.get("test"));
    }

    @Test
    public void testReadonly() throws Exception {
        JedisClusterTest.node1.clusterMeet(localHost, nodeInfoSlave2.getPort());
        JedisClusterTestUtil.waitForClusterReady(JedisClusterTest.node1, JedisClusterTest.node2, JedisClusterTest.node3, JedisClusterTest.nodeSlave2);
        ClusterNodeInformationParser nodeInfoParser = new ClusterNodeInformationParser();
        for (String nodeInfo : JedisClusterTest.node2.clusterNodes().split("\n")) {
            if (nodeInfo.contains("myself")) {
                JedisClusterTest.nodeSlave2.clusterReplicate(nodeInfo.split(" ")[0]);
                break;
            }
        }
        try {
            JedisClusterTest.nodeSlave2.get("test");
            Assert.fail();
        } catch (JedisMovedDataException e) {
        }
        JedisClusterTest.nodeSlave2.readonly();
        JedisClusterTest.nodeSlave2.get("test");
        JedisClusterTest.nodeSlave2.clusterReset(SOFT);
        JedisClusterTest.nodeSlave2.flushDB();
    }

    /**
     * slot->nodes 15363 node3 e
     */
    @Test
    public void testMigrate() {
        log.info("test migrate slot");
        Set<HostAndPort> jedisClusterNode = new HashSet<HostAndPort>();
        jedisClusterNode.add(nodeInfo1);
        JedisCluster jc = new JedisCluster(jedisClusterNode);
        String node3Id = JedisClusterTestUtil.getNodeId(JedisClusterTest.node3.clusterNodes());
        String node2Id = JedisClusterTestUtil.getNodeId(JedisClusterTest.node2.clusterNodes());
        JedisClusterTest.node3.clusterSetSlotMigrating(15363, node2Id);
        JedisClusterTest.node2.clusterSetSlotImporting(15363, node3Id);
        try {
            JedisClusterTest.node2.set("e", "e");
        } catch (JedisMovedDataException jme) {
            Assert.assertEquals(15363, jme.getSlot());
            Assert.assertEquals(new HostAndPort(localHost, nodeInfo3.getPort()), jme.getTargetNode());
        }
        try {
            JedisClusterTest.node3.set("e", "e");
        } catch (JedisAskDataException jae) {
            Assert.assertEquals(15363, jae.getSlot());
            Assert.assertEquals(new HostAndPort(localHost, nodeInfo2.getPort()), jae.getTargetNode());
        }
        jc.set("e", "e");
        try {
            JedisClusterTest.node2.get("e");
        } catch (JedisMovedDataException jme) {
            Assert.assertEquals(15363, jme.getSlot());
            Assert.assertEquals(new HostAndPort(localHost, nodeInfo3.getPort()), jme.getTargetNode());
        }
        try {
            JedisClusterTest.node3.get("e");
        } catch (JedisAskDataException jae) {
            Assert.assertEquals(15363, jae.getSlot());
            Assert.assertEquals(new HostAndPort(localHost, nodeInfo2.getPort()), jae.getTargetNode());
        }
        Assert.assertEquals("e", jc.get("e"));
        JedisClusterTest.node2.clusterSetSlotNode(15363, node2Id);
        JedisClusterTest.node3.clusterSetSlotNode(15363, node2Id);
        // assertEquals("e", jc.get("e"));
        Assert.assertEquals("e", JedisClusterTest.node2.get("e"));
        // assertEquals("e", node3.get("e"));
    }

    @Test
    public void testMigrateToNewNode() throws InterruptedException {
        log.info("test migrate slot to new node");
        Set<HostAndPort> jedisClusterNode = new HashSet<HostAndPort>();
        jedisClusterNode.add(nodeInfo1);
        JedisCluster jc = new JedisCluster(jedisClusterNode);
        JedisClusterTest.node4.clusterMeet(localHost, nodeInfo1.getPort());
        String node3Id = JedisClusterTestUtil.getNodeId(JedisClusterTest.node3.clusterNodes());
        String node4Id = JedisClusterTestUtil.getNodeId(JedisClusterTest.node4.clusterNodes());
        JedisClusterTestUtil.waitForClusterReady(JedisClusterTest.node4);
        JedisClusterTest.node3.clusterSetSlotMigrating(15363, node4Id);
        JedisClusterTest.node4.clusterSetSlotImporting(15363, node3Id);
        try {
            JedisClusterTest.node4.set("e", "e");
        } catch (JedisMovedDataException jme) {
            Assert.assertEquals(15363, jme.getSlot());
            Assert.assertEquals(new HostAndPort(localHost, nodeInfo3.getPort()), jme.getTargetNode());
        }
        try {
            JedisClusterTest.node3.set("e", "e");
        } catch (JedisAskDataException jae) {
            Assert.assertEquals(15363, jae.getSlot());
            Assert.assertEquals(new HostAndPort(localHost, nodeInfo4.getPort()), jae.getTargetNode());
        }
        jc.set("e", "e");
        try {
            JedisClusterTest.node4.get("e");
        } catch (JedisMovedDataException jme) {
            Assert.assertEquals(15363, jme.getSlot());
            Assert.assertEquals(new HostAndPort(localHost, nodeInfo3.getPort()), jme.getTargetNode());
        }
        try {
            JedisClusterTest.node3.get("e");
        } catch (JedisAskDataException jae) {
            Assert.assertEquals(15363, jae.getSlot());
            Assert.assertEquals(new HostAndPort(localHost, nodeInfo4.getPort()), jae.getTargetNode());
        }
        Assert.assertEquals("e", jc.get("e"));
        JedisClusterTest.node4.clusterSetSlotNode(15363, node4Id);
        JedisClusterTest.node3.clusterSetSlotNode(15363, node4Id);
        // assertEquals("e", jc.get("e"));
        Assert.assertEquals("e", JedisClusterTest.node4.get("e"));
        // assertEquals("e", node3.get("e"));
    }

    @Test
    public void testRecalculateSlotsWhenMoved() throws InterruptedException {
        Set<HostAndPort> jedisClusterNode = new HashSet<HostAndPort>();
        jedisClusterNode.add(new HostAndPort("127.0.0.1", 7379));
        JedisCluster jc = new JedisCluster(jedisClusterNode);
        int slot51 = JedisClusterCRC16.getSlot("51");
        JedisClusterTest.node2.clusterDelSlots(slot51);
        JedisClusterTest.node3.clusterDelSlots(slot51);
        JedisClusterTest.node3.clusterAddSlots(slot51);
        JedisClusterTestUtil.waitForClusterReady(JedisClusterTest.node1, JedisClusterTest.node2, JedisClusterTest.node3);
        jc.set("51", "foo");
        Assert.assertEquals("foo", jc.get("51"));
    }

    @Test
    public void testAskResponse() throws InterruptedException {
        Set<HostAndPort> jedisClusterNode = new HashSet<HostAndPort>();
        jedisClusterNode.add(new HostAndPort("127.0.0.1", 7379));
        JedisCluster jc = new JedisCluster(jedisClusterNode);
        int slot51 = JedisClusterCRC16.getSlot("51");
        JedisClusterTest.node3.clusterSetSlotImporting(slot51, JedisClusterTestUtil.getNodeId(JedisClusterTest.node2.clusterNodes()));
        JedisClusterTest.node2.clusterSetSlotMigrating(slot51, JedisClusterTestUtil.getNodeId(JedisClusterTest.node3.clusterNodes()));
        jc.set("51", "foo");
        Assert.assertEquals("foo", jc.get("51"));
    }

    @Test(expected = JedisClusterMaxRedirectionsException.class)
    public void testRedisClusterMaxRedirections() {
        Set<HostAndPort> jedisClusterNode = new HashSet<HostAndPort>();
        jedisClusterNode.add(new HostAndPort("127.0.0.1", 7379));
        JedisCluster jc = new JedisCluster(jedisClusterNode);
        int slot51 = JedisClusterCRC16.getSlot("51");
        // This will cause an infinite redirection loop
        JedisClusterTest.node2.clusterSetSlotMigrating(slot51, JedisClusterTestUtil.getNodeId(JedisClusterTest.node3.clusterNodes()));
        jc.set("51", "foo");
    }

    @Test
    public void testRedisHashtag() {
        Assert.assertEquals(JedisClusterCRC16.getSlot("{bar"), JedisClusterCRC16.getSlot("foo{{bar}}zap"));
        Assert.assertEquals(JedisClusterCRC16.getSlot("{user1000}.following"), JedisClusterCRC16.getSlot("{user1000}.followers"));
        Assert.assertNotEquals(JedisClusterCRC16.getSlot("foo{}{bar}"), JedisClusterCRC16.getSlot("bar"));
        Assert.assertEquals(JedisClusterCRC16.getSlot("foo{bar}{zap}"), JedisClusterCRC16.getSlot("bar"));
    }

    @Test
    public void testClusterForgetNode() throws InterruptedException {
        // at first, join node4 to cluster
        JedisClusterTest.node1.clusterMeet("127.0.0.1", nodeInfo4.getPort());
        String node7Id = JedisClusterTestUtil.getNodeId(JedisClusterTest.node4.clusterNodes());
        JedisClusterTestUtil.assertNodeIsKnown(JedisClusterTest.node3, node7Id, 1000);
        JedisClusterTestUtil.assertNodeIsKnown(JedisClusterTest.node2, node7Id, 1000);
        JedisClusterTestUtil.assertNodeIsKnown(JedisClusterTest.node1, node7Id, 1000);
        assertNodeHandshakeEnded(JedisClusterTest.node3, 1000);
        assertNodeHandshakeEnded(JedisClusterTest.node2, 1000);
        assertNodeHandshakeEnded(JedisClusterTest.node1, 1000);
        Assert.assertEquals(4, JedisClusterTest.node1.clusterNodes().split("\n").length);
        Assert.assertEquals(4, JedisClusterTest.node2.clusterNodes().split("\n").length);
        Assert.assertEquals(4, JedisClusterTest.node3.clusterNodes().split("\n").length);
        // do cluster forget
        JedisClusterTest.node1.clusterForget(node7Id);
        JedisClusterTest.node2.clusterForget(node7Id);
        JedisClusterTest.node3.clusterForget(node7Id);
        JedisClusterTestUtil.assertNodeIsUnknown(JedisClusterTest.node1, node7Id, 1000);
        JedisClusterTestUtil.assertNodeIsUnknown(JedisClusterTest.node2, node7Id, 1000);
        JedisClusterTestUtil.assertNodeIsUnknown(JedisClusterTest.node3, node7Id, 1000);
        Assert.assertEquals(3, JedisClusterTest.node1.clusterNodes().split("\n").length);
        Assert.assertEquals(3, JedisClusterTest.node2.clusterNodes().split("\n").length);
        Assert.assertEquals(3, JedisClusterTest.node3.clusterNodes().split("\n").length);
    }

    @Test
    public void testClusterFlushSlots() {
        String slotRange = JedisClusterTest.getNodeServingSlotRange(JedisClusterTest.node1.clusterNodes());
        Assert.assertNotNull(slotRange);
        try {
            JedisClusterTest.node1.clusterFlushSlots();
            Assert.assertNull(JedisClusterTest.getNodeServingSlotRange(JedisClusterTest.node1.clusterNodes()));
        } finally {
            // rollback
            String[] rangeInfo = slotRange.split("-");
            int lower = Integer.parseInt(rangeInfo[0]);
            int upper = Integer.parseInt(rangeInfo[1]);
            int[] node1Slots = new int[(upper - lower) + 1];
            for (int i = 0; lower <= upper;) {
                node1Slots[(i++)] = lower++;
            }
            JedisClusterTest.node1.clusterAddSlots(node1Slots);
        }
    }

    @Test
    public void testClusterKeySlot() {
        // It assumes JedisClusterCRC16 is correctly implemented
        Assert.assertEquals(JedisClusterTest.node1.clusterKeySlot("foo{bar}zap}").intValue(), JedisClusterCRC16.getSlot("foo{bar}zap"));
        Assert.assertEquals(JedisClusterTest.node1.clusterKeySlot("{user1000}.following").intValue(), JedisClusterCRC16.getSlot("{user1000}.following"));
    }

    @Test
    public void testClusterCountKeysInSlot() {
        Set<HostAndPort> jedisClusterNode = new HashSet<HostAndPort>();
        jedisClusterNode.add(new HostAndPort(nodeInfo1.getHost(), nodeInfo1.getPort()));
        JedisCluster jc = new JedisCluster(jedisClusterNode);
        for (int index = 0; index < 5; index++) {
            jc.set(("foo{bar}" + index), "hello");
        }
        int slot = JedisClusterCRC16.getSlot("foo{bar}");
        Assert.assertEquals(5, JedisClusterTest.node1.clusterCountKeysInSlot(slot).intValue());
    }

    @Test
    public void testStableSlotWhenMigratingNodeOrImportingNodeIsNotSpecified() throws InterruptedException {
        Set<HostAndPort> jedisClusterNode = new HashSet<HostAndPort>();
        jedisClusterNode.add(new HostAndPort(nodeInfo1.getHost(), nodeInfo1.getPort()));
        JedisCluster jc = new JedisCluster(jedisClusterNode);
        int slot51 = JedisClusterCRC16.getSlot("51");
        jc.set("51", "foo");
        // node2 is responsible of taking care of slot51 (7186)
        JedisClusterTest.node3.clusterSetSlotImporting(slot51, JedisClusterTestUtil.getNodeId(JedisClusterTest.node2.clusterNodes()));
        Assert.assertEquals("foo", jc.get("51"));
        JedisClusterTest.node3.clusterSetSlotStable(slot51);
        Assert.assertEquals("foo", jc.get("51"));
        JedisClusterTest.node2.clusterSetSlotMigrating(slot51, JedisClusterTestUtil.getNodeId(JedisClusterTest.node3.clusterNodes()));
        // assertEquals("foo", jc.get("51")); // it leads Max Redirections
        JedisClusterTest.node2.clusterSetSlotStable(slot51);
        Assert.assertEquals("foo", jc.get("51"));
    }

    @Test(expected = JedisConnectionException.class)
    public void testIfPoolConfigAppliesToClusterPools() {
        GenericObjectPoolConfig config = new GenericObjectPoolConfig();
        config.setMaxTotal(0);
        config.setMaxWaitMillis(2000);
        Set<HostAndPort> jedisClusterNode = new HashSet<HostAndPort>();
        jedisClusterNode.add(new HostAndPort("127.0.0.1", 7379));
        JedisCluster jc = new JedisCluster(jedisClusterNode, config);
        jc.set("52", "poolTestValue");
    }

    @Test
    public void testCloseable() throws IOException {
        Set<HostAndPort> jedisClusterNode = new HashSet<HostAndPort>();
        jedisClusterNode.add(new HostAndPort(nodeInfo1.getHost(), nodeInfo1.getPort()));
        JedisCluster jc = null;
        try {
            jc = new JedisCluster(jedisClusterNode);
            jc.set("51", "foo");
        } finally {
            if (jc != null) {
                jc.close();
            }
        }
        Iterator<JedisPool> poolIterator = jc.getClusterNodes().values().iterator();
        while (poolIterator.hasNext()) {
            JedisPool pool = poolIterator.next();
            try {
                pool.getResource();
                Assert.fail("JedisCluster's internal pools should be already destroyed");
            } catch (JedisConnectionException e) {
                // ok to go...
            }
        } 
    }

    @Test
    public void testJedisClusterTimeout() {
        Set<HostAndPort> jedisClusterNode = new HashSet<HostAndPort>();
        jedisClusterNode.add(new HostAndPort(nodeInfo1.getHost(), nodeInfo1.getPort()));
        JedisCluster jc = new JedisCluster(jedisClusterNode, 4000);
        for (JedisPool pool : jc.getClusterNodes().values()) {
            Jedis jedis = pool.getResource();
            Assert.assertEquals(jedis.getClient().getConnectionTimeout(), 4000);
            Assert.assertEquals(jedis.getClient().getSoTimeout(), 4000);
            jedis.close();
        }
    }

    @Test
    public void testJedisClusterRunsWithMultithreaded() throws IOException, InterruptedException, ExecutionException {
        Set<HostAndPort> jedisClusterNode = new HashSet<HostAndPort>();
        jedisClusterNode.add(new HostAndPort("127.0.0.1", 7379));
        final JedisCluster jc = new JedisCluster(jedisClusterNode);
        jc.set("foo", "bar");
        ThreadPoolExecutor executor = new ThreadPoolExecutor(10, 100, 0, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(10));
        List<Future<String>> futures = new ArrayList<Future<String>>();
        for (int i = 0; i < 50; i++) {
            executor.submit(new Callable<String>() {
                @Override
                public String call() throws Exception {
                    // FIXME : invalidate slot cache from JedisCluster to test
                    // random connection also does work
                    return jc.get("foo");
                }
            });
        }
        for (Future<String> future : futures) {
            String value = future.get();
            Assert.assertEquals("bar", value);
        }
        jc.close();
    }

    @Test(timeout = 2000)
    public void testReturnConnectionOnJedisConnectionException() throws InterruptedException {
        Set<HostAndPort> jedisClusterNode = new HashSet<HostAndPort>();
        jedisClusterNode.add(new HostAndPort("127.0.0.1", 7379));
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxTotal(1);
        JedisCluster jc = new JedisCluster(jedisClusterNode, config);
        Jedis j = jc.getClusterNodes().get("127.0.0.1:7380").getResource();
        ClientKillerUtil.tagClient(j, "DEAD");
        ClientKillerUtil.killClient(j, "DEAD");
        j.close();
        jc.get("test");
    }

    @Test(expected = JedisClusterMaxRedirectionsException.class, timeout = 2000)
    public void testReturnConnectionOnRedirection() {
        Set<HostAndPort> jedisClusterNode = new HashSet<HostAndPort>();
        jedisClusterNode.add(new HostAndPort("127.0.0.1", 7379));
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxTotal(1);
        JedisCluster jc = new JedisCluster(jedisClusterNode, 0, 2, config);
        // This will cause an infinite redirection between node 2 and 3
        JedisClusterTest.node3.clusterSetSlotMigrating(15363, JedisClusterTestUtil.getNodeId(JedisClusterTest.node2.clusterNodes()));
        jc.get("e");
    }
}

