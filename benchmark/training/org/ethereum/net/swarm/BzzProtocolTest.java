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
package org.ethereum.net.swarm;


import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import org.ethereum.crypto.HashUtil;
import org.ethereum.net.rlpx.Node;
import org.ethereum.net.swarm.bzz.BzzMessage;
import org.ethereum.net.swarm.bzz.BzzProtocol;
import org.ethereum.net.swarm.bzz.PeerAddress;
import org.junit.Assert;
import org.junit.Test;


/**
 * Created by Admin on 24.06.2015.
 */
public class BzzProtocolTest {
    interface Predicate<T> {
        boolean test(T t);
    }

    public static class FilterPrinter extends PrintWriter {
        String filter;

        BzzProtocolTest.Predicate<String> pFilter;

        public FilterPrinter(OutputStream out) {
            super(out, true);
        }

        @Override
        public void println(String x) {
            if (((pFilter) == null) || (pFilter.test(x))) {
                // if (filter == null || x.contains(filter)) {
                super.println(x);
            }
        }

        public void setFilter(final String filter) {
            pFilter = ( s) -> s.contains(filter);
        }

        public void setFilter(BzzProtocolTest.Predicate<String> pFilter) {
            this.pFilter = pFilter;
        }
    }

    static BzzProtocolTest.FilterPrinter stdout = new BzzProtocolTest.FilterPrinter(System.out);

    public static class TestPipe {
        protected Consumer<BzzMessage> out1;

        protected Consumer<BzzMessage> out2;

        protected String name1;

        protected String name2;

        public TestPipe(Consumer<BzzMessage> out1, Consumer<BzzMessage> out2) {
            this.out1 = out1;
            this.out2 = out2;
        }

        protected TestPipe() {
        }

        Consumer<BzzMessage> createIn1() {
            return ( bzzMessage) -> {
                BzzMessage smsg = serialize(bzzMessage);
                if (BzzProtocolTest.TestPeer.MessageOut) {
                    BzzProtocolTest.stdout.println(((((("+ " + (name1)) + " => ") + (name2)) + ": ") + smsg));
                }
                out2.accept(smsg);
            };
        }

        Consumer<BzzMessage> createIn2() {
            return ( bzzMessage) -> {
                BzzMessage smsg = serialize(bzzMessage);
                if (BzzProtocolTest.TestPeer.MessageOut) {
                    BzzProtocolTest.stdout.println(((((("+ " + (name2)) + " => ") + (name1)) + ": ") + smsg));
                }
                out1.accept(smsg);
            };
        }

        public void setNames(String name1, String name2) {
            this.name1 = name1;
            this.name2 = name2;
        }

