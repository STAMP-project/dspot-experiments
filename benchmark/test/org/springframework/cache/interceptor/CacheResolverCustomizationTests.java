/**
 * Copyright 2002-2017 the original author or authors.
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
package org.springframework.cache.interceptor;


import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.CacheTestUtils;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.Nullable;
import org.springframework.util.ReflectionUtils;


/**
 * Provides various {@link CacheResolver} customisations scenario
 *
 * @author Stephane Nicoll
 * @since 4.1
 */
public class CacheResolverCustomizationTests {
    private CacheManager cacheManager;

    private CacheManager anotherCacheManager;

    private CacheResolverCustomizationTests.SimpleService simpleService;

    @Test
    public void noCustomization() {
        Cache cache = this.cacheManager.getCache("default");
        Object key = new Object();
        CacheTestUtils.assertCacheMiss(key, cache);
        Object value = this.simpleService.getSimple(key);
        CacheTestUtils.assertCacheHit(key, value, cache);
    }

    @Test
    public void customCacheResolver() {
        Cache cache = this.cacheManager.getCache("primary");
        Object key = new Object();
        CacheTestUtils.assertCacheMiss(key, cache);
        Object value = this.simpleService.getWithCustomCacheResolver(key);
        CacheTestUtils.assertCacheHit(key, value, cache);
    }

    @Test
    public void customCacheManager() {
        Cache cache = this.anotherCacheManager.getCache("default");
        Object key = new Object();
        CacheTestUtils.assertCacheMiss(key, cache);
        Object value = this.simpleService.getWithCustomCacheManager(key);
        CacheTestUtils.assertCacheHit(key, value, cache);
    }

    @Test
    public void runtimeResolution() {
        Cache defaultCache = this.cacheManager.getCache("default");
        Cache primaryCache = this.cacheManager.getCache("primary");
        Object key = new Object();
        CacheTestUtils.assertCacheMiss(key, defaultCache, primaryCache);
        Object value = this.simpleService.getWithRuntimeCacheResolution(key, "default");
        CacheTestUtils.assertCacheHit(key, value, defaultCache);
        CacheTestUtils.assertCacheMiss(key, primaryCache);
        Object key2 = new Object();
        CacheTestUtils.assertCacheMiss(key2, defaultCache, primaryCache);
        Object value2 = this.simpleService.getWithRuntimeCacheResolution(key2, "primary");
        CacheTestUtils.assertCacheHit(key2, value2, primaryCache);
        CacheTestUtils.assertCacheMiss(key2, defaultCache);
    }

    @Test
    public void namedResolution() {
        Cache cache = this.cacheManager.getCache("secondary");
        Object key = new Object();
        CacheTestUtils.assertCacheMiss(key, cache);
        Object value = this.simpleService.getWithNamedCacheResolution(key);
        CacheTestUtils.assertCacheHit(key, value, cache);
    }

    @Test
    public void noCacheResolved() {
        Method method = ReflectionUtils.findMethod(CacheResolverCustomizationTests.SimpleService.class, "noCacheResolved", Object.class);
        try {
            this.simpleService.noCacheResolved(new Object());
            Assert.fail("Should have failed, no cache resolved");
        } catch (IllegalStateException ex) {
            Assert.assertTrue("Reference to the method must be contained in the message", ex.getMessage().contains(method.toString()));
        }
    }

    @Test
    public void unknownCacheResolver() {
        try {
            this.simpleService.unknownCacheResolver(new Object());
            Assert.fail("Should have failed, no cache resolver with that name");
        } catch (NoSuchBeanDefinitionException ex) {
            Assert.assertEquals("Wrong bean name in exception", "unknownCacheResolver", ex.getBeanName());
        }
    }

    @Configuration
    @EnableCaching
    static class Config extends CachingConfigurerSupport {
        @Override
        @Bean
        public CacheManager cacheManager() {
            return CacheTestUtils.createSimpleCacheManager("default", "primary", "secondary");
        }

        @Bean
        public CacheManager anotherCacheManager() {
            return CacheTestUtils.createSimpleCacheManager("default", "primary", "secondary");
        }

        @Bean
        public CacheResolver primaryCacheResolver() {
            return new NamedCacheResolver(cacheManager(), "primary");
        }

        @Bean
        public CacheResolver secondaryCacheResolver() {
            return new NamedCacheResolver(cacheManager(), "primary");
        }

        @Bean
        public CacheResolver runtimeCacheResolver() {
            return new CacheResolverCustomizationTests.RuntimeCacheResolver(cacheManager());
        }

        @Bean
        public CacheResolver namedCacheResolver() {
            NamedCacheResolver resolver = new NamedCacheResolver();
            resolver.setCacheManager(cacheManager());
            resolver.setCacheNames(Collections.singleton("secondary"));
            return resolver;
        }

        @Bean
        public CacheResolver nullCacheResolver() {
            return new CacheResolverCustomizationTests.NullCacheResolver(cacheManager());
        }

        @Bean
        public CacheResolverCustomizationTests.SimpleService simpleService() {
            return new CacheResolverCustomizationTests.SimpleService();
        }
    }

    @CacheConfig(cacheNames = "default")
    static class SimpleService {
        private final AtomicLong counter = new AtomicLong();

        @Cacheable
        public Object getSimple(Object key) {
            return this.counter.getAndIncrement();
        }

        @Cacheable(cacheResolver = "primaryCacheResolver")
        public Object getWithCustomCacheResolver(Object key) {
            return this.counter.getAndIncrement();
        }

        @Cacheable(cacheManager = "anotherCacheManager")
        public Object getWithCustomCacheManager(Object key) {
            return this.counter.getAndIncrement();
        }

        @Cacheable(cacheResolver = "runtimeCacheResolver", key = "#p0")
        public Object getWithRuntimeCacheResolution(Object key, String cacheName) {
            return this.counter.getAndIncrement();
        }

        @Cacheable(cacheResolver = "namedCacheResolver")
        public Object getWithNamedCacheResolution(Object key) {
            return this.counter.getAndIncrement();
        }

        // No cache resolved for the operation
        @Cacheable(cacheResolver = "nullCacheResolver")
        public Object noCacheResolved(Object key) {
            return this.counter.getAndIncrement();
        }

        // No such bean defined
        @Cacheable(cacheResolver = "unknownCacheResolver")
        public Object unknownCacheResolver(Object key) {
            return this.counter.getAndIncrement();
        }
    }

    /**
     * Example of {@link CacheResolver} that resolve the caches at
     * runtime (i.e. based on method invocation parameters).
     * <p>Expects the second argument to hold the name of the cache to use
     */
    private static class RuntimeCacheResolver extends AbstractCacheResolver {
        private RuntimeCacheResolver(CacheManager cacheManager) {
            super(cacheManager);
        }

        @Override
        @Nullable
        protected Collection<String> getCacheNames(CacheOperationInvocationContext<?> context) {
            String cacheName = ((String) (context.getArgs()[1]));
            return Collections.singleton(cacheName);
        }
    }

    private static class NullCacheResolver extends AbstractCacheResolver {
        private NullCacheResolver(CacheManager cacheManager) {
            super(cacheManager);
        }

        @Override
        @Nullable
        protected Collection<String> getCacheNames(CacheOperationInvocationContext<?> context) {
            return null;
        }
    }
}

