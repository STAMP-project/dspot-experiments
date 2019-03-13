/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hadoop.hdfs;


import com.google.common.collect.HashMultiset;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.ReadableByteChannel;
import org.apache.hadoop.hdfs.net.Peer;
import org.apache.hadoop.hdfs.protocol.DatanodeID;
import org.apache.hadoop.net.unix.DomainSocket;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class TestPeerCache {
    static final Logger LOG = LoggerFactory.getLogger(TestPeerCache.class);

    private static class FakePeer implements Peer {
        private boolean closed = false;

        private final boolean hasDomain;

        private final DatanodeID dnId;

        public FakePeer(DatanodeID dnId, boolean hasDomain) {
            this.dnId = dnId;
            this.hasDomain = hasDomain;
        }

        @Override
        public ReadableByteChannel getInputStreamChannel() {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setReadTimeout(int timeoutMs) throws IOException {
            throw new UnsupportedOperationException();
        }

        @Override
        public int getReceiveBufferSize() throws IOException {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean getTcpNoDelay() throws IOException {
            return false;
        }

        @Override
        public void setWriteTimeout(int timeoutMs) throws IOException {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean isClosed() {
            return closed;
        }

        @Override
        public void close() throws IOException {
            closed = true;
        }

        @Override
        public String getRemoteAddressString() {
            return dnId.getInfoAddr();
        }

        @Override
        public String getLocalAddressString() {
            return "127.0.0.1:123";
        }

        @Override
        public InputStream getInputStream() throws IOException {
            throw new UnsupportedOperationException();
        }

        @Override
        public OutputStream getOutputStream() throws IOException {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean isLocal() {
            return true;
        }

        @Override
        public String toString() {
            return ("FakePeer(dnId=" + (dnId)) + ")";
        }

        @Override
        public DomainSocket getDomainSocket() {
            if (!(hasDomain))
                return null;

            // Return a mock which throws an exception whenever any function is
            // called.
            return Mockito.mock(DomainSocket.class, new Answer<Object>() {
                @Override
                public Object answer(InvocationOnMock invocation) throws Throwable {
                    throw new RuntimeException("injected fault.");
                }
            });
        }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof TestPeerCache.FakePeer))
                return false;

            TestPeerCache.FakePeer other = ((TestPeerCache.FakePeer) (o));
            return ((hasDomain) == (other.hasDomain)) && (dnId.equals(other.dnId));
        }

        @Override
        public int hashCode() {
            return (dnId.hashCode()) ^ (hasDomain ? 1 : 0);
        }

        @Override
        public boolean hasSecureChannel() {
            return false;
        }
    }

    @Test
    public void testAddAndRetrieve() throws Exception {
        PeerCache cache = new PeerCache(3, 100000);
        DatanodeID dnId = new DatanodeID("192.168.0.1", "fakehostname", "fake_datanode_id", 100, 101, 102, 103);
        TestPeerCache.FakePeer peer = new TestPeerCache.FakePeer(dnId, false);
        cache.put(dnId, peer);
        Assert.assertTrue((!(peer.isClosed())));
        Assert.assertEquals(1, cache.size());
        Assert.assertEquals(peer, cache.get(dnId, false));
        Assert.assertEquals(0, cache.size());
        cache.close();
    }

    @Test
    public void testExpiry() throws Exception {
        final int CAPACITY = 3;
        final int EXPIRY_PERIOD = 10;
        PeerCache cache = new PeerCache(CAPACITY, EXPIRY_PERIOD);
        DatanodeID[] dnIds = new DatanodeID[CAPACITY];
        TestPeerCache.FakePeer[] peers = new TestPeerCache.FakePeer[CAPACITY];
        for (int i = 0; i < CAPACITY; ++i) {
            dnIds[i] = new DatanodeID("192.168.0.1", ("fakehostname_" + i), "fake_datanode_id", 100, 101, 102, 103);
            peers[i] = new TestPeerCache.FakePeer(dnIds[i], false);
        }
        for (int i = 0; i < CAPACITY; ++i) {
            cache.put(dnIds[i], peers[i]);
        }
        // Wait for the peers to expire
        Thread.sleep((EXPIRY_PERIOD * 50));
        Assert.assertEquals(0, cache.size());
        // make sure that the peers were closed when they were expired
        for (int i = 0; i < CAPACITY; ++i) {
            Assert.assertTrue(peers[i].isClosed());
        }
        // sleep for another second and see if
        // the daemon thread runs fine on empty cache
        Thread.sleep((EXPIRY_PERIOD * 50));
        cache.close();
    }

    @Test
    public void testEviction() throws Exception {
        final int CAPACITY = 3;
        PeerCache cache = new PeerCache(CAPACITY, 100000);
        DatanodeID[] dnIds = new DatanodeID[CAPACITY + 1];
        TestPeerCache.FakePeer[] peers = new TestPeerCache.FakePeer[CAPACITY + 1];
        for (int i = 0; i < (dnIds.length); ++i) {
            dnIds[i] = new DatanodeID("192.168.0.1", ("fakehostname_" + i), ("fake_datanode_id_" + i), 100, 101, 102, 103);
            peers[i] = new TestPeerCache.FakePeer(dnIds[i], false);
        }
        for (int i = 0; i < CAPACITY; ++i) {
            cache.put(dnIds[i], peers[i]);
        }
        // Check that the peers are cached
        Assert.assertEquals(CAPACITY, cache.size());
        // Add another entry and check that the first entry was evicted
        cache.put(dnIds[CAPACITY], peers[CAPACITY]);
        Assert.assertEquals(CAPACITY, cache.size());
        Assert.assertSame(null, cache.get(dnIds[0], false));
        // Make sure that the other entries are still there
        for (int i = 1; i < CAPACITY; ++i) {
            Peer peer = cache.get(dnIds[i], false);
            Assert.assertSame(peers[i], peer);
            Assert.assertTrue((!(peer.isClosed())));
            peer.close();
        }
        Assert.assertEquals(1, cache.size());
        cache.close();
    }

    @Test
    public void testMultiplePeersWithSameKey() throws Exception {
        final int CAPACITY = 3;
        PeerCache cache = new PeerCache(CAPACITY, 100000);
        DatanodeID dnId = new DatanodeID("192.168.0.1", "fakehostname", "fake_datanode_id", 100, 101, 102, 103);
        HashMultiset<TestPeerCache.FakePeer> peers = HashMultiset.create(CAPACITY);
        for (int i = 0; i < CAPACITY; ++i) {
            TestPeerCache.FakePeer peer = new TestPeerCache.FakePeer(dnId, false);
            peers.add(peer);
            cache.put(dnId, peer);
        }
        // Check that all of the peers ended up in the cache
        Assert.assertEquals(CAPACITY, cache.size());
        while (!(peers.isEmpty())) {
            Peer peer = cache.get(dnId, false);
            Assert.assertTrue((peer != null));
            Assert.assertTrue((!(peer.isClosed())));
            peers.remove(peer);
        } 
        Assert.assertEquals(0, cache.size());
        cache.close();
    }

    @Test
    public void testDomainSocketPeers() throws Exception {
        final int CAPACITY = 3;
        PeerCache cache = new PeerCache(CAPACITY, 100000);
        DatanodeID dnId = new DatanodeID("192.168.0.1", "fakehostname", "fake_datanode_id", 100, 101, 102, 103);
        HashMultiset<TestPeerCache.FakePeer> peers = HashMultiset.create(CAPACITY);
        for (int i = 0; i < CAPACITY; ++i) {
            TestPeerCache.FakePeer peer = new TestPeerCache.FakePeer(dnId, (i == (CAPACITY - 1)));
            peers.add(peer);
            cache.put(dnId, peer);
        }
        // Check that all of the peers ended up in the cache
        Assert.assertEquals(CAPACITY, cache.size());
        // Test that get(requireDomainPeer=true) finds the peer with the
        // domain socket.
        Peer peer = cache.get(dnId, true);
        Assert.assertTrue(((peer.getDomainSocket()) != null));
        peers.remove(peer);
        // Test that get(requireDomainPeer=true) returns null when there are
        // no more peers with domain sockets.
        peer = cache.get(dnId, true);
        Assert.assertTrue((peer == null));
        // Check that all of the other peers ended up in the cache.
        while (!(peers.isEmpty())) {
            peer = cache.get(dnId, false);
            Assert.assertTrue((peer != null));
            Assert.assertTrue((!(peer.isClosed())));
            peers.remove(peer);
        } 
        Assert.assertEquals(0, cache.size());
        cache.close();
    }
}

