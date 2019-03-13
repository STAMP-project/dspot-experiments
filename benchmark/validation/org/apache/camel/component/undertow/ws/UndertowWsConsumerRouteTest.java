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
package org.apache.camel.component.undertow.ws;


import EventType.ONCLOSE;
import EventType.ONOPEN;
import UndertowConstants.CONNECTION_KEY;
import UndertowConstants.EVENT_TYPE_ENUM;
import java.io.InputStream;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.component.undertow.BaseUndertowTest;
import org.apache.camel.component.undertow.UndertowConstants.EventType;
import org.apache.camel.converter.IOConverter;
import org.asynchttpclient.AsyncHttpClient;
import org.asynchttpclient.DefaultAsyncHttpClient;
import org.asynchttpclient.ws.WebSocket;
import org.asynchttpclient.ws.WebSocketListener;
import org.asynchttpclient.ws.WebSocketUpgradeHandler;
import org.junit.Assert;
import org.junit.Test;


public class UndertowWsConsumerRouteTest extends BaseUndertowTest {
    private static final String CONNECTED_PREFIX = "connected ";

    private static final String BROADCAST_MESSAGE_PREFIX = "broadcast ";

    @Test
    public void wsClientSingleText() throws Exception {
        AsyncHttpClient c = new DefaultAsyncHttpClient();
        WebSocket websocket = c.prepareGet((("ws://localhost:" + (BaseUndertowTest.getPort())) + "/app1")).execute(new WebSocketUpgradeHandler.Builder().addWebSocketListener(new WebSocketListener() {
            @Override
            public void onTextFrame(String message, boolean finalFragment, int rsv) {
                System.out.println(("got message " + message));
            }

            @Override
            public void onOpen(WebSocket webSocket) {
            }

            @Override
            public void onClose(WebSocket webSocket, int code, String reason) {
            }

            @Override
            public void onError(Throwable t) {
                t.printStackTrace();
            }
        }).build()).get();
        MockEndpoint result = getMockEndpoint("mock:result1");
        result.expectedBodiesReceived("Test");
        websocket.sendTextFrame("Test");
        result.await(60, TimeUnit.SECONDS);
        result.assertIsSatisfied();
        websocket.sendCloseFrame();
        c.close();
    }

    @Test
    public void wsClientSingleTextStreaming() throws Exception {
        AsyncHttpClient c = new DefaultAsyncHttpClient();
        WebSocket websocket = c.prepareGet((("ws://localhost:" + (BaseUndertowTest.getPort())) + "/app2")).execute(new WebSocketUpgradeHandler.Builder().addWebSocketListener(new WebSocketListener() {
            @Override
            public void onTextFrame(String message, boolean finalFragment, int rsv) {
                System.out.println(("got message " + message));
            }

            @Override
            public void onOpen(WebSocket webSocket) {
            }

            @Override
            public void onClose(WebSocket webSocket, int code, String reason) {
            }

            @Override
            public void onError(Throwable t) {
                t.printStackTrace();
            }
        }).build()).get();
        MockEndpoint result = getMockEndpoint("mock:result2");
        result.expectedMessageCount(1);
        websocket.sendTextFrame("Test");
        result.await(60, TimeUnit.SECONDS);
        List<Exchange> exchanges = result.getReceivedExchanges();
        Assert.assertEquals(1, exchanges.size());
        Object body = result.getReceivedExchanges().get(0).getIn().getBody();
        Assert.assertTrue(("body is " + (body.getClass().getName())), (body instanceof Reader));
        Reader r = ((Reader) (body));
        Assert.assertEquals("Test", IOConverter.toString(r));
        websocket.sendCloseFrame();
        c.close();
    }

    @Test
    public void wsClientSingleBytes() throws Exception {
        AsyncHttpClient c = new DefaultAsyncHttpClient();
        WebSocket websocket = c.prepareGet((("ws://localhost:" + (BaseUndertowTest.getPort())) + "/app1")).execute(new WebSocketUpgradeHandler.Builder().addWebSocketListener(new WebSocketListener() {
            @Override
            public void onOpen(WebSocket webSocket) {
            }

            @Override
            public void onClose(WebSocket webSocket, int code, String reason) {
            }

            @Override
            public void onError(Throwable t) {
                t.printStackTrace();
            }

            @Override
            public void onBinaryFrame(byte[] message, boolean finalFragment, int rsv) {
                System.out.println("got byte[] message");
            }
        }).build()).get();
        MockEndpoint result = getMockEndpoint("mock:result1");
        final byte[] testmessage = "Test".getBytes("utf-8");
        result.expectedBodiesReceived(testmessage);
        websocket.sendBinaryFrame(testmessage);
        result.assertIsSatisfied();
        websocket.sendCloseFrame();
        c.close();
    }

