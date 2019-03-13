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
package com.hazelcast.internal.partition.impl;


import ClusterState.FROZEN;
import PartitionServiceState.MIGRATION_LOCAL;
import PartitionServiceState.REPLICA_NOT_OWNED;
import PartitionServiceState.REPLICA_NOT_SYNC;
import PartitionServiceState.SAFE;
import com.hazelcast.config.Config;
import com.hazelcast.config.ServiceConfig;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.internal.cluster.impl.AdvancedClusterStateTest;
import com.hazelcast.internal.partition.AntiEntropyCorrectnessTest;
import com.hazelcast.internal.partition.PartitionReplica;
import com.hazelcast.internal.partition.service.TestMigrationAwareService;
import com.hazelcast.internal.partition.service.TestPutOperation;
import com.hazelcast.nio.Address;
import com.hazelcast.spi.NodeEngine;
import com.hazelcast.spi.Operation;
import com.hazelcast.test.AssertTask;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.TestHazelcastInstanceFactory;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class PartitionReplicaStateCheckerTest extends HazelcastTestSupport {
    @Test
    public void shouldBeSafe_whenNotInitialized() {
        TestHazelcastInstanceFactory factory = createHazelcastInstanceFactory();
        HazelcastInstance hz = factory.newHazelcastInstance();
        InternalPartitionServiceImpl partitionService = HazelcastTestSupport.getNode(hz).partitionService;
        PartitionReplicaStateChecker replicaStateChecker = partitionService.getPartitionReplicaStateChecker();
        PartitionServiceState state = replicaStateChecker.getPartitionServiceState();
        Assert.assertEquals(SAFE, state);
    }

    @Test
    public void shouldBeSafe_whenInitializedOnMaster() {
        TestHazelcastInstanceFactory factory = createHazelcastInstanceFactory();
        HazelcastInstance hz = factory.newHazelcastInstance();
        InternalPartitionServiceImpl partitionService = HazelcastTestSupport.getNode(hz).partitionService;
        partitionService.firstArrangement();
        PartitionReplicaStateChecker replicaStateChecker = partitionService.getPartitionReplicaStateChecker();
        PartitionServiceState state = replicaStateChecker.getPartitionServiceState();
        Assert.assertEquals(SAFE, state);
    }

    @Test
    public void shouldNotBeSafe_whenMissingReplicasPresent() {
        TestHazelcastInstanceFactory factory = createHazelcastInstanceFactory();
        HazelcastInstance hz = factory.newHazelcastInstance();
        InternalPartitionServiceImpl partitionService = HazelcastTestSupport.getNode(hz).partitionService;
        partitionService.firstArrangement();
        PartitionStateManager partitionStateManager = partitionService.getPartitionStateManager();
        InternalPartitionImpl partition = partitionStateManager.getPartitionImpl(0);
        PartitionReplica[] members = partition.getReplicas();
        partition.setReplicas(new PartitionReplica[members.length]);
        PartitionReplicaStateChecker replicaStateChecker = partitionService.getPartitionReplicaStateChecker();
        Assert.assertEquals(REPLICA_NOT_OWNED, replicaStateChecker.getPartitionServiceState());
        partition.setReplicas(members);
        Assert.assertEquals(SAFE, replicaStateChecker.getPartitionServiceState());
    }

    @Test
    public void shouldNotBeSafe_whenUnknownReplicaOwnerPresent() throws UnknownHostException {
        TestHazelcastInstanceFactory factory = createHazelcastInstanceFactory();
        HazelcastInstance hz = factory.newHazelcastInstance();
        InternalPartitionServiceImpl partitionService = HazelcastTestSupport.getNode(hz).partitionService;
        partitionService.firstArrangement();
        PartitionStateManager partitionStateManager = partitionService.getPartitionStateManager();
        InternalPartitionImpl partition = partitionStateManager.getPartitionImpl(0);
        PartitionReplica[] members = partition.getReplicas();
        PartitionReplica[] illegalMembers = Arrays.copyOf(members, members.length);
        Address address = members[0].address();
        illegalMembers[0] = new PartitionReplica(new Address(address.getInetAddress(), ((address.getPort()) + 1000)), members[0].uuid());
        partition.setReplicas(illegalMembers);
        PartitionReplicaStateChecker replicaStateChecker = partitionService.getPartitionReplicaStateChecker();
        Assert.assertEquals(REPLICA_NOT_OWNED, replicaStateChecker.getPartitionServiceState());
        partition.setReplicas(members);
        Assert.assertEquals(SAFE, replicaStateChecker.getPartitionServiceState());
    }

    @Test
    public void shouldBeSafe_whenKnownReplicaOwnerPresent_whileNotActive() {
        TestHazelcastInstanceFactory factory = createHazelcastInstanceFactory();
        HazelcastInstance hz = factory.newHazelcastInstance();
        HazelcastInstance hz2 = factory.newHazelcastInstance();
        InternalPartitionServiceImpl partitionService = HazelcastTestSupport.getNode(hz).partitionService;
        partitionService.firstArrangement();
        AdvancedClusterStateTest.changeClusterStateEventually(hz2, FROZEN);
        hz2.shutdown();
        HazelcastTestSupport.assertClusterSizeEventually(1, hz);
        PartitionReplicaStateChecker replicaStateChecker = partitionService.getPartitionReplicaStateChecker();
        Assert.assertEquals(SAFE, replicaStateChecker.getPartitionServiceState());
    }

    @Test
    public void shouldNotBeSafe_whenUnknownReplicaOwnerPresent_whileNotActive() throws UnknownHostException {
        TestHazelcastInstanceFactory factory = createHazelcastInstanceFactory();
        HazelcastInstance hz = factory.newHazelcastInstance();
        HazelcastInstance hz2 = factory.newHazelcastInstance();
        InternalPartitionServiceImpl partitionService = HazelcastTestSupport.getNode(hz).partitionService;
        partitionService.firstArrangement();
        AdvancedClusterStateTest.changeClusterStateEventually(hz2, FROZEN);
        hz2.shutdown();
        HazelcastTestSupport.assertClusterSizeEventually(1, hz);
        PartitionStateManager partitionStateManager = partitionService.getPartitionStateManager();
        InternalPartitionImpl partition = partitionStateManager.getPartitionImpl(0);
        PartitionReplica[] members = partition.getReplicas();
        PartitionReplica[] illegalMembers = Arrays.copyOf(members, members.length);
        Address address = members[0].address();
        illegalMembers[0] = new PartitionReplica(new Address(address.getInetAddress(), ((address.getPort()) + 1000)), members[0].uuid());
        partition.setReplicas(illegalMembers);
        PartitionReplicaStateChecker replicaStateChecker = partitionService.getPartitionReplicaStateChecker();
        Assert.assertEquals(REPLICA_NOT_OWNED, replicaStateChecker.getPartitionServiceState());
        partition.setReplicas(members);
        Assert.assertEquals(SAFE, replicaStateChecker.getPartitionServiceState());
    }

    @Test
    public void shouldNotBeSafe_whenMigrationTasksScheduled() {
        TestHazelcastInstanceFactory factory = createHazelcastInstanceFactory();
        HazelcastInstance hz = factory.newHazelcastInstance();
        InternalPartitionServiceImpl partitionService = HazelcastTestSupport.getNode(hz).partitionService;
        final CountDownLatch latch = new CountDownLatch(1);
        MigrationManager migrationManager = partitionService.getMigrationManager();
        migrationManager.schedule(new MigrationRunnable() {
            @Override
            public void run() {
                try {
                    latch.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        final PartitionReplicaStateChecker replicaStateChecker = partitionService.getPartitionReplicaStateChecker();
        Assert.assertEquals(MIGRATION_LOCAL, replicaStateChecker.getPartitionServiceState());
        latch.countDown();
        HazelcastTestSupport.assertEqualsEventually(new Callable<PartitionServiceState>() {
            @Override
            public PartitionServiceState call() throws Exception {
                return replicaStateChecker.getPartitionServiceState();
            }
        }, SAFE);
    }

    @Test
    public void shouldNotBeSafe_whenReplicasAreNotSync() {
        Config config = new Config();
        ServiceConfig serviceConfig = TestMigrationAwareService.createServiceConfig(1);
        config.getServicesConfig().addServiceConfig(serviceConfig);
        TestHazelcastInstanceFactory factory = createHazelcastInstanceFactory();
        HazelcastInstance hz = factory.newHazelcastInstance(config);
        HazelcastInstance hz2 = factory.newHazelcastInstance(config);
        InternalPartitionServiceImpl partitionService1 = HazelcastTestSupport.getNode(hz).partitionService;
        InternalPartitionServiceImpl partitionService2 = HazelcastTestSupport.getNode(hz2).partitionService;
        int maxPermits = drainAllReplicaSyncPermits(partitionService1);
        int maxPermits2 = drainAllReplicaSyncPermits(partitionService2);
        Assert.assertEquals(maxPermits, maxPermits2);
        HazelcastTestSupport.warmUpPartitions(hz, hz2);
        AntiEntropyCorrectnessTest.setBackupPacketDropFilter(hz, 100);
        AntiEntropyCorrectnessTest.setBackupPacketDropFilter(hz2, 100);
        NodeEngine nodeEngine = HazelcastTestSupport.getNode(hz).nodeEngine;
        for (int i = 0; i < (nodeEngine.getPartitionService().getPartitionCount()); i++) {
            Operation op = new PartitionReplicaStateCheckerTest.TestPutOperationWithAsyncBackup(i);
            nodeEngine.getOperationService().invokeOnPartition(null, op, i).join();
        }
        final PartitionReplicaStateChecker replicaStateChecker1 = partitionService1.getPartitionReplicaStateChecker();
        final PartitionReplicaStateChecker replicaStateChecker2 = partitionService2.getPartitionReplicaStateChecker();
        Assert.assertEquals(REPLICA_NOT_SYNC, replicaStateChecker1.getPartitionServiceState());
        Assert.assertEquals(REPLICA_NOT_SYNC, replicaStateChecker2.getPartitionServiceState());
        addReplicaSyncPermits(partitionService1, maxPermits);
        addReplicaSyncPermits(partitionService2, maxPermits);
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() throws Exception {
                Assert.assertEquals(SAFE, replicaStateChecker1.getPartitionServiceState());
                Assert.assertEquals(SAFE, replicaStateChecker2.getPartitionServiceState());
            }
        });
    }

    private static class TestPutOperationWithAsyncBackup extends TestPutOperation {
        public TestPutOperationWithAsyncBackup() {
        }

        TestPutOperationWithAsyncBackup(int i) {
            super(i);
        }

        @Override
        public int getSyncBackupCount() {
            return 0;
        }

        @Override
        public int getAsyncBackupCount() {
            return super.getSyncBackupCount();
        }
    }
}

