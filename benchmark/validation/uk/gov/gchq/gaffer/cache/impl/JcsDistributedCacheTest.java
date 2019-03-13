package uk.gov.gchq.gaffer.cache.impl;


import java.util.Collection;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;
import uk.gov.gchq.gaffer.cache.exception.CacheOperationException;


public class JcsDistributedCacheTest {
    private static JcsCache<String, Integer> cache1;

    private static JcsCache<String, Integer> cache2;

    @Test
    public void shouldSendUpdatesToOtherNodes() throws CacheOperationException {
        // given cache is distributed
        // when
        JcsDistributedCacheTest.cache1.put("test", 1);
        // then
        Assert.assertEquals(new Integer(1), JcsDistributedCacheTest.cache2.get("test"));
    }

    @Test
    public void shouldBeAbleToRetrieveAllKeys() throws CacheOperationException {
        // given cache is distributed
        // when
        JcsDistributedCacheTest.cache1.put("test1", 1);
        JcsDistributedCacheTest.cache1.put("test2", 2);
        JcsDistributedCacheTest.cache2.put("test3", 3);
        // then
        Set<String> keys = JcsDistributedCacheTest.cache2.getAllKeys();
        Assert.assertEquals(3, keys.size());
        assert keys.contains("test1");
        assert keys.contains("test2");
        assert keys.contains("test3");
    }

    @Test
    public void shouldBeAbleToGetAllValues() throws CacheOperationException {
        // given cache is distributed
        // when
        JcsDistributedCacheTest.cache1.put("test4", 4);
        JcsDistributedCacheTest.cache1.put("test5", 5);
        JcsDistributedCacheTest.cache2.put("test6", 6);
        // then
        Collection<Integer> keys = JcsDistributedCacheTest.cache2.getAllValues();
        Assert.assertEquals(3, keys.size());
        assert keys.contains(4);
        assert keys.contains(5);
        assert keys.contains(6);
    }

    @Test
    public void shouldOverwriteEntriesRemotely() throws CacheOperationException {
        // given cache is distributed
        // when
        JcsDistributedCacheTest.cache1.put("test7", 7);
        JcsDistributedCacheTest.cache1.put("test7", 8);
        // then
        Assert.assertEquals(new Integer(8), JcsDistributedCacheTest.cache2.get("test7"));
        Assert.assertEquals(new Integer(8), JcsDistributedCacheTest.cache1.get("test7"));
        // when
        JcsDistributedCacheTest.cache2.put("test7", 7);
        // then
        Assert.assertEquals(new Integer(7), JcsDistributedCacheTest.cache2.get("test7"));
        Assert.assertEquals(new Integer(7), JcsDistributedCacheTest.cache1.get("test7"));
    }
}

