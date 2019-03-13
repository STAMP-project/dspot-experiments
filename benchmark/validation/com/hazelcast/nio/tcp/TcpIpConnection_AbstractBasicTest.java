/**
 * Copyright (c) 2008-2019, Hazelcast, Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.hazelcast.nio.tcp;


import Packet.FLAG_URGENT;
import com.hazelcast.nio.Packet;
import com.hazelcast.test.AssertTask;
import com.hazelcast.test.HazelcastTestSupport;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


public abstract class TcpIpConnection_AbstractBasicTest extends TcpIpConnection_AbstractTest {
    // sleep time for lastWrite and lastRead tests
    private static final int LAST_READ_WRITE_SLEEP_SECONDS = 5;

    // if we make this MARGIN_OF_ERROR_MS very small, there is a high chance of spurious failures
    private static final int MARGIN_OF_ERROR_MS = 3000;

    private List<Packet> packetsB;

    @Test
    public void write_whenNonUrgent() {
        TcpIpConnection c = connect(networkingServiceA, addressB);
        Packet packet = new Packet(serializationService.toBytes("foo"));
        boolean result = c.write(packet);
        Assert.assertTrue(result);
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() {
                Assert.assertEquals(1, packetsB.size());
            }
        });
        Packet found = packetsB.get(0);
        Assert.assertEquals(packet, found);
    }

    @Test
    public void write_whenUrgent() {
        TcpIpConnection c = connect(networkingServiceA, addressB);
        Packet packet = new Packet(serializationService.toBytes("foo"));
        packet.raiseFlags(FLAG_URGENT);
        boolean result = c.write(packet);
        Assert.assertTrue(result);
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() {
                Assert.assertEquals(1, packetsB.size());
            }
        });
        Packet found = packetsB.get(0);
        Assert.assertEquals(packet, found);
    }

    @Test
    public void lastWriteTimeMillis_whenPacketWritten() {
        TcpIpConnection connAB = connect(networkingServiceA, addressB);
        // we need to sleep some so that the lastWriteTime of the connection gets nice and old.
        // we need this so we can determine the lastWriteTime got updated
        HazelcastTestSupport.sleepSeconds(TcpIpConnection_AbstractBasicTest.LAST_READ_WRITE_SLEEP_SECONDS);
        Packet packet = new Packet(serializationService.toBytes("foo"));
        connAB.write(packet);
        // wait for the packet to get written
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() {
                Assert.assertEquals(1, packetsB.size());
            }
        });
        long lastWriteTimeMs = connAB.lastWriteTimeMillis();
        long nowMs = System.currentTimeMillis();
        // make sure that the lastWrite time is within the given MARGIN_OF_ERROR_MS
        // last write time should be equal or smaller than now
        Assert.assertTrue(((("nowMs = " + nowMs) + ", lastWriteTimeMs = ") + lastWriteTimeMs), (lastWriteTimeMs <= nowMs));
        // last write time should be larger or equal than the now - MARGIN_OF_ERROR_MS
        Assert.assertTrue(((("nowMs = " + nowMs) + ", lastWriteTimeMs = ") + lastWriteTimeMs), (lastWriteTimeMs >= (nowMs - (TcpIpConnection_AbstractBasicTest.MARGIN_OF_ERROR_MS))));
    }

    @Test
    public void lastWriteTime_whenNothingWritten() {
        TcpIpConnection c = connect(networkingServiceA, addressB);
        long result1 = c.lastWriteTimeMillis();
        long result2 = c.lastWriteTimeMillis();
        Assert.assertEquals(result1, result2);
    }

    // we check the lastReadTimeMillis by sending a packet on the local connection, and
    // on the remote side we check the if the lastReadTime is updated
    @Test
    public void lastReadTimeMillis() {
        TcpIpConnection connAB = connect(networkingServiceA, addressB);
        TcpIpConnection connBA = connect(networkingServiceB, addressA);
        // we need to sleep some so that the lastReadTime of the connection gets nice and old.
        // we need this so we can determine the lastReadTime got updated
        HazelcastTestSupport.sleepSeconds(TcpIpConnection_AbstractBasicTest.LAST_READ_WRITE_SLEEP_SECONDS);
        Packet packet = new Packet(serializationService.toBytes("foo"));
        connAB.write(packet);
        // wait for the packet to get read
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() {
                Assert.assertEquals(1, packetsB.size());
                System.out.println("Packet processed");
            }
        });
        long lastReadTimeMs = connBA.lastReadTimeMillis();
        long nowMs = System.currentTimeMillis();
        // make sure that the lastRead time is within the given MARGIN_OF_ERROR_MS
        // last read time should be equal or smaller than now
        Assert.assertTrue(((("nowMs = " + nowMs) + ", lastReadTimeMs = ") + lastReadTimeMs), (lastReadTimeMs <= nowMs));
        // last read time should be larger or equal than the now - MARGIN_OF_ERROR_MS
        Assert.assertTrue(((("nowMs = " + nowMs) + ", lastReadTimeMs = ") + lastReadTimeMs), (lastReadTimeMs >= (nowMs - (TcpIpConnection_AbstractBasicTest.MARGIN_OF_ERROR_MS))));
    }

    @Test
    public void lastReadTime_whenNothingWritten() {
        TcpIpConnection c = connect(networkingServiceA, addressB);
        long result1 = c.lastReadTimeMillis();
        long result2 = c.lastReadTimeMillis();
        Assert.assertEquals(result1, result2);
    }

    @Test
    public void write_whenNotAlive() {
        TcpIpConnection c = connect(networkingServiceA, addressB);
        c.close(null, null);
        Packet packet = new Packet(serializationService.toBytes("foo"));
        boolean result = c.write(packet);
        Assert.assertFalse(result);
    }

    @Test
    public void getInetAddress() {
        TcpIpConnection c = connect(networkingServiceA, addressB);
        InetAddress result = c.getInetAddress();
        Assert.assertEquals(c.getChannel().socket().getInetAddress(), result);
    }

    @Test
    public void getRemoteSocketAddress() {
        TcpIpConnection c = connect(networkingServiceA, addressB);
        InetSocketAddress result = c.getRemoteSocketAddress();
        Assert.assertEquals(new InetSocketAddress(addressB.getHost(), addressB.getPort()), result);
    }

    @Test
    public void getPort() {
        TcpIpConnection c = connect(networkingServiceA, addressB);
        int result = c.getPort();
        Assert.assertEquals(c.getChannel().socket().getPort(), result);
    }

    @Test
    public void test_equals() {
        TcpIpConnection connAB = connect(networkingServiceA, addressB);
        TcpIpConnection connAC = connect(networkingServiceA, addressC);
        Assert.assertEquals(connAB, connAB);
        Assert.assertEquals(connAC, connAC);
        Assert.assertNotEquals(connAB, null);
        Assert.assertNotEquals(connAB, connAC);
        Assert.assertNotEquals(connAC, connAB);
        Assert.assertNotEquals(connAB, "foo");
    }
}

