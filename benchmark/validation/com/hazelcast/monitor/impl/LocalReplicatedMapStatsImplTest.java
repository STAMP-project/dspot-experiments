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
package com.hazelcast.monitor.impl;


import com.hazelcast.internal.json.JsonObject;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class LocalReplicatedMapStatsImplTest {
    private LocalReplicatedMapStatsImpl localReplicatedMapStats;

    @Test
    public void testDefaultConstructor() {
        Assert.assertTrue(((localReplicatedMapStats.getCreationTime()) > 0));
        Assert.assertEquals(5, localReplicatedMapStats.getOwnedEntryCount());
        Assert.assertEquals(0, localReplicatedMapStats.getBackupEntryCount());
        Assert.assertEquals(0, localReplicatedMapStats.getBackupCount());
        Assert.assertEquals(1234, localReplicatedMapStats.getOwnedEntryMemoryCost());
        Assert.assertEquals(0, localReplicatedMapStats.getBackupEntryMemoryCost());
        Assert.assertEquals(1231241512, localReplicatedMapStats.getLastAccessTime());
        Assert.assertEquals(1341412343, localReplicatedMapStats.getLastUpdateTime());
        Assert.assertEquals(12314, localReplicatedMapStats.getHits());
        Assert.assertEquals(0, localReplicatedMapStats.getLockedEntryCount());
        Assert.assertEquals(0, localReplicatedMapStats.getDirtyEntryCount());
        Assert.assertEquals(11, localReplicatedMapStats.total());
        Assert.assertEquals(2, localReplicatedMapStats.getPutOperationCount());
        Assert.assertEquals(3, localReplicatedMapStats.getGetOperationCount());
        Assert.assertEquals(1, localReplicatedMapStats.getRemoveOperationCount());
        Assert.assertEquals(5632, localReplicatedMapStats.getTotalPutLatency());
        Assert.assertEquals(1247, localReplicatedMapStats.getTotalGetLatency());
        Assert.assertEquals(1238, localReplicatedMapStats.getTotalRemoveLatency());
        Assert.assertEquals(5631, localReplicatedMapStats.getMaxPutLatency());
        Assert.assertEquals(1233, localReplicatedMapStats.getMaxGetLatency());
        Assert.assertEquals(1238, localReplicatedMapStats.getMaxRemoveLatency());
        Assert.assertEquals(5, localReplicatedMapStats.getOtherOperationCount());
        Assert.assertEquals(2, localReplicatedMapStats.getEventOperationCount());
        Assert.assertEquals(0, localReplicatedMapStats.getHeapCost());
        Assert.assertEquals(0, localReplicatedMapStats.getMerkleTreesCost());
        Assert.assertNotNull(localReplicatedMapStats.toString());
    }

    @Test
    public void testSerialization() {
        JsonObject serialized = localReplicatedMapStats.toJson();
        LocalReplicatedMapStatsImpl deserialized = new LocalReplicatedMapStatsImpl();
        deserialized.fromJson(serialized);
        Assert.assertTrue(((deserialized.getCreationTime()) > 0));
        Assert.assertEquals(5, deserialized.getOwnedEntryCount());
        Assert.assertEquals(0, deserialized.getBackupEntryCount());
        Assert.assertEquals(0, deserialized.getBackupCount());
        Assert.assertEquals(1234, deserialized.getOwnedEntryMemoryCost());
        Assert.assertEquals(0, deserialized.getBackupEntryMemoryCost());
        Assert.assertEquals(1231241512, deserialized.getLastAccessTime());
        Assert.assertEquals(1341412343, deserialized.getLastUpdateTime());
        Assert.assertEquals(12314, deserialized.getHits());
        Assert.assertEquals(0, deserialized.getLockedEntryCount());
        Assert.assertEquals(0, deserialized.getDirtyEntryCount());
        Assert.assertEquals(11, deserialized.total());
        Assert.assertEquals(2, deserialized.getPutOperationCount());
        Assert.assertEquals(3, deserialized.getGetOperationCount());
        Assert.assertEquals(1, deserialized.getRemoveOperationCount());
        Assert.assertEquals(5632, deserialized.getTotalPutLatency());
        Assert.assertEquals(1247, deserialized.getTotalGetLatency());
        Assert.assertEquals(1238, deserialized.getTotalRemoveLatency());
        Assert.assertEquals(5631, deserialized.getMaxPutLatency());
        Assert.assertEquals(1233, deserialized.getMaxGetLatency());
        Assert.assertEquals(1238, deserialized.getMaxRemoveLatency());
        Assert.assertEquals(5, deserialized.getOtherOperationCount());
        Assert.assertEquals(2, deserialized.getEventOperationCount());
        Assert.assertEquals(0, deserialized.getHeapCost());
        Assert.assertEquals(0, deserialized.getMerkleTreesCost());
        Assert.assertNotNull(deserialized.toString());
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testNearCacheStats() {
        localReplicatedMapStats.getNearCacheStats();
    }
}

