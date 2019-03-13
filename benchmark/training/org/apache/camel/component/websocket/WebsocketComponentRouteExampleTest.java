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


import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.asynchttpclient.AsyncHttpClient;
import org.asynchttpclient.DefaultAsyncHttpClient;
import org.asynchttpclient.ws.WebSocket;
import org.asynchttpclient.ws.WebSocketTextListener;
import org.asynchttpclient.ws.WebSocketUpgradeHandler;
import org.junit.Test;


public class WebsocketComponentRouteExampleTest extends CamelTestSupport {
    private static List<String> received = new ArrayList<>();

    private static CountDownLatch latch = new CountDownLatch(1);

    protected int port;

    @Test
    public void testWSHttpCall() throws Exception {
        AsyncHttpClient c = new DefaultAsyncHttpClient();
        WebSocket websocket = c.prepareGet((("ws://localhost:" + (port)) + "/echo")).execute(new WebSocketUpgradeHandler.Builder().addWebSocketListener(new WebSocketTextListener() {
            @Override
            public void onMessage(String message) {
                WebsocketComponentRouteExampleTest.received.add(message);
                log.info(("received --> " + message));
                WebsocketComponentRouteExampleTest.latch.countDown();
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
        websocket.sendMessage("Beer");
        assertTrue(WebsocketComponentRouteExampleTest.latch.await(10, TimeUnit.SECONDS));
        assertEquals(1, WebsocketComponentRouteExampleTest.received.size());
        assertEquals("BeerBeer", WebsocketComponentRouteExampleTest.received.get(0));
        websocket.close();
        c.close();
    }
}

