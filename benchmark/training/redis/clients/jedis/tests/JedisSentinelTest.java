package redis.clients.jedis.tests;


import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.exceptions.JedisDataException;
import redis.clients.jedis.tests.utils.JedisSentinelTestUtil;


public class JedisSentinelTest extends JedisTestBase {
    private static final String MASTER_NAME = "mymaster";

    private static final String MONITOR_MASTER_NAME = "mymastermonitor";

    private static final String REMOVE_MASTER_NAME = "mymasterremove";

    private static final String FAILOVER_MASTER_NAME = "mymasterfailover";

    private static final String MASTER_IP = "127.0.0.1";

    protected static HostAndPort master = HostAndPortUtil.getRedisServers().get(0);

    protected static HostAndPort slave = HostAndPortUtil.getRedisServers().get(4);

    protected static HostAndPort sentinel = HostAndPortUtil.getSentinelServers().get(0);

    protected static HostAndPort sentinelForFailover = HostAndPortUtil.getSentinelServers().get(2);

    protected static HostAndPort masterForFailover = HostAndPortUtil.getRedisServers().get(5);

    @Test
    public void sentinel() {
        Jedis j = new Jedis(JedisSentinelTest.sentinel.getHost(), JedisSentinelTest.sentinel.getPort());
        try {
            List<Map<String, String>> masters = j.sentinelMasters();
            boolean inMasters = false;
            for (Map<String, String> master : masters)
                if (JedisSentinelTest.MASTER_NAME.equals(master.get("name")))
                    inMasters = true;


            Assert.assertTrue(inMasters);
            List<String> masterHostAndPort = j.sentinelGetMasterAddrByName(JedisSentinelTest.MASTER_NAME);
            HostAndPort masterFromSentinel = new HostAndPort(masterHostAndPort.get(0), Integer.parseInt(masterHostAndPort.get(1)));
            assertEquals(JedisSentinelTest.master, masterFromSentinel);
            List<Map<String, String>> slaves = j.sentinelSlaves(JedisSentinelTest.MASTER_NAME);
            Assert.assertTrue(((slaves.size()) > 0));
            Assert.assertEquals(JedisSentinelTest.master.getPort(), Integer.parseInt(slaves.get(0).get("master-port")));
            // DO NOT RE-RUN TEST TOO FAST, RESET TAKES SOME TIME TO... RESET
            Assert.assertEquals(Long.valueOf(1), j.sentinelReset(JedisSentinelTest.MASTER_NAME));
            Assert.assertEquals(Long.valueOf(0), j.sentinelReset(("woof" + (JedisSentinelTest.MASTER_NAME))));
        } finally {
            j.close();
        }
    }

    @Test
    public void sentinelFailover() throws InterruptedException {
        Jedis j = new Jedis(JedisSentinelTest.sentinelForFailover.getHost(), JedisSentinelTest.sentinelForFailover.getPort());
        Jedis j2 = new Jedis(JedisSentinelTest.sentinelForFailover.getHost(), JedisSentinelTest.sentinelForFailover.getPort());
        try {
            List<String> masterHostAndPort = j.sentinelGetMasterAddrByName(JedisSentinelTest.FAILOVER_MASTER_NAME);
            HostAndPort currentMaster = new HostAndPort(masterHostAndPort.get(0), Integer.parseInt(masterHostAndPort.get(1)));
            JedisSentinelTestUtil.waitForNewPromotedMaster(JedisSentinelTest.FAILOVER_MASTER_NAME, j, j2);
            masterHostAndPort = j.sentinelGetMasterAddrByName(JedisSentinelTest.FAILOVER_MASTER_NAME);
            HostAndPort newMaster = new HostAndPort(masterHostAndPort.get(0), Integer.parseInt(masterHostAndPort.get(1)));
            Assert.assertNotEquals(newMaster, currentMaster);
        } finally {
            j.close();
        }
    }

    @Test
    public void sentinelMonitor() {
        Jedis j = new Jedis(JedisSentinelTest.sentinel.getHost(), JedisSentinelTest.sentinel.getPort());
        try {
            // monitor new master
            String result = j.sentinelMonitor(JedisSentinelTest.MONITOR_MASTER_NAME, JedisSentinelTest.MASTER_IP, JedisSentinelTest.master.getPort(), 1);
            Assert.assertEquals("OK", result);
            // already monitored
            try {
                j.sentinelMonitor(JedisSentinelTest.MONITOR_MASTER_NAME, JedisSentinelTest.MASTER_IP, JedisSentinelTest.master.getPort(), 1);
                Assert.fail();
            } catch (JedisDataException e) {
                // pass
            }
        } finally {
            j.close();
        }
    }

    @Test
    public void sentinelRemove() {
        Jedis j = new Jedis(JedisSentinelTest.sentinel.getHost(), JedisSentinelTest.sentinel.getPort());
        try {
            ensureMonitored(JedisSentinelTest.sentinel, JedisSentinelTest.REMOVE_MASTER_NAME, JedisSentinelTest.MASTER_IP, JedisSentinelTest.master.getPort(), 1);
            String result = j.sentinelRemove(JedisSentinelTest.REMOVE_MASTER_NAME);
            Assert.assertEquals("OK", result);
            // not exist
            try {
                result = j.sentinelRemove(JedisSentinelTest.REMOVE_MASTER_NAME);
                Assert.assertNotEquals("OK", result);
                Assert.fail();
            } catch (JedisDataException e) {
                // pass
            }
        } finally {
            j.close();
        }
    }

    @Test
    public void sentinelSet() {
        Jedis j = new Jedis(JedisSentinelTest.sentinel.getHost(), JedisSentinelTest.sentinel.getPort());
        try {
            Map<String, String> parameterMap = new HashMap<String, String>();
            parameterMap.put("down-after-milliseconds", String.valueOf(1234));
            parameterMap.put("parallel-syncs", String.valueOf(3));
            parameterMap.put("quorum", String.valueOf(2));
            j.sentinelSet(JedisSentinelTest.MASTER_NAME, parameterMap);
            List<Map<String, String>> masters = j.sentinelMasters();
            for (Map<String, String> master : masters) {
                if (master.get("name").equals(JedisSentinelTest.MASTER_NAME)) {
                    Assert.assertEquals(1234, Integer.parseInt(master.get("down-after-milliseconds")));
                    Assert.assertEquals(3, Integer.parseInt(master.get("parallel-syncs")));
                    Assert.assertEquals(2, Integer.parseInt(master.get("quorum")));
                }
            }
            parameterMap.put("quorum", String.valueOf(1));
            j.sentinelSet(JedisSentinelTest.MASTER_NAME, parameterMap);
        } finally {
            j.close();
        }
    }
}

