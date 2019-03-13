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


import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.ethereum.config.NoAutoscan;
import org.ethereum.config.SystemProperties;
import org.ethereum.facade.Ethereum;
import org.ethereum.facade.EthereumFactory;
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
public class LongSyncTest {
    private static Node nodeA;

    private static List<Block> mainB1B10;

    private static org.ethereum.core.Block b10;

    private Ethereum ethereumA;

    private Ethereum ethereumB;

    private EthHandler ethA;

    private String testDbA;

    private String testDbB;

    // general case, A has imported 10 blocks
    // expected: B downloads blocks from A => B synced
    @Test
    public void test1() throws InterruptedException {
        setupPeers();
        // A == b10, B == genesis
        final CountDownLatch semaphore = new CountDownLatch(1);
        ethereumB.addListener(new EthereumListenerAdapter() {
            @Override
            public void onBlock(Block block, List<TransactionReceipt> receipts) {
                if (block.isEqual(LongSyncTest.b10)) {
                    semaphore.countDown();
                }
            }
        });
        semaphore.await(40, TimeUnit.SECONDS);
        // check if B == b10
        if ((semaphore.getCount()) > 0) {
            Assert.fail("PeerB bestBlock is incorrect");
        }
    }

    // bodies validation: A doesn't send bodies for blocks lower than its best block
    // expected: B drops A
    @Test
    public void test2() throws InterruptedException {
        LongSyncTest.SysPropConfigA.eth62 = new Eth62() {
            @Override
            protected void processGetBlockBodies(GetBlockBodiesMessage msg) {
                List<byte[]> bodies = Arrays.asList(LongSyncTest.mainB1B10.get(0).getEncodedBody());
                BlockBodiesMessage response = new BlockBodiesMessage(bodies);
                sendMessage(response);
            }
        };
        setupPeers();
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
        semaphoreDisconnect.await(10, TimeUnit.SECONDS);
        // check if peer was dropped
        if ((semaphoreDisconnect.getCount()) > 0) {
            Assert.fail("PeerA is not dropped");
        }
    }

    // headers validation: headers count in A respond more than requested limit
    // expected: B drops A
    @Test
    public void test3() throws InterruptedException {
        LongSyncTest.SysPropConfigA.eth62 = new Eth62() {
            @Override
            protected void processGetBlockHeaders(GetBlockHeadersMessage msg) {
                if (Arrays.equals(msg.getBlockIdentifier().getHash(), LongSyncTest.b10.getHash())) {
                    super.processGetBlockHeaders(msg);
                    return;
                }
                List<BlockHeader> headers = Arrays.asList(LongSyncTest.mainB1B10.get(0).getHeader(), LongSyncTest.mainB1B10.get(1).getHeader(), LongSyncTest.mainB1B10.get(2).getHeader(), LongSyncTest.mainB1B10.get(3).getHeader());
                BlockHeadersMessage response = new BlockHeadersMessage(headers);
                sendMessage(response);
            }
        };
        setupPeers();
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
        semaphoreDisconnect.await(10, TimeUnit.SECONDS);
        // check if peer was dropped
        if ((semaphoreDisconnect.getCount()) > 0) {
            Assert.fail("PeerA is not dropped");
        }
    }

    // headers validation: A sends empty response
    // expected: B drops A
    @Test
    public void test4() throws InterruptedException {
        LongSyncTest.SysPropConfigA.eth62 = new Eth62() {
            @Override
            protected void processGetBlockHeaders(GetBlockHeadersMessage msg) {
                if (Arrays.equals(msg.getBlockIdentifier().getHash(), LongSyncTest.b10.getHash())) {
                    super.processGetBlockHeaders(msg);
                    return;
                }
                List<BlockHeader> headers = Collections.emptyList();
                BlockHeadersMessage response = new BlockHeadersMessage(headers);
                sendMessage(response);
            }
        };
        setupPeers();
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
        semaphoreDisconnect.await(10, TimeUnit.SECONDS);
        // check if peer was dropped
        if ((semaphoreDisconnect.getCount()) > 0) {
            Assert.fail("PeerA is not dropped");
        }
    }

