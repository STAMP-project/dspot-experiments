/**
 * Copyright (C) 2017 Genymobile
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.genymobile.gnirehtet.relay;


import IPv4Header.Protocol.UDP;
import java.nio.ByteBuffer;
import org.junit.Assert;
import org.junit.Test;


@SuppressWarnings("checkstyle:MagicNumber")
public class IPv4HeaderTest {
    @Test
    public void testReadIPVersionUnavailable() {
        ByteBuffer buffer = ByteBuffer.allocate(20);
        buffer.flip();
        int firstPacketVersion = IPv4Header.readVersion(buffer);
        Assert.assertEquals("IPv4 packet version must be unknown", (-1), firstPacketVersion);
    }

    @Test
    public void testReadIPVersionAvailable() {
        ByteBuffer buffer = ByteBuffer.allocate(20);
        byte versionAndIHL = (4 << 4) | 5;
        buffer.put(versionAndIHL);
        buffer.flip();
        int firstPacketVersion = IPv4Header.readVersion(buffer);
        Assert.assertEquals("Wrong IP version field value", 4, firstPacketVersion);
    }

    @Test
    public void testReadLengthUnavailable() {
        ByteBuffer buffer = ByteBuffer.allocate(20);
        buffer.flip();
        int firstPacketLength = IPv4Header.readLength(buffer);
        Assert.assertEquals("IPv4 packet length must be unknown", (-1), firstPacketLength);
    }

    @Test
    public void testReadLengthAvailable() {
        ByteBuffer buffer = ByteBuffer.allocate(20);
        buffer.put(2, ((byte) (1)));
        buffer.put(3, ((byte) (35)));
        buffer.position(20);// consider we wrote the whole header

        buffer.flip();
        int firstPacketLength = IPv4Header.readLength(buffer);
        Assert.assertEquals("Wrong IP length field value", 291, firstPacketLength);
    }

    @Test
    public void testParsePacketHeaders() {
        IPv4Header header = new IPv4Header(IPv4HeaderTest.createMockHeaders());
        Assert.assertNotNull("Valid IPv4 header not parsed", header);
        Assert.assertTrue(header.isSupported());
        Assert.assertEquals(UDP, header.getProtocol());
        Assert.assertEquals(20, header.getHeaderLength());
        Assert.assertEquals(28, header.getTotalLength());
    }

    @Test
    public void testEditHeaders() {
        ByteBuffer buffer = IPv4HeaderTest.createMockHeaders();
        IPv4Header header = new IPv4Header(buffer);
        header.setSource(-2023406815);
        header.setDestination(606348324);
        header.setTotalLength(42);
        Assert.assertEquals(-2023406815, header.getSource());
        Assert.assertEquals(606348324, header.getDestination());
        Assert.assertEquals(42, header.getTotalLength());
        // assert the buffer has been modified
        int source = buffer.getInt(12);
        int destination = buffer.getInt(16);
        int totalLength = Short.toUnsignedInt(buffer.getShort(2));
        Assert.assertEquals(-2023406815, source);
        Assert.assertEquals(606348324, destination);
        Assert.assertEquals(42, totalLength);
        header.swapSourceAndDestination();
        Assert.assertEquals(606348324, header.getSource());
        Assert.assertEquals(-2023406815, header.getDestination());
        source = buffer.getInt(12);
        destination = buffer.getInt(16);
        Assert.assertEquals(606348324, source);
        Assert.assertEquals(-2023406815, destination);
    }

    @Test
    public void testComputeChecksum() {
        ByteBuffer buffer = IPv4HeaderTest.createMockHeaders();
        IPv4Header header = new IPv4Header(buffer);
        // set a fake checksum value to assert that it is correctly computed
        buffer.putShort(10, ((short) (121)));
        header.computeChecksum();
        int sum = ((((((((17664 + 28) + 0) + 0) + 17) + 0) + 4660) + 22136) + 16962) + 16962;
        while ((sum & (~65535)) != 0) {
            sum = (sum & 65535) + (sum >> 16);
        } 
        short checksum = ((short) (~sum));
        Assert.assertEquals(checksum, header.getChecksum());
    }
}

