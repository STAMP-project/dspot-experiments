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


import LifecycleState.STOP;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.channels.ServerSocketChannel;
import java.nio.charset.Charset;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.LineIterator;
import org.apache.flume.Channel;
import org.apache.flume.Context;
import org.apache.flume.FlumeException;
import org.apache.flume.conf.Configurables;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class TestNetcatSource {
    private static final Logger logger = LoggerFactory.getLogger(TestAvroSource.class);

    /**
     * Five first sentences of the Fables "The Crow and the Fox"
     * written by Jean de La Fontaine, French poet.
     *
     * @see <a href="http://en.wikipedia.org/wiki/Jean_de_La_Fontaine">Jean de La Fontaine on
    wikipedia</a>
     */
    private final String french = "Ma?tre Corbeau, sur un arbre perch?, " + ((("Tenait en son bec un fromage. " + "Ma?tre Renard, par l'odeur all?ch?, ") + "Lui tint ? peu pr?s ce langage : ") + "Et bonjour, Monsieur du Corbeau,");

    private final String english = "At the top of a tree perched Master Crow; " + ((("In his beak he was holding a cheese. " + "Drawn by the smell, Master Fox spoke, below. ") + "The words, more or less, were these: ") + "\"Hey, now, Sir Crow! Good day, good day!");

    private int selectedPort;

    private NetcatSource source;

    private Channel channel;

    private InetAddress localhost;

    private Charset defaultCharset = Charset.forName("UTF-8");

    /**
     * Test with UTF-16BE encoding Text with both french and english sentences
     *
     * @throws InterruptedException
     * 		
     * @throws IOException
     * 		
     */
    @Test
    public void testUTF16BEencoding() throws IOException, InterruptedException {
        String encoding = "UTF-16BE";
        startSource(encoding, "false", "1", "512");
        Socket netcatSocket = new Socket(localhost, selectedPort);
        try {
            // Test on english text snippet
            for (int i = 0; i < 20; i++) {
                sendEvent(netcatSocket, english, encoding);
                Assert.assertArrayEquals("Channel contained our event", english.getBytes(defaultCharset), getFlumeEvent());
            }
            // Test on french text snippet
            for (int i = 0; i < 20; i++) {
                sendEvent(netcatSocket, french, encoding);
                Assert.assertArrayEquals("Channel contained our event", french.getBytes(defaultCharset), getFlumeEvent());
            }
        } finally {
            netcatSocket.close();
            stopSource();
        }
    }

    /**
     * Test with UTF-16LE encoding Text with both french and english sentences
     *
     * @throws InterruptedException
     * 		
     * @throws IOException
     * 		
     */
    @Test
    public void testUTF16LEencoding() throws IOException, InterruptedException {
        String encoding = "UTF-16LE";
        startSource(encoding, "false", "1", "512");
        Socket netcatSocket = new Socket(localhost, selectedPort);
        try {
            // Test on english text snippet
            for (int i = 0; i < 20; i++) {
                sendEvent(netcatSocket, english, encoding);
                Assert.assertArrayEquals("Channel contained our event", english.getBytes(defaultCharset), getFlumeEvent());
            }
            // Test on french text snippet
            for (int i = 0; i < 20; i++) {
                sendEvent(netcatSocket, french, encoding);
                Assert.assertArrayEquals("Channel contained our event", french.getBytes(defaultCharset), getFlumeEvent());
            }
        } finally {
            netcatSocket.close();
            stopSource();
        }
    }

    /**
     * Test with UTF-8 encoding Text with both french and english sentences
     *
     * @throws InterruptedException
     * 		
     * @throws IOException
     * 		
     */
    @Test
    public void testUTF8encoding() throws IOException, InterruptedException {
        String encoding = "UTF-8";
        startSource(encoding, "false", "1", "512");
        Socket netcatSocket = new Socket(localhost, selectedPort);
        try {
            // Test on english text snippet
            for (int i = 0; i < 20; i++) {
                sendEvent(netcatSocket, english, encoding);
                Assert.assertArrayEquals("Channel contained our event", english.getBytes(defaultCharset), getFlumeEvent());
            }
            // Test on french text snippet
            for (int i = 0; i < 20; i++) {
                sendEvent(netcatSocket, french, encoding);
                Assert.assertArrayEquals("Channel contained our event", french.getBytes(defaultCharset), getFlumeEvent());
            }
        } finally {
            netcatSocket.close();
            stopSource();
        }
    }

    /**
     * Test with ISO-8859-1 encoding Text with both french and english sentences
     *
     * @throws InterruptedException
     * 		
     * @throws IOException
     * 		
     */
    @Test
    public void testIS88591encoding() throws IOException, InterruptedException {
        String encoding = "ISO-8859-1";
        startSource(encoding, "false", "1", "512");
        Socket netcatSocket = new Socket(localhost, selectedPort);
        try {
            // Test on english text snippet
            for (int i = 0; i < 20; i++) {
                sendEvent(netcatSocket, english, encoding);
                Assert.assertArrayEquals("Channel contained our event", english.getBytes(defaultCharset), getFlumeEvent());
            }
            // Test on french text snippet
            for (int i = 0; i < 20; i++) {
                sendEvent(netcatSocket, french, encoding);
                Assert.assertArrayEquals("Channel contained our event", french.getBytes(defaultCharset), getFlumeEvent());
            }
        } finally {
            netcatSocket.close();
            stopSource();
        }
    }

    /**
     * Test if an ack is sent for every event in the correct encoding
     *
     * @throws InterruptedException
     * 		
     * @throws IOException
     * 		
     */
    @Test
    public void testAck() throws IOException, InterruptedException {
        String encoding = "UTF-8";
        String ackEvent = "OK";
        startSource(encoding, "true", "1", "512");
        Socket netcatSocket = new Socket(localhost, selectedPort);
        LineIterator inputLineIterator = IOUtils.lineIterator(netcatSocket.getInputStream(), encoding);
        try {
            // Test on english text snippet
            for (int i = 0; i < 20; i++) {
                sendEvent(netcatSocket, english, encoding);
                Assert.assertArrayEquals("Channel contained our event", english.getBytes(defaultCharset), getFlumeEvent());
                Assert.assertEquals("Socket contained the Ack", ackEvent, inputLineIterator.nextLine());
            }
            // Test on french text snippet
            for (int i = 0; i < 20; i++) {
                sendEvent(netcatSocket, french, encoding);
                Assert.assertArrayEquals("Channel contained our event", french.getBytes(defaultCharset), getFlumeEvent());
                Assert.assertEquals("Socket contained the Ack", ackEvent, inputLineIterator.nextLine());
            }
        } finally {
            netcatSocket.close();
            stopSource();
        }
    }

    /**
     * Test that line above MaxLineLength are discarded
     *
     * @throws InterruptedException
     * 		
     * @throws IOException
     * 		
     */
    @Test
    public void testMaxLineLength() throws IOException, InterruptedException {
        String encoding = "UTF-8";
        startSource(encoding, "false", "1", "10");
        Socket netcatSocket = new Socket(localhost, selectedPort);
        try {
            sendEvent(netcatSocket, "123456789", encoding);
            Assert.assertArrayEquals("Channel contained our event", "123456789".getBytes(defaultCharset), getFlumeEvent());
            sendEvent(netcatSocket, english, encoding);
            Assert.assertEquals("Channel does not contain an event", null, getRawFlumeEvent());
        } finally {
            netcatSocket.close();
            stopSource();
        }
    }

    /**
     * Test that line above MaxLineLength are discarded
     *
     * @throws InterruptedException
     * 		
     * @throws IOException
     * 		
     */
    @Test
    public void testMaxLineLengthwithAck() throws IOException, InterruptedException {
        String encoding = "UTF-8";
        String ackEvent = "OK";
        String ackErrorEvent = "FAILED: Event exceeds the maximum length (10 chars, including newline)";
        startSource(encoding, "true", "1", "10");
        Socket netcatSocket = new Socket(localhost, selectedPort);
        LineIterator inputLineIterator = IOUtils.lineIterator(netcatSocket.getInputStream(), encoding);
        try {
            sendEvent(netcatSocket, "123456789", encoding);
            Assert.assertArrayEquals("Channel contained our event", "123456789".getBytes(defaultCharset), getFlumeEvent());
            Assert.assertEquals("Socket contained the Ack", ackEvent, inputLineIterator.nextLine());
            sendEvent(netcatSocket, english, encoding);
            Assert.assertEquals("Channel does not contain an event", null, getRawFlumeEvent());
            Assert.assertEquals("Socket contained the Error Ack", ackErrorEvent, inputLineIterator.nextLine());
        } finally {
            netcatSocket.close();
            stopSource();
        }
    }

    /**
     * Tests that the source is stopped when an exception is thrown
     * on port bind attempt due to port already being in use.
     *
     * @throws InterruptedException
     * 		
     */
    @Test
    public void testSourceStoppedOnFlumeException() throws IOException, InterruptedException {
        boolean isFlumeExceptionThrown = false;
        // create a dummy socket bound to a known port.
        try (ServerSocketChannel dummyServerSocket = ServerSocketChannel.open()) {
            dummyServerSocket.socket().setReuseAddress(true);
            dummyServerSocket.socket().bind(new InetSocketAddress("0.0.0.0", 10500));
            Context context = new Context();
            context.put("port", String.valueOf(10500));
            context.put("bind", "0.0.0.0");
            context.put("ack-every-event", "false");
            Configurables.configure(source, context);
            source.start();
        } catch (FlumeException fe) {
            isFlumeExceptionThrown = true;
        }
        // As port is already in use, an exception is thrown and the source is stopped
        // cleaning up the opened sockets during source.start().
        Assert.assertTrue("Flume exception is thrown as port already in use", isFlumeExceptionThrown);
        Assert.assertEquals("Server is stopped", STOP, source.getLifecycleState());
    }
}

