package redis.clients.jedis.tests;


import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import org.junit.Assert;
import org.junit.Test;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisShardInfo;
import redis.clients.jedis.Response;
import redis.clients.jedis.ShardedJedis;
import redis.clients.jedis.ShardedJedisPipeline;
import redis.clients.jedis.Tuple;
import redis.clients.jedis.exceptions.JedisDataException;


public class ShardedJedisPipelineTest {
    private static HostAndPort redis1 = HostAndPortUtil.getRedisServers().get(0);

    private static HostAndPort redis2 = HostAndPortUtil.getRedisServers().get(1);

    private ShardedJedis jedis;

    @Test
    public void pipeline() throws UnsupportedEncodingException {
        ShardedJedisPipeline p = jedis.pipelined();
        p.set("foo", "bar");
        p.get("foo");
        List<Object> results = p.syncAndReturnAll();
        Assert.assertEquals(2, results.size());
        Assert.assertEquals("OK", results.get(0));
        Assert.assertEquals("bar", results.get(1));
    }

    @Test
    public void pipelineResponse() {
        jedis.set("string", "foo");
        jedis.lpush("list", "foo");
        jedis.hset("hash", "foo", "bar");
        jedis.zadd("zset", 1, "foo");
        jedis.sadd("set", "foo");
        ShardedJedisPipeline p = jedis.pipelined();
        Response<String> string = p.get("string");
        Response<Long> del = p.del("string");
        Response<String> emptyString = p.get("string");
        Response<String> list = p.lpop("list");
        Response<String> hash = p.hget("hash", "foo");
        Response<Set<String>> zset = p.zrange("zset", 0, (-1));
        Response<String> set = p.spop("set");
        Response<Boolean> blist = p.exists("list");
        Response<Double> zincrby = p.zincrby("zset", 1, "foo");
        Response<Long> zcard = p.zcard("zset");
        p.lpush("list", "bar");
        Response<List<String>> lrange = p.lrange("list", 0, (-1));
        Response<Map<String, String>> hgetAll = p.hgetAll("hash");
        p.sadd("set", "foo");
        Response<Set<String>> smembers = p.smembers("set");
        Response<Set<Tuple>> zrangeWithScores = p.zrangeWithScores("zset", 0, (-1));
        p.sync();
        Assert.assertEquals("foo", string.get());
        Assert.assertEquals(Long.valueOf(1), del.get());
        Assert.assertNull(emptyString.get());
        Assert.assertEquals("foo", list.get());
        Assert.assertEquals("bar", hash.get());
        Assert.assertEquals("foo", zset.get().iterator().next());
        Assert.assertEquals("foo", set.get());
        Assert.assertFalse(blist.get());
        Assert.assertEquals(Double.valueOf(2), zincrby.get());
        Assert.assertEquals(Long.valueOf(1), zcard.get());
        Assert.assertEquals(1, lrange.get().size());
        Assert.assertNotNull(hgetAll.get().get("foo"));
        Assert.assertEquals(1, smembers.get().size());
        Assert.assertEquals(1, zrangeWithScores.get().size());
    }

    @Test(expected = JedisDataException.class)
    public void pipelineResponseWithinPipeline() {
        jedis.set("string", "foo");
        ShardedJedisPipeline p = jedis.pipelined();
        Response<String> string = p.get("string");
        string.get();
        p.sync();
    }

    @Test
    public void canRetrieveUnsetKey() {
        ShardedJedisPipeline p = jedis.pipelined();
        Response<String> shouldNotExist = p.get(UUID.randomUUID().toString());
        p.sync();
        Assert.assertNull(shouldNotExist.get());
    }

    @Test
    public void testSyncWithNoCommandQueued() {
        JedisShardInfo shardInfo1 = new JedisShardInfo(ShardedJedisPipelineTest.redis1.getHost(), ShardedJedisPipelineTest.redis1.getPort());
        JedisShardInfo shardInfo2 = new JedisShardInfo(ShardedJedisPipelineTest.redis2.getHost(), ShardedJedisPipelineTest.redis2.getPort());
        shardInfo1.setPassword("foobared");
        shardInfo2.setPassword("foobared");
        List<JedisShardInfo> shards = new ArrayList<JedisShardInfo>();
        shards.add(shardInfo1);
        shards.add(shardInfo2);
        ShardedJedis jedis2 = new ShardedJedis(shards);
        ShardedJedisPipeline pipeline = jedis2.pipelined();
        pipeline.sync();
        jedis2.close();
        jedis2 = new ShardedJedis(shards);
        pipeline = jedis2.pipelined();
        List<Object> resp = pipeline.syncAndReturnAll();
        Assert.assertTrue(resp.isEmpty());
        jedis2.close();
    }
}

