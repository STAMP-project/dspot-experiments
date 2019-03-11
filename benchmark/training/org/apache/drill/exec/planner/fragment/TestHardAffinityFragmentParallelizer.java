/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.drill.exec.planner.fragment;


import java.util.Collections;
import java.util.List;
import org.apache.drill.categories.PlannerTest;
import org.apache.drill.exec.ExecConstants;
import org.apache.drill.exec.proto.CoordinationProtos.DrillbitEndpoint;
import org.apache.drill.shaded.guava.com.google.common.collect.HashMultiset;
import org.apache.drill.shaded.guava.com.google.common.collect.ImmutableList;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;


@Category(PlannerTest.class)
public class TestHardAffinityFragmentParallelizer {
    // Create a set of test endpoints
    private static final DrillbitEndpoint N1_EP1 = TestHardAffinityFragmentParallelizer.newDrillbitEndpoint("node1", 30010);

    private static final DrillbitEndpoint N1_EP2 = TestHardAffinityFragmentParallelizer.newDrillbitEndpoint("node1", 30011);

    private static final DrillbitEndpoint N2_EP1 = TestHardAffinityFragmentParallelizer.newDrillbitEndpoint("node2", 30010);

    private static final DrillbitEndpoint N2_EP2 = TestHardAffinityFragmentParallelizer.newDrillbitEndpoint("node2", 30011);

    private static final DrillbitEndpoint N3_EP1 = TestHardAffinityFragmentParallelizer.newDrillbitEndpoint("node3", 30010);

    private static final DrillbitEndpoint N3_EP2 = TestHardAffinityFragmentParallelizer.newDrillbitEndpoint("node3", 30011);

    private static final DrillbitEndpoint N4_EP2 = TestHardAffinityFragmentParallelizer.newDrillbitEndpoint("node4", 30011);

    @Test
    public void simpleCase1() throws Exception {
        final Wrapper wrapper = newWrapper(200, 1, 20, Collections.singletonList(new org.apache.drill.exec.physical.EndpointAffinity(TestHardAffinityFragmentParallelizer.N1_EP1, 1.0, true, Integer.MAX_VALUE)));
        HardAffinityFragmentParallelizer.INSTANCE.parallelizeFragment(wrapper, TestHardAffinityFragmentParallelizer.newParameters(ExecConstants.SLICE_TARGET_DEFAULT, 5, 20), null);
        // Expect the fragment parallelization to be just one because:
        // The cost (200) is below the threshold (SLICE_TARGET_DEFAULT) (which gives width of 200/10000 = ~1) and
        Assert.assertEquals(1, wrapper.getWidth());
        final List<DrillbitEndpoint> assignedEps = wrapper.getAssignedEndpoints();
        Assert.assertEquals(1, assignedEps.size());
        Assert.assertEquals(TestHardAffinityFragmentParallelizer.N1_EP1, assignedEps.get(0));
    }

    @Test
    public void simpleCase2() throws Exception {
        // Set the slice target to 1
        final Wrapper wrapper = newWrapper(200, 1, 20, Collections.singletonList(new org.apache.drill.exec.physical.EndpointAffinity(TestHardAffinityFragmentParallelizer.N1_EP1, 1.0, true, Integer.MAX_VALUE)));
        HardAffinityFragmentParallelizer.INSTANCE.parallelizeFragment(wrapper, TestHardAffinityFragmentParallelizer.newParameters(1, 5, 20), null);
        // Expect the fragment parallelization to be 5:
        // 1. the cost (200) is above the threshold (SLICE_TARGET_DEFAULT) (which gives 200/1=200 width) and
        // 2. Max width per node is 5 (limits the width 200 to 5)
        Assert.assertEquals(5, wrapper.getWidth());
        final List<DrillbitEndpoint> assignedEps = wrapper.getAssignedEndpoints();
        Assert.assertEquals(5, assignedEps.size());
        for (DrillbitEndpoint ep : assignedEps) {
            Assert.assertEquals(TestHardAffinityFragmentParallelizer.N1_EP1, ep);
        }
    }

