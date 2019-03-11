package com.alicp.jetcache.autoconfigure;


import FastjsonKeyConvertor.INSTANCE;
import com.alicp.jetcache.Cache;
import com.alicp.jetcache.anno.CreateCache;
import com.alicp.jetcache.anno.config.EnableCreateCacheAnnotation;
import com.alicp.jetcache.anno.config.EnableMethodCache;
import com.alicp.jetcache.redis.lettuce4.RedisLettuceCacheConfig;
import com.alicp.jetcache.redis.lettuce4.RedisLettuceCacheTest;
import com.alicp.jetcache.test.beans.MyFactoryBean;
import com.alicp.jetcache.test.spring.SpringTest;
import com.lambdaworks.redis.RedisClient;
import com.lambdaworks.redis.api.StatefulRedisConnection;
import com.lambdaworks.redis.api.async.RedisAsyncCommands;
import com.lambdaworks.redis.api.rx.RedisReactiveCommands;
import com.lambdaworks.redis.api.sync.RedisCommands;
import com.lambdaworks.redis.cluster.RedisClusterClient;
import com.lambdaworks.redis.cluster.api.async.RedisClusterAsyncCommands;
import com.lambdaworks.redis.cluster.api.rx.RedisClusterReactiveCommands;
import com.lambdaworks.redis.cluster.api.sync.RedisClusterCommands;
import javax.annotation.PostConstruct;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import static RedisLettuce4AutoConfiguration.AUTO_INIT_BEAN_NAME;


/**
 * Created on 2017/05/11.
 *
 * @author <a href="mailto:areyouok@gmail.com">huangli</a>
 */
@Configuration
@EnableAutoConfiguration
@ComponentScan(basePackages = { "com.alicp.jetcache.test.beans", "com.alicp.jetcache.anno.inittestbeans" })
@EnableMethodCache(basePackages = { "com.alicp.jetcache.test.beans", "com.alicp.jetcache.anno.inittestbeans" })
@EnableCreateCacheAnnotation
public class RedisLettuce4StarterTest extends SpringTest {
    @Test
    public void tests() throws Exception {
        if (RedisLettuceCacheTest.checkOS()) {
            System.setProperty("spring.profiles.active", "redislettuce4-cluster");
        } else {
            System.setProperty("spring.profiles.active", "redislettuce4");
        }
        context = SpringApplication.run(RedisLettuce4StarterTest.class);
        doTest();
        RedisLettuce4StarterTest.A bean = context.getBean(RedisLettuce4StarterTest.A.class);
        bean.test();
        RedisClient t1 = ((RedisClient) (context.getBean("defaultClient")));
        RedisClient t2 = ((RedisClient) (context.getBean("a1Client")));
        Assert.assertNotNull(t1);
        Assert.assertNotNull(t2);
        Assert.assertNotSame(t1, t2);
        AutoConfigureBeans acb = context.getBean(AutoConfigureBeans.class);
        String key = "remote.A1";
        Assert.assertTrue(((getObject()) instanceof StatefulRedisConnection));
        Assert.assertTrue(((getObject()) instanceof RedisCommands));
        Assert.assertTrue(((getObject()) instanceof RedisAsyncCommands));
        Assert.assertTrue(((getObject()) instanceof RedisReactiveCommands));
        if (RedisLettuceCacheTest.checkOS()) {
            key = "remote.A2";
            Assert.assertTrue(((getObject()) instanceof RedisClusterClient));
            Assert.assertTrue(((getObject()) instanceof RedisClusterCommands));
            Assert.assertTrue(((getObject()) instanceof RedisClusterAsyncCommands));
            Assert.assertTrue(((getObject()) instanceof RedisClusterReactiveCommands));
        }
    }

    @Component
    public static class A {
        @CreateCache
        private Cache c1;

        public void test() {
            Assert.assertNotNull(c1.unwrap(RedisClient.class));
            RedisLettuceCacheConfig cc1 = ((RedisLettuceCacheConfig) (c1.config()));
            Assert.assertEquals(20000, cc1.getExpireAfterWriteInMillis());
            Assert.assertSame(INSTANCE, cc1.getKeyConvertor());
        }
    }

    @Component
    public static class B {
        @Autowired
        private RedisClient defaultClient;

        @Autowired
        private RedisClient a1Client;

        @PostConstruct
        public void init() {
            Assert.assertNotNull(defaultClient);
            Assert.assertNotNull(a1Client);
        }
    }

    @Configuration
    public static class Config {
        @Bean(name = "factoryBeanTarget")
        public MyFactoryBean factoryBean() {
            return new MyFactoryBean();
        }

        @Bean(name = "defaultClient")
        @DependsOn(AUTO_INIT_BEAN_NAME)
        public Lettuce4Factory defaultClient() {
            return new Lettuce4Factory("remote.default", RedisClient.class);
        }

        @Bean(name = "a1Client")
        @DependsOn(AUTO_INIT_BEAN_NAME)
        public Lettuce4Factory a1Client() {
            return new Lettuce4Factory("remote.A1", RedisClient.class);
        }
    }
}

