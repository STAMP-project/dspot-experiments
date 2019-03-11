package com.baeldung.jcache;


import javax.cache.Cache;
import org.junit.Assert;
import org.junit.Test;


public class EntryProcessorIntegrationTest {
    private static final String CACHE_NAME = "MyCache";

    private static final String CACHE_PROVIDER_NAME = "com.hazelcast.cache.HazelcastCachingProvider";

    private Cache<String, String> cache;

    @Test
    public void whenModifyValue_thenCorrect() {
        this.cache.invoke("key", new SimpleEntryProcessor());
        Assert.assertEquals("value - modified", cache.get("key"));
    }
}

