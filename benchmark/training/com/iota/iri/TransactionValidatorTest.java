package com.iota.iri;


import SpongeFactory.Mode.CURLP81;
import com.iota.iri.controllers.TransactionViewModel;
import com.iota.iri.crypto.SpongeFactory;
import com.iota.iri.model.TransactionHash;
import com.iota.iri.service.snapshot.SnapshotProvider;
import com.iota.iri.storage.Tangle;
import com.iota.iri.utils.Converter;
import org.junit.Assert;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;


public class TransactionValidatorTest {
    private static final int MAINNET_MWM = 14;

    private static final TemporaryFolder dbFolder = new TemporaryFolder();

    private static final TemporaryFolder logFolder = new TemporaryFolder();

    private static Tangle tangle;

    private static SnapshotProvider snapshotProvider;

    private static TransactionValidator txValidator;

    @Test
    public void testMinMwm() throws InterruptedException {
        TransactionValidatorTest.txValidator.init(false, 5);
        Assert.assertTrue(((TransactionValidatorTest.txValidator.getMinWeightMagnitude()) == 13));
        TransactionValidatorTest.txValidator.shutdown();
        TransactionValidatorTest.txValidator.init(false, TransactionValidatorTest.MAINNET_MWM);
    }

    @Test
    public void validateTrits() {
        byte[] trits = TransactionTestUtils.getRandomTransactionTrits();
        Converter.copyTrits(0, trits, 0, trits.length);
        TransactionValidatorTest.txValidator.validateTrits(trits, TransactionValidatorTest.MAINNET_MWM);
    }

    @Test(expected = RuntimeException.class)
    public void validateTritsWithInvalidMetadata() {
        byte[] trits = TransactionTestUtils.getRandomTransactionTrits();
        TransactionValidatorTest.txValidator.validateTrits(trits, TransactionValidatorTest.MAINNET_MWM);
    }

    @Test
    public void validateBytesWithNewCurl() throws Exception {
        byte[] trits = TransactionTestUtils.getRandomTransactionTrits();
        Converter.copyTrits(0, trits, 0, trits.length);
        byte[] bytes = Converter.allocateBytesForTrits(trits.length);
        Converter.bytes(trits, 0, bytes, 0, trits.length);
        TransactionValidatorTest.txValidator.validateBytes(bytes, TransactionValidatorTest.txValidator.getMinWeightMagnitude(), SpongeFactory.create(CURLP81));
    }

    @Test
    public void verifyTxIsSolid() throws Exception {
        TransactionViewModel tx = getTxWithBranchAndTrunk();
        Assert.assertTrue(TransactionValidatorTest.txValidator.checkSolidity(tx.getHash(), false));
        Assert.assertTrue(TransactionValidatorTest.txValidator.checkSolidity(tx.getHash(), true));
    }

    @Test
    public void verifyTxIsNotSolid() throws Exception {
        TransactionViewModel tx = getTxWithoutBranchAndTrunk();
        Assert.assertFalse(TransactionValidatorTest.txValidator.checkSolidity(tx.getHash(), false));
        Assert.assertFalse(TransactionValidatorTest.txValidator.checkSolidity(tx.getHash(), true));
    }

    @Test
    public void addSolidTransactionWithoutErrors() {
        byte[] trits = TransactionTestUtils.getRandomTransactionTrits();
        Converter.copyTrits(0, trits, 0, trits.length);
        TransactionValidatorTest.txValidator.addSolidTransaction(TransactionHash.calculate(CURLP81, trits));
    }

