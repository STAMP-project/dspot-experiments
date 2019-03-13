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
package org.apache.camel.component.atmosphere.websocket;


import java.util.List;
import org.junit.Test;


// END SNIPPET: payload
public class WebsocketRouteTest extends WebsocketCamelRouterTestSupport {
    private static final String RESPONSE_GREETING = "Hola ";

    private static final byte[] RESPONSE_GREETING_BYTES = new byte[]{ 72, 111, 108, 97, 32 };

    @Test
    public void testWebsocketSingleClient() throws Exception {
        TestClient wsclient = new TestClient((("ws://localhost:" + (WebsocketCamelRouterTestSupport.PORT)) + "/hola"));
        wsclient.connect();
        wsclient.sendTextMessage("Cerveza");
        assertTrue(wsclient.await(10));
        List<String> received = wsclient.getReceived(String.class);
        assertEquals(1, received.size());
        assertEquals("Hola Cerveza", received.get(0));
        wsclient.close();
    }

    @Test
    public void testWebsocketSingleClientForBytes() throws Exception {
        TestClient wsclient = new TestClient((("ws://localhost:" + (WebsocketCamelRouterTestSupport.PORT)) + "/hola"));
        wsclient.connect();
        wsclient.sendBytesMessage("Cerveza".getBytes("UTF-8"));
        assertTrue(wsclient.await(10));
        List<String> received = wsclient.getReceived(String.class);
        assertEquals(1, received.size());
        assertEquals("Hola Cerveza", received.get(0));
        wsclient.close();
    }

    @Test
    public void testWebsocketSingleClientForReader() throws Exception {
        TestClient wsclient = new TestClient((("ws://localhost:" + (WebsocketCamelRouterTestSupport.PORT)) + "/hola3"));
        wsclient.connect();
        wsclient.sendTextMessage("Cerveza");
        assertTrue(wsclient.await(10));
        List<String> received = wsclient.getReceived(String.class);
        assertEquals(1, received.size());
        assertEquals("Hola Cerveza", received.get(0));
        wsclient.close();
    }

    @Test
    public void testWebsocketSingleClientForInputStream() throws Exception {
        TestClient wsclient = new TestClient((("ws://localhost:" + (WebsocketCamelRouterTestSupport.PORT)) + "/hola3"));
        wsclient.connect();
        wsclient.sendBytesMessage("Cerveza".getBytes("UTF-8"));
        assertTrue(wsclient.await(10));
        List<String> received = wsclient.getReceived(String.class);
        assertEquals(1, received.size());
        assertEquals("Hola Cerveza", received.get(0));
        wsclient.close();
    }

    @Test
    public void testWebsocketBroadcastClient() throws Exception {
        TestClient wsclient1 = new TestClient((("ws://localhost:" + (WebsocketCamelRouterTestSupport.PORT)) + "/hola2"), 2);
        TestClient wsclient2 = new TestClient((("ws://localhost:" + (WebsocketCamelRouterTestSupport.PORT)) + "/hola2"), 2);
        wsclient1.connect();
        wsclient2.connect();
        wsclient1.sendTextMessage("Gambas");
        wsclient2.sendTextMessage("Calamares");
        assertTrue(wsclient1.await(10));
        assertTrue(wsclient2.await(10));
        List<String> received1 = wsclient1.getReceived(String.class);
        assertEquals(2, received1.size());
        assertTrue(received1.contains("Hola Gambas"));
        assertTrue(received1.contains("Hola Calamares"));
        List<String> received2 = wsclient2.getReceived(String.class);
        assertEquals(2, received2.size());
        assertTrue(received2.contains("Hola Gambas"));
        assertTrue(received2.contains("Hola Calamares"));
        wsclient1.close();
        wsclient2.close();
    }

    @Test
    public void testWebsocketEventsResendingDisabled() throws Exception {
        TestClient wsclient = new TestClient((("ws://localhost:" + (WebsocketCamelRouterTestSupport.PORT)) + "/hola4"));
        wsclient.connect();
        assertFalse(wsclient.await(10));
        wsclient.close();
    }
}

