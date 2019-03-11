package com.netflix.discovery.converters;


import com.netflix.discovery.util.StringCache;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Tomasz Bak
 */
public class StringCacheTest {
    public static final int CACHE_SIZE = 100000;

    @Test
    public void testVerifyStringsAreGarbageCollectedIfNotReferenced() throws Exception {
        StringCache cache = new StringCache();
        for (int i = 0; i < (StringCacheTest.CACHE_SIZE); i++) {
            cache.cachedValueOf(("id#" + i));
        }
        StringCacheTest.gc();
        // Testing GC behavior is unpredictable, so we set here low target level
        // The tests run on desktop show that all strings are removed actually.
        Assert.assertTrue(((cache.size()) < ((StringCacheTest.CACHE_SIZE) * 0.1)));
    }
}

