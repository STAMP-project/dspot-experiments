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


import java.util.concurrent.TimeUnit;
import org.apache.activemq.util.Wait;
import org.eclipse.jetty.websocket.client.ClientUpgradeRequest;
import org.eclipse.jetty.websocket.client.WebSocketClient;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test that a WS client can steal links when enabled.
 */
public class MQTTWSLinkStealingTest extends WSTransportTestSupport {
    private final String CLIENT_ID = "WS-CLIENT-ID";

    protected WebSocketClient wsClient;

    protected MQTTWSConnection wsMQTTConnection;

    protected ClientUpgradeRequest request;

    @Test(timeout = 60000)
    public void testConnectAndStealLink() throws Exception {
        wsMQTTConnection.connect(CLIENT_ID);
        Assert.assertTrue("Connection should open", Wait.waitFor(new Wait.Condition() {
            @Override
            public boolean isSatisified() throws Exception {
                return (getProxyToBroker().getCurrentConnectionsCount()) == 1;
            }
        }));
        MQTTWSConnection theif = new MQTTWSConnection();
        wsClient.connect(theif, wsConnectUri, request);
        if (!(theif.awaitConnection(30, TimeUnit.SECONDS))) {
            Assert.fail("Could not open new WS connection for link stealing client");
        }
        theif.connect(CLIENT_ID);
        Assert.assertTrue("Connection should open", Wait.waitFor(new Wait.Condition() {
            @Override
            public boolean isSatisified() throws Exception {
                return (getProxyToBroker().getCurrentConnectionsCount()) == 1;
            }
        }));
        Assert.assertTrue("Original Connection should close", Wait.waitFor(new Wait.Condition() {
            @Override
            public boolean isSatisified() throws Exception {
                return !(wsMQTTConnection.isConnected());
            }
        }));
        theif.disconnect();
        theif.close();
        Assert.assertTrue("Connection should close", Wait.waitFor(new Wait.Condition() {
            @Override
            public boolean isSatisified() throws Exception {
                return (getProxyToBroker().getCurrentConnectionsCount()) == 0;
            }
        }));
    }
}

