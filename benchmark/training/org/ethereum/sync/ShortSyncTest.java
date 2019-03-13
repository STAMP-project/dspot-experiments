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
package org.ethereum.sync;


import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.ethereum.config.NoAutoscan;
import org.ethereum.config.SystemProperties;
import org.ethereum.core.Block;
import org.ethereum.core.BlockHeader;
import org.ethereum.core.Blockchain;
import org.ethereum.core.TransactionReceipt;
import org.ethereum.facade.Ethereum;
import org.ethereum.listener.EthereumListenerAdapter;
import org.ethereum.net.eth.handler.Eth62;
import org.ethereum.net.eth.handler.EthHandler;
import org.ethereum.net.message.Message;
import org.ethereum.net.p2p.DisconnectMessage;
import org.ethereum.net.rlpx.Node;
import org.ethereum.net.server.Channel;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;


/**
 *
 *
 * @author Mikhail Kalinin
 * @since 14.12.2015
 */
@Ignore("Long network tests")
public class ShortSyncTest {
    private static BigInteger minDifficultyBackup;

    private static Node nodeA;

    private static List<Block> mainB1B10;

    private static List<Block> forkB1B5B8_;

    private static Block b10;

    private static Block b8_;

    private Ethereum ethereumA;

    private Ethereum ethereumB;

    private EthHandler ethA;

    private String testDbA;

    private String testDbB;

    // positive gap, A on main, B on main
    // expected: B downloads missed blocks from A => B on main
    @Test
    public void test1() throws InterruptedException {
        setupPeers();
        // A == B == genesis
        Blockchain blockchainA = ((Blockchain) (ethereumA.getBlockchain()));
        for (Block b : ShortSyncTest.mainB1B10) {
            blockchainA.tryToConnect(b);
        }
        // A == b10, B == genesis
        final CountDownLatch semaphore = new CountDownLatch(1);
        ethereumB.addListener(new EthereumListenerAdapter() {
            @Override
            public void onBlock(Block block, List<TransactionReceipt> receipts) {
                if (block.isEqual(ShortSyncTest.b10)) {
                    semaphore.countDown();
                }
            }
        });
        ethA.sendNewBlock(ShortSyncTest.b10);
        semaphore.await(10, TimeUnit.SECONDS);
        // check if B == b10
        if ((semaphore.getCount()) > 0) {
            Assert.fail("PeerB bestBlock is incorrect");
        }
    }

    // positive gap, A on fork, B on main
    // positive gap, A on fork, B on fork (same story)
    // expected: B downloads missed blocks from A => B on A's fork
    @Test
    public void test2() throws InterruptedException {
        setupPeers();
        // A == B == genesis
        Blockchain blockchainA = ((Blockchain) (ethereumA.getBlockchain()));
        for (Block b : ShortSyncTest.forkB1B5B8_) {
            blockchainA.tryToConnect(b);
        }
        // A == b8', B == genesis
        final CountDownLatch semaphore = new CountDownLatch(1);
        ethereumB.addListener(new EthereumListenerAdapter() {
            @Override
            public void onBlock(Block block, List<TransactionReceipt> receipts) {
                if (block.isEqual(ShortSyncTest.b8_)) {
                    semaphore.countDown();
                }
            }
        });
        ethA.sendNewBlock(ShortSyncTest.b8_);
        semaphore.await(10, TimeUnit.SECONDS);
        // check if B == b8'
        if ((semaphore.getCount()) > 0) {
            Assert.fail("PeerB bestBlock is incorrect");
        }
    }

    // positive gap, A on main, B on fork
    // expected: B finds common ancestor and downloads missed blocks from A => B on main
    @Test
    public void test3() throws InterruptedException {
        setupPeers();
        // A == B == genesis
        Blockchain blockchainA = ((Blockchain) (ethereumA.getBlockchain()));
        Blockchain blockchainB = ((Blockchain) (ethereumB.getBlockchain()));
        for (Block b : ShortSyncTest.mainB1B10) {
            blockchainA.tryToConnect(b);
        }
        for (Block b : ShortSyncTest.forkB1B5B8_) {
            blockchainB.tryToConnect(b);
        }
        // A == b10, B == b8'
        final CountDownLatch semaphore = new CountDownLatch(1);
        ethereumB.addListener(new EthereumListenerAdapter() {
            @Override
            public void onBlock(Block block, List<TransactionReceipt> receipts) {
                if (block.isEqual(ShortSyncTest.b10)) {
                    semaphore.countDown();
                }
            }
        });
        ethA.sendNewBlock(ShortSyncTest.b10);
        semaphore.await(10, TimeUnit.SECONDS);
        // check if B == b10
        if ((semaphore.getCount()) > 0) {
            Assert.fail("PeerB bestBlock is incorrect");
        }
    }

