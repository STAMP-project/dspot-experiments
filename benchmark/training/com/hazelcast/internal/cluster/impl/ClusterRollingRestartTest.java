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
package com.hazelcast.internal.cluster.impl;


import ClusterState.ACTIVE;
import com.hazelcast.cluster.ClusterState;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.nio.Address;
import com.hazelcast.test.HazelcastParametersRunnerFactory;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.TestHazelcastInstanceFactory;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


@RunWith(Parameterized.class)
@Parameterized.UseParametersRunnerFactory(HazelcastParametersRunnerFactory.class)
@Category({ QuickTest.class, ParallelTest.class })
public class ClusterRollingRestartTest extends HazelcastTestSupport {
    @Parameterized.Parameter(0)
    public ClusterState clusterState;

    @Parameterized.Parameter(1)
    public ClusterRollingRestartTest.PartitionAssignmentType partitionAssignmentType;

    @Test
    public void test_rollingRestart() {
        final TestHazelcastInstanceFactory factory = createHazelcastInstanceFactory();
        final int nodeCount = 3;
        final HazelcastInstance[] instances = new HazelcastInstance[nodeCount];
        instances[0] = factory.newHazelcastInstance();
        if ((partitionAssignmentType) == (ClusterRollingRestartTest.PartitionAssignmentType.DURING_STARTUP)) {
            HazelcastTestSupport.warmUpPartitions(instances[0]);
        }
        for (int i = 1; i < nodeCount; i++) {
            instances[i] = factory.newHazelcastInstance();
        }
        if ((partitionAssignmentType) == (ClusterRollingRestartTest.PartitionAssignmentType.AT_THE_END)) {
            HazelcastTestSupport.warmUpPartitions(instances);
        }
        AdvancedClusterStateTest.changeClusterStateEventually(instances[0], clusterState);
        Address address = HazelcastTestSupport.getNode(instances[0]).getThisAddress();
        instances[0].shutdown();
        instances[0] = factory.newHazelcastInstance(address);
        for (HazelcastInstance instance : instances) {
            HazelcastTestSupport.assertClusterSizeEventually(nodeCount, instance);
            HazelcastTestSupport.assertClusterState(clusterState, instance);
        }
        AdvancedClusterStateTest.changeClusterStateEventually(instances[0], ACTIVE);
    }

    private enum PartitionAssignmentType {

        NEVER,
        DURING_STARTUP,
        AT_THE_END;}
}

