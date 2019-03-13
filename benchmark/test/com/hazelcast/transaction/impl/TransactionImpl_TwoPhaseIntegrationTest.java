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


import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.spi.impl.NodeEngineImpl;
import com.hazelcast.test.AssertTask;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.QuickTest;
import com.hazelcast.transaction.TransactionException;
import com.hazelcast.transaction.TransactionOptions;
import com.hazelcast.transaction.impl.TransactionManagerServiceImpl.TxBackupLog;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category(QuickTest.class)
public class TransactionImpl_TwoPhaseIntegrationTest extends HazelcastTestSupport {
    private HazelcastInstance[] cluster;

    private TransactionManagerServiceImpl localTxService;

    private TransactionManagerServiceImpl remoteTxService;

    private NodeEngineImpl localNodeEngine;

    private String txOwner;

    // =================== prepare ===========================================
    @Test
    public void prepare_whenSingleItemAndDurabilityOne_thenNoBackupLog() {
        TransactionOptions options = new TransactionOptions().setTransactionType(TWO_PHASE).setDurability(1);
        TransactionImpl tx = new TransactionImpl(localTxService, localNodeEngine, options, txOwner);
        tx.begin();
        MockTransactionLogRecord record = new MockTransactionLogRecord();
        tx.add(record);
        tx.prepare();
        assertPrepared(tx);
        assertNoBackupLogOnRemote(tx);
        record.assertPrepareCalled().assertCommitNotCalled().assertRollbackNotCalled();
    }

    @Test
    public void prepare_whenMultipleItemsAndDurabilityOne_thenBackupLog() {
        TransactionOptions options = new TransactionOptions().setTransactionType(TWO_PHASE).setDurability(1);
        TransactionImpl tx = new TransactionImpl(localTxService, localNodeEngine, options, txOwner);
        tx.begin();
        MockTransactionLogRecord record1 = new MockTransactionLogRecord();
        tx.add(record1);
        MockTransactionLogRecord record2 = new MockTransactionLogRecord();
        tx.add(record2);
        tx.prepare();
        assertPrepared(tx);
        TxBackupLog log = remoteTxService.txBackupLogs.get(tx.getTxnId());
        Assert.assertNotNull(log);
        Assert.assertEquals(State.COMMITTING, log.state);
        record1.assertPrepareCalled().assertCommitNotCalled().assertRollbackNotCalled();
        record2.assertPrepareCalled().assertCommitNotCalled().assertRollbackNotCalled();
    }

    @Test
    public void prepare_whenMultipleItemsAndDurabilityZero_thenNoBackupLog() {
        TransactionOptions options = new TransactionOptions().setTransactionType(TWO_PHASE).setDurability(0);
        TransactionImpl tx = new TransactionImpl(localTxService, localNodeEngine, options, txOwner);
        tx.begin();
        MockTransactionLogRecord record1 = new MockTransactionLogRecord();
        tx.add(record1);
        MockTransactionLogRecord record2 = new MockTransactionLogRecord();
        tx.add(record2);
        tx.prepare();
        assertPrepared(tx);
        assertNoBackupLogOnRemote(tx);
        record1.assertPrepareCalled().assertCommitNotCalled().assertRollbackNotCalled();
        record2.assertPrepareCalled().assertCommitNotCalled().assertRollbackNotCalled();
    }

    @Test
    public void testPrepareFailed() {
        TransactionOptions options = new TransactionOptions().setTransactionType(TWO_PHASE).setDurability(1);
        TransactionImpl tx = new TransactionImpl(localTxService, localNodeEngine, options, txOwner);
        tx.begin();
        MockTransactionLogRecord record = new MockTransactionLogRecord().failPrepare();
        tx.add(record);
        try {
            tx.prepare();
            Assert.fail();
        } catch (TransactionException expected) {
        }
        assertPreparing(tx);
        assertNoBackupLogOnRemote(tx);
        record.assertPrepareCalled().assertCommitNotCalled().assertRollbackNotCalled();
    }

    // =================== commit ===========================================
    @Test
    public void commit_whenSingleItemAndDurabilityOne_thenNoBackupLog() {
        TransactionOptions options = new TransactionOptions().setTransactionType(TWO_PHASE).setDurability(1);
        TransactionImpl tx = new TransactionImpl(localTxService, localNodeEngine, options, txOwner);
        tx.begin();
        MockTransactionLogRecord record = new MockTransactionLogRecord();
        tx.add(record);
        tx.prepare();
        tx.commit();
        assertCommitted(tx);
        assertNoBackupLogOnRemote(tx);
        record.assertPrepareCalled().assertCommitCalled().assertRollbackNotCalled();
    }

