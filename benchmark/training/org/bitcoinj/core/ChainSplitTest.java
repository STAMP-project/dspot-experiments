/**
 * Copyright 2012 Google Inc.
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


import Block.BLOCK_VERSION_GENESIS;
import Coin.ZERO;
import ConfidenceType.BUILDING;
import ConfidenceType.DEAD;
import ConfidenceType.PENDING;
import Threading.SAME_THREAD;
import Wallet.BalanceType.ESTIMATED;
import WalletTransaction.Pool.SPENT;
import WalletTransaction.Pool.UNSPENT;
import com.google.common.base.Preconditions;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.math.BigInteger;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import org.bitcoinj.core.TransactionConfidence.ConfidenceType;
import org.bitcoinj.core.listeners.TransactionConfidenceEventListener;
import org.bitcoinj.params.UnitTestParams;
import org.bitcoinj.testing.FakeTxBuilder;
import org.bitcoinj.utils.Threading;
import org.bitcoinj.wallet.Wallet;
import org.bitcoinj.wallet.listeners.WalletChangeEventListener;
import org.bitcoinj.wallet.listeners.WalletCoinsReceivedEventListener;
import org.bitcoinj.wallet.listeners.WalletReorganizeEventListener;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ChainSplitTest {
    private static final Logger log = LoggerFactory.getLogger(ChainSplitTest.class);

    private static final NetworkParameters UNITTEST = UnitTestParams.get();

    private Wallet wallet;

    private BlockChain chain;

    private Address coinsTo;

    private Address coinsTo2;

    private Address someOtherGuy;

    @Test
    public void testForking1() throws Exception {
        // Check that if the block chain forks, we end up using the right chain. Only tests inbound transactions
        // (receiving coins). Checking that we understand reversed spends is in testForking2.
        final AtomicBoolean reorgHappened = new AtomicBoolean();
        final AtomicInteger walletChanged = new AtomicInteger();
        wallet.addReorganizeEventListener(new WalletReorganizeEventListener() {
            @Override
            public void onReorganize(Wallet wallet) {
                reorgHappened.set(true);
            }
        });
        wallet.addChangeEventListener(new WalletChangeEventListener() {
            @Override
            public void onWalletChanged(Wallet wallet) {
                walletChanged.incrementAndGet();
            }
        });
        // Start by building a couple of blocks on top of the genesis block.
        Block b1 = ChainSplitTest.UNITTEST.getGenesisBlock().createNextBlock(coinsTo);
        Block b2 = b1.createNextBlock(coinsTo);
        Assert.assertTrue(chain.add(b1));
        Assert.assertTrue(chain.add(b2));
        Threading.waitForUserCode();
        Assert.assertFalse(reorgHappened.get());
        Assert.assertEquals(2, walletChanged.get());
        // We got two blocks which sent 50 coins each to us.
        Assert.assertEquals(Coin.Coin.valueOf(100, 0), wallet.getBalance());
        // We now have the following chain:
        // genesis -> b1 -> b2
        // 
        // so fork like this:
        // 
        // genesis -> b1 -> b2
        // \-> b3
        // 
        // Nothing should happen at this point. We saw b2 first so it takes priority.
        Block b3 = b1.createNextBlock(someOtherGuy);
        Assert.assertTrue(chain.add(b3));
        Threading.waitForUserCode();
        Assert.assertFalse(reorgHappened.get());// No re-org took place.

        Assert.assertEquals(2, walletChanged.get());
        Assert.assertEquals(Coin.Coin.valueOf(100, 0), wallet.getBalance());
        // Check we can handle multi-way splits: this is almost certainly going to be extremely rare, but we have to
        // handle it anyway. The same transaction appears in b7/b8 (side chain) but not b2 or b3.
        // genesis -> b1--> b2
        // |-> b3
        // |-> b7 (x)
        // \-> b8 (x)
        Block b7 = b1.createNextBlock(coinsTo);
        Assert.assertTrue(chain.add(b7));
        Block b8 = b1.createNextBlock(coinsTo);
        final Transaction t = b7.getTransactions().get(1);
        final Sha256Hash tHash = t.getTxId();
        b8.addTransaction(t);
        b8.solve();
        Assert.assertTrue(chain.add(roundtrip(b8)));
        Threading.waitForUserCode();
        Assert.assertEquals(2, wallet.getTransaction(tHash).getAppearsInHashes().size());
        Assert.assertFalse(reorgHappened.get());// No re-org took place.

        Assert.assertEquals(5, walletChanged.get());
        Assert.assertEquals(Coin.Coin.valueOf(100, 0), wallet.getBalance());
        // Now we add another block to make the alternative chain longer.
        Assert.assertTrue(chain.add(b3.createNextBlock(someOtherGuy)));
        Threading.waitForUserCode();
        Assert.assertTrue(reorgHappened.get());// Re-org took place.

        Assert.assertEquals(6, walletChanged.get());
        reorgHappened.set(false);
        // 
        // genesis -> b1 -> b2
        // \-> b3 -> b4
        // We lost some coins! b2 is no longer a part of the best chain so our available balance should drop to 50.
        // It's now pending reconfirmation.
        Assert.assertEquals(FIFTY_COINS, wallet.getBalance());
        // ... and back to the first chain.
        Block b5 = b2.createNextBlock(coinsTo);
        Block b6 = b5.createNextBlock(coinsTo);
        Assert.assertTrue(chain.add(b5));
        Assert.assertTrue(chain.add(b6));
        // 
        // genesis -> b1 -> b2 -> b5 -> b6
        // \-> b3 -> b4
        // 
        Threading.waitForUserCode();
        Assert.assertTrue(reorgHappened.get());
        Assert.assertEquals(9, walletChanged.get());
        Assert.assertEquals(Coin.Coin.valueOf(200, 0), wallet.getBalance());
    }

    @Test
    public void testForking2() throws Exception {
        // Check that if the chain forks and new coins are received in the alternate chain our balance goes up
        // after the re-org takes place.
        Block b1 = ChainSplitTest.UNITTEST.getGenesisBlock().createNextBlock(someOtherGuy);
        Block b2 = b1.createNextBlock(someOtherGuy);
        Assert.assertTrue(chain.add(b1));
        Assert.assertTrue(chain.add(b2));
        // genesis -> b1 -> b2
        // \-> b3 -> b4
        Assert.assertEquals(ZERO, wallet.getBalance());
        Block b3 = b1.createNextBlock(coinsTo);
        Block b4 = b3.createNextBlock(someOtherGuy);
        Assert.assertTrue(chain.add(b3));
        Assert.assertEquals(ZERO, wallet.getBalance());
        Assert.assertTrue(chain.add(b4));
        Assert.assertEquals(FIFTY_COINS, wallet.getBalance());
    }

    @Test
    public void testForking3() throws Exception {
        // Check that we can handle our own spends being rolled back by a fork.
        Block b1 = ChainSplitTest.UNITTEST.getGenesisBlock().createNextBlock(coinsTo);
        chain.add(b1);
        Assert.assertEquals(FIFTY_COINS, wallet.getBalance());
        Address dest = LegacyAddress.fromKey(ChainSplitTest.UNITTEST, new ECKey());
        Transaction spend = wallet.createSend(dest, valueOf(10, 0));
        wallet.commitTx(spend);
        // Waiting for confirmation ... make it eligible for selection.
        Assert.assertEquals(ZERO, wallet.getBalance());
        spend.getConfidence().markBroadcastBy(new PeerAddress(ChainSplitTest.UNITTEST, InetAddress.getByAddress(new byte[]{ 1, 2, 3, 4 })));
        spend.getConfidence().markBroadcastBy(new PeerAddress(ChainSplitTest.UNITTEST, InetAddress.getByAddress(new byte[]{ 5, 6, 7, 8 })));
        Assert.assertEquals(PENDING, spend.getConfidence().getConfidenceType());
        Assert.assertEquals(valueOf(40, 0), wallet.getBalance());
        Block b2 = b1.createNextBlock(someOtherGuy);
        b2.addTransaction(spend);
        b2.solve();
        chain.add(roundtrip(b2));
        // We have 40 coins in change.
        Assert.assertEquals(BUILDING, spend.getConfidence().getConfidenceType());
        // genesis -> b1 (receive coins) -> b2 (spend coins)
        // \-> b3 -> b4
        Block b3 = b1.createNextBlock(someOtherGuy);
        Block b4 = b3.createNextBlock(someOtherGuy);
        chain.add(b3);
        chain.add(b4);
        // b4 causes a re-org that should make our spend go pending again.
        Assert.assertEquals(valueOf(40, 0), wallet.getBalance(ESTIMATED));
        Assert.assertEquals(PENDING, spend.getConfidence().getConfidenceType());
    }

    @Test
    public void testForking4() throws Exception {
        // Check that we can handle external spends on an inactive chain becoming active. An external spend is where
        // we see a transaction that spends our own coins but we did not broadcast it ourselves. This happens when
        // keys are being shared between wallets.
        Block b1 = ChainSplitTest.UNITTEST.getGenesisBlock().createNextBlock(coinsTo);
        chain.add(b1);
        Assert.assertEquals(FIFTY_COINS, wallet.getBalance());
        Address dest = LegacyAddress.fromKey(ChainSplitTest.UNITTEST, new ECKey());
        Transaction spend = wallet.createSend(dest, FIFTY_COINS);
        // We do NOT confirm the spend here. That means it's not considered to be pending because createSend is
        // stateless. For our purposes it is as if some other program with our keys created the tx.
        // 
        // genesis -> b1 (receive 50) --> b2
        // \-> b3 (external spend) -> b4
        Block b2 = b1.createNextBlock(someOtherGuy);
        chain.add(b2);
        Block b3 = b1.createNextBlock(someOtherGuy);
        b3.addTransaction(spend);
        b3.solve();
        chain.add(roundtrip(b3));
        // The external spend is now pending.
        Assert.assertEquals(ZERO, wallet.getBalance());
        Transaction tx = wallet.getTransaction(spend.getTxId());
        Assert.assertEquals(PENDING, tx.getConfidence().getConfidenceType());
        Block b4 = b3.createNextBlock(someOtherGuy);
        chain.add(b4);
        // The external spend is now active.
        Assert.assertEquals(ZERO, wallet.getBalance());
        Assert.assertEquals(BUILDING, tx.getConfidence().getConfidenceType());
    }

    @Test
    public void testForking5() throws Exception {
        // Test the standard case in which a block containing identical transactions appears on a side chain.
        Block b1 = ChainSplitTest.UNITTEST.getGenesisBlock().createNextBlock(coinsTo);
        chain.add(b1);
        final Transaction t = b1.transactions.get(1);
        Assert.assertEquals(FIFTY_COINS, wallet.getBalance());
        // genesis -> b1
        // -> b2
        Block b2 = ChainSplitTest.UNITTEST.getGenesisBlock().createNextBlock(coinsTo);
        Transaction b2coinbase = b2.transactions.get(0);
        b2.transactions.clear();
        b2.addTransaction(b2coinbase);
        b2.addTransaction(t);
        b2.solve();
        chain.add(roundtrip(b2));
        Assert.assertEquals(FIFTY_COINS, wallet.getBalance());
        Assert.assertTrue(wallet.isConsistent());
        Assert.assertEquals(2, wallet.getTransaction(t.getTxId()).getAppearsInHashes().size());
        // -> b2 -> b3
        Block b3 = b2.createNextBlock(someOtherGuy);
        chain.add(b3);
        Assert.assertEquals(FIFTY_COINS, wallet.getBalance());
    }

    @Test
    public void testForking6() throws Exception {
        // Test the case in which a side chain block contains a tx, and then it appears in the best chain too.
        Block b1 = ChainSplitTest.UNITTEST.getGenesisBlock().createNextBlock(someOtherGuy);
        chain.add(b1);
        // genesis -> b1
        // -> b2
        Block b2 = ChainSplitTest.UNITTEST.getGenesisBlock().createNextBlock(coinsTo);
        chain.add(b2);
        Assert.assertEquals(ZERO, wallet.getBalance());
        // genesis -> b1 -> b3
        // -> b2
        Block b3 = b1.createNextBlock(someOtherGuy);
        b3.addTransaction(b2.transactions.get(1));
        b3.solve();
        chain.add(roundtrip(b3));
        Assert.assertEquals(FIFTY_COINS, wallet.getBalance());
    }

    @Test
    public void testDoubleSpendOnFork() throws Exception {
        // Check what happens when a re-org happens and one of our confirmed transactions becomes invalidated by a
        // double spend on the new best chain.
        final boolean[] eventCalled = new boolean[1];
        wallet.addTransactionConfidenceEventListener(new TransactionConfidenceEventListener() {
            @Override
            public void onTransactionConfidenceChanged(Wallet wallet, Transaction tx) {
                if ((tx.getConfidence().getConfidenceType()) == (ConfidenceType.DEAD))
                    eventCalled[0] = true;

            }
        });
        Block b1 = ChainSplitTest.UNITTEST.getGenesisBlock().createNextBlock(coinsTo);
        chain.add(b1);
        Transaction t1 = wallet.createSend(someOtherGuy, valueOf(10, 0));
        Address yetAnotherGuy = LegacyAddress.fromKey(ChainSplitTest.UNITTEST, new ECKey());
        Transaction t2 = wallet.createSend(yetAnotherGuy, valueOf(20, 0));
        wallet.commitTx(t1);
        // Receive t1 as confirmed by the network.
        Block b2 = b1.createNextBlock(LegacyAddress.fromKey(ChainSplitTest.UNITTEST, new ECKey()));
        b2.addTransaction(t1);
        b2.solve();
        chain.add(roundtrip(b2));
        // Now we make a double spend become active after a re-org.
        Block b3 = b1.createNextBlock(LegacyAddress.fromKey(ChainSplitTest.UNITTEST, new ECKey()));
        b3.addTransaction(t2);
        b3.solve();
        chain.add(roundtrip(b3));// Side chain.

        Block b4 = b3.createNextBlock(LegacyAddress.fromKey(ChainSplitTest.UNITTEST, new ECKey()));
        chain.add(b4);// New best chain.

        Threading.waitForUserCode();
        // Should have seen a double spend.
        Assert.assertTrue(eventCalled[0]);
        Assert.assertEquals(valueOf(30, 0), wallet.getBalance());
    }

    @Test
    public void testDoubleSpendOnForkPending() throws Exception {
        // Check what happens when a re-org happens and one of our unconfirmed transactions becomes invalidated by a
        // double spend on the new best chain.
        final Transaction[] eventDead = new Transaction[1];
        final Transaction[] eventReplacement = new Transaction[1];
        wallet.addTransactionConfidenceEventListener(new TransactionConfidenceEventListener() {
            @Override
            public void onTransactionConfidenceChanged(Wallet wallet, Transaction tx) {
                if ((tx.getConfidence().getConfidenceType()) == (ConfidenceType.DEAD)) {
                    eventDead[0] = tx;
                    eventReplacement[0] = tx.getConfidence().getOverridingTransaction();
                }
            }
        });
        // Start with 50 coins.
        Block b1 = ChainSplitTest.UNITTEST.getGenesisBlock().createNextBlock(coinsTo);
        chain.add(b1);
        Transaction t1 = Preconditions.checkNotNull(wallet.createSend(someOtherGuy, valueOf(10, 0)));
        Address yetAnotherGuy = LegacyAddress.fromKey(ChainSplitTest.UNITTEST, new ECKey());
        Transaction t2 = Preconditions.checkNotNull(wallet.createSend(yetAnotherGuy, valueOf(20, 0)));
        wallet.commitTx(t1);
        // t1 is still pending ...
        Block b2 = b1.createNextBlock(LegacyAddress.fromKey(ChainSplitTest.UNITTEST, new ECKey()));
        chain.add(b2);
        Assert.assertEquals(ZERO, wallet.getBalance());
        Assert.assertEquals(valueOf(40, 0), wallet.getBalance(ESTIMATED));
        // Now we make a double spend become active after a re-org.
        // genesis -> b1 -> b2 [t1 pending]
        // \-> b3 (t2) -> b4
        Block b3 = b1.createNextBlock(LegacyAddress.fromKey(ChainSplitTest.UNITTEST, new ECKey()));
        b3.addTransaction(t2);
        b3.solve();
        chain.add(roundtrip(b3));// Side chain.

        Block b4 = b3.createNextBlock(LegacyAddress.fromKey(ChainSplitTest.UNITTEST, new ECKey()));
        chain.add(b4);// New best chain.

        Threading.waitForUserCode();
        // Should have seen a double spend against the pending pool.
        // genesis -> b1 -> b2 [t1 dead and exited the miners mempools]
        // \-> b3 (t2) -> b4
        Assert.assertEquals(t1, eventDead[0]);
        Assert.assertEquals(t2, eventReplacement[0]);
        Assert.assertEquals(valueOf(30, 0), wallet.getBalance());
        // ... and back to our own parallel universe.
        Block b5 = b2.createNextBlock(LegacyAddress.fromKey(ChainSplitTest.UNITTEST, new ECKey()));
        chain.add(b5);
        Block b6 = b5.createNextBlock(LegacyAddress.fromKey(ChainSplitTest.UNITTEST, new ECKey()));
        chain.add(b6);
        // genesis -> b1 -> b2 -> b5 -> b6 [t1 still dead]
        // \-> b3 [t2 resurrected and now pending] -> b4
        Assert.assertEquals(ZERO, wallet.getBalance());
        // t2 is pending - resurrected double spends take precedence over our dead transactions (which are in nobodies
        // mempool by this point).
        t1 = Preconditions.checkNotNull(wallet.getTransaction(t1.getTxId()));
        t2 = Preconditions.checkNotNull(wallet.getTransaction(t2.getTxId()));
        Assert.assertEquals(DEAD, t1.getConfidence().getConfidenceType());
        Assert.assertEquals(PENDING, t2.getConfidence().getConfidenceType());
    }

    @Test
    public void txConfidenceLevels() throws Exception {
        // Check that as the chain forks and re-orgs, the confidence data associated with each transaction is
        // maintained correctly.
        final ArrayList<Transaction> txns = new ArrayList<>(3);
        wallet.addCoinsReceivedEventListener(new WalletCoinsReceivedEventListener() {
            @Override
            public void onCoinsReceived(Wallet wallet, Transaction tx, Coin.Coin prevBalance, Coin.Coin newBalance) {
                txns.add(tx);
            }
        });
        // Start by building three blocks on top of the genesis block. All send to us.
        Block b1 = ChainSplitTest.UNITTEST.getGenesisBlock().createNextBlock(coinsTo);
        BigInteger work1 = b1.getWork();
        Block b2 = b1.createNextBlock(coinsTo2);
        BigInteger work2 = b2.getWork();
        Block b3 = b2.createNextBlock(coinsTo2);
        BigInteger work3 = b3.getWork();
        Assert.assertTrue(chain.add(b1));
        Assert.assertTrue(chain.add(b2));
        Assert.assertTrue(chain.add(b3));
        Threading.waitForUserCode();
        // Check the transaction confidence levels are correct.
        Assert.assertEquals(3, txns.size());
        Assert.assertEquals(1, txns.get(0).getConfidence().getAppearedAtChainHeight());
        Assert.assertEquals(2, txns.get(1).getConfidence().getAppearedAtChainHeight());
        Assert.assertEquals(3, txns.get(2).getConfidence().getAppearedAtChainHeight());
        Assert.assertEquals(3, txns.get(0).getConfidence().getDepthInBlocks());
        Assert.assertEquals(2, txns.get(1).getConfidence().getDepthInBlocks());
        Assert.assertEquals(1, txns.get(2).getConfidence().getDepthInBlocks());
        // We now have the following chain:
        // genesis -> b1 -> b2 -> b3
        // 
        // so fork like this:
        // 
        // genesis -> b1 -> b2 -> b3
        // \-> b4 -> b5
        // 
        // Nothing should happen at this point. We saw b2 and b3 first so it takes priority.
        Block b4 = b1.createNextBlock(someOtherGuy);
        BigInteger work4 = b4.getWork();
        Block b5 = b4.createNextBlock(someOtherGuy);
        BigInteger work5 = b5.getWork();
        Assert.assertTrue(chain.add(b4));
        Assert.assertTrue(chain.add(b5));
        Threading.waitForUserCode();
        Assert.assertEquals(3, txns.size());
        Assert.assertEquals(1, txns.get(0).getConfidence().getAppearedAtChainHeight());
        Assert.assertEquals(2, txns.get(1).getConfidence().getAppearedAtChainHeight());
        Assert.assertEquals(3, txns.get(2).getConfidence().getAppearedAtChainHeight());
        Assert.assertEquals(3, txns.get(0).getConfidence().getDepthInBlocks());
        Assert.assertEquals(2, txns.get(1).getConfidence().getDepthInBlocks());
        Assert.assertEquals(1, txns.get(2).getConfidence().getDepthInBlocks());
        // Now we add another block to make the alternative chain longer.
        Block b6 = b5.createNextBlock(someOtherGuy);
        BigInteger work6 = b6.getWork();
        Assert.assertTrue(chain.add(b6));
        // 
        // genesis -> b1 -> b2 -> b3
        // \-> b4 -> b5 -> b6
        // 
        Assert.assertEquals(3, txns.size());
        Assert.assertEquals(1, txns.get(0).getConfidence().getAppearedAtChainHeight());
        Assert.assertEquals(4, txns.get(0).getConfidence().getDepthInBlocks());
        // Transaction 1 (in block b2) is now on a side chain, so it goes pending (not see in chain).
        Assert.assertEquals(PENDING, txns.get(1).getConfidence().getConfidenceType());
        try {
            txns.get(1).getConfidence().getAppearedAtChainHeight();
            Assert.fail();
        } catch (IllegalStateException e) {
        }
        Assert.assertEquals(0, txns.get(1).getConfidence().getDepthInBlocks());
        // ... and back to the first chain.
        Block b7 = b3.createNextBlock(coinsTo);
        BigInteger work7 = b7.getWork();
        Block b8 = b7.createNextBlock(coinsTo);
        BigInteger work8 = b7.getWork();
        Assert.assertTrue(chain.add(b7));
        Assert.assertTrue(chain.add(b8));
        // 
        // genesis -> b1 -> b2 -> b3 -> b7 -> b8
        // \-> b4 -> b5 -> b6
        // 
        // This should be enabled, once we figure out the best way to inform the user of how the wallet is changing
        // during the re-org.
        // assertEquals(5, txns.size());
        Assert.assertEquals(1, txns.get(0).getConfidence().getAppearedAtChainHeight());
        Assert.assertEquals(2, txns.get(1).getConfidence().getAppearedAtChainHeight());
        Assert.assertEquals(3, txns.get(2).getConfidence().getAppearedAtChainHeight());
        Assert.assertEquals(5, txns.get(0).getConfidence().getDepthInBlocks());
        Assert.assertEquals(4, txns.get(1).getConfidence().getDepthInBlocks());
        Assert.assertEquals(3, txns.get(2).getConfidence().getDepthInBlocks());
        Assert.assertEquals(Coin.Coin.valueOf(250, 0), wallet.getBalance());
        // Now add two more blocks that don't send coins to us. Despite being irrelevant the wallet should still update.
        Block b9 = b8.createNextBlock(someOtherGuy);
        Block b10 = b9.createNextBlock(someOtherGuy);
        chain.add(b9);
        chain.add(b10);
        BigInteger extraWork = b9.getWork().add(b10.getWork());
        Assert.assertEquals(7, txns.get(0).getConfidence().getDepthInBlocks());
        Assert.assertEquals(6, txns.get(1).getConfidence().getDepthInBlocks());
        Assert.assertEquals(5, txns.get(2).getConfidence().getDepthInBlocks());
    }

    @Test
    public void orderingInsideBlock() throws Exception {
        // Test that transactions received in the same block have their ordering preserved when reorganising.
        // This covers issue 468.
        // Receive some money to the wallet.
        Transaction t1 = FakeTxBuilder.createFakeTx(ChainSplitTest.UNITTEST, COIN, coinsTo);
        final Block b1 = FakeTxBuilder.makeSolvedTestBlock(ChainSplitTest.UNITTEST.genesisBlock, t1);
        chain.add(b1);
        // Send a couple of payments one after the other (so the second depends on the change output of the first).
        wallet.allowSpendingUnconfirmedTransactions();
        Transaction t2 = Preconditions.checkNotNull(wallet.createSend(LegacyAddress.fromKey(ChainSplitTest.UNITTEST, new ECKey()), CENT));
        wallet.commitTx(t2);
        Transaction t3 = Preconditions.checkNotNull(wallet.createSend(LegacyAddress.fromKey(ChainSplitTest.UNITTEST, new ECKey()), CENT));
        wallet.commitTx(t3);
        chain.add(FakeTxBuilder.makeSolvedTestBlock(b1, t2, t3));
        final Coin.Coin coins0point98 = COIN.subtract(CENT).subtract(CENT);
        Assert.assertEquals(coins0point98, wallet.getBalance());
        // Now round trip the wallet and force a re-org.
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        wallet.saveToFileStream(bos);
        wallet = Wallet.loadFromFileStream(new ByteArrayInputStream(bos.toByteArray()));
        final Block b2 = FakeTxBuilder.makeSolvedTestBlock(b1, t2, t3);
        final Block b3 = FakeTxBuilder.makeSolvedTestBlock(b2);
        chain.add(b2);
        chain.add(b3);
        // And verify that the balance is as expected. Because new ECKey() is non-deterministic, if the order
        // isn't being stored correctly this should fail 50% of the time.
        Assert.assertEquals(coins0point98, wallet.getBalance());
    }

    @Test
    public void coinbaseDeath() throws Exception {
        // Check that a coinbase tx is marked as dead after a reorg rather than pending as normal non-double-spent
        // transactions would be. Also check that a dead coinbase on a sidechain is resurrected if the sidechain
        // becomes the best chain once more. Finally, check that dependent transactions are killed recursively.
        final ArrayList<Transaction> txns = new ArrayList<>(3);
        wallet.addCoinsReceivedEventListener(SAME_THREAD, new WalletCoinsReceivedEventListener() {
            @Override
            public void onCoinsReceived(Wallet wallet, Transaction tx, Coin.Coin prevBalance, Coin.Coin newBalance) {
                txns.add(tx);
            }
        });
        Block b1 = ChainSplitTest.UNITTEST.getGenesisBlock().createNextBlock(someOtherGuy);
        final ECKey coinsTo2 = wallet.freshReceiveKey();
        Block b2 = b1.createNextBlockWithCoinbase(BLOCK_VERSION_GENESIS, coinsTo2.getPubKey(), 2);
        Block b3 = b2.createNextBlock(someOtherGuy);
        ChainSplitTest.log.debug("Adding block b1");
        Assert.assertTrue(chain.add(b1));
        ChainSplitTest.log.debug("Adding block b2");
        Assert.assertTrue(chain.add(b2));
        ChainSplitTest.log.debug("Adding block b3");
        Assert.assertTrue(chain.add(b3));
        // We now have the following chain:
        // genesis -> b1 -> b2 -> b3
        // 
        // Check we have seen the coinbase.
        Assert.assertEquals(1, txns.size());
        // Check the coinbase transaction is building and in the unspent pool only.
        final Transaction coinbase = txns.get(0);
        Assert.assertEquals(BUILDING, coinbase.getConfidence().getConfidenceType());
        Assert.assertTrue((!(wallet.poolContainsTxHash(WalletTransaction.Pool.PENDING, coinbase.getTxId()))));
        Assert.assertTrue(wallet.poolContainsTxHash(UNSPENT, coinbase.getTxId()));
        Assert.assertTrue((!(wallet.poolContainsTxHash(SPENT, coinbase.getTxId()))));
        Assert.assertTrue((!(wallet.poolContainsTxHash(WalletTransaction.Pool.DEAD, coinbase.getTxId()))));
        // Add blocks to b3 until we can spend the coinbase.
        Block firstTip = b3;
        for (int i = 0; i < ((ChainSplitTest.UNITTEST.getSpendableCoinbaseDepth()) - 2); i++) {
            firstTip = firstTip.createNextBlock(someOtherGuy);
            chain.add(firstTip);
        }
        // ... and spend.
        Transaction fodder = wallet.createSend(LegacyAddress.fromKey(ChainSplitTest.UNITTEST, new ECKey()), FIFTY_COINS);
        wallet.commitTx(fodder);
        final AtomicBoolean fodderIsDead = new AtomicBoolean(false);
        fodder.getConfidence().addEventListener(SAME_THREAD, new TransactionConfidence.Listener() {
            @Override
            public void onConfidenceChanged(TransactionConfidence confidence, ChangeReason reason) {
                fodderIsDead.set(((confidence.getConfidenceType()) == (ConfidenceType.DEAD)));
            }
        });
        // Fork like this:
        // 
        // genesis -> b1 -> b2 -> b3 -> [...]
        // \-> b4 -> b5 -> b6 -> [...]
        // 
        // The b4/ b5/ b6 is now the best chain
        Block b4 = b1.createNextBlock(someOtherGuy);
        Block b5 = b4.createNextBlock(someOtherGuy);
        Block b6 = b5.createNextBlock(someOtherGuy);
        ChainSplitTest.log.debug("Adding block b4");
        Assert.assertTrue(chain.add(b4));
        ChainSplitTest.log.debug("Adding block b5");
        Assert.assertTrue(chain.add(b5));
        ChainSplitTest.log.debug("Adding block b6");
        Assert.assertTrue(chain.add(b6));
        Block secondTip = b6;
        for (int i = 0; i < ((ChainSplitTest.UNITTEST.getSpendableCoinbaseDepth()) - 2); i++) {
            secondTip = secondTip.createNextBlock(someOtherGuy);
            chain.add(secondTip);
        }
        // Transaction 1 (in block b2) is now on a side chain and should have confidence type of dead and be in
        // the dead pool only.
        Assert.assertEquals(TransactionConfidence.ConfidenceType.DEAD, coinbase.getConfidence().getConfidenceType());
        Assert.assertTrue((!(wallet.poolContainsTxHash(WalletTransaction.Pool.PENDING, coinbase.getTxId()))));
        Assert.assertTrue((!(wallet.poolContainsTxHash(UNSPENT, coinbase.getTxId()))));
        Assert.assertTrue((!(wallet.poolContainsTxHash(SPENT, coinbase.getTxId()))));
        Assert.assertTrue(wallet.poolContainsTxHash(WalletTransaction.Pool.DEAD, coinbase.getTxId()));
        Assert.assertTrue(fodderIsDead.get());
        // ... and back to the first chain.
        Block b7 = firstTip.createNextBlock(someOtherGuy);
        Block b8 = b7.createNextBlock(someOtherGuy);
        ChainSplitTest.log.debug("Adding block b7");
        Assert.assertTrue(chain.add(b7));
        ChainSplitTest.log.debug("Adding block b8");
        Assert.assertTrue(chain.add(b8));
        // 
        // genesis -> b1 -> b2 -> b3 -> [...] -> b7 -> b8
        // \-> b4 -> b5 -> b6 -> [...]
        // 
        // The coinbase transaction should now have confidence type of building once more and in the unspent pool only.
        Assert.assertEquals(TransactionConfidence.ConfidenceType.BUILDING, coinbase.getConfidence().getConfidenceType());
        Assert.assertTrue((!(wallet.poolContainsTxHash(WalletTransaction.Pool.PENDING, coinbase.getTxId()))));
        Assert.assertTrue(wallet.poolContainsTxHash(UNSPENT, coinbase.getTxId()));
        Assert.assertTrue((!(wallet.poolContainsTxHash(SPENT, coinbase.getTxId()))));
        Assert.assertTrue((!(wallet.poolContainsTxHash(WalletTransaction.Pool.DEAD, coinbase.getTxId()))));
        // However, fodder is still dead. Bitcoin Core doesn't keep killed transactions around in case they become
        // valid again later. They are just deleted from the mempool for good.
        // ... make the side chain dominant again.
        Block b9 = secondTip.createNextBlock(someOtherGuy);
        Block b10 = b9.createNextBlock(someOtherGuy);
        ChainSplitTest.log.debug("Adding block b9");
        Assert.assertTrue(chain.add(b9));
        ChainSplitTest.log.debug("Adding block b10");
        Assert.assertTrue(chain.add(b10));
        // 
        // genesis -> b1 -> b2 -> b3 -> [...] -> b7 -> b8
        // \-> b4 -> b5 -> b6 -> [...] -> b9 -> b10
        // 
        // The coinbase transaction should now have the confidence type of dead and be in the dead pool only.
        Assert.assertEquals(TransactionConfidence.ConfidenceType.DEAD, coinbase.getConfidence().getConfidenceType());
        Assert.assertTrue((!(wallet.poolContainsTxHash(WalletTransaction.Pool.PENDING, coinbase.getTxId()))));
        Assert.assertTrue((!(wallet.poolContainsTxHash(UNSPENT, coinbase.getTxId()))));
        Assert.assertTrue((!(wallet.poolContainsTxHash(SPENT, coinbase.getTxId()))));
        Assert.assertTrue(wallet.poolContainsTxHash(WalletTransaction.Pool.DEAD, coinbase.getTxId()));
    }
}