    // negative gap, A on main, B on main
    // expected: B skips A's block as already imported => B on main
    @Test
    public void test4() throws InterruptedException {
        setupPeers();
        final Block b5 = ShortSyncTest.mainB1B10.get(4);
        Block b9 = ShortSyncTest.mainB1B10.get(8);
        // A == B == genesis
        Blockchain blockchainA = ((Blockchain) (ethereumA.getBlockchain()));
        Blockchain blockchainB = ((Blockchain) (ethereumB.getBlockchain()));
        for (Block b : ShortSyncTest.mainB1B10) {
            blockchainA.tryToConnect(b);
            if (b.isEqual(b5))
                break;

        }
        for (Block b : ShortSyncTest.mainB1B10) {
            blockchainB.tryToConnect(b);
            if (b.isEqual(b9))
                break;

        }
        // A == b5, B == b9
        final CountDownLatch semaphore = new CountDownLatch(1);
        ethereumB.addListener(new EthereumListenerAdapter() {
            @Override
            public void onBlock(Block block, List<TransactionReceipt> receipts) {
                if (block.isEqual(ShortSyncTest.b10)) {
                    semaphore.countDown();
                }
            }
        });
        ethA.sendNewBlockHashes(b5);
        for (Block b : ShortSyncTest.mainB1B10) {
            blockchainA.tryToConnect(b);
        }
        // A == b10
        ethA.sendNewBlock(ShortSyncTest.b10);
        semaphore.await(10, TimeUnit.SECONDS);
        // check if B == b10
        if ((semaphore.getCount()) > 0) {
            Assert.fail("PeerB bestBlock is incorrect");
        }
    }

    // negative gap, A on fork, B on main
    // negative gap, A on fork, B on fork (same story)
    // expected: B downloads A's fork and imports it as NOT_BEST => B on its chain
    @Test
    public void test5() throws InterruptedException {
        setupPeers();
        Block b9 = ShortSyncTest.mainB1B10.get(8);
        // A == B == genesis
        Blockchain blockchainA = ((Blockchain) (ethereumA.getBlockchain()));
        Blockchain blockchainB = ((Blockchain) (ethereumB.getBlockchain()));
        for (Block b : ShortSyncTest.forkB1B5B8_) {
            blockchainA.tryToConnect(b);
        }
        for (Block b : ShortSyncTest.mainB1B10) {
            blockchainB.tryToConnect(b);
            if (b.isEqual(b9))
                break;

        }
        // A == b8', B == b9
        final CountDownLatch semaphore = new CountDownLatch(1);
        final CountDownLatch semaphoreB8_ = new CountDownLatch(1);
        ethereumB.addListener(new EthereumListenerAdapter() {
            @Override
            public void onBlock(Block block, List<TransactionReceipt> receipts) {
                if (block.isEqual(ShortSyncTest.b10)) {
                    semaphore.countDown();
                }
                if (block.isEqual(ShortSyncTest.b8_)) {
                    semaphoreB8_.countDown();
                }
            }
        });
        ethA.sendNewBlockHashes(ShortSyncTest.b8_);
        semaphoreB8_.await(10, TimeUnit.SECONDS);
        if ((semaphoreB8_.getCount()) > 0) {
            Assert.fail("PeerB didn't import b8'");
        }
        for (Block b : ShortSyncTest.mainB1B10) {
            blockchainA.tryToConnect(b);
        }
        // A == b10
        ethA.sendNewBlock(ShortSyncTest.b10);
        semaphore.await(10, TimeUnit.SECONDS);
        // check if B == b10
        if ((semaphore.getCount()) > 0) {
            Assert.fail("PeerB bestBlock is incorrect");
        }
    }

