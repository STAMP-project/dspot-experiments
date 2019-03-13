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
package org.ehcache.config.builders;


import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import org.ehcache.CachePersistenceException;
import org.ehcache.PersistentCacheManager;
import org.ehcache.impl.internal.util.FileExistenceMatchers;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TemporaryFolder;


/**
 *
 *
 * @author Alex Snaps
 */
public class PersistentCacheManagerTest {
    private static final String TEST_CACHE_ALIAS = "test123";

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Rule
    public final TemporaryFolder folder = new TemporaryFolder();

    private File rootDirectory;

    private CacheManagerBuilder<PersistentCacheManager> builder;

    @Test
    public void testInitializesLocalPersistenceService() throws IOException {
        builder.build(true);
        Assert.assertTrue(rootDirectory.isDirectory());
        Assert.assertThat(Arrays.asList(rootDirectory.list()), Matchers.contains(".lock"));
    }

    @Test
    public void testInitializesLocalPersistenceServiceAndCreateCache() throws IOException {
        buildCacheManagerWithCache(true);
        Assert.assertThat(rootDirectory, FileExistenceMatchers.isLocked());
        Assert.assertThat(rootDirectory, FileExistenceMatchers.containsCacheDirectory(PersistentCacheManagerTest.TEST_CACHE_ALIAS));
    }

    @Test
    public void testDestroyCache_NullAliasNotAllowed() throws CachePersistenceException {
        PersistentCacheManager manager = builder.build(true);
        thrown.expect(NullPointerException.class);
        thrown.expectMessage("Alias cannot be null");
        manager.destroyCache(null);
    }

    @Test
    public void testDestroyCache_UnexistingCacheDoesNothing() throws CachePersistenceException {
        PersistentCacheManager manager = builder.build(true);
        manager.destroyCache(PersistentCacheManagerTest.TEST_CACHE_ALIAS);
    }

    @Test
    public void testDestroyCache_Initialized_DestroyExistingCache() throws CachePersistenceException {
        PersistentCacheManager manager = buildCacheManagerWithCache(true);
        manager.destroyCache(PersistentCacheManagerTest.TEST_CACHE_ALIAS);
        Assert.assertThat(rootDirectory, FileExistenceMatchers.isLocked());
        Assert.assertThat(rootDirectory, Matchers.not(FileExistenceMatchers.containsCacheDirectory(PersistentCacheManagerTest.TEST_CACHE_ALIAS)));
    }

    @Test
    public void testDestroyCache_Uninitialized_DestroyExistingCache() throws CachePersistenceException {
        PersistentCacheManager manager = buildCacheManagerWithCache(true);
        manager.close();
        manager.destroyCache(PersistentCacheManagerTest.TEST_CACHE_ALIAS);
        Assert.assertThat(rootDirectory, Matchers.not(FileExistenceMatchers.isLocked()));
        Assert.assertThat(rootDirectory, Matchers.not(FileExistenceMatchers.containsCacheDirectory(PersistentCacheManagerTest.TEST_CACHE_ALIAS)));
    }

    @Test
    public void testDestroyCache_CacheManagerUninitialized() throws CachePersistenceException {
        PersistentCacheManager manager = buildCacheManagerWithCache(false);
        manager.destroyCache(PersistentCacheManagerTest.TEST_CACHE_ALIAS);
        Assert.assertThat(rootDirectory, Matchers.not(FileExistenceMatchers.isLocked()));
        Assert.assertThat(rootDirectory, Matchers.not(FileExistenceMatchers.containsCacheDirectory(PersistentCacheManagerTest.TEST_CACHE_ALIAS)));
    }

    @Test
    public void testClose_DiskCacheLockReleased() throws CachePersistenceException {
        PersistentCacheManager manager = buildCacheManagerWithCache(true);
        // Should lock the file when the CacheManager is opened
        Assert.assertThat(rootDirectory, FileExistenceMatchers.isLocked());
        manager.close();// pass it to uninitialized

        // Should unlock the file when the CacheManager is closed
        Assert.assertThat(rootDirectory, Matchers.not(FileExistenceMatchers.isLocked()));
    }

    @Test
    public void testCloseAndThenOpenOnTheSameFile() throws CachePersistenceException {
        // Open a CacheManager that will create a cache, close it and put it out of scope
        {
            PersistentCacheManager manager = buildCacheManagerWithCache(true);
            manager.close();
        }
        // Create a new CacheManager that will have the same cache. The cache should be there but the cache manager unlocked since the CacheManager isn't started
        {
            PersistentCacheManager manager = builder.build(false);
            Assert.assertThat(rootDirectory, Matchers.not(FileExistenceMatchers.isLocked()));
            Assert.assertThat(rootDirectory, FileExistenceMatchers.containsCacheDirectory(PersistentCacheManagerTest.TEST_CACHE_ALIAS));
        }
    }

    public static class A {
        public A() throws IOException {
            throw new IOException("..");
        }
    }
}

