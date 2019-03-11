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
package org.ehcache.integration;


import java.io.Serializable;
import org.ehcache.Cache;
import org.ehcache.CacheManager;
import org.ehcache.config.CacheConfiguration;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.ehcache.config.units.MemoryUnit;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;


/**
 *
 *
 * @author Abhilash
 */
public class OverSizeMappingTest {
    @Test
    public void testOverSizedObjectGetsReturnedFromLowerTier() {
        CacheManager cacheManager = CacheManagerBuilder.newCacheManagerBuilder().withDefaultSizeOfMaxObjectSize(500, MemoryUnit.B).withDefaultSizeOfMaxObjectGraph(1000).build(true);
        CacheConfiguration<String, String> objectSize = CacheConfigurationBuilder.newCacheConfigurationBuilder(String.class, String.class, ResourcePoolsBuilder.newResourcePoolsBuilder().heap(100, MemoryUnit.KB).offheap(10, MemoryUnit.MB).build()).build();
        Cache<String, String> objectSizeCache = cacheManager.createCache("objectSize", objectSize);
        objectSizeCache.put("key1", OverSizeMappingTest.getOverSizedObject());
        MatcherAssert.assertThat(objectSizeCache.get("key1"), Matchers.equalTo(OverSizeMappingTest.getOverSizedObject()));
        CacheConfiguration<String, OverSizeMappingTest.ObjectSizeGreaterThanN> objectGraphSize = CacheConfigurationBuilder.newCacheConfigurationBuilder(String.class, OverSizeMappingTest.ObjectSizeGreaterThanN.class, ResourcePoolsBuilder.newResourcePoolsBuilder().heap(100, MemoryUnit.KB).offheap(10, MemoryUnit.MB).build()).build();
        Cache<String, OverSizeMappingTest.ObjectSizeGreaterThanN> objectGraphSizeCache = cacheManager.createCache("objectGraphSize", objectGraphSize);
        objectGraphSizeCache.put("key1", OverSizeMappingTest.getObjectSizeGreaterThanN(1002));
        MatcherAssert.assertThat(objectGraphSizeCache.get("key1"), Matchers.equalTo(OverSizeMappingTest.getObjectSizeGreaterThanN(1002)));
    }

    @Test
    public void testOverSizedObjectPutFailsWithOnHeapAsAuthority() {
        CacheManager cacheManager = CacheManagerBuilder.newCacheManagerBuilder().withDefaultSizeOfMaxObjectSize(500, MemoryUnit.B).build(true);
        CacheConfiguration<String, String> cacheConfiguration = CacheConfigurationBuilder.newCacheConfigurationBuilder(String.class, String.class, ResourcePoolsBuilder.newResourcePoolsBuilder().heap(100, MemoryUnit.KB).build()).build();
        Cache<String, String> cache = cacheManager.createCache("cache", cacheConfiguration);
        cache.put("key1", OverSizeMappingTest.getOverSizedObject());
        MatcherAssert.assertThat(cache.get("key1"), Matchers.nullValue());
        cache.put("key1", "value1");
        cache.replace("key1", OverSizeMappingTest.getOverSizedObject());
        MatcherAssert.assertThat(cache.get("key1"), Matchers.nullValue());
    }

    private static class ObjectSizeGreaterThanN implements Serializable {
        private static final long serialVersionUID = 1L;

        private final Integer[] arr;

        private ObjectSizeGreaterThanN(int n) {
            arr = new Integer[n];
            for (int i = 0; i < (arr.length); i++) {
                arr[i] = new Integer(i);
            }
        }

        // just for this test
        @Override
        public boolean equals(Object obj) {
            if ((this) == obj) {
                return true;
            }
            return (obj instanceof OverSizeMappingTest.ObjectSizeGreaterThanN) && ((this.arr.length) == (((OverSizeMappingTest.ObjectSizeGreaterThanN) (obj)).arr.length));
        }

        @Override
        public int hashCode() {
            return arr.length;
        }
    }
}

