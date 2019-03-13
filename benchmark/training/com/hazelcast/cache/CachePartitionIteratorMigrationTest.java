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
package com.hazelcast.cache;


import Cache.Entry;
import GroupProperty.PARTITION_COUNT;
import com.hazelcast.cache.impl.CacheProxy;
import com.hazelcast.config.Config;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.test.HazelcastParametersRunnerFactory;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.TestHazelcastInstanceFactory;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import java.util.HashSet;
import java.util.Iterator;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


@Ignore
@RunWith(Parameterized.class)
@Parameterized.UseParametersRunnerFactory(HazelcastParametersRunnerFactory.class)
@Category({ QuickTest.class, ParallelTest.class })
public class CachePartitionIteratorMigrationTest extends HazelcastTestSupport {
    @Parameterized.Parameter
    public boolean prefetchValues;

    @Test
    public void test_DoesNotReturn_DuplicateEntry_When_Rehashing_Happens() throws Exception {
        HazelcastInstance instance = createHazelcastInstance();
        CacheProxy<String, String> proxy = getCacheProxy(createCachingProvider(instance));
        HashSet<String> readKeys = new HashSet<String>();
        String value = "initialValue";
        putValuesToPartition(instance, proxy, value, 1, 100);
        Iterator<Entry<String, String>> iterator = proxy.iterator(10, 1, prefetchValues);
        assertUniques(readKeys, iterator, 50);
        // force rehashing
        putValuesToPartition(instance, proxy, HazelcastTestSupport.randomString(), 1, 150);
        assertUniques(readKeys, iterator);
    }

    @Test
    public void test_DoesNotReturn_DuplicateEntry_When_Migration_Happens() throws Exception {
        Config config = getConfig();
        config.setProperty(PARTITION_COUNT.getName(), "2");
        TestHazelcastInstanceFactory factory = createHazelcastInstanceFactory();
        HazelcastInstance instance = factory.newHazelcastInstance(config);
        CacheProxy<String, String> proxy = getCacheProxy(createCachingProvider(instance));
        HashSet<String> readKeysP1 = new HashSet<String>();
        HashSet<String> readKeysP2 = new HashSet<String>();
        String value = "value";
        putValuesToPartition(instance, proxy, value, 0, 100);
        putValuesToPartition(instance, proxy, value, 1, 100);
        Iterator<Entry<String, String>> iteratorP1 = proxy.iterator(10, 0, prefetchValues);
        Iterator<Entry<String, String>> iteratorP2 = proxy.iterator(10, 1, prefetchValues);
        assertUniques(readKeysP1, iteratorP1, 50);
        assertUniques(readKeysP2, iteratorP2, 50);
        // force migration
        factory.newHazelcastInstance(config);
        // force rehashing
        putValuesToPartition(instance, proxy, HazelcastTestSupport.randomString(), 0, 150);
        putValuesToPartition(instance, proxy, HazelcastTestSupport.randomString(), 1, 150);
        assertUniques(readKeysP1, iteratorP1);
        assertUniques(readKeysP2, iteratorP2);
    }
}

