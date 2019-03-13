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


import java.io.File;
import java.io.Serializable;
import java.nio.channels.OverlappingFileLockException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import junit.framework.TestCase;
import org.ehcache.PersistentCacheManager;
import org.ehcache.StateTransitionException;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.ehcache.config.units.MemoryUnit;
import org.ehcache.impl.config.persistence.CacheManagerPersistenceConfiguration;
import org.ehcache.integration.util.JavaExec;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.hamcrest.core.Is;
import org.hamcrest.core.StringContains;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.rules.TestName;


public class PersistentCacheTest {
    @ClassRule
    public static final TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Rule
    public TestName testName = new TestName();

    @Test
    public void testRecoverPersistentCacheFailsWhenConfiguringIncompatibleClass() throws Exception {
        File folder = PersistentCacheTest.temporaryFolder.newFolder(testName.getMethodName());
        {
            PersistentCacheManager cacheManager = CacheManagerBuilder.newCacheManagerBuilder().with(new CacheManagerPersistenceConfiguration(folder)).withCache("persistentCache", CacheConfigurationBuilder.newCacheConfigurationBuilder(Long.class, String.class, ResourcePoolsBuilder.newResourcePoolsBuilder().heap(1, MemoryUnit.MB).offheap(2, MemoryUnit.MB).disk(5, MemoryUnit.MB, true))).build(true);
            cacheManager.close();
        }
        {
            PersistentCacheManager cacheManager = CacheManagerBuilder.newCacheManagerBuilder().with(new CacheManagerPersistenceConfiguration(folder)).withCache("persistentCache", CacheConfigurationBuilder.newCacheConfigurationBuilder(Long.class, Serializable.class, ResourcePoolsBuilder.newResourcePoolsBuilder().heap(1, MemoryUnit.MB).offheap(2, MemoryUnit.MB).disk(5, MemoryUnit.MB, true))).build();
            try {
                cacheManager.init();
                TestCase.fail("expected StateTransitionException");
            } catch (StateTransitionException ste) {
                Throwable rootCause = findRootCause(ste);
                MatcherAssert.assertThat(rootCause, Matchers.instanceOf(IllegalArgumentException.class));
                MatcherAssert.assertThat(rootCause.getMessage(), Matchers.equalTo("Persisted value type 'java.lang.String' is not the same as the configured value type 'java.io.Serializable'"));
            }
        }
    }

    @Test
    public void testRecoverPersistentCacheSucceedsWhenConfiguringArrayClass() throws Exception {
        File folder = PersistentCacheTest.temporaryFolder.newFolder(testName.getMethodName());
        {
            PersistentCacheManager cacheManager = CacheManagerBuilder.newCacheManagerBuilder().with(new CacheManagerPersistenceConfiguration(folder)).withCache("persistentCache", CacheConfigurationBuilder.newCacheConfigurationBuilder(Long.class, byte[].class, ResourcePoolsBuilder.newResourcePoolsBuilder().heap(1, MemoryUnit.MB).offheap(2, MemoryUnit.MB).disk(5, MemoryUnit.MB, true))).build(true);
            cacheManager.close();
        }
        {
            PersistentCacheManager cacheManager = CacheManagerBuilder.newCacheManagerBuilder().with(new CacheManagerPersistenceConfiguration(folder)).withCache("persistentCache", CacheConfigurationBuilder.newCacheConfigurationBuilder(Long.class, byte[].class, ResourcePoolsBuilder.newResourcePoolsBuilder().heap(1, MemoryUnit.MB).offheap(2, MemoryUnit.MB).disk(5, MemoryUnit.MB, true))).build(true);
            cacheManager.close();
        }
    }

    @Test
    @SuppressWarnings("try")
    public void testPersistentCachesColliding() throws Exception {
        File folder = PersistentCacheTest.temporaryFolder.newFolder(testName.getMethodName());
        try (PersistentCacheManager cm = CacheManagerBuilder.newCacheManagerBuilder().with(new CacheManagerPersistenceConfiguration(folder)).build(true)) {
            CacheManagerBuilder.newCacheManagerBuilder().with(new CacheManagerPersistenceConfiguration(folder)).build(true).close();
            Assert.fail("Expected StateTransitionException");
        } catch (StateTransitionException e) {
            MatcherAssert.assertThat(e.getCause().getMessage(), StringContains.containsString("Persistence directory already locked by this process"));
            MatcherAssert.assertThat(e.getCause().getCause(), Matchers.instanceOf(OverlappingFileLockException.class));
        }
    }

    @Test
    public void testPersistentCachesCollidingCrossProcess() throws Exception {
        File folder = PersistentCacheTest.temporaryFolder.newFolder(testName.getMethodName());
        File ping = new File(folder, "ping");
        File pong = new File(folder, "pong");
        Future<Integer> external = JavaExec.exec(PersistentCacheTest.Locker.class, folder.getAbsolutePath());
        while (!(ping.exists()));
        try {
            CacheManagerBuilder.newCacheManagerBuilder().with(new CacheManagerPersistenceConfiguration(folder)).build(true).close();
            Assert.fail("Expected StateTransitionException");
        } catch (StateTransitionException e) {
            MatcherAssert.assertThat(e.getCause().getMessage(), StringContains.containsString("Persistence directory already locked by another process"));
        } finally {
            pong.createNewFile();
            MatcherAssert.assertThat(external.get(), Is.is(0));
        }
    }

    public static final class Locker {
        @SuppressWarnings("try")
        public static void main(String[] args) throws Exception {
            File folder = new File(args[0]);
            File ping = new File(folder, "ping");
            File pong = new File(folder, "pong");
            try (PersistentCacheManager cm = CacheManagerBuilder.newCacheManagerBuilder().with(new CacheManagerPersistenceConfiguration(folder)).build(true)) {
                ping.createNewFile();
                long bailout = (System.nanoTime()) + (TimeUnit.SECONDS.toNanos(30));
                while (((System.nanoTime()) < bailout) && (!(pong.exists())));
            }
        }
    }
}

