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
package com.hazelcast.partition;


import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.internal.partition.TestPartitionUtils;
import com.hazelcast.nio.Address;
import com.hazelcast.test.AssertTask;
import com.hazelcast.test.HazelcastParametersRunnerFactory;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.SlowTest;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


@RunWith(Parameterized.class)
@Parameterized.UseParametersRunnerFactory(HazelcastParametersRunnerFactory.class)
@Category(SlowTest.class)
public class PartitionLostListenerStressTest extends AbstractPartitionLostListenerTest {
    @Parameterized.Parameter(0)
    public int numberOfNodesToCrash;

    @Parameterized.Parameter(1)
    public boolean withData;

    @Parameterized.Parameter(2)
    public AbstractPartitionLostListenerTest.NodeLeaveType nodeLeaveType;

    @Parameterized.Parameter(3)
    public boolean shouldExpectPartitionLostEvents;

    @Test
    public void testPartitionLostListener() {
        List<HazelcastInstance> instances = getCreatedInstancesShuffledAfterWarmedUp();
        List<HazelcastInstance> survivingInstances = new ArrayList<HazelcastInstance>(instances);
        List<HazelcastInstance> terminatingInstances = survivingInstances.subList(0, numberOfNodesToCrash);
        survivingInstances = survivingInstances.subList(numberOfNodesToCrash, instances.size());
        if (withData) {
            populateMaps(survivingInstances.get(0));
        }
        final String log = (("Surviving: " + survivingInstances) + " Terminating: ") + terminatingInstances;
        final PartitionLostListenerStressTest.EventCollectingPartitionLostListener listener = registerPartitionLostListener(survivingInstances.get(0));
        final Map<Integer, Integer> survivingPartitions = getMinReplicaIndicesByPartitionId(survivingInstances);
        final Map<Integer, List<Address>> partitionTables = TestPartitionUtils.getAllReplicaAddresses(survivingInstances);
        stopInstances(terminatingInstances, nodeLeaveType);
        waitAllForSafeStateAndDumpPartitionServiceOnFailure(survivingInstances, 300);
        if (shouldExpectPartitionLostEvents) {
            HazelcastTestSupport.assertTrueEventually(new AssertTask() {
                @Override
                public void run() {
                    assertLostPartitions(log, listener, survivingPartitions, partitionTables);
                }
            });
        } else {
            HazelcastTestSupport.assertTrueAllTheTime(new AssertTask() {
                @Override
                public void run() {
                    Assert.assertTrue(listener.getEvents().isEmpty());
                }
            }, 1);
        }
    }

    @Test
    public void test_partitionLostListenerNotInvoked_whenNewNodesJoin() {
        HazelcastInstance master = createInstances(1).get(0);
        PartitionLostListenerStressTest.EventCollectingPartitionLostListener listener = registerPartitionLostListener(master);
        List<HazelcastInstance> others = createInstances(((getNodeCount()) - 1));
        HazelcastTestSupport.waitAllForSafeState(Collections.singletonList(master));
        HazelcastTestSupport.waitAllForSafeState(others);
        Assert.assertTrue("No invocation to PartitionLostListener when new nodes join to cluster", listener.getEvents().isEmpty());
    }

    public static class EventCollectingPartitionLostListener implements PartitionLostListener {
        private List<PartitionLostEvent> lostPartitions = new ArrayList<PartitionLostEvent>();

        @Override
        public synchronized void partitionLost(PartitionLostEvent event) {
            lostPartitions.add(event);
        }

        public synchronized List<PartitionLostEvent> getEvents() {
            return new ArrayList<PartitionLostEvent>(lostPartitions);
        }

        public synchronized void clear() {
            lostPartitions.clear();
        }
    }
}

