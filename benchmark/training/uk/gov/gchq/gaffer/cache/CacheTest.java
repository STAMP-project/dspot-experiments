/**
 * Copyright 2018-2019 Crown Copyright
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
package uk.gov.gchq.gaffer.cache;


import java.util.Properties;
import org.junit.Assert;
import org.junit.Test;
import uk.gov.gchq.gaffer.cache.exception.CacheOperationException;


public class CacheTest {
    private static final String CACHE_SERVICE_CLASS_STRING = "uk.gov.gchq.gaffer.cache.impl.HashMapCacheService";

    private static Cache<Integer> cache;

    private static Properties properties = new Properties();

    @Test
    public void shouldAddAndGetCache() throws CacheOperationException {
        Integer expected = 1;
        CacheTest.cache.addToCache("key1", expected, true);
        Integer actual = CacheTest.cache.getFromCache("key1");
        Integer actual2 = CacheTest.cache.getFromCache("key2");
        Assert.assertEquals(expected, actual);
        Assert.assertNotEquals(expected, actual2);
        Assert.assertNull(actual2);
    }

    @Test
    public void shouldAddAndGetCacheOverwrite() throws CacheOperationException {
        Integer expected = 1;
        Integer before = 2;
        CacheTest.cache.addToCache("key1", before, true);
        CacheTest.cache.addToCache("key1", expected, true);
        Integer actual = CacheTest.cache.getFromCache("key1");
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void shouldAddAndGetCacheNoOverwrite() throws CacheOperationException {
        Integer expected = 1;
        Integer before = 2;
        CacheTest.cache.addToCache("key1", before, true);
        try {
            CacheTest.cache.addToCache("key1", expected, false);
            Assert.fail("exception expected");
        } catch (Exception e) {
            Assert.assertEquals("Cache entry already exists for key: key1", e.getMessage());
        }
        Integer actual = CacheTest.cache.getFromCache("key1");
        Assert.assertEquals(before, actual);
    }

    @Test
    public void shouldGetCacheServiceName() throws CacheOperationException {
        Assert.assertEquals("serviceName1", CacheTest.cache.getCacheName());
    }

    @Test
    public void shouldDelete() throws CacheOperationException {
        CacheTest.cache.addToCache("key1", 1, false);
        CacheTest.cache.deleteFromCache("key1");
        Integer actual = CacheTest.cache.getFromCache("key1");
        Assert.assertEquals(null, actual);
    }
}

