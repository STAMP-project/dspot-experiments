/**
 * (C) 2007-2012 Alibaba Group Holding Limited.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * Authors:
 *   wuhua <wq163@163.com> , boyan <killme2008@gmail.com>
 */
package com.taobao.metamorphosis.server.assembly;


import Transaction.FINISHED_STATE;
import Transaction.HEURISTIC_COMMIT_STATE;
import com.taobao.gecko.service.Connection;
import com.taobao.metamorphosis.server.CommandProcessor;
import com.taobao.metamorphosis.server.network.SessionContext;
import com.taobao.metamorphosis.server.store.MessageStore;
import com.taobao.metamorphosis.server.transaction.BaseTransactionUnitTest;
import com.taobao.metamorphosis.server.transaction.Transaction;
import com.taobao.metamorphosis.server.transaction.XATransaction;
import com.taobao.metamorphosis.server.utils.XIDGenerator;
import com.taobao.metamorphosis.transaction.LocalTransactionId;
import com.taobao.metamorphosis.transaction.TransactionId;
import java.util.Map;
import javax.transaction.xa.XAException;
import org.junit.Assert;
import org.junit.Test;


public class TransactionalCommandProcessorUnitTest extends BaseTransactionUnitTest {
    private TransactionalCommandProcessor processor;

    private CommandProcessor next;

    private Connection conn;

    @Test
    public void testBeginPutCommitOnePhase() throws Exception {
        final SessionContext context = new com.taobao.metamorphosis.server.network.SessionContextImpl("test", this.conn);
        final TransactionId xid = new LocalTransactionId("test", 100);
        Assert.assertNull(context.getTransactions().get(xid));
        this.processor.beginTransaction(context, xid, 0);
        final Transaction tx = context.getTransactions().get(xid);
        Assert.assertNotNull(tx);
        Assert.assertSame(xid, tx.getTransactionId());
        this.replay();
        this.processor.processPutCommand(new com.taobao.metamorphosis.network.PutCommand("topic1", 2, "hello".getBytes(), xid, 0, 1), context, null);
        final MessageStore store = this.messageStoreManager.getOrCreateMessageStore("topic1", 2);
        store.flush();
        Assert.assertEquals(0, store.getSizeInBytes());
        // commit one phase
        this.processor.commitTransaction(context, xid, true);
        store.flush();
        Assert.assertEquals(FINISHED_STATE, tx.getState());
        Assert.assertNull(context.getTransactions().get(xid));
        Assert.assertTrue(((store.getSizeInBytes()) > 0));
    }

    @Test
    public void testSetTransactionTimeout() throws Exception {
        final SessionContext context = new com.taobao.metamorphosis.server.network.SessionContextImpl("test", this.conn);
        final TransactionId xid = XIDGenerator.createXID(100);
        Assert.assertNull(context.getTransactions().get(xid));
        // ?????????
        // this.processor.setTransactionTimeout(context, xid, 3);
        this.replay();
        this.processor.beginTransaction(context, xid, 3);
        final Transaction tx = this.processor.getTransaction(context, xid);
        Assert.assertNotNull(tx);
        Assert.assertSame(xid, tx.getTransactionId());
        Assert.assertNotNull(tx.getTimeoutRef());
        Assert.assertFalse(tx.getTimeoutRef().isExpired());
        Thread.sleep(4000);
        Assert.assertTrue(tx.getTimeoutRef().isExpired());
        // It's rolled back
        try {
            Assert.assertNull(this.processor.getTransaction(context, xid));
            Assert.fail();
        } catch (final XAException e) {
            Assert.assertEquals((("XA transaction '" + xid) + "' has not been started."), e.getMessage());
        }
    }

    @Test
    public void testBeginPreparedCommitTwoPhase() throws Exception {
        final SessionContext context = new com.taobao.metamorphosis.server.network.SessionContextImpl("test", this.conn);
        final TransactionId xid = XIDGenerator.createXID(100);
        Assert.assertNull(context.getTransactions().get(xid));
        this.processor.beginTransaction(context, xid, 0);
        final Transaction tx = this.processor.getTransaction(context, xid);
        Assert.assertNotNull(tx);
        Assert.assertSame(xid, tx.getTransactionId());
        this.replay();
        this.processor.processPutCommand(new com.taobao.metamorphosis.network.PutCommand("topic1", 2, "hello".getBytes(), xid, 0, 1), context, null);
        final MessageStore store = this.messageStoreManager.getOrCreateMessageStore("topic1", 2);
        store.flush();
        Assert.assertEquals(0, store.getSizeInBytes());
        this.processor.prepareTransaction(context, xid);
        Assert.assertSame(tx, this.processor.getTransaction(context, xid));
        // commit two phase
        this.processor.commitTransaction(context, xid, false);
        store.flush();
        Assert.assertEquals(FINISHED_STATE, tx.getState());
        Assert.assertNull(context.getTransactions().get(xid));
        Assert.assertTrue(((store.getSizeInBytes()) > 0));
    }

