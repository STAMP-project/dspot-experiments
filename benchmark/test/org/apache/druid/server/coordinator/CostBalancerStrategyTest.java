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
import org.apache.druid.java.util.common.DateTimes;
import org.apache.druid.java.util.common.Intervals;
import org.apache.druid.timeline.DataSegment;
import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.junit.Assert;
import org.junit.Test;

import static CostBalancerStrategy.INV_LAMBDA_SQUARE;
import static CostBalancerStrategy.LAMBDA;


public class CostBalancerStrategyTest {
    private static final Interval day = Intervals.of("2015-01-01T00/2015-01-01T01");

    @Test
    public void testCostBalancerMultiThreadedStrategy() {
        List<ServerHolder> serverHolderList = CostBalancerStrategyTest.setupDummyCluster(10, 20);
        DataSegment segment = CostBalancerStrategyTest.getSegment(1000);
        BalancerStrategy strategy = new CostBalancerStrategy(MoreExecutors.listeningDecorator(Executors.newFixedThreadPool(4)));
        ServerHolder holder = strategy.findNewSegmentHomeReplicator(segment, serverHolderList);
        Assert.assertNotNull("Should be able to find a place for new segment!!", holder);
        Assert.assertEquals("Best Server should be BEST_SERVER", "BEST_SERVER", holder.getServer().getName());
    }

    @Test
    public void testCostBalancerSingleThreadStrategy() {
        List<ServerHolder> serverHolderList = CostBalancerStrategyTest.setupDummyCluster(10, 20);
        DataSegment segment = CostBalancerStrategyTest.getSegment(1000);
        BalancerStrategy strategy = new CostBalancerStrategy(MoreExecutors.listeningDecorator(Executors.newFixedThreadPool(1)));
        ServerHolder holder = strategy.findNewSegmentHomeReplicator(segment, serverHolderList);
        Assert.assertNotNull("Should be able to find a place for new segment!!", holder);
        Assert.assertEquals("Best Server should be BEST_SERVER", "BEST_SERVER", holder.getServer().getName());
    }

    @Test
    public void testComputeJointSegmentCost() {
        DateTime referenceTime = DateTimes.of("2014-01-01T00:00:00");
        CostBalancerStrategy strategy = new CostBalancerStrategy(MoreExecutors.listeningDecorator(Executors.newFixedThreadPool(4)));
        double segmentCost = strategy.computeJointSegmentsCost(CostBalancerStrategyTest.getSegment(100, "DUMMY", new Interval(referenceTime, referenceTime.plusHours(1))), CostBalancerStrategyTest.getSegment(101, "DUMMY", new Interval(referenceTime.minusHours(2), referenceTime.minusHours(2).plusHours(1))));
        Assert.assertEquals((((INV_LAMBDA_SQUARE) * (CostBalancerStrategy.intervalCost((1 * (LAMBDA)), ((-2) * (LAMBDA)), ((-1) * (LAMBDA))))) * 2), segmentCost, 1.0E-6);
    }

    @Test
    public void testIntervalCost() {
        // additivity
        Assert.assertEquals(CostBalancerStrategy.intervalCost(1, 1, 3), ((CostBalancerStrategy.intervalCost(1, 1, 2)) + (CostBalancerStrategy.intervalCost(1, 2, 3))), 1.0E-6);
        Assert.assertEquals(CostBalancerStrategy.intervalCost(2, 1, 3), ((CostBalancerStrategy.intervalCost(2, 1, 2)) + (CostBalancerStrategy.intervalCost(2, 2, 3))), 1.0E-6);
        Assert.assertEquals(CostBalancerStrategy.intervalCost(3, 1, 2), (((CostBalancerStrategy.intervalCost(1, 1, 2)) + (CostBalancerStrategy.intervalCost(1, 0, 1))) + (CostBalancerStrategy.intervalCost(1, 1, 2))), 1.0E-6);
        // no overlap [0, 1) [1, 2)
        Assert.assertEquals(0.3995764, CostBalancerStrategy.intervalCost(1, 1, 2), 1.0E-6);
        // no overlap [0, 1) [-1, 0)
        Assert.assertEquals(0.3995764, CostBalancerStrategy.intervalCost(1, (-1), 0), 1.0E-6);
        // exact overlap [0, 1), [0, 1)
        Assert.assertEquals(0.7357589, CostBalancerStrategy.intervalCost(1, 0, 1), 1.0E-6);
        // exact overlap [0, 2), [0, 2)
        Assert.assertEquals(2.270671, CostBalancerStrategy.intervalCost(2, 0, 2), 1.0E-6);
        // partial overlap [0, 2), [1, 3)
        Assert.assertEquals(1.681908, CostBalancerStrategy.intervalCost(2, 1, 3), 1.0E-6);
        // partial overlap [0, 2), [1, 2)
        Assert.assertEquals(1.135335, CostBalancerStrategy.intervalCost(2, 1, 2), 1.0E-6);
        // partial overlap [0, 2), [0, 1)
        Assert.assertEquals(1.135335, CostBalancerStrategy.intervalCost(2, 0, 1), 1.0E-6);
        // partial overlap [0, 3), [1, 2)
        Assert.assertEquals(1.534912, CostBalancerStrategy.intervalCost(3, 1, 2), 1.0E-6);
    }
}

