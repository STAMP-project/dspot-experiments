/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.activemq.transport.ws;


import Stomp.Commands;
import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import org.apache.activemq.transport.stomp.Stomp;
import org.apache.activemq.util.Wait;
import org.eclipse.jetty.websocket.client.WebSocketClient;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Test STOMP over WebSockets functionality.
 */
public class StompWSTransportTest extends WSTransportTestSupport {
    private static final Logger LOG = LoggerFactory.getLogger(StompWSTransportTest.class);

    protected WebSocketClient wsClient;

    protected StompWSConnection wsStompConnection;

    @Test(timeout = 60000)
    public void testConnect() throws Exception {
        String connectFrame = ("STOMP\n" + (((("login:system\n" + "passcode:manager\n") + "accept-version:1.2\n") + "host:localhost\n") + "\n")) + (Stomp.NULL);
        wsStompConnection.sendRawFrame(connectFrame);
        String incoming = wsStompConnection.receive(30, TimeUnit.SECONDS);
        Assert.assertNotNull(incoming);
        Assert.assertTrue(incoming.startsWith("CONNECTED"));
        Assert.assertEquals("v11.stomp", wsStompConnection.getConnection().getUpgradeResponse().getAcceptedSubProtocol());
        Assert.assertTrue("Connection should close", Wait.waitFor(new Wait.Condition() {
            @Override
            public boolean isSatisified() throws Exception {
                return (getProxyToBroker().getCurrentConnectionsCount()) == 1;
            }
        }));
        wsStompConnection.sendFrame(new org.apache.activemq.transport.stomp.StompFrame(Commands.DISCONNECT));
        wsStompConnection.close();
        Assert.assertTrue("Connection should close", Wait.waitFor(new Wait.Condition() {
            @Override
            public boolean isSatisified() throws Exception {
                return (getProxyToBroker().getCurrentConnectionsCount()) == 0;
            }
        }));
    }

    @Test(timeout = 60000)
    public void testConnectWithVersionOptions() throws Exception {
        String connectFrame = ("STOMP\n" + (((("login:system\n" + "passcode:manager\n") + "accept-version:1.0,1.1\n") + "host:localhost\n") + "\n")) + (Stomp.NULL);
        wsStompConnection.sendRawFrame(connectFrame);
        String incoming = wsStompConnection.receive(30, TimeUnit.SECONDS);
        Assert.assertTrue(incoming.startsWith("CONNECTED"));
        Assert.assertTrue(((incoming.indexOf("version:1.1")) >= 0));
        Assert.assertTrue(((incoming.indexOf("session:")) >= 0));
        wsStompConnection.sendFrame(new org.apache.activemq.transport.stomp.StompFrame(Commands.DISCONNECT));
        wsStompConnection.close();
    }

    @Test(timeout = 60000)
    public void testRejectInvalidHeartbeats1() throws Exception {
        String connectFrame = ("STOMP\n" + ((((("login:system\n" + "passcode:manager\n") + "accept-version:1.1\n") + "heart-beat:0\n") + "host:localhost\n") + "\n")) + (Stomp.NULL);
        wsStompConnection.sendRawFrame(connectFrame);
        try {
            String incoming = wsStompConnection.receive(30, TimeUnit.SECONDS);
            Assert.assertTrue(incoming.startsWith("ERROR"));
            Assert.assertTrue(((incoming.indexOf("heart-beat")) >= 0));
            Assert.assertTrue(((incoming.indexOf("message:")) >= 0));
        } catch (IOException ex) {
            StompWSTransportTest.LOG.debug("Connection closed before Frame was read.");
        }
        Assert.assertTrue("Connection should close", Wait.waitFor(new Wait.Condition() {
            @Override
            public boolean isSatisified() throws Exception {
                return (getProxyToBroker().getCurrentConnectionsCount()) == 0;
            }
        }));
    }

    @Test(timeout = 60000)
    public void testRejectInvalidHeartbeats2() throws Exception {
        String connectFrame = ("STOMP\n" + ((((("login:system\n" + "passcode:manager\n") + "accept-version:1.1\n") + "heart-beat:T,0\n") + "host:localhost\n") + "\n")) + (Stomp.NULL);
        wsStompConnection.sendRawFrame(connectFrame);
        try {
            String incoming = wsStompConnection.receive(30, TimeUnit.SECONDS);
            Assert.assertTrue(incoming.startsWith("ERROR"));
            Assert.assertTrue(((incoming.indexOf("heart-beat")) >= 0));
            Assert.assertTrue(((incoming.indexOf("message:")) >= 0));
        } catch (IOException ex) {
            StompWSTransportTest.LOG.debug("Connection closed before Frame was read.");
        }
        Assert.assertTrue("Connection should close", Wait.waitFor(new Wait.Condition() {
            @Override
            public boolean isSatisified() throws Exception {
                return (getProxyToBroker().getCurrentConnectionsCount()) == 0;
            }
        }));
    }

