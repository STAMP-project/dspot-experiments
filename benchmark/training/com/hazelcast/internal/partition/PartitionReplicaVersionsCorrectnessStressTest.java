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
package com.hazelcast.internal.partition;


import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.nio.Address;
import com.hazelcast.partition.AbstractPartitionLostListenerTest;
import com.hazelcast.test.HazelcastParametersRunnerFactory;
import com.hazelcast.test.annotation.SlowTest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


@RunWith(Parameterized.class)
@Parameterized.UseParametersRunnerFactory(HazelcastParametersRunnerFactory.class)
@Category(SlowTest.class)
public class PartitionReplicaVersionsCorrectnessStressTest extends AbstractPartitionLostListenerTest {
    private static final int ITEM_COUNT_PER_MAP = 10000;

    private static final int SAFE_STATE_TIMEOUT_SECONDS = 300;

    private static final long TEST_TIMEOUT_SECONDS = (PartitionReplicaVersionsCorrectnessStressTest.SAFE_STATE_TIMEOUT_SECONDS) + 120;

    @Parameterized.Parameter(0)
    public int numberOfNodesToStop;

    @Parameterized.Parameter(1)
    public int nodeCount;

    @Parameterized.Parameter(2)
    public AbstractPartitionLostListenerTest.NodeLeaveType nodeLeaveType;

    @Test(timeout = (PartitionReplicaVersionsCorrectnessStressTest.TEST_TIMEOUT_SECONDS) * 1000)
    public void testReplicaVersionsWhenNodesCrashSimultaneously() throws InterruptedException {
        List<HazelcastInstance> instances = getCreatedInstancesShuffledAfterWarmedUp();
        List<HazelcastInstance> instancesCopy = new ArrayList<HazelcastInstance>(instances);
        List<HazelcastInstance> terminatingInstances = instancesCopy.subList(0, numberOfNodesToStop);
        List<HazelcastInstance> survivingInstances = instancesCopy.subList(numberOfNodesToStop, instances.size());
        populateMaps(survivingInstances.get(0));
        String log = (("Surviving: " + survivingInstances) + " Terminating: ") + terminatingInstances;
        Map<Integer, PartitionReplicaVersionsView> replicaVersionsByPartitionId = TestPartitionUtils.getAllReplicaVersions(instances);
        Map<Integer, List<Address>> partitionReplicaAddresses = TestPartitionUtils.getAllReplicaAddresses(instances);
        Map<Integer, Integer> minSurvivingReplicaIndexByPartitionId = getMinReplicaIndicesByPartitionId(survivingInstances);
        stopInstances(terminatingInstances, nodeLeaveType, PartitionReplicaVersionsCorrectnessStressTest.SAFE_STATE_TIMEOUT_SECONDS);
        waitAllForSafeStateAndDumpPartitionServiceOnFailure(survivingInstances, PartitionReplicaVersionsCorrectnessStressTest.SAFE_STATE_TIMEOUT_SECONDS);
        validateReplicaVersions(log, numberOfNodesToStop, survivingInstances, replicaVersionsByPartitionId, partitionReplicaAddresses, minSurvivingReplicaIndexByPartitionId);
    }
}

