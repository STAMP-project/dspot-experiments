package com.iota.iri.service.tipselection.impl;


import com.iota.iri.TransactionTestUtils;
import com.iota.iri.controllers.TransactionViewModel;
import com.iota.iri.model.HashId;
import com.iota.iri.service.snapshot.SnapshotProvider;
import com.iota.iri.service.tipselection.RatingCalculator;
import com.iota.iri.storage.Tangle;
import com.iota.iri.utils.collections.interfaces.UnIterableMap;
import org.junit.Assert;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;


public class RatingOneTest {
    private static final TemporaryFolder dbFolder = new TemporaryFolder();

    private static final TemporaryFolder logFolder = new TemporaryFolder();

    private static final String TX_CUMULATIVE_WEIGHT_IS_NOT_AS_EXPECTED_FORMAT = "tx%d cumulative weight is not as expected";

    private static Tangle tangle;

    private static SnapshotProvider snapshotProvider;

    private static RatingCalculator rating;

    @Test
    public void testCalculate() throws Exception {
        TransactionViewModel transaction;
        TransactionViewModel transaction1;
        TransactionViewModel transaction2;
        TransactionViewModel transaction3;
        TransactionViewModel transaction4;
        transaction = new TransactionViewModel(TransactionTestUtils.getRandomTransactionTrits(), TransactionTestUtils.getRandomTransactionHash());
        transaction1 = new TransactionViewModel(TransactionTestUtils.getTransactionWithTrunkAndBranch(transaction.getHash(), transaction.getHash()), TransactionTestUtils.getRandomTransactionHash());
        transaction2 = new TransactionViewModel(TransactionTestUtils.getTransactionWithTrunkAndBranch(transaction1.getHash(), transaction1.getHash()), TransactionTestUtils.getRandomTransactionHash());
        transaction3 = new TransactionViewModel(TransactionTestUtils.getTransactionWithTrunkAndBranch(transaction2.getHash(), transaction1.getHash()), TransactionTestUtils.getRandomTransactionHash());
        transaction4 = new TransactionViewModel(TransactionTestUtils.getTransactionWithTrunkAndBranch(transaction2.getHash(), transaction3.getHash()), TransactionTestUtils.getRandomTransactionHash());
        transaction.store(RatingOneTest.tangle, RatingOneTest.snapshotProvider.getInitialSnapshot());
        transaction1.store(RatingOneTest.tangle, RatingOneTest.snapshotProvider.getInitialSnapshot());
        transaction2.store(RatingOneTest.tangle, RatingOneTest.snapshotProvider.getInitialSnapshot());
        transaction3.store(RatingOneTest.tangle, RatingOneTest.snapshotProvider.getInitialSnapshot());
        transaction4.store(RatingOneTest.tangle, RatingOneTest.snapshotProvider.getInitialSnapshot());
        UnIterableMap<HashId, Integer> rate = RatingOneTest.rating.calculate(transaction.getHash());
        Assert.assertEquals(RatingOneTest.TX_CUMULATIVE_WEIGHT_IS_NOT_AS_EXPECTED_FORMAT, 1, rate.get(transaction4.getHash()).intValue());
        Assert.assertEquals(RatingOneTest.TX_CUMULATIVE_WEIGHT_IS_NOT_AS_EXPECTED_FORMAT, 1, rate.get(transaction3.getHash()).intValue());
        Assert.assertEquals(RatingOneTest.TX_CUMULATIVE_WEIGHT_IS_NOT_AS_EXPECTED_FORMAT, 1, rate.get(transaction2.getHash()).intValue());
        Assert.assertEquals(RatingOneTest.TX_CUMULATIVE_WEIGHT_IS_NOT_AS_EXPECTED_FORMAT, 1, rate.get(transaction1.getHash()).intValue());
        Assert.assertEquals(RatingOneTest.TX_CUMULATIVE_WEIGHT_IS_NOT_AS_EXPECTED_FORMAT, 1, rate.get(transaction.getHash()).intValue());
    }
}

