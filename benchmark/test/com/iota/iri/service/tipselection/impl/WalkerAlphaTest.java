package com.iota.iri.service.tipselection.impl;


import com.iota.iri.TransactionTestUtils;
import com.iota.iri.controllers.TransactionViewModel;
import com.iota.iri.model.Hash;
import com.iota.iri.model.HashId;
import com.iota.iri.service.snapshot.SnapshotProvider;
import com.iota.iri.service.tipselection.RatingCalculator;
import com.iota.iri.storage.Tangle;
import com.iota.iri.utils.collections.interfaces.UnIterableMap;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class WalkerAlphaTest {
    private static final TemporaryFolder dbFolder = new TemporaryFolder();

    private static final TemporaryFolder logFolder = new TemporaryFolder();

    private static Tangle tangle;

    private static SnapshotProvider snapshotProvider;

    private static WalkerAlpha walker;

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Test
    public void testWalkEndsOnlyInRating() throws Exception {
        // build a small tangle - 1,2,3,4 point to  transaction
        TransactionViewModel transaction;
        TransactionViewModel transaction1;
        TransactionViewModel transaction2;
        TransactionViewModel transaction3;
        TransactionViewModel transaction4;
        transaction = new TransactionViewModel(TransactionTestUtils.getRandomTransactionTrits(), TransactionTestUtils.getRandomTransactionHash());
        transaction1 = new TransactionViewModel(TransactionTestUtils.getTransactionWithTrunkAndBranch(transaction.getHash(), transaction.getHash()), TransactionTestUtils.getRandomTransactionHash());
        transaction2 = new TransactionViewModel(TransactionTestUtils.getTransactionWithTrunkAndBranch(transaction.getHash(), transaction.getHash()), TransactionTestUtils.getRandomTransactionHash());
        transaction3 = new TransactionViewModel(TransactionTestUtils.getTransactionWithTrunkAndBranch(transaction.getHash(), transaction.getHash()), TransactionTestUtils.getRandomTransactionHash());
        transaction.store(WalkerAlphaTest.tangle, WalkerAlphaTest.snapshotProvider.getInitialSnapshot());
        transaction1.store(WalkerAlphaTest.tangle, WalkerAlphaTest.snapshotProvider.getInitialSnapshot());
        transaction2.store(WalkerAlphaTest.tangle, WalkerAlphaTest.snapshotProvider.getInitialSnapshot());
        transaction3.store(WalkerAlphaTest.tangle, WalkerAlphaTest.snapshotProvider.getInitialSnapshot());
        // calculate rating
        RatingCalculator ratingCalculator = new RatingOne(WalkerAlphaTest.tangle);
        UnIterableMap<HashId, Integer> rating = ratingCalculator.calculate(transaction.getHash());
        // add 4 after the rating was calculated
        transaction4 = new TransactionViewModel(TransactionTestUtils.getTransactionWithTrunkAndBranch(transaction.getHash(), transaction.getHash()), TransactionTestUtils.getRandomTransactionHash());
        transaction4.store(WalkerAlphaTest.tangle, WalkerAlphaTest.snapshotProvider.getInitialSnapshot());
        for (int i = 0; i < 100; i++) {
            // select
            Hash tip = WalkerAlphaTest.walker.walk(transaction.getHash(), rating, ( o) -> true);
            Assert.assertTrue((tip != null));
            // log.info("selected tip: " + tip.toString());
            Assert.assertTrue((!(transaction4.getHash().equals(tip))));
        }
    }

    @Test
    public void showWalkDistributionAlphaHalf() throws Exception {
        // build a small tangle - 1,2,3,4 point to  transaction
        TransactionViewModel transaction;
        TransactionViewModel transaction1;
        TransactionViewModel transaction2;
        TransactionViewModel transaction3;
        transaction = new TransactionViewModel(TransactionTestUtils.getRandomTransactionTrits(), TransactionTestUtils.getRandomTransactionHash());
        transaction1 = new TransactionViewModel(TransactionTestUtils.getTransactionWithTrunkAndBranch(transaction.getHash(), transaction.getHash()), TransactionTestUtils.getRandomTransactionHash());
        transaction2 = new TransactionViewModel(TransactionTestUtils.getTransactionWithTrunkAndBranch(transaction.getHash(), transaction.getHash()), TransactionTestUtils.getRandomTransactionHash());
        transaction3 = new TransactionViewModel(TransactionTestUtils.getTransactionWithTrunkAndBranch(transaction.getHash(), transaction.getHash()), TransactionTestUtils.getRandomTransactionHash());
        transaction.store(WalkerAlphaTest.tangle, WalkerAlphaTest.snapshotProvider.getInitialSnapshot());
        transaction1.store(WalkerAlphaTest.tangle, WalkerAlphaTest.snapshotProvider.getInitialSnapshot());
        transaction2.store(WalkerAlphaTest.tangle, WalkerAlphaTest.snapshotProvider.getInitialSnapshot());
        transaction3.store(WalkerAlphaTest.tangle, WalkerAlphaTest.snapshotProvider.getInitialSnapshot());
        // calculate rating
        RatingCalculator ratingCalculator = new RatingOne(WalkerAlphaTest.tangle);
        UnIterableMap<HashId, Integer> rating = ratingCalculator.calculate(transaction.getHash());
        // set a higher rate for transaction2
        rating.put(transaction2.getHash(), 10);
        Map<Hash, Integer> counters = new java.util.HashMap(rating.size());
        int iterations = 100;
        WalkerAlphaTest.walker.setAlpha(0.3);
        for (int i = 0; i < iterations; i++) {
            // select
            Hash tip = WalkerAlphaTest.walker.walk(transaction.getHash(), rating, ( o) -> true);
            Assert.assertNotNull(tip);
            counters.put(tip, (1 + (counters.getOrDefault(tip, 0))));
        }
        for (Map.Entry<Hash, Integer> entry : counters.entrySet()) {
            log.info((((entry.getKey().toString()) + " : ") + (entry.getValue())));
        }
        Assert.assertTrue(((counters.get(transaction2.getHash())) > (iterations / 2)));
    }

    @Test
    public void showWalkDistributionAlphaZero() throws Exception {
        // build a small tangle - 1,2,3,4 point to  transaction
        TransactionViewModel transaction;
        TransactionViewModel transaction1;
        TransactionViewModel transaction2;
        TransactionViewModel transaction3;
        TransactionViewModel transaction4;
        transaction = new TransactionViewModel(TransactionTestUtils.getRandomTransactionTrits(), TransactionTestUtils.getRandomTransactionHash());
        transaction1 = new TransactionViewModel(TransactionTestUtils.getTransactionWithTrunkAndBranch(transaction.getHash(), transaction.getHash()), TransactionTestUtils.getRandomTransactionHash());
        transaction2 = new TransactionViewModel(TransactionTestUtils.getTransactionWithTrunkAndBranch(transaction.getHash(), transaction.getHash()), TransactionTestUtils.getRandomTransactionHash());
        transaction3 = new TransactionViewModel(TransactionTestUtils.getTransactionWithTrunkAndBranch(transaction.getHash(), transaction.getHash()), TransactionTestUtils.getRandomTransactionHash());
        transaction.store(WalkerAlphaTest.tangle, WalkerAlphaTest.snapshotProvider.getInitialSnapshot());
        transaction1.store(WalkerAlphaTest.tangle, WalkerAlphaTest.snapshotProvider.getInitialSnapshot());
        transaction2.store(WalkerAlphaTest.tangle, WalkerAlphaTest.snapshotProvider.getInitialSnapshot());
        transaction3.store(WalkerAlphaTest.tangle, WalkerAlphaTest.snapshotProvider.getInitialSnapshot());
        // calculate rating
        RatingCalculator ratingCalculator = new RatingOne(WalkerAlphaTest.tangle);
        UnIterableMap<HashId, Integer> rating = ratingCalculator.calculate(transaction.getHash());
        // set a higher rate for transaction2
        rating.put(transaction2.getHash(), 10);
        // add 4 after the rating was calculated
        transaction4 = new TransactionViewModel(TransactionTestUtils.getTransactionWithTrunkAndBranch(transaction.getHash(), transaction.getHash()), TransactionTestUtils.getRandomTransactionHash());
        transaction4.store(WalkerAlphaTest.tangle, WalkerAlphaTest.snapshotProvider.getInitialSnapshot());
        Map<Hash, Integer> counters = new java.util.HashMap(rating.size());
        int iterations = 100;
        WalkerAlphaTest.walker.setAlpha(0);
        for (int i = 0; i < iterations; i++) {
            // select
            Hash tip = WalkerAlphaTest.walker.walk(transaction.getHash(), rating, ( o) -> true);
            Assert.assertNotNull(tip);
            counters.put(tip, (1 + (counters.getOrDefault(tip, 0))));
        }
        for (Map.Entry<Hash, Integer> entry : counters.entrySet()) {
            log.info((((entry.getKey().toString()) + " : ") + (entry.getValue())));
        }
        Assert.assertTrue(((counters.get(transaction1.getHash())) > (iterations / 6)));
    }

    @Test
    public void testWalk() throws Exception {
        // build a small tangle
        TransactionViewModel transaction;
        TransactionViewModel transaction1;
        TransactionViewModel transaction2;
        TransactionViewModel transaction3;
        TransactionViewModel transaction4;
        transaction = new TransactionViewModel(TransactionTestUtils.getRandomTransactionTrits(), Hash.NULL_HASH);
        transaction1 = new TransactionViewModel(TransactionTestUtils.getTransactionWithTrunkAndBranch(transaction.getHash(), transaction.getHash()), TransactionTestUtils.getRandomTransactionHash());
        transaction2 = new TransactionViewModel(TransactionTestUtils.getTransactionWithTrunkAndBranch(transaction1.getHash(), transaction1.getHash()), TransactionTestUtils.getRandomTransactionHash());
        transaction3 = new TransactionViewModel(TransactionTestUtils.getTransactionWithTrunkAndBranch(transaction2.getHash(), transaction1.getHash()), TransactionTestUtils.getRandomTransactionHash());
        transaction4 = new TransactionViewModel(TransactionTestUtils.getTransactionWithTrunkAndBranch(transaction2.getHash(), transaction3.getHash()), TransactionTestUtils.getRandomTransactionHash());
        transaction.store(WalkerAlphaTest.tangle, WalkerAlphaTest.snapshotProvider.getInitialSnapshot());
        transaction1.store(WalkerAlphaTest.tangle, WalkerAlphaTest.snapshotProvider.getInitialSnapshot());
        transaction2.store(WalkerAlphaTest.tangle, WalkerAlphaTest.snapshotProvider.getInitialSnapshot());
        transaction3.store(WalkerAlphaTest.tangle, WalkerAlphaTest.snapshotProvider.getInitialSnapshot());
        transaction4.store(WalkerAlphaTest.tangle, WalkerAlphaTest.snapshotProvider.getInitialSnapshot());
        // calculate rating
        RatingCalculator ratingCalculator = new RatingOne(WalkerAlphaTest.tangle);
        UnIterableMap<HashId, Integer> rating = ratingCalculator.calculate(transaction.getHash());
        // reach the tips
        Hash tip = WalkerAlphaTest.walker.walk(transaction.getHash(), rating, ( o) -> true);
        log.info(("selected tip: " + (tip.toString())));
        Assert.assertEquals(tip, transaction4.getHash());
    }

    @Test
    public void testWalkDiamond() throws Exception {
        // build a small tangle
        TransactionViewModel transaction;
        TransactionViewModel transaction1;
        TransactionViewModel transaction2;
        TransactionViewModel transaction3;
        transaction = new TransactionViewModel(TransactionTestUtils.getRandomTransactionTrits(), TransactionTestUtils.getRandomTransactionHash());
        transaction1 = new TransactionViewModel(TransactionTestUtils.getTransactionWithTrunkAndBranch(transaction.getHash(), transaction.getHash()), TransactionTestUtils.getRandomTransactionHash());
        transaction2 = new TransactionViewModel(TransactionTestUtils.getTransactionWithTrunkAndBranch(transaction.getHash(), transaction.getHash()), TransactionTestUtils.getRandomTransactionHash());
        transaction3 = new TransactionViewModel(TransactionTestUtils.getTransactionWithTrunkAndBranch(transaction1.getHash(), transaction2.getHash()), TransactionTestUtils.getRandomTransactionHash());
        transaction.store(WalkerAlphaTest.tangle, WalkerAlphaTest.snapshotProvider.getInitialSnapshot());
        transaction1.store(WalkerAlphaTest.tangle, WalkerAlphaTest.snapshotProvider.getInitialSnapshot());
        transaction2.store(WalkerAlphaTest.tangle, WalkerAlphaTest.snapshotProvider.getInitialSnapshot());
        transaction3.store(WalkerAlphaTest.tangle, WalkerAlphaTest.snapshotProvider.getInitialSnapshot());
        // calculate rating
        RatingCalculator ratingCalculator = new RatingOne(WalkerAlphaTest.tangle);
        UnIterableMap<HashId, Integer> rating = ratingCalculator.calculate(transaction.getHash());
        // reach the tips
        Hash tip = WalkerAlphaTest.walker.walk(transaction.getHash(), rating, ( o) -> true);
        log.info(("selected tip: " + (tip.toString())));
        Assert.assertEquals(tip, transaction3.getHash());
    }

    @Test
    public void testWalkChain() throws Exception {
        // build a small tangle
        TransactionViewModel transaction;
        TransactionViewModel transaction1;
        TransactionViewModel transaction2;
        TransactionViewModel transaction3;
        TransactionViewModel transaction4;
        transaction = new TransactionViewModel(TransactionTestUtils.getRandomTransactionTrits(), TransactionTestUtils.getRandomTransactionHash());
        transaction1 = new TransactionViewModel(TransactionTestUtils.getTransactionWithTrunkAndBranch(transaction.getHash(), transaction.getHash()), TransactionTestUtils.getRandomTransactionHash());
        transaction2 = new TransactionViewModel(TransactionTestUtils.getTransactionWithTrunkAndBranch(transaction1.getHash(), transaction1.getHash()), TransactionTestUtils.getRandomTransactionHash());
        transaction3 = new TransactionViewModel(TransactionTestUtils.getTransactionWithTrunkAndBranch(transaction2.getHash(), transaction2.getHash()), TransactionTestUtils.getRandomTransactionHash());
        transaction4 = new TransactionViewModel(TransactionTestUtils.getTransactionWithTrunkAndBranch(transaction3.getHash(), transaction3.getHash()), TransactionTestUtils.getRandomTransactionHash());
        transaction.store(WalkerAlphaTest.tangle, WalkerAlphaTest.snapshotProvider.getInitialSnapshot());
        transaction1.store(WalkerAlphaTest.tangle, WalkerAlphaTest.snapshotProvider.getInitialSnapshot());
        transaction2.store(WalkerAlphaTest.tangle, WalkerAlphaTest.snapshotProvider.getInitialSnapshot());
        transaction3.store(WalkerAlphaTest.tangle, WalkerAlphaTest.snapshotProvider.getInitialSnapshot());
        transaction4.store(WalkerAlphaTest.tangle, WalkerAlphaTest.snapshotProvider.getInitialSnapshot());
        // calculate rating
        RatingCalculator ratingCalculator = new RatingOne(WalkerAlphaTest.tangle);
        UnIterableMap<HashId, Integer> rating = ratingCalculator.calculate(transaction.getHash());
        // reach the tips
        Hash tip = WalkerAlphaTest.walker.walk(transaction.getHash(), rating, ( o) -> true);
        log.info(("selected tip: " + (tip.toString())));
        Assert.assertEquals(tip, transaction4.getHash());
    }
}