    // negative gap, A on main, B on fork
    // expected: B finds common ancestor and downloads A's blocks => B on main
    @Test
    public void test6() throws InterruptedException {
        setupPeers();
        final Block b7 = ShortSyncTest.mainB1B10.get(6);
        // A == B == genesis
        Blockchain blockchainA = ((Blockchain) (ethereumA.getBlockchain()));
        Blockchain blockchainB = ((Blockchain) (ethereumB.getBlockchain()));
        for (Block b : ShortSyncTest.mainB1B10) {
            blockchainA.tryToConnect(b);
            if (b.isEqual(b7))
                break;

        }
        for (Block b : ShortSyncTest.forkB1B5B8_) {
            blockchainB.tryToConnect(b);
        }
        // A == b7, B == b8'
        final CountDownLatch semaphore = new CountDownLatch(1);
        final CountDownLatch semaphoreB7 = new CountDownLatch(1);
        ethereumB.addListener(new EthereumListenerAdapter() {
            @Override
            public void onBlock(Block block, List<TransactionReceipt> receipts) {
                if (block.isEqual(b7)) {
                    semaphoreB7.countDown();
                }
                if (block.isEqual(ShortSyncTest.b10)) {
                    semaphore.countDown();
                }
            }
        });
        ethA.sendNewBlockHashes(b7);
        semaphoreB7.await(10, TimeUnit.SECONDS);
        // check if B == b7
        if ((semaphoreB7.getCount()) > 0) {
            Assert.fail("PeerB didn't recover a gap");
        }
        for (Block b : ShortSyncTest.mainB1B10) {
            blockchainA.tryToConnect(b);
        }
        // A == b10
        ethA.sendNewBlock(ShortSyncTest.b10);
        semaphore.await(10, TimeUnit.SECONDS);
        // check if B == b10
        if ((semaphore.getCount()) > 0) {
            Assert.fail("PeerB bestBlock is incorrect");
        }
    }

    // positive gap, A on fork, B on main
    // A does a re-branch to main
    // expected: B downloads main blocks from A => B on main
    @Test
    public void test7() throws InterruptedException {
        setupPeers();
        Block b4 = ShortSyncTest.mainB1B10.get(3);
        // A == B == genesis
        final Blockchain blockchainA = ((Blockchain) (ethereumA.getBlockchain()));
        Blockchain blockchainB = ((Blockchain) (ethereumB.getBlockchain()));
        for (Block b : ShortSyncTest.forkB1B5B8_) {
            blockchainA.tryToConnect(b);
        }
        for (Block b : ShortSyncTest.mainB1B10) {
            blockchainB.tryToConnect(b);
            if (b.isEqual(b4))
                break;

        }
        // A == b8', B == b4
        ethereumB.addListener(new EthereumListenerAdapter() {
            @Override
            public void onRecvMessage(Channel channel, Message message) {
                if (message instanceof NewBlockMessage) {
                    // it's time to do a re-branch
                    for (Block b : ShortSyncTest.mainB1B10) {
                        blockchainA.tryToConnect(b);
                    }
                }
            }
        });
        final CountDownLatch semaphore = new CountDownLatch(1);
        ethereumB.addListener(new EthereumListenerAdapter() {
            @Override
            public void onBlock(Block block, List<TransactionReceipt> receipts) {
                if (block.isEqual(ShortSyncTest.b10)) {
                    semaphore.countDown();
                }
            }
        });
        ethA.sendNewBlock(ShortSyncTest.b8_);
        ethA.sendNewBlock(ShortSyncTest.b10);
        semaphore.await(10, TimeUnit.SECONDS);
        // check if B == b10
        if ((semaphore.getCount()) > 0) {
            Assert.fail("PeerB bestBlock is incorrect");
        }
    }

