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
package com.taobao.metamorphosis.server.transaction;


import Transaction.FINISHED_STATE;
import Transaction.PREPARED_STATE;
import com.taobao.metamorphosis.server.CommandProcessor;
import javax.transaction.xa.XAException;
import javax.transaction.xa.XAResource;
import org.junit.Assert;
import org.junit.Test;


public class XATransactionUnitTest extends TransactionUnitTest {
    private XATransaction tx;

    private CommandProcessor processor;

    @Test
    public void testCommitOnePhase() throws Exception {
        Assert.assertFalse(this.tx.isPrepared());
        this.mockRemoveTx();
        this.replay();
        this.tx.commit(true);
        Assert.assertEquals(this.tx.getState(), FINISHED_STATE);
    }

    @Test
    public void testCommitTwoPhase() throws Exception {
        this.tx.setTransactionInUse();
        this.replay();
        try {
            this.tx.commit(false);
            Assert.fail();
        } catch (final XAException e) {
            Assert.assertEquals("Cannot do 2 phase commit if the transaction has not been prepared.", e.getMessage());
        }
    }

    @Test
    public void testPrepareNoWorkDone() throws Exception {
        this.mockRemoveTx();
        this.replay();
        Assert.assertEquals(XAResource.XA_RDONLY, this.tx.prepare());
        Assert.assertEquals(this.tx.getState(), FINISHED_STATE);
        Assert.assertFalse(this.tx.isPrepared());
    }

    @Test
    public void testPrepareCommitTwoPhase() throws Exception {
        this.tx.setTransactionInUse();
        this.mockStorePrepare();
        this.mockStoreCommitTwoPhase();
        this.mockRemoveTx();
        this.replay();
        Assert.assertEquals(XAResource.XA_OK, this.tx.prepare());
        Assert.assertEquals(this.tx.getState(), PREPARED_STATE);
        Assert.assertTrue(this.tx.isPrepared());
        this.tx.commit(false);
        Assert.assertEquals(this.tx.getState(), FINISHED_STATE);
    }

    @Test
    public void testRollback() throws Exception {
        this.mockRemoveTx();
        this.replay();
        this.tx.rollback();
        Assert.assertEquals(this.tx.getState(), FINISHED_STATE);
    }

    @Test
    public void testPrepareRollback() throws Exception {
        this.tx.setTransactionInUse();
        this.mockStorePrepare();
        this.mockStoreRollback();
        this.mockRemoveTx();
        this.replay();
        Assert.assertEquals(XAResource.XA_OK, this.tx.prepare());
        this.tx.rollback();
        Assert.assertEquals(this.tx.getState(), FINISHED_STATE);
    }

    @Test
    public void testPrepareCommitRollback() throws Exception {
        this.tx.setTransactionInUse();
        this.mockStorePrepare();
        this.mockStoreCommitTwoPhase();
        this.mockStoreRollback();
        this.mockRemoveTx();
        this.replay();
        Assert.assertEquals(XAResource.XA_OK, this.tx.prepare());
        this.tx.commit(false);
        Assert.assertEquals(this.tx.getState(), FINISHED_STATE);
        this.tx.rollback();
        Assert.assertEquals(this.tx.getState(), FINISHED_STATE);
    }
}

