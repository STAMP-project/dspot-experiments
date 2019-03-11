package com.iota.iri.service.tipselection.impl;


import Hash.NULL_HASH;
import SpongeFactory.Mode.CURLP81;
import com.iota.iri.model.Hash;
import com.iota.iri.model.TransactionHash;
import com.iota.iri.service.milestone.LatestMilestoneTracker;
import com.iota.iri.service.snapshot.SnapshotProvider;
import com.iota.iri.service.tipselection.EntryPointSelector;
import com.iota.iri.storage.Tangle;
import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;


@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class EntryPointSelectorImplTest {
    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Mock
    private LatestMilestoneTracker latestMilestoneTracker;

    @Mock
    private Tangle tangle;

    private static SnapshotProvider snapshotProvider;

    @Test
    public void testEntryPointBWithTangleData() throws Exception {
        Hash milestoneHash = TransactionHash.calculate(CURLP81, new byte[]{ 1 });
        mockTangleBehavior(milestoneHash);
        mockMilestoneTrackerBehavior(((EntryPointSelectorImplTest.snapshotProvider.getInitialSnapshot().getIndex()) + 1), NULL_HASH);
        EntryPointSelector entryPointSelector = new EntryPointSelectorImpl(tangle, EntryPointSelectorImplTest.snapshotProvider, latestMilestoneTracker);
        Hash entryPoint = entryPointSelector.getEntryPoint(10);
        Assert.assertEquals("The entry point should be the milestone in the Tangle", milestoneHash, entryPoint);
    }

    @Test
    public void testEntryPointAWithoutTangleData() throws Exception {
        mockMilestoneTrackerBehavior(0, NULL_HASH);
        EntryPointSelector entryPointSelector = new EntryPointSelectorImpl(tangle, EntryPointSelectorImplTest.snapshotProvider, latestMilestoneTracker);
        Hash entryPoint = entryPointSelector.getEntryPoint(10);
        Assert.assertEquals("The entry point should be the last tracked solid milestone", NULL_HASH, entryPoint);
    }
}

