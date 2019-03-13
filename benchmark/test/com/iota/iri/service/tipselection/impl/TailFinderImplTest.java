package com.iota.iri.service.tipselection.impl;


import com.iota.iri.TransactionTestUtils;
import com.iota.iri.controllers.TransactionViewModel;
import com.iota.iri.model.Hash;
import com.iota.iri.service.snapshot.SnapshotProvider;
import com.iota.iri.storage.Tangle;
import java.util.Optional;
import org.junit.Assert;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;


public class TailFinderImplTest {
    private static final TemporaryFolder dbFolder = new TemporaryFolder();

    private static final TemporaryFolder logFolder = new TemporaryFolder();

    private static Tangle tangle;

    private static SnapshotProvider snapshotProvider;

    private TailFinderImpl tailFinder;

    public TailFinderImplTest() {
        tailFinder = new TailFinderImpl(TailFinderImplTest.tangle);
    }

    @Test
    public void findTailTest() throws Exception {
        TransactionViewModel txa = new TransactionViewModel(TransactionTestUtils.getRandomTransactionTrits(), TransactionTestUtils.getRandomTransactionHash());
        txa.store(TailFinderImplTest.tangle, TailFinderImplTest.snapshotProvider.getInitialSnapshot());
        TransactionViewModel tx2 = TransactionTestUtils.createBundleHead(2);
        tx2.store(TailFinderImplTest.tangle, TailFinderImplTest.snapshotProvider.getInitialSnapshot());
        TransactionViewModel tx1 = TransactionTestUtils.createTransactionWithTrunkBundleHash(tx2, txa.getHash());
        tx1.store(TailFinderImplTest.tangle, TailFinderImplTest.snapshotProvider.getInitialSnapshot());
        TransactionViewModel tx0 = TransactionTestUtils.createTransactionWithTrunkBundleHash(tx1, txa.getHash());
        tx0.store(TailFinderImplTest.tangle, TailFinderImplTest.snapshotProvider.getInitialSnapshot());
        // negative index - make sure we stop at 0
        TransactionViewModel txNeg = TransactionTestUtils.createTransactionWithTrunkBundleHash(tx0, txa.getHash());
        txNeg.store(TailFinderImplTest.tangle, TailFinderImplTest.snapshotProvider.getInitialSnapshot());
        TransactionViewModel txLateTail = TransactionTestUtils.createTransactionWithTrunkBundleHash(tx1, txa.getHash());
        txLateTail.store(TailFinderImplTest.tangle, TailFinderImplTest.snapshotProvider.getInitialSnapshot());
        Optional<Hash> tail = tailFinder.findTail(tx2.getHash());
        Assert.assertTrue("no tail was found", tail.isPresent());
        Assert.assertEquals("Expected tail not found", tx0.getHash(), tail.get());
    }

    @Test
    public void findMissingTailTest() throws Exception {
        TransactionViewModel txa = new TransactionViewModel(TransactionTestUtils.getRandomTransactionTrits(), TransactionTestUtils.getRandomTransactionHash());
        txa.store(TailFinderImplTest.tangle, TailFinderImplTest.snapshotProvider.getInitialSnapshot());
        TransactionViewModel tx2 = TransactionTestUtils.createBundleHead(2);
        tx2.store(TailFinderImplTest.tangle, TailFinderImplTest.snapshotProvider.getInitialSnapshot());
        TransactionViewModel tx1 = TransactionTestUtils.createTransactionWithTrunkBundleHash(tx2, txa.getHash());
        tx1.store(TailFinderImplTest.tangle, TailFinderImplTest.snapshotProvider.getInitialSnapshot());
        TransactionViewModel tx0 = new TransactionViewModel(TransactionTestUtils.getTransactionWithTrunkAndBranch(tx1.getHash(), tx2.getHash()), TransactionTestUtils.getRandomTransactionHash());
        tx0.store(TailFinderImplTest.tangle, TailFinderImplTest.snapshotProvider.getInitialSnapshot());
        Optional<Hash> tail = tailFinder.findTail(tx2.getHash());
        Assert.assertFalse("tail was found, but should me missing", tail.isPresent());
    }
}

