package com.alicp.jetcache.redis;


import FastjsonKeyConvertor.INSTANCE;
import com.alicp.jetcache.Cache;
import com.alicp.jetcache.test.external.AbstractExternalCacheTest;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.junit.Assert;
import org.junit.Test;
import redis.clients.util.ShardInfo;


/**
 * Created on 2016/10/8.
 *
 * @author <a href="mailto:areyouok@gmail.com">huangli</a>
 */
public class RedisCacheTest extends AbstractExternalCacheTest {
    @Test
    public void testSimplePool() throws Exception {
        System.out.println("RedisCacheTest.testSimplePool");
        GenericObjectPoolConfig pc = new GenericObjectPoolConfig();
        pc.setMinIdle(2);
        pc.setMaxIdle(10);
        pc.setMaxTotal(10);
        JedisPool pool = new JedisPool(pc, "localhost", 6379);
        testWithPool(pool);
    }

    @Test
    public void testSentinel() throws Exception {
        System.out.println("RedisCacheTest.testSentinel");
        GenericObjectPoolConfig pc = new GenericObjectPoolConfig();
        pc.setMinIdle(2);
        pc.setMaxIdle(10);
        pc.setMaxTotal(10);
        Set<String> sentinels = new HashSet<>();
        sentinels.add("127.0.0.1:26379");
        sentinels.add("127.0.0.1:26380");
        sentinels.add("127.0.0.1:26381");
        JedisSentinelPool pool = new JedisSentinelPool("mymaster", sentinels, pc);
        testWithPool(pool);
    }

    @Test
    public void shardTest() throws Exception {
        System.out.println("RedisCacheTest.shardTest");
        GenericObjectPoolConfig pc = new GenericObjectPoolConfig();
        pc.setMinIdle(2);
        pc.setMaxIdle(10);
        pc.setMaxTotal(10);
        List list = new ArrayList();
        ShardInfo si0 = new JedisShardInfo("localhost", 6379);
        ShardInfo si1 = new JedisShardInfo("localhost", 6379);
        list.add(si0);
        list.add(si1);
        ShardedJedisPool pool = new ShardedJedisPool(pc, list);
        testWithPool(pool);
    }

    @Test
    public void testRandomIndex() {
        {
            int[] ws = new int[]{ 100, 100, 100 };
            int[] result = new int[3];
            for (int i = 0; i < 10000; i++) {
                int index = RedisCache.randomIndex(ws);
                (result[index])++;
            }
            Assert.assertEquals(1.0, ((1.0 * (result[1])) / (result[0])), 0.2);
            Assert.assertEquals(1.0, ((1.0 * (result[2])) / (result[0])), 0.2);
        }
        {
            int[] ws = new int[]{ 1, 2, 3 };
            int[] result = new int[3];
            for (int i = 0; i < 10000; i++) {
                int index = RedisCache.randomIndex(ws);
                (result[index])++;
            }
            Assert.assertEquals(2.0, ((1.0 * (result[1])) / (result[0])), 0.2);
            Assert.assertEquals(3.0, ((1.0 * (result[2])) / (result[0])), 0.4);
        }
    }

    @Test
    public void readFromSlaveTest() throws Exception {
        System.out.println("RedisCacheTest.readFromSlaveTest");
        GenericObjectPoolConfig pc = new GenericObjectPoolConfig();
        pc.setMinIdle(2);
        pc.setMaxIdle(10);
        pc.setMaxTotal(10);
        JedisPool pool1 = new JedisPool(pc, "localhost", 6379);
        JedisPool pool2 = new JedisPool(pc, "localhost", 6380);
        JedisPool pool3 = new JedisPool(pc, "localhost", 6381);
        RedisCacheBuilder builder = RedisCacheBuilder.createRedisCacheBuilder();
        builder.setJedisPool(pool1);
        builder.setReadFromSlave(true);
        builder.setJedisSlavePools(pool2, pool3);
        builder.setSlaveReadWeights(1, 1);
        builder.setKeyConvertor(INSTANCE);
        builder.setValueEncoder(JavaValueEncoder.INSTANCE);
        builder.setValueDecoder(JavaValueDecoder.INSTANCE);
        builder.setKeyPrefix(((new Random().nextInt()) + ""));
        builder.setExpireAfterWriteInMillis(500);
        Cache cache = builder.buildCache();
        cache.put("readFromSlaveTest_K1", "V1");
        Assert.assertNotSame(pool1, getReadPool());
        Assert.assertNotSame(pool1, getReadPool());
        Assert.assertNotSame(pool1, getReadPool());
        Assert.assertNotSame(pool1, getReadPool());
        Thread.sleep(15);
        Assert.assertEquals("V1", cache.get("readFromSlaveTest_K1"));
        Assert.assertEquals("V1", cache.get("readFromSlaveTest_K1"));
        Assert.assertEquals("V1", cache.get("readFromSlaveTest_K1"));
        Assert.assertEquals("V1", cache.get("readFromSlaveTest_K1"));
    }
}

