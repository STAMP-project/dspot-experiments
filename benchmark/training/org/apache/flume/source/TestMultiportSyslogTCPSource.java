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


import SyslogUtils.EVENT_STATUS;
import SyslogUtils.SyslogStatus.INVALID;
import com.google.common.base.Charsets;
import com.google.common.collect.Lists;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.UnknownHostException;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.security.cert.X509Certificate;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import javax.net.SocketFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import org.apache.flume.Channel;
import org.apache.flume.ChannelException;
import org.apache.flume.ChannelSelector;
import org.apache.flume.Context;
import org.apache.flume.Event;
import org.apache.flume.channel.ChannelProcessor;
import org.apache.flume.channel.MemoryChannel;
import org.apache.flume.channel.ReplicatingChannelSelector;
import org.apache.flume.conf.Configurables;
import org.apache.flume.instrumentation.SourceCounter;
import org.apache.flume.source.MultiportSyslogTCPSource.LineSplitter;
import org.apache.flume.source.MultiportSyslogTCPSource.MultiportSyslogHandler;
import org.apache.flume.source.MultiportSyslogTCPSource.ParsedBuffer;
import org.apache.flume.source.MultiportSyslogTCPSource.ThreadSafeDecoder;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.DefaultIoSessionDataStructureFactory;
import org.apache.mina.transport.socket.nio.NioSession;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.internal.util.reflection.Whitebox;


public class TestMultiportSyslogTCPSource {
    private final DateTime time = new DateTime();

    private final String stamp1 = time.toString();

    private final String host1 = "localhost.localdomain";

    private final String data1 = "proc1 - some msg";

    /**
     * Basic test to exercise multiple-port parsing.
     */
    @Test
    public void testMultiplePorts() throws IOException, ParseException {
        MultiportSyslogTCPSource source = new MultiportSyslogTCPSource();
        Channel channel = new MemoryChannel();
        List<Event> channelEvents = new ArrayList<>();
        int numPorts = 1000;
        List<Integer> portList = testNPorts(source, channel, channelEvents, numPorts, null, getSimpleEventSender(), new Context());
        // Since events can arrive out of order, search for each event in the array
        processEvents(channelEvents, numPorts, portList);
        source.stop();
    }

