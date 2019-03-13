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
import com.hazelcast.memory.GarbageCollectorStats;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class LocalGCStatsImplTest {
    @Test
    public void testDefaultConstructor() {
        LocalGCStatsImpl localGCStats = new LocalGCStatsImpl();
        localGCStats.setMajorCount(8);
        localGCStats.setMajorTime(7);
        localGCStats.setMinorCount(6);
        localGCStats.setMinorTime(5);
        localGCStats.setUnknownCount(4);
        localGCStats.setUnknownTime(3);
        Assert.assertTrue(((localGCStats.getCreationTime()) > 0));
        Assert.assertEquals(8, localGCStats.getMajorCollectionCount());
        Assert.assertEquals(7, localGCStats.getMajorCollectionTime());
        Assert.assertEquals(6, localGCStats.getMinorCollectionCount());
        Assert.assertEquals(5, localGCStats.getMinorCollectionTime());
        Assert.assertEquals(4, localGCStats.getUnknownCollectionCount());
        Assert.assertEquals(3, localGCStats.getUnknownCollectionTime());
        Assert.assertNotNull(localGCStats.toString());
    }

    @Test
    public void testSerialization() {
        GarbageCollectorStats garbageCollectorStats = new GarbageCollectorStats() {
            @Override
            public long getMajorCollectionCount() {
                return 125;
            }

            @Override
            public long getMajorCollectionTime() {
                return 14778;
            }

            @Override
            public long getMinorCollectionCount() {
                return 19;
            }

            @Override
            public long getMinorCollectionTime() {
                return 102931;
            }

            @Override
            public long getUnknownCollectionCount() {
                return 129;
            }

            @Override
            public long getUnknownCollectionTime() {
                return 49182;
            }
        };
        LocalGCStatsImpl localGCStats = new LocalGCStatsImpl(garbageCollectorStats);
        JsonObject serialized = localGCStats.toJson();
        LocalGCStatsImpl deserialized = new LocalGCStatsImpl();
        deserialized.fromJson(serialized);
        Assert.assertEquals(0, localGCStats.getCreationTime());
        Assert.assertEquals(125, localGCStats.getMajorCollectionCount());
        Assert.assertEquals(14778, localGCStats.getMajorCollectionTime());
        Assert.assertEquals(19, localGCStats.getMinorCollectionCount());
        Assert.assertEquals(102931, localGCStats.getMinorCollectionTime());
        Assert.assertEquals(129, localGCStats.getUnknownCollectionCount());
        Assert.assertEquals(49182, localGCStats.getUnknownCollectionTime());
        Assert.assertNotNull(localGCStats.toString());
    }
}

