package com.iota.iri.network;


import com.iota.iri.TransactionTestUtils;
import com.iota.iri.model.Hash;
import com.iota.iri.service.snapshot.SnapshotProvider;
import com.iota.iri.storage.Tangle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;

import static TransactionRequester.MAX_TX_REQ_QUEUE_SIZE;


public class TransactionRequesterTest {
    private static Tangle tangle = new Tangle();

    private static SnapshotProvider snapshotProvider;

    @Test
    public void popEldestTransactionToRequest() throws Exception {
        TransactionRequester txReq = new TransactionRequester(TransactionRequesterTest.tangle, TransactionRequesterTest.snapshotProvider);
        // Add some Txs to the pool and see if the method pops the eldest one
        Hash eldest = TransactionTestUtils.getRandomTransactionHash();
        txReq.requestTransaction(eldest, false);
        txReq.requestTransaction(TransactionTestUtils.getRandomTransactionHash(), false);
        txReq.requestTransaction(TransactionTestUtils.getRandomTransactionHash(), false);
        txReq.requestTransaction(TransactionTestUtils.getRandomTransactionHash(), false);
        txReq.popEldestTransactionToRequest();
        // Check that the transaction is there no more
        Assert.assertFalse(txReq.isTransactionRequested(eldest, false));
    }

    @Test
    public void transactionRequestedFreshness() throws Exception {
        // Add some Txs to the pool and see if the method pops the eldest one
        List<Hash> eldest = new ArrayList<Hash>(Arrays.asList(TransactionTestUtils.getRandomTransactionHash(), TransactionTestUtils.getRandomTransactionHash(), TransactionTestUtils.getRandomTransactionHash()));
        TransactionRequester txReq = new TransactionRequester(TransactionRequesterTest.tangle, TransactionRequesterTest.snapshotProvider);
        int capacity = MAX_TX_REQ_QUEUE_SIZE;
        // fill tips list
        for (int i = 0; i < 3; i++) {
            txReq.requestTransaction(eldest.get(i), false);
        }
        for (int i = 0; i < capacity; i++) {
            Hash hash = TransactionTestUtils.getRandomTransactionHash();
            txReq.requestTransaction(hash, false);
        }
        // check that limit wasn't breached
        Assert.assertEquals("Queue capacity breached!!", capacity, txReq.numberOfTransactionsToRequest());
        // None of the eldest transactions should be in the pool
        for (int i = 0; i < 3; i++) {
            Assert.assertFalse("Old transaction has been requested", txReq.isTransactionRequested(eldest.get(i), false));
        }
    }

    @Test
    public void nonMilestoneCapacityLimited() throws Exception {
        TransactionRequester txReq = new TransactionRequester(TransactionRequesterTest.tangle, TransactionRequesterTest.snapshotProvider);
        int capacity = MAX_TX_REQ_QUEUE_SIZE;
        // fill tips list
        for (int i = 0; i < (capacity * 2); i++) {
            Hash hash = TransactionTestUtils.getRandomTransactionHash();
            txReq.requestTransaction(hash, false);
        }
        // check that limit wasn't breached
        Assert.assertEquals(capacity, txReq.numberOfTransactionsToRequest());
    }

    @Test
    public void milestoneCapacityNotLimited() throws Exception {
        TransactionRequester txReq = new TransactionRequester(TransactionRequesterTest.tangle, TransactionRequesterTest.snapshotProvider);
        int capacity = MAX_TX_REQ_QUEUE_SIZE;
        // fill tips list
        for (int i = 0; i < (capacity * 2); i++) {
            Hash hash = TransactionTestUtils.getRandomTransactionHash();
            txReq.requestTransaction(hash, true);
        }
        // check that limit was surpassed
        Assert.assertEquals((capacity * 2), txReq.numberOfTransactionsToRequest());
    }

    @Test
    public void mixedCapacityLimited() throws Exception {
        TransactionRequester txReq = new TransactionRequester(TransactionRequesterTest.tangle, TransactionRequesterTest.snapshotProvider);
        int capacity = MAX_TX_REQ_QUEUE_SIZE;
        // fill tips list
        for (int i = 0; i < (capacity * 4); i++) {
            Hash hash = TransactionTestUtils.getRandomTransactionHash();
            txReq.requestTransaction(hash, ((i % 2) == 1));
        }
        // check that limit wasn't breached
        Assert.assertEquals((capacity + (capacity * 2)), txReq.numberOfTransactionsToRequest());
    }
}

