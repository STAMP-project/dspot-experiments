/**
 * Copyright (c) [2016] [ <ether.camp> ]
 * This file is part of the ethereumJ library.
 *
 * The ethereumJ library is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * The ethereumJ library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with the ethereumJ library. If not, see <http://www.gnu.org/licenses/>.
 */
package org.ethereum.mine;


import EtherUtil.Unit.ETHER;
import SyncStatus.SyncStage.Complete;
import java.math.BigInteger;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.ethereum.config.NoAutoscan;
import org.ethereum.config.SystemProperties;
import org.ethereum.core.Block;
import org.ethereum.core.BlockSummary;
import org.ethereum.core.Blockchain;
import org.ethereum.core.ImportResult;
import org.ethereum.core.Transaction;
import org.ethereum.crypto.ECKey;
import org.ethereum.crypto.HashUtil;
import org.ethereum.facade.Ethereum;
import org.ethereum.listener.EthereumListenerAdapter;
import org.ethereum.net.eth.handler.Eth62;
import org.ethereum.net.rlpx.Node;
import org.ethereum.util.FastByteComparisons;
import org.ethereum.util.blockchain.EtherUtil;
import org.ethereum.util.blockchain.StandaloneBlockchain;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.spongycastle.util.encoders.Hex;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;


/**
 * If Miner is started manually, sync status is not changed in any manner,
 * so if miner is started while the peer is in Long Sync mode, miner creates
 * new blocks but ignores any txs, because they are dropped by {@link org.ethereum.core.PendingStateImpl}
 * While automatic detection of long sync works correctly in any live network
 * with big number of peers, automatic detection of Short Sync condition in
 * detached or small private networks looks not doable.
 *
 * To resolve this and any other similar issues manual switch to Short Sync mode
 * was added: {@link EthereumImpl#switchToShortSync()}
 * This test verifies that manual switching to Short Sync works correctly
 * and solves miner issue.
 */
@Ignore("Long network tests")
public class SyncDoneTest {
    private static Node nodeA;

    private static List<Block> mainB1B10;

    private Ethereum ethereumA;

    private Ethereum ethereumB;

    private String testDbA;

    private String testDbB;

    private static final int MAX_SECONDS_WAIT = 60;

