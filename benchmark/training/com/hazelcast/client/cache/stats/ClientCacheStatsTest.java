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
package com.hazelcast.client.cache.stats;


import com.hazelcast.cache.CacheStatistics;
import com.hazelcast.cache.ICache;
import com.hazelcast.cache.stats.CacheStatsTest;
import com.hazelcast.client.test.TestHazelcastFactory;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.test.HazelcastParametersRunnerFactory;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


@RunWith(Parameterized.class)
@Parameterized.UseParametersRunnerFactory(HazelcastParametersRunnerFactory.class)
@Category({ QuickTest.class, ParallelTest.class })
public class ClientCacheStatsTest extends CacheStatsTest {
    @Parameterized.Parameter
    public boolean nearCacheEnabled;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private final TestHazelcastFactory instanceFactory = new TestHazelcastFactory();

    private HazelcastInstance client;

    @Override
    @Test
    public void testOwnedEntryCountWhenThereIsNoBackup() {
        expectedException.expect(UnsupportedOperationException.class);
        super.testOwnedEntryCountWhenThereIsNoBackup();
    }

    @Override
    @Test
    public void testOwnedEntryCountWhenThereAreBackupsOnStaticCluster() {
        expectedException.expect(UnsupportedOperationException.class);
        super.testOwnedEntryCountWhenThereAreBackupsOnStaticCluster();
    }

    @Override
    @Test
    public void testOwnedEntryCountWhenThereAreBackupsOnDynamicCluster() {
        expectedException.expect(UnsupportedOperationException.class);
        super.testOwnedEntryCountWhenThereAreBackupsOnDynamicCluster();
    }

    @Override
    @Test
    public void testExpirations() {
        expectedException.expect(UnsupportedOperationException.class);
        super.testExpirations();
    }

    @Override
    @Test
    public void testEvictions() {
        ICache<Integer, String> cache = createCache();
        CacheStatistics stats = cache.getLocalCacheStatistics();
        expectedException.expect(UnsupportedOperationException.class);
        stats.getCacheEvictions();
    }

    @Override
    @Test
    public void testNearCacheStats_availableWhenEnabled() {
        if (nearCacheEnabled) {
            testNearCacheStats_whenNearCacheEnabled();
        } else {
            expectedException.expect(UnsupportedOperationException.class);
            testNearCacheStats_whenNearCacheDisabled();
        }
    }
}

