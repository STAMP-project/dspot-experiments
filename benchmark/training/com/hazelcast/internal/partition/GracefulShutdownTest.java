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


import ClusterState.FROZEN;
import ClusterState.PASSIVE;
import GroupProperty.PARTITION_COUNT;
import GroupProperty.PARTITION_MIGRATION_INTERVAL;
import com.hazelcast.config.Config;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.test.HazelcastSerialClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.TestHazelcastInstanceFactory;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import com.hazelcast.util.RandomPicker;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


/* When executed with HazelcastParallelClassRunner, this test creates a massive amount of threads (peaks of 1000 threads
and 800 daemon threads). As comparison the BasicMapTest creates about 700 threads and 25 daemon threads.
This regularly results in test failures when multiple PR builders run in parallel due to a resource starvation.

Countermeasures are to remove the ParallelTest annotation or to use the HazelcastSerialClassRunner.

Without ParallelTest we'll add the whole test duration to the PR builder time (about 25 seconds) and still create the
resource usage peak, which may have a negative impact on parallel PR builder runs on the same host machine.

With HazelcastSerialClassRunner the test takes over 3 minutes, but with a maximum of 200 threads and 160 daemon threads.
This should have less impact on other tests and the total duration of the PR build (since the test will still be executed
in parallel to others).
 */
