package com.iota.iri.controllers;


import com.iota.iri.TransactionTestUtils;
import com.iota.iri.model.Hash;
import java.util.concurrent.ExecutionException;
import org.junit.Assert;
import org.junit.Test;

import static TipsViewModel.MAX_TIPS;


public class TipsViewModelTest {
    @Test
    public void nonsolidCapacityLimited() throws InterruptedException, ExecutionException {
        TipsViewModel tipsVM = new TipsViewModel();
        int capacity = MAX_TIPS;
        // fill tips list
        for (int i = 0; i < (capacity * 2); i++) {
            Hash hash = TransactionTestUtils.getRandomTransactionHash();
            tipsVM.addTipHash(hash);
        }
        // check that limit wasn't breached
        Assert.assertEquals(capacity, tipsVM.nonSolidSize());
    }

    @Test
    public void solidCapacityLimited() throws InterruptedException, ExecutionException {
        TipsViewModel tipsVM = new TipsViewModel();
        int capacity = MAX_TIPS;
        // fill tips list
        for (int i = 0; i < (capacity * 2); i++) {
            Hash hash = TransactionTestUtils.getRandomTransactionHash();
            tipsVM.addTipHash(hash);
            tipsVM.setSolid(hash);
        }
        // check that limit wasn't breached
        Assert.assertEquals(capacity, tipsVM.size());
    }

    @Test
    public void totalCapacityLimited() throws InterruptedException, ExecutionException {
        TipsViewModel tipsVM = new TipsViewModel();
        int capacity = MAX_TIPS;
        // fill tips list
        for (int i = 0; i <= (capacity * 4); i++) {
            Hash hash = TransactionTestUtils.getRandomTransactionHash();
            tipsVM.addTipHash(hash);
            if ((i % 2) == 1) {
                tipsVM.setSolid(hash);
            }
        }
        // check that limit wasn't breached
        Assert.assertEquals((capacity * 2), tipsVM.size());
    }
}

