/**
 * Created on 2018/1/30.
 */
package com.alicp.jetcache;


import FastjsonKeyConvertor.INSTANCE;
import com.alicp.jetcache.embedded.CaffeineCacheBuilder;
import com.alicp.jetcache.redis.RedisCacheBuilder;
import com.alicp.jetcache.redis.lettuce.RedisLettuceCacheBuilder;
import com.alicp.jetcache.test.MockRemoteCacheBuilder;
import io.lettuce.core.RedisClient;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.junit.jupiter.api.Test;
import redis.clients.jedis.JedisPool;


/**
 *
 *
 * @author <a href="mailto:areyouok@gmail.com">huangli</a>
 */
public class MultiLevelCacheMixTest {
    private Cache<Object, Object> cache;

    private Cache<Object, Object> l1Cache;

    private Cache<Object, Object> l2Cache;

    @Test
    public void testWithMockRemoteCache() {
        l1Cache = CaffeineCacheBuilder.createCaffeineCacheBuilder().limit(10).expireAfterWrite(1000, TimeUnit.MILLISECONDS).keyConvertor(INSTANCE).buildCache();
        l2Cache = new MockRemoteCacheBuilder().limit(1000).expireAfterWrite(1000, TimeUnit.MILLISECONDS).keyConvertor(INSTANCE).buildCache();
        cache = MultiLevelCacheBuilder.createMultiLevelCacheBuilder().addCache(l1Cache, l2Cache).buildCache();
        testImpl();
    }

    @Test
    public void testWithJedis() {
        l1Cache = CaffeineCacheBuilder.createCaffeineCacheBuilder().limit(10).expireAfterWrite(1000, TimeUnit.MILLISECONDS).keyConvertor(INSTANCE).buildCache();
        GenericObjectPoolConfig pc = new GenericObjectPoolConfig();
        pc.setMinIdle(1);
        pc.setMaxIdle(1);
        pc.setMaxTotal(2);
        JedisPool pool = new JedisPool(pc, "localhost", 6379);
        l2Cache = RedisCacheBuilder.createRedisCacheBuilder().keyConvertor(INSTANCE).valueEncoder(JavaValueEncoder.INSTANCE).valueDecoder(JavaValueDecoder.INSTANCE).jedisPool(pool).keyPrefix(((new Random().nextInt()) + "")).expireAfterWrite(1000, TimeUnit.MILLISECONDS).buildCache();
        cache = MultiLevelCacheBuilder.createMultiLevelCacheBuilder().addCache(l1Cache, l2Cache).buildCache();
        testImpl();
    }

    @Test
    public void testWithLettuce() {
        l1Cache = CaffeineCacheBuilder.createCaffeineCacheBuilder().limit(10).expireAfterWrite(1000, TimeUnit.MILLISECONDS).keyConvertor(INSTANCE).buildCache();
        RedisClient client = RedisClient.create("redis://127.0.0.1");
        l2Cache = RedisLettuceCacheBuilder.createRedisLettuceCacheBuilder().redisClient(client).keyConvertor(INSTANCE).valueEncoder(JavaValueEncoder.INSTANCE).valueDecoder(JavaValueDecoder.INSTANCE).keyPrefix(((new Random().nextInt()) + "")).expireAfterWrite(1000, TimeUnit.MILLISECONDS).buildCache();
        cache = MultiLevelCacheBuilder.createMultiLevelCacheBuilder().addCache(l1Cache, l2Cache).buildCache();
        testImpl();
    }
}

