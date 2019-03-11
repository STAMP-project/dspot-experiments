package uk.gov.gchq.gaffer.cache.impl;


import org.hamcrest.core.IsCollectionContaining;
import org.junit.Assert;
import org.junit.Test;
import uk.gov.gchq.gaffer.cache.exception.CacheOperationException;
import uk.gov.gchq.gaffer.commonutil.exception.OverwritingException;


public class JcsCacheTest {
    private static JcsCache<String, Integer> cache;

    @Test
    public void shouldThrowAnExceptionIfEntryAlreadyExistsWhenUsingPutSafe() {
        try {
            JcsCacheTest.cache.put("test", 1);
            JcsCacheTest.cache.putSafe("test", 1);
            Assert.fail();
        } catch (OverwritingException | CacheOperationException e) {
            Assert.assertEquals("Cache entry already exists for key: test", getMessage());
        }
    }

    @Test
    public void shouldThrowExceptionWhenAddingNullKeyToCache() {
        try {
            JcsCacheTest.cache.put(null, 2);
            Assert.fail("Expected an exception");
        } catch (final CacheOperationException e) {
            Assert.assertNotNull(e.getMessage());
        }
    }

    @Test
    public void shouldThrowExceptionIfAddingNullValue() {
        try {
            JcsCacheTest.cache.put("test", null);
            Assert.fail("Expected an exception");
        } catch (final CacheOperationException e) {
            Assert.assertNotNull(e.getMessage());
        }
    }

    @Test
    public void shouldAddToCache() throws CacheOperationException {
        // when
        JcsCacheTest.cache.put("key", 1);
        // then
        Assert.assertEquals(1, JcsCacheTest.cache.size());
    }

    @Test
    public void shouldReadFromCache() throws CacheOperationException {
        // when
        JcsCacheTest.cache.put("key", 2);
        // then
        Assert.assertEquals(new Integer(2), JcsCacheTest.cache.get("key"));
    }

    @Test
    public void shouldDeleteCachedEntries() throws CacheOperationException {
        // given
        JcsCacheTest.cache.put("key", 3);
        // when
        JcsCacheTest.cache.remove("key");
        // then
        Assert.assertEquals(0, JcsCacheTest.cache.size());
    }

    @Test
    public void shouldUpdateCachedEntries() throws CacheOperationException {
        // given
        JcsCacheTest.cache.put("key", 4);
        // when
        JcsCacheTest.cache.put("key", 5);
        // then
        Assert.assertEquals(1, JcsCacheTest.cache.size());
        Assert.assertEquals(new Integer(5), JcsCacheTest.cache.get("key"));
    }

    @Test
    public void shouldRemoveAllEntries() throws CacheOperationException {
        // given
        JcsCacheTest.cache.put("key1", 1);
        JcsCacheTest.cache.put("key2", 2);
        JcsCacheTest.cache.put("key3", 3);
        // when
        JcsCacheTest.cache.clear();
        // then
        Assert.assertEquals(0, JcsCacheTest.cache.size());
    }

    @Test
    public void shouldGetAllKeys() throws CacheOperationException {
        JcsCacheTest.cache.put("test1", 1);
        JcsCacheTest.cache.put("test2", 2);
        JcsCacheTest.cache.put("test3", 3);
        Assert.assertEquals(3, JcsCacheTest.cache.size());
        Assert.assertThat(JcsCacheTest.cache.getAllKeys(), IsCollectionContaining.hasItems("test1", "test2", "test3"));
    }

    @Test
    public void shouldGetAllValues() throws CacheOperationException {
        JcsCacheTest.cache.put("test1", 1);
        JcsCacheTest.cache.put("test2", 2);
        JcsCacheTest.cache.put("test3", 3);
        JcsCacheTest.cache.put("duplicate", 3);
        Assert.assertEquals(4, JcsCacheTest.cache.size());
        Assert.assertEquals(4, JcsCacheTest.cache.getAllValues().size());
        Assert.assertThat(JcsCacheTest.cache.getAllValues(), IsCollectionContaining.hasItems(1, 2, 3));
    }
}