    @Test
    public void wsClientSingleBytesStreaming() throws Exception {
        AsyncHttpClient c = new DefaultAsyncHttpClient();
        WebSocket websocket = c.prepareGet((("ws://localhost:" + (BaseUndertowTest.getPort())) + "/app2")).execute(new WebSocketUpgradeHandler.Builder().addWebSocketListener(new WebSocketListener() {
            @Override
            public void onBinaryFrame(byte[] message, boolean finalFragment, int rsv) {
                System.out.println(("got message " + message));
            }

            @Override
            public void onOpen(WebSocket webSocket) {
            }

            @Override
            public void onClose(WebSocket webSocket, int code, String reason) {
            }

            @Override
            public void onError(Throwable t) {
                t.printStackTrace();
            }
        }).build()).get();
        MockEndpoint result = getMockEndpoint("mock:result2");
        result.expectedMessageCount(1);
        final byte[] testmessage = "Test".getBytes("utf-8");
        websocket.sendBinaryFrame(testmessage);
        result.await(60, TimeUnit.SECONDS);
        List<Exchange> exchanges = result.getReceivedExchanges();
        Assert.assertEquals(1, exchanges.size());
        Object body = result.getReceivedExchanges().get(0).getIn().getBody();
        Assert.assertTrue(("body is " + (body.getClass().getName())), (body instanceof InputStream));
        InputStream in = ((InputStream) (body));
        Assert.assertArrayEquals(testmessage, IOConverter.toBytes(in));
        websocket.sendCloseFrame();
        c.close();
    }

    @Test
    public void wsClientMultipleText() throws Exception {
        AsyncHttpClient c1 = new DefaultAsyncHttpClient();
        WebSocket websocket1 = c1.prepareGet((("ws://localhost:" + (BaseUndertowTest.getPort())) + "/app1")).execute(new WebSocketUpgradeHandler.Builder().addWebSocketListener(new WebSocketListener() {
            @Override
            public void onTextFrame(String message, boolean finalFragment, int rsv) {
                System.out.println(("got message " + message));
            }

            @Override
            public void onOpen(WebSocket webSocket) {
            }

            @Override
            public void onClose(WebSocket webSocket, int code, String reason) {
            }

            @Override
            public void onError(Throwable t) {
                t.printStackTrace();
            }
        }).build()).get();
        AsyncHttpClient c2 = new DefaultAsyncHttpClient();
        WebSocket websocket2 = c2.prepareGet((("ws://localhost:" + (BaseUndertowTest.getPort())) + "/app1")).execute(new WebSocketUpgradeHandler.Builder().addWebSocketListener(new WebSocketListener() {
            @Override
            public void onTextFrame(String message, boolean finalFragment, int rsv) {
                System.out.println(("got message " + message));
            }

            @Override
            public void onOpen(WebSocket webSocket) {
            }

            @Override
            public void onClose(WebSocket webSocket, int code, String reason) {
            }

            @Override
            public void onError(Throwable t) {
                t.printStackTrace();
            }
        }).build()).get();
        MockEndpoint result = getMockEndpoint("mock:result1");
        result.expectedMessageCount(2);
        websocket1.sendTextFrame("Test1");
        websocket2.sendTextFrame("Test2");
        result.await(60, TimeUnit.SECONDS);
        result.assertIsSatisfied();
        List<Exchange> exchanges = result.getReceivedExchanges();
        Set<String> actual = new HashSet<>();
        actual.add(exchanges.get(0).getIn().getBody(String.class));
        actual.add(exchanges.get(1).getIn().getBody(String.class));
        Assert.assertEquals(new HashSet<>(Arrays.asList("Test1", "Test2")), actual);
        websocket1.sendCloseFrame();
        websocket2.sendCloseFrame();
        c1.close();
        c2.close();
    }

    @Test
    public void echo() throws Exception {
        TestClient wsclient1 = new TestClient((("ws://localhost:" + (BaseUndertowTest.getPort())) + "/app3"), 2);
        wsclient1.connect();
        wsclient1.sendTextMessage("Test1");
        wsclient1.sendTextMessage("Test2");
        Assert.assertTrue(wsclient1.await(10));
        Assert.assertEquals(Arrays.asList("Test1", "Test2"), wsclient1.getReceived(String.class));
        wsclient1.close();
    }

    @Test
    public void echoMulti() throws Exception {
        TestClient wsclient1 = new TestClient((("ws://localhost:" + (BaseUndertowTest.getPort())) + "/app3"), 1);
        TestClient wsclient2 = new TestClient((("ws://localhost:" + (BaseUndertowTest.getPort())) + "/app3"), 1);
        wsclient1.connect();
        wsclient2.connect();
        wsclient1.sendTextMessage("Gambas");
        wsclient2.sendTextMessage("Calamares");
        Assert.assertTrue(wsclient1.await(10));
        Assert.assertTrue(wsclient2.await(10));
        Assert.assertEquals(Arrays.asList("Gambas"), wsclient1.getReceived(String.class));
        Assert.assertEquals(Arrays.asList("Calamares"), wsclient2.getReceived(String.class));
        wsclient1.close();
        wsclient2.close();
    }

