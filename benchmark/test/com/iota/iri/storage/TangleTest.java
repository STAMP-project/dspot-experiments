package com.iota.iri.storage;


import SpongeFactory.Mode.CURLP81;
import com.iota.iri.controllers.TransactionViewModel;
import com.iota.iri.model.TransactionHash;
import com.iota.iri.model.persistables.Tag;
import com.iota.iri.service.snapshot.SnapshotProvider;
import java.util.Arrays;
import java.util.Random;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;


public class TangleTest {
    private final TemporaryFolder dbFolder = new TemporaryFolder();

    private final TemporaryFolder logFolder = new TemporaryFolder();

    private Tangle tangle = new Tangle();

    private static SnapshotProvider snapshotProvider;

    private static final Random seed = new Random();

    @Test
    public void getKeysStartingWithValue() throws Exception {
        byte[] trits = TangleTest.getRandomTransactionTrits();
        TransactionViewModel transactionViewModel = new TransactionViewModel(trits, TransactionHash.calculate(CURLP81, trits));
        transactionViewModel.store(tangle, TangleTest.snapshotProvider.getInitialSnapshot());
        Set<Indexable> tag = tangle.keysStartingWith(Tag.class, Arrays.copyOf(transactionViewModel.getTagValue().bytes(), 15));
        Assert.assertNotEquals(tag.size(), 0);
    }
}

