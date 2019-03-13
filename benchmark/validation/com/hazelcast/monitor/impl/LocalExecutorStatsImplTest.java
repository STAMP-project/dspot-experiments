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
public class LocalExecutorStatsImplTest {
    @Test
    public void testDefaultConstructor() {
        LocalExecutorStatsImpl localExecutorStats = new LocalExecutorStatsImpl();
        Assert.assertTrue(((localExecutorStats.getCreationTime()) > 0));
        Assert.assertEquals(0, localExecutorStats.getPendingTaskCount());
        Assert.assertEquals(0, localExecutorStats.getStartedTaskCount());
        Assert.assertEquals(0, localExecutorStats.getCompletedTaskCount());
        Assert.assertEquals(0, localExecutorStats.getCancelledTaskCount());
        Assert.assertEquals(0, localExecutorStats.getTotalStartLatency());
        Assert.assertEquals(0, localExecutorStats.getTotalExecutionLatency());
        Assert.assertNotNull(localExecutorStats.toString());
    }

    @Test
    public void testSerialization() {
        LocalExecutorStatsImpl localExecutorStats = new LocalExecutorStatsImpl();
        localExecutorStats.startPending();
        localExecutorStats.startPending();
        localExecutorStats.startPending();
        localExecutorStats.startPending();
        localExecutorStats.startExecution(5);
        localExecutorStats.startExecution(5);
        localExecutorStats.finishExecution(5);
        localExecutorStats.rejectExecution();
        localExecutorStats.cancelExecution();
        JsonObject serialized = localExecutorStats.toJson();
        LocalExecutorStatsImpl deserialized = new LocalExecutorStatsImpl();
        deserialized.fromJson(serialized);
        Assert.assertEquals(1, deserialized.getPendingTaskCount());
        Assert.assertEquals(2, deserialized.getStartedTaskCount());
        Assert.assertEquals(1, deserialized.getCompletedTaskCount());
        Assert.assertEquals(1, localExecutorStats.getCancelledTaskCount());
        Assert.assertEquals(1, deserialized.getCancelledTaskCount());
    }
}

