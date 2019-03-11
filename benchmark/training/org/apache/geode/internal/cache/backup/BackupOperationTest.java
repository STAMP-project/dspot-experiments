/**
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license
 * agreements. See the NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The ASF licenses this file to You under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License. You may obtain a
 * copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package org.apache.geode.internal.cache.backup;


import org.apache.geode.distributed.internal.DistributionManager;
import org.apache.geode.internal.cache.InternalCache;
import org.apache.geode.internal.cache.backup.BackupOperation.MissingPersistentMembersProvider;
import org.apache.geode.management.BackupStatus;
import org.apache.geode.management.ManagementException;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InOrder;
import org.mockito.Mockito;


public class BackupOperationTest {
    private FlushToDiskFactory flushToDiskFactory;

    private PrepareBackupFactory prepareBackupFactory;

    private AbortBackupFactory abortBackupFactory;

    private FinishBackupFactory finishBackupFactory;

    private DistributionManager dm;

    private InternalCache cache;

    private BackupLockService backupLockService;

    private MissingPersistentMembersProvider missingPersistentMembersProvider;

    private String targetDirPath;

    private String baselineDirPath;

    private BackupOperation backupOperation;

    @Test
    public void hasNoBackedUpDiskStoresIfNoMembers() {
        BackupStatus backupStatus = backupOperation.backupAllMembers(targetDirPath, baselineDirPath);
        assertThat(backupStatus.getBackedUpDiskStores()).hasSize(0);
    }

    @Test
    public void hasNoOfflineDiskStoresIfNoMembers() {
        BackupStatus backupStatus = backupOperation.backupAllMembers(targetDirPath, baselineDirPath);
        assertThat(backupStatus.getOfflineDiskStores()).hasSize(0);
    }

    @Test
    public void flushPrepareFinishOrdering() {
        backupOperation.backupAllMembers(targetDirPath, baselineDirPath);
        InOrder inOrder = Mockito.inOrder(flushToDiskFactory, prepareBackupFactory, finishBackupFactory);
        inOrder.verify(flushToDiskFactory).createFlushToDiskStep(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any());
        inOrder.verify(prepareBackupFactory).createPrepareBackupStep(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any());
        inOrder.verify(finishBackupFactory).createFinishBackupStep(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any());
    }

    @Test
    public void abortIfPrepareFails() {
        PrepareBackupStep prepareBackupStep = Mockito.mock(PrepareBackupStep.class);
        RuntimeException thrownBySend = new RuntimeException("thrownBySend");
        Mockito.when(prepareBackupFactory.createPrepareBackupStep(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn(prepareBackupStep);
        Mockito.when(prepareBackupStep.send()).thenThrow(thrownBySend);
        assertThatThrownBy(() -> backupOperation.backupAllMembers(targetDirPath, baselineDirPath)).isSameAs(thrownBySend);
        InOrder inOrder = Mockito.inOrder(flushToDiskFactory, prepareBackupFactory, abortBackupFactory);
        inOrder.verify(flushToDiskFactory).createFlushToDiskStep(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any());
        inOrder.verify(prepareBackupFactory).createPrepareBackupStep(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any());
        inOrder.verify(abortBackupFactory).createAbortBackupStep(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any());
        Mockito.verifyZeroInteractions(finishBackupFactory);
    }

    @Test
    public void failedToAcquireLockThrows() {
        Mockito.when(backupLockService.obtainLock(dm)).thenReturn(false);
        assertThatThrownBy(() -> backupOperation.backupAllMembers(targetDirPath, baselineDirPath)).isInstanceOf(ManagementException.class);
        Mockito.verifyZeroInteractions(flushToDiskFactory, prepareBackupFactory, abortBackupFactory, finishBackupFactory);
    }
}