    // negative gap, A on fork, B on main
    // A does a re-branch to main
    // expected: B downloads A's fork and imports it as NOT_BEST => B on main
    @Test
    public void test8() throws InterruptedException {
        setupPeers();
        final Block b7_ = ShortSyncTest.forkB1B5B8_.get(6);
        Block b8 = ShortSyncTest.mainB1B10.get(7);
        // A == B == genesis
        final Blockchain blockchainA = ((Blockchain) (ethereumA.getBlockchain()));
        Blockchain blockchainB = ((Blockchain) (ethereumB.getBlockchain()));
        for (Block b : ShortSyncTest.forkB1B5B8_) {
            blockchainA.tryToConnect(b);
            if (b.isEqual(b7_))
                break;

        }
        for (Block b : ShortSyncTest.mainB1B10) {
            blockchainB.tryToConnect(b);
            if (b.isEqual(b8))
                break;

        }
        // A == b7', B == b8
        final CountDownLatch semaphore = new CountDownLatch(1);
        final CountDownLatch semaphoreB7_ = new CountDownLatch(1);
        ethereumB.addListener(new EthereumListenerAdapter() {
            @Override
            public void onBlock(Block block, List<TransactionReceipt> receipts) {
                if (block.isEqual(b7_)) {
                    // it's time to do a re-branch
                    for (Block b : ShortSyncTest.mainB1B10) {
                        blockchainA.tryToConnect(b);
                    }
                    semaphoreB7_.countDown();
                }
                if (block.isEqual(ShortSyncTest.b10)) {
                    semaphore.countDown();
                }
            }
        });
        ethA.sendNewBlockHashes(b7_);
        semaphoreB7_.await(10, TimeUnit.SECONDS);
        if ((semaphoreB7_.getCount()) > 0) {
            Assert.fail("PeerB didn't import b7'");
        }
        ethA.sendNewBlock(ShortSyncTest.b10);
        semaphore.await(10, TimeUnit.SECONDS);
        // check if B == b10
        if ((semaphore.getCount()) > 0) {
            Assert.fail("PeerB bestBlock is incorrect");
        }
    }

    // positive gap, A on fork, B on main
    // A doesn't send common ancestor
    // expected: B drops A and all its blocks => B on main
    @Test
    public void test9() throws InterruptedException {
        // handler which don't send an ancestor
        ShortSyncTest.SysPropConfigA.eth62 = new Eth62() {
            @Override
            protected void processGetBlockHeaders(GetBlockHeadersMessage msg) {
                // process init header request correctly
                if ((msg.getMaxHeaders()) == 1) {
                    super.processGetBlockHeaders(msg);
                    return;
                }
                List<BlockHeader> headers = new ArrayList<>();
                for (int i = 7; i < (ShortSyncTest.mainB1B10.size()); i++) {
                    headers.add(ShortSyncTest.mainB1B10.get(i).getHeader());
                }
                BlockHeadersMessage response = new BlockHeadersMessage(headers);
                sendMessage(response);
            }
        };
        setupPeers();
        Block b6 = ShortSyncTest.mainB1B10.get(5);
        // A == B == genesis
        final Blockchain blockchainA = ((Blockchain) (ethereumA.getBlockchain()));
        Blockchain blockchainB = ((Blockchain) (ethereumB.getBlockchain()));
        for (Block b : ShortSyncTest.forkB1B5B8_) {
            blockchainA.tryToConnect(b);
        }
        for (Block b : ShortSyncTest.mainB1B10) {
            blockchainB.tryToConnect(b);
            if (b.isEqual(b6))
                break;

        }
        // A == b8', B == b6
        ethA.sendNewBlock(ShortSyncTest.b8_);
        final CountDownLatch semaphoreDisconnect = new CountDownLatch(1);
        ethereumA.addListener(new EthereumListenerAdapter() {
            @Override
            public void onRecvMessage(Channel channel, Message message) {
                if (message instanceof DisconnectMessage) {
                    semaphoreDisconnect.countDown();
                }
            }
        });
        semaphoreDisconnect.await(10, TimeUnit.SECONDS);
        // check if peer was dropped
        if ((semaphoreDisconnect.getCount()) > 0) {
            Assert.fail("PeerA is not dropped");
        }
        // back to usual handler
        ShortSyncTest.SysPropConfigA.eth62 = null;
        for (Block b : ShortSyncTest.mainB1B10) {
            blockchainA.tryToConnect(b);
        }
        final CountDownLatch semaphore = new CountDownLatch(1);
        ethereumB.addListener(new EthereumListenerAdapter() {
            @Override
            public void onBlock(Block block, List<TransactionReceipt> receipts) {
                if (block.isEqual(ShortSyncTest.b10)) {
                    semaphore.countDown();
                }
            }
        });
        final CountDownLatch semaphoreConnect = new CountDownLatch(1);
        ethereumB.addListener(new EthereumListenerAdapter() {
            @Override
            public void onPeerAddedToSyncPool(Channel peer) {
                semaphoreConnect.countDown();
            }
        });
        ethereumB.connect(ShortSyncTest.nodeA);
        // await connection
        semaphoreConnect.await(10, TimeUnit.SECONDS);
        if ((semaphoreConnect.getCount()) > 0) {
            Assert.fail("PeerB is not able to connect to PeerA");
        }
        ethA.sendNewBlock(ShortSyncTest.b10);
        semaphore.await(10, TimeUnit.SECONDS);
        // check if B == b10
        if ((semaphore.getCount()) > 0) {
            Assert.fail("PeerB bestBlock is incorrect");
        }
    }

