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


import com.hazelcast.config.Config;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.PartitionService;
import com.hazelcast.test.AssertTask;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import java.util.Collection;
import java.util.concurrent.TimeUnit;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class PartitionServiceSafetyCheckTest extends PartitionCorrectnessTestSupport {
    private static final float BLOCK_RATIO = 0.95F;

    @Test
    public void clusterShouldBeSafe_withoutPartitionInitialization() throws InterruptedException {
        Config config = getConfig(false, false);
        startNodes(config, nodeCount);
        Collection<HazelcastInstance> instances = factory.getAllHazelcastInstances();
        assertSafe(instances);
    }

    @Test
    public void clusterShouldBeSafe_withOnlyLiteMembers() throws InterruptedException {
        Config config = getConfig(false, false);
        config.setLiteMember(true);
        startNodes(config, nodeCount);
        Collection<HazelcastInstance> instances = factory.getAllHazelcastInstances();
        assertSafe(instances);
    }

    @Test
    public void clusterShouldBeSafe_withSingleDataMember() throws InterruptedException {
        Config config0 = getConfig(true, false);
        HazelcastInstance hz = factory.newHazelcastInstance(config0);
        fillData(hz);
        Config config = getConfig(true, false);
        config.setLiteMember(true);
        startNodes(config, ((nodeCount) - 1));
        Collection<HazelcastInstance> instances = factory.getAllHazelcastInstances();
        assertSafeEventually(instances);
    }

    @Test
    public void clusterShouldBeEventuallySafe_withPartitionInitialization() throws InterruptedException {
        clusterShouldBeEventuallySafe_withPartitionInitialization(false);
    }

    @Test
    public void clusterShouldBeEventuallySafe_withPartitionInitializationAndAntiEntropy() throws InterruptedException {
        clusterShouldBeEventuallySafe_withPartitionInitialization(true);
    }

    @Test
    public void clusterShouldBeEventuallySafe_duringMigration() throws InterruptedException {
        Config config = getConfig(true, false);
        HazelcastInstance hz = factory.newHazelcastInstance(config);
        fillData(hz);
        startNodes(config, ((nodeCount) - 1));
        Collection<HazelcastInstance> instances = factory.getAllHazelcastInstances();
        assertSafeEventually(instances);
    }

    @Test
    public void clusterShouldNotBeSafe_whenBackupsBlocked_withoutAntiEntropy() throws InterruptedException {
        Config config = getConfig(true, false);
        HazelcastInstance hz = factory.newHazelcastInstance(config);
        startNodes(config, ((nodeCount) - 1));
        final Collection<HazelcastInstance> instances = factory.getAllHazelcastInstances();
        HazelcastTestSupport.warmUpPartitions(instances);
        for (HazelcastInstance instance : instances) {
            AntiEntropyCorrectnessTest.setBackupPacketDropFilter(instance, PartitionServiceSafetyCheckTest.BLOCK_RATIO);
        }
        fillData(hz);
        HazelcastTestSupport.assertTrueFiveSeconds(new AssertTask() {
            @Override
            public void run() throws Exception {
                Assert.assertFalse(HazelcastTestSupport.isAllInSafeState(instances));
                for (HazelcastInstance instance : instances) {
                    PartitionService ps = instance.getPartitionService();
                    Assert.assertFalse(ps.isClusterSafe());
                }
            }
        });
    }

    @Test
    public void clusterShouldBeSafe_whenBackupsBlocked_withForceToBeSafe() throws InterruptedException {
        Config config = getConfig(true, true);
        HazelcastInstance hz = factory.newHazelcastInstance(config);
        startNodes(config, ((nodeCount) - 1));
        final Collection<HazelcastInstance> instances = factory.getAllHazelcastInstances();
        HazelcastTestSupport.warmUpPartitions(instances);
        for (HazelcastInstance instance : instances) {
            AntiEntropyCorrectnessTest.setBackupPacketDropFilter(instance, PartitionServiceSafetyCheckTest.BLOCK_RATIO);
        }
        fillData(hz);
        for (HazelcastInstance instance : instances) {
            Assert.assertTrue(instance.getPartitionService().forceLocalMemberToBeSafe(1, TimeUnit.MINUTES));
        }
    }

    @Test
    public void partitionAssignmentsShouldBeCorrect_whenClusterIsSafe() {
        Config config = getConfig(false, false);
        HazelcastInstance hz1 = factory.newHazelcastInstance(config);
        HazelcastTestSupport.warmUpPartitions(hz1);
        HazelcastInstance hz2 = factory.newHazelcastInstance(config);
        HazelcastInstance hz3 = factory.newHazelcastInstance(config);
        HazelcastTestSupport.waitAllForSafeState(hz1, hz2, hz3);
        hz2.getLifecycleService().terminate();
        HazelcastTestSupport.assertClusterSizeEventually(2, hz1, hz3);
        HazelcastTestSupport.waitAllForSafeState(hz1, hz3);
        PartitionCorrectnessTestSupport.assertPartitionAssignments(factory);
        hz1.getLifecycleService().terminate();
        HazelcastTestSupport.assertClusterSizeEventually(1, hz3);
        HazelcastTestSupport.waitAllForSafeState(hz3);
        PartitionCorrectnessTestSupport.assertPartitionAssignments(factory);
    }
}

