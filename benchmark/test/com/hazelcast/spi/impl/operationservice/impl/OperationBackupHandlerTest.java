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
package com.hazelcast.spi.impl.operationservice.impl;


import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.internal.partition.InternalPartition;
import com.hazelcast.spi.BackupAwareOperation;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class OperationBackupHandlerTest extends HazelcastTestSupport {
    public static final boolean FORCE_SYNC_ENABLED = true;

    public static final boolean FORCE_SYNC_DISABLED = false;

    public static final boolean BACKPRESSURE_ENABLED = true;

    public static final boolean BACKPRESSURE_DISABLED = false;

    private HazelcastInstance local;

    private OperationServiceImpl operationService;

    private OperationBackupHandler backupHandler;

    int BACKUPS = 4;

    // ============================ syncBackups =================================
    @Test
    public void syncBackups_whenForceSyncEnabled() {
        setup(OperationBackupHandlerTest.BACKPRESSURE_ENABLED);
        // when force sync enabled, we sum tot sync and asyncs
        Assert.assertEquals(0, backupHandler.syncBackups(0, 0, OperationBackupHandlerTest.FORCE_SYNC_ENABLED));
        Assert.assertEquals(1, backupHandler.syncBackups(1, 0, OperationBackupHandlerTest.FORCE_SYNC_ENABLED));
        Assert.assertEquals(0, backupHandler.syncBackups(0, 0, OperationBackupHandlerTest.FORCE_SYNC_ENABLED));
        Assert.assertEquals(3, backupHandler.syncBackups(1, 2, OperationBackupHandlerTest.FORCE_SYNC_ENABLED));
        // checking to see what happens when we are at or above the maximum number of backups
        Assert.assertEquals(BACKUPS, backupHandler.syncBackups(BACKUPS, 0, OperationBackupHandlerTest.FORCE_SYNC_ENABLED));
        Assert.assertEquals(BACKUPS, backupHandler.syncBackups(((BACKUPS) + 1), 0, OperationBackupHandlerTest.FORCE_SYNC_ENABLED));
        Assert.assertEquals(BACKUPS, backupHandler.syncBackups(BACKUPS, 1, OperationBackupHandlerTest.FORCE_SYNC_ENABLED));
    }

    @Test
    public void syncBackups_whenForceSyncDisabled() {
        setup(OperationBackupHandlerTest.BACKPRESSURE_ENABLED);
        // when force-sync disabled, we only look at the sync backups
        Assert.assertEquals(0, backupHandler.syncBackups(0, 0, OperationBackupHandlerTest.FORCE_SYNC_DISABLED));
        Assert.assertEquals(1, backupHandler.syncBackups(1, 0, OperationBackupHandlerTest.FORCE_SYNC_DISABLED));
        Assert.assertEquals(0, backupHandler.syncBackups(0, 0, OperationBackupHandlerTest.FORCE_SYNC_DISABLED));
        Assert.assertEquals(1, backupHandler.syncBackups(1, 1, OperationBackupHandlerTest.FORCE_SYNC_DISABLED));
        // checking to see what happens when we are at or above the maximum number of backups
        Assert.assertEquals(BACKUPS, backupHandler.syncBackups(BACKUPS, 0, OperationBackupHandlerTest.FORCE_SYNC_DISABLED));
        Assert.assertEquals(BACKUPS, backupHandler.syncBackups(((BACKUPS) + 1), 0, OperationBackupHandlerTest.FORCE_SYNC_DISABLED));
    }

    // ============================ asyncBackups =================================
    @Test
    public void asyncBackups_whenForceSyncDisabled() {
        setup(OperationBackupHandlerTest.BACKPRESSURE_ENABLED);
        // when forceSync disabled, only the async matters
        Assert.assertEquals(0, backupHandler.asyncBackups(0, 0, OperationBackupHandlerTest.FORCE_SYNC_DISABLED));
        Assert.assertEquals(1, backupHandler.asyncBackups(0, 1, OperationBackupHandlerTest.FORCE_SYNC_DISABLED));
        Assert.assertEquals(0, backupHandler.asyncBackups(2, 0, OperationBackupHandlerTest.FORCE_SYNC_DISABLED));
        Assert.assertEquals(1, backupHandler.asyncBackups(2, 1, OperationBackupHandlerTest.FORCE_SYNC_DISABLED));
        // see what happens when we reach maximum number of backups
        Assert.assertEquals(BACKUPS, backupHandler.asyncBackups(0, ((BACKUPS) + 1), OperationBackupHandlerTest.FORCE_SYNC_DISABLED));
    }

    @Test
    public void asyncBackups_whenForceSyncEnabled() {
        setup(true);
        // when forceSync is enabled, then async should always be 0
        Assert.assertEquals(0, backupHandler.asyncBackups(0, 0, OperationBackupHandlerTest.FORCE_SYNC_ENABLED));
        Assert.assertEquals(0, backupHandler.asyncBackups(0, 1, OperationBackupHandlerTest.FORCE_SYNC_ENABLED));
        Assert.assertEquals(0, backupHandler.asyncBackups(2, 0, OperationBackupHandlerTest.FORCE_SYNC_ENABLED));
        Assert.assertEquals(0, backupHandler.asyncBackups(2, 1, OperationBackupHandlerTest.FORCE_SYNC_ENABLED));
        // see what happens when we reach maximum number of backups
        Assert.assertEquals(0, backupHandler.asyncBackups(0, ((BACKUPS) + 1), OperationBackupHandlerTest.FORCE_SYNC_ENABLED));
    }

    // ============================ backup =================================
    @Test(expected = IllegalArgumentException.class)
    public void backup_whenNegativeSyncBackupCount() throws Exception {
        setup(OperationBackupHandlerTest.BACKPRESSURE_ENABLED);
        BackupAwareOperation op = makeOperation((-1), 0);
        backupHandler.sendBackups0(op);
    }

    @Test(expected = IllegalArgumentException.class)
    public void backup_whenTooLargeSyncBackupCount() throws Exception {
        setup(OperationBackupHandlerTest.BACKPRESSURE_ENABLED);
        BackupAwareOperation op = makeOperation(((InternalPartition.MAX_BACKUP_COUNT) + 1), 0);
        backupHandler.sendBackups0(op);
    }

    @Test(expected = IllegalArgumentException.class)
    public void backup_whenNegativeAsyncBackupCount() throws Exception {
        setup(OperationBackupHandlerTest.BACKPRESSURE_ENABLED);
        BackupAwareOperation op = makeOperation(0, (-1));
        backupHandler.sendBackups0(op);
    }

    @Test(expected = IllegalArgumentException.class)
    public void backup_whenTooLargeAsyncBackupCount() throws Exception {
        setup(OperationBackupHandlerTest.BACKPRESSURE_ENABLED);
        BackupAwareOperation op = makeOperation(0, ((InternalPartition.MAX_BACKUP_COUNT) + 1));
        backupHandler.sendBackups0(op);
    }

    @Test(expected = IllegalArgumentException.class)
    public void backup_whenTooLargeSumOfSyncAndAsync() throws Exception {
        setup(OperationBackupHandlerTest.BACKPRESSURE_ENABLED);
        BackupAwareOperation op = makeOperation(1, InternalPartition.MAX_BACKUP_COUNT);
        backupHandler.sendBackups0(op);
    }

    @Test
    public void backup_whenBackpressureDisabled() throws Exception {
        setup(OperationBackupHandlerTest.BACKPRESSURE_DISABLED);
        assertBackup(0, 0, 0);
        assertBackup(0, 1, 0);
        assertBackup(2, 0, 2);
        assertBackup(2, 1, 2);
        assertBackup(BACKUPS, 0, BACKUPS);
        assertBackup(((BACKUPS) + 1), 0, BACKUPS);
    }

    @Test
    public void backup_whenBackpressureEnabled() throws Exception {
        setup(OperationBackupHandlerTest.BACKPRESSURE_ENABLED);
        // when forceSync is enabled, then number of backups=sync+async
        assertBackup(0, 0, 0);
        assertBackup(0, 1, 1);
        assertBackup(2, 0, 2);
        assertBackup(2, 1, 3);
        // see what happens when we reach the maximum number of backups
        assertBackup(0, BACKUPS, BACKUPS);
        assertBackup(0, ((BACKUPS) + 1), BACKUPS);
    }
}

