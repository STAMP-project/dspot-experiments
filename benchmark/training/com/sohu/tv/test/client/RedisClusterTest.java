package com.sohu.tv.test.client;


import com.sohu.tv.builder.ClientBuilder;
import com.sohu.tv.test.base.BaseTest;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import org.junit.Test;


/**
 * cachecloud-rediscluster?????
 *
 * @author leifu
 * @unknown 2014?11?21?
 * @unknown ??11:58:53
 */
public class RedisClusterTest extends BaseTest {
    private static final long appId = 0L;

    @Test
    public void pushData() throws Exception {
        JedisCluster redisCluster = ClientBuilder.redisCluster(RedisClusterTest.appId).setJedisPoolConfig(getPoolConfig()).setConnectionTimeout(2000).setSoTimeout(1000).build();
        for (int i = 1; i < 100; i++) {
            redisCluster.setex(("tmp:key" + i), (60 * 20), ("value" + i));
            TimeUnit.MILLISECONDS.sleep(20);
            logger.info(("push:" + i));
        }
    }

    @Test
    public void testCluster() {
        JedisCluster redisCluster = ClientBuilder.redisCluster(RedisClusterTest.appId).setJedisPoolConfig(getPoolConfig()).setConnectionTimeout(2000).setSoTimeout(1000).build();
        Map<String, JedisPool> clusterMap = redisCluster.getClusterNodes();
        for (String key : clusterMap.keySet()) {
            logger.info("key={}", key);
            JedisPool jedisPool = clusterMap.get(key);
            Jedis jedis = jedisPool.getResource();
            logger.info(("before:cluster-slave-validity-factor->" + (jedis.configGet("cluster-slave-validity-factor"))));
            jedis.configSet("cluster-slave-validity-factor", "10");
            logger.info(("after:cluster-slave-validity-factor->" + (jedis.configGet("cluster-slave-validity-factor"))));
            logger.info("------------------------------------");
            logger.info(("before:repl-disable-tcp-nodelay->" + (jedis.configGet("repl-disable-tcp-nodelay"))));
            jedis.configSet("repl-disable-tcp-nodelay", "no");
            logger.info(("after:repl-disable-tcp-nodelay->" + (jedis.configGet("repl-disable-tcp-nodelay"))));
            logger.info("####################################");
            jedis.close();
        }
    }
}

