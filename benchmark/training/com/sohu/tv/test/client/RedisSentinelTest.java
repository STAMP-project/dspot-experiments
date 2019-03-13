package com.sohu.tv.test.client;


import com.sohu.tv.builder.ClientBuilder;
import com.sohu.tv.test.base.BaseTest;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.junit.Assert;
import org.junit.Test;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisSentinelPool;


/**
 * cachecloud-redisSentinel?????
 *
 * @author leifu
 * @unknown 2014?11?21?
 * @unknown ??11:58:53
 */
public class RedisSentinelTest extends BaseTest {
    private static final long appId = 0L;

    @Test
    public void testSentinel() {
        JedisSentinelPool sentinelPool = ClientBuilder.redisSentinel(RedisSentinelTest.appId).setConnectionTimeout(2000).setSoTimeout(1000).build();
        HostAndPort currentHostMaster = sentinelPool.getCurrentHostMaster();
        logger.info("current master: {}", currentHostMaster.toString());
        Jedis jedis = sentinelPool.getResource();
        for (int i = 0; i < 10; i++) {
            jedis.lpush("mylist", ("list-" + i));
        }
        jedis.close();
        sentinelPool.destroy();
    }

    @Test
    public void testSentinelExample() {
        JedisSentinelPool sentinelPool = null;
        // ??????
        // sentinelPool = ClientBuilder.redisSentinel(appId).build();
        /**
         * ?????
         */
        GenericObjectPoolConfig poolConfig = new GenericObjectPoolConfig();
        poolConfig.setMaxIdle(((GenericObjectPoolConfig.DEFAULT_MAX_IDLE) * 3));
        poolConfig.setMinIdle(((GenericObjectPoolConfig.DEFAULT_MIN_IDLE) * 2));
        poolConfig.setJmxEnabled(true);
        poolConfig.setMaxWaitMillis(3000);
        sentinelPool = ClientBuilder.redisSentinel(RedisSentinelTest.appId).setPoolConfig(poolConfig).setConnectionTimeout(2000).setSoTimeout(1000).build();
        Jedis jedis = sentinelPool.getResource();
        jedis.set("key1", "1");
        Assert.assertEquals("2", jedis.incr("key1"));
        jedis.close();
    }
}

