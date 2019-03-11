package com.baeldung.jcache;


import javax.cache.Cache;
import javax.cache.CacheManager;
import javax.cache.Caching;
import javax.cache.configuration.MutableConfiguration;
import javax.cache.spi.CachingProvider;
import org.junit.Assert;
import org.junit.Test;


public class JCacheIntegrationTest {
    @Test
    public void instantiateCache() {
        CachingProvider cachingProvider = Caching.getCachingProvider("com.hazelcast.cache.HazelcastCachingProvider");
        CacheManager cacheManager = cachingProvider.getCacheManager();
        MutableConfiguration<String, String> config = new MutableConfiguration();
        Cache<String, String> cache = cacheManager.createCache("simpleCache", config);
        cache.put("key1", "value1");
        cache.put("key2", "value2");
        Assert.assertEquals("value1", cache.get("key1"));
        Assert.assertEquals("value2", cache.get("key2"));
        cacheManager.close();
    }
}