    @Test(expected = XAException.class)
    public void testPutNotBeginTransaction() throws Exception {
        final SessionContext context = new com.taobao.metamorphosis.server.network.SessionContextImpl("test", this.conn);
        final TransactionId xid = XIDGenerator.createXID(100);
        this.replay();
        this.processor.processPutCommand(new com.taobao.metamorphosis.network.PutCommand("topic1", 2, "hello".getBytes(), xid, 0, 1), context, null);
    }

    @Test
    public void testBeginPutPutCloseRecoverGetPreparedTransactionsCommit() throws Exception {
        final SessionContext context = new com.taobao.metamorphosis.server.network.SessionContextImpl("test", this.conn);
        final TransactionId xid = XIDGenerator.createXID(100);
        Assert.assertNull(context.getTransactions().get(xid));
        this.processor.beginTransaction(context, xid, 0);
        this.replay();
        this.processor.processPutCommand(new com.taobao.metamorphosis.network.PutCommand("topic1", 2, "hello".getBytes(), xid, 0, 1), context, null);
        this.processor.processPutCommand(new com.taobao.metamorphosis.network.PutCommand("topic1", 2, "world".getBytes(), xid, 0, 1), context, null);
        MessageStore store = this.messageStoreManager.getOrCreateMessageStore("topic1", 2);
        store.flush();
        Assert.assertEquals(0, store.getSizeInBytes());
        Assert.assertEquals(0, this.processor.getPreparedTransactions(context, XIDGenerator.UNIQUE_QUALIFIER).length);
        // prepare
        this.processor.prepareTransaction(context, xid);
        Assert.assertEquals(1, this.processor.getPreparedTransactions(context, XIDGenerator.UNIQUE_QUALIFIER).length);
        Assert.assertEquals(1, this.processor.getPreparedTransactions(context, null).length);
        Assert.assertEquals(0, this.processor.getPreparedTransactions(context, "unknown").length);
        Assert.assertSame(this.processor.getPreparedTransactions(context, XIDGenerator.UNIQUE_QUALIFIER)[0], this.processor.getTransaction(context, xid).getTransactionId());
        // close and reopen it
        this.tearDown();
        this.init(this.path);
        this.newProcessor();
        this.processor.recoverPreparedTransactions();
        Assert.assertEquals(1, this.processor.getPreparedTransactions(context, XIDGenerator.UNIQUE_QUALIFIER).length);
        Assert.assertEquals(1, this.processor.getPreparedTransactions(context, null).length);
        Assert.assertEquals(0, this.processor.getPreparedTransactions(context, "unknown").length);
        Assert.assertSame(this.processor.getPreparedTransactions(context, XIDGenerator.UNIQUE_QUALIFIER)[0], this.processor.getTransaction(context, xid).getTransactionId());
        store = this.messageStoreManager.getOrCreateMessageStore("topic1", 2);
        store.flush();
        Assert.assertEquals(0, store.getSizeInBytes());
        // commit two phase
        this.processor.commitTransaction(context, xid, false);
        store.flush();
        Assert.assertEquals(0, this.processor.getPreparedTransactions(context, XIDGenerator.UNIQUE_QUALIFIER).length);
        Assert.assertNull(context.getTransactions().get(xid));
        store.flush();
        Assert.assertTrue(((store.getSizeInBytes()) > 0));
    }

    @Test
    public void testBeginPutRollback() throws Exception {
        final SessionContext context = new com.taobao.metamorphosis.server.network.SessionContextImpl("test", this.conn);
        final TransactionId xid = new LocalTransactionId("test", 100);
        Assert.assertNull(context.getTransactions().get(xid));
        this.processor.beginTransaction(context, xid, 0);
        final Transaction tx = context.getTransactions().get(xid);
        Assert.assertNotNull(tx);
        Assert.assertSame(xid, tx.getTransactionId());
        this.replay();
        this.processor.processPutCommand(new com.taobao.metamorphosis.network.PutCommand("topic1", 2, "hello".getBytes(), xid, 0, 1), context, null);
        final MessageStore store = this.messageStoreManager.getOrCreateMessageStore("topic1", 2);
        store.flush();
        Assert.assertEquals(0, store.getSizeInBytes());
        // rollback
        this.processor.rollbackTransaction(context, xid);
        store.flush();
        Assert.assertEquals(FINISHED_STATE, tx.getState());
        Assert.assertNull(context.getTransactions().get(xid));
        store.flush();
        Assert.assertEquals(0, store.getSizeInBytes());
    }

