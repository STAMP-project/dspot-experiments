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
package org.apache.camel.component.websocket;


import WebsocketConstants.SEND_TO_ALL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.asynchttpclient.AsyncHttpClient;
import org.asynchttpclient.DefaultAsyncHttpClient;
import org.asynchttpclient.ws.DefaultWebSocketListener;
import org.asynchttpclient.ws.WebSocket;
import org.asynchttpclient.ws.WebSocketTextListener;
import org.asynchttpclient.ws.WebSocketUpgradeHandler;
import org.junit.Test;


public class WebsocketProducerRouteExampleTest extends CamelTestSupport {
    private static List<Object> received = new ArrayList<>();

    private static CountDownLatch latch;

    protected int port;

    @Produce(uri = "direct:shop")
    private ProducerTemplate producer;

    @Test
    public void testWSHttpCall() throws Exception {
        AsyncHttpClient c = new DefaultAsyncHttpClient();
        WebSocket websocket = c.prepareGet((("ws://localhost:" + (port)) + "/shop")).execute(new WebSocketUpgradeHandler.Builder().addWebSocketListener(new WebSocketTextListener() {
            @Override
            public void onMessage(String message) {
                WebsocketProducerRouteExampleTest.received.add(message);
                log.info(("received --> " + message));
                WebsocketProducerRouteExampleTest.latch.countDown();
            }

            @Override
            public void onOpen(WebSocket websocket) {
            }

            @Override
            public void onClose(WebSocket websocket) {
            }

            @Override
            public void onError(Throwable t) {
                t.printStackTrace();
            }
        }).build()).get();
        // Send message to the direct endpoint
        producer.sendBodyAndHeader("Beer on stock at Apache Mall", SEND_TO_ALL, "true");
        assertTrue(WebsocketProducerRouteExampleTest.latch.await(10, TimeUnit.SECONDS));
        assertEquals(1, WebsocketProducerRouteExampleTest.received.size());
        Object r = WebsocketProducerRouteExampleTest.received.get(0);
        assertTrue((r instanceof String));
        assertEquals("Beer on stock at Apache Mall", r);
        websocket.close();
        c.close();
    }

    @Test
    public void testWSBytesHttpCall() throws Exception {
        AsyncHttpClient c = new DefaultAsyncHttpClient();
        WebSocket websocket = c.prepareGet((("ws://localhost:" + (port)) + "/shop")).execute(new WebSocketUpgradeHandler.Builder().addWebSocketListener(new DefaultWebSocketListener() {
            @Override
            public void onMessage(byte[] message) {
                WebsocketProducerRouteExampleTest.received.add(message);
                log.info(("received --> " + (Arrays.toString(message))));
                WebsocketProducerRouteExampleTest.latch.countDown();
            }

            @Override
            public void onOpen(WebSocket websocket) {
            }

            @Override
            public void onClose(WebSocket websocket) {
            }

            @Override
            public void onError(Throwable t) {
                t.printStackTrace();
            }
        }).build()).get();
        // Send message to the direct endpoint
        byte[] testmessage = "Beer on stock at Apache Mall".getBytes("utf-8");
        producer.sendBodyAndHeader(testmessage, SEND_TO_ALL, "true");
        assertTrue(WebsocketProducerRouteExampleTest.latch.await(10, TimeUnit.SECONDS));
        assertEquals(1, WebsocketProducerRouteExampleTest.received.size());
        Object r = WebsocketProducerRouteExampleTest.received.get(0);
        assertTrue((r instanceof byte[]));
        assertArrayEquals(testmessage, ((byte[]) (r)));
        websocket.close();
        c.close();
    }
}

