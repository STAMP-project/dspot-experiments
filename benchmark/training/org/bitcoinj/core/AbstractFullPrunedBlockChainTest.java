/**
 * Copyright 2012 Google Inc.
 * Copyright 2012 Matt Corallo.
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


import Block.BLOCK_VERSION_BIP34;
import Block.BLOCK_VERSION_GENESIS;
import Coin.ZERO;
import VerificationException.CoinbaseHeightMismatch;
import Wallet.BalanceType.AVAILABLE;
import Wallet.BalanceType.ESTIMATED;
import WalletTransaction.Pool.PENDING;
import com.google.common.collect.Lists;
import java.io.File;
import java.lang.ref.WeakReference;
import java.util.Arrays;
import java.util.List;
import org.bitcoinj.params.MainNetParams;
import org.bitcoinj.params.UnitTestParams;
import org.bitcoinj.script.Script;
import org.bitcoinj.store.FullPrunedBlockStore;
import org.bitcoinj.utils.BlockFileLoader;
import org.bitcoinj.wallet.SendRequest;
import org.bitcoinj.wallet.Wallet;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static Coin.ZERO;


/**
 * We don't do any wallet tests here, we leave that to {@link ChainSplitTest}
 */
public abstract class AbstractFullPrunedBlockChainTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private static final Logger log = LoggerFactory.getLogger(AbstractFullPrunedBlockChainTest.class);

    protected static final NetworkParameters PARAMS = new UnitTestParams() {
        @Override
        public int getInterval() {
            return 10000;
        }
    };

    private static final NetworkParameters MAINNET = MainNetParams.get();

    protected FullPrunedBlockChain chain;

    protected FullPrunedBlockStore store;

    @Test
    public void testGeneratedChain() throws Exception {
        // Tests various test cases from FullBlockTestGenerator
        FullBlockTestGenerator generator = new FullBlockTestGenerator(AbstractFullPrunedBlockChainTest.PARAMS);
        RuleList blockList = generator.getBlocksToTest(false, false, null);
        store = createStore(AbstractFullPrunedBlockChainTest.PARAMS, blockList.maximumReorgBlockCount);
        chain = new FullPrunedBlockChain(AbstractFullPrunedBlockChainTest.PARAMS, store);
        for (Rule rule : blockList.list) {
            if (!(rule instanceof FullBlockTestGenerator.BlockAndValidity))
                continue;

            FullBlockTestGenerator.BlockAndValidity block = ((FullBlockTestGenerator.BlockAndValidity) (rule));
            AbstractFullPrunedBlockChainTest.log.info(((("Testing rule " + (block.ruleName)) + " with block hash ") + (block.block.getHash())));
            boolean threw = false;
            try {
                if ((chain.add(block.block)) != (block.connects)) {
                    AbstractFullPrunedBlockChainTest.log.error(("Block didn't match connects flag on block " + (block.ruleName)));
                    Assert.fail();
                }
            } catch (VerificationException e) {
                threw = true;
                if (!(block.throwsException)) {
                    AbstractFullPrunedBlockChainTest.log.error(("Block didn't match throws flag on block " + (block.ruleName)));
                    throw e;
                }
                if (block.connects) {
                    AbstractFullPrunedBlockChainTest.log.error(("Block didn't match connects flag on block " + (block.ruleName)));
                    Assert.fail();
                }
            }
            if ((!threw) && (block.throwsException)) {
                AbstractFullPrunedBlockChainTest.log.error(("Block didn't match throws flag on block " + (block.ruleName)));
                Assert.fail();
            }
            if (!(chain.getChainHead().getHeader().getHash().equals(block.hashChainTipAfterBlock))) {
                AbstractFullPrunedBlockChainTest.log.error(("New block head didn't match the correct value after block " + (block.ruleName)));
                Assert.fail();
            }
            if ((chain.getChainHead().getHeight()) != (block.heightAfterBlock)) {
                AbstractFullPrunedBlockChainTest.log.error(("New block head didn't match the correct height after block " + (block.ruleName)));
                Assert.fail();
            }
        }
        try {
            store.close();
        } catch (Exception e) {
        }
    }

    @Test
    public void skipScripts() throws Exception {
        store = createStore(AbstractFullPrunedBlockChainTest.PARAMS, 10);
        chain = new FullPrunedBlockChain(AbstractFullPrunedBlockChainTest.PARAMS, store);
        // Check that we aren't accidentally leaving any references
        // to the full StoredUndoableBlock's lying around (ie memory leaks)
        ECKey outKey = new ECKey();
        int height = 1;
        // Build some blocks on genesis block to create a spendable output
        Block rollingBlock = AbstractFullPrunedBlockChainTest.PARAMS.getGenesisBlock().createNextBlockWithCoinbase(BLOCK_VERSION_GENESIS, outKey.getPubKey(), (height++));
        chain.add(rollingBlock);
        TransactionOutput spendableOutput = rollingBlock.getTransactions().get(0).getOutput(0);
        for (int i = 1; i < (AbstractFullPrunedBlockChainTest.PARAMS.getSpendableCoinbaseDepth()); i++) {
            rollingBlock = rollingBlock.createNextBlockWithCoinbase(BLOCK_VERSION_GENESIS, outKey.getPubKey(), (height++));
            chain.add(rollingBlock);
        }
        rollingBlock = rollingBlock.createNextBlock(null);
        Transaction t = new Transaction(AbstractFullPrunedBlockChainTest.PARAMS);
        t.addOutput(new TransactionOutput(AbstractFullPrunedBlockChainTest.PARAMS, t, Coin.FIFTY_COINS, new byte[]{  }));
        TransactionInput input = t.addInput(spendableOutput);
        // Invalid script.
        input.clearScriptBytes();
        rollingBlock.addTransaction(t);
        rollingBlock.solve();
        chain.setRunScripts(false);
        try {
            chain.add(rollingBlock);
        } catch (VerificationException e) {
            Assert.fail();
        }
        try {
            store.close();
        } catch (Exception e) {
        }
    }

    @Test
    public void testFinalizedBlocks() throws Exception {
        final int UNDOABLE_BLOCKS_STORED = 10;
        store = createStore(AbstractFullPrunedBlockChainTest.PARAMS, UNDOABLE_BLOCKS_STORED);
        chain = new FullPrunedBlockChain(AbstractFullPrunedBlockChainTest.PARAMS, store);
        // Check that we aren't accidentally leaving any references
        // to the full StoredUndoableBlock's lying around (ie memory leaks)
        ECKey outKey = new ECKey();
        int height = 1;
        // Build some blocks on genesis block to create a spendable output
        Block rollingBlock = AbstractFullPrunedBlockChainTest.PARAMS.getGenesisBlock().createNextBlockWithCoinbase(BLOCK_VERSION_GENESIS, outKey.getPubKey(), (height++));
        chain.add(rollingBlock);
        TransactionOutPoint spendableOutput = new TransactionOutPoint(AbstractFullPrunedBlockChainTest.PARAMS, 0, rollingBlock.getTransactions().get(0).getTxId());
        byte[] spendableOutputScriptPubKey = rollingBlock.getTransactions().get(0).getOutputs().get(0).getScriptBytes();
        for (int i = 1; i < (AbstractFullPrunedBlockChainTest.PARAMS.getSpendableCoinbaseDepth()); i++) {
            rollingBlock = rollingBlock.createNextBlockWithCoinbase(BLOCK_VERSION_GENESIS, outKey.getPubKey(), (height++));
            chain.add(rollingBlock);
        }
        WeakReference<UTXO> out = new WeakReference<UTXO>(store.getTransactionOutput(spendableOutput.getHash(), spendableOutput.getIndex()));
        rollingBlock = rollingBlock.createNextBlock(null);
        Transaction t = new Transaction(AbstractFullPrunedBlockChainTest.PARAMS);
        // Entirely invalid scriptPubKey
        t.addOutput(new TransactionOutput(AbstractFullPrunedBlockChainTest.PARAMS, t, Coin.FIFTY_COINS, new byte[]{  }));
        t.addSignedInput(spendableOutput, new Script(spendableOutputScriptPubKey), outKey);
        rollingBlock.addTransaction(t);
        rollingBlock.solve();
        chain.add(rollingBlock);
        WeakReference<StoredUndoableBlock> undoBlock = new WeakReference(store.getUndoBlock(rollingBlock.getHash()));
        StoredUndoableBlock storedUndoableBlock = undoBlock.get();
        Assert.assertNotNull(storedUndoableBlock);
        Assert.assertNull(storedUndoableBlock.getTransactions());
        WeakReference<TransactionOutputChanges> changes = new WeakReference(storedUndoableBlock.getTxOutChanges());
        Assert.assertNotNull(changes.get());
        storedUndoableBlock = null;// Blank the reference so it can be GCd.

        // Create a chain longer than UNDOABLE_BLOCKS_STORED
        for (int i = 0; i < UNDOABLE_BLOCKS_STORED; i++) {
            rollingBlock = rollingBlock.createNextBlock(null);
            chain.add(rollingBlock);
        }
        // Try to get the garbage collector to run
        System.gc();
        Assert.assertNull(undoBlock.get());
        Assert.assertNull(changes.get());
        Assert.assertNull(out.get());
        try {
            store.close();
        } catch (Exception e) {
        }
    }

    @Test
    public void testFirst100KBlocks() throws Exception {
        Context context = new Context(AbstractFullPrunedBlockChainTest.MAINNET);
        File blockFile = new File(getClass().getResource("first-100k-blocks.dat").getFile());
        BlockFileLoader loader = new BlockFileLoader(AbstractFullPrunedBlockChainTest.MAINNET, Arrays.asList(blockFile));
        store = createStore(AbstractFullPrunedBlockChainTest.MAINNET, 10);
        resetStore(store);
        chain = new FullPrunedBlockChain(context, store);
        for (Block block : loader)
            chain.add(block);

        try {
            store.close();
        } catch (Exception e) {
        }
    }

    @Test
    public void testGetOpenTransactionOutputs() throws Exception {
        final int UNDOABLE_BLOCKS_STORED = 10;
        store = createStore(AbstractFullPrunedBlockChainTest.PARAMS, UNDOABLE_BLOCKS_STORED);
        chain = new FullPrunedBlockChain(AbstractFullPrunedBlockChainTest.PARAMS, store);
        // Check that we aren't accidentally leaving any references
        // to the full StoredUndoableBlock's lying around (ie memory leaks)
        ECKey outKey = new ECKey();
        int height = 1;
        // Build some blocks on genesis block to create a spendable output
        Block rollingBlock = AbstractFullPrunedBlockChainTest.PARAMS.getGenesisBlock().createNextBlockWithCoinbase(BLOCK_VERSION_GENESIS, outKey.getPubKey(), (height++));
        chain.add(rollingBlock);
        Transaction transaction = rollingBlock.getTransactions().get(0);
        TransactionOutPoint spendableOutput = new TransactionOutPoint(AbstractFullPrunedBlockChainTest.PARAMS, 0, transaction.getTxId());
        byte[] spendableOutputScriptPubKey = transaction.getOutputs().get(0).getScriptBytes();
        for (int i = 1; i < (AbstractFullPrunedBlockChainTest.PARAMS.getSpendableCoinbaseDepth()); i++) {
            rollingBlock = rollingBlock.createNextBlockWithCoinbase(BLOCK_VERSION_GENESIS, outKey.getPubKey(), (height++));
            chain.add(rollingBlock);
        }
        rollingBlock = rollingBlock.createNextBlock(null);
        // Create bitcoin spend of 1 BTC.
        ECKey toKey = new ECKey();
        Coin amount = Coin.valueOf(100000000);
        Address address = LegacyAddress.fromKey(AbstractFullPrunedBlockChainTest.PARAMS, toKey);
        Coin totalAmount = ZERO;
        Transaction t = new Transaction(AbstractFullPrunedBlockChainTest.PARAMS);
        t.addOutput(new TransactionOutput(AbstractFullPrunedBlockChainTest.PARAMS, t, amount, toKey));
        t.addSignedInput(spendableOutput, new Script(spendableOutputScriptPubKey), outKey);
        rollingBlock.addTransaction(t);
        rollingBlock.solve();
        chain.add(rollingBlock);
        totalAmount = totalAmount.add(amount);
        List<UTXO> outputs = store.getOpenTransactionOutputs(Lists.newArrayList(toKey));
        Assert.assertNotNull(outputs);
        Assert.assertEquals("Wrong Number of Outputs", 1, outputs.size());
        UTXO output = outputs.get(0);
        Assert.assertEquals("The address is not equal", address.toString(), output.getAddress());
        Assert.assertEquals("The amount is not equal", totalAmount, output.getValue());
        outputs = null;
        output = null;
        try {
            store.close();
        } catch (Exception e) {
        }
    }

    @Test
    public void testUTXOProviderWithWallet() throws Exception {
        final int UNDOABLE_BLOCKS_STORED = 10;
        store = createStore(AbstractFullPrunedBlockChainTest.PARAMS, UNDOABLE_BLOCKS_STORED);
        chain = new FullPrunedBlockChain(AbstractFullPrunedBlockChainTest.PARAMS, store);
        // Check that we aren't accidentally leaving any references
        // to the full StoredUndoableBlock's lying around (ie memory leaks)
        ECKey outKey = new ECKey();
        int height = 1;
        // Build some blocks on genesis block to create a spendable output.
        Block rollingBlock = AbstractFullPrunedBlockChainTest.PARAMS.getGenesisBlock().createNextBlockWithCoinbase(BLOCK_VERSION_GENESIS, outKey.getPubKey(), (height++));
        chain.add(rollingBlock);
        Transaction transaction = rollingBlock.getTransactions().get(0);
        TransactionOutPoint spendableOutput = new TransactionOutPoint(AbstractFullPrunedBlockChainTest.PARAMS, 0, transaction.getTxId());
        byte[] spendableOutputScriptPubKey = transaction.getOutputs().get(0).getScriptBytes();
        for (int i = 1; i < (AbstractFullPrunedBlockChainTest.PARAMS.getSpendableCoinbaseDepth()); i++) {
            rollingBlock = rollingBlock.createNextBlockWithCoinbase(BLOCK_VERSION_GENESIS, outKey.getPubKey(), (height++));
            chain.add(rollingBlock);
        }
        rollingBlock = rollingBlock.createNextBlock(null);
        // Create 1 BTC spend to a key in this wallet (to ourselves).
        Wallet wallet = new Wallet(AbstractFullPrunedBlockChainTest.PARAMS);
        Assert.assertEquals("Available balance is incorrect", ZERO, wallet.getBalance(AVAILABLE));
        Assert.assertEquals("Estimated balance is incorrect", ZERO, wallet.getBalance(ESTIMATED));
        wallet.setUTXOProvider(store);
        ECKey toKey = wallet.freshReceiveKey();
        Coin amount = Coin.valueOf(100000000);
        Transaction t = new Transaction(AbstractFullPrunedBlockChainTest.PARAMS);
        t.addOutput(new TransactionOutput(AbstractFullPrunedBlockChainTest.PARAMS, t, amount, toKey));
        t.addSignedInput(spendableOutput, new Script(spendableOutputScriptPubKey), outKey);
        rollingBlock.addTransaction(t);
        rollingBlock.solve();
        chain.add(rollingBlock);
        // Create another spend of 1/2 the value of BTC we have available using the wallet (store coin selector).
        ECKey toKey2 = new ECKey();
        Coin amount2 = amount.divide(2);
        Address address2 = LegacyAddress.fromKey(AbstractFullPrunedBlockChainTest.PARAMS, toKey2);
        SendRequest req = SendRequest.to(address2, amount2);
        wallet.completeTx(req);
        wallet.commitTx(req.tx);
        Coin fee = ZERO;
        // There should be one pending tx (our spend).
        Assert.assertEquals("Wrong number of PENDING.4", 1, wallet.getPoolSize(PENDING));
        Coin totalPendingTxAmount = ZERO;
        for (Transaction tx : wallet.getPendingTransactions()) {
            totalPendingTxAmount = totalPendingTxAmount.add(tx.getValueSentToMe(wallet));
        }
        // The available balance should be the 0 (as we spent the 1 BTC that's pending) and estimated should be 1/2 - fee BTC
        Assert.assertEquals("Available balance is incorrect", ZERO, wallet.getBalance(AVAILABLE));
        Assert.assertEquals("Estimated balance is incorrect", amount2.subtract(fee), wallet.getBalance(ESTIMATED));
        Assert.assertEquals("Pending tx amount is incorrect", amount2.subtract(fee), totalPendingTxAmount);
        try {
            store.close();
        } catch (Exception e) {
        }
    }

    /**
     * Test that if the block height is missing from coinbase of a version 2
     * block, it's rejected.
     */
    @Test
    public void missingHeightFromCoinbase() throws Exception {
        final int UNDOABLE_BLOCKS_STORED = (AbstractFullPrunedBlockChainTest.PARAMS.getMajorityEnforceBlockUpgrade()) + 1;
        store = createStore(AbstractFullPrunedBlockChainTest.PARAMS, UNDOABLE_BLOCKS_STORED);
        try {
            chain = new FullPrunedBlockChain(AbstractFullPrunedBlockChainTest.PARAMS, store);
            ECKey outKey = new ECKey();
            int height = 1;
            Block chainHead = AbstractFullPrunedBlockChainTest.PARAMS.getGenesisBlock();
            // Build some blocks on genesis block to create a spendable output.
            // Put in just enough v1 blocks to stop the v2 blocks from forming a majority
            for (height = 1; height <= ((AbstractFullPrunedBlockChainTest.PARAMS.getMajorityWindow()) - (AbstractFullPrunedBlockChainTest.PARAMS.getMajorityEnforceBlockUpgrade())); height++) {
                chainHead = chainHead.createNextBlockWithCoinbase(BLOCK_VERSION_GENESIS, outKey.getPubKey(), height);
                chain.add(chainHead);
            }
            // Fill the rest of the window in with v2 blocks
            for (; height < (AbstractFullPrunedBlockChainTest.PARAMS.getMajorityWindow()); height++) {
                chainHead = chainHead.createNextBlockWithCoinbase(BLOCK_VERSION_BIP34, outKey.getPubKey(), height);
                chain.add(chainHead);
            }
            // Throw a broken v2 block in before we have a supermajority to enable
            // enforcement, which should validate as-is
            chainHead = chainHead.createNextBlockWithCoinbase(BLOCK_VERSION_BIP34, outKey.getPubKey(), (height * 2));
            chain.add(chainHead);
            height++;
            // Trying to add a broken v2 block should now result in rejection as
            // we have a v2 supermajority
            thrown.expect(CoinbaseHeightMismatch.class);
            chainHead = chainHead.createNextBlockWithCoinbase(BLOCK_VERSION_BIP34, outKey.getPubKey(), (height * 2));
            chain.add(chainHead);
        } catch (final VerificationException ex) {
            throw ((Exception) (ex.getCause()));
        } finally {
            try {
                store.close();
            } catch (Exception e) {
                // Catch and drop any exception so a break mid-test doesn't result
                // in a new exception being thrown and the original lost
            }
        }
    }
}

