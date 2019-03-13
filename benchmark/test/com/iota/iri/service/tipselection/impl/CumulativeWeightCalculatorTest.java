package com.iota.iri.service.tipselection.impl;


import com.iota.iri.TransactionTestUtils;
import com.iota.iri.controllers.TransactionViewModel;
import com.iota.iri.model.Hash;
import com.iota.iri.model.HashId;
import com.iota.iri.service.snapshot.SnapshotProvider;
import com.iota.iri.storage.Tangle;
import com.iota.iri.utils.collections.interfaces.UnIterableMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class CumulativeWeightCalculatorTest {
    private static final TemporaryFolder dbFolder = new TemporaryFolder();

    private static final TemporaryFolder logFolder = new TemporaryFolder();

    private static final String TX_CUMULATIVE_WEIGHT_IS_NOT_AS_EXPECTED_FORMAT = "tx%d cumulative weight is not as expected";

    private static Tangle tangle;

    private static SnapshotProvider snapshotProvider;

    private static CumulativeWeightCalculator cumulativeWeightCalculator;

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Test
    public void testCalculateCumulativeWeight() throws Exception {
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
        transaction.store(CumulativeWeightCalculatorTest.tangle, CumulativeWeightCalculatorTest.snapshotProvider.getInitialSnapshot());
        transaction1.store(CumulativeWeightCalculatorTest.tangle, CumulativeWeightCalculatorTest.snapshotProvider.getInitialSnapshot());
        transaction2.store(CumulativeWeightCalculatorTest.tangle, CumulativeWeightCalculatorTest.snapshotProvider.getInitialSnapshot());
        transaction3.store(CumulativeWeightCalculatorTest.tangle, CumulativeWeightCalculatorTest.snapshotProvider.getInitialSnapshot());
        transaction4.store(CumulativeWeightCalculatorTest.tangle, CumulativeWeightCalculatorTest.snapshotProvider.getInitialSnapshot());
        UnIterableMap<HashId, Integer> txToCw = CumulativeWeightCalculatorTest.cumulativeWeightCalculator.calculate(transaction.getHash());
        Assert.assertEquals(String.format(CumulativeWeightCalculatorTest.TX_CUMULATIVE_WEIGHT_IS_NOT_AS_EXPECTED_FORMAT, 4), 1, txToCw.get(transaction4.getHash()).intValue());
        Assert.assertEquals(String.format(CumulativeWeightCalculatorTest.TX_CUMULATIVE_WEIGHT_IS_NOT_AS_EXPECTED_FORMAT, 3), 2, txToCw.get(transaction3.getHash()).intValue());
        Assert.assertEquals(String.format(CumulativeWeightCalculatorTest.TX_CUMULATIVE_WEIGHT_IS_NOT_AS_EXPECTED_FORMAT, 2), 3, txToCw.get(transaction2.getHash()).intValue());
        Assert.assertEquals(String.format(CumulativeWeightCalculatorTest.TX_CUMULATIVE_WEIGHT_IS_NOT_AS_EXPECTED_FORMAT, 1), 4, txToCw.get(transaction1.getHash()).intValue());
        Assert.assertEquals(String.format(CumulativeWeightCalculatorTest.TX_CUMULATIVE_WEIGHT_IS_NOT_AS_EXPECTED_FORMAT, 0), 5, txToCw.get(transaction.getHash()).intValue());
    }

    @Test
    public void testCalculateCumulativeWeightDiamond() throws Exception {
        TransactionViewModel transaction;
        TransactionViewModel transaction1;
        TransactionViewModel transaction2;
        TransactionViewModel transaction3;
        transaction = new TransactionViewModel(TransactionTestUtils.getRandomTransactionTrits(), TransactionTestUtils.getRandomTransactionHash());
        transaction1 = new TransactionViewModel(TransactionTestUtils.getTransactionWithTrunkAndBranch(transaction.getHash(), transaction.getHash()), TransactionTestUtils.getRandomTransactionHash());
        transaction2 = new TransactionViewModel(TransactionTestUtils.getTransactionWithTrunkAndBranch(transaction.getHash(), transaction.getHash()), TransactionTestUtils.getRandomTransactionHash());
        transaction3 = new TransactionViewModel(TransactionTestUtils.getTransactionWithTrunkAndBranch(transaction1.getHash(), transaction2.getHash()), TransactionTestUtils.getRandomTransactionHash());
        transaction.store(CumulativeWeightCalculatorTest.tangle, CumulativeWeightCalculatorTest.snapshotProvider.getInitialSnapshot());
        transaction1.store(CumulativeWeightCalculatorTest.tangle, CumulativeWeightCalculatorTest.snapshotProvider.getInitialSnapshot());
        transaction2.store(CumulativeWeightCalculatorTest.tangle, CumulativeWeightCalculatorTest.snapshotProvider.getInitialSnapshot());
        transaction3.store(CumulativeWeightCalculatorTest.tangle, CumulativeWeightCalculatorTest.snapshotProvider.getInitialSnapshot());
        log.debug("printing transaction in diamond shape \n                      {} \n{}  {}\n                      {}", transaction.getHash(), transaction1.getHash(), transaction2.getHash(), transaction3.getHash());
        UnIterableMap<HashId, Integer> txToCw = CumulativeWeightCalculatorTest.cumulativeWeightCalculator.calculate(transaction.getHash());
        Assert.assertEquals(String.format(CumulativeWeightCalculatorTest.TX_CUMULATIVE_WEIGHT_IS_NOT_AS_EXPECTED_FORMAT, 3), 1, txToCw.get(transaction3.getHash()).intValue());
        Assert.assertEquals(String.format(CumulativeWeightCalculatorTest.TX_CUMULATIVE_WEIGHT_IS_NOT_AS_EXPECTED_FORMAT, 1), 2, txToCw.get(transaction1.getHash()).intValue());
        Assert.assertEquals(String.format(CumulativeWeightCalculatorTest.TX_CUMULATIVE_WEIGHT_IS_NOT_AS_EXPECTED_FORMAT, 2), 2, txToCw.get(transaction2.getHash()).intValue());
        Assert.assertEquals(String.format(CumulativeWeightCalculatorTest.TX_CUMULATIVE_WEIGHT_IS_NOT_AS_EXPECTED_FORMAT, 0), 4, txToCw.get(transaction.getHash()).intValue());
    }

    @Test
    public void testCalculateCumulativeWeightLinear() throws Exception {
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
        transaction.store(CumulativeWeightCalculatorTest.tangle, CumulativeWeightCalculatorTest.snapshotProvider.getInitialSnapshot());
        transaction1.store(CumulativeWeightCalculatorTest.tangle, CumulativeWeightCalculatorTest.snapshotProvider.getInitialSnapshot());
        transaction2.store(CumulativeWeightCalculatorTest.tangle, CumulativeWeightCalculatorTest.snapshotProvider.getInitialSnapshot());
        transaction3.store(CumulativeWeightCalculatorTest.tangle, CumulativeWeightCalculatorTest.snapshotProvider.getInitialSnapshot());
        transaction4.store(CumulativeWeightCalculatorTest.tangle, CumulativeWeightCalculatorTest.snapshotProvider.getInitialSnapshot());
        log.info(String.format("Linear ordered hashes from tip %.4s, %.4s, %.4s, %.4s, %.4s", transaction4.getHash(), transaction3.getHash(), transaction2.getHash(), transaction1.getHash(), transaction.getHash()));
        UnIterableMap<HashId, Integer> txToCw = CumulativeWeightCalculatorTest.cumulativeWeightCalculator.calculate(transaction.getHash());
        Assert.assertEquals(String.format(CumulativeWeightCalculatorTest.TX_CUMULATIVE_WEIGHT_IS_NOT_AS_EXPECTED_FORMAT, 4), 1, txToCw.get(transaction4.getHash()).intValue());
        Assert.assertEquals(String.format(CumulativeWeightCalculatorTest.TX_CUMULATIVE_WEIGHT_IS_NOT_AS_EXPECTED_FORMAT, 3), 2, txToCw.get(transaction3.getHash()).intValue());
        Assert.assertEquals(String.format(CumulativeWeightCalculatorTest.TX_CUMULATIVE_WEIGHT_IS_NOT_AS_EXPECTED_FORMAT, 2), 3, txToCw.get(transaction2.getHash()).intValue());
        Assert.assertEquals(String.format(CumulativeWeightCalculatorTest.TX_CUMULATIVE_WEIGHT_IS_NOT_AS_EXPECTED_FORMAT, 1), 4, txToCw.get(transaction1.getHash()).intValue());
        Assert.assertEquals(String.format(CumulativeWeightCalculatorTest.TX_CUMULATIVE_WEIGHT_IS_NOT_AS_EXPECTED_FORMAT, 0), 5, txToCw.get(transaction.getHash()).intValue());
    }

    @Test
    public void testCalculateCumulativeWeight2() throws Exception {
        TransactionViewModel transaction;
        TransactionViewModel transaction1;
        TransactionViewModel transaction2;
        TransactionViewModel transaction3;
        TransactionViewModel transaction4;
        TransactionViewModel transaction5;
        TransactionViewModel transaction6;
        transaction = new TransactionViewModel(TransactionTestUtils.getRandomTransactionTrits(), TransactionTestUtils.getRandomTransactionHash());
        transaction1 = new TransactionViewModel(TransactionTestUtils.getTransactionWithTrunkAndBranch(transaction.getHash(), transaction.getHash()), TransactionTestUtils.getRandomTransactionHash());
        transaction2 = new TransactionViewModel(TransactionTestUtils.getTransactionWithTrunkAndBranch(transaction.getHash(), transaction.getHash()), TransactionTestUtils.getRandomTransactionHash());
        transaction3 = new TransactionViewModel(TransactionTestUtils.getTransactionWithTrunkAndBranch(transaction.getHash(), transaction.getHash()), TransactionTestUtils.getRandomTransactionHash());
        transaction4 = new TransactionViewModel(TransactionTestUtils.getTransactionWithTrunkAndBranch(transaction.getHash(), transaction.getHash()), TransactionTestUtils.getRandomTransactionHash());
        transaction5 = new TransactionViewModel(TransactionTestUtils.getTransactionWithTrunkAndBranch(transaction3.getHash(), transaction2.getHash()), TransactionTestUtils.getRandomTransactionHash());
        transaction6 = new TransactionViewModel(TransactionTestUtils.getTransactionWithTrunkAndBranch(transaction4.getHash(), transaction5.getHash()), TransactionTestUtils.getRandomTransactionHash());
        transaction.store(CumulativeWeightCalculatorTest.tangle, CumulativeWeightCalculatorTest.snapshotProvider.getInitialSnapshot());
        transaction1.store(CumulativeWeightCalculatorTest.tangle, CumulativeWeightCalculatorTest.snapshotProvider.getInitialSnapshot());
        transaction2.store(CumulativeWeightCalculatorTest.tangle, CumulativeWeightCalculatorTest.snapshotProvider.getInitialSnapshot());
        transaction3.store(CumulativeWeightCalculatorTest.tangle, CumulativeWeightCalculatorTest.snapshotProvider.getInitialSnapshot());
        transaction4.store(CumulativeWeightCalculatorTest.tangle, CumulativeWeightCalculatorTest.snapshotProvider.getInitialSnapshot());
        transaction5.store(CumulativeWeightCalculatorTest.tangle, CumulativeWeightCalculatorTest.snapshotProvider.getInitialSnapshot());
        transaction6.store(CumulativeWeightCalculatorTest.tangle, CumulativeWeightCalculatorTest.snapshotProvider.getInitialSnapshot());
        log.debug("printing transactions in order \n{}\n{}\n{}\n{}\n{}\n{}\n{}", transaction.getHash(), transaction1.getHash(), transaction2.getHash(), transaction3.getHash(), transaction4, transaction5, transaction6);
        UnIterableMap<HashId, Integer> txToCw = CumulativeWeightCalculatorTest.cumulativeWeightCalculator.calculate(transaction.getHash());
        Assert.assertEquals(String.format(CumulativeWeightCalculatorTest.TX_CUMULATIVE_WEIGHT_IS_NOT_AS_EXPECTED_FORMAT, 6), 1, txToCw.get(transaction6.getHash()).intValue());
        Assert.assertEquals(String.format(CumulativeWeightCalculatorTest.TX_CUMULATIVE_WEIGHT_IS_NOT_AS_EXPECTED_FORMAT, 5), 2, txToCw.get(transaction5.getHash()).intValue());
        Assert.assertEquals(String.format(CumulativeWeightCalculatorTest.TX_CUMULATIVE_WEIGHT_IS_NOT_AS_EXPECTED_FORMAT, 4), 2, txToCw.get(transaction4.getHash()).intValue());
        Assert.assertEquals(String.format(CumulativeWeightCalculatorTest.TX_CUMULATIVE_WEIGHT_IS_NOT_AS_EXPECTED_FORMAT, 3), 3, txToCw.get(transaction3.getHash()).intValue());
        Assert.assertEquals(String.format(CumulativeWeightCalculatorTest.TX_CUMULATIVE_WEIGHT_IS_NOT_AS_EXPECTED_FORMAT, 2), 3, txToCw.get(transaction2.getHash()).intValue());
        Assert.assertEquals(String.format(CumulativeWeightCalculatorTest.TX_CUMULATIVE_WEIGHT_IS_NOT_AS_EXPECTED_FORMAT, 1), 1, txToCw.get(transaction1.getHash()).intValue());
        Assert.assertEquals(String.format(CumulativeWeightCalculatorTest.TX_CUMULATIVE_WEIGHT_IS_NOT_AS_EXPECTED_FORMAT, 0), 7, txToCw.get(transaction.getHash()).intValue());
    }

    @Test
    public void cwCalculationSameAsLegacy() throws Exception {
        Hash[] hashes = new Hash[100];
        hashes[0] = TransactionTestUtils.getRandomTransactionHash();
        TransactionViewModel transactionViewModel1 = new TransactionViewModel(TransactionTestUtils.getRandomTransactionTrits(), hashes[0]);
        transactionViewModel1.store(CumulativeWeightCalculatorTest.tangle, CumulativeWeightCalculatorTest.snapshotProvider.getInitialSnapshot());
        // constant seed for consistent results
        Random random = new Random(181783497276652981L);
        for (int i = 1; i < (hashes.length); i++) {
            hashes[i] = TransactionTestUtils.getRandomTransactionHash();
            TransactionViewModel transactionViewModel = new TransactionViewModel(TransactionTestUtils.getTransactionWithTrunkAndBranch(hashes[((i - (random.nextInt(i))) - 1)], hashes[((i - (random.nextInt(i))) - 1)]), hashes[i]);
            transactionViewModel.store(CumulativeWeightCalculatorTest.tangle, CumulativeWeightCalculatorTest.snapshotProvider.getInitialSnapshot());
            log.debug(String.format("current transaction %.4s \n with trunk %.4s \n and branch %.4s", hashes[i], transactionViewModel.getTrunkTransactionHash(), transactionViewModel.getBranchTransactionHash()));
        }
        Map<HashId, Set<HashId>> ratings = new HashMap<>();
        CumulativeWeightCalculatorTest.updateApproversRecursively(hashes[0], ratings, new HashSet());
        UnIterableMap<HashId, Integer> txToCw = CumulativeWeightCalculatorTest.cumulativeWeightCalculator.calculate(hashes[0]);
        Assert.assertEquals("missing txs from new calculation", ratings.size(), txToCw.size());
        ratings.forEach(( hash, weight) -> {
            log.debug(String.format("tx %.4s has expected weight of %d", hash, weight.size()));
            Assert.assertEquals(("new calculation weight is not as expected for hash " + hash), weight.size(), txToCw.get(hash).intValue());
        });
    }

    @Test
    public void testTangleWithCircle() throws Exception {
        TransactionViewModel transaction;
        Hash randomTransactionHash = TransactionTestUtils.getRandomTransactionHash();
        transaction = new TransactionViewModel(TransactionTestUtils.getTransactionWithTrunkAndBranch(randomTransactionHash, randomTransactionHash), randomTransactionHash);
        transaction.store(CumulativeWeightCalculatorTest.tangle, CumulativeWeightCalculatorTest.snapshotProvider.getInitialSnapshot());
        UnIterableMap<HashId, Integer> txToCw = CumulativeWeightCalculatorTest.cumulativeWeightCalculator.calculate(transaction.getHash());
        Assert.assertEquals("There should be only one tx in the map", 1, txToCw.size());
        Assert.assertEquals("The circle raised the weight", 1, txToCw.get(randomTransactionHash).intValue());
    }

    @Test
    public void testTangleWithCircle2() throws Exception {
        TransactionViewModel transaction;
        TransactionViewModel transaction1;
        TransactionViewModel transaction2;
        TransactionViewModel transaction3;
        TransactionViewModel transaction4;
        Hash randomTransactionHash2 = TransactionTestUtils.getRandomTransactionHash();
        transaction = new TransactionViewModel(TransactionTestUtils.getTransactionWithTrunkAndBranch(randomTransactionHash2, randomTransactionHash2), TransactionTestUtils.getRandomTransactionHash());
        transaction1 = new TransactionViewModel(TransactionTestUtils.getTransactionWithTrunkAndBranch(transaction.getHash(), transaction.getHash()), TransactionTestUtils.getRandomTransactionHash());
        transaction2 = new TransactionViewModel(TransactionTestUtils.getTransactionWithTrunkAndBranch(transaction1.getHash(), transaction1.getHash()), randomTransactionHash2);
        transaction3 = new TransactionViewModel(TransactionTestUtils.getTransactionWithTrunkAndBranch(transaction.getHash(), transaction.getHash()), TransactionTestUtils.getRandomTransactionHash());
        transaction.store(CumulativeWeightCalculatorTest.tangle, CumulativeWeightCalculatorTest.snapshotProvider.getInitialSnapshot());
        transaction1.store(CumulativeWeightCalculatorTest.tangle, CumulativeWeightCalculatorTest.snapshotProvider.getInitialSnapshot());
        transaction2.store(CumulativeWeightCalculatorTest.tangle, CumulativeWeightCalculatorTest.snapshotProvider.getInitialSnapshot());
        transaction3.store(CumulativeWeightCalculatorTest.tangle, CumulativeWeightCalculatorTest.snapshotProvider.getInitialSnapshot());
        CumulativeWeightCalculatorTest.cumulativeWeightCalculator.calculate(transaction.getHash());
        // No infinite loop (which will probably result in an overflow exception) means test has passed
    }

    @Test
    public void testCollsionsInDiamondTangle() throws Exception {
        TransactionViewModel transaction;
        TransactionViewModel transaction1;
        TransactionViewModel transaction2;
        TransactionViewModel transaction3;
        transaction = new TransactionViewModel(TransactionTestUtils.getRandomTransactionTrits(), TransactionTestUtils.getRandomTransactionHash());
        transaction1 = new TransactionViewModel(TransactionTestUtils.getTransactionWithTrunkAndBranch(transaction.getHash(), transaction.getHash()), TransactionTestUtils.getRandomTransactionHash());
        Hash transactionHash2 = getHashWithSimilarPrefix(transaction1);
        transaction2 = new TransactionViewModel(TransactionTestUtils.getTransactionWithTrunkAndBranch(transaction.getHash(), transaction.getHash()), transactionHash2);
        transaction3 = new TransactionViewModel(TransactionTestUtils.getTransactionWithTrunkAndBranch(transaction1.getHash(), transaction2.getHash()), TransactionTestUtils.getRandomTransactionHash());
        transaction.store(CumulativeWeightCalculatorTest.tangle, CumulativeWeightCalculatorTest.snapshotProvider.getInitialSnapshot());
        transaction1.store(CumulativeWeightCalculatorTest.tangle, CumulativeWeightCalculatorTest.snapshotProvider.getInitialSnapshot());
        transaction2.store(CumulativeWeightCalculatorTest.tangle, CumulativeWeightCalculatorTest.snapshotProvider.getInitialSnapshot());
        transaction3.store(CumulativeWeightCalculatorTest.tangle, CumulativeWeightCalculatorTest.snapshotProvider.getInitialSnapshot());
        log.debug("printing transaction in diamond shape \n                      {} \n{}  {}\n                      {}", transaction.getHash(), transaction1.getHash(), transaction2.getHash(), transaction3.getHash());
        UnIterableMap<HashId, Integer> txToCw = CumulativeWeightCalculatorTest.cumulativeWeightCalculator.calculate(transaction.getHash());
        Assert.assertEquals(String.format(CumulativeWeightCalculatorTest.TX_CUMULATIVE_WEIGHT_IS_NOT_AS_EXPECTED_FORMAT, 3), 1, txToCw.get(transaction3.getHash()).intValue());
        Assert.assertEquals(String.format(CumulativeWeightCalculatorTest.TX_CUMULATIVE_WEIGHT_IS_NOT_AS_EXPECTED_FORMAT, 1), 2, txToCw.get(transaction1.getHash()).intValue());
        Assert.assertEquals(String.format(CumulativeWeightCalculatorTest.TX_CUMULATIVE_WEIGHT_IS_NOT_AS_EXPECTED_FORMAT, 2), 2, txToCw.get(transaction2.getHash()).intValue());
        // expected to not count 1 of the parents due to collision
        Assert.assertEquals(String.format(CumulativeWeightCalculatorTest.TX_CUMULATIVE_WEIGHT_IS_NOT_AS_EXPECTED_FORMAT, 0), 3, txToCw.get(transaction.getHash()).intValue());
    }
}

