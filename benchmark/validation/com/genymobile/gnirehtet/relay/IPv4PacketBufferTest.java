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


import java.io.IOException;
import java.nio.ByteBuffer;
import org.junit.Assert;
import org.junit.Test;


@SuppressWarnings("checkstyle:MagicNumber")
public class IPv4PacketBufferTest {
    @Test
    public void testParseIPv4PacketBuffer() throws IOException {
        ByteBuffer buffer = IPv4PacketBufferTest.createMockPacket();
        IPv4PacketBuffer packetBuffer = new IPv4PacketBuffer();
        packetBuffer.readFrom(IPv4PacketBufferTest.contentToChannel(buffer));
        IPv4Packet packet = packetBuffer.asIPv4Packet();
        Assert.assertNotNull(packet);
        IPv4PacketBufferTest.checkPacketHeaders(packet);
    }

    @Test
    public void testParseFragmentedIPv4PacketBuffer() throws IOException {
        ByteBuffer buffer = IPv4PacketBufferTest.createMockPacket();
        IPv4PacketBuffer packetBuffer = new IPv4PacketBuffer();
        // onReadable the first 14 bytes
        buffer.limit(14);
        packetBuffer.readFrom(IPv4PacketBufferTest.contentToChannel(buffer));
        Assert.assertNull(packetBuffer.asIPv4Packet());
        // onReadable the remaining
        buffer.limit(32).position(14);
        packetBuffer.readFrom(IPv4PacketBufferTest.contentToChannel(buffer));
        IPv4Packet packet = packetBuffer.asIPv4Packet();
        Assert.assertNotNull(packet);
        IPv4PacketBufferTest.checkPacketHeaders(packet);
    }

    @Test
    public void testMultiPackets() throws IOException {
        ByteBuffer buffer = IPv4PacketBufferTest.createMockPackets();
        IPv4PacketBuffer packetBuffer = new IPv4PacketBuffer();
        packetBuffer.readFrom(IPv4PacketBufferTest.contentToChannel(buffer));
        for (int i = 0; i < 3; ++i) {
            IPv4Packet packet = packetBuffer.asIPv4Packet();
            Assert.assertNotNull(packet);
            IPv4PacketBufferTest.checkPacketHeaders(packet);
            packetBuffer.next();
        }
        // after the 3 packets have been consumed, there is nothing left
        Assert.assertNull(packetBuffer.asIPv4Packet());
    }
}

