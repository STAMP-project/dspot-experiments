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
package org.ehcache.impl.internal.persistence;


import java.io.File;
import org.ehcache.Cache;
import org.ehcache.PersistentCacheManager;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.ehcache.config.units.EntryUnit;
import org.ehcache.config.units.MemoryUnit;
import org.ehcache.impl.config.persistence.CacheManagerPersistenceConfiguration;
import org.ehcache.impl.internal.util.FileExistenceMatchers;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;


public class CacheManagerDestroyRemovesPersistenceTest {
    public static final String PERSISTENT_CACHE = "persistent-cache";

    @Rule
    public final TemporaryFolder diskPath = new TemporaryFolder();

    private PersistentCacheManager persistentCacheManager;

    @Test
    public void testDestroyRemovesPersistenceData() throws Exception {
        File file = new File(getStoragePath(), "myData");
        initCacheManager(file);
        putValuesInCacheAndCloseCacheManager();
        initCacheManager(file);
        persistentCacheManager.close();
        persistentCacheManager.destroy();
        MatcherAssert.assertThat(file, Matchers.not(FileExistenceMatchers.isLocked()));
    }

    @Test
    public void testDestroyCacheDestroysPersistenceContext() throws Exception {
        File file = new File(getStoragePath(), "testDestroy");
        initCacheManager(file);
        persistentCacheManager.destroyCache(CacheManagerDestroyRemovesPersistenceTest.PERSISTENT_CACHE);
        MatcherAssert.assertThat(file, Matchers.not(FileExistenceMatchers.containsCacheDirectory(CacheManagerDestroyRemovesPersistenceTest.PERSISTENT_CACHE)));
    }

    @Test
    public void testCreateCacheWithSameAliasAfterDestroy() throws Exception {
        File file = new File(getStoragePath(), "testDestroy");
        initCacheManager(file);
        persistentCacheManager.destroyCache(CacheManagerDestroyRemovesPersistenceTest.PERSISTENT_CACHE);
        persistentCacheManager.createCache(CacheManagerDestroyRemovesPersistenceTest.PERSISTENT_CACHE, CacheConfigurationBuilder.newCacheConfigurationBuilder(Long.class, String.class, ResourcePoolsBuilder.newResourcePoolsBuilder().heap(10, EntryUnit.ENTRIES).disk(10L, MemoryUnit.MB, true)).build());
        Assert.assertNotNull(persistentCacheManager.getCache(CacheManagerDestroyRemovesPersistenceTest.PERSISTENT_CACHE, Long.class, String.class));
        persistentCacheManager.close();
    }

    @Test
    public void testDestroyCacheWithUnknownAlias() throws Exception {
        File file = new File(getStoragePath(), "testDestroyUnknownAlias");
        initCacheManager(file);
        Cache<Long, String> cache = persistentCacheManager.getCache(CacheManagerDestroyRemovesPersistenceTest.PERSISTENT_CACHE, Long.class, String.class);
        cache.put(1L, "One");
        persistentCacheManager.close();
        PersistentCacheManager anotherPersistentCacheManager = CacheManagerBuilder.newCacheManagerBuilder().with(new CacheManagerPersistenceConfiguration(file)).build(true);
        anotherPersistentCacheManager.destroyCache(CacheManagerDestroyRemovesPersistenceTest.PERSISTENT_CACHE);
        MatcherAssert.assertThat(file, Matchers.not(FileExistenceMatchers.containsCacheDirectory(CacheManagerDestroyRemovesPersistenceTest.PERSISTENT_CACHE)));
    }
}

