package com.iota.iri.service.snapshot.impl;


import Hash.NULL_HASH;
import HashFactory.ADDRESS;
import HashFactory.TRANSACTION;
import com.iota.iri.TangleMockUtils;
import com.iota.iri.controllers.TransactionViewModel;
import com.iota.iri.model.Hash;
import com.iota.iri.model.persistables.Transaction;
import com.iota.iri.service.snapshot.Snapshot;
import com.iota.iri.service.snapshot.SnapshotException;
import com.iota.iri.service.snapshot.SnapshotProvider;
import com.iota.iri.storage.Tangle;
import java.util.Map;
import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;


// endregion ////////////////////////////////////////////////////////////////////////////////////////////////////////
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class SnapshotServiceImplTest {
    // region [CONSTANTS FOR THE TEST] //////////////////////////////////////////////////////////////////////////////////
    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    private enum MockedMilestone {

        A("ARWY9LWHXEWNL9DTN9IGMIMIVSBQUIEIDSFRYTCSXQARRTVEUFSBWFZRQOJUQNAGQLWHTFNVECELCOFYB", 70001, 1542146728L),
        B("BRWY9LWHXEWNL9DTN9IGMIMIVSBQUIEIDSFRYTCSXQARRTVEUFSBWFZRQOJUQNAGQLWHTFNVECELCOFYB", 70005, 1546146728L);
        private final Hash transactionHash;

        private final int milestoneIndex;

        private final long timestamp;

        MockedMilestone(String transactionHash, int milestoneIndex, long timestamp) {
            this.transactionHash = TRANSACTION.create(transactionHash);
            this.milestoneIndex = milestoneIndex;
            this.timestamp = timestamp;
        }

        public void mock(Tangle tangle, Map<Hash, Long> stateDiff) {
            TangleMockUtils.mockMilestone(tangle, transactionHash, milestoneIndex);
            TangleMockUtils.mockStateDiff(tangle, transactionHash, stateDiff);
            Transaction mockedTransaction = TangleMockUtils.mockTransaction(tangle, transactionHash);
            mockedTransaction.timestamp = timestamp;
        }
    }

    private static final Hash ADDRESS_1 = ADDRESS.create("EKRQUHQRZWDGFTRFSTSPAZYBXMEYGHOFIVXDCRRTXUJ9HXOAYLKFEBEZPWEPTG9ZFTOHGCQZCHIKKQ9RD");

    private static final Hash ADDRESS_2 = ADDRESS.create("GRWY9LWHXEWNL9DTN9IGMIMIVSBQUIEIDSFRYTCSXQARRTVEUFSBWFZRQOJUQNAGQLWHTFNVECELCOFYB");

    private static final Hash ADDRESS_3 = ADDRESS.create("JLDULQUXBL99AGZZKXMACLJRAYDUTBTMFGLEHVTLDTHVUIBYV9ZKGHLWCVFJVIYGHNXNTQUYQTISHDUSW");

    // endregion ////////////////////////////////////////////////////////////////////////////////////////////////////////
    // region [BOILERPLATE] /////////////////////////////////////////////////////////////////////////////////////////////
    @Mock
    private Tangle tangle;

    @Mock
    private SnapshotProvider snapshotProvider;

    @InjectMocks
    private SnapshotServiceImpl snapshotService;

    // endregion ////////////////////////////////////////////////////////////////////////////////////////////////////////
    // region [TEST: replayMilestones] //////////////////////////////////////////////////////////////////////////////////
    @Test
    public void replayMilestonesSingle() throws Exception {
        Snapshot latestSnapshot = snapshotProvider.getLatestSnapshot();
        SnapshotServiceImplTest.MockedMilestone.A.mock(tangle, SnapshotServiceImplTest.createBalanceMap(NULL_HASH, (-2337L), SnapshotServiceImplTest.ADDRESS_1, 1337L, SnapshotServiceImplTest.ADDRESS_2, 1000L));
        snapshotService.replayMilestones(latestSnapshot, SnapshotServiceImplTest.MockedMilestone.A.milestoneIndex);
        Assert.assertEquals("the snapshot should have the milestone index of the last applied milestone", SnapshotServiceImplTest.MockedMilestone.A.milestoneIndex, latestSnapshot.getIndex());
        Assert.assertEquals("the snapshot should have the transaction hash of the last applied milestone", SnapshotServiceImplTest.MockedMilestone.A.transactionHash, latestSnapshot.getHash());
        Assert.assertEquals("the snapshot should have the timestamp of the last applied milestone", SnapshotServiceImplTest.MockedMilestone.A.timestamp, latestSnapshot.getTimestamp());
        Assert.assertEquals("the balance of the addresses should reflect the accumulated changes of the milestones", (((TransactionViewModel.SUPPLY) - 1337L) - 1000L), ((long) (latestSnapshot.getBalance(NULL_HASH))));
        Assert.assertEquals("the balance of the addresses should reflect the accumulated changes of the milestones", 1337L, ((long) (latestSnapshot.getBalance(SnapshotServiceImplTest.ADDRESS_1))));
        Assert.assertEquals("the balance of the addresses should reflect the accumulated changes of the milestones", 1000L, ((long) (latestSnapshot.getBalance(SnapshotServiceImplTest.ADDRESS_2))));
    }

    @Test
    public void replayMilestonesMultiple() throws Exception {
        Snapshot latestSnapshot = snapshotProvider.getLatestSnapshot();
        SnapshotServiceImplTest.MockedMilestone.A.mock(tangle, SnapshotServiceImplTest.createBalanceMap(NULL_HASH, (-2337L), SnapshotServiceImplTest.ADDRESS_1, 1337L, SnapshotServiceImplTest.ADDRESS_2, 1000L));
        SnapshotServiceImplTest.MockedMilestone.B.mock(tangle, SnapshotServiceImplTest.createBalanceMap(NULL_HASH, (-1234L), SnapshotServiceImplTest.ADDRESS_2, 1000L, SnapshotServiceImplTest.ADDRESS_3, 234L));
        snapshotService.replayMilestones(latestSnapshot, SnapshotServiceImplTest.MockedMilestone.B.milestoneIndex);
        Assert.assertEquals("the snapshot should have the milestone index of the last applied milestone", SnapshotServiceImplTest.MockedMilestone.B.milestoneIndex, latestSnapshot.getIndex());
        Assert.assertEquals("the snapshot should have the transaction hash of the last applied milestone", SnapshotServiceImplTest.MockedMilestone.B.transactionHash, latestSnapshot.getHash());
        Assert.assertEquals("the snapshot should have the timestamp of the last applied milestone", SnapshotServiceImplTest.MockedMilestone.B.timestamp, latestSnapshot.getTimestamp());
        Assert.assertEquals("the balance of the addresses should reflect the accumulated changes of the milestones", ((((TransactionViewModel.SUPPLY) - 1337L) - 2000L) - 234L), ((long) (latestSnapshot.getBalance(NULL_HASH))));
        Assert.assertEquals("the balance of the addresses should reflect the accumulated changes of the milestones", 1337L, ((long) (latestSnapshot.getBalance(SnapshotServiceImplTest.ADDRESS_1))));
        Assert.assertEquals("the balance of the addresses should reflect the accumulated changes of the milestones", 2000L, ((long) (latestSnapshot.getBalance(SnapshotServiceImplTest.ADDRESS_2))));
        Assert.assertEquals("the balance of the addresses should reflect the accumulated changes of the milestones", 234L, ((long) (latestSnapshot.getBalance(SnapshotServiceImplTest.ADDRESS_3))));
    }

    @Test
    public void replayMilestonesInconsistent() {
        Snapshot initialSnapshot = snapshotProvider.getInitialSnapshot();
        Snapshot latestSnapshot = snapshotProvider.getLatestSnapshot();
        SnapshotServiceImplTest.MockedMilestone.A.mock(tangle, SnapshotServiceImplTest.createBalanceMap(NULL_HASH, (-2337L), SnapshotServiceImplTest.ADDRESS_1, 1337L, SnapshotServiceImplTest.ADDRESS_2, 1000L));
        // create inconsistent milestone (the sum of the balance changes is not "0")
        SnapshotServiceImplTest.MockedMilestone.B.mock(tangle, SnapshotServiceImplTest.createBalanceMap(NULL_HASH, (-1234L), SnapshotServiceImplTest.ADDRESS_2, 1000L, SnapshotServiceImplTest.ADDRESS_3, 1234L));
        try {
            snapshotService.replayMilestones(latestSnapshot, SnapshotServiceImplTest.MockedMilestone.B.milestoneIndex);
            Assert.fail("replaying inconsistent milestones should raise an exception");
        } catch (SnapshotException e) {
            Assert.assertEquals("failed replays should not modify the snapshot", initialSnapshot, latestSnapshot);
        }
    }

    // endregion ////////////////////////////////////////////////////////////////////////////////////////////////////////
    // region [TEST: rollbackMilestones] ////////////////////////////////////////////////////////////////////////////////
    @Test
    public void rollbackMilestonesSingle() throws Exception {
        Snapshot latestSnapshot = snapshotProvider.getLatestSnapshot();
        replayMilestonesMultiple();
        snapshotService.rollBackMilestones(latestSnapshot, SnapshotServiceImplTest.MockedMilestone.B.milestoneIndex);
        Assert.assertEquals("the snapshot should have the milestone index of the milestone that we rolled back to", SnapshotServiceImplTest.MockedMilestone.A.milestoneIndex, latestSnapshot.getIndex());
        Assert.assertEquals("the snapshot should have the transaction hash of the milestone that we rolled back to", SnapshotServiceImplTest.MockedMilestone.A.transactionHash, latestSnapshot.getHash());
        Assert.assertEquals("the snapshot should have the timestamp of the milestone that we rolled back to", SnapshotServiceImplTest.MockedMilestone.A.timestamp, latestSnapshot.getTimestamp());
        Assert.assertEquals("the balance of the addresses should reflect the balances of the rolled back milestone", (((TransactionViewModel.SUPPLY) - 1337L) - 1000L), ((long) (latestSnapshot.getBalance(NULL_HASH))));
        Assert.assertEquals("the balance of the addresses should reflect the balances of the rolled back milestone", 1337L, ((long) (latestSnapshot.getBalance(SnapshotServiceImplTest.ADDRESS_1))));
        Assert.assertEquals("the balance of the addresses should reflect the balances of the rolled back milestone", 1000L, ((long) (latestSnapshot.getBalance(SnapshotServiceImplTest.ADDRESS_2))));
    }

    @Test
    public void rollbackMilestonesAll() throws Exception {
        Snapshot initialSnapshot = snapshotProvider.getInitialSnapshot();
        Snapshot latestSnapshot = snapshotProvider.getLatestSnapshot();
        replayMilestonesMultiple();
        snapshotService.rollBackMilestones(latestSnapshot, SnapshotServiceImplTest.MockedMilestone.A.milestoneIndex);
        Assert.assertEquals("rolling back all milestones should revert all changes", initialSnapshot, latestSnapshot);
    }

    @Test
    public void rollbackMilestonesInvalidIndex() throws Exception {
        Snapshot initialSnapshot = snapshotProvider.getInitialSnapshot();
        Snapshot latestSnapshot = snapshotProvider.getLatestSnapshot();
        replayMilestonesMultiple();
        Snapshot clonedSnapshot = latestSnapshot.clone();
        Assert.assertEquals("the cloned snapshots should be equal to the source", clonedSnapshot, latestSnapshot);
        try {
            snapshotService.rollBackMilestones(latestSnapshot, ((latestSnapshot.getIndex()) + 1));
            Assert.fail("rolling back non-applied milestones should fail");
        } catch (SnapshotException e) {
            Assert.assertEquals("failed rollbacks should not modify the snapshot", clonedSnapshot, latestSnapshot);
        }
        try {
            snapshotService.rollBackMilestones(latestSnapshot, initialSnapshot.getIndex());
            Assert.fail("rolling back milestones prior to the genesis should fail");
        } catch (SnapshotException e) {
            Assert.assertEquals("failed rollbacks should not modify the snapshot", clonedSnapshot, latestSnapshot);
        }
    }
}