    @Test
    public void sendToAll() throws Exception {
        TestClient wsclient1 = new TestClient((("ws://localhost:" + (BaseUndertowTest.getPort())) + "/app4"), 2);
        TestClient wsclient2 = new TestClient((("ws://localhost:" + (BaseUndertowTest.getPort())) + "/app4"), 2);
        wsclient1.connect();
        wsclient2.connect();
        wsclient1.sendTextMessage("Gambas");
        wsclient2.sendTextMessage("Calamares");
        Assert.assertTrue(wsclient1.await(10));
        Assert.assertTrue(wsclient2.await(10));
        List<String> received1 = wsclient1.getReceived(String.class);
        Assert.assertEquals(2, received1.size());
        Assert.assertTrue(received1.contains("Gambas"));
        Assert.assertTrue(received1.contains("Calamares"));
        List<String> received2 = wsclient2.getReceived(String.class);
        Assert.assertEquals(2, received2.size());
        Assert.assertTrue(received2.contains("Gambas"));
        Assert.assertTrue(received2.contains("Calamares"));
        wsclient1.close();
        wsclient2.close();
    }

    @Test
    public void fireWebSocketChannelEvents() throws Exception {
        MockEndpoint result = getMockEndpoint("mock:result5");
        result.expectedMessageCount(6);
        TestClient wsclient1 = new TestClient((("ws://localhost:" + (BaseUndertowTest.getPort())) + "/app5"), 2);
        TestClient wsclient2 = new TestClient((("ws://localhost:" + (BaseUndertowTest.getPort())) + "/app5"), 2);
        wsclient1.connect();
        wsclient2.connect();
        wsclient1.sendTextMessage("Gambas");
        wsclient2.sendTextMessage("Calamares");
        wsclient1.close();
        wsclient2.close();
        result.await(60, TimeUnit.SECONDS);
        final List<Exchange> exchanges = result.getReceivedExchanges();
        final Map<String, List<String>> connections = new HashMap<>();
        for (Exchange exchange : exchanges) {
            final Message in = exchange.getIn();
            final String key = ((String) (in.getHeader(CONNECTION_KEY)));
            Assert.assertNotNull(key);
            List<String> messages = connections.get(key);
            if (messages == null) {
                messages = new ArrayList<>();
                connections.put(key, messages);
            }
            String body = in.getBody(String.class);
            if (body != null) {
                messages.add(body);
            } else {
                messages.add(in.getHeader(EVENT_TYPE_ENUM, EventType.class).name());
            }
        }
        final List<String> expected1 = Arrays.asList(ONOPEN.name(), "Gambas", ONCLOSE.name());
        final List<String> expected2 = Arrays.asList(ONOPEN.name(), "Calamares", ONCLOSE.name());
        Assert.assertEquals(2, connections.size());
        final Iterator<List<String>> it = connections.values().iterator();
        final List<String> actual1 = it.next();
        Assert.assertTrue(("actual " + actual1), ((actual1.equals(expected1)) || (actual1.equals(expected2))));
        final List<String> actual2 = it.next();
        Assert.assertTrue(("actual " + actual2), ((actual2.equals(expected1)) || (actual2.equals(expected2))));
    }

    @Test
    public void connectionKeyList() throws Exception {
        TestClient wsclient1 = new TestClient((("ws://localhost:" + (BaseUndertowTest.getPort())) + "/app6"), 1);
        TestClient wsclient2 = new TestClient((("ws://localhost:" + (BaseUndertowTest.getPort())) + "/app6"), 1);
        TestClient wsclient3 = new TestClient((("ws://localhost:" + (BaseUndertowTest.getPort())) + "/app6"), 1);
        wsclient1.connect();
        wsclient2.connect();
        wsclient3.connect();
        wsclient1.await(10);
        final String connectionKey1 = assertConnected(wsclient1);
        Assert.assertNotNull(connectionKey1);
        wsclient2.await(10);
        final String connectionKey2 = assertConnected(wsclient2);
        wsclient3.await(10);
        final String connectionKey3 = assertConnected(wsclient3);
        wsclient1.reset(1);
        wsclient2.reset(1);
        wsclient3.reset(1);
        final String broadcastMsg = (((UndertowWsConsumerRouteTest.BROADCAST_MESSAGE_PREFIX) + connectionKey2) + " ") + connectionKey3;
        wsclient1.sendTextMessage(broadcastMsg);// this one should go to wsclient2 and wsclient3

        wsclient1.sendTextMessage("private");// this one should go to wsclient1 only

        wsclient2.await(10);
        Assert.assertEquals(broadcastMsg, wsclient2.getReceived(String.class).get(0));
        wsclient3.await(10);
        Assert.assertEquals(broadcastMsg, wsclient3.getReceived(String.class).get(0));
        wsclient1.await(10);
        Assert.assertEquals("private", wsclient1.getReceived(String.class).get(0));
        wsclient1.close();
        wsclient2.close();
        wsclient3.close();
    }
}

