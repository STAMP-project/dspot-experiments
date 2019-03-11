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
package org.apache.druid.server.coordinator.rules;


import LoadRule.ASSIGNED_COUNT;
import com.google.common.collect.ImmutableList;
import java.util.ArrayList;
import java.util.List;
import org.apache.druid.java.util.common.DateTimes;
import org.apache.druid.server.coordinator.CoordinatorStats;
import org.apache.druid.server.coordinator.DruidCluster;
import org.apache.druid.server.coordinator.DruidCoordinatorRuntimeParams;
import org.apache.druid.server.coordinator.SegmentReplicantLookup;
import org.apache.druid.server.coordinator.ServerHolder;
import org.apache.druid.timeline.DataSegment;
import org.junit.Assert;
import org.junit.Test;


public class BroadcastDistributionRuleTest {
    private DruidCluster druidCluster;

    private ServerHolder holderOfSmallSegment;

    private List<ServerHolder> holdersOfLargeSegments = new ArrayList<>();

    private List<ServerHolder> holdersOfLargeSegments2 = new ArrayList<>();

    private final List<DataSegment> largeSegments = new ArrayList<>();

    private final List<DataSegment> largeSegments2 = new ArrayList<>();

    private DataSegment smallSegment;

    private DruidCluster secondCluster;

    private ServerHolder generalServer;

    private ServerHolder maintenanceServer2;

    private ServerHolder maintenanceServer1;

    @Test
    public void testBroadcastToSingleDataSource() {
        final ForeverBroadcastDistributionRule rule = new ForeverBroadcastDistributionRule(ImmutableList.of("large_source"));
        CoordinatorStats stats = rule.run(null, DruidCoordinatorRuntimeParams.newBuilder().withDruidCluster(druidCluster).withSegmentReplicantLookup(SegmentReplicantLookup.make(druidCluster)).withBalancerReferenceTimestamp(DateTimes.of("2013-01-01")).withAvailableSegments(smallSegment, largeSegments.get(0), largeSegments.get(1), largeSegments.get(2), largeSegments2.get(0), largeSegments2.get(1)).build(), smallSegment);
        Assert.assertEquals(3L, stats.getGlobalStat(ASSIGNED_COUNT));
        Assert.assertEquals(false, stats.hasPerTierStats());
        Assert.assertTrue(holdersOfLargeSegments.stream().allMatch(( holder) -> holder.getPeon().getSegmentsToLoad().contains(smallSegment)));
        Assert.assertTrue(holdersOfLargeSegments2.stream().noneMatch(( holder) -> holder.getPeon().getSegmentsToLoad().contains(smallSegment)));
        Assert.assertFalse(holderOfSmallSegment.getPeon().getSegmentsToLoad().contains(smallSegment));
    }

    /**
     * Servers:
     * name         | segments
     * -------------+--------------
     * general      | large segment
     * maintenance1 | small segment
     * maintenance2 | large segment
     *
     * After running the rule for the small segment:
     * general      | large & small segments
     * maintenance1 |
     * maintenance2 | large segment
     */
    @Test
    public void testBroadcastWithMaintenance() {
        final ForeverBroadcastDistributionRule rule = new ForeverBroadcastDistributionRule(ImmutableList.of("large_source"));
        CoordinatorStats stats = rule.run(null, DruidCoordinatorRuntimeParams.newBuilder().withDruidCluster(secondCluster).withSegmentReplicantLookup(SegmentReplicantLookup.make(secondCluster)).withBalancerReferenceTimestamp(DateTimes.of("2013-01-01")).withAvailableSegments(smallSegment, largeSegments.get(0), largeSegments.get(1)).build(), smallSegment);
        Assert.assertEquals(1L, stats.getGlobalStat(ASSIGNED_COUNT));
        Assert.assertEquals(false, stats.hasPerTierStats());
        Assert.assertEquals(1, generalServer.getPeon().getSegmentsToLoad().size());
        Assert.assertEquals(1, maintenanceServer1.getPeon().getSegmentsToDrop().size());
        Assert.assertEquals(0, maintenanceServer2.getPeon().getSegmentsToLoad().size());
    }

    @Test
    public void testBroadcastToMultipleDataSources() {
        final ForeverBroadcastDistributionRule rule = new ForeverBroadcastDistributionRule(ImmutableList.of("large_source", "large_source2"));
        CoordinatorStats stats = rule.run(null, DruidCoordinatorRuntimeParams.newBuilder().withDruidCluster(druidCluster).withSegmentReplicantLookup(SegmentReplicantLookup.make(druidCluster)).withBalancerReferenceTimestamp(DateTimes.of("2013-01-01")).withAvailableSegments(smallSegment, largeSegments.get(0), largeSegments.get(1), largeSegments.get(2), largeSegments2.get(0), largeSegments2.get(1)).build(), smallSegment);
        Assert.assertEquals(5L, stats.getGlobalStat(ASSIGNED_COUNT));
        Assert.assertEquals(false, stats.hasPerTierStats());
        Assert.assertTrue(holdersOfLargeSegments.stream().allMatch(( holder) -> holder.getPeon().getSegmentsToLoad().contains(smallSegment)));
        Assert.assertTrue(holdersOfLargeSegments2.stream().allMatch(( holder) -> holder.getPeon().getSegmentsToLoad().contains(smallSegment)));
        Assert.assertFalse(holderOfSmallSegment.getPeon().getSegmentsToLoad().contains(smallSegment));
    }

    @Test
    public void testBroadcastToAllServers() {
        final ForeverBroadcastDistributionRule rule = new ForeverBroadcastDistributionRule(null);
        CoordinatorStats stats = rule.run(null, DruidCoordinatorRuntimeParams.newBuilder().withDruidCluster(druidCluster).withSegmentReplicantLookup(SegmentReplicantLookup.make(druidCluster)).withBalancerReferenceTimestamp(DateTimes.of("2013-01-01")).withAvailableSegments(smallSegment, largeSegments.get(0), largeSegments.get(1), largeSegments.get(2), largeSegments2.get(0), largeSegments2.get(1)).build(), smallSegment);
        Assert.assertEquals(6L, stats.getGlobalStat(ASSIGNED_COUNT));
        Assert.assertEquals(false, stats.hasPerTierStats());
        Assert.assertTrue(druidCluster.getAllServers().stream().allMatch(( holder) -> holder.getPeon().getSegmentsToLoad().contains(smallSegment)));
    }
}

