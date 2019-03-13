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
package com.hazelcast.nio;


import com.hazelcast.nio.serialization.SerializationConcurrencyTest;
import com.hazelcast.test.HazelcastSerialClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.QuickTest;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


/**
 * Unit test that verifies that a packet can safely be stored in a byte-buffer and converted back
 * again into a packet.
 */
@RunWith(HazelcastSerialClassRunner.class)
@Category(QuickTest.class)
public class PacketIOHelperTest extends HazelcastTestSupport {
    private PacketIOHelper packetWriter;

    private PacketIOHelper packetReader;

    private final SerializationConcurrencyTest.Person person = new SerializationConcurrencyTest.Person(111, 123L, 89.56, "test-person", new SerializationConcurrencyTest.Address("street", 987));

    private final SerializationConcurrencyTest.PortablePerson portablePerson = new SerializationConcurrencyTest.PortablePerson(222, 456L, "portable-person", new SerializationConcurrencyTest.PortableAddress("street", 567));

    @Test
    public void testPacketWriteRead() throws IOException {
        testPacketWriteRead(person);
    }

    @Test
    public void testPacketWriteRead_usingPortable() throws IOException {
        testPacketWriteRead(portablePerson);
    }

    /**
     * Checks if the packet can deal with a buffer that is very small, but the data is very large, which
     * needs repeated calls to {@link PacketIOHelper#writeTo(Packet, ByteBuffer)} and
     * {@link PacketIOHelper#readFrom(ByteBuffer)}.
     */
    @Test
    public void largeValue() {
        Packet originalPacket = new Packet(HazelcastTestSupport.generateRandomString(100000).getBytes());
        Packet clonedPacket;
        ByteBuffer bb = ByteBuffer.allocate(20);
        boolean writeCompleted;
        do {
            writeCompleted = packetWriter.writeTo(originalPacket, bb);
            bb.flip();
            clonedPacket = packetReader.readFrom(bb);
            bb.clear();
        } while (!writeCompleted );
        Assert.assertNotNull(clonedPacket);
        PacketIOHelperTest.assertPacketEquals(originalPacket, clonedPacket);
    }

    @Test
    public void lotsOfPackets() {
        List<Packet> originalPackets = new LinkedList<Packet>();
        Random random = new Random();
        for (int k = 0; k < 1000; k++) {
            byte[] bytes = HazelcastTestSupport.generateRandomString(((random.nextInt(1000)) + 8)).getBytes();
            Packet originalPacket = new Packet(bytes);
            originalPackets.add(originalPacket);
        }
        ByteBuffer bb = ByteBuffer.allocate(20);
        for (Packet originalPacket : originalPackets) {
            Packet clonedPacket;
            boolean writeCompleted;
            do {
                writeCompleted = packetWriter.writeTo(originalPacket, bb);
                bb.flip();
                clonedPacket = packetReader.readFrom(bb);
                bb.clear();
            } while (!writeCompleted );
            Assert.assertNotNull(clonedPacket);
            PacketIOHelperTest.assertPacketEquals(originalPacket, clonedPacket);
        }
    }

    /**
     * Verifies that writing a Packet to a ByteBuffer and then reading it from the ByteBuffer, gives the same Packet (content).
     */
    @Test
    public void cloningOfPacket() {
        Packet originalPacket = new Packet("foobarbaz".getBytes());
        ByteBuffer bb = ByteBuffer.allocate(100);
        boolean written = packetWriter.writeTo(originalPacket, bb);
        Assert.assertTrue(written);
        bb.flip();
        Packet clonedPacket = packetReader.readFrom(bb);
        Assert.assertNotNull(clonedPacket);
        PacketIOHelperTest.assertPacketEquals(originalPacket, clonedPacket);
    }
}

