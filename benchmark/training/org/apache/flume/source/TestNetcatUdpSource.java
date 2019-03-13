/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.flume.source;


import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import org.apache.flume.Channel;
import org.apache.flume.Event;
import org.apache.flume.Transaction;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class TestNetcatUdpSource {
    private static final Logger logger = LoggerFactory.getLogger(TestNetcatUdpSource.class);

    private NetcatUdpSource source;

    private Channel channel;

    private static final int TEST_NETCAT_PORT = 0;

    private final String shortString = "Lorem ipsum dolor sit amet.";

    private final String mediumString = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. " + "Nunc maximus rhoncus viverra. Nunc a metus.";

    @Test
    public void testLargePayload() throws Exception {
        init();
        source.start();
        // Write some message to the netcat port
        byte[] largePayload = getPayload(1000).getBytes();
        DatagramSocket socket;
        DatagramPacket datagramPacket;
        datagramPacket = new DatagramPacket(largePayload, 1000, InetAddress.getLocalHost(), source.getSourcePort());
        for (int i = 0; i < 10; i++) {
            socket = new DatagramSocket();
            socket.send(datagramPacket);
            socket.close();
        }
        List<Event> channelEvents = new ArrayList<Event>();
        Transaction txn = channel.getTransaction();
        txn.begin();
        for (int i = 0; i < 10; i++) {
            Event e = channel.take();
            Assert.assertNotNull(e);
            channelEvents.add(e);
        }
        try {
            txn.commit();
        } catch (Throwable t) {
            txn.rollback();
        } finally {
            txn.close();
        }
        source.stop();
        for (Event e : channelEvents) {
            Assert.assertNotNull(e);
            Assert.assertArrayEquals(largePayload, e.getBody());
        }
    }

    @Test
    public void testShortString() throws IOException {
        runUdpTest(shortString);
    }

    @Test
    public void testMediumString() throws IOException {
        runUdpTest(mediumString);
    }
}