    @Test
    public void testTransactionPropagation() throws Exception {
        TransactionViewModel leftChildLeaf = TransactionTestUtils.createTransactionWithTrytes("CHILDTX");
        leftChildLeaf.updateSolid(true);
        leftChildLeaf.store(TransactionValidatorTest.tangle, TransactionValidatorTest.snapshotProvider.getInitialSnapshot());
        TransactionViewModel rightChildLeaf = TransactionTestUtils.createTransactionWithTrytes("CHILDTWOTX");
        rightChildLeaf.updateSolid(true);
        rightChildLeaf.store(TransactionValidatorTest.tangle, TransactionValidatorTest.snapshotProvider.getInitialSnapshot());
        TransactionViewModel parent = TransactionTestUtils.createTransactionWithTrunkAndBranch("PARENT", leftChildLeaf.getHash(), rightChildLeaf.getHash());
        parent.updateSolid(false);
        parent.store(TransactionValidatorTest.tangle, TransactionValidatorTest.snapshotProvider.getInitialSnapshot());
        TransactionViewModel parentSibling = TransactionTestUtils.createTransactionWithTrytes("PARENTLEAF");
        parentSibling.updateSolid(true);
        parentSibling.store(TransactionValidatorTest.tangle, TransactionValidatorTest.snapshotProvider.getInitialSnapshot());
        TransactionViewModel grandParent = TransactionTestUtils.createTransactionWithTrunkAndBranch("GRANDPARENT", parent.getHash(), parentSibling.getHash());
        grandParent.updateSolid(false);
        grandParent.store(TransactionValidatorTest.tangle, TransactionValidatorTest.snapshotProvider.getInitialSnapshot());
        TransactionValidatorTest.txValidator.addSolidTransaction(leftChildLeaf.getHash());
        while (!(TransactionValidatorTest.txValidator.isNewSolidTxSetsEmpty())) {
            TransactionValidatorTest.txValidator.propagateSolidTransactions();
        } 
        parent = TransactionViewModel.fromHash(TransactionValidatorTest.tangle, parent.getHash());
        Assert.assertTrue("Parent tx was expected to be solid", parent.isSolid());
        grandParent = TransactionViewModel.fromHash(TransactionValidatorTest.tangle, grandParent.getHash());
        Assert.assertTrue("Grandparent  was expected to be solid", grandParent.isSolid());
    }

    @Test
    public void testTransactionPropagationFailure() throws Exception {
        TransactionViewModel leftChildLeaf = new TransactionViewModel(TransactionTestUtils.getRandomTransactionTrits(), TransactionTestUtils.getRandomTransactionHash());
        leftChildLeaf.updateSolid(true);
        leftChildLeaf.store(TransactionValidatorTest.tangle, TransactionValidatorTest.snapshotProvider.getInitialSnapshot());
        TransactionViewModel rightChildLeaf = new TransactionViewModel(TransactionTestUtils.getRandomTransactionTrits(), TransactionTestUtils.getRandomTransactionHash());
        rightChildLeaf.updateSolid(true);
        rightChildLeaf.store(TransactionValidatorTest.tangle, TransactionValidatorTest.snapshotProvider.getInitialSnapshot());
        TransactionViewModel parent = new TransactionViewModel(TransactionTestUtils.getTransactionWithTrunkAndBranch(leftChildLeaf.getHash(), rightChildLeaf.getHash()), TransactionTestUtils.getRandomTransactionHash());
        parent.updateSolid(false);
        parent.store(TransactionValidatorTest.tangle, TransactionValidatorTest.snapshotProvider.getInitialSnapshot());
        TransactionViewModel parentSibling = new TransactionViewModel(TransactionTestUtils.getRandomTransactionTrits(), TransactionTestUtils.getRandomTransactionHash());
        parentSibling.updateSolid(false);
        parentSibling.store(TransactionValidatorTest.tangle, TransactionValidatorTest.snapshotProvider.getInitialSnapshot());
        TransactionViewModel grandParent = new TransactionViewModel(TransactionTestUtils.getTransactionWithTrunkAndBranch(parent.getHash(), parentSibling.getHash()), TransactionTestUtils.getRandomTransactionHash());
        grandParent.updateSolid(false);
        grandParent.store(TransactionValidatorTest.tangle, TransactionValidatorTest.snapshotProvider.getInitialSnapshot());
        TransactionValidatorTest.txValidator.addSolidTransaction(leftChildLeaf.getHash());
        while (!(TransactionValidatorTest.txValidator.isNewSolidTxSetsEmpty())) {
            TransactionValidatorTest.txValidator.propagateSolidTransactions();
        } 
        parent = TransactionViewModel.fromHash(TransactionValidatorTest.tangle, parent.getHash());
        Assert.assertTrue("Parent tx was expected to be solid", parent.isSolid());
        grandParent = TransactionViewModel.fromHash(TransactionValidatorTest.tangle, grandParent.getHash());
        Assert.assertFalse("GrandParent tx was expected to be not solid", grandParent.isSolid());
    }
}

