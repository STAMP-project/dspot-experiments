package com.iota.iri.network.impl;


import com.iota.iri.TangleMockUtils;
import com.iota.iri.TransactionTestUtils;
import com.iota.iri.controllers.TipsViewModel;
import com.iota.iri.controllers.TransactionViewModel;
import com.iota.iri.model.Hash;
import com.iota.iri.model.persistables.Transaction;
import com.iota.iri.network.Node;
import com.iota.iri.network.TransactionRequester;
import com.iota.iri.service.snapshot.SnapshotProvider;
import com.iota.iri.storage.Tangle;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;


public class TransactionRequesterWorkerImplTest {
    // Good
    private static final TransactionViewModel TVMRandomNull = new TransactionViewModel(TransactionTestUtils.getRandomTransaction(), Hash.NULL_HASH);

    private static final TransactionViewModel TVMRandomNotNull = new TransactionViewModel(TransactionTestUtils.getRandomTransaction(), TransactionTestUtils.getRandomTransactionHash());

    private static final TransactionViewModel TVMAll9Null = new TransactionViewModel(TransactionTestUtils.get9Transaction(), Hash.NULL_HASH);

    private static final TransactionViewModel TVMAll9NotNull = new TransactionViewModel(TransactionTestUtils.get9Transaction(), TransactionTestUtils.getRandomTransactionHash());

    // Bad
    private static final TransactionViewModel TVMNullNull = new TransactionViewModel(((Transaction) (null)), Hash.NULL_HASH);

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private static SnapshotProvider snapshotProvider;

    private static TransactionRequester requester;

    private static TransactionRequesterWorkerImpl worker;

    @Mock
    private Tangle tangle;

    @Mock
    private Node node;

    @Mock
    private TipsViewModel tipsVM;

    @Test
    public void workerActive() throws Exception {
        Assert.assertFalse("Empty worker should not be active", TransactionRequesterWorkerImplTest.worker.isActive());
        fillRequester();
        Assert.assertTrue("Worker should be active when it requester is over threshold", TransactionRequesterWorkerImplTest.worker.isActive());
    }

    @Test
    public void processRequestQueueTest() throws Exception {
        // getTransactionToSendWithRequest starts reading from solid tips, so mock data from that call
        Mockito.when(tipsVM.getRandomSolidTipHash()).thenReturn(TransactionRequesterWorkerImplTest.TVMRandomNull.getHash(), TransactionRequesterWorkerImplTest.TVMRandomNotNull.getHash(), TransactionRequesterWorkerImplTest.TVMAll9Null.getHash(), TransactionRequesterWorkerImplTest.TVMAll9NotNull.getHash(), TransactionRequesterWorkerImplTest.TVMNullNull.getHash(), null);
        Assert.assertFalse("Unfilled queue shouldnt process", TransactionRequesterWorkerImplTest.worker.processRequestQueue());
        // Requester never goes down since nodes don't really request
        fillRequester();
        TangleMockUtils.mockTransaction(tangle, TransactionRequesterWorkerImplTest.TVMRandomNull.getHash(), TransactionTestUtils.buildTransaction(TransactionRequesterWorkerImplTest.TVMRandomNull.trits()));
        Assert.assertTrue("Null transaction hash should be processed", TransactionRequesterWorkerImplTest.worker.processRequestQueue());
        TangleMockUtils.mockTransaction(tangle, TransactionRequesterWorkerImplTest.TVMRandomNotNull.getHash(), TransactionTestUtils.buildTransaction(TransactionRequesterWorkerImplTest.TVMRandomNotNull.trits()));
        Assert.assertTrue("Not null transaction hash should be processed", TransactionRequesterWorkerImplTest.worker.processRequestQueue());
        TangleMockUtils.mockTransaction(tangle, TransactionRequesterWorkerImplTest.TVMAll9Null.getHash(), TransactionTestUtils.buildTransaction(TransactionRequesterWorkerImplTest.TVMAll9Null.trits()));
        Assert.assertTrue("Null transaction hash should be processed", TransactionRequesterWorkerImplTest.worker.processRequestQueue());
        TangleMockUtils.mockTransaction(tangle, TransactionRequesterWorkerImplTest.TVMAll9NotNull.getHash(), TransactionTestUtils.buildTransaction(TransactionRequesterWorkerImplTest.TVMAll9NotNull.trits()));
        Assert.assertTrue("All 9s transaction should be processed", TransactionRequesterWorkerImplTest.worker.processRequestQueue());
        // Null gets loaded as all 0, so type is 0 -> Filled
        TangleMockUtils.mockTransaction(tangle, TransactionRequesterWorkerImplTest.TVMNullNull.getHash(), null);
        Assert.assertTrue("0 transaction should be processed", TransactionRequesterWorkerImplTest.worker.processRequestQueue());
        // null -> NULL_HASH -> gets loaded as all 0 -> filled
        Assert.assertTrue("Null transaction should be processed", TransactionRequesterWorkerImplTest.worker.processRequestQueue());
    }

    @Test
    public void validTipToAddTest() throws Exception {
        Assert.assertTrue("Null transaction hash should always be accepted", TransactionRequesterWorkerImplTest.worker.isValidTransaction(TransactionRequesterWorkerImplTest.TVMRandomNull));
        Assert.assertTrue("Not null transaction hash should always be accepted", TransactionRequesterWorkerImplTest.worker.isValidTransaction(TransactionRequesterWorkerImplTest.TVMRandomNotNull));
        Assert.assertTrue("Null transaction hash should always be accepted", TransactionRequesterWorkerImplTest.worker.isValidTransaction(TransactionRequesterWorkerImplTest.TVMAll9Null));
        Assert.assertTrue("All 9s transaction should be accepted", TransactionRequesterWorkerImplTest.worker.isValidTransaction(TransactionRequesterWorkerImplTest.TVMAll9NotNull));
        // Null gets loaded as all 0, so type is 0 -> Filled
        Assert.assertTrue("0 transaction should be accepted", TransactionRequesterWorkerImplTest.worker.isValidTransaction(TransactionRequesterWorkerImplTest.TVMNullNull));
        Assert.assertFalse("Null transaction should not be accepted", TransactionRequesterWorkerImplTest.worker.isValidTransaction(null));
    }
}

