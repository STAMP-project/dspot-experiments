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
package org.apache.activemq.transport.wss;


import Stomp.Commands;
import java.io.IOException;
import java.net.URI;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import junit.framework.TestCase;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.transport.stomp.Stomp;
import org.apache.activemq.transport.ws.MQTTWSConnection;
import org.apache.activemq.transport.ws.StompWSConnection;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.client.ClientUpgradeRequest;
import org.eclipse.jetty.websocket.client.WebSocketClient;
import org.junit.Test;


public class WSSTransportNeedClientAuthTest {
    public static final String KEYSTORE_TYPE = "jks";

    public static final String PASSWORD = "password";

    public static final String TRUST_KEYSTORE = "src/test/resources/client.keystore";

    public static final String KEYSTORE = "src/test/resources/server.keystore";

    private BrokerService broker;

    @Test
    public void testStompNeedClientAuth() throws Exception {
        StompWSConnection wsStompConnection = new StompWSConnection();
        System.out.println("starting connection");
        SslContextFactory factory = new SslContextFactory();
        factory.setKeyStorePath(WSSTransportNeedClientAuthTest.KEYSTORE);
        factory.setKeyStorePassword(WSSTransportNeedClientAuthTest.PASSWORD);
        factory.setKeyStoreType(WSSTransportNeedClientAuthTest.KEYSTORE_TYPE);
        factory.setTrustStorePath(WSSTransportNeedClientAuthTest.TRUST_KEYSTORE);
        factory.setTrustStorePassword(WSSTransportNeedClientAuthTest.PASSWORD);
        factory.setTrustStoreType(WSSTransportNeedClientAuthTest.KEYSTORE_TYPE);
        WebSocketClient wsClient = new WebSocketClient(factory);
        wsClient.start();
        Future<Session> connected = wsClient.connect(wsStompConnection, new URI("wss://localhost:61618"));
        Session sess = connected.get(30, TimeUnit.SECONDS);
        String connectFrame = ("STOMP\n" + (((("login:system\n" + "passcode:manager\n") + "accept-version:1.2\n") + "host:localhost\n") + "\n")) + (Stomp.NULL);
        wsStompConnection.sendRawFrame(connectFrame);
        String incoming = wsStompConnection.receive(30, TimeUnit.SECONDS);
        TestCase.assertNotNull(incoming);
        TestCase.assertTrue(incoming.startsWith("CONNECTED"));
        wsStompConnection.sendFrame(new org.apache.activemq.transport.stomp.StompFrame(Commands.DISCONNECT));
        wsStompConnection.close();
    }

    @Test
    public void testMQTTNeedClientAuth() throws Exception {
        SslContextFactory factory = new SslContextFactory();
        factory.setKeyStorePath(WSSTransportNeedClientAuthTest.KEYSTORE);
        factory.setKeyStorePassword(WSSTransportNeedClientAuthTest.PASSWORD);
        factory.setKeyStoreType(WSSTransportNeedClientAuthTest.KEYSTORE_TYPE);
        factory.setTrustStorePath(WSSTransportNeedClientAuthTest.TRUST_KEYSTORE);
        factory.setTrustStorePassword(WSSTransportNeedClientAuthTest.PASSWORD);
        factory.setTrustStoreType(WSSTransportNeedClientAuthTest.KEYSTORE_TYPE);
        WebSocketClient wsClient = new WebSocketClient(factory);
        wsClient.start();
        ClientUpgradeRequest request = new ClientUpgradeRequest();
        request.setSubProtocols("mqttv3.1");
        MQTTWSConnection wsMQTTConnection = new MQTTWSConnection();
        wsClient.connect(wsMQTTConnection, new URI("wss://localhost:61618"), request);
        if (!(wsMQTTConnection.awaitConnection(30, TimeUnit.SECONDS))) {
            throw new IOException("Could not connect to MQTT WS endpoint");
        }
        wsMQTTConnection.connect();
        TestCase.assertTrue("Client not connected", wsMQTTConnection.isConnected());
        wsMQTTConnection.disconnect();
        wsMQTTConnection.close();
    }
}