    @Test
    public void multiNodeCluster1() throws Exception {
        final Wrapper wrapper = newWrapper(200, 1, 20, ImmutableList.of(new org.apache.drill.exec.physical.EndpointAffinity(TestHardAffinityFragmentParallelizer.N1_EP1, 0.15, true, Integer.MAX_VALUE), new org.apache.drill.exec.physical.EndpointAffinity(TestHardAffinityFragmentParallelizer.N1_EP2, 0.15, true, Integer.MAX_VALUE), new org.apache.drill.exec.physical.EndpointAffinity(TestHardAffinityFragmentParallelizer.N2_EP1, 0.1, true, Integer.MAX_VALUE), new org.apache.drill.exec.physical.EndpointAffinity(TestHardAffinityFragmentParallelizer.N3_EP2, 0.2, true, Integer.MAX_VALUE), new org.apache.drill.exec.physical.EndpointAffinity(TestHardAffinityFragmentParallelizer.N4_EP2, 0.2, true, Integer.MAX_VALUE)));
        HardAffinityFragmentParallelizer.INSTANCE.parallelizeFragment(wrapper, TestHardAffinityFragmentParallelizer.newParameters(ExecConstants.SLICE_TARGET_DEFAULT, 5, 20), null);
        // Expect the fragment parallelization to be 5 because:
        // 1. The cost (200) is below the threshold (SLICE_TARGET_DEFAULT) (which gives width of 200/10000 = ~1) and
        // 2. Number of mandoatory node assignments are 5 which overrides the cost based width of 1.
        Assert.assertEquals(5, wrapper.getWidth());
        // As there are 5 required eps and the width is 5, everyone gets assigned 1.
        final List<DrillbitEndpoint> assignedEps = wrapper.getAssignedEndpoints();
        Assert.assertEquals(5, assignedEps.size());
        Assert.assertTrue(assignedEps.contains(TestHardAffinityFragmentParallelizer.N1_EP1));
        Assert.assertTrue(assignedEps.contains(TestHardAffinityFragmentParallelizer.N1_EP2));
        Assert.assertTrue(assignedEps.contains(TestHardAffinityFragmentParallelizer.N2_EP1));
        Assert.assertTrue(assignedEps.contains(TestHardAffinityFragmentParallelizer.N3_EP2));
        Assert.assertTrue(assignedEps.contains(TestHardAffinityFragmentParallelizer.N4_EP2));
    }

    @Test
    public void multiNodeCluster2() throws Exception {
        final Wrapper wrapper = newWrapper(200, 1, 20, ImmutableList.of(new org.apache.drill.exec.physical.EndpointAffinity(TestHardAffinityFragmentParallelizer.N1_EP2, 0.15, true, Integer.MAX_VALUE), new org.apache.drill.exec.physical.EndpointAffinity(TestHardAffinityFragmentParallelizer.N2_EP2, 0.15, true, Integer.MAX_VALUE), new org.apache.drill.exec.physical.EndpointAffinity(TestHardAffinityFragmentParallelizer.N3_EP1, 0.1, true, Integer.MAX_VALUE), new org.apache.drill.exec.physical.EndpointAffinity(TestHardAffinityFragmentParallelizer.N4_EP2, 0.2, true, Integer.MAX_VALUE), new org.apache.drill.exec.physical.EndpointAffinity(TestHardAffinityFragmentParallelizer.N1_EP1, 0.2, true, Integer.MAX_VALUE)));
        HardAffinityFragmentParallelizer.INSTANCE.parallelizeFragment(wrapper, TestHardAffinityFragmentParallelizer.newParameters(1, 5, 20), null);
        // Expect the fragment parallelization to be 20 because:
        // 1. the cost (200) is above the threshold (SLICE_TARGET_DEFAULT) (which gives 200/1=200 width) and
        // 2. Number of mandatory node assignments are 5 (current width 200 satisfies the requirement)
        // 3. max fragment width is 20 which limits the width
        Assert.assertEquals(20, wrapper.getWidth());
        final List<DrillbitEndpoint> assignedEps = wrapper.getAssignedEndpoints();
        Assert.assertEquals(20, assignedEps.size());
        final HashMultiset<DrillbitEndpoint> counts = HashMultiset.create();
        for (final DrillbitEndpoint ep : assignedEps) {
            counts.add(ep);
        }
        // Each node gets at max 5.
        Assert.assertTrue(((counts.count(TestHardAffinityFragmentParallelizer.N1_EP2)) <= 5));
        Assert.assertTrue(((counts.count(TestHardAffinityFragmentParallelizer.N2_EP2)) <= 5));
        Assert.assertTrue(((counts.count(TestHardAffinityFragmentParallelizer.N3_EP1)) <= 5));
        Assert.assertTrue(((counts.count(TestHardAffinityFragmentParallelizer.N4_EP2)) <= 5));
        Assert.assertTrue(((counts.count(TestHardAffinityFragmentParallelizer.N1_EP1)) <= 5));
    }

