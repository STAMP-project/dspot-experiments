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
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.flume.Channel;
import org.apache.flume.ChannelException;
import org.apache.flume.Context;
import org.apache.flume.Event;
import org.apache.flume.Transaction;
import org.apache.flume.channel.ChannelProcessor;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class TestSyslogUdpSource {
    private static final Logger logger = LoggerFactory.getLogger(TestSyslogUdpSource.class);

    private SyslogUDPSource source;

    private Channel channel;

    private static final int TEST_SYSLOG_PORT = 0;

    private final DateTime time = new DateTime();

    private final String stamp1 = time.toString();

    private final String host1 = "localhost.localdomain";

    private final String data1 = "test syslog data";

    private final String bodyWithHostname = ((host1) + " ") + (data1);

    private final String bodyWithTimestamp = ((stamp1) + " ") + (data1);

    private final String bodyWithTandH = (((("<10>" + (stamp1)) + " ") + (host1)) + " ") + (data1);

    @Test
    public void testLargePayload() throws Exception {
        init("true");
        source.start();
        // Write some message to the syslog port
        byte[] largePayload = getPayload(1000).getBytes();
        DatagramPacket datagramPacket = createDatagramPacket(largePayload);
        for (int i = 0; i < 10; i++) {
            sendDatagramPacket(datagramPacket);
        }
        List<Event> channelEvents = new ArrayList<>();
        Transaction txn = channel.getTransaction();
        txn.begin();
        for (int i = 0; i < 10; i++) {
            Event e = channel.take();
            Assert.assertNotNull(e);
            channelEvents.add(e);
        }
        commitAndCloseTransaction(txn);
        source.stop();
        for (Event e : channelEvents) {
            Assert.assertNotNull(e);
            Assert.assertArrayEquals(largePayload, e.getBody());
        }
    }

    @Test
    public void testKeepFields() throws IOException {
        runKeepFieldsTest("all");
        // Backwards compatibility
        runKeepFieldsTest("true");
    }

    @Test
    public void testRemoveFields() throws IOException {
        runKeepFieldsTest("none");
        // Backwards compatibility
        runKeepFieldsTest("false");
    }

    @Test
    public void testKeepHostname() throws IOException {
        runKeepFieldsTest("hostname");
    }

    @Test
    public void testKeepTimestamp() throws IOException {
        runKeepFieldsTest("timestamp");
    }

    @Test
    public void testSourceCounter() throws Exception {
        init("true");
        doCounterCommon();
        // Retrying up to 10 times while the acceptedCount == 0 because the event processing in
        // SyslogUDPSource is handled on a separate thread by Netty so message delivery,
        // thus the sourceCounter's increment can be delayed resulting in a flaky test
        for (int i = 0; (i < 10) && ((source.getSourceCounter().getEventAcceptedCount()) == 0); i++) {
            Thread.sleep(100);
        }
        Assert.assertEquals(1, source.getSourceCounter().getEventAcceptedCount());
        Assert.assertEquals(1, source.getSourceCounter().getEventReceivedCount());
    }

    @Test
    public void testSourceCounterChannelFail() throws Exception {
        init("true");
        ChannelProcessor cp = Mockito.mock(ChannelProcessor.class);
        Mockito.doThrow(new ChannelException("dummy")).when(cp).processEvent(ArgumentMatchers.any(Event.class));
        source.setChannelProcessor(cp);
        doCounterCommon();
        for (int i = 0; (i < 10) && ((source.getSourceCounter().getChannelWriteFail()) == 0); i++) {
            Thread.sleep(100);
        }
        Assert.assertEquals(1, source.getSourceCounter().getChannelWriteFail());
    }

    @Test
    public void testSourceCounterReadFail() throws Exception {
        init("true");
        ChannelProcessor cp = Mockito.mock(ChannelProcessor.class);
        Mockito.doThrow(new RuntimeException("dummy")).when(cp).processEvent(ArgumentMatchers.any(Event.class));
        source.setChannelProcessor(cp);
        doCounterCommon();
        for (int i = 0; (i < 10) && ((source.getSourceCounter().getEventReadFail()) == 0); i++) {
            Thread.sleep(100);
        }
        Assert.assertEquals(1, source.getSourceCounter().getEventReadFail());
    }

    @Test
    public void testClientHeaders() throws IOException {
        String testClientIPHeader = "testClientIPHeader";
        String testClientHostnameHeader = "testClientHostnameHeader";
        Context context = new Context();
        context.put("clientIPHeader", testClientIPHeader);
        context.put("clientHostnameHeader", testClientHostnameHeader);
        init("none", context);
        source.start();
        DatagramPacket datagramPacket = createDatagramPacket(bodyWithTandH.getBytes());
        sendDatagramPacket(datagramPacket);
        Transaction txn = channel.getTransaction();
        txn.begin();
        Event e = channel.take();
        commitAndCloseTransaction(txn);
        source.stop();
        Map<String, String> headers = e.getHeaders();
        TestSyslogUdpSource.checkHeader(headers, testClientIPHeader, InetAddress.getLoopbackAddress().getHostAddress());
        TestSyslogUdpSource.checkHeader(headers, testClientHostnameHeader, InetAddress.getLoopbackAddress().getHostName());
    }
}

