/**
 * Copyright (c) 2008-2019, Hazelcast, Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.hazelcast.cache.entryprocessor;


import com.hazelcast.cache.BackupAwareEntryProcessor;
import com.hazelcast.cache.ICache;
import com.hazelcast.cache.impl.CacheService;
import com.hazelcast.cache.impl.HazelcastServerCachingProvider;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.spi.serialization.SerializationService;
import com.hazelcast.test.HazelcastSerialClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.TestHazelcastInstanceFactory;
import com.hazelcast.test.annotation.QuickTest;
import java.io.Serializable;
import javax.cache.Cache;
import javax.cache.CacheManager;
import javax.cache.configuration.CompleteConfiguration;
import javax.cache.processor.EntryProcessor;
import javax.cache.processor.EntryProcessorException;
import javax.cache.processor.MutableEntry;
import javax.cache.spi.CachingProvider;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastSerialClassRunner.class)
@Category(QuickTest.class)
public class CacheEntryProcessorTest extends HazelcastTestSupport {
    private static final int ASSERTION_TIMEOUT_SECONDS = 300;

    private static TestHazelcastInstanceFactory factory;

    private static HazelcastInstance node1;

    private static HazelcastInstance node2;

    private static CacheService cacheServiceOnNode1;

    private static CacheService cacheServiceOnNode2;

    private static SerializationService serializationService;

    /**
     * If there is not any implemented backup entry processor,
     * we are only sending the result of execution to the backup node.
     */
    @Test
    public void whenBackupEntryProcessor_isNotImplemented() throws Exception {
        EntryProcessor<Integer, String, Void> entryProcessor = new CacheEntryProcessorTest.SimpleEntryProcessor();
        executeTestInternal(entryProcessor);
    }

    @Test
    public void whenBackupEntryProcessor_isImplemented() throws Exception {
        EntryProcessor<Integer, String, Void> entryProcessor = new CacheEntryProcessorTest.CustomBackupAwareEntryProcessor();
        executeTestInternal(entryProcessor);
    }

    @Test
    public void whenBackupEntryProcessor_isSame_withPrimaryEntryProcessor() throws Exception {
        EntryProcessor<Integer, String, Void> entryProcessor = new CacheEntryProcessorTest.SimpleBackupAwareEntryProcessor();
        executeTestInternal(entryProcessor);
    }

    @Test
    public void whenBackupEntryProcessor_isNull() throws Exception {
        EntryProcessor<Integer, String, Void> entryProcessor = new CacheEntryProcessorTest.NullBackupAwareEntryProcessor();
        executeTestInternal(entryProcessor);
    }

    @Test
    public void removeRecordWithEntryProcessor() {
        final int ENTRY_COUNT = 10;
        CachingProvider cachingProvider = HazelcastServerCachingProvider.createCachingProvider(CacheEntryProcessorTest.node1);
        CacheManager cacheManager = cachingProvider.getCacheManager();
        CompleteConfiguration<Integer, String> cacheConfig = new javax.cache.configuration.MutableConfiguration<Integer, String>().setTypes(Integer.class, String.class);
        ICache<Integer, String> cache = cacheManager.createCache("MyCache", cacheConfig).unwrap(ICache.class);
        for (int i = 0; i < ENTRY_COUNT; i++) {
            cache.put((i * 1000), ("Value-" + (i * 1000)));
        }
        Assert.assertEquals(ENTRY_COUNT, cache.size());
        for (int i = 0; i < ENTRY_COUNT; i++) {
            if ((i % 2) == 0) {
                cache.invoke((i * 1000), new CacheEntryProcessorTest.RemoveRecordEntryProcessor());
            }
        }
        Assert.assertEquals((ENTRY_COUNT / 2), cache.size());
    }

    @Test
    public void givenEntryExists_whenCallsGetValue_thenSubsequentExistsStillReturnTrue() {
        String cacheName = HazelcastTestSupport.randomName();
        int key = 1;
        CachingProvider cachingProvider = HazelcastServerCachingProvider.createCachingProvider(CacheEntryProcessorTest.node1);
        CacheManager cacheManager = cachingProvider.getCacheManager();
        CompleteConfiguration<Integer, String> config = new javax.cache.configuration.MutableConfiguration<Integer, String>().setTypes(Integer.class, String.class);
        Cache<Integer, String> cache = cacheManager.createCache(cacheName, config);
        cache.put(key, "foo");
        boolean result = cache.invoke(key, new CacheEntryProcessorTest.GetAndCheckEntry());
        Assert.assertTrue(result);
    }

    public static class SimpleEntryProcessor implements Serializable , EntryProcessor<Integer, String, Void> {
        private static final long serialVersionUID = -396575576353368113L;

        @Override
        public Void process(MutableEntry<Integer, String> entry, Object... arguments) throws EntryProcessorException {
            entry.setValue("Foo");
            return null;
        }
    }

    public static class SimpleBackupAwareEntryProcessor implements BackupAwareEntryProcessor<Integer, String, Void> , Serializable {
        private static final long serialVersionUID = -5274605583423489718L;

        @Override
        public Void process(MutableEntry<Integer, String> entry, Object... arguments) throws EntryProcessorException {
            entry.setValue("Foo");
            return null;
        }

        @Override
        public EntryProcessor<Integer, String, Void> createBackupEntryProcessor() {
            return this;
        }
    }

    public static class NullBackupAwareEntryProcessor implements BackupAwareEntryProcessor<Integer, String, Void> , Serializable {
        private static final long serialVersionUID = -8423196656316041614L;

        @Override
        public Void process(MutableEntry<Integer, String> entry, Object... arguments) throws EntryProcessorException {
            entry.setValue("Foo");
            return null;
        }

        @Override
        public EntryProcessor<Integer, String, Void> createBackupEntryProcessor() {
            return null;
        }
    }

    public static class CustomBackupAwareEntryProcessor implements BackupAwareEntryProcessor<Integer, String, Void> , Serializable {
        private static final long serialVersionUID = 3409663318028125754L;

        @Override
        public Void process(MutableEntry<Integer, String> entry, Object... arguments) throws EntryProcessorException {
            entry.setValue("Foo");
            return null;
        }

        @Override
        public EntryProcessor<Integer, String, Void> createBackupEntryProcessor() {
            return new CacheEntryProcessorTest.BackupEntryProcessor();
        }
    }

    public static class BackupEntryProcessor implements Serializable , EntryProcessor<Integer, String, Void> {
        private static final long serialVersionUID = -6376894786246368848L;

        @Override
        public Void process(MutableEntry<Integer, String> entry, Object... arguments) throws EntryProcessorException {
            entry.setValue("Foo");
            return null;
        }
    }

    public static class RemoveRecordEntryProcessor implements Serializable , EntryProcessor<Integer, String, Void> {
        @Override
        public Void process(MutableEntry<Integer, String> entry, Object... arguments) throws EntryProcessorException {
            entry.remove();
            return null;
        }
    }

    private static class GetAndCheckEntry implements Serializable , EntryProcessor<Integer, String, Boolean> {
        @Override
        public Boolean process(MutableEntry<Integer, String> entry, Object... arguments) throws EntryProcessorException {
            entry.getValue();
            return entry.exists();
        }
    }
}

