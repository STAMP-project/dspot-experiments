/**
 * Copyright 2013 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.bitcoinj.wallet;


import AbstractBlockChain.NewBlockType.BEST_CHAIN;
import Block.BLOCK_HEIGHT_GENESIS;
import TransactionConfidence.ConfidenceType.BUILDING;
import TransactionConfidence.ConfidenceType.PENDING;
import TransactionConfidence.Source.SELF;
import com.google.common.base.Preconditions;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.bitcoinj.core.Coin;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.testing.FakeTxBuilder;
import org.bitcoinj.testing.TestWithWallet;
import org.junit.Assert;
import org.junit.Test;


public class DefaultCoinSelectorTest extends TestWithWallet {
    private static final NetworkParameters UNITTEST = UnitTestParams.get();

    private static final NetworkParameters REGTEST = RegTestParams.get();

    @Test
    public void selectable() throws Exception {
        Transaction t;
        t = new Transaction(DefaultCoinSelectorTest.UNITTEST);
        t.getConfidence().setConfidenceType(PENDING);
        Assert.assertFalse(DefaultCoinSelector.isSelectable(t));
        t.getConfidence().setSource(SELF);
        Assert.assertFalse(DefaultCoinSelector.isSelectable(t));
        t.getConfidence().markBroadcastBy(new PeerAddress(DefaultCoinSelectorTest.UNITTEST, InetAddress.getByName("1.2.3.4")));
        Assert.assertFalse(DefaultCoinSelector.isSelectable(t));
        t.getConfidence().markBroadcastBy(new PeerAddress(DefaultCoinSelectorTest.UNITTEST, InetAddress.getByName("5.6.7.8")));
        Assert.assertTrue(DefaultCoinSelector.isSelectable(t));
        t = new Transaction(DefaultCoinSelectorTest.UNITTEST);
        t.getConfidence().setConfidenceType(BUILDING);
        Assert.assertTrue(DefaultCoinSelector.isSelectable(t));
        t = new Transaction(DefaultCoinSelectorTest.REGTEST);
        t.getConfidence().setConfidenceType(PENDING);
        t.getConfidence().setSource(SELF);
        Assert.assertTrue(DefaultCoinSelector.isSelectable(t));
    }

    @Test
    public void depthOrdering() throws Exception {
        // Send two transactions in two blocks on top of each other.
        Transaction t1 = Preconditions.checkNotNull(sendMoneyToWallet(BEST_CHAIN, COIN));
        Transaction t2 = Preconditions.checkNotNull(sendMoneyToWallet(BEST_CHAIN, COIN));
        // Check we selected just the oldest one.
        DefaultCoinSelector selector = new DefaultCoinSelector();
        CoinSelection selection = selector.select(COIN, wallet.calculateAllSpendCandidates());
        Assert.assertTrue(selection.gathered.contains(t1.getOutputs().get(0)));
        Assert.assertEquals(COIN, selection.valueGathered);
        // Check we ordered them correctly (by depth).
        ArrayList<TransactionOutput> candidates = new ArrayList<>();
        candidates.add(t2.getOutput(0));
        candidates.add(t1.getOutput(0));
        DefaultCoinSelector.sortOutputs(candidates);
        Assert.assertEquals(t1.getOutput(0), candidates.get(0));
        Assert.assertEquals(t2.getOutput(0), candidates.get(1));
    }

    @Test
    public void coinAgeOrdering() throws Exception {
        // Send three transactions in four blocks on top of each other. Coin age of t1 is 1*4=4, coin age of t2 = 2*2=4
        // and t3=0.01.
        Transaction t1 = Preconditions.checkNotNull(sendMoneyToWallet(BEST_CHAIN, COIN));
        // Padding block.
        wallet.notifyNewBestBlock(FakeTxBuilder.createFakeBlock(blockStore, BLOCK_HEIGHT_GENESIS).storedBlock);
        final Coin.Coin TWO_COINS = COIN.multiply(2);
        Transaction t2 = Preconditions.checkNotNull(sendMoneyToWallet(BEST_CHAIN, TWO_COINS));
        Transaction t3 = Preconditions.checkNotNull(sendMoneyToWallet(BEST_CHAIN, CENT));
        // Should be ordered t2, t1, t3.
        ArrayList<TransactionOutput> candidates = new ArrayList<>();
        candidates.add(t3.getOutput(0));
        candidates.add(t2.getOutput(0));
        candidates.add(t1.getOutput(0));
        DefaultCoinSelector.sortOutputs(candidates);
        Assert.assertEquals(t2.getOutput(0), candidates.get(0));
        Assert.assertEquals(t1.getOutput(0), candidates.get(1));
        Assert.assertEquals(t3.getOutput(0), candidates.get(2));
    }

    @Test
    public void identicalInputs() throws Exception {
        // Add four outputs to a transaction with same value and destination. Select them all.
        Transaction t = new Transaction(DefaultCoinSelectorTest.UNITTEST);
        List<TransactionOutput> outputs = Arrays.asList(new TransactionOutput(DefaultCoinSelectorTest.UNITTEST, t, Coin.Coin.valueOf(30302787), myAddress), new TransactionOutput(DefaultCoinSelectorTest.UNITTEST, t, Coin.Coin.valueOf(30302787), myAddress), new TransactionOutput(DefaultCoinSelectorTest.UNITTEST, t, Coin.Coin.valueOf(30302787), myAddress), new TransactionOutput(DefaultCoinSelectorTest.UNITTEST, t, Coin.Coin.valueOf(30302787), myAddress));
        t.getConfidence().setConfidenceType(BUILDING);
        DefaultCoinSelector selector = new DefaultCoinSelector();
        CoinSelection selection = selector.select(COIN.multiply(2), outputs);
        Assert.assertTrue(((selection.gathered.size()) == 4));
    }
}

