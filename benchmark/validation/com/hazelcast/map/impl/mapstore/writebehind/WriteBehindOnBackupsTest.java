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
package com.hazelcast.map.impl.mapstore.writebehind;


import com.hazelcast.core.IMap;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.TestHazelcastInstanceFactory;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import com.hazelcast.test.annotation.SlowTest;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class WriteBehindOnBackupsTest extends HazelcastTestSupport {
    /**
     * {@link com.hazelcast.map.impl.mapstore.writebehind.StoreWorker} delays processing of write-behind queues (wbq) by adding
     * delay with {@link GroupProperty#MAP_REPLICA_SCHEDULED_TASK_DELAY_SECONDS} property.
     * This is used to provide some extra robustness against node disaster scenarios by trying to prevent lost of entries in wbq-s.
     * Normally backup nodes don't store entries only remove them from wbq-s. Here, we are testing removal of entries occurred or not.
     */
    @Test
    public void testBackupRemovesEntries_afterProcessingDelay() throws Exception {
        final int numberOfItems = 10;
        final String mapName = HazelcastTestSupport.randomMapName();
        final MapStoreWithCounter<Integer, Integer> mapStore = new MapStoreWithCounter<Integer, Integer>();
        TestMapUsingMapStoreBuilder<Integer, Integer> storeBuilder = TestMapUsingMapStoreBuilder.create();
        final IMap<Integer, Integer> map = storeBuilder.mapName(mapName).withMapStore(mapStore).withNodeCount(2).withNodeFactory(createHazelcastInstanceFactory(2)).withWriteDelaySeconds(1).withBackupCount(1).withPartitionCount(1).withBackupProcessingDelay(1).build();
        populateMap(map, numberOfItems);
        assertWriteBehindQueuesEmptyOnOwnerAndOnBackups(mapName, numberOfItems, mapStore, storeBuilder.getNodes());
    }

    @Test
    public void testPutTransientDoesNotStoreEntry_onBackupPartition() {
        String mapName = HazelcastTestSupport.randomMapName();
        final MapStoreWithCounter<Integer, Integer> mapStore = new MapStoreWithCounter<Integer, Integer>();
        TestMapUsingMapStoreBuilder<Integer, Integer> storeBuilder = TestMapUsingMapStoreBuilder.create();
        final IMap<Integer, Integer> map = storeBuilder.mapName(mapName).withMapStore(mapStore).withNodeCount(2).withNodeFactory(createHazelcastInstanceFactory(2)).withWriteDelaySeconds(1).withBackupCount(1).withPartitionCount(1).withBackupProcessingDelay(1).build();
        map.putTransient(1, 1, 1, TimeUnit.DAYS);
        HazelcastTestSupport.sleepSeconds(5);
        Assert.assertEquals("There should not be any store operation", 0, mapStore.countStore.get());
    }

    @Test
    @Category(SlowTest.class)
    public void testPutTransientDoesNotStoreEntry_onPromotedReplica() {
        String mapName = HazelcastTestSupport.randomMapName();
        final MapStoreWithCounter<String, Object> mapStore = new MapStoreWithCounter<String, Object>();
        TestMapUsingMapStoreBuilder<String, Object> storeBuilder = TestMapUsingMapStoreBuilder.create();
        TestHazelcastInstanceFactory factory = createHazelcastInstanceFactory(2);
        final IMap<String, Object> map = storeBuilder.mapName(mapName).withMapStore(mapStore).withNodeCount(2).withNodeFactory(factory).withWriteDelaySeconds(5).withBackupCount(1).withPartitionCount(1).withBackupProcessingDelay(1).build();
        String key = UUID.randomUUID().toString();
        map.putTransient(key, 1, 1, TimeUnit.DAYS);
        killKeyOwner(key, storeBuilder);
        HazelcastTestSupport.sleepSeconds(10);
        Assert.assertEquals("There should not be any store operation on promoted replica", 0, mapStore.countStore.get());
    }
}

