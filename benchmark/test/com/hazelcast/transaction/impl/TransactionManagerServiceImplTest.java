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
package com.hazelcast.transaction.impl;


import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.QuickTest;
import com.hazelcast.transaction.TransactionException;
import java.util.LinkedList;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

import static State.ROLLED_BACK;


@RunWith(HazelcastParallelClassRunner.class)
@Category(QuickTest.class)
public class TransactionManagerServiceImplTest extends HazelcastTestSupport {
    private TransactionManagerServiceImpl txService;

    // ================= createBackupLog ===================================
    @Test
    public void createBackupLog_whenNotCreated() {
        String callerUuid = "somecaller";
        String txId = "tx1";
        txService.createBackupLog(callerUuid, txId);
        assertTxLogState(txId, State.ACTIVE);
    }

    @Test(expected = TransactionException.class)
    public void createBackupLog_whenAlreadyExist() {
        String callerUuid = "somecaller";
        String txId = "tx1";
        txService.createBackupLog(callerUuid, txId);
        txService.createBackupLog(callerUuid, txId);
    }

    // ================= rollbackBackupLog ===================================
    @Test(expected = TransactionException.class)
    public void replicaBackupLog_whenNotExist_thenTransactionException() {
        List<TransactionLogRecord> records = new LinkedList<TransactionLogRecord>();
        txService.replicaBackupLog(records, "notexist", "notexist", 1, 1);
    }

    @Test
    public void replicaBackupLog_whenExist() {
        String callerUuid = "somecaller";
        String txId = "tx1";
        txService.createBackupLog(callerUuid, txId);
        List<TransactionLogRecord> records = new LinkedList<TransactionLogRecord>();
        txService.replicaBackupLog(records, callerUuid, txId, 1, 1);
        assertTxLogState(txId, State.COMMITTING);
    }

    @Test(expected = TransactionException.class)
    public void replicaBackupLog_whenNotActive() {
        String callerUuid = "somecaller";
        String txId = "tx1";
        txService.createBackupLog(callerUuid, txId);
        txService.txBackupLogs.get(txId).state = ROLLED_BACK;
        List<TransactionLogRecord> records = new LinkedList<TransactionLogRecord>();
        txService.replicaBackupLog(records, callerUuid, txId, 1, 1);
    }

    // ================= rollbackBackupLog ===================================
    @Test
    public void rollbackBackupLog_whenExist() {
        String callerUuid = "somecaller";
        String txId = "tx1";
        txService.createBackupLog(callerUuid, txId);
        txService.rollbackBackupLog(txId);
        assertTxLogState(txId, State.ROLLING_BACK);
    }

    @Test
    public void rollbackBackupLog_whenNotExist_thenIgnored() {
        txService.rollbackBackupLog("notexist");
    }

    // ================= purgeBackupLog ===================================
    @Test
    public void purgeBackupLog_whenExist_thenRemoved() {
        String callerUuid = "somecaller";
        String txId = "tx1";
        txService.createBackupLog(callerUuid, txId);
        txService.purgeBackupLog(txId);
        Assert.assertFalse(txService.txBackupLogs.containsKey(txId));
    }

    @Test
    public void purgeBackupLog_whenNotExist_thenIgnored() {
        txService.purgeBackupLog("notexist");
    }
}

