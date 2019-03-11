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


import org.apache.activemq.transport.stomp.Stomp;
import org.eclipse.jetty.websocket.client.WebSocketClient;
import org.junit.Test;


/**
 * Test STOMP sub protocol detection.
 */
public class StompWSSubProtocolTest extends WSTransportTestSupport {
    protected WebSocketClient wsClient;

    protected StompWSConnection wsStompConnection;

    @Test(timeout = 60000)
    public void testConnectV12() throws Exception {
        connect("v12.stomp");
        String connectFrame = ("STOMP\n" + (("accept-version:1.2\n" + "host:localhost\n") + "\n")) + (Stomp.NULL);
        wsStompConnection.sendRawFrame(connectFrame);
        assertSubProtocol("v12.stomp");
    }

    @Test(timeout = 60000)
    public void testConnectV11() throws Exception {
        connect("v11.stomp");
        String connectFrame = ("STOMP\n" + (("accept-version:1.2\n" + "host:localhost\n") + "\n")) + (Stomp.NULL);
        wsStompConnection.sendRawFrame(connectFrame);
        assertSubProtocol("v11.stomp");
    }

    @Test(timeout = 60000)
    public void testConnectV10() throws Exception {
        connect("v10.stomp");
        String connectFrame = ("STOMP\n" + (("accept-version:1.2\n" + "host:localhost\n") + "\n")) + (Stomp.NULL);
        wsStompConnection.sendRawFrame(connectFrame);
        assertSubProtocol("v10.stomp");
    }

    @Test(timeout = 60000)
    public void testConnectNone() throws Exception {
        connect(null);
        String connectFrame = ("STOMP\n" + (("accept-version:1.2\n" + "host:localhost\n") + "\n")) + (Stomp.NULL);
        wsStompConnection.sendRawFrame(connectFrame);
        assertSubProtocol("stomp");
    }

    @Test(timeout = 60000)
    public void testConnectMultiple() throws Exception {
        connect("v10.stomp,v11.stomp");
        String connectFrame = ("STOMP\n" + (("accept-version:1.2\n" + "host:localhost\n") + "\n")) + (Stomp.NULL);
        wsStompConnection.sendRawFrame(connectFrame);
        assertSubProtocol("v11.stomp");
    }

    @Test(timeout = 60000)
    public void testConnectInvalid() throws Exception {
        connect("invalid");
        String connectFrame = ("STOMP\n" + (("accept-version:1.2\n" + "host:localhost\n") + "\n")) + (Stomp.NULL);
        wsStompConnection.sendRawFrame(connectFrame);
        assertSubProtocol("stomp");
    }
}

