package com.iota.iri.controllers;


import HashFactory.TRANSACTION;
import com.iota.iri.conf.MainnetConfig;
import com.iota.iri.model.Hash;
import com.iota.iri.storage.Tangle;
import org.junit.Assert;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;


public class MilestoneViewModelTest {
    final TemporaryFolder dbFolder = new TemporaryFolder();

    final TemporaryFolder logFolder = new TemporaryFolder();

    private static Tangle tangle = new Tangle();

    int index = 30;

    @Test
    public void getMilestone() throws Exception {
        Hash milestoneHash = TRANSACTION.create("ABCDEFGHIJKLMNOPQRSTUVWXYZ9ABCDEFGHIJKLMNOPQRSTUVWXYZ9ABCDEFGHIJKLMNOPQRSTUV99999");
        MilestoneViewModel milestoneViewModel = new MilestoneViewModel((++(index)), milestoneHash);
        Assert.assertTrue(milestoneViewModel.store(MilestoneViewModelTest.tangle));
        MilestoneViewModel.clear();
        MilestoneViewModel.load(MilestoneViewModelTest.tangle, index);
        Assert.assertEquals(MilestoneViewModel.get(MilestoneViewModelTest.tangle, index).getHash(), milestoneHash);
    }

    @Test
    public void store() throws Exception {
        MilestoneViewModel milestoneViewModel = new MilestoneViewModel((++(index)), Hash.NULL_HASH);
        Assert.assertTrue(milestoneViewModel.store(MilestoneViewModelTest.tangle));
    }

    @Test
    public void snapshot() throws Exception {
        Hash milestoneHash = TRANSACTION.create("BBCDEFGHIJKLMNOPQRSTUVWXYZ9ABCDEFGHIJKLMNOPQRSTUVWXYZ9ABCDEFGHIJKLMNOPQRSTUV99999");
        long value = 3;
        MilestoneViewModel milestoneViewModel = new MilestoneViewModel((++(index)), milestoneHash);
    }

    @Test
    public void initSnapshot() throws Exception {
        MilestoneViewModel milestoneViewModel = new MilestoneViewModel((++(index)), Hash.NULL_HASH);
    }

    @Test
    public void updateSnapshot() throws Exception {
        Hash milestoneHash = TRANSACTION.create("CBCDEFGHIJKLMNOPQRSTUVWXYZ9ABCDEFGHIJKLMNOPQRSTUVWXYZ9ABCDEFGHIJKLMNOPQRSTUV99999");
        MilestoneViewModel milestoneViewModel = new MilestoneViewModel((++(index)), milestoneHash);
        Assert.assertTrue(milestoneViewModel.store(MilestoneViewModelTest.tangle));
        MilestoneViewModel.clear();
        Assert.assertEquals(MilestoneViewModel.get(MilestoneViewModelTest.tangle, index).getHash(), milestoneHash);
    }

    @Test
    public void getHash() throws Exception {
        Hash milestoneHash = TRANSACTION.create("DBCDEFGHIJKLMNOPQRSTUVWXYZ9ABCDEFGHIJKLMNOPQRSTUVWXYZ9ABCDEFGHIJKLMNOPQRSTUV99999");
        MilestoneViewModel milestoneViewModel = new MilestoneViewModel((++(index)), milestoneHash);
        Assert.assertEquals(milestoneHash, milestoneViewModel.getHash());
    }

    @Test
    public void index() throws Exception {
        Hash milestoneHash = TRANSACTION.create("EBCDEFGHIJKLMNOPQRSTUVWXYZ9ABCDEFGHIJKLMNOPQRSTUVWXYZ9ABCDEFGHIJKLMNOPQRSTUV99999");
        MilestoneViewModel milestoneViewModel = new MilestoneViewModel((++(index)), milestoneHash);
        Assert.assertTrue(((index) == (milestoneViewModel.index())));
    }

    @Test
    public void latest() throws Exception {
        int top = 100;
        Hash milestoneHash = TRANSACTION.create("ZBCDEFGHIJKLMNOPQRSTUVWXYZ9ABCDEFGHIJKLMNOPQRSTUVWXYZ9ABCDEFGHIJKLMNOPQRSTUV99999");
        MilestoneViewModel milestoneViewModel = new MilestoneViewModel(top, milestoneHash);
        milestoneViewModel.store(MilestoneViewModelTest.tangle);
        Assert.assertTrue((top == (MilestoneViewModel.latest(MilestoneViewModelTest.tangle).index())));
    }

