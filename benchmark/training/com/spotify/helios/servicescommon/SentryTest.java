/**
 * -
 * -\-\-
 * Helios Services
 * --
 * Copyright (C) 2016 Spotify AB
 * --
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * -/-/-
 */
package com.spotify.helios.servicescommon;


import com.spotify.helios.TemporaryPorts;
import com.spotify.helios.agent.AgentParser;
import com.spotify.helios.common.LoggingConfig;
import com.spotify.helios.master.MasterParser;
import com.spotify.logging.LoggingConfigurator;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class SentryTest {
    private static final Logger log = LoggerFactory.getLogger(SentryTest.class);

    @Rule
    public TemporaryPorts temporaryPorts = TemporaryPorts.create();

    private static final int UDP_SERVER_TIMEOUT = 60000;

    private int sentryPort;

    private String testDsn;

    @Test
    public void testMasterParserAndConfig() throws Exception {
        final String dsn = new MasterParser("--sentry-dsn", testDsn).getMasterConfig().getSentryDsn();
        Assert.assertEquals("wrong sentry DSN", testDsn, dsn);
    }

    @Test
    public void testAgentParserAndConfig() throws Exception {
        final String dsn = new AgentParser("--sentry-dsn", testDsn).getAgentConfig().getSentryDsn();
        Assert.assertEquals("wrong sentry DSN", testDsn, dsn);
    }

    @Test
    public void testSentryAppender() throws Exception {
        // start our UDP server which will receive sentry messages
        final SentryTest.UdpServer udpServer = new SentryTest.UdpServer(sentryPort);
        // turn on logging which enables the sentry appender
        final LoggingConfig config = new LoggingConfig(0, true, null, false);
        ServiceMain.setupLogging(config, testDsn);
        // log a message at error level so sentry appender sends it to UDP server
        SentryTest.log.error("Ignore test message printed by Helios SentryTest");
        // be nice and turn logging back off
        LoggingConfigurator.configureNoLogging();
        // make sure we got the message as expected, note that getMessage is a blocking method
        final String message = udpServer.getMessage();
        Assert.assertTrue(("Expected message beginning with 'Sentry', instead got " + message), message.startsWith("Sentry"));
    }

    /**
     * Simple Udp server which will listen on the specified port when constructed. It stores the
     * first message it receives, and then stops listening for more messages. The message can be
     * retrieved using the getMessage method. Both run and getMessage are synchronized so that
     * getMessage will block until the run method has exited, meaning we've either received a message
     * or the receive operation has timed out.
     */
    private static class UdpServer implements Runnable {
        private final DatagramSocket serverSocket;

        private String message;

        private UdpServer(int port) throws InterruptedException, SocketException {
            serverSocket = new DatagramSocket(port);
            serverSocket.setSoTimeout(SentryTest.UDP_SERVER_TIMEOUT);
            final Thread thread = new Thread(this);
            thread.setDaemon(true);
            thread.start();
        }

        @Override
        public synchronized void run() {
            final byte[] data = new byte[1024];
            final DatagramPacket packet = new DatagramPacket(data, data.length);
            try {
                serverSocket.receive(packet);
                message = new String(packet.getData()).trim();
            } catch (IOException e) {
                message = "Exception while receiving sentry call. " + (e.getMessage());
            }
        }

        public synchronized String getMessage() throws InterruptedException {
            return message;
        }
    }
}