        private BzzMessage serialize(BzzMessage msg) {
            try {
                return msg.getClass().getConstructor(byte[].class).newInstance(msg.getEncoded());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        public Consumer<BzzMessage> getOut1() {
            return out1;
        }

        public Consumer<BzzMessage> getOut2() {
            return out2;
        }
    }

    public static class TestAsyncPipe extends BzzProtocolTest.TestPipe {
        static ScheduledExecutorService exec = Executors.newScheduledThreadPool(32);

        static Queue<Future<?>> tasks = new LinkedBlockingQueue<>();

        class AsyncConsumer implements Consumer<BzzMessage> {
            Consumer<BzzMessage> delegate;

            boolean rev;

            public AsyncConsumer(Consumer<BzzMessage> delegate, boolean rev) {
                this.delegate = delegate;
                this.rev = rev;
            }

            @Override
            public void accept(final BzzMessage bzzMessage) {
                ScheduledFuture<?> future = BzzProtocolTest.TestAsyncPipe.exec.schedule(() -> {
                    try {
                        if (!(rev)) {
                            if (BzzProtocolTest.TestPeer.MessageOut) {
                                BzzProtocolTest.stdout.println(((((("- " + (name1)) + " => ") + (name2)) + ": ") + bzzMessage));
                            }
                        } else {
                            if (BzzProtocolTest.TestPeer.MessageOut) {
                                BzzProtocolTest.stdout.println(((((("- " + (name2)) + " => ") + (name1)) + ": ") + bzzMessage));
                            }
                        }
                        delegate.accept(bzzMessage);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }, channelLatencyMs, TimeUnit.MILLISECONDS);
                BzzProtocolTest.TestAsyncPipe.tasks.add(future);
            }
        }

        long channelLatencyMs = 2;

        public static void waitForCompletion() {
            try {
                while (!(BzzProtocolTest.TestAsyncPipe.tasks.isEmpty())) {
                    BzzProtocolTest.TestAsyncPipe.tasks.poll().get();
                } 
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        public TestAsyncPipe(Consumer<BzzMessage> out1, Consumer<BzzMessage> out2) {
            this.out1 = new BzzProtocolTest.TestAsyncPipe.AsyncConsumer(out1, false);
            this.out2 = new BzzProtocolTest.TestAsyncPipe.AsyncConsumer(out2, true);
        }
    }

    public static class SimpleHive extends Hive {
        Map<BzzProtocol, Object> peers = new IdentityHashMap<>();

        // PeerAddress thisAddress;
        BzzProtocolTest.TestPeer thisPeer;

        // NodeTable nodeTable;
        public SimpleHive(PeerAddress thisAddress) {
            super(thisAddress);
            // this.thisAddress = thisAddress;
            // nodeTable = new NodeTable(thisAddress.toNode());
        }

        public BzzProtocolTest.SimpleHive setThisPeer(BzzProtocolTest.TestPeer thisPeer) {
            this.thisPeer = thisPeer;
            return this;
        }

        @Override
        public void addPeer(BzzProtocol peer) {
            peers.put(peer, null);
            super.addPeer(peer);
            // nodeTable.addNode(peer.getNode().toNode());
            // peersAdded();
        }

        // @Override
        // public void removePeer(BzzProtocol peer) {
        // peers.remove(peer);
        // nodeTable.dropNode(peer.getNode().toNode());
        // }
        // 
        // @Override
        // public void addPeerRecords(BzzPeersMessage req) {
        // for (PeerAddress peerAddress : req.getPeers()) {
        // nodeTable.addNode(peerAddress.toNode());
        // }
        // peersAdded();
        // }
        @Override
        public Collection<PeerAddress> getNodes(Key key, int max) {
            List<Node> closestNodes = nodeTable.getClosestNodes(key.getBytes());
            ArrayList<PeerAddress> ret = new ArrayList<>();
            for (Node node : closestNodes) {
                ret.add(new PeerAddress(node));
                if ((--max) == 0)
                    break;

            }
            return ret;
        }

        @Override
        public Collection<BzzProtocol> getPeers(Key key, int maxCount) {
            if ((thisPeer) == null)
                return peers.keySet();

            // TreeMap<Key, TestPeer> sort = new TreeMap<>((o1, o2) -> {
            // for (int i = 0; i < o1.getBytes().length; i++) {
            // if (o1.getBytes()[i] > o2.getBytes()[i]) return 1;
            // if (o1.getBytes()[i] < o2.getBytes()[i]) return -1;
            // }
            // return 0;
            // });
            // for (TestPeer testPeer : TestPeer.staticMap.values()) {
            // if (thisPeer != testPeer) {
            // sort.put(distance(key, new Key(testPeer.peerAddress.getId())), testPeer);
            // }
            // }
            List<Node> closestNodes = nodeTable.getClosestNodes(key.getBytes());
            ArrayList<BzzProtocol> ret = new ArrayList<>();
            for (Node node : closestNodes) {
                ret.add(thisPeer.getPeer(new PeerAddress(node)));
                if ((--maxCount) == 0)
                    break;

            }
            return ret;
        }
    }

    public static class TestPeer {
        static Map<PeerAddress, BzzProtocolTest.TestPeer> staticMap = Collections.synchronizedMap(new HashMap<PeerAddress, BzzProtocolTest.TestPeer>());

        public static boolean MessageOut = false;

        public static boolean AsyncPipe = false;

        String name;

        PeerAddress peerAddress;

        LocalStore localStore;

        Hive hive;

        NetStore netStore;

        Map<Key, BzzProtocol> connections = new HashMap<>();

        public TestPeer(int num) {
            this(new PeerAddress(new byte[]{ 0, 0, ((byte) ((num >> 8) & 255)), ((byte) (num & 255)) }, (1000 + num), HashUtil.sha3(new byte[]{ ((byte) ((num >> 8) & 255)), ((byte) (num & 255)) })), ("" + num));
        }

        public TestPeer(PeerAddress peerAddress, String name) {
            this.name = name;
            this.peerAddress = peerAddress;
            localStore = new LocalStore(new MemStore(), new MemStore());
            hive = new BzzProtocolTest.SimpleHive(peerAddress).setThisPeer(this);
            netStore = new NetStore(localStore, hive);
            netStore.start(peerAddress);
            BzzProtocolTest.TestPeer.staticMap.put(peerAddress, this);
        }

        public BzzProtocol getPeer(PeerAddress addr) {
            Key peerKey = new Key(addr.getId());
            BzzProtocol protocol = connections.get(peerKey);
            if (protocol == null) {
                connect(BzzProtocolTest.TestPeer.staticMap.get(addr));
                protocol = connections.get(peerKey);
            }
            return protocol;
        }

        private BzzProtocol createPeerProtocol(PeerAddress addr) {
            Key peerKey = new Key(addr.getId());
            BzzProtocol protocol = connections.get(peerKey);
            if (protocol == null) {
                protocol = new BzzProtocol(netStore);
                connections.put(peerKey, protocol);
            }
            return protocol;
        }

        public void connect(BzzProtocolTest.TestPeer peer) {
            BzzProtocol myBzz = this.createPeerProtocol(peer.peerAddress);
            BzzProtocol peerBzz = peer.createPeerProtocol(peerAddress);
            BzzProtocolTest.TestPipe pipe = (BzzProtocolTest.TestPeer.AsyncPipe) ? new BzzProtocolTest.TestAsyncPipe(myBzz, peerBzz) : new BzzProtocolTest.TestPipe(myBzz, peerBzz);
            pipe.setNames(this.name, peer.name);
            System.out.println(((("Connecting: " + (this.name)) + " <=> ") + (peer.name)));
            myBzz.setMessageSender(pipe.createIn1());
            peerBzz.setMessageSender(pipe.createIn2());
            myBzz.start();
            peerBzz.start();
        }

        public void connect(PeerAddress addr) {
            BzzProtocolTest.TestPeer peer = BzzProtocolTest.TestPeer.staticMap.get(addr);
            if (peer != null) {
                connect(peer);
            }
        }
    }

    @Test
    public void simple3PeersTest() throws Exception {
        BzzProtocolTest.TestPeer.MessageOut = true;
        BzzProtocolTest.TestPeer.AsyncPipe = true;
        BzzProtocolTest.TestPeer p1 = new BzzProtocolTest.TestPeer(1);
        BzzProtocolTest.TestPeer p2 = new BzzProtocolTest.TestPeer(2);
        BzzProtocolTest.TestPeer p3 = new BzzProtocolTest.TestPeer(3);
        // TestPeer p4 = new TestPeer(4);
        System.out.println("Put chunk to 1");
        Key key = new Key(new byte[]{ 34, 51 });
        Chunk chunk = new Chunk(key, new byte[]{ 0, 0, 0, 0, 0, 0, 0, 0, 77, 88 });
        p1.netStore.put(chunk);
        System.out.println("Connect 1 <=> 2");
        p1.connect(p2);
        System.out.println("Connect 2 <=> 3");
        p2.connect(p3);
        // p2.connect(p4);
        // Thread.sleep(3000);
        System.err.println("Requesting chunk from 3...");
        Chunk chunk1 = p3.netStore.get(key);
        Assert.assertEquals(key, chunk1.getKey());
        Assert.assertArrayEquals(chunk.getData(), chunk1.getData());
    }

    @Test
    public void simpleTest() {
        PeerAddress peerAddress1 = new PeerAddress(new byte[]{ 0, 0, 0, 1 }, 1001, new byte[]{ 1 });
        PeerAddress peerAddress2 = new PeerAddress(new byte[]{ 0, 0, 0, 2 }, 1002, new byte[]{ 2 });
        LocalStore localStore1 = new LocalStore(new MemStore(), new MemStore());
        LocalStore localStore2 = new LocalStore(new MemStore(), new MemStore());
        Hive hive1 = new BzzProtocolTest.SimpleHive(peerAddress1);
        Hive hive2 = new BzzProtocolTest.SimpleHive(peerAddress2);
        NetStore netStore1 = new NetStore(localStore1, hive1);
        NetStore netStore2 = new NetStore(localStore2, hive2);
        netStore1.start(peerAddress1);
        netStore2.start(peerAddress2);
        BzzProtocol bzz1 = new BzzProtocol(netStore1);
        BzzProtocol bzz2 = new BzzProtocol(netStore2);
        BzzProtocolTest.TestPipe pipe = new BzzProtocolTest.TestPipe(bzz1, bzz2);
        pipe.setNames("1", "2");
        bzz1.setMessageSender(pipe.createIn1());
        bzz2.setMessageSender(pipe.createIn2());
        bzz1.start();
        bzz2.start();
        Key key = new Key(new byte[]{ 34, 51 });
        Chunk chunk = new Chunk(key, new byte[]{ 0, 0, 0, 0, 0, 0, 0, 0, 77, 88 });
        netStore1.put(chunk);
        // netStore1.put(chunk);
        localStore1.clean();
        Chunk chunk1 = netStore1.get(key);
        Assert.assertEquals(key, chunk1.getKey());
        Assert.assertArrayEquals(chunk.getData(), chunk1.getData());
    }
}

