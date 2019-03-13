/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.nifi.processors.gettcp;


import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;


// Ignored for full build due to artificial delays given the
// multi-threaded nature of most of the tests. Please un-Ignore and run
// when working on changes
@Ignore
public class ReceivingClientTest {
    private static final byte EOM = '\r';

    private ScheduledExecutorService scheduler;

    @Test
    public void validateSuccessfullConnectionAndCommunication() throws Exception {
        int port = this.availablePort();
        String msgToSend = "Hello from validateSuccessfullConnectionAndCommunication";
        InetSocketAddress address = new InetSocketAddress(port);
        Server server = new Server(address, 1024, ReceivingClientTest.EOM);
        start();
        ReceivingClient client = new ReceivingClient(address, this.scheduler, 1024, ReceivingClientTest.EOM);
        StringBuilder stringBuilder = new StringBuilder();
        client.setMessageHandler(( fromAddress, message, partialMessage) -> stringBuilder.append(new String(message, StandardCharsets.UTF_8)));
        client.start();
        Assert.assertTrue(client.isRunning());
        this.sendToSocket(address, msgToSend);
        Thread.sleep(200);
        Assert.assertEquals("", stringBuilder.toString());
        this.sendToSocket(address, "\r");
        Thread.sleep(200);
        Assert.assertEquals((msgToSend + "\r"), stringBuilder.toString());
        client.stop();
        stop();
        Assert.assertFalse(client.isRunning());
        Assert.assertFalse(isRunning());
    }

    @Test
    public void validateSuccessfullConnectionAndCommunicationWithClientBufferSmallerThenMessage() throws Exception {
        int port = this.availablePort();
        String msgToSend = "Hello from validateSuccessfullConnectionAndCommunicationWithClientBufferSmallerThenMessage";
        InetSocketAddress address = new InetSocketAddress(port);
        Server server = new Server(address, 1024, ReceivingClientTest.EOM);
        start();
        ReceivingClient client = new ReceivingClient(address, this.scheduler, 64, ReceivingClientTest.EOM);
        List<String> messages = new ArrayList<>();
        client.setMessageHandler(( fromAddress, message, partialMessage) -> messages.add(new String(message, StandardCharsets.UTF_8)));
        client.start();
        Assert.assertTrue(client.isRunning());
        this.sendToSocket(address, msgToSend);
        this.sendToSocket(address, "\r");
        Thread.sleep(200);
        Assert.assertEquals("Hello from validateSuccessfullConnectionAndCommunicationWithClie", messages.get(0));
        Assert.assertEquals("ntBufferSmallerThenMessage\r", messages.get(1));
        client.stop();
        stop();
        Assert.assertFalse(client.isRunning());
        Assert.assertFalse(isRunning());
    }

    @Test
    public void validateMessageSendBeforeAfterClientConnectDisconnectNoEndOfMessageByte() throws Exception {
        int port = this.availablePort();
        String msgToSend = "Hello from validateMessageSendBeforeAfterClientConnectDisconnectNoEndOfMessageByte";
        InetSocketAddress address = new InetSocketAddress(port);
        Server server = new Server(address, 1024, ReceivingClientTest.EOM);
        start();
        this.sendToSocket(address, "foo");// validates no unexpected errors

        ReceivingClient client = new ReceivingClient(address, this.scheduler, 30, ReceivingClientTest.EOM);
        List<String> messages = new ArrayList<>();
        client.setMessageHandler(( fromAddress, message, partialMessage) -> messages.add(new String(message, StandardCharsets.UTF_8)));
        client.start();
        Assert.assertTrue(client.isRunning());
        this.sendToSocket(address, msgToSend);
        Thread.sleep(200);
        Assert.assertEquals(2, messages.size());
        Assert.assertEquals("Hello from validateMessageSend", messages.get(0));
        Assert.assertEquals("BeforeAfterClientConnectDiscon", messages.get(1));
        messages.clear();
        client.stop();
        this.sendToSocket(address, msgToSend);
        Thread.sleep(200);
        Assert.assertEquals(0, messages.size());
        this.sendToSocket(address, msgToSend);
        stop();
        Assert.assertFalse(client.isRunning());
        Assert.assertFalse(isRunning());
    }

    @Test
    public void validateReconnectDuringReceive() throws Exception {
        int port = this.availablePort();
        String msgToSend = "Hello from validateReconnectDuringReceive\r";
        InetSocketAddress addressMain = new InetSocketAddress(port);
        Server server = new Server(addressMain, 1024, ReceivingClientTest.EOM);
        start();
        ExecutorService sendingExecutor = Executors.newSingleThreadExecutor();
        ReceivingClient client = new ReceivingClient(addressMain, this.scheduler, 1024, ReceivingClientTest.EOM);
        client.setReconnectAttempts(10);
        client.setDelayMillisBeforeReconnect(1000);
        client.setMessageHandler(( fromAddress, message, partialMessage) -> System.out.println(new String(message)));
        client.start();
        Assert.assertTrue(client.isRunning());
        sendingExecutor.execute(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 10; i++) {
                    try {
                        sendToSocket(addressMain, msgToSend);
                        Thread.sleep(100);
                    } catch (Exception e) {
                        try {
                            Thread.sleep(1000);
                        } catch (Exception ex) {
                            // ignore
                        }
                    }
                }
            }
        });
        Thread.sleep(500);
        stop();
        Thread.sleep(500);
        start();
        Thread.sleep(1000);
        client.stop();
        stop();
        Assert.assertFalse(client.isRunning());
        Assert.assertFalse(isRunning());
    }
}

