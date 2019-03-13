/**
 * Copyright 2002-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.cache.config;


import java.util.concurrent.atomic.AtomicLong;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.CacheTestUtils;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.Environment;
import org.springframework.mock.env.MockEnvironment;


/**
 * Tests that represent real use cases with advanced configuration.
 *
 * @author Stephane Nicoll
 */
public class EnableCachingIntegrationTests {
    private ConfigurableApplicationContext context;

    @Test
    public void fooServiceWithInterface() {
        this.context = new AnnotationConfigApplicationContext(EnableCachingIntegrationTests.FooConfig.class);
        EnableCachingIntegrationTests.FooService service = this.context.getBean(EnableCachingIntegrationTests.FooService.class);
        fooGetSimple(service);
    }

    @Test
    public void fooServiceWithInterfaceCglib() {
        this.context = new AnnotationConfigApplicationContext(EnableCachingIntegrationTests.FooConfigCglib.class);
        EnableCachingIntegrationTests.FooService service = this.context.getBean(EnableCachingIntegrationTests.FooService.class);
        fooGetSimple(service);
    }

    @Test
    public void beanConditionOff() {
        this.context = new AnnotationConfigApplicationContext(EnableCachingIntegrationTests.BeanConditionConfig.class);
        EnableCachingIntegrationTests.FooService service = this.context.getBean(EnableCachingIntegrationTests.FooService.class);
        Cache cache = getCache();
        Object key = new Object();
        service.getWithCondition(key);
        CacheTestUtils.assertCacheMiss(key, cache);
        service.getWithCondition(key);
        CacheTestUtils.assertCacheMiss(key, cache);
        Assert.assertEquals(2, this.context.getBean(EnableCachingIntegrationTests.BeanConditionConfig.Bar.class).count);
    }

    @Test
    public void beanConditionOn() {
        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
        ctx.setEnvironment(new MockEnvironment().withProperty("bar.enabled", "true"));
        ctx.register(EnableCachingIntegrationTests.BeanConditionConfig.class);
        ctx.refresh();
        this.context = ctx;
        EnableCachingIntegrationTests.FooService service = this.context.getBean(EnableCachingIntegrationTests.FooService.class);
        Cache cache = getCache();
        Object key = new Object();
        Object value = service.getWithCondition(key);
        CacheTestUtils.assertCacheHit(key, value, cache);
        value = service.getWithCondition(key);
        CacheTestUtils.assertCacheHit(key, value, cache);
        Assert.assertEquals(2, this.context.getBean(EnableCachingIntegrationTests.BeanConditionConfig.Bar.class).count);
    }

    @Configuration
    static class SharedConfig extends CachingConfigurerSupport {
        @Override
        @Bean
        public CacheManager cacheManager() {
            return CacheTestUtils.createSimpleCacheManager("testCache");
        }
    }

    @Configuration
    @Import(EnableCachingIntegrationTests.SharedConfig.class)
    @EnableCaching
    static class FooConfig {
        @Bean
        public EnableCachingIntegrationTests.FooService fooService() {
            return new EnableCachingIntegrationTests.FooServiceImpl();
        }
    }

    @Configuration
    @Import(EnableCachingIntegrationTests.SharedConfig.class)
    @EnableCaching(proxyTargetClass = true)
    static class FooConfigCglib {
        @Bean
        public EnableCachingIntegrationTests.FooService fooService() {
            return new EnableCachingIntegrationTests.FooServiceImpl();
        }
    }

    interface FooService {
        Object getSimple(Object key);

        Object getWithCondition(Object key);
    }

    @CacheConfig(cacheNames = "testCache")
    static class FooServiceImpl implements EnableCachingIntegrationTests.FooService {
        private final AtomicLong counter = new AtomicLong();

        @Override
        @Cacheable
        public Object getSimple(Object key) {
            return this.counter.getAndIncrement();
        }

        @Override
        @Cacheable(condition = "@bar.enabled")
        public Object getWithCondition(Object key) {
            return this.counter.getAndIncrement();
        }
    }

    @Configuration
    @Import(EnableCachingIntegrationTests.FooConfig.class)
    @EnableCaching
    static class BeanConditionConfig {
        @Autowired
        Environment env;

        @Bean
        public EnableCachingIntegrationTests.BeanConditionConfig.Bar bar() {
            return new EnableCachingIntegrationTests.BeanConditionConfig.Bar(Boolean.valueOf(env.getProperty("bar.enabled")));
        }

        static class Bar {
            public int count;

            private final boolean enabled;

            public Bar(boolean enabled) {
                this.enabled = enabled;
            }

            public boolean isEnabled() {
                (this.count)++;
                return this.enabled;
            }
        }
    }
}

