package redis.clients.jedis.tests.commands;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.tests.HostAndPortUtil;
import redis.clients.jedis.tests.JedisTestBase;
import redis.clients.util.JedisClusterCRC16;


public class ClusterBinaryJedisCommandsTest extends JedisTestBase {
    private Jedis node1;

    private static Jedis node2;

    private static Jedis node3;

    private HostAndPort nodeInfo1 = HostAndPortUtil.getClusterServers().get(0);

    private HostAndPort nodeInfo2 = HostAndPortUtil.getClusterServers().get(1);

    private HostAndPort nodeInfo3 = HostAndPortUtil.getClusterServers().get(2);

    private final Set<HostAndPort> jedisClusterNode = new HashSet<HostAndPort>();

    JedisCluster jedisCluster;

    @SuppressWarnings("unchecked")
    @Test
    public void testBinaryGetAndSet() {
        byte[] byteKey = "foo".getBytes();
        byte[] byteValue = "2".getBytes();
        jedisCluster.set(byteKey, byteValue);
        Assert.assertEquals(new String(jedisCluster.get(byteKey)), "2");
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testIncr() {
        byte[] byteKey = "foo".getBytes();
        byte[] byteValue = "2".getBytes();
        jedisCluster.set(byteKey, byteValue);
        jedisCluster.incr(byteKey);
        Assert.assertEquals(new String(jedisCluster.get(byteKey)), "3");
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testSadd() {
        byte[] byteKey = "languages".getBytes();
        byte[] firstLanguage = "java".getBytes();
        byte[] secondLanguage = "python".getBytes();
        byte[][] listLanguages = new byte[][]{ firstLanguage, secondLanguage };
        jedisCluster.sadd(byteKey, listLanguages);
        Set<byte[]> setLanguages = jedisCluster.smembers(byteKey);
        List<String> languages = new ArrayList<String>();
        for (byte[] language : setLanguages) {
            languages.add(new String(language));
        }
        Assert.assertTrue(languages.contains("java"));
        Assert.assertTrue(languages.contains("python"));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testHmset() {
        byte[] byteKey = "language".getBytes();
        byte[] language = "java".getBytes();
        HashMap<byte[], byte[]> map = new HashMap();
        map.put(byteKey, language);
        jedisCluster.hmset(byteKey, map);
        List<byte[]> listResults = jedisCluster.hmget(byteKey, byteKey);
        for (byte[] result : listResults) {
            String resultString = new String(result);
            Assert.assertEquals(resultString, "java");
        }
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testRpush() {
        byte[] value1 = "value1".getBytes();
        byte[] value2 = "value2".getBytes();
        byte[] key = "key1".getBytes();
        jedisCluster.del(key);
        jedisCluster.rpush(key, value1);
        jedisCluster.rpush(key, value2);
        long num = 2L;
        Assert.assertEquals(2, ((long) (jedisCluster.llen(key))));
    }

    @Test
    public void testGetSlot() {
        assertEquals(JedisClusterCRC16.getSlot("{bar".getBytes()), JedisClusterCRC16.getSlot("{bar"));
        assertEquals(JedisClusterCRC16.getSlot("{user1000}.following".getBytes()), JedisClusterCRC16.getSlot("{user1000}.followers".getBytes()));
        Assert.assertNotEquals(JedisClusterCRC16.getSlot("foo{}{bar}".getBytes()), JedisClusterCRC16.getSlot("bar".getBytes()));
        assertEquals(JedisClusterCRC16.getSlot("foo{bar}{zap}".getBytes()), JedisClusterCRC16.getSlot("bar".getBytes()));
    }
}

