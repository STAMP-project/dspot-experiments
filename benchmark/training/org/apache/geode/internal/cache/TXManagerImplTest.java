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
package org.apache.geode.internal.cache;


import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import org.apache.geode.distributed.internal.ClusterDistributionManager;
import org.apache.geode.distributed.internal.membership.InternalDistributedMember;
import org.apache.geode.internal.SystemTimer;
import org.apache.geode.internal.cache.partitioned.DestroyMessage;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;


public class TXManagerImplTest {
    private TXManagerImpl txMgr;

    private TXId txid;

    private DestroyMessage msg;

    private TXCommitMessage txCommitMsg;

    private TXId completedTxid;

    private TXId notCompletedTxid;

    private InternalDistributedMember member;

    private CountDownLatch latch;

    private TXStateProxy tx1;

    private TXStateProxy tx2;

    private ClusterDistributionManager dm;

    private TXRemoteRollbackMessage rollbackMsg;

    private TXRemoteCommitMessage commitMsg;

    private InternalCache cache;

    private TXManagerImpl spyTxMgr;

    private InternalCache spyCache;

    private SystemTimer timer;

    @Test
    public void getOrSetHostedTXStateAbleToSetTXStateAndGetLock() {
        TXStateProxy tx = txMgr.getOrSetHostedTXState(txid, msg);
        Assert.assertNotNull(tx);
        Assert.assertEquals(tx, txMgr.getHostedTXState(txid));
        Assert.assertTrue(txMgr.getLock(tx, txid));
    }

    @Test
    public void getLockAfterTXStateRemoved() throws InterruptedException {
        TXStateProxy tx = txMgr.getOrSetHostedTXState(txid, msg);
        Assert.assertEquals(tx, txMgr.getHostedTXState(txid));
        Assert.assertTrue(txMgr.getLock(tx, txid));
        Assert.assertNotNull(tx);
        Assert.assertTrue(txMgr.getLock(tx, txid));
        tx.getLock().unlock();
        TXStateProxy oldtx = txMgr.getOrSetHostedTXState(txid, msg);
        Assert.assertEquals(tx, oldtx);
        Thread t1 = new Thread(new Runnable() {
            @Override
            public void run() {
                txMgr.removeHostedTXState(txid);
            }
        });
        t1.start();
        t1.join();
        TXStateProxy curTx = txMgr.getHostedTXState(txid);
        Assert.assertNull(curTx);
        // after failover command removed the txid from hostedTXState,
        // getLock should put back the original TXStateProxy
        Assert.assertTrue(txMgr.getLock(tx, txid));
        Assert.assertEquals(tx, txMgr.getHostedTXState(txid));
        tx.getLock().unlock();
    }

    @Test
    public void getLockAfterTXStateReplaced() throws InterruptedException {
        TXStateProxy oldtx = txMgr.getOrSetHostedTXState(txid, msg);
        Assert.assertEquals(oldtx, txMgr.getHostedTXState(txid));
        Assert.assertTrue(txMgr.getLock(oldtx, txid));
        Assert.assertNotNull(oldtx);
        oldtx.getLock().unlock();
        TXStateProxy tx = txMgr.getOrSetHostedTXState(txid, msg);
        Assert.assertEquals(tx, oldtx);
        Thread t1 = new Thread(new Runnable() {
            @Override
            public void run() {
                txMgr.removeHostedTXState(txid);
                // replace with new TXState
                txMgr.getOrSetHostedTXState(txid, msg);
            }
        });
        t1.start();
        t1.join();
        TXStateProxy curTx = txMgr.getHostedTXState(txid);
        Assert.assertNotNull(curTx);
        // replaced
        Assert.assertNotEquals(tx, curTx);
        // after TXStateProxy replaced, getLock will not get
        Assert.assertFalse(txMgr.getLock(tx, txid));
    }