    @Test
    public void first() throws Exception {
        int first = 1;
        Hash milestoneHash = TRANSACTION.create("99CDEFGHIJKLMNOPQRSTUVWXYZ9ABCDEFGHIJKLMNOPQRSTUVWXYZ9ABCDEFGHIJKLMNOPQRSTUV99999");
        MilestoneViewModel milestoneViewModel = new MilestoneViewModel(first, milestoneHash);
        milestoneViewModel.store(MilestoneViewModelTest.tangle);
        Assert.assertTrue((first == (MilestoneViewModel.first(MilestoneViewModelTest.tangle).index())));
    }

    @Test
    public void next() throws Exception {
        int first = 1;
        int next = 2;
        MilestoneViewModel firstMilestone = new MilestoneViewModel(first, TRANSACTION.create("99CDEFGHIJKLMNOPQRSTUVWXYZ9ABCDEFGHIJKLMNOPQRSTUVWXYZ9ABCDEFGHIJKLMNOPQRSTUV99999"));
        firstMilestone.store(MilestoneViewModelTest.tangle);
        new MilestoneViewModel(next, TRANSACTION.create("9ACDEFGHIJKLMNOPQRSTUVWXYZ9ABCDEFGHIJKLMNOPQRSTUVWXYZ9ABCDEFGHIJKLMNOPQRSTUV99999")).store(MilestoneViewModelTest.tangle);
        Assert.assertTrue((next == (MilestoneViewModel.first(MilestoneViewModelTest.tangle).next(MilestoneViewModelTest.tangle).index())));
    }

    @Test
    public void previous() throws Exception {
        int first = 1;
        int next = 2;
        MilestoneViewModel nextMilestone = new MilestoneViewModel(next, TRANSACTION.create("99CDEFGHIJKLMNOPQRSTUVWXYZ9ABCDEFGHIJKLMNOPQRSTUVWXYZ9ABCDEFGHIJKLMNOPQRSTUV99999"));
        nextMilestone.store(MilestoneViewModelTest.tangle);
        new MilestoneViewModel(first, TRANSACTION.create("9ACDEFGHIJKLMNOPQRSTUVWXYZ9ABCDEFGHIJKLMNOPQRSTUVWXYZ9ABCDEFGHIJKLMNOPQRSTUV99999")).store(MilestoneViewModelTest.tangle);
        Assert.assertTrue((first == (nextMilestone.previous(MilestoneViewModelTest.tangle).index())));
    }

    @Test
    public void latestSnapshot() throws Exception {
        int nosnapshot = 90;
        int topSnapshot = 80;
        int mid = 50;
        new MilestoneViewModel(nosnapshot, TRANSACTION.create("FBCDEFGHIJKLMNOPQRSTUVWXYZ9ABCDEFGHIJKLMNOPQRSTUVWXYZ9ABCDEFGHIJKLMNOPQRSTUV99999")).store(MilestoneViewModelTest.tangle);
        MilestoneViewModel milestoneViewModelmid = new MilestoneViewModel(mid, TRANSACTION.create("GBCDEFGHIJKLMNOPQRSTUVWXYZ9ABCDEFGHIJKLMNOPQRSTUVWXYZ9ABCDEFGHIJKLMNOPQRSTUV99999"));
        milestoneViewModelmid.store(MilestoneViewModelTest.tangle);
        MilestoneViewModel milestoneViewModeltopSnapshot = new MilestoneViewModel(topSnapshot, TRANSACTION.create("GBCDEFGHIJKLMNOPQRSTUVWXYZ9ABCDEFGHIJKLMNOPQRSTUVWXYZ9ABCDEFGHIJKLMNOPQRSTUV99999"));
        milestoneViewModeltopSnapshot.store(MilestoneViewModelTest.tangle);
        // assertTrue(topSnapshot == MilestoneViewModel.latestWithSnapshot().index());
    }

