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


import AtomicLongService.SERVICE_NAME;
import ClusterState.ACTIVE;
import ClusterState.FROZEN;
import ClusterState.NO_MIGRATION;
import ClusterState.PASSIVE;
import com.hazelcast.cluster.ClusterState;
import com.hazelcast.concurrent.atomiclong.operations.AddAndGetOperation;
import com.hazelcast.config.Config;
import com.hazelcast.core.Cluster;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.HazelcastInstanceNotActiveException;
import com.hazelcast.instance.DefaultNodeExtension;
import com.hazelcast.instance.HazelcastInstanceFactory;
import com.hazelcast.instance.Node;
import com.hazelcast.instance.NodeExtension;
import com.hazelcast.instance.TestUtil;
import com.hazelcast.internal.partition.InternalPartition;
import com.hazelcast.internal.partition.InternalPartitionService;
import com.hazelcast.nio.Address;
import com.hazelcast.spi.Operation;
import com.hazelcast.spi.impl.operationservice.InternalOperationService;
import com.hazelcast.test.AssertTask;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.TestHazelcastInstanceFactory;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import com.hazelcast.test.mocknetwork.MockNodeContext;
import com.hazelcast.transaction.TransactionException;
import com.hazelcast.transaction.TransactionOptions;
import com.hazelcast.transaction.TransactionOptions.TransactionType;
import com.hazelcast.transaction.impl.Transaction;
import com.hazelcast.transaction.impl.TransactionLogRecord;
import com.hazelcast.transaction.impl.TransactionManagerServiceImpl;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class AdvancedClusterStateTest extends HazelcastTestSupport {
    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test
    public void changeClusterState_shouldFail_whenMemberAdded_duringTx() throws Exception {
        final TestHazelcastInstanceFactory factory = createHazelcastInstanceFactory();
        HazelcastInstance[] instances = new HazelcastInstance[3];
        for (int i = 0; i < 3; i++) {
            instances[i] = factory.newHazelcastInstance();
        }
        HazelcastTestSupport.assertClusterSizeEventually(instances.length, instances);
        HazelcastInstance hz = instances[((instances.length) - 1)];
        TransactionManagerServiceImpl transactionManagerService = AdvancedClusterStateTest.spyTransactionManagerService(hz);
        TransactionOptions options = TransactionOptions.getDefault();
        Mockito.when(transactionManagerService.newAllowedDuringPassiveStateTransaction(options)).thenAnswer(new Answer<Transaction>() {
            boolean started;

            @Override
            public Transaction answer(InvocationOnMock invocation) throws Throwable {
                Transaction tx = ((Transaction) (invocation.callRealMethod()));
                return new AdvancedClusterStateTest.DelegatingTransaction(tx) {
                    @Override
                    public void add(TransactionLogRecord record) {
                        super.add(record);
                        if (!(started)) {
                            started = true;
                            factory.newHazelcastInstance();
                        }
                    }
                };
            }
        });
        exception.expect(IllegalStateException.class);
        hz.getCluster().changeClusterState(PASSIVE, options);
    }

    @Test
    public void changeClusterState_shouldFail_whenMemberRemoved_duringTx() throws Exception {
        final TestHazelcastInstanceFactory factory = createHazelcastInstanceFactory();
        final HazelcastInstance[] instances = new HazelcastInstance[3];
        for (int i = 0; i < 3; i++) {
            instances[i] = factory.newHazelcastInstance();
        }
        HazelcastTestSupport.assertClusterSizeEventually(instances.length, instances);
        HazelcastInstance hz = instances[((instances.length) - 1)];
        TransactionManagerServiceImpl transactionManagerService = AdvancedClusterStateTest.spyTransactionManagerService(hz);
        TransactionOptions options = TransactionOptions.getDefault();
        Mockito.when(transactionManagerService.newAllowedDuringPassiveStateTransaction(options)).thenAnswer(new Answer<Transaction>() {
            boolean shutdown;

            @Override
            public Transaction answer(InvocationOnMock invocation) throws Throwable {
                Transaction tx = ((Transaction) (invocation.callRealMethod()));
                return new AdvancedClusterStateTest.DelegatingTransaction(tx) {
                    @Override
                    public void add(TransactionLogRecord record) {
                        super.add(record);
                        if (!(shutdown)) {
                            shutdown = true;
                            TestUtil.terminateInstance(instances[0]);
                        }
                    }
                };
            }
        });
        exception.expect(IllegalStateException.class);
        hz.getCluster().changeClusterState(PASSIVE, options);
    }

    @Test
    public void changeClusterState_shouldFail_whenStateIsAlreadyLocked() {
        final TestHazelcastInstanceFactory factory = createHazelcastInstanceFactory(3);
        HazelcastInstance[] instances = factory.newInstances();
        HazelcastTestSupport.assertClusterSizeEventually(instances.length, instances);
        final HazelcastInstance hz = instances[((instances.length) - 1)];
        lockClusterState(hz);
        final HazelcastInstance hz2 = instances[((instances.length) - 2)];
        exception.expect(TransactionException.class);
        hz2.getCluster().changeClusterState(PASSIVE);
    }

    @Test
    public void changeClusterState_shouldFail_whenInitiatorDies_beforePrepare() throws Exception {
        final TestHazelcastInstanceFactory factory = createHazelcastInstanceFactory(3);
        final HazelcastInstance[] instances = factory.newInstances();
        HazelcastTestSupport.assertClusterSizeEventually(instances.length, instances);
        final HazelcastInstance hz = instances[((instances.length) - 1)];
        TransactionManagerServiceImpl transactionManagerService = AdvancedClusterStateTest.spyTransactionManagerService(hz);
        TransactionOptions options = TransactionOptions.getDefault().setTimeout(60, TimeUnit.SECONDS);
        Mockito.when(transactionManagerService.newAllowedDuringPassiveStateTransaction(options)).thenAnswer(new AdvancedClusterStateTest.TransactionAnswer() {
            @Override
            protected void beforePrepare() {
                TestUtil.terminateInstance(hz);
            }
        });
        try {
            hz.getCluster().changeClusterState(FROZEN, options);
            Assert.fail("`changeClusterState` should throw HazelcastInstanceNotActiveException!");
        } catch (HazelcastInstanceNotActiveException ignored) {
        }
        HazelcastTestSupport.assertClusterStateEventually(ACTIVE, instances[0], instances[1]);
    }

    @Test
    public void changeClusterState_shouldNotFail_whenInitiatorDies_afterPrepare() throws Exception {
        final TestHazelcastInstanceFactory factory = createHazelcastInstanceFactory(3);
        final HazelcastInstance[] instances = factory.newInstances();
        HazelcastTestSupport.assertClusterSizeEventually(instances.length, instances);
        final HazelcastInstance hz = instances[((instances.length) - 1)];
        TransactionManagerServiceImpl transactionManagerService = AdvancedClusterStateTest.spyTransactionManagerService(hz);
        TransactionOptions options = TransactionOptions.getDefault().setDurability(1);
        Mockito.when(transactionManagerService.newAllowedDuringPassiveStateTransaction(options)).thenAnswer(new AdvancedClusterStateTest.TransactionAnswer() {
            @Override
            protected void afterPrepare() {
                TestUtil.terminateInstance(hz);
            }
        });
        try {
            hz.getCluster().changeClusterState(FROZEN, options);
            Assert.fail("This instance is terminated. Cannot commit the transaction!");
        } catch (HazelcastInstanceNotActiveException ignored) {
        }
        HazelcastTestSupport.assertClusterStateEventually(FROZEN, instances[0], instances[1]);
    }

    @Test
    public void changeClusterState_shouldFail_withoutBackup_whenInitiatorDies_beforePrepare() throws Exception {
        final TestHazelcastInstanceFactory factory = createHazelcastInstanceFactory(3);
        final HazelcastInstance[] instances = factory.newInstances();
        HazelcastTestSupport.assertClusterSizeEventually(instances.length, instances);
        final HazelcastInstance hz = instances[((instances.length) - 1)];
        TransactionManagerServiceImpl transactionManagerService = AdvancedClusterStateTest.spyTransactionManagerService(hz);
        TransactionOptions options = new TransactionOptions().setDurability(0).setTimeout(60, TimeUnit.SECONDS);
        Mockito.when(transactionManagerService.newAllowedDuringPassiveStateTransaction(options)).thenAnswer(new AdvancedClusterStateTest.TransactionAnswer() {
            @Override
            protected void beforePrepare() {
                TestUtil.terminateInstance(hz);
            }
        });
        try {
            hz.getCluster().changeClusterState(FROZEN, options);
            Assert.fail("This instance is terminated. Cannot commit the transaction!");
        } catch (HazelcastInstanceNotActiveException ignored) {
        }
        HazelcastTestSupport.assertClusterStateEventually(ACTIVE, instances[0], instances[1]);
    }

    @Test
    public void changeClusterState_shouldFail_withoutBackup_whenInitiatorDies_afterPrepare() throws Exception {
        final TestHazelcastInstanceFactory factory = createHazelcastInstanceFactory(3);
        final HazelcastInstance[] instances = factory.newInstances();
        HazelcastTestSupport.assertClusterSizeEventually(instances.length, instances);
        final HazelcastInstance hz = instances[((instances.length) - 1)];
        TransactionManagerServiceImpl transactionManagerService = AdvancedClusterStateTest.spyTransactionManagerService(hz);
        TransactionOptions options = new TransactionOptions().setDurability(0).setTimeout(60, TimeUnit.SECONDS);
        Mockito.when(transactionManagerService.newAllowedDuringPassiveStateTransaction(options)).thenAnswer(new AdvancedClusterStateTest.TransactionAnswer() {
            @Override
            protected void afterPrepare() {
                TestUtil.terminateInstance(hz);
            }
        });
        try {
            hz.getCluster().changeClusterState(FROZEN, options);
            Assert.fail("This instance is terminated. Cannot commit the transaction!");
        } catch (HazelcastInstanceNotActiveException ignored) {
        }
        HazelcastTestSupport.assertClusterStateEventually(ACTIVE, instances[0], instances[1]);
    }

    @Test
    public void changeClusterState_shouldNotFail_whenNonInitiatorMemberDies_duringCommit() throws Exception {
        final TestHazelcastInstanceFactory factory = createHazelcastInstanceFactory(3);
        final HazelcastInstance[] instances = factory.newInstances();
        HazelcastTestSupport.assertClusterSizeEventually(instances.length, instances);
        final HazelcastInstance hz = instances[2];
        TransactionManagerServiceImpl transactionManagerService = AdvancedClusterStateTest.spyTransactionManagerService(hz);
        final Address address = HazelcastTestSupport.getAddress(instances[0]);
        TransactionOptions options = TransactionOptions.getDefault().setDurability(0);
        Mockito.when(transactionManagerService.newAllowedDuringPassiveStateTransaction(options)).thenAnswer(new AdvancedClusterStateTest.TransactionAnswer() {
            @Override
            protected void afterPrepare() {
                TestUtil.terminateInstance(instances[0]);
            }
        });
        hz.getCluster().changeClusterState(FROZEN, options);
        HazelcastTestSupport.assertClusterStateEventually(FROZEN, instances[2], instances[1]);
        instances[0] = factory.newHazelcastInstance(address);
        HazelcastTestSupport.assertClusterSizeEventually(3, instances);
        HazelcastTestSupport.assertClusterState(FROZEN, instances);
    }

    @Test
    public void changeClusterState_shouldFail_whenNonInitiatorMemberDies_beforePrepare() throws Exception {
        final TestHazelcastInstanceFactory factory = createHazelcastInstanceFactory(3);
        final HazelcastInstance[] instances = factory.newInstances();
        HazelcastTestSupport.assertClusterSizeEventually(instances.length, instances);
        final HazelcastInstance hz = instances[2];
        TransactionManagerServiceImpl transactionManagerService = AdvancedClusterStateTest.spyTransactionManagerService(hz);
        final Address address = HazelcastTestSupport.getAddress(instances[0]);
        TransactionOptions options = TransactionOptions.getDefault().setDurability(0);
        Mockito.when(transactionManagerService.newAllowedDuringPassiveStateTransaction(options)).thenAnswer(new AdvancedClusterStateTest.TransactionAnswer() {
            @Override
            protected void beforePrepare() {
                TestUtil.terminateInstance(instances[0]);
            }
        });
        try {
            hz.getCluster().changeClusterState(FROZEN, options);
            Assert.fail("A member is terminated. Cannot commit the transaction!");
        } catch (IllegalStateException ignored) {
        }
        HazelcastTestSupport.assertClusterStateEventually(ACTIVE, instances[2], instances[1]);
        instances[0] = factory.newHazelcastInstance(address);
        HazelcastTestSupport.assertClusterSizeEventually(3, instances);
        HazelcastTestSupport.assertClusterState(ACTIVE, instances);
    }

    @Test
    public void changeClusterState_shouldFail_whenStartupIsNotCompleted() throws Exception {
        TestHazelcastInstanceFactory factory = createHazelcastInstanceFactory();
        final AtomicBoolean startupDone = new AtomicBoolean(false);
        HazelcastInstance instance = HazelcastInstanceFactory.newHazelcastInstance(new Config(), HazelcastTestSupport.randomName(), new MockNodeContext(factory.getRegistry(), new Address("127.0.0.1", 5555)) {
            @Override
            public NodeExtension createNodeExtension(Node node) {
                return new DefaultNodeExtension(node) {
                    @Override
                    public boolean isStartCompleted() {
                        return (startupDone.get()) && (super.isStartCompleted());
                    }
                };
            }
        });
        try {
            instance.getCluster().changeClusterState(FROZEN);
            Assert.fail("Should not be able to change cluster state when startup is not completed yet!");
        } catch (IllegalStateException expected) {
        }
        startupDone.set(true);
        instance.getCluster().changeClusterState(FROZEN);
    }

    @Test
    public void clusterState_shouldBeTheSame_finally_onAllNodes() {
        final TestHazelcastInstanceFactory factory = createHazelcastInstanceFactory(3);
        HazelcastInstance[] instances = factory.newInstances();
        HazelcastTestSupport.assertClusterSizeEventually(instances.length, instances);
        CountDownLatch latch = new CountDownLatch(instances.length);
        int iteration = 20;
        Random random = new Random();
        for (HazelcastInstance instance : instances) {
            new AdvancedClusterStateTest.IterativeStateChangeThread(instance, iteration, latch, random).start();
        }
        HazelcastTestSupport.assertOpenEventually(latch);
        HazelcastTestSupport.assertClusterState(instances[0].getCluster().getClusterState(), instances);
    }

    @Test
    public void partitionTable_shouldBeFrozen_whenMemberLeaves_inFrozenState() {
        Config config = new Config();
        TestHazelcastInstanceFactory factory = createHazelcastInstanceFactory(3);
        HazelcastInstance[] instances = factory.newInstances(config);
        HazelcastInstance hz1 = instances[0];
        HazelcastInstance hz2 = instances[1];
        HazelcastInstance hz3 = instances[2];
        HazelcastTestSupport.warmUpPartitions(instances);
        HazelcastTestSupport.waitAllForSafeState(instances);
        final Address owner = HazelcastTestSupport.getNode(hz1).getThisAddress();
        int partitionId = HazelcastTestSupport.getPartitionId(hz1);
        AdvancedClusterStateTest.changeClusterStateEventually(hz2, FROZEN);
        TestUtil.terminateInstance(hz1);
        final InternalPartition partition = HazelcastTestSupport.getNode(hz2).getPartitionService().getPartition(partitionId);
        HazelcastTestSupport.assertTrueAllTheTime(new AssertTask() {
            @Override
            public void run() throws Exception {
                Assert.assertEquals(owner, partition.getOwnerOrNull());
            }
        }, 3);
    }

    @Test
    public void partitionAssignment_shouldFail_whenTriggered_inNoMigrationState() {
        partitionAssignment_shouldFail_whenMigrationNotAllowed(NO_MIGRATION);
    }

    @Test
    public void partitionAssignment_shouldFail_whenTriggered_inFrozenState() {
        partitionAssignment_shouldFail_whenMigrationNotAllowed(FROZEN);
    }

    @Test
    public void partitionAssignment_shouldFail_whenTriggered_inPassiveState() {
        partitionAssignment_shouldFail_whenMigrationNotAllowed(PASSIVE);
    }

    @Test
    public void partitionInvocation_shouldFail_whenPartitionsNotAssigned_inNoMigrationState() throws InterruptedException {
        partitionInvocation_shouldFail_whenPartitionsNotAssigned_whenMigrationNotAllowed(NO_MIGRATION);
    }

    @Test
    public void partitionInvocation_shouldFail_whenPartitionsNotAssigned_inFrozenState() throws InterruptedException {
        partitionInvocation_shouldFail_whenPartitionsNotAssigned_whenMigrationNotAllowed(FROZEN);
    }

    @Test
    public void partitionInvocation_shouldFail_whenPartitionsNotAssigned_inPassiveState() throws InterruptedException {
        partitionInvocation_shouldFail_whenPartitionsNotAssigned_whenMigrationNotAllowed(PASSIVE);
    }

    @Test
    public void test_eitherClusterStateChange_orPartitionInitialization_shouldBeSuccessful() throws Exception {
        Config config = new Config();
        TestHazelcastInstanceFactory factory = createHazelcastInstanceFactory(3);
        HazelcastInstance[] instances = factory.newInstances(config);
        HazelcastInstance hz1 = instances[0];
        final HazelcastInstance hz2 = instances[1];
        HazelcastInstance hz3 = instances[2];
        HazelcastTestSupport.assertClusterSizeEventually(instances.length, instances);
        final InternalPartitionService partitionService = HazelcastTestSupport.getNode(hz1).getPartitionService();
        final int initialPartitionStateVersion = partitionService.getPartitionStateVersion();
        final ClusterState newState = ClusterState.PASSIVE;
        final Future future = HazelcastTestSupport.spawn(new Runnable() {
            public void run() {
                try {
                    changeClusterState(hz2, newState, initialPartitionStateVersion);
                } catch (Exception ignored) {
                }
            }
        });
        partitionService.firstArrangement();
        future.get(2, TimeUnit.MINUTES);
        final ClusterState currentState = hz2.getCluster().getClusterState();
        if (currentState == newState) {
            // if cluster state changed then partition state version should be equal to initial version
            Assert.assertEquals(initialPartitionStateVersion, partitionService.getPartitionStateVersion());
        } else {
            Assert.assertEquals(ACTIVE, currentState);
            final InternalPartition partition = partitionService.getPartition(0, false);
            if ((partition.getOwnerOrNull()) == null) {
                // if partition assignment failed then partition state version should be equal to initial version
                Assert.assertEquals(initialPartitionStateVersion, partitionService.getPartitionStateVersion());
            } else {
                // if cluster state change failed and partition assignment is done
                // then partition state version should be some positive number
                final int partitionStateVersion = partitionService.getPartitionStateVersion();
                Assert.assertTrue(("Version should be positive: " + partitionService), (partitionStateVersion > 0));
            }
        }
    }

    @Test
    public void clusterState_shouldBeFrozen_whenMemberReJoins_inFrozenState() {
        Config config = new Config();
        TestHazelcastInstanceFactory factory = createHazelcastInstanceFactory(3);
        HazelcastInstance[] instances = factory.newInstances(config);
        HazelcastInstance hz1 = instances[0];
        HazelcastInstance hz2 = instances[1];
        HazelcastInstance hz3 = instances[2];
        AdvancedClusterStateTest.changeClusterStateEventually(hz2, FROZEN);
        final Address owner = HazelcastTestSupport.getNode(hz1).getThisAddress();
        TestUtil.terminateInstance(hz1);
        hz1 = factory.newHazelcastInstance(owner);
        HazelcastTestSupport.assertClusterSizeEventually(3, hz2);
        HazelcastTestSupport.assertClusterState(FROZEN, hz1, hz2, hz3);
    }

    @Test
    public void nodesCanShutDown_whenClusterState_frozen() {
        nodesCanShutDown_whenClusterState_changesTo(FROZEN);
    }

    @Test
    public void nodesCanShutDown_whenClusterState_passive() {
        nodesCanShutDown_whenClusterState_changesTo(PASSIVE);
    }

    @Test
    public void nodesCanShutDown_whenClusterState_noMigration() {
        nodesCanShutDown_whenClusterState_changesTo(NO_MIGRATION);
    }

    @Test
    public void invocationShouldComplete_whenMemberReJoins_inFrozenState() throws Exception {
        Config config = new Config();
        TestHazelcastInstanceFactory factory = createHazelcastInstanceFactory(3);
        HazelcastInstance[] instances = factory.newInstances(config);
        HazelcastInstance hz1 = instances[0];
        HazelcastInstance hz2 = instances[1];
        HazelcastInstance hz3 = instances[2];
        HazelcastTestSupport.warmUpPartitions(instances);
        HazelcastTestSupport.waitAllForSafeState(instances);
        Address owner = HazelcastTestSupport.getNode(hz1).getThisAddress();
        String key = HazelcastTestSupport.generateKeyOwnedBy(hz1);
        int partitionId = hz1.getPartitionService().getPartition(key).getPartitionId();
        AdvancedClusterStateTest.changeClusterStateEventually(hz2, FROZEN);
        TestUtil.terminateInstance(hz1);
        InternalOperationService operationService = HazelcastTestSupport.getNode(hz3).getNodeEngine().getOperationService();
        Operation op = new AddAndGetOperation(key, 1);
        final Future<Long> future = operationService.invokeOnPartition(SERVICE_NAME, op, partitionId);
        Assert.assertFalse(future.isDone());
        factory.newHazelcastInstance(owner);
        HazelcastTestSupport.assertClusterSizeEventually(3, hz2);
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() throws Exception {
                Assert.assertTrue(future.isDone());
            }
        });
        // should not fail
        future.get();
    }

    private abstract static class TransactionAnswer implements Answer<Transaction> {
        @Override
        public Transaction answer(InvocationOnMock invocation) throws Throwable {
            Transaction tx = ((Transaction) (invocation.callRealMethod()));
            return new AdvancedClusterStateTest.DelegatingTransaction(tx) {
                @Override
                public void prepare() throws TransactionException {
                    beforePrepare();
                    super.prepare();
                    afterPrepare();
                }
            };
        }

        protected void beforePrepare() {
        }

        protected void afterPrepare() {
        }
    }

    private static class IterativeStateChangeThread extends Thread {
        private final HazelcastInstance instance;

        private final int iteration;

        private final CountDownLatch latch;

        private final Random random;

        IterativeStateChangeThread(HazelcastInstance instance, int iteration, CountDownLatch latch, Random random) {
            this.instance = instance;
            this.iteration = iteration;
            this.latch = latch;
            this.random = random;
        }

        public void run() {
            Cluster cluster = instance.getCluster();
            ClusterState newState = AdvancedClusterStateTest.IterativeStateChangeThread.flipState(cluster.getClusterState());
            for (int i = 0; i < (iteration); i++) {
                try {
                    cluster.changeClusterState(newState);
                } catch (TransactionException e) {
                    HazelcastTestSupport.ignore(e);
                }
                newState = AdvancedClusterStateTest.IterativeStateChangeThread.flipState(newState);
                HazelcastTestSupport.sleepMillis(((random.nextInt(5)) + 1));
            }
            latch.countDown();
        }

        private static ClusterState flipState(ClusterState state) {
            state = (state == (ClusterState.ACTIVE)) ? ClusterState.FROZEN : ClusterState.ACTIVE;
            return state;
        }
    }

    private static class DelegatingTransaction implements Transaction {
        final Transaction tx;

        DelegatingTransaction(Transaction tx) {
            this.tx = tx;
        }

        @Override
        public void begin() throws IllegalStateException {
            tx.begin();
        }

        @Override
        public void prepare() throws TransactionException {
            tx.prepare();
        }

        @Override
        public void commit() throws TransactionException, IllegalStateException {
            tx.commit();
        }

        @Override
        public void rollback() throws IllegalStateException {
            tx.rollback();
        }

        @Override
        public String getTxnId() {
            return tx.getTxnId();
        }

        @Override
        public State getState() {
            return tx.getState();
        }

        @Override
        public long getTimeoutMillis() {
            return tx.getTimeoutMillis();
        }

        @Override
        public void add(TransactionLogRecord record) {
            tx.add(record);
        }

        @Override
        public void remove(Object key) {
            tx.remove(key);
        }

        @Override
        public TransactionLogRecord get(Object key) {
            return tx.get(key);
        }

        @Override
        public String getOwnerUuid() {
            return tx.getOwnerUuid();
        }

        @Override
        public TransactionType getTransactionType() {
            return tx.getTransactionType();
        }

        @Override
        public boolean isOriginatedFromClient() {
            return tx.isOriginatedFromClient();
        }
    }
}