    @Test(timeout = 60000)
    public void testRejectInvalidHeartbeats3() throws Exception {
        String connectFrame = ("STOMP\n" + ((((("login:system\n" + "passcode:manager\n") + "accept-version:1.1\n") + "heart-beat:100,10,50\n") + "host:localhost\n") + "\n")) + (Stomp.NULL);
        wsStompConnection.sendRawFrame(connectFrame);
        try {
            String incoming = wsStompConnection.receive(30, TimeUnit.SECONDS);
            Assert.assertTrue(incoming.startsWith("ERROR"));
            Assert.assertTrue(((incoming.indexOf("heart-beat")) >= 0));
            Assert.assertTrue(((incoming.indexOf("message:")) >= 0));
        } catch (IOException ex) {
            StompWSTransportTest.LOG.debug("Connection closed before Frame was read.");
        }
        Assert.assertTrue("Connection should close", Wait.waitFor(new Wait.Condition() {
            @Override
            public boolean isSatisified() throws Exception {
                return (getProxyToBroker().getCurrentConnectionsCount()) == 0;
            }
        }));
    }

    @Test(timeout = 60000)
    public void testHeartbeatsDropsIdleConnection() throws Exception {
        String connectFrame = ("STOMP\n" + ((((("login:system\n" + "passcode:manager\n") + "accept-version:1.1\n") + "heart-beat:1000,0\n") + "host:localhost\n") + "\n")) + (Stomp.NULL);
        wsStompConnection.sendRawFrame(connectFrame);
        String incoming = wsStompConnection.receive(30, TimeUnit.SECONDS);
        Assert.assertTrue(incoming.startsWith("CONNECTED"));
        Assert.assertTrue(((incoming.indexOf("version:1.1")) >= 0));
        Assert.assertTrue(((incoming.indexOf("heart-beat:")) >= 0));
        Assert.assertTrue(((incoming.indexOf("session:")) >= 0));
        Assert.assertTrue("Broker should have closed WS connection:", Wait.waitFor(new Wait.Condition() {
            @Override
            public boolean isSatisified() throws Exception {
                return !(wsStompConnection.isConnected());
            }
        }));
    }

    @Test(timeout = 60000)
    public void testHeartbeatsKeepsConnectionOpen() throws Exception {
        String connectFrame = ("STOMP\n" + ((((("login:system\n" + "passcode:manager\n") + "accept-version:1.1\n") + "heart-beat:2000,0\n") + "host:localhost\n") + "\n")) + (Stomp.NULL);
        wsStompConnection.sendRawFrame(connectFrame);
        String incoming = wsStompConnection.receive(30, TimeUnit.SECONDS);
        Assert.assertTrue(incoming.startsWith("CONNECTED"));
        Assert.assertTrue(((incoming.indexOf("version:1.1")) >= 0));
        Assert.assertTrue(((incoming.indexOf("heart-beat:")) >= 0));
        Assert.assertTrue(((incoming.indexOf("session:")) >= 0));
        String message = (((("SEND\n" + "destination:/queue/") + (getTestName())) + "\n\n") + "Hello World") + (Stomp.NULL);
        wsStompConnection.sendRawFrame(message);
        ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
        service.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                try {
                    StompWSTransportTest.LOG.info("Sending next KeepAlive");
                    wsStompConnection.keepAlive();
                } catch (Exception e) {
                }
            }
        }, 1, 1, TimeUnit.SECONDS);
        TimeUnit.SECONDS.sleep(15);
        String frame = ((((("SUBSCRIBE\n" + "destination:/queue/") + (getTestName())) + "\n") + "id:12345\n") + "ack:auto\n\n") + (Stomp.NULL);
        wsStompConnection.sendRawFrame(frame);
        incoming = wsStompConnection.receive(30, TimeUnit.SECONDS);
        Assert.assertTrue(incoming.startsWith("MESSAGE"));
        service.shutdownNow();
        service.awaitTermination(5, TimeUnit.SECONDS);
        try {
            wsStompConnection.sendFrame(new org.apache.activemq.transport.stomp.StompFrame(Commands.DISCONNECT));
        } catch (Exception ex) {
            StompWSTransportTest.LOG.info("Caught exception on write of disconnect", ex);
        }
    }

    @Test(timeout = 60000)
    public void testEscapedHeaders() throws Exception {
        String connectFrame = ("STOMP\n" + ((((("login:system\n" + "passcode:manager\n") + "accept-version:1.1\n") + "heart-beat:0,0\n") + "host:localhost\n") + "\n")) + (Stomp.NULL);
        wsStompConnection.sendRawFrame(connectFrame);
        String incoming = wsStompConnection.receive(30, TimeUnit.SECONDS);
        Assert.assertTrue(incoming.startsWith("CONNECTED"));
        String message = (((("SEND\n" + "destination:/queue/") + (getTestName())) + "\nescaped-header:one\\ntwo\\cthree\n\n") + "Hello World") + (Stomp.NULL);
        wsStompConnection.sendRawFrame(message);
        String frame = ((((("SUBSCRIBE\n" + "destination:/queue/") + (getTestName())) + "\n") + "id:12345\n") + "ack:auto\n\n") + (Stomp.NULL);
        wsStompConnection.sendRawFrame(frame);
        incoming = wsStompConnection.receive(30, TimeUnit.SECONDS);
        Assert.assertTrue(incoming.startsWith("MESSAGE"));
        Assert.assertTrue(((incoming.indexOf("escaped-header:one\\ntwo\\cthree")) >= 0));
        try {
            wsStompConnection.sendFrame(new org.apache.activemq.transport.stomp.StompFrame(Commands.DISCONNECT));
        } catch (Exception ex) {
            StompWSTransportTest.LOG.info("Caught exception on write of disconnect", ex);
        }
    }
}

