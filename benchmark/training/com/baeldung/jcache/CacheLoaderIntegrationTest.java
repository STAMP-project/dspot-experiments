package com.baeldung.jcache;


import javax.cache.Cache;
import org.junit.Assert;
import org.junit.Test;


public class CacheLoaderIntegrationTest {
    private static final String CACHE_NAME = "SimpleCache";

    private Cache<Integer, String> cache;

    @Test
    public void whenReadingFromStorage_thenCorrect() {
        for (int i = 1; i < 4; i++) {
            String value = cache.get(i);
            Assert.assertEquals(("fromCache" + i), value);
        }
    }
}

