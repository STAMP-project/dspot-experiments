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


import WanPublisherState.REPLICATING;
import com.hazelcast.internal.json.JsonObject;
import com.hazelcast.monitor.LocalWanPublisherStats;
import com.hazelcast.monitor.LocalWanStats;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import java.util.HashMap;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class LocalWanStatsImplTest {
    @Test
    public void testSerialization() {
        LocalWanPublisherStatsImpl tokyo = new LocalWanPublisherStatsImpl();
        tokyo.setConnected(true);
        tokyo.incrementPublishedEventCount(10);
        tokyo.setOutboundQueueSize(100);
        tokyo.setState(REPLICATING);
        LocalWanPublisherStatsImpl singapore = new LocalWanPublisherStatsImpl();
        singapore.setConnected(true);
        singapore.setOutboundQueueSize(200);
        singapore.incrementPublishedEventCount(20);
        singapore.setState(REPLICATING);
        LocalWanStatsImpl localWanStats = new LocalWanStatsImpl();
        Map<String, LocalWanPublisherStats> localWanPublisherStatsMap = new HashMap<String, LocalWanPublisherStats>();
        localWanPublisherStatsMap.put("tokyo", tokyo);
        localWanPublisherStatsMap.put("singapore", singapore);
        localWanStats.setLocalPublisherStatsMap(localWanPublisherStatsMap);
        JsonObject serialized = localWanStats.toJson();
        LocalWanStats deserialized = new LocalWanStatsImpl();
        deserialized.fromJson(serialized);
        LocalWanPublisherStats deserializedTokyo = deserialized.getLocalWanPublisherStats().get("tokyo");
        LocalWanPublisherStats deserializedSingapore = deserialized.getLocalWanPublisherStats().get("singapore");
        Assert.assertEquals(tokyo.isConnected(), deserializedTokyo.isConnected());
        Assert.assertEquals(tokyo.getTotalPublishedEventCount(), deserializedTokyo.getTotalPublishedEventCount());
        Assert.assertEquals(tokyo.getOutboundQueueSize(), deserializedTokyo.getOutboundQueueSize());
        Assert.assertEquals(tokyo.getTotalPublishLatency(), deserializedTokyo.getTotalPublishLatency());
        Assert.assertEquals(singapore.isConnected(), deserializedSingapore.isConnected());
        Assert.assertEquals(singapore.getTotalPublishedEventCount(), deserializedSingapore.getTotalPublishedEventCount());
        Assert.assertEquals(singapore.getOutboundQueueSize(), deserializedSingapore.getOutboundQueueSize());
        Assert.assertEquals(singapore.getTotalPublishLatency(), deserializedSingapore.getTotalPublishLatency());
    }
}