    @Test
    public void getLockAfterTXStateCommitted() throws InterruptedException {
        TXStateProxy oldtx = txMgr.getOrSetHostedTXState(txid, msg);
        Assert.assertEquals(oldtx, txMgr.getHostedTXState(txid));
        Assert.assertTrue(txMgr.getLock(oldtx, txid));
        Assert.assertNotNull(oldtx);
        oldtx.getLock().unlock();
        TXStateProxy tx = txMgr.getOrSetHostedTXState(txid, msg);
        Assert.assertEquals(tx, oldtx);
        Thread t1 = new Thread(new Runnable() {
            @Override
            public void run() {
                Mockito.when(msg.getTXOriginatorClient()).thenReturn(Mockito.mock(InternalDistributedMember.class));
                TXStateProxy tx;
                try {
                    tx = txMgr.masqueradeAs(commitMsg);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    throw new RuntimeException(e);
                }
                tx.setCommitOnBehalfOfRemoteStub(true);
                try {
                    tx.commit();
                } finally {
                    txMgr.unmasquerade(tx);
                }
                txMgr.removeHostedTXState(txid);
                txMgr.saveTXCommitMessageForClientFailover(txid, txCommitMsg);
            }
        });
        t1.start();
        t1.join();
        TXStateProxy curTx = txMgr.getHostedTXState(txid);
        Assert.assertNull(curTx);
        Assert.assertFalse(tx.isInProgress());
        // after TXStateProxy committed, getLock will get the lock for the oldtx
        // but caller should not perform ops on this TXStateProxy
        Assert.assertTrue(txMgr.getLock(tx, txid));
    }

    @Test
    public void masqueradeAsCanGetLock() throws InterruptedException {
        TXStateProxy tx;
        tx = txMgr.masqueradeAs(msg);
        Assert.assertNotNull(tx);
    }

    @Test
    public void masqueradeAsCanGetLockAfterTXStateIsReplaced() throws InterruptedException {
        TXStateProxy tx;
        Thread t1 = new Thread(new Runnable() {
            @Override
            public void run() {
                tx1 = txMgr.getHostedTXState(txid);
                Assert.assertNull(tx1);
                tx1 = txMgr.getOrSetHostedTXState(txid, msg);
                Assert.assertNotNull(tx1);
                Assert.assertTrue(txMgr.getLock(tx1, txid));
                latch.countDown();
                await().until(() -> tx1.getLock().hasQueuedThreads());
                txMgr.removeHostedTXState(txid);
                tx2 = txMgr.getOrSetHostedTXState(txid, msg);
                Assert.assertNotNull(tx2);
                Assert.assertTrue(txMgr.getLock(tx2, txid));
                tx2.getLock().unlock();
                tx1.getLock().unlock();
            }
        });
        t1.start();
        Assert.assertTrue(latch.await(60, TimeUnit.SECONDS));
        tx = txMgr.masqueradeAs(msg);
        Assert.assertNotNull(tx);
        Assert.assertEquals(tx, tx2);
        tx.getLock().unlock();
        t1.join();
    }

    @Test
    public void testTxStateWithNotFinishedTx() {
        TXStateProxy tx = txMgr.getOrSetHostedTXState(notCompletedTxid, msg);
        Assert.assertTrue(tx.isInProgress());
    }

    @Test
    public void testTxStateWithCommittedTx() throws InterruptedException {
        Mockito.when(msg.getTXOriginatorClient()).thenReturn(Mockito.mock(InternalDistributedMember.class));
        setupTx();
        TXStateProxy tx = txMgr.masqueradeAs(commitMsg);
        try {
            tx.commit();
        } finally {
            txMgr.unmasquerade(tx);
        }
        Assert.assertFalse(tx.isInProgress());
    }

    @Test
    public void testTxStateWithRolledBackTx() throws InterruptedException {
        Mockito.when(msg.getTXOriginatorClient()).thenReturn(Mockito.mock(InternalDistributedMember.class));
        setupTx();
        TXStateProxy tx = txMgr.masqueradeAs(rollbackMsg);
        try {
            tx.rollback();
        } finally {
            txMgr.unmasquerade(tx);
        }
        Assert.assertFalse(tx.isInProgress());
    }