    // positive gap, A on main, B on main
    // expected: B downloads missed blocks from A => B on main
    @Test
    public void test1() throws InterruptedException {
        setupPeers();
        // A == B == genesis
        Blockchain blockchainA = ((Blockchain) (ethereumA.getBlockchain()));
        for (Block b : SyncDoneTest.mainB1B10) {
            ImportResult result = blockchainA.tryToConnect(b);
            System.out.println(result.isSuccessful());
        }
        long loadedBlocks = blockchainA.getBestBlock().getNumber();
        // Check that we are synced and on the same block
        Assert.assertTrue((loadedBlocks > 0));
        final CountDownLatch semaphore = new CountDownLatch(1);
        ethereumB.addListener(new EthereumListenerAdapter() {
            @Override
            public void onBlock(BlockSummary blockSummary) {
                if ((blockSummary.getBlock().getNumber()) == loadedBlocks) {
                    semaphore.countDown();
                }
            }
        });
        semaphore.await(SyncDoneTest.MAX_SECONDS_WAIT, TimeUnit.SECONDS);
        Assert.assertEquals(0, semaphore.getCount());
        Assert.assertEquals(loadedBlocks, ethereumB.getBlockchain().getBestBlock().getNumber());
        ethereumA.getBlockMiner().startMining();
        final CountDownLatch semaphore2 = new CountDownLatch(2);
        ethereumB.addListener(new EthereumListenerAdapter() {
            @Override
            public void onBlock(BlockSummary blockSummary) {
                if ((blockSummary.getBlock().getNumber()) == (loadedBlocks + 2)) {
                    semaphore2.countDown();
                    ethereumA.getBlockMiner().stopMining();
                }
            }
        });
        ethereumA.addListener(new EthereumListenerAdapter() {
            @Override
            public void onBlock(BlockSummary blockSummary) {
                if ((blockSummary.getBlock().getNumber()) == (loadedBlocks + 2)) {
                    semaphore2.countDown();
                }
            }
        });
        semaphore2.await(SyncDoneTest.MAX_SECONDS_WAIT, TimeUnit.SECONDS);
        Assert.assertEquals(0, semaphore2.getCount());
        Assert.assertFalse(ethereumA.getSyncStatus().getStage().equals(Complete));
        Assert.assertTrue(ethereumB.getSyncStatus().getStage().equals(Complete));// Receives NEW_BLOCKs from EthereumA

        // Trying to include txs while miner is on long sync
        // Txs should be dropped as peer is not on short sync
        ECKey sender = ECKey.fromPrivate(Hex.decode("3ec771c31cac8c0dba77a69e503765701d3c2bb62435888d4ffa38fed60c445c"));
        Transaction tx = Transaction.create(Hex.toHexString(ECKey.fromPrivate(HashUtil.sha3("cow".getBytes())).getAddress()), EtherUtil.convert(1, ETHER), BigInteger.ZERO, BigInteger.valueOf(ethereumA.getGasPrice()), new BigInteger("3000000"), null);
        tx.sign(sender);
        final CountDownLatch txSemaphore = new CountDownLatch(1);
        ethereumA.addListener(new EthereumListenerAdapter() {
            @Override
            public void onBlock(BlockSummary blockSummary) {
                if (((!(blockSummary.getBlock().getTransactionsList().isEmpty())) && (FastByteComparisons.equal(blockSummary.getBlock().getTransactionsList().get(0).getSender(), sender.getAddress()))) && (blockSummary.getReceipts().get(0).isSuccessful())) {
                    txSemaphore.countDown();
                }
            }
        });
        ethereumB.submitTransaction(tx);
        final CountDownLatch semaphore3 = new CountDownLatch(2);
        ethereumB.addListener(new EthereumListenerAdapter() {
            @Override
            public void onBlock(BlockSummary blockSummary) {
                if ((blockSummary.getBlock().getNumber()) == (loadedBlocks + 5)) {
                    semaphore3.countDown();
                }
            }
        });
        ethereumA.addListener(new EthereumListenerAdapter() {
            @Override
            public void onBlock(BlockSummary blockSummary) {
                if ((blockSummary.getBlock().getNumber()) == (loadedBlocks + 5)) {
                    semaphore3.countDown();
                    ethereumA.getBlockMiner().stopMining();
                }
            }
        });
        ethereumA.getBlockMiner().startMining();
        semaphore3.await(SyncDoneTest.MAX_SECONDS_WAIT, TimeUnit.SECONDS);
        Assert.assertEquals(0, semaphore3.getCount());
        Assert.assertEquals((loadedBlocks + 5), ethereumA.getBlockchain().getBestBlock().getNumber());
        Assert.assertEquals((loadedBlocks + 5), ethereumB.getBlockchain().getBestBlock().getNumber());
        Assert.assertFalse(ethereumA.getSyncStatus().getStage().equals(Complete));
        // Tx was not included, because miner is on long sync
        Assert.assertFalse(((txSemaphore.getCount()) == 0));
        ethereumA.getBlockMiner().startMining();
        try {
            ethereumA.switchToShortSync().get(1, TimeUnit.SECONDS);
        } catch (ExecutionException | TimeoutException e) {
            e.printStackTrace();
        }
        Assert.assertTrue(ethereumA.getSyncStatus().getStage().equals(Complete));
        Transaction tx2 = Transaction.create(Hex.toHexString(ECKey.fromPrivate(HashUtil.sha3("cow".getBytes())).getAddress()), EtherUtil.convert(1, ETHER), BigInteger.ZERO, BigInteger.valueOf(ethereumA.getGasPrice()).add(BigInteger.TEN), new BigInteger("3000000"), null);
        tx2.sign(sender);
        ethereumB.submitTransaction(tx2);
        final CountDownLatch semaphore4 = new CountDownLatch(2);
        ethereumB.addListener(new EthereumListenerAdapter() {
            @Override
            public void onBlock(BlockSummary blockSummary) {
                if ((blockSummary.getBlock().getNumber()) == (loadedBlocks + 9)) {
                    semaphore4.countDown();
                    ethereumA.getBlockMiner().stopMining();
                }
            }
        });
        ethereumA.addListener(new EthereumListenerAdapter() {
            @Override
            public void onBlock(BlockSummary blockSummary) {
                if ((blockSummary.getBlock().getNumber()) == (loadedBlocks + 9)) {
                    semaphore4.countDown();
                }
            }
        });
        semaphore4.await(SyncDoneTest.MAX_SECONDS_WAIT, TimeUnit.SECONDS);
        Assert.assertEquals(0, semaphore4.getCount());
        Assert.assertEquals((loadedBlocks + 9), ethereumA.getBlockchain().getBestBlock().getNumber());
        Assert.assertEquals((loadedBlocks + 9), ethereumB.getBlockchain().getBestBlock().getNumber());
        Assert.assertTrue(ethereumA.getSyncStatus().getStage().equals(Complete));
        Assert.assertTrue(ethereumB.getSyncStatus().getStage().equals(Complete));
        // Tx is included!
        Assert.assertTrue(((txSemaphore.getCount()) == 0));
    }

    @Configuration
    @NoAutoscan
    public static class SysPropConfigA {
        static SystemProperties props = new SystemProperties();

        static Eth62 eth62 = null;

        @Bean
        public SystemProperties systemProperties() {
            SyncDoneTest.SysPropConfigA.props.setBlockchainConfig(StandaloneBlockchain.getEasyMiningConfig());
            return SyncDoneTest.SysPropConfigA.props;
        }

        @Bean
        @Scope("prototype")
        public Eth62 eth62() throws IllegalAccessException, InstantiationException {
            if ((SyncDoneTest.SysPropConfigA.eth62) != null)
                return SyncDoneTest.SysPropConfigA.eth62;

            return new Eth62();
        }
    }

    @Configuration
    @NoAutoscan
    public static class SysPropConfigB {
        static SystemProperties props = new SystemProperties();

        @Bean
        public SystemProperties systemProperties() {
            SyncDoneTest.SysPropConfigB.props.setBlockchainConfig(StandaloneBlockchain.getEasyMiningConfig());
            return SyncDoneTest.SysPropConfigB.props;
        }
    }
}

