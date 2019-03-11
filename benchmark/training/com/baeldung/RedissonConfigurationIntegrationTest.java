package com.baeldung;


import com.google.common.io.Files;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import org.junit.Test;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import redis.embedded.RedisServer;


/**
 * Created by johnson on 3/9/17.
 */
public class RedissonConfigurationIntegrationTest {
    private static RedisServer redisServer;

    private static RedissonClient client;

    private static int port;

    @Test
    public void givenJavaConfig_thenRedissonConnectToRedis() {
        Config config = new Config();
        config.useSingleServer().setAddress(String.format("127.0.0.1:%s", RedissonConfigurationIntegrationTest.port));
        RedissonConfigurationIntegrationTest.client = Redisson.create(config);
        assert ((RedissonConfigurationIntegrationTest.client) != null) && ((RedissonConfigurationIntegrationTest.client.getKeys().count()) >= 0);
    }

    @Test
    public void givenJSONFileConfig_thenRedissonConnectToRedis() throws IOException {
        File configFile = new File(getClass().getClassLoader().getResource("singleNodeConfig.json").getFile());
        String configContent = Files.toString(configFile, Charset.defaultCharset()).replace("6379", String.valueOf(RedissonConfigurationIntegrationTest.port));
        Config config = Config.fromJSON(configContent);
        RedissonConfigurationIntegrationTest.client = Redisson.create(config);
        assert ((RedissonConfigurationIntegrationTest.client) != null) && ((RedissonConfigurationIntegrationTest.client.getKeys().count()) >= 0);
    }

    @Test
    public void givenYAMLFileConfig_thenRedissonConnectToRedis() throws IOException {
        File configFile = new File(getClass().getClassLoader().getResource("singleNodeConfig.yaml").getFile());
        String configContent = Files.toString(configFile, Charset.defaultCharset()).replace("6379", String.valueOf(RedissonConfigurationIntegrationTest.port));
        Config config = Config.fromYAML(configContent);
        RedissonConfigurationIntegrationTest.client = Redisson.create(config);
        assert ((RedissonConfigurationIntegrationTest.client) != null) && ((RedissonConfigurationIntegrationTest.client.getKeys().count()) >= 0);
    }
}

