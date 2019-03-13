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
package com.hazelcast.internal.nearcache;


import EvictionPolicy.LFU;
import EvictionPolicy.LRU;
import EvictionPolicy.RANDOM;
import MaxSizePolicy.ENTRY_COUNT;
import MaxSizePolicy.FREE_NATIVE_MEMORY_PERCENTAGE;
import MaxSizePolicy.FREE_NATIVE_MEMORY_SIZE;
import MaxSizePolicy.USED_NATIVE_MEMORY_PERCENTAGE;
import MaxSizePolicy.USED_NATIVE_MEMORY_SIZE;
import com.hazelcast.config.InMemoryFormat;
import com.hazelcast.test.AssertTask;
import com.hazelcast.test.HazelcastParametersRunnerFactory;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


@RunWith(Parameterized.class)
@Parameterized.UseParametersRunnerFactory(HazelcastParametersRunnerFactory.class)
@Category({ QuickTest.class, ParallelTest.class })
public class NearCacheRecordStoreTest extends NearCacheRecordStoreTestSupport {
    @Parameterized.Parameter
    public InMemoryFormat inMemoryFormat;

    @Test
    public void putAndGetRecord() {
        putAndGetRecord(inMemoryFormat);
    }

    @Test
    public void putAndRemoveRecord() {
        putAndRemoveRecord(inMemoryFormat);
    }

    @Test
    public void clearRecords() {
        clearRecordsOrDestroyStore(inMemoryFormat, false);
    }

    @Test
    public void destroyStore() {
        clearRecordsOrDestroyStore(inMemoryFormat, true);
    }

    @Test
    public void statsCalculated() {
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() {
                statsCalculated(inMemoryFormat);
            }
        });
    }

    @Test
    public void ttlEvaluated() {
        ttlEvaluated(inMemoryFormat);
    }

    @Test
    public void maxIdleTimeEvaluatedSuccessfully() {
        maxIdleTimeEvaluatedSuccessfully(inMemoryFormat);
    }

    @Test
    public void expiredRecordsCleanedUpSuccessfullyBecauseOfTTL() {
        expiredRecordsCleanedUpSuccessfully(inMemoryFormat, false);
    }

    @Test
    public void expiredRecordsCleanedUpSuccessfullyBecauseOfIdleTime() {
        expiredRecordsCleanedUpSuccessfully(inMemoryFormat, true);
    }

    @Test
    public void canCreateWithEntryCountMaxSizePolicy() {
        createNearCacheWithMaxSizePolicy(inMemoryFormat, ENTRY_COUNT, 1000);
    }

    @Test(expected = IllegalArgumentException.class)
    public void cannotCreateWithUsedNativeMemorySizeMaxSizePolicy() {
        createNearCacheWithMaxSizePolicy(inMemoryFormat, USED_NATIVE_MEMORY_SIZE, 1000000);
    }

    @Test(expected = IllegalArgumentException.class)
    public void cannotCreateWithFreeNativeMemorySizeMaxSizePolicy() {
        createNearCacheWithMaxSizePolicy(inMemoryFormat, FREE_NATIVE_MEMORY_SIZE, 1000000);
    }

    @Test(expected = IllegalArgumentException.class)
    public void cannotCreateWithUsedNativeMemoryPercentageMaxSizePolicy() {
        createNearCacheWithMaxSizePolicy(inMemoryFormat, USED_NATIVE_MEMORY_PERCENTAGE, 99);
    }

    @Test(expected = IllegalArgumentException.class)
    public void cannotCreateNearWithFreeNativeMemoryPercentageMaxSizePolicy() {
        createNearCacheWithMaxSizePolicy(inMemoryFormat, FREE_NATIVE_MEMORY_PERCENTAGE, 1);
    }

    @Test
    public void evictionTriggeredAndHandledSuccessfullyWithEntryCountMaxSizePolicyAndLRUEvictionPolicy() {
        doEvictionWithEntryCountMaxSizePolicy(inMemoryFormat, LRU);
    }

    @Test
    public void evictionTriggeredAndHandledSuccessfullyWithEntryCountMaxSizePolicyAndLFUEvictionPolicy() {
        doEvictionWithEntryCountMaxSizePolicy(inMemoryFormat, LFU);
    }

    @Test
    public void evictionTriggeredAndHandledSuccessfullyWithEntryCountMaxSizePolicyAndRandomEvictionPolicy() {
        doEvictionWithEntryCountMaxSizePolicy(inMemoryFormat, RANDOM);
    }

    @Test
    public void evictionTriggeredAndHandledSuccessfullyWithEntryCountMaxSizePolicyAndDefaultEvictionPolicy() {
        doEvictionWithEntryCountMaxSizePolicy(inMemoryFormat, null);
    }
}

