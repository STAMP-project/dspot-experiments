package redis.clients.jedis.tests;


import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.junit.Assert;
import org.junit.Test;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisShardInfo;
import redis.clients.jedis.ShardedJedis;
import redis.clients.jedis.ShardedJedisPipeline;
import redis.clients.jedis.ShardedJedisPool;
import redis.clients.jedis.exceptions.JedisConnectionException;


public class ShardedJedisPoolTest extends Assert {
    private static HostAndPort redis1 = HostAndPortUtil.getRedisServers().get(0);

    private static HostAndPort redis2 = HostAndPortUtil.getRedisServers().get(1);

    private List<JedisShardInfo> shards;

    @Test
    public void checkConnections() {
        ShardedJedisPool pool = new ShardedJedisPool(new GenericObjectPoolConfig(), shards);
        ShardedJedis jedis = pool.getResource();
        jedis.set("foo", "bar");
        Assert.assertEquals("bar", jedis.get("foo"));
        jedis.close();
        pool.destroy();
    }

    @Test
    public void checkCloseableConnections() throws Exception {
        ShardedJedisPool pool = new ShardedJedisPool(new GenericObjectPoolConfig(), shards);
        ShardedJedis jedis = pool.getResource();
        jedis.set("foo", "bar");
        Assert.assertEquals("bar", jedis.get("foo"));
        jedis.close();
        pool.close();
        Assert.assertTrue(pool.isClosed());
    }

    @Test
    public void checkConnectionWithDefaultPort() {
        ShardedJedisPool pool = new ShardedJedisPool(new GenericObjectPoolConfig(), shards);
        ShardedJedis jedis = pool.getResource();
        jedis.set("foo", "bar");
        Assert.assertEquals("bar", jedis.get("foo"));
        jedis.close();
        pool.destroy();
    }

    @Test
    public void checkJedisIsReusedWhenReturned() {
        ShardedJedisPool pool = new ShardedJedisPool(new GenericObjectPoolConfig(), shards);
        ShardedJedis jedis = pool.getResource();
        jedis.set("foo", "0");
        jedis.close();
        jedis = pool.getResource();
        jedis.incr("foo");
        jedis.close();
        pool.destroy();
    }

    @Test
    public void checkPoolRepairedWhenJedisIsBroken() {
        ShardedJedisPool pool = new ShardedJedisPool(new GenericObjectPoolConfig(), shards);
        ShardedJedis jedis = pool.getResource();
        jedis.disconnect();
        jedis.close();
        jedis = pool.getResource();
        jedis.incr("foo");
        jedis.close();
        pool.destroy();
    }

    @Test(expected = JedisConnectionException.class)
    public void checkPoolOverflow() {
        GenericObjectPoolConfig config = new GenericObjectPoolConfig();
        config.setMaxTotal(1);
        config.setBlockWhenExhausted(false);
        ShardedJedisPool pool = new ShardedJedisPool(config, shards);
        ShardedJedis jedis = pool.getResource();
        jedis.set("foo", "0");
        ShardedJedis newJedis = pool.getResource();
        newJedis.incr("foo");
    }

    @Test
    public void shouldNotShareInstances() {
        GenericObjectPoolConfig config = new GenericObjectPoolConfig();
        config.setMaxTotal(2);
        ShardedJedisPool pool = new ShardedJedisPool(config, shards);
        ShardedJedis j1 = pool.getResource();
        ShardedJedis j2 = pool.getResource();
        Assert.assertNotSame(j1.getShard("foo"), j2.getShard("foo"));
    }

    @Test
    public void checkFailedJedisServer() {
        ShardedJedisPool pool = new ShardedJedisPool(new GenericObjectPoolConfig(), shards);
        ShardedJedis jedis = pool.getResource();
        jedis.incr("foo");
        jedis.close();
        pool.destroy();
    }

    @Test
    public void shouldReturnActiveShardsWhenOneGoesOffline() {
        GenericObjectPoolConfig redisConfig = new GenericObjectPoolConfig();
        redisConfig.setTestOnBorrow(false);
        ShardedJedisPool pool = new ShardedJedisPool(redisConfig, shards);
        ShardedJedis jedis = pool.getResource();
        // fill the shards
        for (int i = 0; i < 1000; i++) {
            jedis.set(("a-test-" + i), "0");
        }
        jedis.close();
        // check quantity for each shard
        Jedis j = new Jedis(shards.get(0));
        j.connect();
        Long c1 = j.dbSize();
        j.disconnect();
        j = new Jedis(shards.get(1));
        j.connect();
        Long c2 = j.dbSize();
        j.disconnect();
        // shutdown shard 2 and check thay the pool returns an instance with c1
        // items on one shard
        // alter shard 1 and recreate pool
        pool.destroy();
        shards.set(1, new JedisShardInfo("localhost", 1234));
        pool = new ShardedJedisPool(redisConfig, shards);
        jedis = pool.getResource();
        Long actual = Long.valueOf(0);
        Long fails = Long.valueOf(0);
        for (int i = 0; i < 1000; i++) {
            try {
                jedis.get(("a-test-" + i));
                actual++;
            } catch (RuntimeException e) {
                fails++;
            }
        }
        jedis.close();
        pool.destroy();
        Assert.assertEquals(actual, c1);
        Assert.assertEquals(fails, c2);
    }

