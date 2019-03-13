/**
 * Copyright 2016-2019 Crown Copyright
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package uk.gov.gchq.gaffer.cache.impl;


import org.hamcrest.core.IsCollectionContaining;
import org.junit.Assert;
import org.junit.Test;
import uk.gov.gchq.gaffer.cache.ICache;
import uk.gov.gchq.gaffer.cache.exception.CacheOperationException;
import uk.gov.gchq.gaffer.commonutil.exception.OverwritingException;


public class HashMapCacheServiceTest {
    private HashMapCacheService service = new HashMapCacheService();

    private static final String CACHE_NAME = "test";

    @Test
    public void shouldReturnInstanceOfHashMapCache() {
        // when
        ICache cache = service.getCache(HashMapCacheServiceTest.CACHE_NAME);
        // then
        assert cache instanceof HashMapCache;
    }

    @Test
    public void shouldCreateNewHashMapCacheIfOneDoesNotExist() {
        // when
        ICache cache = service.getCache(HashMapCacheServiceTest.CACHE_NAME);
        // then
        Assert.assertEquals(0, cache.size());
    }

    @Test
    public void shouldReUseCacheIfOneExists() throws CacheOperationException {
        // given
        ICache<String, Integer> cache = service.getCache(HashMapCacheServiceTest.CACHE_NAME);
        cache.put("key", 1);
        // when
        ICache<String, Integer> sameCache = service.getCache(HashMapCacheServiceTest.CACHE_NAME);
        // then
        Assert.assertEquals(1, sameCache.size());
        Assert.assertEquals(new Integer(1), sameCache.get("key"));
    }

    @Test
    public void shouldAddEntriesToCache() throws CacheOperationException {
        service.putInCache(HashMapCacheServiceTest.CACHE_NAME, "test", 1);
        Assert.assertEquals(((Integer) (1)), service.getFromCache(HashMapCacheServiceTest.CACHE_NAME, "test"));
    }

    @Test
    public void shouldOnlyUpdateIfInstructed() throws CacheOperationException {
        service.putInCache(HashMapCacheServiceTest.CACHE_NAME, "test", 1);
        try {
            service.putSafeInCache(HashMapCacheServiceTest.CACHE_NAME, "test", 2);
            Assert.fail("Expected an exception");
        } catch (final OverwritingException e) {
            Assert.assertEquals(((Integer) (1)), service.getFromCache(HashMapCacheServiceTest.CACHE_NAME, "test"));
        }
        service.putInCache(HashMapCacheServiceTest.CACHE_NAME, "test", 2);
        Assert.assertEquals(((Integer) (2)), service.getFromCache(HashMapCacheServiceTest.CACHE_NAME, "test"));
    }

    @Test
    public void shouldBeAbleToDeleteCacheEntries() throws CacheOperationException {
        service.putInCache(HashMapCacheServiceTest.CACHE_NAME, "test", 1);
        service.removeFromCache(HashMapCacheServiceTest.CACHE_NAME, "test");
        Assert.assertEquals(0, service.sizeOfCache(HashMapCacheServiceTest.CACHE_NAME));
    }

    @Test
    public void shouldBeAbleToClearCache() throws CacheOperationException {
        service.putInCache(HashMapCacheServiceTest.CACHE_NAME, "test1", 1);
        service.putInCache(HashMapCacheServiceTest.CACHE_NAME, "test2", 2);
        service.putInCache(HashMapCacheServiceTest.CACHE_NAME, "test3", 3);
        service.clearCache(HashMapCacheServiceTest.CACHE_NAME);
        Assert.assertEquals(0, service.sizeOfCache(HashMapCacheServiceTest.CACHE_NAME));
    }

    @Test
    public void shouldGetAllKeysFromCache() throws CacheOperationException {
        service.putInCache(HashMapCacheServiceTest.CACHE_NAME, "test1", 1);
        service.putInCache(HashMapCacheServiceTest.CACHE_NAME, "test2", 2);
        service.putInCache(HashMapCacheServiceTest.CACHE_NAME, "test3", 3);
        Assert.assertEquals(3, service.sizeOfCache(HashMapCacheServiceTest.CACHE_NAME));
        Assert.assertThat(service.getAllKeysFromCache(HashMapCacheServiceTest.CACHE_NAME), IsCollectionContaining.hasItems("test1", "test2", "test3"));
    }

    @Test
    public void shouldGetAllValues() throws CacheOperationException {
        service.putInCache(HashMapCacheServiceTest.CACHE_NAME, "test1", 1);
        service.putInCache(HashMapCacheServiceTest.CACHE_NAME, "test2", 2);
        service.putInCache(HashMapCacheServiceTest.CACHE_NAME, "test3", 3);
        service.putInCache(HashMapCacheServiceTest.CACHE_NAME, "duplicate", 3);
        Assert.assertEquals(4, service.sizeOfCache(HashMapCacheServiceTest.CACHE_NAME));
        Assert.assertEquals(4, service.getAllValuesFromCache(HashMapCacheServiceTest.CACHE_NAME).size());
        Assert.assertThat(service.getAllValuesFromCache(HashMapCacheServiceTest.CACHE_NAME), IsCollectionContaining.hasItems(1, 2, 3));
    }
}

