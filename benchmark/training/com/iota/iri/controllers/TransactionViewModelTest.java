package com.iota.iri.controllers;


import Hash.NULL_HASH;
import SpongeFactory.Mode.CURLP81;
import TransactionViewModel.BRANCH_TRANSACTION_TRINARY_OFFSET;
import TransactionViewModel.BRANCH_TRANSACTION_TRINARY_SIZE;
import TransactionViewModel.TRUNK_TRANSACTION_TRINARY_OFFSET;
import TransactionViewModel.TRUNK_TRANSACTION_TRINARY_SIZE;
import TransactionViewModel.VALUE_TRINARY_OFFSET;
import TransactionViewModel.VALUE_TRINARY_SIZE;
import TransactionViewModel.VALUE_USABLE_TRINARY_SIZE;
import com.iota.iri.TransactionTestUtils;
import com.iota.iri.model.Hash;
import com.iota.iri.model.TransactionHash;
import com.iota.iri.service.snapshot.SnapshotProvider;
import com.iota.iri.storage.Tangle;
import com.iota.iri.utils.Converter;
import java.util.Arrays;
import java.util.Random;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static TransactionViewModel.BRANCH_TRANSACTION_TRINARY_OFFSET;
import static TransactionViewModel.BRANCH_TRANSACTION_TRINARY_SIZE;
import static TransactionViewModel.TRUNK_TRANSACTION_TRINARY_OFFSET;
import static TransactionViewModel.VALUE_TRINARY_SIZE;
import static java.util.Arrays.copyOf;


public class TransactionViewModelTest {
    private static final TemporaryFolder dbFolder = new TemporaryFolder();

    private static final TemporaryFolder logFolder = new TemporaryFolder();

    Logger log = LoggerFactory.getLogger(TransactionViewModelTest.class);

    private static Tangle tangle = new Tangle();

    private static SnapshotProvider snapshotProvider;

    private static final Random seed = new Random();

    @Test
    public void getApprovers() throws Exception {
        TransactionViewModel transactionViewModel;
        TransactionViewModel otherTxVM;
        TransactionViewModel trunkTx;
        TransactionViewModel branchTx;
        byte[] trits = TransactionTestUtils.getRandomTransactionTrits();
        trunkTx = new TransactionViewModel(trits, TransactionHash.calculate(CURLP81, trits));
        branchTx = new TransactionViewModel(trits, TransactionHash.calculate(CURLP81, trits));
        byte[] childTx = TransactionTestUtils.getRandomTransactionTrits();
        System.arraycopy(trunkTx.getHash().trits(), 0, childTx, TRUNK_TRANSACTION_TRINARY_OFFSET, TRUNK_TRANSACTION_TRINARY_SIZE);
        System.arraycopy(branchTx.getHash().trits(), 0, childTx, BRANCH_TRANSACTION_TRINARY_OFFSET, BRANCH_TRANSACTION_TRINARY_SIZE);
        transactionViewModel = new TransactionViewModel(childTx, TransactionHash.calculate(CURLP81, childTx));
        childTx = TransactionTestUtils.getRandomTransactionTrits();
        System.arraycopy(trunkTx.getHash().trits(), 0, childTx, TRUNK_TRANSACTION_TRINARY_OFFSET, TRUNK_TRANSACTION_TRINARY_SIZE);
        System.arraycopy(branchTx.getHash().trits(), 0, childTx, BRANCH_TRANSACTION_TRINARY_OFFSET, BRANCH_TRANSACTION_TRINARY_SIZE);
        otherTxVM = new TransactionViewModel(childTx, TransactionHash.calculate(CURLP81, childTx));
        otherTxVM.store(TransactionViewModelTest.tangle, TransactionViewModelTest.snapshotProvider.getInitialSnapshot());
        transactionViewModel.store(TransactionViewModelTest.tangle, TransactionViewModelTest.snapshotProvider.getInitialSnapshot());
        trunkTx.store(TransactionViewModelTest.tangle, TransactionViewModelTest.snapshotProvider.getInitialSnapshot());
        branchTx.store(TransactionViewModelTest.tangle, TransactionViewModelTest.snapshotProvider.getInitialSnapshot());
        Set<Hash> approvers = trunkTx.getApprovers(TransactionViewModelTest.tangle).getHashes();
        Assert.assertNotEquals(approvers.size(), 0);
    }