    @Test
    public void startWithUrlString() {
        Jedis j = new Jedis("localhost", 6380);
        j.auth("foobared");
        j.set("foo", "bar");
        j = new Jedis("localhost", 6379);
        j.auth("foobared");
        j.set("foo", "bar");
        List<JedisShardInfo> shards = new ArrayList<JedisShardInfo>();
        shards.add(new JedisShardInfo("redis://:foobared@localhost:6380"));
        shards.add(new JedisShardInfo("redis://:foobared@localhost:6379"));
        GenericObjectPoolConfig redisConfig = new GenericObjectPoolConfig();
        ShardedJedisPool pool = new ShardedJedisPool(redisConfig, shards);
        Jedis[] jedises = pool.getResource().getAllShards().toArray(new Jedis[2]);
        Jedis jedis = jedises[0];
        Assert.assertEquals("PONG", jedis.ping());
        Assert.assertEquals("bar", jedis.get("foo"));
        jedis = jedises[1];
        Assert.assertEquals("PONG", jedis.ping());
        Assert.assertEquals("bar", jedis.get("foo"));
    }

    @Test
    public void startWithUrl() throws URISyntaxException {
        Jedis j = new Jedis("localhost", 6380);
        j.auth("foobared");
        j.set("foo", "bar");
        j = new Jedis("localhost", 6379);
        j.auth("foobared");
        j.set("foo", "bar");
        List<JedisShardInfo> shards = new ArrayList<JedisShardInfo>();
        shards.add(new JedisShardInfo(new URI("redis://:foobared@localhost:6380")));
        shards.add(new JedisShardInfo(new URI("redis://:foobared@localhost:6379")));
        GenericObjectPoolConfig redisConfig = new GenericObjectPoolConfig();
        ShardedJedisPool pool = new ShardedJedisPool(redisConfig, shards);
        Jedis[] jedises = pool.getResource().getAllShards().toArray(new Jedis[2]);
        Jedis jedis = jedises[0];
        Assert.assertEquals("PONG", jedis.ping());
        Assert.assertEquals("bar", jedis.get("foo"));
        jedis = jedises[1];
        Assert.assertEquals("PONG", jedis.ping());
        Assert.assertEquals("bar", jedis.get("foo"));
    }

    @Test
    public void returnResourceShouldResetState() throws URISyntaxException {
        GenericObjectPoolConfig config = new GenericObjectPoolConfig();
        config.setMaxTotal(1);
        config.setBlockWhenExhausted(false);
        List<JedisShardInfo> shards = new ArrayList<JedisShardInfo>();
        shards.add(new JedisShardInfo(new URI("redis://:foobared@localhost:6380")));
        shards.add(new JedisShardInfo(new URI("redis://:foobared@localhost:6379")));
        ShardedJedisPool pool = new ShardedJedisPool(config, shards);
        ShardedJedis jedis = pool.getResource();
        jedis.set("pipelined", String.valueOf(0));
        jedis.set("pipelined2", String.valueOf(0));
        ShardedJedisPipeline pipeline = jedis.pipelined();
        pipeline.incr("pipelined");
        pipeline.incr("pipelined2");
        jedis.resetState();
        pipeline = jedis.pipelined();
        pipeline.incr("pipelined");
        pipeline.incr("pipelined2");
        List<Object> results = pipeline.syncAndReturnAll();
        Assert.assertEquals(2, results.size());
        jedis.close();
        pool.destroy();
    }

    @Test
    public void checkResourceIsCloseable() throws URISyntaxException {
        GenericObjectPoolConfig config = new GenericObjectPoolConfig();
        config.setMaxTotal(1);
        config.setBlockWhenExhausted(false);
        List<JedisShardInfo> shards = new ArrayList<JedisShardInfo>();
        shards.add(new JedisShardInfo(new URI("redis://:foobared@localhost:6380")));
        shards.add(new JedisShardInfo(new URI("redis://:foobared@localhost:6379")));
        ShardedJedisPool pool = new ShardedJedisPool(config, shards);
        ShardedJedis jedis = pool.getResource();
        try {
            jedis.set("hello", "jedis");
        } finally {
            jedis.close();
        }
        ShardedJedis jedis2 = pool.getResource();
        try {
            Assert.assertEquals(jedis, jedis2);
        } finally {
            jedis2.close();
        }
    }
}

