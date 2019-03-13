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
package org.springframework.cache.jcache.interceptor;


import java.util.Arrays;
import java.util.concurrent.atomic.AtomicLong;
import javax.cache.annotation.CacheDefaults;
import javax.cache.annotation.CachePut;
import javax.cache.annotation.CacheRemove;
import javax.cache.annotation.CacheRemoveAll;
import javax.cache.annotation.CacheResult;
import javax.cache.annotation.CacheValue;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.cache.interceptor.SimpleKeyGenerator;
import org.springframework.cache.jcache.config.JCacheConfigurerSupport;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 *
 *
 * @author Stephane Nicoll
 */
public class JCacheErrorHandlerTests {
    private Cache cache;

    private Cache errorCache;

    private CacheErrorHandler errorHandler;

    private JCacheErrorHandlerTests.SimpleService simpleService;

    @Rule
    public final ExpectedException thrown = ExpectedException.none();

    @Test
    public void getFail() {
        UnsupportedOperationException exception = new UnsupportedOperationException("Test exception on get");
        Object key = SimpleKeyGenerator.generateKey(0L);
        BDDMockito.willThrow(exception).given(this.cache).get(key);
        this.simpleService.get(0L);
        Mockito.verify(this.errorHandler).handleCacheGetError(exception, this.cache, key);
    }

    @Test
    public void getPutNewElementFail() {
        UnsupportedOperationException exception = new UnsupportedOperationException("Test exception on put");
        Object key = SimpleKeyGenerator.generateKey(0L);
        BDDMockito.given(this.cache.get(key)).willReturn(null);
        BDDMockito.willThrow(exception).given(this.cache).put(key, 0L);
        this.simpleService.get(0L);
        Mockito.verify(this.errorHandler).handleCachePutError(exception, this.cache, key, 0L);
    }

    @Test
    public void getFailPutExceptionFail() {
        UnsupportedOperationException exceptionOnPut = new UnsupportedOperationException("Test exception on put");
        Object key = SimpleKeyGenerator.generateKey(0L);
        BDDMockito.given(this.cache.get(key)).willReturn(null);
        BDDMockito.willThrow(exceptionOnPut).given(this.errorCache).put(key, JCacheErrorHandlerTests.SimpleService.TEST_EXCEPTION);
        try {
            this.simpleService.getFail(0L);
        } catch (IllegalStateException ex) {
            Assert.assertEquals("Test exception", ex.getMessage());
        }
        Mockito.verify(this.errorHandler).handleCachePutError(exceptionOnPut, this.errorCache, key, JCacheErrorHandlerTests.SimpleService.TEST_EXCEPTION);
    }

    @Test
    public void putFail() {
        UnsupportedOperationException exception = new UnsupportedOperationException("Test exception on put");
        Object key = SimpleKeyGenerator.generateKey(0L);
        BDDMockito.willThrow(exception).given(this.cache).put(key, 234L);
        this.simpleService.put(0L, 234L);
        Mockito.verify(this.errorHandler).handleCachePutError(exception, this.cache, key, 234L);
    }

    @Test
    public void evictFail() {
        UnsupportedOperationException exception = new UnsupportedOperationException("Test exception on evict");
        Object key = SimpleKeyGenerator.generateKey(0L);
        BDDMockito.willThrow(exception).given(this.cache).evict(key);
        this.simpleService.evict(0L);
        Mockito.verify(this.errorHandler).handleCacheEvictError(exception, this.cache, key);
    }

    @Test
    public void clearFail() {
        UnsupportedOperationException exception = new UnsupportedOperationException("Test exception on evict");
        BDDMockito.willThrow(exception).given(this.cache).clear();
        this.simpleService.clear();
        Mockito.verify(this.errorHandler).handleCacheClearError(exception, this.cache);
    }

    @Configuration
    @EnableCaching
    static class Config extends JCacheConfigurerSupport {
        @Bean
        @Override
        public CacheManager cacheManager() {
            SimpleCacheManager cacheManager = new SimpleCacheManager();
            cacheManager.setCaches(Arrays.asList(mockCache(), mockErrorCache()));
            return cacheManager;
        }

        @Bean
        @Override
        public CacheErrorHandler errorHandler() {
            return Mockito.mock(CacheErrorHandler.class);
        }

        @Bean
        public JCacheErrorHandlerTests.SimpleService simpleService() {
            return new JCacheErrorHandlerTests.SimpleService();
        }

        @Bean
        public Cache mockCache() {
            Cache cache = Mockito.mock(Cache.class);
            BDDMockito.given(cache.getName()).willReturn("test");
            return cache;
        }

        @Bean
        public Cache mockErrorCache() {
            Cache cache = Mockito.mock(Cache.class);
            BDDMockito.given(cache.getName()).willReturn("error");
            return cache;
        }
    }

    @CacheDefaults(cacheName = "test")
    public static class SimpleService {
        private static final IllegalStateException TEST_EXCEPTION = new IllegalStateException("Test exception");

        private AtomicLong counter = new AtomicLong();

        @CacheResult
        public Object get(long id) {
            return this.counter.getAndIncrement();
        }

        @CacheResult(exceptionCacheName = "error")
        public Object getFail(long id) {
            throw JCacheErrorHandlerTests.SimpleService.TEST_EXCEPTION;
        }

        @CachePut
        public void put(long id, @CacheValue
        Object object) {
        }

        @CacheRemove
        public void evict(long id) {
        }

        @CacheRemoveAll
        public void clear() {
        }
    }
}

