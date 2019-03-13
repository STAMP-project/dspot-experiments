/**
 * Copyright Terracotta, Inc.
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
package org.ehcache.jsr107;


import Cache.Entry;
import java.util.Collections;
import java.util.Set;
import javax.cache.Cache;
import javax.cache.configuration.MutableConfiguration;
import javax.cache.integration.CacheLoader;
import javax.cache.integration.CacheWriter;
import javax.cache.spi.CachingProvider;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;


/**
 * LoaderWriterConfigTest
 */
public class LoaderWriterConfigTest {
    @Mock
    private CacheLoader<Long, String> cacheLoader;

    @Mock
    private CacheWriter<Long, String> cacheWriter;

    private CachingProvider cachingProvider;

    @Test
    @SuppressWarnings("unchecked")
    public void enablingWriteThroughDoesNotForceReadThrough() throws Exception {
        MutableConfiguration<Long, String> config = getConfiguration(false, cacheLoader, true, cacheWriter);
        Cache<Long, String> cache = cachingProvider.getCacheManager().createCache("writingCache", config);
        cache.put(42L, "Tadam!!!");
        Set<Long> keys = Collections.singleton(25L);
        cache.loadAll(keys, false, null);
        cache.get(100L);
        Mockito.verify(cacheLoader).loadAll(keys);
        Mockito.verifyNoMoreInteractions(cacheLoader);
        Mockito.verify(cacheWriter).write(ArgumentMatchers.any(Entry.class));
    }

    @Test
    public void enablingReadThroughDoesNotForceWriteThrough() throws Exception {
        MutableConfiguration<Long, String> config = getConfiguration(true, cacheLoader, false, cacheWriter);
        Cache<Long, String> cache = cachingProvider.getCacheManager().createCache("writingCache", config);
        cache.put(42L, "Tadam!!!");
        cache.get(100L);
        Mockito.verifyZeroInteractions(cacheWriter);
        Mockito.verify(cacheLoader).load(100L);
    }
}

