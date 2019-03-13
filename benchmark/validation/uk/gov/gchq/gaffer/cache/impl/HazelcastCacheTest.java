package uk.gov.gchq.gaffer.cache.impl;


import org.hamcrest.core.IsCollectionContaining;
import org.junit.Assert;
import org.junit.Test;
import uk.gov.gchq.gaffer.cache.exception.CacheOperationException;
import uk.gov.gchq.gaffer.commonutil.exception.OverwritingException;


public class HazelcastCacheTest {
    private static HazelcastCache<String, Integer> cache;

    @Test
    public void shouldThrowAnExceptionIfEntryAlreadyExistsWhenUsingPutSafe() {
        try {
            HazelcastCacheTest.cache.put("test", 1);
        } catch (final CacheOperationException e) {
            Assert.fail("Did not expect Exception to occur here");
        }
        try {
            HazelcastCacheTest.cache.putSafe("test", 1);
            Assert.fail();
        } catch (final OverwritingException e) {
            Assert.assertEquals("Cache entry already exists for key: test", e.getMessage());
        } catch (final CacheOperationException e) {
            Assert.fail("Should have thrown an OverwritingException");
        }
    }

    @Test
    public void shouldThrowExceptionWhenAddingNullKeyToCache() {
        try {
            HazelcastCacheTest.cache.put(null, 2);
            Assert.fail("Expected an exception");
        } catch (final CacheOperationException e) {
            Assert.assertNotNull(e.getMessage());
        }
    }

    @Test
    public void shouldThrowExceptionIfAddingNullValue() {
        try {
            HazelcastCacheTest.cache.put("test", null);
            Assert.fail("Expected an exception");
        } catch (final CacheOperationException e) {
            Assert.assertNotNull(e.getMessage());
        }
    }

    @Test
    public void shouldAddToCache() throws CacheOperationException {
        // when
        HazelcastCacheTest.cache.put("key", 1);
        // then
        Assert.assertEquals(1, HazelcastCacheTest.cache.size());
    }

    @Test
    public void shouldReadFromCache() throws CacheOperationException {
        // when
        HazelcastCacheTest.cache.put("key", 2);
        // then
        Assert.assertEquals(new Integer(2), HazelcastCacheTest.cache.get("key"));
    }

    @Test
    public void shouldDeleteCachedEntries() throws CacheOperationException {
        // given
        HazelcastCacheTest.cache.put("key", 3);
        // when
        HazelcastCacheTest.cache.remove("key");
        // then
        Assert.assertEquals(0, HazelcastCacheTest.cache.size());
    }

    @Test
    public void shouldUpdateCachedEntries() throws CacheOperationException {
        // given
        HazelcastCacheTest.cache.put("key", 4);
        // when
        HazelcastCacheTest.cache.put("key", 5);
        // then
        Assert.assertEquals(1, HazelcastCacheTest.cache.size());
        Assert.assertEquals(new Integer(5), HazelcastCacheTest.cache.get("key"));
    }

    @Test
    public void shouldRemoveAllEntries() throws CacheOperationException {
        // given
        HazelcastCacheTest.cache.put("key1", 1);
        HazelcastCacheTest.cache.put("key2", 2);
        HazelcastCacheTest.cache.put("key3", 3);
        // when
        HazelcastCacheTest.cache.clear();
        // then
        Assert.assertEquals(0, HazelcastCacheTest.cache.size());
    }

    @Test
    public void shouldGetAllKeys() throws CacheOperationException {
        HazelcastCacheTest.cache.put("test1", 1);
        HazelcastCacheTest.cache.put("test2", 2);
        HazelcastCacheTest.cache.put("test3", 3);
        Assert.assertEquals(3, HazelcastCacheTest.cache.size());
        Assert.assertThat(HazelcastCacheTest.cache.getAllKeys(), IsCollectionContaining.hasItems("test1", "test2", "test3"));
    }

    @Test
    public void shouldGetAllValues() throws CacheOperationException {
        HazelcastCacheTest.cache.put("test1", 1);
        HazelcastCacheTest.cache.put("test2", 2);
        HazelcastCacheTest.cache.put("test3", 3);
        HazelcastCacheTest.cache.put("duplicate", 3);
        Assert.assertEquals(4, HazelcastCacheTest.cache.size());
        Assert.assertEquals(4, HazelcastCacheTest.cache.getAllValues().size());
        Assert.assertThat(HazelcastCacheTest.cache.getAllValues(), IsCollectionContaining.hasItems(1, 2, 3));
    }
}