    @Test
    public void commit_whenMultipleItemsAndDurabilityOne_thenBackupLog() {
        TransactionOptions options = new TransactionOptions().setTransactionType(TWO_PHASE).setDurability(1);
        final TransactionImpl tx = new TransactionImpl(localTxService, localNodeEngine, options, txOwner);
        tx.begin();
        MockTransactionLogRecord record1 = new MockTransactionLogRecord();
        tx.add(record1);
        MockTransactionLogRecord record2 = new MockTransactionLogRecord();
        tx.add(record2);
        tx.prepare();
        tx.commit();
        assertCommitted(tx);
        // it can take some time because the transaction doesn't sync on purging the backups.
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() throws Exception {
                assertNoBackupLogOnRemote(tx);
            }
        });
        record1.assertPrepareCalled().assertCommitCalled().assertRollbackNotCalled();
        record2.assertPrepareCalled().assertCommitCalled().assertRollbackNotCalled();
    }

    @Test
    public void commit_whenMultipleItemsAndDurabilityZero_thenNoBackupLog() {
        TransactionOptions options = new TransactionOptions().setTransactionType(TWO_PHASE).setDurability(0);
        TransactionImpl tx = new TransactionImpl(localTxService, localNodeEngine, options, txOwner);
        tx.begin();
        MockTransactionLogRecord record1 = new MockTransactionLogRecord();
        tx.add(record1);
        MockTransactionLogRecord record2 = new MockTransactionLogRecord();
        tx.add(record2);
        tx.prepare();
        tx.commit();
        assertCommitted(tx);
        assertNoBackupLogOnRemote(tx);
        record1.assertPrepareCalled().assertCommitCalled().assertRollbackNotCalled();
        record2.assertPrepareCalled().assertCommitCalled().assertRollbackNotCalled();
    }

    @Test
    public void commit_whenPrepareSkippedButCommitRunsIntoConflict() {
        TransactionOptions options = new TransactionOptions().setTransactionType(TWO_PHASE).setDurability(1);
        TransactionImpl tx = new TransactionImpl(localTxService, localNodeEngine, options, txOwner);
        tx.begin();
        MockTransactionLogRecord record = new MockTransactionLogRecord().failCommit();
        tx.add(record);
        try {
            tx.commit();
            Assert.fail();
        } catch (TransactionException expected) {
        }
        assertCommitFailed(tx);
        assertNoBackupLogOnRemote(tx);
        record.assertPrepareNotCalled().assertCommitCalled().assertRollbackNotCalled();
    }

    // =================== rollback ===========================================
    @Test
    public void rollback_whenSingleItemAndDurabilityOne_thenNoBackupLog() {
        TransactionOptions options = new TransactionOptions().setTransactionType(TWO_PHASE).setDurability(1);
        TransactionImpl tx = new TransactionImpl(localTxService, localNodeEngine, options, txOwner);
        tx.begin();
        MockTransactionLogRecord record = new MockTransactionLogRecord();
        tx.add(record);
        tx.prepare();
        tx.rollback();
        assertRolledBack(tx);
        assertNoBackupLogOnRemote(tx);
        record.assertPrepareCalled().assertCommitNotCalled().assertRollbackCalled();
    }

    @Test
    public void rollback_whenMultipleItemsAndDurabilityOne_thenBackupLog() {
        TransactionOptions options = new TransactionOptions().setTransactionType(TWO_PHASE).setDurability(1);
        final TransactionImpl tx = new TransactionImpl(localTxService, localNodeEngine, options, txOwner);
        tx.begin();
        MockTransactionLogRecord record1 = new MockTransactionLogRecord();
        tx.add(record1);
        MockTransactionLogRecord record2 = new MockTransactionLogRecord();
        tx.add(record2);
        tx.prepare();
        tx.rollback();
        assertRolledBack(tx);
        // it can take some time because the transaction doesn't sync on purging the backups.
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() throws Exception {
                assertNoBackupLogOnRemote(tx);
            }
        });
        record1.assertPrepareCalled().assertCommitNotCalled().assertRollbackCalled();
        record2.assertPrepareCalled().assertCommitNotCalled().assertRollbackCalled();
    }

    @Test
    public void rollback_whenMultipleItemsAndDurabilityZero_thenNoBackupLog() {
        TransactionOptions options = new TransactionOptions().setTransactionType(TWO_PHASE).setDurability(0);
        TransactionImpl tx = new TransactionImpl(localTxService, localNodeEngine, options, txOwner);
        tx.begin();
        MockTransactionLogRecord record1 = new MockTransactionLogRecord();
        tx.add(record1);
        MockTransactionLogRecord record2 = new MockTransactionLogRecord();
        tx.add(record2);
        tx.prepare();
        tx.rollback();
        assertRolledBack(tx);
        assertNoBackupLogOnRemote(tx);
        record1.assertPrepareCalled().assertCommitNotCalled().assertRollbackCalled();
        record2.assertPrepareCalled().assertCommitNotCalled().assertRollbackCalled();
    }

    @Test
    public void prepare_whenMultipleItemsAndDurabilityOne_thenRemoveBackupLog() {
        final String txOwner = localNodeEngine.getLocalMember().getUuid();
        TransactionOptions options = new TransactionOptions().setTransactionType(TWO_PHASE).setDurability(1);
        final TransactionImpl tx = new TransactionImpl(localTxService, localNodeEngine, options, txOwner);
        tx.begin();
        tx.add(new MockTransactionLogRecord());
        tx.add(new MockTransactionLogRecord());
        tx.prepare();
        assertPrepared(tx);
        TxBackupLog log = remoteTxService.txBackupLogs.get(tx.getTxnId());
        Assert.assertNotNull(log);
        cluster[0].shutdown();
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() throws Exception {
                Assert.assertFalse(remoteTxService.txBackupLogs.containsKey(tx.getTxnId()));
            }
        });
    }
}