    @Test
    public void trits() throws Exception {
        byte[] blanks = new byte[13];
        for (int i = 0; (i++) < 1000;) {
            byte[] trits = TransactionTestUtils.getRandomTransactionTrits();
            byte[] searchTrits;
            System.arraycopy(new byte[VALUE_TRINARY_SIZE], 0, trits, VALUE_TRINARY_OFFSET, VALUE_TRINARY_SIZE);
            Converter.copyTrits(TransactionViewModelTest.seed.nextLong(), trits, VALUE_TRINARY_OFFSET, VALUE_USABLE_TRINARY_SIZE);
            System.arraycopy(blanks, 0, trits, ((TRUNK_TRANSACTION_TRINARY_OFFSET) - (blanks.length)), blanks.length);
            System.arraycopy(blanks, 0, trits, ((BRANCH_TRANSACTION_TRINARY_OFFSET) - (blanks.length)), blanks.length);
            System.arraycopy(blanks, 0, trits, (((BRANCH_TRANSACTION_TRINARY_OFFSET) + (BRANCH_TRANSACTION_TRINARY_SIZE)) - (blanks.length)), blanks.length);
            Hash hash = TransactionTestUtils.getRandomTransactionHash();
            TransactionViewModel transactionViewModel = new TransactionViewModel(trits, hash);
            transactionViewModel.store(TransactionViewModelTest.tangle, TransactionViewModelTest.snapshotProvider.getInitialSnapshot());
            Assert.assertArrayEquals(transactionViewModel.trits(), TransactionViewModel.fromHash(TransactionViewModelTest.tangle, transactionViewModel.getHash()).trits());
        }
    }

    @Test
    public void getBytes() throws Exception {
        for (int i = 0; (i++) < 1000;) {
            byte[] trits = TransactionTestUtils.getRandomTransactionTrits();
            System.arraycopy(new byte[VALUE_TRINARY_SIZE], 0, trits, VALUE_TRINARY_OFFSET, VALUE_TRINARY_SIZE);
            Converter.copyTrits(TransactionViewModelTest.seed.nextLong(), trits, VALUE_TRINARY_OFFSET, VALUE_USABLE_TRINARY_SIZE);
            Hash hash = TransactionTestUtils.getRandomTransactionHash();
            TransactionViewModel transactionViewModel = new TransactionViewModel(trits, hash);
            transactionViewModel.store(TransactionViewModelTest.tangle, TransactionViewModelTest.snapshotProvider.getInitialSnapshot());
            Assert.assertArrayEquals(transactionViewModel.getBytes(), TransactionViewModel.fromHash(TransactionViewModelTest.tangle, transactionViewModel.getHash()).getBytes());
        }
    }

    @Test
    public void updateHeightShouldWork() throws Exception {
        int count = 4;
        TransactionViewModel[] transactionViewModels = new TransactionViewModel[count];
        Hash hash = TransactionTestUtils.getRandomTransactionHash();
        transactionViewModels[0] = new TransactionViewModel(TransactionTestUtils.getTransactionWithTrunkAndBranch(NULL_HASH, NULL_HASH), hash);
        transactionViewModels[0].store(TransactionViewModelTest.tangle, TransactionViewModelTest.snapshotProvider.getInitialSnapshot());
        for (int i = 0; (++i) < count;) {
            transactionViewModels[i] = new TransactionViewModel(TransactionTestUtils.getTransactionWithTrunkAndBranch(hash, NULL_HASH), (hash = TransactionTestUtils.getRandomTransactionHash()));
            transactionViewModels[i].store(TransactionViewModelTest.tangle, TransactionViewModelTest.snapshotProvider.getInitialSnapshot());
        }
        transactionViewModels[(count - 1)].updateHeights(TransactionViewModelTest.tangle, TransactionViewModelTest.snapshotProvider.getInitialSnapshot());
        for (int i = count; i > 1;) {
            Assert.assertEquals(i, TransactionViewModel.fromHash(TransactionViewModelTest.tangle, transactionViewModels[(--i)].getHash()).getHeight());
        }
    }

