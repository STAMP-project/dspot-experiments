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


import org.eclipse.jetty.websocket.client.ClientUpgradeRequest;
import org.eclipse.jetty.websocket.client.WebSocketClient;
import org.junit.Assert;
import org.junit.Test;


public class MQTTWSSubProtocolTest extends WSTransportTestSupport {
    protected WebSocketClient wsClient;

    protected MQTTWSConnection wsMQTTConnection;

    protected ClientUpgradeRequest request;

    @Test(timeout = 60000)
    public void testConnectv31() throws Exception {
        connect("mqttv3.1");
        wsMQTTConnection.connect();
        Assert.assertEquals("mqttv3.1", wsMQTTConnection.getConnection().getUpgradeResponse().getAcceptedSubProtocol());
    }

    @Test(timeout = 60000)
    public void testConnectMqtt() throws Exception {
        connect("mqtt");
        wsMQTTConnection.connect();
        Assert.assertEquals("mqtt", wsMQTTConnection.getConnection().getUpgradeResponse().getAcceptedSubProtocol());
    }

    @Test(timeout = 60000)
    public void testConnectMultiple() throws Exception {
        connect("mqtt,mqttv3.1");
        wsMQTTConnection.connect();
        Assert.assertEquals("mqttv3.1", wsMQTTConnection.getConnection().getUpgradeResponse().getAcceptedSubProtocol());
    }
}

