/**
 * Copyright 2002-2016 the original author or authors.
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


import java.util.Collections;
import java.util.concurrent.atomic.AtomicLong;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.cache.support.SimpleValueWrapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 *
 *
 * @author Stephane Nicoll
 */
public class CacheErrorHandlerTests {
    @Rule
    public final ExpectedException thrown = ExpectedException.none();

    private Cache cache;

    private CacheInterceptor cacheInterceptor;

    private CacheErrorHandler errorHandler;

    private CacheErrorHandlerTests.SimpleService simpleService;

    @Test
    public void getFail() {
        UnsupportedOperationException exception = new UnsupportedOperationException("Test exception on get");
        BDDMockito.willThrow(exception).given(this.cache).get(0L);
        Object result = this.simpleService.get(0L);
        Mockito.verify(this.errorHandler).handleCacheGetError(exception, cache, 0L);
        Mockito.verify(this.cache).get(0L);
        Mockito.verify(this.cache).put(0L, result);// result of the invocation

    }

    @Test
    public void getAndPutFail() {
        UnsupportedOperationException exception = new UnsupportedOperationException("Test exception on get");
        BDDMockito.willThrow(exception).given(this.cache).get(0L);
        BDDMockito.willThrow(exception).given(this.cache).put(0L, 0L);// Update of the cache will fail as well

        Object counter = this.simpleService.get(0L);
        BDDMockito.willReturn(new SimpleValueWrapper(2L)).given(this.cache).get(0L);
        Object counter2 = this.simpleService.get(0L);
        Object counter3 = this.simpleService.get(0L);
        Assert.assertNotSame(counter, counter2);
        Assert.assertEquals(counter2, counter3);
    }

    @Test
    public void getFailProperException() {
        UnsupportedOperationException exception = new UnsupportedOperationException("Test exception on get");
        BDDMockito.willThrow(exception).given(this.cache).get(0L);
        this.cacheInterceptor.setErrorHandler(new SimpleCacheErrorHandler());
        this.thrown.expect(CoreMatchers.is(exception));
        this.simpleService.get(0L);
    }

    @Test
    public void putFail() {
        UnsupportedOperationException exception = new UnsupportedOperationException("Test exception on put");
        BDDMockito.willThrow(exception).given(this.cache).put(0L, 0L);
        this.simpleService.put(0L);
        Mockito.verify(this.errorHandler).handleCachePutError(exception, cache, 0L, 0L);
    }

    @Test
    public void putFailProperException() {
        UnsupportedOperationException exception = new UnsupportedOperationException("Test exception on put");
        BDDMockito.willThrow(exception).given(this.cache).put(0L, 0L);
        this.cacheInterceptor.setErrorHandler(new SimpleCacheErrorHandler());
        this.thrown.expect(CoreMatchers.is(exception));
        this.simpleService.put(0L);
    }

    @Test
    public void evictFail() {
        UnsupportedOperationException exception = new UnsupportedOperationException("Test exception on evict");
        BDDMockito.willThrow(exception).given(this.cache).evict(0L);
        this.simpleService.evict(0L);
        Mockito.verify(this.errorHandler).handleCacheEvictError(exception, cache, 0L);
    }

    @Test
    public void evictFailProperException() {
        UnsupportedOperationException exception = new UnsupportedOperationException("Test exception on evict");
        BDDMockito.willThrow(exception).given(this.cache).evict(0L);
        this.cacheInterceptor.setErrorHandler(new SimpleCacheErrorHandler());
        this.thrown.expect(CoreMatchers.is(exception));
        this.simpleService.evict(0L);
    }

    @Test
    public void clearFail() {
        UnsupportedOperationException exception = new UnsupportedOperationException("Test exception on evict");
        BDDMockito.willThrow(exception).given(this.cache).clear();
        this.simpleService.clear();
        Mockito.verify(this.errorHandler).handleCacheClearError(exception, cache);
    }

    @Test
    public void clearFailProperException() {
        UnsupportedOperationException exception = new UnsupportedOperationException("Test exception on evict");
        BDDMockito.willThrow(exception).given(this.cache).clear();
        this.cacheInterceptor.setErrorHandler(new SimpleCacheErrorHandler());
        this.thrown.expect(CoreMatchers.is(exception));
        this.simpleService.clear();
    }

    @Configuration
    @EnableCaching
    static class Config extends CachingConfigurerSupport {
        @Bean
        @Override
        public CacheErrorHandler errorHandler() {
            return Mockito.mock(CacheErrorHandler.class);
        }

        @Bean
        public CacheErrorHandlerTests.SimpleService simpleService() {
            return new CacheErrorHandlerTests.SimpleService();
        }

        @Bean
        public CacheManager cacheManager() {
            SimpleCacheManager cacheManager = new SimpleCacheManager();
            cacheManager.setCaches(Collections.singletonList(mockCache()));
            return cacheManager;
        }

        @Bean
        public Cache mockCache() {
            Cache cache = Mockito.mock(Cache.class);
            BDDMockito.given(cache.getName()).willReturn("test");
            return cache;
        }
    }

    @CacheConfig(cacheNames = "test")
    public static class SimpleService {
        private AtomicLong counter = new AtomicLong();

        @Cacheable
        public Object get(long id) {
            return this.counter.getAndIncrement();
        }

        @CachePut
        public Object put(long id) {
            return this.counter.getAndIncrement();
        }

        @CacheEvict
        public void evict(long id) {
        }

        @CacheEvict(allEntries = true)
        public void clear() {
        }
    }
}