    // negative gap, A on fork, B on main
    // A doesn't send the gap block in ancestor response
    // expected: B drops A and all its blocks => B on main
    @Test
    public void test10() throws InterruptedException {
        // handler which don't send a gap block
        ShortSyncTest.SysPropConfigA.eth62 = new Eth62() {
            @Override
            protected void processGetBlockHeaders(GetBlockHeadersMessage msg) {
                if ((msg.getMaxHeaders()) == 1) {
                    super.processGetBlockHeaders(msg);
                    return;
                }
                List<BlockHeader> headers = new ArrayList<>();
                for (int i = 0; i < ((ShortSyncTest.forkB1B5B8_.size()) - 1); i++) {
                    headers.add(ShortSyncTest.forkB1B5B8_.get(i).getHeader());
                }
                BlockHeadersMessage response = new BlockHeadersMessage(headers);
                sendMessage(response);
            }
        };
        setupPeers();
        Block b9 = ShortSyncTest.mainB1B10.get(8);
        // A == B == genesis
        final Blockchain blockchainA = ((Blockchain) (ethereumA.getBlockchain()));
        Blockchain blockchainB = ((Blockchain) (ethereumB.getBlockchain()));
        for (Block b : ShortSyncTest.forkB1B5B8_) {
            blockchainA.tryToConnect(b);
        }
        for (Block b : ShortSyncTest.mainB1B10) {
            blockchainB.tryToConnect(b);
            if (b.isEqual(b9))
                break;

        }
        // A == b8', B == b9
        ethA.sendNewBlockHashes(ShortSyncTest.b8_);
        final CountDownLatch semaphoreDisconnect = new CountDownLatch(1);
        ethereumA.addListener(new EthereumListenerAdapter() {
            @Override
            public void onRecvMessage(Channel channel, Message message) {
                if (message instanceof DisconnectMessage) {
                    semaphoreDisconnect.countDown();
                }
            }
        });
        semaphoreDisconnect.await(10, TimeUnit.SECONDS);
        // check if peer was dropped
        if ((semaphoreDisconnect.getCount()) > 0) {
            Assert.fail("PeerA is not dropped");
        }
        // back to usual handler
        ShortSyncTest.SysPropConfigA.eth62 = null;
        final CountDownLatch semaphore = new CountDownLatch(1);
        ethereumB.addListener(new EthereumListenerAdapter() {
            @Override
            public void onBlock(Block block, List<TransactionReceipt> receipts) {
                if (block.isEqual(ShortSyncTest.b10)) {
                    semaphore.countDown();
                }
            }
        });
        final CountDownLatch semaphoreConnect = new CountDownLatch(1);
        ethereumB.addListener(new EthereumListenerAdapter() {
            @Override
            public void onPeerAddedToSyncPool(Channel peer) {
                semaphoreConnect.countDown();
            }
        });
        ethereumB.connect(ShortSyncTest.nodeA);
        // await connection
        semaphoreConnect.await(10, TimeUnit.SECONDS);
        if ((semaphoreConnect.getCount()) > 0) {
            Assert.fail("PeerB is not able to connect to PeerA");
        }
        for (Block b : ShortSyncTest.mainB1B10) {
            blockchainA.tryToConnect(b);
        }
        // A == b10
        ethA.sendNewBlock(ShortSyncTest.b10);
        semaphore.await(10, TimeUnit.SECONDS);
        // check if B == b10
        if ((semaphore.getCount()) > 0) {
            Assert.fail("PeerB bestBlock is incorrect");
        }
    }

