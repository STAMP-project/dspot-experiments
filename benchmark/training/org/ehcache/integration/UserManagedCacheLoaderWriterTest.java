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


import org.ehcache.UserManagedCache;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.ehcache.config.builders.UserManagedCacheBuilder;
import org.ehcache.config.units.EntryUnit;
import org.ehcache.spi.loaderwriter.CacheLoaderWriter;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class UserManagedCacheLoaderWriterTest {
    @SuppressWarnings("unchecked")
    @Test
    public void testLoaderWriterWithUserManagedCache() throws Exception {
        CacheLoaderWriter<Long, Long> cacheLoaderWriter = Mockito.mock(CacheLoaderWriter.class);
        UserManagedCache<Long, Long> userManagedCache = UserManagedCacheBuilder.newUserManagedCacheBuilder(Long.class, Long.class).withResourcePools(ResourcePoolsBuilder.newResourcePoolsBuilder().heap(10, EntryUnit.ENTRIES)).withLoaderWriter(cacheLoaderWriter).build(true);
        userManagedCache.put(1L, 1L);
        Mockito.verify(cacheLoaderWriter, Mockito.times(1)).write(ArgumentMatchers.eq(1L), ArgumentMatchers.eq(1L));
        Mockito.when(cacheLoaderWriter.load(ArgumentMatchers.anyLong())).thenReturn(2L);
        Assert.assertThat(userManagedCache.get(2L), Matchers.is(2L));
    }
}

