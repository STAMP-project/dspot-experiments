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


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.WritableByteChannel;
import org.junit.Assert;
import org.junit.Test;


@SuppressWarnings("checkstyle:MagicNumber")
public class DatagramBufferTest {
    @Test
    public void testSimple() throws IOException {
        ByteBuffer datagram = DatagramBufferTest.createDatagram(5);
        DatagramBuffer datagramBuffer = new DatagramBuffer(9);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        WritableByteChannel channel = Channels.newChannel(bos);
        datagramBuffer.readFrom(datagram);
        datagramBuffer.writeTo(channel);
        byte[] result = bos.toByteArray();
        Assert.assertArrayEquals(datagram.array(), result);
    }

    @Test
    public void testDatagramBoundaries() throws IOException {
        DatagramBuffer datagramBuffer = new DatagramBuffer(32);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        WritableByteChannel channel = Channels.newChannel(bos);
        ByteBuffer datagram5 = DatagramBufferTest.createDatagram(5);
        ByteBuffer datagram0 = DatagramBufferTest.createDatagram(0);
        ByteBuffer datagram3 = DatagramBufferTest.createDatagram(3);
        ByteBuffer datagram4 = DatagramBufferTest.createDatagram(4);
        datagramBuffer.readFrom(datagram5);
        datagramBuffer.readFrom(datagram0);
        datagramBuffer.readFrom(datagram3);
        datagramBuffer.readFrom(datagram4);
        datagramBuffer.writeTo(channel);
        byte[] result = bos.toByteArray();
        Assert.assertArrayEquals(datagram5.array(), result);
        bos.reset();
        datagramBuffer.writeTo(channel);
        result = bos.toByteArray();
        Assert.assertArrayEquals(datagram0.array(), result);
        bos.reset();
        datagramBuffer.writeTo(channel);
        result = bos.toByteArray();
        Assert.assertArrayEquals(datagram3.array(), result);
        bos.reset();
        datagramBuffer.writeTo(channel);
        result = bos.toByteArray();
        Assert.assertArrayEquals(datagram4.array(), result);
    }

    @Test
    public void testCircular() throws IOException {
        ByteBuffer datagram5 = DatagramBufferTest.createDatagram(5);
        ByteBuffer datagram3 = DatagramBufferTest.createDatagram(3);
        DatagramBuffer datagramBuffer = new DatagramBuffer(14);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        WritableByteChannel channel = Channels.newChannel(bos);
        // write and consume 10 bytes
        datagramBuffer.readFrom(DatagramBufferTest.createDatagram(10));
        datagramBuffer.writeTo(Channels.newChannel(new ByteArrayOutputStream()));// forget

        // DatagramBuffer is expected to store the whole datagram (even if it exceeds its "capacity")
        datagramBuffer.readFrom(datagram5);
        datagramBuffer.readFrom(datagram3);
        datagramBuffer.writeTo(channel);
        byte[] result = bos.toByteArray();
        Assert.assertArrayEquals(datagram5.array(), result);
        bos.reset();
        datagramBuffer.writeTo(channel);
        result = bos.toByteArray();
        Assert.assertArrayEquals(datagram3.array(), result);
    }
}