    // headers validation: first header in response doesn't meet expectations
    // expected: B drops A
    @Test
    public void test5() throws InterruptedException {
        LongSyncTest.SysPropConfigA.eth62 = new Eth62() {
            @Override
            protected void processGetBlockHeaders(GetBlockHeadersMessage msg) {
                if (Arrays.equals(msg.getBlockIdentifier().getHash(), LongSyncTest.b10.getHash())) {
                    super.processGetBlockHeaders(msg);
                    return;
                }
                List<BlockHeader> headers = Arrays.asList(LongSyncTest.mainB1B10.get(1).getHeader(), LongSyncTest.mainB1B10.get(2).getHeader(), LongSyncTest.mainB1B10.get(3).getHeader());
                BlockHeadersMessage response = new BlockHeadersMessage(headers);
                sendMessage(response);
            }
        };
        setupPeers();
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
        semaphoreDisconnect.await(10, TimeUnit.SECONDS);
        // check if peer was dropped
        if ((semaphoreDisconnect.getCount()) > 0) {
            Assert.fail("PeerA is not dropped");
        }
    }

    // headers validation: first header in response doesn't meet expectations - second story
    // expected: B drops A
    @Test
    public void test6() throws InterruptedException {
        LongSyncTest.SysPropConfigA.eth62 = new Eth62() {
            @Override
            protected void processGetBlockHeaders(GetBlockHeadersMessage msg) {
                List<BlockHeader> headers = Collections.singletonList(LongSyncTest.mainB1B10.get(1).getHeader());
                BlockHeadersMessage response = new BlockHeadersMessage(headers);
                sendMessage(response);
            }
        };
        ethereumA = EthereumFactory.createEthereum(LongSyncTest.SysPropConfigA.props, LongSyncTest.SysPropConfigA.class);
        Blockchain blockchainA = ((Blockchain) (ethereumA.getBlockchain()));
        for (Block b : LongSyncTest.mainB1B10) {
            blockchainA.tryToConnect(b);
        }
        // A == b10
        ethereumB = EthereumFactory.createEthereum(LongSyncTest.SysPropConfigB.props, LongSyncTest.SysPropConfigB.class);
        ethereumB.connect(LongSyncTest.nodeA);
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
        semaphoreDisconnect.await(10, TimeUnit.SECONDS);
        // check if peer was dropped
        if ((semaphoreDisconnect.getCount()) > 0) {
            Assert.fail("PeerA is not dropped");
        }
    }

    // headers validation: headers order is incorrect, reverse = false
    // expected: B drops A
    @Test
    public void test7() throws InterruptedException {
        LongSyncTest.SysPropConfigA.eth62 = new Eth62() {
            @Override
            protected void processGetBlockHeaders(GetBlockHeadersMessage msg) {
                if (Arrays.equals(msg.getBlockIdentifier().getHash(), LongSyncTest.b10.getHash())) {
                    super.processGetBlockHeaders(msg);
                    return;
                }
                List<BlockHeader> headers = Arrays.asList(LongSyncTest.mainB1B10.get(0).getHeader(), LongSyncTest.mainB1B10.get(2).getHeader(), LongSyncTest.mainB1B10.get(1).getHeader());
                BlockHeadersMessage response = new BlockHeadersMessage(headers);
                sendMessage(response);
            }
        };
        setupPeers();
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
        semaphoreDisconnect.await(10, TimeUnit.SECONDS);
        // check if peer was dropped
        if ((semaphoreDisconnect.getCount()) > 0) {
            Assert.fail("PeerA is not dropped");
        }
    }

    // headers validation: ancestor's parent hash and header's hash does not match, reverse = false
    // expected: B drops A
    @Test
    public void test8() throws InterruptedException {
        LongSyncTest.SysPropConfigA.eth62 = new Eth62() {
            @Override
            protected void processGetBlockHeaders(GetBlockHeadersMessage msg) {
                if (Arrays.equals(msg.getBlockIdentifier().getHash(), LongSyncTest.b10.getHash())) {
                    super.processGetBlockHeaders(msg);
                    return;
                }
                List<BlockHeader> headers = Arrays.asList(LongSyncTest.mainB1B10.get(0).getHeader(), new BlockHeader(new byte[32], new byte[32], new byte[32], new byte[32], new byte[32], 2, new byte[]{ 0 }, 0, 0, new byte[0], new byte[0], new byte[0]), LongSyncTest.mainB1B10.get(2).getHeader());
                BlockHeadersMessage response = new BlockHeadersMessage(headers);
                sendMessage(response);
            }
        };
        setupPeers();
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
            return LongSyncTest.SysPropConfigA.props;
        }

        @Bean
        @Scope("prototype")
        public Eth62 eth62() throws IllegalAccessException, InstantiationException {
            if ((LongSyncTest.SysPropConfigA.eth62) != null)
                return LongSyncTest.SysPropConfigA.eth62;

            return new Eth62();
        }
    }

    @Configuration
    @NoAutoscan
    public static class SysPropConfigB {
        static SystemProperties props = new SystemProperties();

        @Bean
        public SystemProperties systemProperties() {
            return LongSyncTest.SysPropConfigB.props;
        }
    }
}

