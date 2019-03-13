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


import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.Test;


// END SNIPPET: payload
public class WebsocketRouteWithInitParamTest extends WebsocketCamelRouterWithInitParamTestSupport {
    private static final String[] EXISTED_USERS = new String[]{ "Kim", "Pavlo", "Peter" };

    private static String[] broadcastMessageTo = new String[]{  };

    private static Map<String, String> connectionKeyUserMap = new HashMap<>();

    @Test
    public void testWebsocketEventsResendingEnabled() throws Exception {
        TestClient wsclient = new TestClient((("ws://localhost:" + (WebsocketCamelRouterWithInitParamTestSupport.PORT)) + "/hola"));
        wsclient.connect();
        wsclient.close();
    }

    @Test
    public void testPassParametersWebsocketOnOpen() throws Exception {
        TestClient wsclient = new TestClient((("ws://localhost:" + (WebsocketCamelRouterWithInitParamTestSupport.PORT)) + "/hola1?param1=value1&param2=value2"));
        wsclient.connect();
        wsclient.close();
    }

    @Test
    public void testWebsocketSingleClientBroadcastMultipleClients() throws Exception {
        final int awaitTime = 5;
        WebsocketRouteWithInitParamTest.connectionKeyUserMap.clear();
        TestClient wsclient1 = new TestClient((("ws://localhost:" + (WebsocketCamelRouterWithInitParamTestSupport.PORT)) + "/hola2"), 2);
        TestClient wsclient2 = new TestClient((("ws://localhost:" + (WebsocketCamelRouterWithInitParamTestSupport.PORT)) + "/hola2"), 2);
        TestClient wsclient3 = new TestClient((("ws://localhost:" + (WebsocketCamelRouterWithInitParamTestSupport.PORT)) + "/hola2"), 2);
        wsclient1.connect();
        wsclient1.await(awaitTime);
        wsclient2.connect();
        wsclient2.await(awaitTime);
        wsclient3.connect();
        wsclient3.await(awaitTime);
        // all connections were registered in external store
        assertTrue(((WebsocketRouteWithInitParamTest.connectionKeyUserMap.size()) == (WebsocketRouteWithInitParamTest.EXISTED_USERS.length)));
        WebsocketRouteWithInitParamTest.broadcastMessageTo = new String[]{ WebsocketRouteWithInitParamTest.EXISTED_USERS[0], WebsocketRouteWithInitParamTest.EXISTED_USERS[1] };
        wsclient1.sendTextMessage("Gambas");
        wsclient1.await(awaitTime);
        List<String> received1 = wsclient1.getReceived(String.class);
        assertEquals(1, received1.size());
        for (String element : WebsocketRouteWithInitParamTest.broadcastMessageTo) {
            assertTrue(received1.get(0).contains(element));
        }
        List<String> received2 = wsclient2.getReceived(String.class);
        assertEquals(1, received2.size());
        for (String element : WebsocketRouteWithInitParamTest.broadcastMessageTo) {
            assertTrue(received2.get(0).contains(element));
        }
        List<String> received3 = wsclient3.getReceived(String.class);
        assertEquals(0, received3.size());
        wsclient1.close();
        wsclient2.close();
        wsclient3.close();
    }

    @Test
    public void testWebsocketSingleClientBroadcastMultipleClientsGuaranteeDelivery() throws Exception {
        final int awaitTime = 5;
        WebsocketRouteWithInitParamTest.connectionKeyUserMap.clear();
        TestClient wsclient1 = new TestClient((("ws://localhost:" + (WebsocketCamelRouterWithInitParamTestSupport.PORT)) + "/hola3"), 2);
        TestClient wsclient2 = new TestClient((("ws://localhost:" + (WebsocketCamelRouterWithInitParamTestSupport.PORT)) + "/hola3"), 2);
        TestClient wsclient3 = new TestClient((("ws://localhost:" + (WebsocketCamelRouterWithInitParamTestSupport.PORT)) + "/hola3"), 2);
        wsclient1.connect();
        wsclient1.await(awaitTime);
        wsclient2.connect();
        wsclient2.await(awaitTime);
        wsclient3.connect();
        wsclient3.await(awaitTime);
        // all connections were registered in external store
        assertTrue(((WebsocketRouteWithInitParamTest.connectionKeyUserMap.size()) == (WebsocketRouteWithInitParamTest.EXISTED_USERS.length)));
        wsclient2.close();
        wsclient2.await(awaitTime);
        WebsocketRouteWithInitParamTest.broadcastMessageTo = new String[]{ WebsocketRouteWithInitParamTest.EXISTED_USERS[0], WebsocketRouteWithInitParamTest.EXISTED_USERS[1] };
        wsclient1.sendTextMessage("Gambas");
        wsclient1.await(awaitTime);
        List<String> received1 = wsclient1.getReceived(String.class);
        assertEquals(1, received1.size());
        for (String element : WebsocketRouteWithInitParamTest.broadcastMessageTo) {
            assertTrue(received1.get(0).contains(element));
        }
        List<String> received2 = wsclient2.getReceived(String.class);
        assertEquals(0, received2.size());
        List<String> received3 = wsclient3.getReceived(String.class);
        assertEquals(0, received3.size());
        wsclient1.close();
        wsclient3.close();
    }
}

