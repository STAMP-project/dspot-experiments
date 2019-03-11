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
package io.crate.execution.jobs;


import DistributionInfo.DEFAULT_SAME_NODE;
import com.carrotsearch.hppc.IntArrayList;
import com.google.common.collect.Lists;
import io.crate.execution.dsl.phases.ExecutionPhase;
import io.crate.execution.dsl.phases.NodeOperation;
import io.crate.planner.distribution.DistributionInfo;
import io.crate.test.integration.CrateUnitTest;
import io.crate.testing.StubPhases;
import java.util.Collections;
import java.util.stream.StreamSupport;
import org.hamcrest.Matchers;
import org.junit.Test;


public class NodeOperationCtxTest extends CrateUnitTest {
    @Test
    public void testFindLeafs() {
        ExecutionPhase p1 = StubPhases.newPhase(0, "n1", "n2");
        ExecutionPhase p2 = StubPhases.newPhase(1, "n1", "n2");
        ExecutionPhase p3 = StubPhases.newPhase(2, "n2");
        JobSetup.NodeOperationCtx opCtx = new JobSetup.NodeOperationCtx("n1", Lists.newArrayList(NodeOperation.withDownstream(p1, p2, ((byte) (0))), NodeOperation.withDownstream(p2, p3, ((byte) (0)))));
        assertThat(opCtx.findLeafs(), Matchers.is(IntArrayList.from(2)));
    }

    @Test
    public void testFindLeafWithNodeOperationsThatHaveNoLeaf() {
        ExecutionPhase p1 = StubPhases.newPhase(0, "n1");
        JobSetup.NodeOperationCtx opCtx = new JobSetup.NodeOperationCtx("n1", Collections.singletonList(NodeOperation.withDownstream(p1, p1, ((byte) (0)))));
        assertThat(StreamSupport.stream(opCtx.findLeafs().spliterator(), false).count(), Matchers.is(0L));
    }

    @Test
    public void testIsUpstreamOnSameNodeWithSameNodeOptimization() {
        ExecutionPhase p1 = StubPhases.newUpstreamPhase(0, DistributionInfo.DEFAULT_BROADCAST, "n1");
        ExecutionPhase p2 = StubPhases.newPhase(1, "n1");
        JobSetup.NodeOperationCtx opCtx = new JobSetup.NodeOperationCtx("n1", Collections.singletonList(NodeOperation.withDownstream(p1, p2, ((byte) (0)))));
        // withDownstream set DistributionInfo to SAME_NODE because both phases are on n1
        assertThat(opCtx.upstreamsAreOnSameNode(1), Matchers.is(true));
    }

    @Test
    public void testIsUpstreamOnSameNodeWithUpstreamOnOtherNode() {
        ExecutionPhase p1 = StubPhases.newUpstreamPhase(0, DistributionInfo.DEFAULT_BROADCAST, "n2");
        ExecutionPhase p2 = StubPhases.newPhase(1, "n1");
        JobSetup.NodeOperationCtx opCtx = new JobSetup.NodeOperationCtx("n1", Collections.singletonList(NodeOperation.withDownstream(p1, p2, ((byte) (0)))));
        assertThat(opCtx.upstreamsAreOnSameNode(1), Matchers.is(false));
    }

    @Test
    public void testIsUpstreamOnSameNodeWithTwoUpstreamsThatAreOnTheSameNode() {
        ExecutionPhase p1 = StubPhases.newUpstreamPhase(0, DEFAULT_SAME_NODE, "n2");
        ExecutionPhase p2 = StubPhases.newUpstreamPhase(2, DEFAULT_SAME_NODE, "n2");
        ExecutionPhase p3 = StubPhases.newPhase(3, "n2");
        JobSetup.NodeOperationCtx opCtx = new JobSetup.NodeOperationCtx("n1", Lists.newArrayList(NodeOperation.withDownstream(p1, p3, ((byte) (0))), NodeOperation.withDownstream(p2, p3, ((byte) (0)))));
        assertThat(opCtx.upstreamsAreOnSameNode(3), Matchers.is(true));
    }
}

