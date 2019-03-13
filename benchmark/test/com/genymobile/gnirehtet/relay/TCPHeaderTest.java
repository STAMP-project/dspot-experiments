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


import java.nio.ByteBuffer;
import org.junit.Assert;
import org.junit.Test;

import static TCPHeader.FLAG_ACK;
import static TCPHeader.FLAG_FIN;


@SuppressWarnings("checkstyle:MagicNumber")
public class TCPHeaderTest {
    @Test
    public void testEditHeaders() {
        ByteBuffer buffer = TCPHeaderTest.createMockTCPHeader();
        TCPHeader header = new TCPHeader(buffer);
        header.setSourcePort(1111);
        header.setDestinationPort(2222);
        header.setSequenceNumber(300);
        header.setAcknowledgementNumber(101);
        header.setFlags(((FLAG_FIN) | (FLAG_ACK)));
        Assert.assertEquals(1111, header.getSourcePort());
        Assert.assertEquals(2222, header.getDestinationPort());
        Assert.assertEquals(300, header.getSequenceNumber());
        Assert.assertEquals(101, header.getAcknowledgementNumber());
        Assert.assertEquals(((FLAG_FIN) | (FLAG_ACK)), header.getFlags());
        // assert the buffer has been modified
        int sourcePort = Short.toUnsignedInt(buffer.getShort(0));
        int destinationPort = Short.toUnsignedInt(buffer.getShort(2));
        int sequenceNumber = buffer.getInt(4);
        int acknowledgementNumber = buffer.getInt(8);
        short dataOffsetAndFlags = buffer.getShort(12);
        Assert.assertEquals(1111, sourcePort);
        Assert.assertEquals(2222, destinationPort);
        Assert.assertEquals(300, sequenceNumber);
        Assert.assertEquals(101, acknowledgementNumber);
        Assert.assertEquals(20497, dataOffsetAndFlags);
        header.swapSourceAndDestination();
        Assert.assertEquals(2222, header.getSourcePort());
        Assert.assertEquals(1111, header.getDestinationPort());
        sourcePort = Short.toUnsignedInt(buffer.getShort(0));
        destinationPort = Short.toUnsignedInt(buffer.getShort(2));
        Assert.assertEquals(2222, sourcePort);
        Assert.assertEquals(1111, destinationPort);
    }

    @Test
    public void testComputeChecksum() {
        ByteBuffer buffer = TCPHeaderTest.createMockPacket();
        buffer.flip();
        IPv4Packet packet = new IPv4Packet(buffer);
        TCPHeader tcpHeader = ((TCPHeader) (packet.getTransportHeader()));
        // set a fake checksum value to assert that it is correctly computed
        buffer.putShort(36, ((short) (121)));
        tcpHeader.computeChecksum(packet.getIpv4Header(), packet.getPayload());
        // pseudo-header
        int sum = ((((4660 + 22136) + 41634) + 16962) + 6) + 24;
        // header
        sum += ((((((((4660 + 22136) + 0) + 273) + 0) + 546) + 20480) + 0) + 0) + 0;
        // payload
        sum += 4386 + 61183;
        while ((sum & (~65535)) != 0) {
            sum = (sum & 65535) + (sum >> 16);
        } 
        short checksum = ((short) (~sum));
        Assert.assertEquals(checksum, tcpHeader.getChecksum());
    }

    @Test
    public void testComputeChecksumOddLength() {
        ByteBuffer buffer = TCPHeaderTest.createMockOddPacket();
        buffer.flip();
        IPv4Packet packet = new IPv4Packet(buffer);
        TCPHeader tcpHeader = ((TCPHeader) (packet.getTransportHeader()));
        // set a fake checksum value to assert that it is correctly computed
        buffer.putShort(36, ((short) (121)));
        tcpHeader.computeChecksum(packet.getIpv4Header(), packet.getPayload());
        // pseudo-header
        int sum = ((((4660 + 22136) + 41634) + 16962) + 6) + 25;
        // header
        sum += ((((((((4660 + 22136) + 0) + 273) + 0) + 546) + 20480) + 0) + 0) + 0;
        // payload
        sum += (4386 + 61183) + 34816;
        while ((sum & (~65535)) != 0) {
            sum = (sum & 65535) + (sum >> 16);
        } 
        short checksum = ((short) (~sum));
        Assert.assertEquals(checksum, tcpHeader.getChecksum());
    }

    @Test
    public void testCopyTo() {
        ByteBuffer buffer = TCPHeaderTest.createMockTCPHeader();
        TCPHeader header = new TCPHeader(buffer);
        ByteBuffer target = ByteBuffer.allocate(40);
        target.position(12);
        TCPHeader copy = header.copyTo(target);
        copy.setSourcePort(9999);
        Assert.assertEquals(32, target.position());
        Assert.assertEquals("Header must modify target", 9999, target.getShort(12));
        Assert.assertEquals("Header must not modify buffer", 4660, buffer.getShort(0));
    }
}

