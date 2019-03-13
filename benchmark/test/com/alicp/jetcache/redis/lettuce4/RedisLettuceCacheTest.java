package com.alicp.jetcache.redis.lettuce4;


import FastjsonKeyConvertor.INSTANCE;
import RedisURI.Builder;
import com.alicp.jetcache.Cache;
import com.alicp.jetcache.LoadingCacheTest;
import com.alicp.jetcache.MultiLevelCacheBuilder;
import com.alicp.jetcache.embedded.CaffeineCacheBuilder;
import com.alicp.jetcache.test.external.AbstractExternalCacheTest;
import com.lambdaworks.redis.RedisClient;
import com.lambdaworks.redis.RedisURI;
import com.lambdaworks.redis.cluster.RedisClusterClient;
import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import org.junit.Test;


/**
 * Created on 2017/5/4.
 *
 * @author <a href="mailto:areyouok@gmail.com">huangli</a>
 */
public class RedisLettuceCacheTest extends AbstractExternalCacheTest {
    @Test
    public void testSimple() throws Exception {
        RedisClient client = RedisClient.create("redis://127.0.0.1");
        test(client);
    }

    @Test
    public void testSentinel() throws Exception {
        RedisURI redisUri = Builder.sentinel("127.0.0.1", 26379, "mymaster").withSentinel("127.0.0.1", 26380).withSentinel("127.0.0.1", 26381).build();
        RedisClient client = RedisClient.create(redisUri);
        test(client);
    }

    @Test
    public void testCluster() throws Exception {
        if (!(RedisLettuceCacheTest.checkOS())) {
            return;
        }
        RedisURI node1 = RedisURI.create("127.0.0.1", 7000);
        RedisURI node2 = RedisURI.create("127.0.0.1", 7001);
        RedisURI node3 = RedisURI.create("127.0.0.1", 7002);
        RedisClusterClient client = RedisClusterClient.create(Arrays.asList(node1, node2, node3));
        test(client);
    }

    @Test
    public void testWithMultiLevelCache() throws Exception {
        Cache l1Cache = CaffeineCacheBuilder.createCaffeineCacheBuilder().limit(10).expireAfterWrite(500, TimeUnit.MILLISECONDS).keyConvertor(INSTANCE).buildCache();
        RedisClient client = RedisClient.create("redis://127.0.0.1");
        Cache l2Cache = RedisLettuceCacheBuilder.createRedisLettuceCacheBuilder().redisClient(client).keyConvertor(INSTANCE).valueEncoder(JavaValueEncoder.INSTANCE).valueDecoder(JavaValueDecoder.INSTANCE).keyPrefix(((new Random().nextInt()) + "")).expireAfterWrite(500, TimeUnit.MILLISECONDS).buildCache();
        cache = MultiLevelCacheBuilder.createMultiLevelCacheBuilder().expireAfterWrite(500, TimeUnit.MILLISECONDS).addCache(l1Cache, l2Cache).buildCache();
        baseTest();
        expireAfterWriteTest(500);
        LoadingCacheTest.loadingCacheTest(MultiLevelCacheBuilder.createMultiLevelCacheBuilder().expireAfterWrite(5000, TimeUnit.MILLISECONDS).addCache(l1Cache, l2Cache), 50);
        LettuceConnectionManager.defaultManager().removeAndClose(client);
    }
}

