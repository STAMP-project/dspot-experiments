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


import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class LocalIndexStatsImplTest {
    private LocalIndexStatsImpl stats;

    @Test
    public void testDefaultConstructor() {
        Assert.assertEquals(1234, stats.getCreationTime());
        Assert.assertEquals(20, stats.getHitCount());
        Assert.assertEquals(11, stats.getQueryCount());
        Assert.assertEquals(0.5, stats.getAverageHitSelectivity(), 0.01);
        Assert.assertEquals(81273, stats.getAverageHitLatency());
        Assert.assertEquals(91238, stats.getInsertCount());
        Assert.assertEquals(83912, stats.getTotalInsertLatency());
        Assert.assertEquals(712639, stats.getUpdateCount());
        Assert.assertEquals(34623, stats.getTotalUpdateLatency());
        Assert.assertEquals(749274, stats.getRemoveCount());
        Assert.assertEquals(1454957, stats.getTotalRemoveLatency());
        Assert.assertEquals(2345, stats.getMemoryCost());
        Assert.assertNotNull(stats.toString());
    }

    @Test
    public void testSerialization() {
        LocalIndexStatsImpl deserialized = new LocalIndexStatsImpl();
        deserialized.fromJson(stats.toJson());
        Assert.assertEquals(1234, deserialized.getCreationTime());
        Assert.assertEquals(20, deserialized.getHitCount());
        Assert.assertEquals(11, deserialized.getQueryCount());
        Assert.assertEquals(0.5, deserialized.getAverageHitSelectivity(), 0.01);
        Assert.assertEquals(81273, deserialized.getAverageHitLatency());
        Assert.assertEquals(91238, deserialized.getInsertCount());
        Assert.assertEquals(83912, deserialized.getTotalInsertLatency());
        Assert.assertEquals(712639, deserialized.getUpdateCount());
        Assert.assertEquals(34623, deserialized.getTotalUpdateLatency());
        Assert.assertEquals(749274, deserialized.getRemoveCount());
        Assert.assertEquals(1454957, deserialized.getTotalRemoveLatency());
        Assert.assertEquals(2345, deserialized.getMemoryCost());
        Assert.assertNotNull(deserialized.toString());
    }
}

