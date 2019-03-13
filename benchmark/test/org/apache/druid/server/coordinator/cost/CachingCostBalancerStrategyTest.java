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
package org.apache.druid.server.coordinator.cost;


import com.google.common.util.concurrent.ListeningExecutorService;
import java.util.List;
import org.apache.druid.server.coordinator.CachingCostBalancerStrategy;
import org.apache.druid.server.coordinator.CostBalancerStrategy;
import org.apache.druid.server.coordinator.ServerHolder;
import org.apache.druid.timeline.DataSegment;
import org.junit.Assert;
import org.junit.Test;


public class CachingCostBalancerStrategyTest {
    private static final int DAYS_IN_MONTH = 30;

    private static final int SEGMENT_SIZE = 100;

    private static final int NUMBER_OF_SEGMENTS_ON_SERVER = 10000;

    private static final int NUMBER_OF_QUERIES = 1000;

    private static final int NUMBER_OF_SERVERS = 3;

    private List<ServerHolder> serverHolderList;

    private List<DataSegment> segmentQueries;

    private ListeningExecutorService executorService;

    @Test
    public void decisionTest() {
        CachingCostBalancerStrategy cachingCostBalancerStrategy = createCachingCostBalancerStrategy(serverHolderList, executorService);
        CostBalancerStrategy costBalancerStrategy = createCostBalancerStrategy(executorService);
        int notEqual = segmentQueries.stream().mapToInt(( s) -> {
            ServerHolder s1 = cachingCostBalancerStrategy.findNewSegmentHomeBalancer(s, serverHolderList);
            ServerHolder s2 = costBalancerStrategy.findNewSegmentHomeBalancer(s, serverHolderList);
            return s1.getServer().getName().equals(s2.getServer().getName()) ? 0 : 1;
        }).sum();
        Assert.assertTrue(((((double) (notEqual)) / ((double) (segmentQueries.size()))) < 0.01));
    }
}

