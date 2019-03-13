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
package com.hazelcast.map;


import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.partition.AbstractPartitionLostListenerTest;
import com.hazelcast.test.AssertTask;
import com.hazelcast.test.HazelcastParametersRunnerFactory;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.SlowTest;
import java.util.ArrayList;
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
public class MapPartitionLostListenerStressTest extends AbstractPartitionLostListenerTest {
    @Parameterized.Parameter(0)
    public int numberOfNodesToCrash;

    @Parameterized.Parameter(1)
    public boolean withData;

    @Parameterized.Parameter(2)
    public AbstractPartitionLostListenerTest.NodeLeaveType nodeLeaveType;

    @Parameterized.Parameter(3)
    public boolean shouldExpectPartitionLostEvents;

    @Test
    public void testMapPartitionLostListener() throws InterruptedException {
        List<HazelcastInstance> instances = getCreatedInstancesShuffledAfterWarmedUp();
        List<HazelcastInstance> survivingInstances = new ArrayList<HazelcastInstance>(instances);
        List<HazelcastInstance> terminatingInstances = survivingInstances.subList(0, numberOfNodesToCrash);
        survivingInstances = survivingInstances.subList(numberOfNodesToCrash, instances.size());
        List<TestEventCollectingMapPartitionLostListener> listeners = registerListeners(survivingInstances.get(0));
        if (withData) {
            populateMaps(survivingInstances.get(0));
        }
        String log = (("Surviving: " + survivingInstances) + " Terminating: ") + terminatingInstances;
        Map<Integer, Integer> survivingPartitions = getMinReplicaIndicesByPartitionId(survivingInstances);
        stopInstances(terminatingInstances, nodeLeaveType);
        waitAllForSafeStateAndDumpPartitionServiceOnFailure(survivingInstances, 300);
        if (shouldExpectPartitionLostEvents) {
            for (int i = 0; i < (getNodeCount()); i++) {
                MapPartitionLostListenerStressTest.assertListenerInvocationsEventually(log, i, numberOfNodesToCrash, listeners.get(i), survivingPartitions);
            }
        } else {
            for (final TestEventCollectingMapPartitionLostListener listener : listeners) {
                HazelcastTestSupport.assertTrueAllTheTime(new AssertTask() {
                    @Override
                    public void run() throws Exception {
                        Assert.assertTrue(listener.getEvents().isEmpty());
                    }
                }, 1);
            }
        }
    }
}

