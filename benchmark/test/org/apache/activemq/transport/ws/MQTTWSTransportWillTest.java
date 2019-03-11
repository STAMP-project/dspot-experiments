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
import java.util.concurrent.TimeUnit;
import org.eclipse.jetty.websocket.client.ClientUpgradeRequest;
import org.eclipse.jetty.websocket.client.WebSocketClient;
import org.fusesource.mqtt.client.QoS;
import org.fusesource.mqtt.client.Topic;
import org.fusesource.mqtt.codec.CONNECT;
import org.fusesource.mqtt.codec.PUBACK;
import org.fusesource.mqtt.codec.PUBLISH;
import org.fusesource.mqtt.codec.SUBSCRIBE;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


/**
 * This shows that last will and testament messages work with MQTT over WS.
 * This test is modeled after org.apache.activemq.transport.mqtt.MQTTWillTest
 */
@RunWith(Parameterized.class)
public class MQTTWSTransportWillTest extends WSTransportTestSupport {
    protected WebSocketClient wsClient;

    protected MQTTWSConnection wsMQTTConnection1;

    protected MQTTWSConnection wsMQTTConnection2;

    protected ClientUpgradeRequest request;

    private String willTopic = "willTopic";

    private String payload = "last will";

    private boolean closeWithDisconnect;

    public MQTTWSTransportWillTest(boolean closeWithDisconnect) {
        this.closeWithDisconnect = closeWithDisconnect;
    }

    @Test(timeout = 60000)
    public void testWill() throws Exception {
        // connect with will retain false
        CONNECT command = getWillConnectCommand(false);
        // connect both connections
        wsMQTTConnection1.connect(command);
        wsMQTTConnection2.connect();
        // Subscribe to topics
        SUBSCRIBE subscribe = new SUBSCRIBE();
        subscribe.topics(new Topic[]{ new Topic("#", QoS.EXACTLY_ONCE) });
        wsMQTTConnection2.sendFrame(subscribe.encode());
        wsMQTTConnection2.receive(5, TimeUnit.SECONDS);
        // Test message send/receive
        wsMQTTConnection1.sendFrame(getTestMessage(((short) (125))).encode());
        assertMessageReceived(wsMQTTConnection2);
        // close the first connection without sending a proper disconnect frame first
        // if closeWithDisconnect is false
        if (closeWithDisconnect) {
            wsMQTTConnection1.disconnect();
        }
        wsMQTTConnection1.close();
        // Make sure LWT message is not received
        if (closeWithDisconnect) {
            Assert.assertNull(wsMQTTConnection2.receive(5, TimeUnit.SECONDS));
            // make sure LWT is received
        } else {
            assertWillTopicReceived(wsMQTTConnection2);
        }
    }

    @Test(timeout = 60 * 1000)
    public void testRetainWillMessage() throws Exception {
        // create connection with will retain true
        CONNECT command = getWillConnectCommand(true);
        wsMQTTConnection1.connect(command);
        wsMQTTConnection2.connect();
        // set to at most once to test will retain
        SUBSCRIBE subscribe = new SUBSCRIBE();
        subscribe.topics(new Topic[]{ new Topic("#", QoS.AT_MOST_ONCE) });
        wsMQTTConnection2.sendFrame(subscribe.encode());
        wsMQTTConnection2.receive(5, TimeUnit.SECONDS);
        // Test message send/receive
        PUBLISH pub = getTestMessage(((short) (127)));
        wsMQTTConnection1.sendFrame(pub.encode());
        assertMessageReceived(wsMQTTConnection2);
        PUBACK ack = new PUBACK();
        ack.messageId(pub.messageId());
        wsMQTTConnection2.sendFrame(ack.encode());
        // Properly close connection 2 and improperly close connection 1 for LWT test
        wsMQTTConnection2.disconnect();
        wsMQTTConnection2.close();
        Thread.sleep(1000);
        // close the first connection without sending a proper disconnect frame first
        // if closeWithoutDisconnect is false
        if (closeWithDisconnect) {
            wsMQTTConnection1.disconnect();
        }
        wsMQTTConnection1.close();
        Thread.sleep(1000);
        // Do the reconnect of the websocket after close
        wsMQTTConnection2 = new MQTTWSConnection();
        wsClient.connect(wsMQTTConnection2, wsConnectUri, request);
        if (!(wsMQTTConnection2.awaitConnection(30, TimeUnit.SECONDS))) {
            throw new IOException("Could not connect to MQTT WS endpoint");
        }
        // Make sure the will message is received on reconnect
        wsMQTTConnection2.connect();
        wsMQTTConnection2.sendFrame(subscribe.encode());
        wsMQTTConnection2.receive(5, TimeUnit.SECONDS);
        // Make sure LWT message not received
        if (closeWithDisconnect) {
            Assert.assertNull(wsMQTTConnection2.receive(5, TimeUnit.SECONDS));
            // make sure LWT is received
        } else {
            assertWillTopicReceived(wsMQTTConnection2);
        }
    }
}

