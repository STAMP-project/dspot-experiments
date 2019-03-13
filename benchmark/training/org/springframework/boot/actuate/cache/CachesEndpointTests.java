/**
 * Copyright 2012-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.boot.actuate.cache;


import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.boot.actuate.cache.CachesEndpoint.CacheEntry;
import org.springframework.boot.actuate.cache.CachesEndpoint.CacheManagerDescriptor;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;


/**
 * Tests for {@link CachesEndpoint}.
 *
 * @author Stephane Nicoll
 */
public class CachesEndpointTests {
    @Test
    public void allCachesWithSingleCacheManager() {
        CachesEndpoint endpoint = new CachesEndpoint(Collections.singletonMap("test", new ConcurrentMapCacheManager("a", "b")));
        Map<String, CacheManagerDescriptor> allDescriptors = endpoint.caches().getCacheManagers();
        assertThat(allDescriptors).containsOnlyKeys("test");
        CacheManagerDescriptor descriptors = allDescriptors.get("test");
        assertThat(descriptors.getCaches()).containsOnlyKeys("a", "b");
        assertThat(descriptors.getCaches().get("a").getTarget()).isEqualTo(ConcurrentHashMap.class.getName());
        assertThat(descriptors.getCaches().get("b").getTarget()).isEqualTo(ConcurrentHashMap.class.getName());
    }

    @Test
    public void allCachesWithSeveralCacheManagers() {
        Map<String, CacheManager> cacheManagers = new LinkedHashMap<>();
        cacheManagers.put("test", new ConcurrentMapCacheManager("a", "b"));
        cacheManagers.put("another", new ConcurrentMapCacheManager("a", "c"));
        CachesEndpoint endpoint = new CachesEndpoint(cacheManagers);
        Map<String, CacheManagerDescriptor> allDescriptors = endpoint.caches().getCacheManagers();
        assertThat(allDescriptors).containsOnlyKeys("test", "another");
        assertThat(allDescriptors.get("test").getCaches()).containsOnlyKeys("a", "b");
        assertThat(allDescriptors.get("another").getCaches()).containsOnlyKeys("a", "c");
    }

    @Test
    public void namedCacheWithSingleCacheManager() {
        CachesEndpoint endpoint = new CachesEndpoint(Collections.singletonMap("test", new ConcurrentMapCacheManager("b", "a")));
        CacheEntry entry = endpoint.cache("a", null);
        assertThat(entry).isNotNull();
        assertThat(entry.getCacheManager()).isEqualTo("test");
        assertThat(entry.getName()).isEqualTo("a");
        assertThat(entry.getTarget()).isEqualTo(ConcurrentHashMap.class.getName());
    }

    @Test
    public void namedCacheWithSeveralCacheManagers() {
        Map<String, CacheManager> cacheManagers = new LinkedHashMap<>();
        cacheManagers.put("test", new ConcurrentMapCacheManager("b", "dupe-cache"));
        cacheManagers.put("another", new ConcurrentMapCacheManager("c", "dupe-cache"));
        CachesEndpoint endpoint = new CachesEndpoint(cacheManagers);
        assertThatExceptionOfType(NonUniqueCacheException.class).isThrownBy(() -> endpoint.cache("dupe-cache", null)).withMessageContaining("dupe-cache").withMessageContaining("test").withMessageContaining("another");
    }

    @Test
    public void namedCacheWithUnknownCache() {
        CachesEndpoint endpoint = new CachesEndpoint(Collections.singletonMap("test", new ConcurrentMapCacheManager("b", "a")));
        CacheEntry entry = endpoint.cache("unknown", null);
        assertThat(entry).isNull();
    }

    @Test
    public void namedCacheWithWrongCacheManager() {
        Map<String, CacheManager> cacheManagers = new LinkedHashMap<>();
        cacheManagers.put("test", new ConcurrentMapCacheManager("b", "a"));
        cacheManagers.put("another", new ConcurrentMapCacheManager("c", "a"));
        CachesEndpoint endpoint = new CachesEndpoint(cacheManagers);
        CacheEntry entry = endpoint.cache("c", "test");
        assertThat(entry).isNull();
    }

    @Test
    public void namedCacheWithSeveralCacheManagersWithCacheManagerFilter() {
        Map<String, CacheManager> cacheManagers = new LinkedHashMap<>();
        cacheManagers.put("test", new ConcurrentMapCacheManager("b", "a"));
        cacheManagers.put("another", new ConcurrentMapCacheManager("c", "a"));
        CachesEndpoint endpoint = new CachesEndpoint(cacheManagers);
        CacheEntry entry = endpoint.cache("a", "test");
        assertThat(entry).isNotNull();
        assertThat(entry.getCacheManager()).isEqualTo("test");
        assertThat(entry.getName()).isEqualTo("a");
    }

    @Test
    public void clearAllCaches() {
        Cache a = mockCache("a");
        Cache b = mockCache("b");
        CachesEndpoint endpoint = new CachesEndpoint(Collections.singletonMap("test", cacheManager(a, b)));
        endpoint.clearCaches();
        Mockito.verify(a).clear();
        Mockito.verify(b).clear();
    }

    @Test
    public void clearCache() {
        Cache a = mockCache("a");
        Cache b = mockCache("b");
        CachesEndpoint endpoint = new CachesEndpoint(Collections.singletonMap("test", cacheManager(a, b)));
        assertThat(endpoint.clearCache("a", null)).isTrue();
        Mockito.verify(a).clear();
        Mockito.verify(b, Mockito.never()).clear();
    }

    @Test
    public void clearCacheWithSeveralCacheManagers() {
        Map<String, CacheManager> cacheManagers = new LinkedHashMap<>();
        cacheManagers.put("test", cacheManager(mockCache("dupe-cache"), mockCache("b")));
        cacheManagers.put("another", cacheManager(mockCache("dupe-cache")));
        CachesEndpoint endpoint = new CachesEndpoint(cacheManagers);
        assertThatExceptionOfType(NonUniqueCacheException.class).isThrownBy(() -> endpoint.clearCache("dupe-cache", null)).withMessageContaining("dupe-cache").withMessageContaining("test").withMessageContaining("another");
    }

    @Test
    public void clearCacheWithSeveralCacheManagersWithCacheManagerFilter() {
        Map<String, CacheManager> cacheManagers = new LinkedHashMap<>();
        Cache a = mockCache("a");
        Cache b = mockCache("b");
        cacheManagers.put("test", cacheManager(a, b));
        Cache anotherA = mockCache("a");
        cacheManagers.put("another", cacheManager(anotherA));
        CachesEndpoint endpoint = new CachesEndpoint(cacheManagers);
        assertThat(endpoint.clearCache("a", "another")).isTrue();
        Mockito.verify(a, Mockito.never()).clear();
        Mockito.verify(anotherA).clear();
        Mockito.verify(b, Mockito.never()).clear();
    }

    @Test
    public void clearCacheWithUnknownCache() {
        Cache a = mockCache("a");
        CachesEndpoint endpoint = new CachesEndpoint(Collections.singletonMap("test", cacheManager(a)));
        assertThat(endpoint.clearCache("unknown", null)).isFalse();
        Mockito.verify(a, Mockito.never()).clear();
    }

    @Test
    public void clearCacheWithUnknownCacheManager() {
        Cache a = mockCache("a");
        CachesEndpoint endpoint = new CachesEndpoint(Collections.singletonMap("test", cacheManager(a)));
        assertThat(endpoint.clearCache("a", "unknown")).isFalse();
        Mockito.verify(a, Mockito.never()).clear();
    }
}

