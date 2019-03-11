package org.baeldung.caching.test;


import java.util.ArrayList;
import java.util.List;
import org.baeldung.caching.eviction.service.CachingService;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.concurrent.ConcurrentMapCacheFactoryBean;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


@RunWith(SpringJUnit4ClassRunner.class)
@org.springframework.test.context.ContextConfiguration
public class CacheManagerEvictIntegrationTest {
    @Configuration
    static class ContextConfiguration {
        @Bean
        public CachingService cachingService() {
            return new CachingService();
        }

        @Bean
        public CacheManager cacheManager() {
            SimpleCacheManager cacheManager = new SimpleCacheManager();
            List<Cache> caches = new ArrayList<>();
            caches.add(cacheBeanFirst().getObject());
            caches.add(cacheBeanSecond().getObject());
            cacheManager.setCaches(caches);
            return cacheManager;
        }

        @Bean
        public ConcurrentMapCacheFactoryBean cacheBeanFirst() {
            ConcurrentMapCacheFactoryBean cacheFactoryBean = new ConcurrentMapCacheFactoryBean();
            cacheFactoryBean.setName("first");
            return cacheFactoryBean;
        }

        @Bean
        public ConcurrentMapCacheFactoryBean cacheBeanSecond() {
            ConcurrentMapCacheFactoryBean cacheFactoryBean = new ConcurrentMapCacheFactoryBean();
            cacheFactoryBean.setName("second");
            return cacheFactoryBean;
        }
    }

    @Autowired
    CachingService cachingService;

    @Test
    public void givenFirstCache_whenSingleCacheValueEvictRequested_thenEmptyCacheValue() {
        cachingService.evictSingleCacheValue("first", "key1");
        String key1 = cachingService.getFromCache("first", "key1");
        Assert.assertThat(key1, CoreMatchers.is(CoreMatchers.nullValue()));
    }

    @Test
    public void givenFirstCache_whenAllCacheValueEvictRequested_thenEmptyCache() {
        cachingService.evictAllCacheValues("first");
        String key1 = cachingService.getFromCache("first", "key1");
        String key2 = cachingService.getFromCache("first", "key2");
        Assert.assertThat(key1, CoreMatchers.is(CoreMatchers.nullValue()));
        Assert.assertThat(key2, CoreMatchers.is(CoreMatchers.nullValue()));
    }

    @Test
    public void givenAllCaches_whenAllCacheEvictRequested_thenEmptyAllCaches() {
        cachingService.evictAllCaches();
        String key1 = cachingService.getFromCache("first", "key1");
        Assert.assertThat(key1, CoreMatchers.is(CoreMatchers.nullValue()));
        String key = cachingService.getFromCache("second", "key");
        Assert.assertThat(key, CoreMatchers.is(CoreMatchers.nullValue()));
    }
}

