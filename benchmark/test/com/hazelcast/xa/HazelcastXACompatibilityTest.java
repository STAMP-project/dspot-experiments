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
package com.hazelcast.xa;


import com.hazelcast.core.TransactionalMap;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import com.hazelcast.transaction.HazelcastXAResource;
import com.hazelcast.transaction.TransactionContext;
import java.util.concurrent.CountDownLatch;
import javax.transaction.xa.XAException;
import javax.transaction.xa.XAResource;
import javax.transaction.xa.Xid;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class HazelcastXACompatibilityTest extends HazelcastTestSupport {
    private HazelcastXAResource xaResource;

    private HazelcastXAResource secondXaResource;

    private Xid xid;

    @Test
    public void testRecoveryRequiresRollbackOfPreparedXidOnSecondXAResource() throws Exception {
        doSomeWorkWithXa(xaResource);
        performPrepareWithXa(xaResource);
        performRollbackWithXa(secondXaResource);
    }

    @Test
    public void testRecoveryRequiresCommitOfPreparedXidOnSecondXAResource() throws Exception {
        doSomeWorkWithXa(xaResource);
        performPrepareWithXa(xaResource);
        performCommitWithXa(secondXaResource);
    }

    @Test
    public void testRecoveryReturnsPreparedXidOnSecondXAResource() throws Exception {
        doSomeWorkWithXa(xaResource);
        performPrepareWithXa(xaResource);
        assertRecoversXid(secondXaResource);
    }

    @Test
    public void testRecoveryReturnsPreparedXidOnXAResource() throws Exception {
        doSomeWorkWithXa(xaResource);
        performPrepareWithXa(xaResource);
        assertRecoversXid(xaResource);
    }

    @Test
    public void testRecoveryRequiresRollbackOfUnknownXid() {
        performRollbackWithXa(xaResource);
    }

    @Test
    public void testIsSameRm() throws Exception {
        Assert.assertTrue(xaResource.isSameRM(secondXaResource));
    }

    @Test
    public void testRecoveryAllowedAtAnyTime() throws Exception {
        recover(xaResource);
        doSomeWorkWithXa(xaResource);
        recover(xaResource);
        performPrepareWithXa(xaResource);
        recover(xaResource);
        performCommitWithXa(xaResource);
        recover(xaResource);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testManualBeginShouldThrowException() throws Exception {
        xaResource.start(xid, XAResource.TMNOFLAGS);
        TransactionContext transactionContext = xaResource.getTransactionContext();
        transactionContext.beginTransaction();
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testManualCommitShouldThrowException() throws Exception {
        xaResource.start(xid, XAResource.TMNOFLAGS);
        TransactionContext transactionContext = xaResource.getTransactionContext();
        transactionContext.commitTransaction();
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testManualRollbackShouldThrowException() throws Exception {
        xaResource.start(xid, XAResource.TMNOFLAGS);
        TransactionContext transactionContext = xaResource.getTransactionContext();
        transactionContext.rollbackTransaction();
    }

    @Test
    public void testTransactionTimeout() throws XAException {
        boolean timeoutSet = xaResource.setTransactionTimeout(2);
        Assert.assertTrue(timeoutSet);
        xaResource.start(xid, XAResource.TMNOFLAGS);
        TransactionContext context = xaResource.getTransactionContext();
        TransactionalMap<Object, Object> map = context.getMap("map");
        map.put("key", "val");
        xaResource.end(xid, XAResource.TMSUCCESS);
        HazelcastTestSupport.sleepSeconds(3);
        try {
            xaResource.commit(xid, true);
            Assert.fail();
        } catch (XAException e) {
            Assert.assertEquals(XAException.XA_RBTIMEOUT, e.errorCode);
        }
    }

    @Test
    public void testRollbackWithoutPrepare() throws Exception {
        doSomeWorkWithXa(xaResource);
        performRollbackWithXa(xaResource);
    }

    @Test
    public void testRollbackWithoutPrepare_EmptyTransactionLog() throws Exception {
        xaResource.start(xid, XAResource.TMNOFLAGS);
        xaResource.end(xid, XAResource.TMSUCCESS);
        performRollbackWithXa(xaResource);
    }

    @Test
    public void testRollbackWithoutPrepare_SecondXAResource() throws Exception {
        doSomeWorkWithXa(xaResource);
        performRollbackWithXa(secondXaResource);
    }

    @Test
    public void testRollbackWithoutPrepare_SecondXAResource_EmptyTransactionLog() throws Exception {
        xaResource.start(xid, XAResource.TMNOFLAGS);
        xaResource.end(xid, XAResource.TMSUCCESS);
        performRollbackWithXa(secondXaResource);
    }

    @Test
    public void testEnd_FromDifferentThread() throws Exception {
        xaResource.start(xid, XAResource.TMNOFLAGS);
        TransactionContext context = xaResource.getTransactionContext();
        TransactionalMap<Object, Object> map = context.getMap("map");
        map.put("key", "value");
        final CountDownLatch latch = new CountDownLatch(1);
        new Thread() {
            @Override
            public void run() {
                try {
                    xaResource.end(xid, XAResource.TMFAIL);
                    latch.countDown();
                } catch (XAException e) {
                    e.printStackTrace();
                }
            }
        }.start();
        HazelcastTestSupport.assertOpenEventually(latch, 10);
    }
}

