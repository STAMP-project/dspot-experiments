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
public class LocalMultiMapStatsImplTest {
    private LocalMultiMapStatsImpl localMapStats;

    @Test
    public void testDefaultConstructor() {
        Assert.assertTrue(((localMapStats.getCreationTime()) > 0));
        Assert.assertEquals(5, localMapStats.getOwnedEntryCount());
        Assert.assertEquals(3, localMapStats.getBackupEntryCount());
        Assert.assertEquals(4, localMapStats.getBackupCount());
        Assert.assertEquals(1234, localMapStats.getOwnedEntryMemoryCost());
        Assert.assertEquals(4321, localMapStats.getBackupEntryMemoryCost());
        Assert.assertEquals(1231241512, localMapStats.getLastAccessTime());
        Assert.assertEquals(1341412343, localMapStats.getLastUpdateTime());
        Assert.assertEquals(12314, localMapStats.getHits());
        Assert.assertEquals(1231, localMapStats.getLockedEntryCount());
        Assert.assertEquals(4252, localMapStats.getDirtyEntryCount());
        Assert.assertEquals(11, localMapStats.total());
        Assert.assertEquals(2, localMapStats.getPutOperationCount());
        Assert.assertEquals(3, localMapStats.getGetOperationCount());
        Assert.assertEquals(1, localMapStats.getRemoveOperationCount());
        Assert.assertEquals(5632, localMapStats.getTotalPutLatency());
        Assert.assertEquals(1247, localMapStats.getTotalGetLatency());
        Assert.assertEquals(1238, localMapStats.getTotalRemoveLatency());
        Assert.assertEquals(5631, localMapStats.getMaxPutLatency());
        Assert.assertEquals(1233, localMapStats.getMaxGetLatency());
        Assert.assertEquals(1238, localMapStats.getMaxRemoveLatency());
        Assert.assertEquals(5, localMapStats.getOtherOperationCount());
        Assert.assertEquals(2, localMapStats.getEventOperationCount());
        Assert.assertEquals(7461762, localMapStats.getHeapCost());
        Assert.assertNotNull(localMapStats.getNearCacheStats());
        Assert.assertNotNull(localMapStats.toString());
    }

    @Test
    public void testSerialization() {
        JsonObject serialized = localMapStats.toJson();
        LocalMultiMapStatsImpl deserialized = new LocalMultiMapStatsImpl();
        deserialized.fromJson(serialized);
        Assert.assertTrue(((deserialized.getCreationTime()) > 0));
        Assert.assertEquals(5, deserialized.getOwnedEntryCount());
        Assert.assertEquals(3, deserialized.getBackupEntryCount());
        Assert.assertEquals(4, deserialized.getBackupCount());
        Assert.assertEquals(1234, deserialized.getOwnedEntryMemoryCost());
        Assert.assertEquals(4321, deserialized.getBackupEntryMemoryCost());
        Assert.assertEquals(1231241512, deserialized.getLastAccessTime());
        Assert.assertEquals(1341412343, deserialized.getLastUpdateTime());
        Assert.assertEquals(12314, deserialized.getHits());
        Assert.assertEquals(1231, deserialized.getLockedEntryCount());
        Assert.assertEquals(4252, deserialized.getDirtyEntryCount());
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
        Assert.assertEquals(7461762, deserialized.getHeapCost());
        Assert.assertNotNull(deserialized.getNearCacheStats());
        Assert.assertNotNull(deserialized.toString());
    }
}