    // A sends block with low TD to B
    // expected: B skips this block
    @Test
    public void test11() throws InterruptedException {
        Block b5 = ShortSyncTest.mainB1B10.get(4);
        final Block b6_ = ShortSyncTest.forkB1B5B8_.get(5);
        setupPeers();
        // A == B == genesis
        Blockchain blockchainA = ((Blockchain) (ethereumA.getBlockchain()));
        final Blockchain blockchainB = ((Blockchain) (ethereumB.getBlockchain()));
        for (Block b : ShortSyncTest.forkB1B5B8_) {
            blockchainA.tryToConnect(b);
        }
        for (Block b : ShortSyncTest.mainB1B10) {
            blockchainB.tryToConnect(b);
            if (b.isEqual(b5))
                break;

        }
        // A == b8', B == b5
        final CountDownLatch semaphore1 = new CountDownLatch(1);
        final CountDownLatch semaphore2 = new CountDownLatch(1);
        ethereumB.addListener(new EthereumListenerAdapter() {
            @Override
            public void onBlock(Block block, List<TransactionReceipt> receipts) {
                if (block.isEqual(b6_)) {
                    if ((semaphore1.getCount()) > 0) {
                        semaphore1.countDown();
                    } else {
                        semaphore2.countDown();
                    }
                }
            }
        });
        ethA.sendNewBlock(b6_);
        semaphore1.await(10, TimeUnit.SECONDS);
        if ((semaphore1.getCount()) > 0) {
            Assert.fail("PeerB doesn't accept block with higher TD");
        }
        for (Block b : ShortSyncTest.mainB1B10) {
            blockchainB.tryToConnect(b);
        }
        // B == b10
        ethA.sendNewBlock(b6_);
        semaphore2.await(5, TimeUnit.SECONDS);
        // check if B skips b6'
        if ((semaphore2.getCount()) == 0) {
            Assert.fail("PeerB doesn't skip block with lower TD");
        }
    }

    // bodies validation: A doesn't send bodies corresponding to headers which were sent previously
    // expected: B drops A
    @Test
    public void test12() throws InterruptedException {
        ShortSyncTest.SysPropConfigA.eth62 = new Eth62() {
            @Override
            protected void processGetBlockBodies(GetBlockBodiesMessage msg) {
                List<byte[]> bodies = Arrays.asList(ShortSyncTest.mainB1B10.get(0).getEncodedBody());
                BlockBodiesMessage response = new BlockBodiesMessage(bodies);
                sendMessage(response);
            }
        };
        setupPeers();
        Blockchain blockchainA = ((Blockchain) (ethereumA.getBlockchain()));
        for (Block b : ShortSyncTest.mainB1B10) {
            blockchainA.tryToConnect(b);
        }
        // A == b10, B == genesis
        final CountDownLatch semaphoreDisconnect = new CountDownLatch(1);
        ethereumA.addListener(new EthereumListenerAdapter() {
            @Override
            public void onRecvMessage(Channel channel, Message message) {
                if (message instanceof DisconnectMessage) {
                    semaphoreDisconnect.countDown();
                }
            }
        });
        ethA.sendNewBlock(ShortSyncTest.b10);
        semaphoreDisconnect.await(10, TimeUnit.SECONDS);
        // check if peer was dropped
        if ((semaphoreDisconnect.getCount()) > 0) {
            Assert.fail("PeerA is not dropped");
        }
    }