    @Test
    public void testBeginPutPrepareRollbackCloseRecover() throws Exception {
        final SessionContext context = new com.taobao.metamorphosis.server.network.SessionContextImpl("test", this.conn);
        final TransactionId xid = XIDGenerator.createXID(100);
        Assert.assertNull(context.getTransactions().get(xid));
        this.processor.beginTransaction(context, xid, 0);
        final Transaction tx = this.processor.getTransaction(context, xid);
        Assert.assertNotNull(tx);
        Assert.assertSame(xid, tx.getTransactionId());
        this.replay();
        this.processor.processPutCommand(new com.taobao.metamorphosis.network.PutCommand("topic1", 2, "hello".getBytes(), xid, 0, 1), context, null);
        MessageStore store = this.messageStoreManager.getOrCreateMessageStore("topic1", 2);
        store.flush();
        Assert.assertEquals(0, store.getSizeInBytes());
        // prepare
        this.processor.prepareTransaction(context, xid);
        Assert.assertSame(tx, this.processor.getTransaction(context, xid));
        // rollback
        this.processor.rollbackTransaction(context, xid);
        store.flush();
        Assert.assertEquals(FINISHED_STATE, tx.getState());
        Assert.assertNull(context.getTransactions().get(xid));
        Assert.assertEquals(0, store.getSizeInBytes());
        // close and reopen it
        this.tearDown();
        this.init(this.path);
        this.newProcessor();
        this.processor.recoverPreparedTransactions();
        // ???prepare???
        store = this.messageStoreManager.getOrCreateMessageStore("topic1", 2);
        store.flush();
        Assert.assertEquals(0, store.getSizeInBytes());
        Assert.assertEquals(0, this.processor.getPreparedTransactions(context, XIDGenerator.UNIQUE_QUALIFIER).length);
    }

    @Test
    public void testCommitTransactionHeuristicallyRecoverHeuristicTransactions() throws Exception {
        final SessionContext context = new com.taobao.metamorphosis.server.network.SessionContextImpl("test", this.conn);
        final TransactionId xid = XIDGenerator.createXID(100);
        Assert.assertNull(context.getTransactions().get(xid));
        this.processor.beginTransaction(context, xid, 0);
        final Transaction tx = this.processor.getTransaction(context, xid);
        Assert.assertNotNull(tx);
        Assert.assertSame(xid, tx.getTransactionId());
        this.replay();
        this.processor.processPutCommand(new com.taobao.metamorphosis.network.PutCommand("topic1", 2, "hello".getBytes(), xid, 0, 1), context, null);
        final MessageStore store = this.messageStoreManager.getOrCreateMessageStore("topic1", 2);
        store.flush();
        Assert.assertEquals(0, store.getSizeInBytes());
        // prepare
        this.processor.prepareTransaction(context, xid);
        Assert.assertSame(tx, this.processor.getTransaction(context, xid));
        // ?????
        this.processor.commitTransactionHeuristically(xid.getTransactionKey(), false);
        Map<TransactionId, XATransaction> heuristicTxMap = this.processor.getXAHeuristicTransactions();
        Assert.assertFalse(heuristicTxMap.isEmpty());
        Assert.assertTrue(heuristicTxMap.containsKey(xid));
        Assert.assertSame(tx, heuristicTxMap.get(xid));
        Assert.assertEquals(HEURISTIC_COMMIT_STATE, heuristicTxMap.get(xid).getState());
        // ?????????recover
        this.processor.dispose();
        this.newProcessor();
        this.processor.recoverHeuristicTransactions();
        // ??????recover
        heuristicTxMap = this.processor.getXAHeuristicTransactions();
        Assert.assertFalse(heuristicTxMap.isEmpty());
        Assert.assertTrue(heuristicTxMap.containsKey(xid));
        Assert.assertEquals(tx.getTransactionId(), heuristicTxMap.get(xid).getTransactionId());
        Assert.assertEquals(HEURISTIC_COMMIT_STATE, heuristicTxMap.get(xid).getState());
    }
}

