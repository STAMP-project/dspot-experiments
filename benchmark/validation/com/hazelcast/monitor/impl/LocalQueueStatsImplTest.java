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
public class LocalQueueStatsImplTest {
    @Test
    public void testDefaultConstructor() {
        LocalQueueStatsImpl localQueueStats = new LocalQueueStatsImpl();
        localQueueStats.setMinAge(13);
        localQueueStats.setMaxAge(28);
        localQueueStats.setAveAge(18);
        localQueueStats.setOwnedItemCount(1234);
        localQueueStats.setBackupItemCount(15124);
        Assert.assertTrue(((localQueueStats.getCreationTime()) > 0));
        Assert.assertEquals(13, localQueueStats.getMinAge());
        Assert.assertEquals(28, localQueueStats.getMaxAge());
        Assert.assertEquals(18, localQueueStats.getAvgAge());
        Assert.assertEquals(1234, localQueueStats.getOwnedItemCount());
        Assert.assertEquals(15124, localQueueStats.getBackupItemCount());
        Assert.assertNotNull(localQueueStats.toString());
    }

    @Test
    public void testSerialization() {
        LocalQueueStatsImpl localQueueStats = new LocalQueueStatsImpl();
        localQueueStats.incrementOtherOperations();
        localQueueStats.incrementOtherOperations();
        localQueueStats.incrementOffers();
        localQueueStats.incrementOffers();
        localQueueStats.incrementOffers();
        localQueueStats.incrementRejectedOffers();
        localQueueStats.incrementRejectedOffers();
        localQueueStats.incrementRejectedOffers();
        localQueueStats.incrementRejectedOffers();
        localQueueStats.incrementPolls();
        localQueueStats.incrementEmptyPolls();
        localQueueStats.incrementEmptyPolls();
        localQueueStats.incrementReceivedEvents();
        localQueueStats.incrementReceivedEvents();
        localQueueStats.incrementReceivedEvents();
        JsonObject serialized = localQueueStats.toJson();
        LocalQueueStatsImpl deserialized = new LocalQueueStatsImpl();
        deserialized.fromJson(serialized);
        Assert.assertTrue(((deserialized.getCreationTime()) > 0));
        Assert.assertEquals(2, deserialized.getOtherOperationsCount());
        Assert.assertEquals(3, deserialized.getOfferOperationCount());
        Assert.assertEquals(4, deserialized.getRejectedOfferOperationCount());
        Assert.assertEquals(1, deserialized.getPollOperationCount());
        Assert.assertEquals(2, deserialized.getEmptyPollOperationCount());
        Assert.assertEquals(3, deserialized.getEventOperationCount());
        Assert.assertEquals(6, deserialized.total());
        Assert.assertNotNull(deserialized.toString());
    }
}

