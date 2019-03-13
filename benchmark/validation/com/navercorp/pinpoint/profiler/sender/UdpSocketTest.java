/**
 * Copyright 2014 NAVER Corp.
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
package com.navercorp.pinpoint.profiler.sender;


import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.SocketUtils;


/**
 *
 *
 * @author emeroad
 */
@Ignore
public class UdpSocketTest {
    private final Logger logger = LoggerFactory.getLogger(this.getClass().getName());

    // port conflict against base port. so increased 5
    private int PORT = SocketUtils.findAvailableUdpPort(61112);

    // The correct maximum UDP message size is 65507, as determined by the following formula:
    // 0xffff - (sizeof(IP Header) + sizeof(UDP Header)) = 65535-(20+8) = 65507
    private static int AcceptedSize = 65507;

    private DatagramSocket receiver;

    private DatagramSocket sender;

    @Test
    public void testChunkSize() throws IOException {
        DatagramPacket packet1 = newDatagramPacket(1000);
        sender.send(packet1);
        DatagramPacket packet2 = newDatagramPacket(500);
        sender.send(packet2);
        DatagramPacket r1 = newDatagramPacket(2000);
        receiver.receive(r1);
        Assert.assertEquals(r1.getLength(), 1000);
        DatagramPacket r2 = newDatagramPacket(2000);
        receiver.receive(r2);
        Assert.assertEquals(r2.getLength(), 500);
    }

    @Test
    public void testDatagramSendFail() {
        int size = 70000;
        DatagramPacket packet1 = newDatagramPacket(size);
        try {
            sender.send(packet1);
            Assert.fail("expected fail, but succeed");
        } catch (IOException ignore) {
        }
    }

    @Test
    public void testDatagramMaxSend() throws IOException {
        DatagramPacket packet1 = newDatagramPacket(UdpSocketTest.AcceptedSize);
        sender.send(packet1);
        DatagramPacket r1 = newDatagramPacket(UdpSocketTest.AcceptedSize);
        receiver.receive(r1);
        Assert.assertEquals(r1.getLength(), UdpSocketTest.AcceptedSize);
    }

    @Test
    public void testMaxBytes() throws IOException {
        DatagramPacket packet1 = newDatagramPacket(50000);
        sender.send(packet1);
        DatagramPacket r1 = newDatagramPacket(50000);
        receiver.receive(r1);
        logger.debug("packetSize:{}", r1.getLength());
    }
}

