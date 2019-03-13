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
import com.taobao.metamorphosis.server.network.SessionContext;
import com.taobao.metamorphosis.transaction.TransactionId;
import java.util.concurrent.ConcurrentHashMap;
import javax.transaction.xa.XAException;
import org.junit.Assert;
import org.junit.Test;


public class LocalTransactionUnitTest extends TransactionUnitTest {
    private LocalTransaction localTransaction;

    private SessionContext context;

    private ConcurrentHashMap<TransactionId, Transaction> txMap;

    @Test(expected = XAException.class)
    public void testPrepare() throws Exception {
        this.replay();
        this.localTransaction.prepare();
    }

    @Test
    public void testCommit() throws Exception {
        this.mockStoreCommitOnePhase();
        this.replay();
        this.localTransaction.commit(true);
        Assert.assertEquals(FINISHED_STATE, this.localTransaction.getState());
        Assert.assertNull(this.txMap.get(this.xid));
    }

    @Test(expected = XAException.class)
    public void testCommitPrepared() throws Exception {
        this.localTransaction.setState(PREPARED_STATE);
        this.replay();
        this.localTransaction.commit(true);
    }

    @Test
    public void testRollback() throws Exception {
        this.mockStoreRollback();
        this.replay();
        this.localTransaction.rollback();
        Assert.assertEquals(FINISHED_STATE, this.localTransaction.getState());
        Assert.assertNull(this.txMap.get(this.xid));
    }
}