@RunWith(HazelcastSerialClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class GracefulShutdownTest extends HazelcastTestSupport {
    private TestHazelcastInstanceFactory factory;

    @Test
    public void shutdownSingleMember_withoutPartitionInitialization() {
        HazelcastInstance hz = factory.newHazelcastInstance();
        hz.shutdown();
    }

    @Test
    public void shutdownSingleMember_withPartitionInitialization() {
        HazelcastInstance hz = factory.newHazelcastInstance();
        HazelcastTestSupport.warmUpPartitions(hz);
        hz.shutdown();
    }

    @Test
    public void shutdownSingleLiteMember() {
        HazelcastInstance hz = factory.newHazelcastInstance(new Config().setLiteMember(true));
        hz.shutdown();
    }

    @Test
    @SuppressWarnings("unused")
    public void shutdownSlaveMember_withoutPartitionInitialization() {
        HazelcastInstance hz1 = factory.newHazelcastInstance();
        HazelcastInstance hz2 = factory.newHazelcastInstance();
        HazelcastInstance hz3 = factory.newHazelcastInstance();
        hz2.shutdown();
    }

    @Test
    public void shutdownSlaveMember_withPartitionInitialization() {
        HazelcastInstance hz1 = factory.newHazelcastInstance();
        HazelcastInstance hz2 = factory.newHazelcastInstance();
        HazelcastInstance hz3 = factory.newHazelcastInstance();
        HazelcastTestSupport.warmUpPartitions(hz1, hz2, hz3);
        hz2.shutdown();
        AbstractPartitionAssignmentsCorrectnessTest.assertPartitionAssignmentsEventually(factory);
    }

    @Test
    @SuppressWarnings("unused")
    public void shutdownSlaveMember_whilePartitionsMigrating() {
        Config config = new Config().setProperty(PARTITION_COUNT.getName(), "12").setProperty(PARTITION_MIGRATION_INTERVAL.getName(), "1");
        HazelcastInstance hz1 = factory.newHazelcastInstance(config);
        HazelcastTestSupport.warmUpPartitions(hz1);
        HazelcastInstance hz2 = factory.newHazelcastInstance(config);
        HazelcastInstance hz3 = factory.newHazelcastInstance(config);
        hz2.shutdown();
        AbstractPartitionAssignmentsCorrectnessTest.assertPartitionAssignmentsEventually(factory);
    }

    @Test
    public void shutdownSlaveLiteMember() {
        HazelcastInstance hz1 = factory.newHazelcastInstance();
        HazelcastInstance hz2 = factory.newHazelcastInstance(new Config().setLiteMember(true));
        HazelcastInstance hz3 = factory.newHazelcastInstance();
        HazelcastTestSupport.warmUpPartitions(hz1, hz2, hz3);
        hz2.shutdown();
        PartitionCorrectnessTestSupport.assertPartitionAssignments(factory);
    }

    @Test
    @SuppressWarnings("unused")
    public void shutdownMasterMember_withoutPartitionInitialization() {
        HazelcastInstance hz1 = factory.newHazelcastInstance();
        HazelcastInstance hz2 = factory.newHazelcastInstance();
        HazelcastInstance hz3 = factory.newHazelcastInstance();
        hz1.shutdown();
    }

    @Test
    public void shutdownMasterMember_withPartitionInitialization() {
        HazelcastInstance hz1 = factory.newHazelcastInstance();
        HazelcastInstance hz2 = factory.newHazelcastInstance();
        HazelcastInstance hz3 = factory.newHazelcastInstance();
        HazelcastTestSupport.warmUpPartitions(hz1, hz2, hz3);
        hz1.shutdown();
        AbstractPartitionAssignmentsCorrectnessTest.assertPartitionAssignmentsEventually(factory);
    }

    @Test
    @SuppressWarnings("unused")
    public void shutdownMasterMember_whilePartitionsMigrating() {
        Config config = GracefulShutdownTest.newConfig();
        HazelcastInstance hz1 = factory.newHazelcastInstance(config);
        HazelcastTestSupport.warmUpPartitions(hz1);
        HazelcastInstance hz2 = factory.newHazelcastInstance(config);
        HazelcastInstance hz3 = factory.newHazelcastInstance(config);
        hz1.shutdown();
        AbstractPartitionAssignmentsCorrectnessTest.assertPartitionAssignmentsEventually(factory);
    }

    @Test
    public void shutdownMasterLiteMember() {
        HazelcastInstance hz1 = factory.newHazelcastInstance(new Config().setLiteMember(true));
        HazelcastInstance hz2 = factory.newHazelcastInstance();
        HazelcastInstance hz3 = factory.newHazelcastInstance();
        HazelcastTestSupport.warmUpPartitions(hz1, hz2, hz3);
        hz1.shutdown();
        PartitionCorrectnessTestSupport.assertPartitionAssignments(factory);
    }

    @Test
    public void shutdownAllMembers_withoutPartitionInitialization() {
        shutdownAllMembers(false);
    }

    @Test
    public void shutdownAllMembers_withPartitionInitialization() {
        shutdownAllMembers(true);
    }

    @Test
    public void shutdownMultipleSlaveMembers_withoutPartitionInitialization() {
        shutdownMultipleMembers(false, false);
    }

    @Test
    public void shutdownMultipleMembers_withoutPartitionInitialization() {
        shutdownMultipleMembers(true, false);
    }

    @Test
    public void shutdownMultipleSlaveMembers_withPartitionInitialization() {
        shutdownMultipleMembers(false, true);
    }

    @Test
    public void shutdownMultipleMembers_withPartitionInitialization() {
        shutdownMultipleMembers(true, true);
    }

    @Test
    public void shutdownMultipleMembers_whilePartitionsMigrating() {
        Config config = GracefulShutdownTest.newConfig();
        HazelcastInstance master = factory.newHazelcastInstance(config);
        HazelcastTestSupport.warmUpPartitions(master);
        HazelcastInstance[] slaves = factory.newInstances(config, 5);
        final List<HazelcastInstance> instances = new ArrayList<HazelcastInstance>(((slaves.length) + 1));
        instances.add(master);
        instances.addAll(Arrays.asList(slaves));
        Collections.shuffle(instances);
        final int count = (instances.size()) / 2;
        final CountDownLatch latch = new CountDownLatch(count);
        for (int i = 0; i < count; i++) {
            final int index = i;
            new Thread() {
                public void run() {
                    HazelcastInstance instance = instances.get(index);
                    instance.shutdown();
                    latch.countDown();
                }
            }.start();
        }
        HazelcastTestSupport.assertOpenEventually(latch);
        AbstractPartitionAssignmentsCorrectnessTest.assertPartitionAssignmentsEventually(factory);
    }

    @Test
    public void shutdownAndTerminateSlaveMembers_concurrently() {
        HazelcastInstance[] instances = factory.newInstances(new Config(), 5);
        int shutdownIndex = RandomPicker.getInt(1, instances.length);
        int terminateIndex;
        do {
            terminateIndex = RandomPicker.getInt(1, instances.length);
        } while (terminateIndex == shutdownIndex );
        shutdownAndTerminateMembers_concurrently(instances, shutdownIndex, terminateIndex);
    }

    @Test
    public void shutdownMasterAndTerminateSlaveMember_concurrently() {
        HazelcastInstance[] instances = factory.newInstances(new Config(), 5);
        int shutdownIndex = 0;
        int terminateIndex = RandomPicker.getInt(1, instances.length);
        shutdownAndTerminateMembers_concurrently(instances, shutdownIndex, terminateIndex);
    }

    @Test
    public void shutdownSlaveAndTerminateMasterMember_concurrently() {
        HazelcastInstance[] instances = factory.newInstances(new Config(), 5);
        int shutdownIndex = RandomPicker.getInt(1, instances.length);
        int terminateIndex = 0;
        shutdownAndTerminateMembers_concurrently(instances, shutdownIndex, terminateIndex);
    }

    @Test
    public void shutdownMasterMember_whenClusterFrozen_withoutPartitionInitialization() {
        shutdownMember_whenClusterNotActive(true, false, FROZEN);
    }

    @Test
    public void shutdownMasterMember_whenClusterPassive_withoutPartitionInitialization() {
        shutdownMember_whenClusterNotActive(true, false, PASSIVE);
    }

    @Test
    public void shutdownMasterMember_whenClusterFrozen_withPartitionInitialization() {
        shutdownMember_whenClusterNotActive(true, true, FROZEN);
    }

    @Test
    public void shutdownMasterMember_whenClusterPassive_withPartitionInitialization() {
        shutdownMember_whenClusterNotActive(true, true, PASSIVE);
    }

    @Test
    public void shutdownSlaveMember_whenClusterFrozen_withoutPartitionInitialization() {
        shutdownMember_whenClusterNotActive(false, false, FROZEN);
    }

    @Test
    public void shutdownSlaveMember_whenClusterPassive_withoutPartitionInitialization() {
        shutdownMember_whenClusterNotActive(false, false, PASSIVE);
    }

    @Test
    public void shutdownSlaveMember_whenClusterFrozen_withPartitionInitialization() {
        shutdownMember_whenClusterNotActive(false, true, FROZEN);
    }

    @Test
    public void shutdownSlaveMember_whenClusterPassive_withPartitionInitialization() {
        shutdownMember_whenClusterNotActive(false, true, PASSIVE);
    }

    @Test
    public void shutdownMemberAndCluster_withoutPartitionInitialization() {
        shutdownMemberAndCluster(false);
    }

    @Test
    public void shutdownMemberAndCluster_withPartitionInitialization() {
        shutdownMemberAndCluster(true);
    }

    @Test
    public void shutdownMemberAndCluster_concurrently_withoutPartitionInitialization() throws Exception {
        shutdownMemberAndCluster_concurrently(false);
    }

    @Test
    public void shutdownMemberAndCluster_concurrently_withPartitionInitialization() throws Exception {
        shutdownMemberAndCluster_concurrently(true);
    }
}

