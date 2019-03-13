/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.avro.ipc.netty;


import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.Charset;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.apache.avro.ipc.Server;
import org.apache.avro.ipc.Transceiver;
import org.apache.avro.ipc.specific.SpecificRequestor;
import org.apache.avro.test.Mail;
import org.apache.avro.test.Message;
import org.junit.Assert;
import org.junit.Test;


public class TestNettyServer {
    static final long CONNECT_TIMEOUT_MILLIS = 2000;// 2 sec


    private static Server server;

    private static Transceiver transceiver;

    private static Mail proxy;

    private static TestNettyServer.MailImpl mailService;

    public static class MailImpl implements Mail {
        private CountDownLatch allMessages = new CountDownLatch(5);

        // in this simple example just return details of the message
        public String send(Message message) {
            return ((((("Sent message to [" + (message.getTo())) + "] from [") + (message.getFrom())) + "] with body [") + (message.getBody())) + "]";
        }

        public void fireandforget(Message message) {
            allMessages.countDown();
        }

        private void awaitMessages() throws InterruptedException {
            allMessages.await(2, TimeUnit.SECONDS);
        }

        private void assertAllMessagesReceived() {
            Assert.assertEquals(0, allMessages.getCount());
        }

        public void reset() {
            allMessages = new CountDownLatch(5);
        }
    }

    @Test
    public void testRequestResponse() throws Exception {
        for (int x = 0; x < 5; x++) {
            verifyResponse(TestNettyServer.proxy.send(createMessage()));
        }
    }

    @Test
    public void testOneway() throws Exception {
        for (int x = 0; x < 5; x++) {
            TestNettyServer.proxy.fireandforget(createMessage());
        }
        TestNettyServer.mailService.awaitMessages();
        TestNettyServer.mailService.assertAllMessagesReceived();
    }

    @Test
    public void testMixtureOfRequests() throws Exception {
        TestNettyServer.mailService.reset();
        for (int x = 0; x < 5; x++) {
            Message createMessage = createMessage();
            TestNettyServer.proxy.fireandforget(createMessage);
            verifyResponse(TestNettyServer.proxy.send(createMessage));
        }
        TestNettyServer.mailService.awaitMessages();
        TestNettyServer.mailService.assertAllMessagesReceived();
    }

    @Test
    public void testConnectionsCount() throws Exception {
        Transceiver transceiver2 = new NettyTransceiver(new InetSocketAddress(TestNettyServer.server.getPort()), TestNettyServer.CONNECT_TIMEOUT_MILLIS);
        Mail proxy2 = SpecificRequestor.getClient(Mail.class, transceiver2);
        TestNettyServer.proxy.fireandforget(createMessage());
        proxy2.fireandforget(createMessage());
        Assert.assertEquals(2, getNumActiveConnections());
        transceiver2.close();
        // Check the active connections with some retries as closing at the client
        // side might not take effect on the server side immediately
        int numActiveConnections = ((NettyServer) (TestNettyServer.server)).getNumActiveConnections();
        for (int i = 0; (i < 50) && (numActiveConnections == 2); ++i) {
            System.out.println("Server still has 2 active connections; retrying...");
            Thread.sleep(100);
            numActiveConnections = ((NettyServer) (TestNettyServer.server)).getNumActiveConnections();
        }
        Assert.assertEquals(1, numActiveConnections);
    }

    // send a malformed request (HTTP) to the NettyServer port
    @Test
    public void testBadRequest() throws IOException {
        int port = TestNettyServer.server.getPort();
        String msg = "GET /status HTTP/1.1\n\n";
        InetSocketAddress sockAddr = new InetSocketAddress("127.0.0.1", port);
        Socket sock = new Socket();
        sock.connect(sockAddr);
        OutputStream out = sock.getOutputStream();
        out.write(msg.getBytes(Charset.forName("UTF-8")));
        out.flush();
        byte[] buf = new byte[2048];
        int bytesRead = sock.getInputStream().read(buf);
        Assert.assertTrue("Connection should have been closed", (bytesRead == (-1)));
    }
}