    @Test
    public void txRolledbackShouldCompleteTx() throws InterruptedException {
        Mockito.when(msg.getTXOriginatorClient()).thenReturn(Mockito.mock(InternalDistributedMember.class));
        Thread t1 = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    tx1 = txMgr.masqueradeAs(msg);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    throw new RuntimeException(e);
                }
                msg.process(dm);
                TXStateProxy existingTx = masqueradeToRollback();
                latch.countDown();
                await().until(() -> tx1.getLock().hasQueuedThreads());
                rollbackTransaction(existingTx);
            }
        });
        t1.start();
        Assert.assertTrue(latch.await(60, TimeUnit.SECONDS));
        TXStateProxy tx = txMgr.masqueradeAs(rollbackMsg);
        Assert.assertEquals(tx, tx1);
        t1.join();
        rollbackTransaction(tx);
    }

    @Test
    public void txStateNotCleanedupIfNotRemovedFromHostedTxStatesMap() {
        tx1 = txMgr.getOrSetHostedTXState(txid, msg);
        TXStateProxyImpl txStateProxy = ((TXStateProxyImpl) (tx1));
        Assert.assertNotNull(txStateProxy);
        Assert.assertFalse(txStateProxy.getLocalRealDeal().isClosed());
        txMgr.masqueradeAs(tx1);
        txMgr.unmasquerade(tx1);
        Assert.assertFalse(txStateProxy.getLocalRealDeal().isClosed());
    }

    @Test
    public void txStateCleanedUpIfRemovedFromHostedTxStatesMap() {
        tx1 = txMgr.getOrSetHostedTXState(txid, msg);
        TXStateProxyImpl txStateProxy = ((TXStateProxyImpl) (tx1));
        Assert.assertNotNull(txStateProxy);
        Assert.assertFalse(txStateProxy.getLocalRealDeal().isClosed());
        txMgr.masqueradeAs(tx1);
        // during TX failover, tx can be removed from the hostedTXStates map by FindRemoteTXMessage
        txMgr.getHostedTXStates().remove(txid);
        txMgr.unmasquerade(tx1);
        Assert.assertTrue(txStateProxy.getLocalRealDeal().isClosed());
    }

    @Test
    public void clientTransactionWithIdleTimeLongerThanTransactionTimeoutIsRemoved() throws Exception {
        Mockito.when(msg.getTXOriginatorClient()).thenReturn(Mockito.mock(InternalDistributedMember.class));
        TXStateProxyImpl tx = Mockito.spy(((TXStateProxyImpl) (txMgr.getOrSetHostedTXState(txid, msg))));
        Mockito.doReturn(true).when(tx).isOverTransactionTimeoutLimit();
        txMgr.scheduleToRemoveExpiredClientTransaction(txid);
        Assert.assertTrue(txMgr.isHostedTXStatesEmpty());
    }

    @Test
    public void clientTransactionsToBeRemovedAndDistributedAreSentToRemoveServerIfWithNoTimeout() {
        Set<TXId> txIds = ((Set<TXId>) (Mockito.mock(Set.class)));
        Mockito.doReturn(0).when(spyTxMgr).getTransactionTimeToLive();
        Mockito.when(txIds.iterator()).thenAnswer(new Answer<Iterator<TXId>>() {
            @Override
            public Iterator<TXId> answer(InvocationOnMock invocation) throws Throwable {
                return Arrays.asList(txid, Mockito.mock(TXId.class)).iterator();
            }
        });
        spyTxMgr.expireDisconnectedClientTransactions(txIds, true);
        Mockito.verify(spyTxMgr, Mockito.times(1)).expireClientTransactionsOnRemoteServer(ArgumentMatchers.eq(txIds));
    }

    @Test
    public void clientTransactionsToBeExpiredAreRemovedAndNotDistributedIfWithNoTimeout() {
        Mockito.doReturn(1).when(spyTxMgr).getTransactionTimeToLive();
        TXId txId1 = Mockito.mock(TXId.class);
        TXId txId2 = Mockito.mock(TXId.class);
        TXId txId3 = Mockito.mock(TXId.class);
        tx1 = spyTxMgr.getOrSetHostedTXState(txId1, msg);
        tx2 = spyTxMgr.getOrSetHostedTXState(txId2, msg);
        Set<TXId> txIds = Mockito.spy(new HashSet<>());
        txIds.add(txId1);
        Mockito.doReturn(0).when(spyTxMgr).getTransactionTimeToLive();
        Mockito.when(txIds.iterator()).thenAnswer(new Answer<Iterator<TXId>>() {
            @Override
            public Iterator<TXId> answer(InvocationOnMock invocation) throws Throwable {
                return Arrays.asList(txId1, txId3).iterator();
            }
        });
        Assert.assertEquals(2, spyTxMgr.getHostedTXStates().size());
        spyTxMgr.expireDisconnectedClientTransactions(txIds, false);
        Mockito.verify(spyTxMgr, Mockito.never()).expireClientTransactionsOnRemoteServer(ArgumentMatchers.eq(txIds));
        Mockito.verify(spyTxMgr, Mockito.times(1)).removeHostedTXState(ArgumentMatchers.eq(txIds));
        Mockito.verify(spyTxMgr, Mockito.times(1)).removeHostedTXState(ArgumentMatchers.eq(txId1));
        Mockito.verify(spyTxMgr, Mockito.times(1)).removeHostedTXState(ArgumentMatchers.eq(txId3));
        Assert.assertEquals(tx2, spyTxMgr.getHostedTXStates().get(txId2));
        Assert.assertEquals(1, spyTxMgr.getHostedTXStates().size());
    }

    @Test
    public void clientTransactionsToBeExpiredAndDistributedAreSentToRemoveServer() {
        Set<TXId> txIds = Mockito.mock(Set.class);
        spyTxMgr.expireDisconnectedClientTransactions(txIds, true);
        Mockito.verify(spyTxMgr, Mockito.times(1)).expireClientTransactionsOnRemoteServer(ArgumentMatchers.eq(txIds));
    }

    @Test
    public void clientTransactionsNotToBeDistributedAreNotSentToRemoveServer() {
        Set<TXId> txIds = Mockito.mock(Set.class);
        spyTxMgr.expireDisconnectedClientTransactions(txIds, false);
        Mockito.verify(spyTxMgr, Mockito.never()).expireClientTransactionsOnRemoteServer(ArgumentMatchers.eq(txIds));
    }

    @Test
    public void clientTransactionsToBeExpiredIsScheduledToBeRemoved() {
        Mockito.doReturn(1).when(spyTxMgr).getTransactionTimeToLive();
        TXId txId1 = Mockito.mock(TXId.class);
        TXId txId2 = Mockito.mock(TXId.class);
        TXId txId3 = Mockito.mock(TXId.class);
        tx1 = spyTxMgr.getOrSetHostedTXState(txId1, msg);
        tx2 = spyTxMgr.getOrSetHostedTXState(txId2, msg);
        Set<TXId> set = new HashSet<>();
        set.add(txId1);
        set.add(txId2);
        spyTxMgr.expireDisconnectedClientTransactions(set, false);
        Mockito.verify(spyTxMgr, Mockito.times(1)).scheduleToRemoveClientTransaction(ArgumentMatchers.eq(txId1), ArgumentMatchers.eq(1100L));
        Mockito.verify(spyTxMgr, Mockito.times(1)).scheduleToRemoveClientTransaction(ArgumentMatchers.eq(txId2), ArgumentMatchers.eq(1100L));
        Mockito.verify(spyTxMgr, Mockito.never()).scheduleToRemoveClientTransaction(ArgumentMatchers.eq(txId3), ArgumentMatchers.eq(1100L));
    }

    @Test
    public void clientTransactionIsRemovedIfWithNoTimeout() {
        spyTxMgr.scheduleToRemoveClientTransaction(txid, 0);
        Mockito.verify(spyTxMgr, Mockito.times(1)).removeHostedTXState(ArgumentMatchers.eq(txid));
    }

    @Test
    public void clientTransactionIsScheduledToBeRemovedIfWithTimeout() {
        spyTxMgr.scheduleToRemoveClientTransaction(txid, 1000);
        Mockito.verify(timer, Mockito.times(1)).schedule(ArgumentMatchers.any(), ArgumentMatchers.eq(1000L));
    }

    @Test
    public void unmasqueradeReleasesTheLockHeld() {
        tx1 = Mockito.mock(TXStateProxyImpl.class);
        ReentrantLock lock = Mockito.mock(ReentrantLock.class);
        Mockito.when(tx1.getLock()).thenReturn(lock);
        spyTxMgr.unmasquerade(tx1);
        Mockito.verify(lock, Mockito.times(1)).unlock();
    }

    @Test(expected = RuntimeException.class)
    public void unmasqueradeReleasesTheLockHeldWhenCleanupTransactionIfNoLongerHostFailedWithException() {
        tx1 = Mockito.mock(TXStateProxyImpl.class);
        ReentrantLock lock = Mockito.mock(ReentrantLock.class);
        Mockito.when(tx1.getLock()).thenReturn(lock);
        Mockito.doThrow(new RuntimeException()).when(spyTxMgr).cleanupTransactionIfNoLongerHost(tx1);
        spyTxMgr.unmasquerade(tx1);
        Mockito.verify(lock, Mockito.times(1)).unlock();
    }

    @Test
    public void masqueradeAsSetsTarget() throws InterruptedException {
        TXStateProxy tx;
        tx = txMgr.masqueradeAs(msg);
        Assert.assertNotNull(tx.getTarget());
    }
}