    @Test
    public void firstWithSnapshot() throws Exception {
        int first = 5;
        int firstSnapshot = 6;
        int next = 7;
        new MilestoneViewModel(first, TRANSACTION.create("FBCDEFGHIJ9LMNOPQRSTUVWXYZ9ABCDEFGHIJKLMNOPQRSTUVWXYZ9ABCDEFGHIJKLMNOPQRSTUV99999")).store(MilestoneViewModelTest.tangle);
        MilestoneViewModel milestoneViewModelmid = new MilestoneViewModel(next, TRANSACTION.create("GBCDE9GHIJKLMNOPQRSTUVWXYZ9ABCDEFGHIJKLMNOPQRSTUVWXYZ9ABCDEFGHIJKLMNOPQRSTUV99999"));
        milestoneViewModelmid.store(MilestoneViewModelTest.tangle);
        MilestoneViewModel milestoneViewModeltopSnapshot = new MilestoneViewModel(firstSnapshot, TRANSACTION.create("GBCDEFGHIJKLMNOPQRSTUVWXYZ9ABCDEFGHIJKLMNOPQRSTUVWXYA9ABCDEFGHIJKLMNOPQRSTUV99999"));
        milestoneViewModeltopSnapshot.store(MilestoneViewModelTest.tangle);
        // assertTrue(firstSnapshot == MilestoneViewModel.firstWithSnapshot().index());
    }

    @Test
    public void nextWithSnapshot() throws Exception {
        int firstSnapshot = 8;
        int next = 9;
        MilestoneViewModel milestoneViewModelmid = new MilestoneViewModel(next, TRANSACTION.create("GBCDEBGHIJKLMNOPQRSTUVWXYZ9ABCDEFGHIJKLMNOPQRSTUVWXYZ9ABCDEFGHIJKLMNOPQRSTUV99999"));
        milestoneViewModelmid.store(MilestoneViewModelTest.tangle);
        MilestoneViewModel milestoneViewModel = new MilestoneViewModel(firstSnapshot, TRANSACTION.create("GBCDEFGHIJKLMNODQRSTUVWXYZ9ABCDEFGHIJKLMNOPQRSTUVWXYZ9ABCDEFGHIJKLMNOPQRSTUV99999"));
        milestoneViewModel.store(MilestoneViewModelTest.tangle);
        // assertTrue(next == milestoneViewModel.nextWithSnapshot().index());
    }

    @Test
    public void nextGreaterThan() throws Exception {
        int milestoneStartIndex = new MainnetConfig().getMilestoneStartIndex();
        int first = milestoneStartIndex + 1;
        int next = first + 1;
        new MilestoneViewModel(next, TRANSACTION.create("GBCDEBGHIJKLMNOPQRSTUVWXYZ9ABCDEFGHIJKLMNOPQRSTUVWXYZ9ABCDEFGHIJKLMNOPQRSTUV99999")).store(MilestoneViewModelTest.tangle);
        new MilestoneViewModel(first, TRANSACTION.create("GBCDEFGHIJKLMNODQRSTUVWXYZ9ABCDEFGHIJKLMNOPQRSTUVWXYZ9ABCDEFGHIJKLMNOPQRSTUV99999")).store(MilestoneViewModelTest.tangle);
        Assert.assertEquals("the found milestone should be following the previous one", next, MilestoneViewModel.findClosestNextMilestone(MilestoneViewModelTest.tangle, first, next).index().intValue());
    }

    @Test
    public void PrevBefore() throws Exception {
        int first = 8;
        int next = 9;
        new MilestoneViewModel(next, TRANSACTION.create("GBCDEBGHIJKLMNOPQRSTUVWXYZ9ABCDEFGHIJKLMNOPQRSTUVWXYZ9ABCDEFGHIJKLMNOPQRSTUV99999")).store(MilestoneViewModelTest.tangle);
        new MilestoneViewModel(first, TRANSACTION.create("GBCDEFGHIJKLMNODQRSTUVWXYZ9ABCDEFGHIJKLMNOPQRSTUVWXYZ9ABCDEFGHIJKLMNOPQRSTUV99999")).store(MilestoneViewModelTest.tangle);
        Assert.assertEquals(first, MilestoneViewModel.findClosestPrevMilestone(MilestoneViewModelTest.tangle, next, first).index().intValue());
    }
}

