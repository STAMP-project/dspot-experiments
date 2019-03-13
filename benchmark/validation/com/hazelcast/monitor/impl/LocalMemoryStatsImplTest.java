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
public class LocalMemoryStatsImplTest {
    private LocalMemoryStatsImpl localMemoryStats;

    @Test
    public void testDefaultConstructor() {
        Assert.assertEquals(0, localMemoryStats.getCreationTime());
        Assert.assertEquals(4196, localMemoryStats.getTotalPhysical());
        Assert.assertEquals(2048, localMemoryStats.getFreePhysical());
        Assert.assertEquals(1024, localMemoryStats.getMaxNative());
        Assert.assertEquals(768, localMemoryStats.getCommittedNative());
        Assert.assertEquals(512, localMemoryStats.getUsedNative());
        Assert.assertEquals(256, localMemoryStats.getFreeNative());
        Assert.assertEquals(3333, localMemoryStats.getMaxHeap());
        Assert.assertEquals(2222, localMemoryStats.getCommittedHeap());
        Assert.assertEquals(1111, localMemoryStats.getUsedHeap());
        Assert.assertEquals(2222, localMemoryStats.getFreeHeap());
        Assert.assertNotNull(localMemoryStats.getGCStats());
        Assert.assertNotNull(localMemoryStats.toString());
    }

    @Test
    public void testSerialization() {
        LocalMemoryStatsImpl memoryStats = new LocalMemoryStatsImpl(localMemoryStats);
        memoryStats.setGcStats(null);
        JsonObject serialized = memoryStats.toJson();
        LocalMemoryStatsImpl deserialized = new LocalMemoryStatsImpl();
        deserialized.fromJson(serialized);
        Assert.assertEquals(0, deserialized.getCreationTime());
        Assert.assertEquals(4196, deserialized.getTotalPhysical());
        Assert.assertEquals(2048, deserialized.getFreePhysical());
        Assert.assertEquals(1024, deserialized.getMaxNative());
        Assert.assertEquals(768, deserialized.getCommittedNative());
        Assert.assertEquals(512, deserialized.getUsedNative());
        Assert.assertEquals(256, deserialized.getFreeNative());
        Assert.assertEquals(3333, deserialized.getMaxHeap());
        Assert.assertEquals(2222, deserialized.getCommittedHeap());
        Assert.assertEquals(1111, deserialized.getUsedHeap());
        Assert.assertEquals(2222, deserialized.getFreeHeap());
        Assert.assertNotNull(deserialized.getGCStats());
        Assert.assertNotNull(deserialized.toString());
    }
}

