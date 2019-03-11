package com.baeldung.jcache;


import javax.cache.Cache;
import javax.cache.configuration.FactoryBuilder;
import javax.cache.configuration.MutableCacheEntryListenerConfiguration;
import org.junit.Assert;
import org.junit.Test;


public class EventListenerIntegrationTest {
    private static final String CACHE_NAME = "MyCache";

    private static final String CACHE_PROVIDER_NAME = "com.hazelcast.cache.HazelcastCachingProvider";

    private Cache<String, String> cache;

    private SimpleCacheEntryListener listener;

    private MutableCacheEntryListenerConfiguration<String, String> listenerConfiguration;

    @Test
    public void whenRunEvent_thenCorrect() throws InterruptedException {
        this.listenerConfiguration = new MutableCacheEntryListenerConfiguration(FactoryBuilder.factoryOf(this.listener), null, false, true);
        this.cache.registerCacheEntryListener(this.listenerConfiguration);
        Assert.assertEquals(false, this.listener.getCreated());
        this.cache.put("key", "value");
        Assert.assertEquals(true, this.listener.getCreated());
        Assert.assertEquals(false, this.listener.getUpdated());
        this.cache.put("key", "newValue");
        Assert.assertEquals(true, this.listener.getUpdated());
    }
}

