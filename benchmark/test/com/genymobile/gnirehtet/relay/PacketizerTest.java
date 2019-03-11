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


import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import org.junit.Assert;
import org.junit.Test;


@SuppressWarnings("checkstyle:MagicNumber")
public class PacketizerTest {
    @Test
    public void testMergeHeadersAndPayload() throws IOException {
        IPv4Packet referencePacket = new IPv4Packet(PacketizerTest.createMockPacket());
        IPv4Header ipv4Header = referencePacket.getIpv4Header();
        TransportHeader transportHeader = referencePacket.getTransportHeader();
        byte[] data = new byte[]{ 17, 34, 51, 68, 85, 102, 119, ((byte) (136)) };
        ReadableByteChannel channel = Channels.newChannel(new ByteArrayInputStream(data));
        Packetizer packetizer = new Packetizer(ipv4Header, transportHeader);
        IPv4Packet packet = packetizer.packetize(channel);
        Assert.assertEquals(36, packet.getIpv4Header().getTotalLength());
        ByteBuffer packetPayload = packet.getPayload();
        Assert.assertEquals(8, packetPayload.remaining());
        Assert.assertEquals(1234605616436508552L, packetPayload.getLong());
    }

    @Test
    public void testPacketizeChunks() throws IOException {
        IPv4Packet originalPacket = new IPv4Packet(PacketizerTest.createMockPacket());
        IPv4Header ipv4Header = originalPacket.getIpv4Header();
        TransportHeader transportHeader = originalPacket.getTransportHeader();
        byte[] data = new byte[]{ 17, 34, 51, 68, 85, 102, 119, ((byte) (136)) };
        ReadableByteChannel channel = Channels.newChannel(new ByteArrayInputStream(data));
        Packetizer packetizer = new Packetizer(ipv4Header, transportHeader);
        IPv4Packet packet = packetizer.packetize(channel, 2);
        ByteBuffer packetPayload = packet.getPayload();
        Assert.assertEquals(30, packet.getIpv4Header().getTotalLength());
        Assert.assertEquals(2, packetPayload.remaining());
        Assert.assertEquals(4386, Short.toUnsignedInt(packetPayload.getShort()));
        packet = packetizer.packetize(channel, 3);
        packetPayload = packet.getPayload();
        Assert.assertEquals(31, packet.getIpv4Header().getTotalLength());
        Assert.assertEquals(3, packetPayload.remaining());
        Assert.assertEquals(51, packetPayload.get());
        Assert.assertEquals(68, packetPayload.get());
        Assert.assertEquals(85, packetPayload.get());
        packet = packetizer.packetize(channel, 1024);
        packetPayload = packet.getPayload();
        Assert.assertEquals(31, packet.getIpv4Header().getTotalLength());
        Assert.assertEquals(3, packetPayload.remaining());
        Assert.assertEquals(102, packetPayload.get());
        Assert.assertEquals(119, packetPayload.get());
        Assert.assertEquals(((byte) (136)), packetPayload.get());
    }
}

