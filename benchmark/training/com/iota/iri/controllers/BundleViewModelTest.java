package com.iota.iri.controllers;


import SpongeFactory.Mode.CURLP81;
import com.iota.iri.TransactionTestUtils;
import com.iota.iri.model.TransactionHash;
import com.iota.iri.service.snapshot.SnapshotProvider;
import com.iota.iri.storage.Tangle;
import org.junit.Assert;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;


public class BundleViewModelTest {
    private static final TemporaryFolder dbFolder = new TemporaryFolder();

    private static final TemporaryFolder logFolder = new TemporaryFolder();

    private static Tangle tangle = new Tangle();

    private static SnapshotProvider snapshotProvider;

    @Test
    public void firstShouldFindTx() throws Exception {
        byte[] trits = TransactionTestUtils.getRandomTransactionTrits();
        TransactionViewModel transactionViewModel = new TransactionViewModel(trits, TransactionHash.calculate(CURLP81, trits));
        transactionViewModel.store(BundleViewModelTest.tangle, BundleViewModelTest.snapshotProvider.getInitialSnapshot());
        BundleViewModel result = BundleViewModel.first(BundleViewModelTest.tangle);
        Assert.assertTrue(result.getHashes().contains(transactionViewModel.getHash()));
    }
}