    @Test
    public void multiNodeClusterNegative1() throws Exception {
        final Wrapper wrapper = newWrapper(200, 1, 20, ImmutableList.of(new org.apache.drill.exec.physical.EndpointAffinity(TestHardAffinityFragmentParallelizer.N1_EP2, 0.15, true, Integer.MAX_VALUE), new org.apache.drill.exec.physical.EndpointAffinity(TestHardAffinityFragmentParallelizer.N2_EP2, 0.15, true, Integer.MAX_VALUE), new org.apache.drill.exec.physical.EndpointAffinity(TestHardAffinityFragmentParallelizer.N3_EP1, 0.1, true, Integer.MAX_VALUE), new org.apache.drill.exec.physical.EndpointAffinity(TestHardAffinityFragmentParallelizer.N4_EP2, 0.2, true, Integer.MAX_VALUE), new org.apache.drill.exec.physical.EndpointAffinity(TestHardAffinityFragmentParallelizer.N1_EP1, 0.2, true, Integer.MAX_VALUE)));
        try {
            HardAffinityFragmentParallelizer.INSTANCE.parallelizeFragment(wrapper, TestHardAffinityFragmentParallelizer.newParameters(1, 2, 2), null);
            Assert.fail("Expected an exception, because max global query width (2) is less than the number of mandatory nodes (5)");
        } catch (Exception e) {
            // ok
        }
    }

    @Test
    public void multiNodeClusterNegative2() throws Exception {
        final Wrapper wrapper = newWrapper(200, 1, 3, ImmutableList.of(new org.apache.drill.exec.physical.EndpointAffinity(TestHardAffinityFragmentParallelizer.N1_EP2, 0.15, true, Integer.MAX_VALUE), new org.apache.drill.exec.physical.EndpointAffinity(TestHardAffinityFragmentParallelizer.N2_EP2, 0.15, true, Integer.MAX_VALUE), new org.apache.drill.exec.physical.EndpointAffinity(TestHardAffinityFragmentParallelizer.N3_EP1, 0.1, true, Integer.MAX_VALUE), new org.apache.drill.exec.physical.EndpointAffinity(TestHardAffinityFragmentParallelizer.N4_EP2, 0.2, true, Integer.MAX_VALUE), new org.apache.drill.exec.physical.EndpointAffinity(TestHardAffinityFragmentParallelizer.N1_EP1, 0.2, true, Integer.MAX_VALUE)));
        try {
            HardAffinityFragmentParallelizer.INSTANCE.parallelizeFragment(wrapper, TestHardAffinityFragmentParallelizer.newParameters(1, 2, 2), null);
            Assert.fail("Expected an exception, because max fragment width (3) is less than the number of mandatory nodes (5)");
        } catch (Exception e) {
            // ok
        }
    }
}