    @Test
    public void updateHeightPrefilledSlotShouldFail() throws Exception {
        int count = 4;
        TransactionViewModel[] transactionViewModels = new TransactionViewModel[count];
        Hash hash = TransactionTestUtils.getRandomTransactionHash();
        for (int i = 0; (++i) < count;) {
            transactionViewModels[i] = new TransactionViewModel(TransactionTestUtils.getTransactionWithTrunkAndBranch(hash, NULL_HASH), (hash = TransactionTestUtils.getRandomTransactionHash()));
            transactionViewModels[i].store(TransactionViewModelTest.tangle, TransactionViewModelTest.snapshotProvider.getInitialSnapshot());
        }
        transactionViewModels[(count - 1)].updateHeights(TransactionViewModelTest.tangle, TransactionViewModelTest.snapshotProvider.getInitialSnapshot());
        for (int i = count; i > 1;) {
            Assert.assertEquals(0, TransactionViewModel.fromHash(TransactionViewModelTest.tangle, transactionViewModels[(--i)].getHash()).getHeight());
        }
    }

    @Test
    public void findShouldBeSuccessful() throws Exception {
        byte[] trits = TransactionTestUtils.getRandomTransactionTrits();
        TransactionViewModel transactionViewModel = new TransactionViewModel(trits, TransactionHash.calculate(CURLP81, trits));
        transactionViewModel.store(TransactionViewModelTest.tangle, TransactionViewModelTest.snapshotProvider.getInitialSnapshot());
        Hash hash = transactionViewModel.getHash();
        Assert.assertArrayEquals(TransactionViewModel.find(TransactionViewModelTest.tangle, copyOf(hash.bytes(), MainnetConfig.Defaults.REQ_HASH_SIZE)).getBytes(), transactionViewModel.getBytes());
    }

    @Test
    public void findShouldReturnNull() throws Exception {
        byte[] trits = TransactionTestUtils.getRandomTransactionTrits();
        TransactionViewModel transactionViewModel = new TransactionViewModel(trits, TransactionHash.calculate(CURLP81, trits));
        trits = TransactionTestUtils.getRandomTransactionTrits();
        TransactionViewModel transactionViewModelNoSave = new TransactionViewModel(trits, TransactionHash.calculate(CURLP81, trits));
        transactionViewModel.store(TransactionViewModelTest.tangle, TransactionViewModelTest.snapshotProvider.getInitialSnapshot());
        Hash hash = transactionViewModelNoSave.getHash();
        Assert.assertFalse(Arrays.equals(TransactionViewModel.find(TransactionViewModelTest.tangle, copyOf(hash.bytes(), new com.iota.iri.conf.MainnetConfig().getRequestHashSize())).getBytes(), transactionViewModel.getBytes()));
    }

    @Test
    public void firstShouldFindTx() throws Exception {
        byte[] trits = TransactionTestUtils.getRandomTransactionTrits();
        TransactionViewModel transactionViewModel = new TransactionViewModel(trits, TransactionHash.calculate(CURLP81, trits));
        transactionViewModel.store(TransactionViewModelTest.tangle, TransactionViewModelTest.snapshotProvider.getInitialSnapshot());
        TransactionViewModel result = TransactionViewModel.first(TransactionViewModelTest.tangle);
        Assert.assertEquals(transactionViewModel.getHash(), result.getHash());
    }
}

