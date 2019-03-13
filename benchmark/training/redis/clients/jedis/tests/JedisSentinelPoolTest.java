package redis.clients.jedis.tests;


import java.util.HashSet;
import java.util.Set;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.junit.Assert;
import org.junit.Test;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisSentinelPool;
import redis.clients.jedis.Transaction;
import redis.clients.jedis.exceptions.JedisConnectionException;
import redis.clients.jedis.exceptions.JedisException;


public class JedisSentinelPoolTest extends JedisTestBase {
    private static final String MASTER_NAME = "mymaster";

    protected static HostAndPort master = HostAndPortUtil.getRedisServers().get(2);

    protected static HostAndPort slave1 = HostAndPortUtil.getRedisServers().get(3);

    protected static HostAndPort sentinel1 = HostAndPortUtil.getSentinelServers().get(1);

    protected static HostAndPort sentinel2 = HostAndPortUtil.getSentinelServers().get(3);

    protected static Jedis sentinelJedis1;

    protected static Jedis sentinelJedis2;

    protected Set<String> sentinels = new HashSet<String>();

    @Test(expected = JedisConnectionException.class)
    public void initializeWithNotAvailableSentinelsShouldThrowException() {
        Set<String> wrongSentinels = new HashSet<String>();
        wrongSentinels.add(new HostAndPort("localhost", 65432).toString());
        wrongSentinels.add(new HostAndPort("localhost", 65431).toString());
        JedisSentinelPool pool = new JedisSentinelPool(JedisSentinelPoolTest.MASTER_NAME, wrongSentinels);
        pool.destroy();
    }

    @Test(expected = JedisException.class)
    public void initializeWithNotMonitoredMasterNameShouldThrowException() {
        final String wrongMasterName = "wrongMasterName";
        JedisSentinelPool pool = new JedisSentinelPool(wrongMasterName, sentinels);
        pool.destroy();
    }

    @Test
    public void checkCloseableConnections() throws Exception {
        GenericObjectPoolConfig config = new GenericObjectPoolConfig();
        JedisSentinelPool pool = new JedisSentinelPool(JedisSentinelPoolTest.MASTER_NAME, sentinels, config, 1000, "foobared", 2);
        Jedis jedis = pool.getResource();
        jedis.auth("foobared");
        jedis.set("foo", "bar");
        Assert.assertEquals("bar", jedis.get("foo"));
        jedis.close();
        pool.close();
        Assert.assertTrue(pool.isClosed());
    }

    @Test
    public void ensureSafeTwiceFailover() throws InterruptedException {
        JedisSentinelPool pool = new JedisSentinelPool(JedisSentinelPoolTest.MASTER_NAME, sentinels, new GenericObjectPoolConfig(), 1000, "foobared", 2);
        forceFailover(pool);
        // after failover sentinel needs a bit of time to stabilize before a new
        // failover
        Thread.sleep(100);
        forceFailover(pool);
        // you can test failover as much as possible
    }

    @Test
    public void returnResourceShouldResetState() {
        GenericObjectPoolConfig config = new GenericObjectPoolConfig();
        config.setMaxTotal(1);
        config.setBlockWhenExhausted(false);
        JedisSentinelPool pool = new JedisSentinelPool(JedisSentinelPoolTest.MASTER_NAME, sentinels, config, 1000, "foobared", 2);
        Jedis jedis = pool.getResource();
        Jedis jedis2 = null;
        try {
            jedis.set("hello", "jedis");
            Transaction t = jedis.multi();
            t.set("hello", "world");
            jedis.close();
            jedis2 = pool.getResource();
            Assert.assertTrue((jedis == jedis2));
            Assert.assertEquals("jedis", jedis2.get("hello"));
        } catch (JedisConnectionException e) {
            if (jedis2 != null) {
                jedis2 = null;
            }
        } finally {
            jedis2.close();
            pool.destroy();
        }
    }

    @Test
    public void checkResourceIsCloseable() {
        GenericObjectPoolConfig config = new GenericObjectPoolConfig();
        config.setMaxTotal(1);
        config.setBlockWhenExhausted(false);
        JedisSentinelPool pool = new JedisSentinelPool(JedisSentinelPoolTest.MASTER_NAME, sentinels, config, 1000, "foobared", 2);
        Jedis jedis = pool.getResource();
        try {
            jedis.set("hello", "jedis");
        } finally {
            jedis.close();
        }
        Jedis jedis2 = pool.getResource();
        try {
            assertEquals(jedis, jedis2);
        } finally {
            jedis2.close();
        }
    }

    @Test
    public void customClientName() {
        GenericObjectPoolConfig config = new GenericObjectPoolConfig();
        config.setMaxTotal(1);
        config.setBlockWhenExhausted(false);
        JedisSentinelPool pool = new JedisSentinelPool(JedisSentinelPoolTest.MASTER_NAME, sentinels, config, 1000, "foobared", 0, "my_shiny_client_name");
        Jedis jedis = pool.getResource();
        try {
            Assert.assertEquals("my_shiny_client_name", jedis.clientGetname());
        } finally {
            jedis.close();
            pool.destroy();
        }
        Assert.assertTrue(pool.isClosed());
    }
}