    // bodies validation: headers order is incorrect in the response, reverse = true
    // expected: B drops A
    @Test
    public void test13() throws InterruptedException {
        Block b9 = ShortSyncTest.mainB1B10.get(8);
        ShortSyncTest.SysPropConfigA.eth62 = new Eth62() {
            @Override
            protected void processGetBlockHeaders(GetBlockHeadersMessage msg) {
                if ((msg.getMaxHeaders()) == 1) {
                    super.processGetBlockHeaders(msg);
                    return;
                }
                List<BlockHeader> headers = Arrays.asList(ShortSyncTest.forkB1B5B8_.get(7).getHeader(), ShortSyncTest.forkB1B5B8_.get(6).getHeader(), ShortSyncTest.forkB1B5B8_.get(4).getHeader(), ShortSyncTest.forkB1B5B8_.get(5).getHeader());
                BlockHeadersMessage response = new BlockHeadersMessage(headers);
                sendMessage(response);
            }
        };
        setupPeers();
        Blockchain blockchainA = ((Blockchain) (ethereumA.getBlockchain()));
        Blockchain blockchainB = ((Blockchain) (ethereumB.getBlockchain()));
        for (Block b : ShortSyncTest.forkB1B5B8_) {
            blockchainA.tryToConnect(b);
        }
        for (Block b : ShortSyncTest.mainB1B10) {
            blockchainB.tryToConnect(b);
            if (b.isEqual(b9))
                break;

        }
        // A == b8', B == b10
        final CountDownLatch semaphoreDisconnect = new CountDownLatch(1);
        ethereumA.addListener(new EthereumListenerAdapter() {
            @Override
            public void onRecvMessage(Channel channel, Message message) {
                if (message instanceof DisconnectMessage) {
                    semaphoreDisconnect.countDown();
                }
            }
        });
        ethA.sendNewBlockHashes(ShortSyncTest.b8_);
        semaphoreDisconnect.await(10, TimeUnit.SECONDS);
        // check if peer was dropped
        if ((semaphoreDisconnect.getCount()) > 0) {
            Assert.fail("PeerA is not dropped");
        }
    }

    // bodies validation: ancestor's parent hash and header's hash does not match, reverse = true
    // expected: B drops A
    @Test
    public void test14() throws InterruptedException {
        Block b9 = ShortSyncTest.mainB1B10.get(8);
        ShortSyncTest.SysPropConfigA.eth62 = new Eth62() {
            @Override
            protected void processGetBlockHeaders(GetBlockHeadersMessage msg) {
                if ((msg.getMaxHeaders()) == 1) {
                    super.processGetBlockHeaders(msg);
                    return;
                }
                List<BlockHeader> headers = Arrays.asList(ShortSyncTest.forkB1B5B8_.get(7).getHeader(), ShortSyncTest.forkB1B5B8_.get(6).getHeader(), new BlockHeader(new byte[32], new byte[32], new byte[32], new byte[32], new byte[32], 6, new byte[]{ 0 }, 0, 0, new byte[0], new byte[0], new byte[0]), ShortSyncTest.forkB1B5B8_.get(4).getHeader());
                BlockHeadersMessage response = new BlockHeadersMessage(headers);
                sendMessage(response);
            }
        };
        setupPeers();
        Blockchain blockchainA = ((Blockchain) (ethereumA.getBlockchain()));
        Blockchain blockchainB = ((Blockchain) (ethereumB.getBlockchain()));
        for (Block b : ShortSyncTest.forkB1B5B8_) {
            blockchainA.tryToConnect(b);
        }
        for (Block b : ShortSyncTest.mainB1B10) {
            blockchainB.tryToConnect(b);
            if (b.isEqual(b9))
                break;

        }
        // A == b8', B == b10
        final CountDownLatch semaphoreDisconnect = new CountDownLatch(1);
        ethereumA.addListener(new EthereumListenerAdapter() {
            @Override
            public void onRecvMessage(Channel channel, Message message) {
                if (message instanceof DisconnectMessage) {
                    semaphoreDisconnect.countDown();
                }
            }
        });
        ethA.sendNewBlockHashes(ShortSyncTest.b8_);
        semaphoreDisconnect.await(10, TimeUnit.SECONDS);
        // check if peer was dropped
        if ((semaphoreDisconnect.getCount()) > 0) {
            Assert.fail("PeerA is not dropped");
        }
    }

    @Configuration
    @NoAutoscan
    public static class SysPropConfigA {
        static SystemProperties props = new SystemProperties();

        static Eth62 eth62 = null;

        @Bean
        public SystemProperties systemProperties() {
            return ShortSyncTest.SysPropConfigA.props;
        }

        @Bean
        @Scope("prototype")
        public Eth62 eth62() throws IllegalAccessException, InstantiationException {
            if ((ShortSyncTest.SysPropConfigA.eth62) != null)
                return ShortSyncTest.SysPropConfigA.eth62;

            return new Eth62();
        }
    }

    @Configuration
    @NoAutoscan
    public static class SysPropConfigB {
        static SystemProperties props = new SystemProperties();

        @Bean
        public SystemProperties systemProperties() {
            return ShortSyncTest.SysPropConfigB.props;
        }
    }
}

