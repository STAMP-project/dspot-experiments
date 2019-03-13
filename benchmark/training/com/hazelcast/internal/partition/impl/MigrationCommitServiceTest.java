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


import InternalPartitionService.SERVICE_NAME;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.internal.partition.MigrationInfo;
import com.hazelcast.internal.partition.PartitionReplica;
import com.hazelcast.spi.ServiceNamespace;
import com.hazelcast.spi.impl.NodeEngineImpl;
import com.hazelcast.spi.impl.PartitionSpecificRunnable;
import com.hazelcast.test.ExpectedRuntimeException;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.TestHazelcastInstanceFactory;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import java.util.concurrent.CountDownLatch;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

import static MigrationParticipant.DESTINATION;
import static MigrationParticipant.MASTER;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class MigrationCommitServiceTest extends HazelcastTestSupport {
    private static final int NODE_COUNT = 4;

    private static final int PARTITION_COUNT = 10;

    private static final int BACKUP_COUNT = 6;

    private static final int PARTITION_ID_TO_MIGRATE = 0;

    private volatile CountDownLatch blockMigrationStartLatch;

    private TestHazelcastInstanceFactory factory;

    private HazelcastInstance[] instances;

    @Test
    public void testPartitionOwnerMoveCommit() throws Exception {
        int replicaIndexToClear = (MigrationCommitServiceTest.NODE_COUNT) - 1;
        int replicaIndexToMigrate = 0;
        testSuccessfulMoveMigration(MigrationCommitServiceTest.PARTITION_ID_TO_MIGRATE, replicaIndexToClear, replicaIndexToMigrate);
    }

    @Test
    public void testPartitionOwnerMoveRollback() throws Exception {
        int replicaIndexToClear = (MigrationCommitServiceTest.NODE_COUNT) - 1;
        int replicaIndexToMigrate = 0;
        testFailedMoveMigration(MigrationCommitServiceTest.PARTITION_ID_TO_MIGRATE, replicaIndexToClear, replicaIndexToMigrate);
    }

    @Test
    public void testPartitionBackupMoveCommit() throws Exception {
        int replicaIndexToClear = (MigrationCommitServiceTest.NODE_COUNT) - 1;
        int replicaIndexToMigrate = (MigrationCommitServiceTest.NODE_COUNT) - 2;
        testSuccessfulMoveMigration(MigrationCommitServiceTest.PARTITION_ID_TO_MIGRATE, replicaIndexToClear, replicaIndexToMigrate);
    }

    @Test
    public void testPartitionBackupMoveRollback() throws Exception {
        int replicaIndexToClear = (MigrationCommitServiceTest.NODE_COUNT) - 1;
        int replicaIndexToMigrate = (MigrationCommitServiceTest.NODE_COUNT) - 2;
        testFailedMoveMigration(MigrationCommitServiceTest.PARTITION_ID_TO_MIGRATE, replicaIndexToClear, replicaIndexToMigrate);
    }

    @Test
    public void testPartitionOwnerShiftDownCommit() throws Exception {
        int oldReplicaIndex = 0;
        int replicaIndexToClear = (MigrationCommitServiceTest.NODE_COUNT) - 1;
        int newReplicaIndex = (MigrationCommitServiceTest.NODE_COUNT) - 1;
        testSuccessfulShiftDownMigration(MigrationCommitServiceTest.PARTITION_ID_TO_MIGRATE, replicaIndexToClear, oldReplicaIndex, newReplicaIndex);
    }

    @Test
    public void testPartitionOwnerShiftDownRollback() throws Exception {
        int oldReplicaIndex = 0;
        int replicaIndexToClear = (MigrationCommitServiceTest.NODE_COUNT) - 1;
        int newReplicaIndex = (MigrationCommitServiceTest.NODE_COUNT) - 1;
        testFailedShiftDownMigration(MigrationCommitServiceTest.PARTITION_ID_TO_MIGRATE, replicaIndexToClear, oldReplicaIndex, newReplicaIndex);
    }

    @Test
    public void testPartitionBackupShiftDownCommit() throws Exception {
        int oldReplicaIndex = 1;
        int replicaIndexToClear = (MigrationCommitServiceTest.NODE_COUNT) - 1;
        int newReplicaIndex = (MigrationCommitServiceTest.NODE_COUNT) - 1;
        testSuccessfulShiftDownMigration(MigrationCommitServiceTest.PARTITION_ID_TO_MIGRATE, replicaIndexToClear, oldReplicaIndex, newReplicaIndex);
    }

    @Test
    public void testPartitionBackupShiftDownRollback() throws Exception {
        int oldReplicaIndex = 1;
        int replicaIndexToClear = (MigrationCommitServiceTest.NODE_COUNT) - 1;
        int newReplicaIndex = (MigrationCommitServiceTest.NODE_COUNT) - 1;
        testFailedShiftDownMigration(MigrationCommitServiceTest.PARTITION_ID_TO_MIGRATE, replicaIndexToClear, oldReplicaIndex, newReplicaIndex);
    }

    @Test
    public void testPartitionBackupCopyCommit() throws Exception {
        PartitionReplica destination = clearReplicaIndex(MigrationCommitServiceTest.PARTITION_ID_TO_MIGRATE, ((MigrationCommitServiceTest.NODE_COUNT) - 1));
        MigrationInfo migration = createCopyMigration(MigrationCommitServiceTest.PARTITION_ID_TO_MIGRATE, ((MigrationCommitServiceTest.NODE_COUNT) - 1), destination);
        migrateWithSuccess(migration);
        assertMigrationDestinationCommit(migration);
        assertPartitionDataAfterMigrations();
    }

    @Test
    public void testPartitionBackupCopyRollback() throws Exception {
        PartitionReplica destination = clearReplicaIndex(MigrationCommitServiceTest.PARTITION_ID_TO_MIGRATE, ((MigrationCommitServiceTest.NODE_COUNT) - 1));
        MigrationInfo migration = createCopyMigration(MigrationCommitServiceTest.PARTITION_ID_TO_MIGRATE, ((MigrationCommitServiceTest.NODE_COUNT) - 1), destination);
        migrateWithFailure(migration);
        assertMigrationDestinationRollback(migration);
        assertPartitionDataAfterMigrations();
    }

    @Test
    public void testPartitionBackupShiftUpCommitWithNonNullOwnerOfReplicaIndex() throws Exception {
        int oldReplicaIndex = (MigrationCommitServiceTest.NODE_COUNT) - 1;
        int newReplicaIndex = (MigrationCommitServiceTest.NODE_COUNT) - 2;
        MigrationInfo migration = createShiftUpMigration(MigrationCommitServiceTest.PARTITION_ID_TO_MIGRATE, oldReplicaIndex, newReplicaIndex);
        migrateWithSuccess(migration);
        assertMigrationSourceCommit(migration);
        assertMigrationDestinationCommit(migration);
        assertPartitionDataAfterMigrations();
    }

    @Test
    public void testPartitionBackupShiftUpRollbackWithNonNullOwnerOfReplicaIndex() throws Exception {
        int oldReplicaIndex = (MigrationCommitServiceTest.NODE_COUNT) - 1;
        int newReplicaIndex = (MigrationCommitServiceTest.NODE_COUNT) - 2;
        MigrationInfo migration = createShiftUpMigration(MigrationCommitServiceTest.PARTITION_ID_TO_MIGRATE, oldReplicaIndex, newReplicaIndex);
        migrateWithFailure(migration);
        assertMigrationSourceRollback(migration);
        assertMigrationDestinationRollback(migration);
        assertPartitionDataAfterMigrations();
    }

    @Test
    public void testPartitionBackupShiftUpCommitWithNullOwnerOfReplicaIndex() throws Exception {
        int oldReplicaIndex = (MigrationCommitServiceTest.NODE_COUNT) - 1;
        int newReplicaIndex = (MigrationCommitServiceTest.NODE_COUNT) - 2;
        clearReplicaIndex(MigrationCommitServiceTest.PARTITION_ID_TO_MIGRATE, newReplicaIndex);
        MigrationInfo migration = createShiftUpMigration(MigrationCommitServiceTest.PARTITION_ID_TO_MIGRATE, oldReplicaIndex, newReplicaIndex);
        migrateWithSuccess(migration);
        assertMigrationDestinationCommit(migration);
        assertPartitionDataAfterMigrations();
    }

    @Test
    public void testPartitionBackupShiftUpRollbackWithNullOwnerOfReplicaIndex() throws Exception {
        int oldReplicaIndex = (MigrationCommitServiceTest.NODE_COUNT) - 1;
        int newReplicaIndex = (MigrationCommitServiceTest.NODE_COUNT) - 2;
        clearReplicaIndex(MigrationCommitServiceTest.PARTITION_ID_TO_MIGRATE, newReplicaIndex);
        MigrationInfo migration = createShiftUpMigration(MigrationCommitServiceTest.PARTITION_ID_TO_MIGRATE, oldReplicaIndex, newReplicaIndex);
        migrateWithFailure(migration);
        assertMigrationDestinationRollback(migration);
        assertPartitionDataAfterMigrations();
    }

    private static class RejectMigrationOnComplete extends InternalMigrationListener {
        private final HazelcastInstance instance;

        RejectMigrationOnComplete(HazelcastInstance instance) {
            this.instance = instance;
        }

        @Override
        public void onMigrationComplete(MigrationParticipant participant, MigrationInfo migrationInfo, boolean success) {
            MigrationCommitTest.resetInternalMigrationListener(instance);
            throw new ExpectedRuntimeException("migration is failed intentionally on complete");
        }
    }

    private class CountDownMigrationRollbackOnMaster extends InternalMigrationListener {
        private final CountDownLatch latch = new CountDownLatch(1);

        private final MigrationInfo expected;

        private volatile boolean blockMigrations;

        CountDownMigrationRollbackOnMaster(MigrationInfo expected) {
            this.expected = expected;
        }

        @Override
        public void onMigrationStart(MigrationParticipant participant, MigrationInfo migrationInfo) {
            if ((participant == (MASTER)) && (blockMigrations)) {
                HazelcastTestSupport.assertOpenEventually(blockMigrationStartLatch);
            }
        }

        @Override
        public void onMigrationComplete(MigrationParticipant participant, MigrationInfo migrationInfo, boolean success) {
            if (participant == (DESTINATION)) {
                throw new ExpectedRuntimeException("migration is failed intentionally on complete");
            }
        }

        @Override
        public void onMigrationRollback(MigrationParticipant participant, MigrationInfo migrationInfo) {
            if ((participant == (MASTER)) && (expected.equals(migrationInfo))) {
                blockMigrations = true;
                latch.countDown();
            }
        }
    }

    static final class ClearReplicaRunnable implements PartitionSpecificRunnable {
        private final int partitionId;

        private final NodeEngineImpl nodeEngine;

        ClearReplicaRunnable(int partitionId, NodeEngineImpl nodeEngine) {
            this.partitionId = partitionId;
            this.nodeEngine = nodeEngine;
        }

        @Override
        public void run() {
            InternalPartitionServiceImpl partitionService = nodeEngine.getService(SERVICE_NAME);
            PartitionReplicaManager replicaManager = partitionService.getReplicaManager();
            replicaManager.cancelReplicaSync(partitionId);
            for (ServiceNamespace namespace : replicaManager.getNamespaces(partitionId)) {
                replicaManager.clearPartitionReplicaVersions(partitionId, namespace);
            }
        }

        @Override
        public int getPartitionId() {
            return partitionId;
        }
    }
}

