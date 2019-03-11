/**
 * Licensed to Crate under one or more contributor license agreements.
 * See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership.  Crate licenses this file
 * to you under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License.  You may
 * obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied.  See the License for the specific language governing
 * permissions and limitations under the License.
 *
 * However, if you have executed another commercial license agreement
 * with Crate these terms will supersede the license and you may use the
 * software solely pursuant to the terms of the relevant commercial
 * agreement.
 */
package io.crate.cluster.gracefulstop;


import Decision.Type.NO;
import Decision.Type.YES;
import DecommissioningService.GRACEFUL_STOP_MIN_AVAILABILITY_SETTING;
import io.crate.test.integration.CrateDummyClusterServiceUnitTest;
import org.elasticsearch.cluster.routing.RoutingNode;
import org.elasticsearch.cluster.routing.ShardRouting;
import org.elasticsearch.cluster.routing.allocation.RoutingAllocation;
import org.elasticsearch.cluster.routing.allocation.decider.Decision;
import org.elasticsearch.common.settings.Settings;
import org.hamcrest.Matchers;
import org.junit.Test;

import static DecommissioningService.DECOMMISSION_PREFIX;


public class DecommissionAllocationDeciderTest extends CrateDummyClusterServiceUnitTest {
    private RoutingAllocation routingAllocation;

    private ShardRouting primaryShard;

    private ShardRouting replicaShard;

    private RoutingNode n1;

    private RoutingNode n2;

    @Test
    public void testShouldNotBeAbleToAllocatePrimaryOntoDecommissionedNode() throws Exception {
        Settings settings = Settings.builder().put(((DECOMMISSION_PREFIX) + "n1"), true).build();
        DecommissionAllocationDecider allocationDecider = new DecommissionAllocationDecider(settings, clusterService.getClusterSettings());
        Decision decision = allocationDecider.canAllocate(primaryShard, n1, routingAllocation);
        assertThat(decision.type(), Matchers.is(NO));
        decision = allocationDecider.canAllocate(primaryShard, n2, routingAllocation);
        assertThat(decision.type(), Matchers.is(YES));
    }

    @Test
    public void testCanAlwaysAllocateIfDataAvailabilityIsNone() throws Exception {
        Settings settings = Settings.builder().put(GRACEFUL_STOP_MIN_AVAILABILITY_SETTING.getKey(), "none").put(((DECOMMISSION_PREFIX) + "n1"), true).build();
        DecommissionAllocationDecider allocationDecider = new DecommissionAllocationDecider(settings, clusterService.getClusterSettings());
        Decision decision = allocationDecider.canAllocate(primaryShard, n1, routingAllocation);
        assertThat(decision.type(), Matchers.is(YES));
        decision = allocationDecider.canAllocate(primaryShard, n2, routingAllocation);
        assertThat(decision.type(), Matchers.is(YES));
    }

    @Test
    public void testReplicasCanRemainButCannotAllocateOnDecommissionedNodeWithPrimariesDataAvailability() throws Exception {
        Settings settings = Settings.builder().put(GRACEFUL_STOP_MIN_AVAILABILITY_SETTING.getKey(), "primaries").put(((DECOMMISSION_PREFIX) + "n1"), true).build();
        DecommissionAllocationDecider allocationDecider = new DecommissionAllocationDecider(settings, clusterService.getClusterSettings());
        Decision decision = allocationDecider.canAllocate(replicaShard, n1, routingAllocation);
        assertThat(decision.type(), Matchers.is(NO));
        decision = allocationDecider.canRemain(replicaShard, n1, routingAllocation);
        assertThat(decision.type(), Matchers.is(YES));
    }

    @Test
    public void testCannotAllocatePrimaryOrReplicaIfDataAvailabilityIsFull() throws Exception {
        Settings settings = Settings.builder().put(GRACEFUL_STOP_MIN_AVAILABILITY_SETTING.getKey(), "full").put(((DECOMMISSION_PREFIX) + "n1"), true).build();
        DecommissionAllocationDecider allocationDecider = new DecommissionAllocationDecider(settings, clusterService.getClusterSettings());
        Decision decision = allocationDecider.canAllocate(replicaShard, n1, routingAllocation);
        assertThat(decision.type(), Matchers.is(NO));
        decision = allocationDecider.canRemain(replicaShard, n1, routingAllocation);
        assertThat(decision.type(), Matchers.is(NO));
        decision = allocationDecider.canAllocate(primaryShard, n1, routingAllocation);
        assertThat(decision.type(), Matchers.is(NO));
        decision = allocationDecider.canRemain(primaryShard, n1, routingAllocation);
        assertThat(decision.type(), Matchers.is(NO));
    }

    @Test
    public void testDecommissionSettingsAreUpdated() {
        DecommissionAllocationDecider allocationDecider = new DecommissionAllocationDecider(Settings.EMPTY, clusterService.getClusterSettings());
        Settings settings = Settings.builder().put(GRACEFUL_STOP_MIN_AVAILABILITY_SETTING.getKey(), "full").put(((DECOMMISSION_PREFIX) + "n1"), true).build();
        clusterService.getClusterSettings().applySettings(settings);
        Decision decision = allocationDecider.canAllocate(primaryShard, n1, routingAllocation);
        assertThat(decision.type(), Matchers.is(NO));
    }
}

