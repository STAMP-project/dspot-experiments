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


import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import org.apache.activemq.util.Wait;
import org.eclipse.jetty.websocket.client.ClientUpgradeRequest;
import org.eclipse.jetty.websocket.client.WebSocketClient;
import org.fusesource.hawtbuf.UTF8Buffer;
import org.fusesource.mqtt.codec.CONNACK;
import org.fusesource.mqtt.codec.CONNECT;
import org.fusesource.mqtt.codec.MQTTFrame;
import org.fusesource.mqtt.codec.PINGREQ;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


@RunWith(Parameterized.class)
public class MQTTWSTransportTest extends WSTransportTestSupport {
    protected WebSocketClient wsClient;

    protected MQTTWSConnection wsMQTTConnection;

    protected ClientUpgradeRequest request;

    protected boolean partialFrames;

    public MQTTWSTransportTest(String testName, boolean partialFrames) {
        this.partialFrames = partialFrames;
    }

    @Test(timeout = 60000)
    public void testConnectCycles() throws Exception {
        for (int i = 0; i < 10; ++i) {
            testConnect();
            wsMQTTConnection = new MQTTWSConnection().setWritePartialFrames(partialFrames);
            wsClient.connect(wsMQTTConnection, wsConnectUri, request);
            if (!(wsMQTTConnection.awaitConnection(30, TimeUnit.SECONDS))) {
                throw new IOException("Could not connect to MQTT WS endpoint");
            }
        }
    }

    @Test(timeout = 60000)
    public void testConnect() throws Exception {
        wsMQTTConnection.connect();
        Assert.assertTrue("Connection should close", Wait.waitFor(new Wait.Condition() {
            @Override
            public boolean isSatisified() throws Exception {
                return (getProxyToBroker().getCurrentConnectionsCount()) == 1;
            }
        }));
        wsMQTTConnection.disconnect();
        wsMQTTConnection.close();
        Assert.assertTrue("Connection should close", Wait.waitFor(new Wait.Condition() {
            @Override
            public boolean isSatisified() throws Exception {
                return (getProxyToBroker().getCurrentConnectionsCount()) == 0;
            }
        }));
    }

    @Test(timeout = 60000)
    public void testConnectWithNoHeartbeatsClosesConnection() throws Exception {
        CONNECT command = new CONNECT();
        command.clientId(new UTF8Buffer(UUID.randomUUID().toString()));
        command.cleanSession(false);
        command.version(3);
        command.keepAlive(((short) (2)));
        wsMQTTConnection.sendFrame(command.encode());
        MQTTFrame received = wsMQTTConnection.receive(15, TimeUnit.SECONDS);
        if ((received == null) || ((received.messageType()) != (CONNACK.TYPE))) {
            Assert.fail("Client did not get expected CONNACK");
        }
        Assert.assertTrue("Connection should open", Wait.waitFor(new Wait.Condition() {
            @Override
            public boolean isSatisified() throws Exception {
                return (getProxyToBroker().getCurrentConnectionsCount()) == 1;
            }
        }));
        Assert.assertTrue("Connection should close", Wait.waitFor(new Wait.Condition() {
            @Override
            public boolean isSatisified() throws Exception {
                return (getProxyToBroker().getCurrentConnectionsCount()) == 0;
            }
        }));
        Assert.assertTrue("Client Connection should close", Wait.waitFor(new Wait.Condition() {
            @Override
            public boolean isSatisified() throws Exception {
                return !(wsMQTTConnection.isConnected());
            }
        }));
    }

    @Test(timeout = 60000)
    public void testConnectWithHeartbeatsKeepsConnectionAlive() throws Exception {
        final AtomicBoolean done = new AtomicBoolean();
        CONNECT command = new CONNECT();
        command.clientId(new UTF8Buffer(UUID.randomUUID().toString()));
        command.cleanSession(false);
        command.version(3);
        command.keepAlive(((short) (2)));
        wsMQTTConnection.sendFrame(command.encode());
        MQTTFrame received = wsMQTTConnection.receive(15, TimeUnit.SECONDS);
        if ((received == null) || ((received.messageType()) != (CONNACK.TYPE))) {
            Assert.fail("Client did not get expected CONNACK");
        }
        Thread pinger = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while (!(done.get())) {
                        TimeUnit.SECONDS.sleep(1);
                        wsMQTTConnection.sendFrame(new PINGREQ().encode());
                    } 
                } catch (Exception e) {
                }
            }
        });
        pinger.start();
        Assert.assertTrue("Connection should open", Wait.waitFor(new Wait.Condition() {
            @Override
            public boolean isSatisified() throws Exception {
                return (getProxyToBroker().getCurrentConnectionsCount()) == 1;
            }
        }));
        TimeUnit.SECONDS.sleep(10);
        Assert.assertTrue("Connection should still open", Wait.waitFor(new Wait.Condition() {
            @Override
            public boolean isSatisified() throws Exception {
                return (getProxyToBroker().getCurrentConnectionsCount()) == 1;
            }
        }));
        wsMQTTConnection.disconnect();
        wsMQTTConnection.close();
        done.set(true);
        Assert.assertTrue("Connection should close", Wait.waitFor(new Wait.Condition() {
            @Override
            public boolean isSatisified() throws Exception {
                return (getProxyToBroker().getCurrentConnectionsCount()) == 0;
            }
        }));
        Assert.assertTrue("Client Connection should close", Wait.waitFor(new Wait.Condition() {
            @Override
            public boolean isSatisified() throws Exception {
                return !(wsMQTTConnection.isConnected());
            }
        }));
    }
}

