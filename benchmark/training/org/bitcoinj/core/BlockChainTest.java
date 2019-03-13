/**
 * Copyright 2011 Google Inc.
 * Copyright 2014 Andreas Schildbach
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
package org.bitcoinj.core;


import BalanceType.AVAILABLE;
import BalanceType.ESTIMATED;
import Block.BLOCK_VERSION_BIP34;
import Block.BLOCK_VERSION_BIP65;
import Block.BLOCK_VERSION_BIP66;
import Block.BLOCK_VERSION_GENESIS;
import Block.EASIEST_DIFFICULTY_TARGET;
import Coin.ZERO;
import com.google.common.util.concurrent.ListenableFuture;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import org.bitcoinj.params.MainNetParams;
import org.bitcoinj.params.TestNet3Params;
import org.bitcoinj.params.UnitTestParams;
import org.bitcoinj.store.BlockStore;
import org.bitcoinj.testing.FakeTxBuilder;
import org.bitcoinj.wallet.Wallet;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static AbstractBlockChain.FP_ESTIMATOR_ALPHA;
import static Block.BLOCK_VERSION_GENESIS;


// Handling of chain splits/reorgs are in ChainSplitTests.
public class BlockChainTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private BlockChain testNetChain;

    private Wallet wallet;

    private BlockChain chain;

    private BlockStore blockStore;

    private Address coinbaseTo;

    private final StoredBlock[] block = new StoredBlock[1];

    private Transaction coinbaseTransaction;

    private static class TweakableTestNet3Params extends TestNet3Params {
        public void setMaxTarget(BigInteger limit) {
            maxTarget = limit;
        }
    }

    private static final BlockChainTest.TweakableTestNet3Params TESTNET = new BlockChainTest.TweakableTestNet3Params();

    private static final NetworkParameters UNITTEST = UnitTestParams.get();

    private static final NetworkParameters MAINNET = MainNetParams.get();

    @Test
    public void testBasicChaining() throws Exception {
        // Check that we can plug a few blocks together and the futures work.
        ListenableFuture<StoredBlock> future = testNetChain.getHeightFuture(2);
        // Block 1 from the testnet.
        Block b1 = BlockChainTest.getBlock1();
        Assert.assertTrue(testNetChain.add(b1));
        Assert.assertFalse(future.isDone());
        // Block 2 from the testnet.
        Block b2 = BlockChainTest.getBlock2();
        // Let's try adding an invalid block.
        long n = b2.getNonce();
        try {
            b2.setNonce(12345);
            testNetChain.add(b2);
            Assert.fail();
        } catch (VerificationException e) {
            b2.setNonce(n);
        }
        // Now it works because we reset the nonce.
        Assert.assertTrue(testNetChain.add(b2));
        Assert.assertTrue(future.isDone());
        Assert.assertEquals(2, future.get().getHeight());
    }

    @Test
    public void receiveCoins() throws Exception {
        int height = 1;
        // Quick check that we can actually receive coins.
        Transaction tx1 = FakeTxBuilder.createFakeTx(BlockChainTest.UNITTEST, COIN, LegacyAddress.fromKey(BlockChainTest.UNITTEST, wallet.currentReceiveKey()));
        Block b1 = FakeTxBuilder.createFakeBlock(blockStore, height, tx1).block;
        chain.add(b1);
        Assert.assertTrue(((wallet.getBalance().signum()) > 0));
    }

    @Test
    public void unconnectedBlocks() throws Exception {
        Block b1 = BlockChainTest.UNITTEST.getGenesisBlock().createNextBlock(coinbaseTo);
        Block b2 = b1.createNextBlock(coinbaseTo);
        Block b3 = b2.createNextBlock(coinbaseTo);
        // Connected.
        Assert.assertTrue(chain.add(b1));
        // Unconnected but stored. The head of the chain is still b1.
        Assert.assertFalse(chain.add(b3));
        Assert.assertEquals(chain.getChainHead().getHeader(), b1.cloneAsHeader());
        // Add in the middle block.
        Assert.assertTrue(chain.add(b2));
        Assert.assertEquals(chain.getChainHead().getHeader(), b3.cloneAsHeader());
    }

    @Test
    public void difficultyTransitions() throws Exception {
        // Add a bunch of blocks in a loop until we reach a difficulty transition point. The unit test params have an
        // artificially shortened period.
        Block prev = BlockChainTest.UNITTEST.getGenesisBlock();
        Utils.setMockClock(((System.currentTimeMillis()) / 1000));
        for (int height = 0; height < ((BlockChainTest.UNITTEST.getInterval()) - 1); height++) {
            Block newBlock = prev.createNextBlock(coinbaseTo, 1, Utils.currentTimeSeconds(), height);
            Assert.assertTrue(chain.add(newBlock));
            prev = newBlock;
            // The fake chain should seem to be "fast" for the purposes of difficulty calculations.
            Utils.rollMockClock(2);
        }
        // Now add another block that has no difficulty adjustment, it should be rejected.
        try {
            chain.add(prev.createNextBlock(coinbaseTo, 1, Utils.currentTimeSeconds(), BlockChainTest.UNITTEST.getInterval()));
            Assert.fail();
        } catch (VerificationException e) {
        }
        // Create a new block with the right difficulty target given our blistering speed relative to the huge amount
        // of time it's supposed to take (set in the unit test network parameters).
        Block b = prev.createNextBlock(coinbaseTo, 1, Utils.currentTimeSeconds(), ((BlockChainTest.UNITTEST.getInterval()) + 1));
        b.setDifficultyTarget(538968063L);
        b.solve();
        Assert.assertTrue(chain.add(b));
        // Successfully traversed a difficulty transition period.
    }

    @Test
    public void badDifficulty() throws Exception {
        Assert.assertTrue(testNetChain.add(BlockChainTest.getBlock1()));
        Block b2 = BlockChainTest.getBlock2();
        Assert.assertTrue(testNetChain.add(b2));
        Block bad = new Block(BlockChainTest.TESTNET, BLOCK_VERSION_GENESIS);
        // Merkle root can be anything here, doesn't matter.
        bad.setMerkleRoot(Sha256Hash.wrap("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"));
        // Nonce was just some number that made the hash < difficulty limit set below, it can be anything.
        bad.setNonce(140548933);
        bad.setTime(1279242649);
        bad.setPrevBlockHash(b2.getHash());
        // We're going to make this block so easy 50% of solutions will pass, and check it gets rejected for having a
        // bad difficulty target. Unfortunately the encoding mechanism means we cannot make one that accepts all
        // solutions.
        bad.setDifficultyTarget(EASIEST_DIFFICULTY_TARGET);
        try {
            testNetChain.add(bad);
            // The difficulty target above should be rejected on the grounds of being easier than the networks
            // allowable difficulty.
            Assert.fail();
        } catch (VerificationException e) {
            Assert.assertTrue(e.getMessage(), e.getCause().getMessage().contains("Difficulty target is bad"));
        }
        // Accept any level of difficulty now.
        BigInteger oldVal = getMaxTarget();
        BlockChainTest.TESTNET.setMaxTarget(new BigInteger("00ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff", 16));
        try {
            testNetChain.add(bad);
            // We should not get here as the difficulty target should not be changing at this point.
            Assert.fail();
        } catch (VerificationException e) {
            Assert.assertTrue(e.getMessage(), e.getCause().getMessage().contains("Unexpected change in difficulty"));
        }
        BlockChainTest.TESTNET.setMaxTarget(oldVal);
        // TODO: Test difficulty change is not out of range when a transition period becomes valid.
    }

    /**
     * Test that version 2 blocks are rejected once version 3 blocks are a super
     * majority.
     */
    @Test
    public void badBip66Version() throws Exception {
        testDeprecatedBlockVersion(BLOCK_VERSION_BIP34, BLOCK_VERSION_BIP66);
    }

    /**
     * Test that version 3 blocks are rejected once version 4 blocks are a super
     * majority.
     */
    @Test
    public void badBip65Version() throws Exception {
        testDeprecatedBlockVersion(BLOCK_VERSION_BIP66, BLOCK_VERSION_BIP65);
    }

    @Test
    public void duplicates() throws Exception {
        // Adding a block twice should not have any effect, in particular it should not send the block to the wallet.
        Block b1 = BlockChainTest.UNITTEST.getGenesisBlock().createNextBlock(coinbaseTo);
        Block b2 = b1.createNextBlock(coinbaseTo);
        Block b3 = b2.createNextBlock(coinbaseTo);
        Assert.assertTrue(chain.add(b1));
        Assert.assertEquals(b1, block[0].getHeader());
        Assert.assertTrue(chain.add(b2));
        Assert.assertEquals(b2, block[0].getHeader());
        Assert.assertTrue(chain.add(b3));
        Assert.assertEquals(b3, block[0].getHeader());
        Assert.assertEquals(b3, chain.getChainHead().getHeader());
        Assert.assertTrue(chain.add(b2));
        Assert.assertEquals(b3, chain.getChainHead().getHeader());
        // Wallet was NOT called with the new block because the duplicate add was spotted.
        Assert.assertEquals(b3, block[0].getHeader());
    }

    @Test
    public void intraBlockDependencies() throws Exception {
        // Covers issue 166 in which transactions that depend on each other inside a block were not always being
        // considered relevant.
        Address somebodyElse = LegacyAddress.fromKey(BlockChainTest.UNITTEST, new ECKey());
        Block b1 = BlockChainTest.UNITTEST.getGenesisBlock().createNextBlock(somebodyElse);
        ECKey key = wallet.freshReceiveKey();
        Address addr = LegacyAddress.fromKey(BlockChainTest.UNITTEST, key);
        // Create a tx that gives us some coins, and another that spends it to someone else in the same block.
        Transaction t1 = FakeTxBuilder.createFakeTx(BlockChainTest.UNITTEST, COIN, addr);
        Transaction t2 = new Transaction(BlockChainTest.UNITTEST);
        t2.addInput(t1.getOutputs().get(0));
        t2.addOutput(valueOf(2, 0), somebodyElse);
        b1.addTransaction(t1);
        b1.addTransaction(t2);
        b1.solve();
        chain.add(b1);
        Assert.assertEquals(ZERO, wallet.getBalance());
    }

    @Test
    public void coinbaseTransactionAvailability() throws Exception {
        // Check that a coinbase transaction is only available to spend after NetworkParameters.getSpendableCoinbaseDepth() blocks.
        // Create a second wallet to receive the coinbase spend.
        Wallet wallet2 = new Wallet(BlockChainTest.UNITTEST);
        ECKey receiveKey = wallet2.freshReceiveKey();
        int height = 1;
        chain.addWallet(wallet2);
        Address addressToSendTo = LegacyAddress.fromKey(BlockChainTest.UNITTEST, receiveKey);
        // Create a block, sending the coinbase to the coinbaseTo address (which is in the wallet).
        Block b1 = BlockChainTest.UNITTEST.getGenesisBlock().createNextBlockWithCoinbase(BLOCK_VERSION_GENESIS, wallet.currentReceiveKey().getPubKey(), (height++));
        chain.add(b1);
        // Check a transaction has been received.
        Assert.assertNotNull(coinbaseTransaction);
        // The coinbase tx is not yet available to spend.
        Assert.assertEquals(ZERO, wallet.getBalance());
        Assert.assertEquals(wallet.getBalance(ESTIMATED), FIFTY_COINS);
        Assert.assertTrue((!(coinbaseTransaction.isMature())));
        // Attempt to spend the coinbase - this should fail as the coinbase is not mature yet.
        try {
            wallet.createSend(addressToSendTo, valueOf(49, 0));
            Assert.fail();
        } catch (InsufficientMoneyException e) {
        }
        // Check that the coinbase is unavailable to spend for the next spendableCoinbaseDepth - 2 blocks.
        for (int i = 0; i < ((BlockChainTest.UNITTEST.getSpendableCoinbaseDepth()) - 2); i++) {
            // Non relevant tx - just for fake block creation.
            Transaction tx2 = FakeTxBuilder.createFakeTx(BlockChainTest.UNITTEST, COIN, LegacyAddress.fromKey(BlockChainTest.UNITTEST, new ECKey()));
            Block b2 = FakeTxBuilder.createFakeBlock(blockStore, (height++), tx2).block;
            chain.add(b2);
            // Wallet still does not have the coinbase transaction available for spend.
            Assert.assertEquals(ZERO, wallet.getBalance());
            Assert.assertEquals(wallet.getBalance(ESTIMATED), FIFTY_COINS);
            // The coinbase transaction is still not mature.
            Assert.assertTrue((!(coinbaseTransaction.isMature())));
            // Attempt to spend the coinbase - this should fail.
            try {
                wallet.createSend(addressToSendTo, valueOf(49, 0));
                Assert.fail();
            } catch (InsufficientMoneyException e) {
            }
        }
        // Give it one more block - should now be able to spend coinbase transaction. Non relevant tx.
        Transaction tx3 = FakeTxBuilder.createFakeTx(BlockChainTest.UNITTEST, COIN, LegacyAddress.fromKey(BlockChainTest.UNITTEST, new ECKey()));
        Block b3 = FakeTxBuilder.createFakeBlock(blockStore, (height++), tx3).block;
        chain.add(b3);
        // Wallet now has the coinbase transaction available for spend.
        Assert.assertEquals(wallet.getBalance(), FIFTY_COINS);
        Assert.assertEquals(wallet.getBalance(ESTIMATED), FIFTY_COINS);
        Assert.assertTrue(coinbaseTransaction.isMature());
        // Create a spend with the coinbase BTC to the address in the second wallet - this should now succeed.
        Transaction coinbaseSend2 = wallet.createSend(addressToSendTo, valueOf(49, 0));
        Assert.assertNotNull(coinbaseSend2);
        // Commit the coinbaseSpend to the first wallet and check the balances decrement.
        wallet.commitTx(coinbaseSend2);
        Assert.assertEquals(wallet.getBalance(ESTIMATED), COIN);
        // Available balance is zero as change has not been received from a block yet.
        Assert.assertEquals(wallet.getBalance(AVAILABLE), ZERO);
        // Give it one more block - change from coinbaseSpend should now be available in the first wallet.
        Block b4 = FakeTxBuilder.createFakeBlock(blockStore, (height++), coinbaseSend2).block;
        chain.add(b4);
        Assert.assertEquals(wallet.getBalance(AVAILABLE), COIN);
        // Check the balances in the second wallet.
        Assert.assertEquals(wallet2.getBalance(ESTIMATED), valueOf(49, 0));
        Assert.assertEquals(wallet2.getBalance(AVAILABLE), valueOf(49, 0));
    }

    @Test
    public void estimatedBlockTime() throws Exception {
        BlockChain prod = new BlockChain(new Context(BlockChainTest.MAINNET), new org.bitcoinj.store.MemoryBlockStore(BlockChainTest.MAINNET));
        Date d = prod.estimateBlockTime(200000);
        // The actual date of block 200,000 was 2012-09-22 10:47:00
        Assert.assertEquals(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.US).parse("2012-10-23T08:35:05.000-0700"), d);
    }

    @Test
    public void falsePositives() throws Exception {
        double decay = FP_ESTIMATOR_ALPHA;
        Assert.assertTrue((0 == (chain.getFalsePositiveRate())));// Exactly

        chain.trackFalsePositives(55);
        Assert.assertEquals((decay * 55), chain.getFalsePositiveRate(), 1.0E-4);
        chain.trackFilteredTransactions(550);
        double rate1 = chain.getFalsePositiveRate();
        // Run this scenario a few more time for the filter to converge
        for (int i = 1; i < 10; i++) {
            chain.trackFalsePositives(55);
            chain.trackFilteredTransactions(550);
        }
        // Ensure we are within 10%
        Assert.assertEquals(0.1, chain.getFalsePositiveRate(), 0.01);
        // Check that we get repeatable results after a reset
        chain.resetFalsePositiveEstimate();
        Assert.assertTrue((0 == (chain.getFalsePositiveRate())));// Exactly

        chain.trackFalsePositives(55);
        Assert.assertEquals((decay * 55), chain.getFalsePositiveRate(), 1.0E-4);
        chain.trackFilteredTransactions(550);
        Assert.assertEquals(rate1, chain.getFalsePositiveRate(), 1.0E-4);
    }

    @Test
    public void rollbackBlockStore() throws Exception {
        // This test simulates an issue on Android, that causes the VM to crash while receiving a block, so that the
        // block store is persisted but the wallet is not.
        Block b1 = BlockChainTest.UNITTEST.getGenesisBlock().createNextBlock(coinbaseTo);
        Block b2 = b1.createNextBlock(coinbaseTo);
        // Add block 1, no frills.
        Assert.assertTrue(chain.add(b1));
        Assert.assertEquals(b1.cloneAsHeader(), chain.getChainHead().getHeader());
        Assert.assertEquals(1, chain.getBestChainHeight());
        Assert.assertEquals(1, wallet.getLastBlockSeenHeight());
        // Add block 2 while wallet is disconnected, to simulate crash.
        chain.removeWallet(wallet);
        Assert.assertTrue(chain.add(b2));
        Assert.assertEquals(b2.cloneAsHeader(), chain.getChainHead().getHeader());
        Assert.assertEquals(2, chain.getBestChainHeight());
        Assert.assertEquals(1, wallet.getLastBlockSeenHeight());
        // Add wallet back. This will detect the height mismatch and repair the damage done.
        chain.addWallet(wallet);
        Assert.assertEquals(b1.cloneAsHeader(), chain.getChainHead().getHeader());
        Assert.assertEquals(1, chain.getBestChainHeight());
        Assert.assertEquals(1, wallet.getLastBlockSeenHeight());
        // Now add block 2 correctly.
        Assert.assertTrue(chain.add(b2));
        Assert.assertEquals(b2.cloneAsHeader(), chain.getChainHead().getHeader());
        Assert.assertEquals(2, chain.getBestChainHeight());
        Assert.assertEquals(2, wallet.getLastBlockSeenHeight());
    }
}