    /**
     * Basic test to exercise multiple-port parsing.
     */
    @Test
    public void testMultiplePortsSSL() throws Exception {
        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, new TrustManager[]{ new X509TrustManager() {
            @Override
            public void checkClientTrusted(X509Certificate[] certs, String s) {
                // nothing
            }

            @Override
            public void checkServerTrusted(X509Certificate[] certs, String s) {
                // nothing
            }

            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return new X509Certificate[0];
            }
        } }, null);
        SocketFactory socketFactory = sslContext.getSocketFactory();
        Context context = new Context();
        context.put("ssl", "true");
        context.put("keystore", "src/test/resources/server.p12");
        context.put("keystore-password", "password");
        context.put("keystore-type", "PKCS12");
        MultiportSyslogTCPSource source = new MultiportSyslogTCPSource();
        Channel channel = new MemoryChannel();
        List<Event> channelEvents = new ArrayList<>();
        int numPorts = 10;
        List<Integer> portList = testNPorts(source, channel, channelEvents, numPorts, null, getSSLEventSender(socketFactory), context);
        // Since events can arrive out of order, search for each event in the array
        processEvents(channelEvents, numPorts, portList);
        source.stop();
    }

    /**
     * Test the reassembly of a single line across multiple packets.
     */
    @Test
    public void testFragmented() throws CharacterCodingException {
        final int maxLen = 100;
        IoBuffer savedBuf = IoBuffer.allocate(maxLen);
        String origMsg = "<1>- - blah blam foo\n";
        IoBuffer buf1 = IoBuffer.wrap(origMsg.substring(0, 11).getBytes(Charsets.UTF_8));
        IoBuffer buf2 = IoBuffer.wrap(origMsg.substring(11, 16).getBytes(Charsets.UTF_8));
        IoBuffer buf3 = IoBuffer.wrap(origMsg.substring(16, 21).getBytes(Charsets.UTF_8));
        LineSplitter lineSplitter = new LineSplitter(maxLen);
        ParsedBuffer parsedLine = new ParsedBuffer();
        Assert.assertFalse("Incomplete line should not be parsed", lineSplitter.parseLine(buf1, savedBuf, parsedLine));
        Assert.assertFalse("Incomplete line should not be parsed", lineSplitter.parseLine(buf2, savedBuf, parsedLine));
        Assert.assertTrue("Completed line should be parsed", lineSplitter.parseLine(buf3, savedBuf, parsedLine));
        // the fragmented message should now be reconstructed
        Assert.assertEquals(origMsg.trim(), parsedLine.buffer.getString(Charsets.UTF_8.newDecoder()));
        parsedLine.buffer.rewind();
        MultiportSyslogHandler handler = new MultiportSyslogHandler(maxLen, 100, null, null, null, null, null, new ThreadSafeDecoder(Charsets.UTF_8), new ConcurrentHashMap<Integer, ThreadSafeDecoder>(), null);
        Event event = handler.parseEvent(parsedLine, Charsets.UTF_8.newDecoder());
        String body = new String(event.getBody(), Charsets.UTF_8);
        Assert.assertEquals("Event body incorrect", origMsg.trim().substring(7), body);
    }

    /**
     * Test parser handling of different character sets.
     */
    @Test
    public void testCharsetParsing() throws FileNotFoundException, IOException {
        String header = "<10>2012-08-11T01:01:01Z localhost ";
        String enBody = "Yarf yarf yarf";
        String enMsg = header + enBody;
        String frBody = "Comment " + ("\u00ea" + "tes-vous?");
        String frMsg = header + frBody;
        String esBody = "?C?mo est?s?";
        String esMsg = header + esBody;
        // defaults to UTF-8
        MultiportSyslogHandler handler = new MultiportSyslogHandler(1000, 10, new ChannelProcessor(new ReplicatingChannelSelector()), new SourceCounter("test"), null, null, null, new ThreadSafeDecoder(Charsets.UTF_8), new ConcurrentHashMap<Integer, ThreadSafeDecoder>(), null);
        ParsedBuffer parsedBuf = new ParsedBuffer();
        parsedBuf.incomplete = false;
        // should be able to encode/decode any of these messages in UTF-8 or ISO
        String[] bodies = new String[]{ enBody, esBody, frBody };
        String[] msgs = new String[]{ enMsg, esMsg, frMsg };
        Charset[] charsets = new Charset[]{ Charsets.UTF_8, Charsets.ISO_8859_1 };
        for (Charset charset : charsets) {
            for (int i = 0; i < (msgs.length); i++) {
                String msg = msgs[i];
                String body = bodies[i];
                parsedBuf.buffer = IoBuffer.wrap(msg.getBytes(charset));
                Event evt = handler.parseEvent(parsedBuf, charset.newDecoder());
                String result = new String(evt.getBody(), charset);
                // this doesn't work with non-UTF-8 chars... not sure why...
                Assert.assertEquals(((charset + " parse error: ") + msg), body, result);
                Assert.assertNull(evt.getHeaders().get(EVENT_STATUS));
            }
        }
        // Construct an invalid UTF-8 sequence.
        // The parser should still generate an Event, but mark it as INVALID.
        byte[] badUtf8Seq = enMsg.getBytes(Charsets.ISO_8859_1);
        int badMsgLen = badUtf8Seq.length;
        badUtf8Seq[(badMsgLen - 2)] = ((byte) (254));// valid ISO-8859-1, invalid UTF-8

        badUtf8Seq[(badMsgLen - 1)] = ((byte) (255));// valid ISO-8859-1, invalid UTF-8

        parsedBuf.buffer = IoBuffer.wrap(badUtf8Seq);
        Event evt = handler.parseEvent(parsedBuf, Charsets.UTF_8.newDecoder());
        Assert.assertEquals(((((("event body: " + (new String(evt.getBody(), Charsets.ISO_8859_1))) + " and my default charset = ") + (Charset.defaultCharset())) + " with event = ") + evt), INVALID.getSyslogStatus(), evt.getHeaders().get(EVENT_STATUS));
        Assert.assertArrayEquals("Raw message data should be kept in body of event", badUtf8Seq, evt.getBody());
        SourceCounter sc = ((SourceCounter) (Whitebox.getInternalState(handler, "sourceCounter")));
        Assert.assertEquals(1, sc.getEventReadFail());
    }

    @Test
    public void testHandlerGenericFail() throws Exception {
        // defaults to UTF-8
        MultiportSyslogHandler handler = new MultiportSyslogHandler(1000, 10, new ChannelProcessor(new ReplicatingChannelSelector()), new SourceCounter("test"), null, null, null, new ThreadSafeDecoder(Charsets.UTF_8), new ConcurrentHashMap<Integer, ThreadSafeDecoder>(), null);
        handler.exceptionCaught(null, new RuntimeException("dummy"));
        SourceCounter sc = ((SourceCounter) (Whitebox.getInternalState(handler, "sourceCounter")));
        Assert.assertEquals(1, sc.getGenericProcessingFail());
    }

    /**
     * Test that different charsets are parsed by different ports correctly.
     */
    @Test
    public void testPortCharsetHandling() throws Exception, UnknownHostException {
        // /////////////////////////////////////////////////////
        // port setup
        InetAddress localAddr = InetAddress.getLocalHost();
        DefaultIoSessionDataStructureFactory dsFactory = new DefaultIoSessionDataStructureFactory();
        // one faker on port 10001
        int port1 = 10001;
        NioSession session1 = Mockito.mock(NioSession.class);
        session1.setAttributeMap(dsFactory.getAttributeMap(session1));
        SocketAddress sockAddr1 = new InetSocketAddress(localAddr, port1);
        Mockito.when(session1.getLocalAddress()).thenReturn(sockAddr1);
        // another faker on port 10002
        int port2 = 10002;
        NioSession session2 = Mockito.mock(NioSession.class);
        session2.setAttributeMap(dsFactory.getAttributeMap(session2));
        SocketAddress sockAddr2 = new InetSocketAddress(localAddr, port2);
        Mockito.when(session2.getLocalAddress()).thenReturn(sockAddr2);
        // set up expected charsets per port
        ConcurrentMap<Integer, ThreadSafeDecoder> portCharsets = new ConcurrentHashMap<Integer, ThreadSafeDecoder>();
        portCharsets.put(port1, new ThreadSafeDecoder(Charsets.ISO_8859_1));
        portCharsets.put(port2, new ThreadSafeDecoder(Charsets.UTF_8));
        // /////////////////////////////////////////////////////
        // channel / source setup
        // set up channel to receive events
        MemoryChannel chan = new MemoryChannel();
        chan.configure(new Context());
        chan.start();
        ReplicatingChannelSelector sel = new ReplicatingChannelSelector();
        sel.setChannels(Lists.<Channel>newArrayList(chan));
        ChannelProcessor chanProc = new ChannelProcessor(sel);
        // defaults to UTF-8
        MultiportSyslogHandler handler = new MultiportSyslogHandler(1000, 10, chanProc, new SourceCounter("test"), null, null, null, new ThreadSafeDecoder(Charsets.UTF_8), portCharsets, null);
        // initialize buffers
        handler.sessionCreated(session1);
        handler.sessionCreated(session2);
        // /////////////////////////////////////////////////////
        // event setup
        // Create events of varying charsets.
        String header = "<10>2012-08-17T02:14:00-07:00 192.168.1.110 ";
        // These chars encode under ISO-8859-1 as illegal bytes under UTF-8.
        String dangerousChars = "????";
        // /////////////////////////////////////////////////////
        // encode and send them through the message handler
        String msg;
        IoBuffer buf;
        Event evt;
        // valid ISO-8859-1 on the right (ISO-8859-1) port
        msg = (header + dangerousChars) + "\n";
        buf = IoBuffer.wrap(msg.getBytes(Charsets.ISO_8859_1));
        handler.messageReceived(session1, buf);
        evt = TestMultiportSyslogTCPSource.takeEvent(chan);
        Assert.assertNotNull("Event vanished!", evt);
        Assert.assertNull(evt.getHeaders().get(EVENT_STATUS));
        // valid ISO-8859-1 on the wrong (UTF-8) port
        msg = (header + dangerousChars) + "\n";
        buf = IoBuffer.wrap(msg.getBytes(Charsets.ISO_8859_1));
        handler.messageReceived(session2, buf);
        evt = TestMultiportSyslogTCPSource.takeEvent(chan);
        Assert.assertNotNull("Event vanished!", evt);
        Assert.assertEquals("Expected invalid event due to character encoding", INVALID.getSyslogStatus(), evt.getHeaders().get(EVENT_STATUS));
        // valid UTF-8 on the right (UTF-8) port
        msg = (header + dangerousChars) + "\n";
        buf = IoBuffer.wrap(msg.getBytes(Charsets.UTF_8));
        handler.messageReceived(session2, buf);
        evt = TestMultiportSyslogTCPSource.takeEvent(chan);
        Assert.assertNotNull("Event vanished!", evt);
        Assert.assertNull(evt.getHeaders().get(EVENT_STATUS));
        SourceCounter sc = ((SourceCounter) (Whitebox.getInternalState(handler, "sourceCounter")));
        Assert.assertEquals(1, sc.getEventReadFail());
    }

    @Test
    public void testErrorCounterChannelWriteFail() throws Exception {
        MultiportSyslogTCPSource source = new MultiportSyslogTCPSource();
        Channel channel = new MemoryChannel();
        List<Event> channelEvents = new ArrayList<>();
        ChannelProcessor cp = Mockito.mock(ChannelProcessor.class);
        Mockito.doThrow(new ChannelException("dummy")).doNothing().when(cp).processEventBatch(ArgumentMatchers.anyListOf(Event.class));
        try {
            testNPorts(source, channel, channelEvents, 1, cp, getSimpleEventSender(), new Context());
        } catch (Exception e) {
            // 
        }
        SourceCounter sc = ((SourceCounter) (Whitebox.getInternalState(source, "sourceCounter")));
        Assert.assertEquals(1, sc.getChannelWriteFail());
        source.stop();
    }

    @Test
    public void testClientHeaders() throws IOException {
        String testClientIPHeader = "testClientIPHeader";
        String testClientHostnameHeader = "testClientHostnameHeader";
        MultiportSyslogTCPSource source = new MultiportSyslogTCPSource();
        Channel channel = new MemoryChannel();
        Configurables.configure(channel, new Context());
        List<Channel> channels = Lists.newArrayList();
        channels.add(channel);
        ChannelSelector rcs = new ReplicatingChannelSelector();
        rcs.setChannels(channels);
        source.setChannelProcessor(new ChannelProcessor(rcs));
        int port = TestMultiportSyslogTCPSource.getFreePort();
        Context context = new Context();
        context.put("host", InetAddress.getLoopbackAddress().getHostAddress());
        context.put("ports", String.valueOf(port));
        context.put("clientIPHeader", testClientIPHeader);
        context.put("clientHostnameHeader", testClientHostnameHeader);
        source.configure(context);
        source.start();
        // create a socket to send a test event
        Socket syslogSocket = new Socket(InetAddress.getLoopbackAddress().getHostAddress(), port);
        syslogSocket.getOutputStream().write(getEvent(0));
        Event e = TestMultiportSyslogTCPSource.takeEvent(channel);
        source.stop();
        Map<String, String> headers = e.getHeaders();
        TestMultiportSyslogTCPSource.checkHeader(headers, testClientIPHeader, InetAddress.getLoopbackAddress().getHostAddress());
        TestMultiportSyslogTCPSource.checkHeader(headers, testClientHostnameHeader, InetAddress.getLoopbackAddress().getHostName());
    }
}

