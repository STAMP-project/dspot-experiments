/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.druid.server.coordinator;


import com.google.common.util.concurrent.MoreExecutors;
import java.util.List;
import java.util.concurrent.Executors;
import org.apache.druid.java.util.common.Intervals;
import org.apache.druid.timeline.DataSegment;
import org.joda.time.Interval;
import org.junit.Assert;
import org.junit.Test;


public class DiskNormalizedCostBalancerStrategyTest {
    private static final Interval day = Intervals.of("2015-01-01T00/2015-01-01T01");

    @Test
    public void testNormalizedCostBalancerMultiThreadedStrategy() {
        List<ServerHolder> serverHolderList = DiskNormalizedCostBalancerStrategyTest.setupDummyCluster(10, 20);
        DataSegment segment = DiskNormalizedCostBalancerStrategyTest.getSegment(1000);
        BalancerStrategy strategy = new DiskNormalizedCostBalancerStrategy(MoreExecutors.listeningDecorator(Executors.newFixedThreadPool(4)));
        ServerHolder holder = strategy.findNewSegmentHomeReplicator(segment, serverHolderList);
        Assert.assertNotNull("Should be able to find a place for new segment!!", holder);
        Assert.assertEquals("Best Server should be BEST_SERVER", "BEST_SERVER", holder.getServer().getName());
    }

    @Test
    public void testNormalizedCostBalancerSingleThreadStrategy() {
        List<ServerHolder> serverHolderList = DiskNormalizedCostBalancerStrategyTest.setupDummyCluster(10, 20);
        DataSegment segment = DiskNormalizedCostBalancerStrategyTest.getSegment(1000);
        BalancerStrategy strategy = new DiskNormalizedCostBalancerStrategy(MoreExecutors.listeningDecorator(Executors.newFixedThreadPool(1)));
        ServerHolder holder = strategy.findNewSegmentHomeReplicator(segment, serverHolderList);
        Assert.assertNotNull("Should be able to find a place for new segment!!", holder);
        Assert.assertEquals("Best Server should be BEST_SERVER", "BEST_SERVER", holder.getServer().getName());
    }
}

