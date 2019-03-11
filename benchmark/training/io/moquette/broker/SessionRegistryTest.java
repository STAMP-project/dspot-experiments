/**
 * Copyright (c) 2012-2018 The original author or authors
 * ------------------------------------------------------
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Apache License v2.0 which accompanies this distribution.
 *
 * The Eclipse Public License is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * The Apache License v2.0 is available at
 * http://www.opensource.org/licenses/apache2.0.php
 *
 * You may elect to redistribute this code under either of these licenses.
 */
package io.moquette.broker;


import MqttMessageBuilders.ConnectBuilder;
import MqttVersion.MQTT_3_1;
import MqttVersion.MQTT_3_1_1;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.mqtt.MqttConnectMessage;
import io.netty.handler.codec.mqtt.MqttMessageBuilders;
import org.junit.Assert;
import org.junit.Test;


public class SessionRegistryTest {
    static final String FAKE_CLIENT_ID = "FAKE_123";

    static final String TEST_USER = "fakeuser";

    static final String TEST_PWD = "fakepwd";

    private MQTTConnection connection;

    private EmbeddedChannel channel;

    private SessionRegistry sut;

    private ConnectBuilder connMsg;

    private static final BrokerConfiguration ALLOW_ANONYMOUS_AND_ZEROBYTE_CLIENT_ID = new BrokerConfiguration(true, true, false);

    private MemoryQueueRepository queueRepository;

    @Test
    public void testConnAckContainsSessionPresentFlag() {
        MqttConnectMessage msg = connMsg.clientId(SessionRegistryTest.FAKE_CLIENT_ID).protocolVersion(MQTT_3_1_1).build();
        NettyUtils.clientID(channel, SessionRegistryTest.FAKE_CLIENT_ID);
        NettyUtils.cleanSession(channel, false);
        // Connect a first time
        sut.bindToSession(connection, msg, SessionRegistryTest.FAKE_CLIENT_ID);
        // disconnect
        sut.disconnect(SessionRegistryTest.FAKE_CLIENT_ID);
        // Exercise, reconnect
        EmbeddedChannel anotherChannel = new EmbeddedChannel();
        MQTTConnection anotherConnection = createMQTTConnection(SessionRegistryTest.ALLOW_ANONYMOUS_AND_ZEROBYTE_CLIENT_ID, anotherChannel);
        sut.bindToSession(anotherConnection, msg, SessionRegistryTest.FAKE_CLIENT_ID);
        // Verify
        NettyChannelAssertions.assertEqualsConnAck(CONNECTION_ACCEPTED, anotherChannel.readOutbound());
        Assert.assertTrue("Connection is accepted and therefore should remain open", anotherChannel.isOpen());
    }

    @Test
    public void connectWithCleanSessionUpdateClientSession() {
        // first connect with clean session true
        MqttConnectMessage msg = connMsg.clientId(SessionRegistryTest.FAKE_CLIENT_ID).cleanSession(true).build();
        connection.processConnect(msg);
        NettyChannelAssertions.assertEqualsConnAck(CONNECTION_ACCEPTED, channel.readOutbound());
        connection.processDisconnect(null);
        Assert.assertFalse(channel.isOpen());
        // second connect with clean session false
        EmbeddedChannel anotherChannel = new EmbeddedChannel();
        MQTTConnection anotherConnection = createMQTTConnection(SessionRegistryTest.ALLOW_ANONYMOUS_AND_ZEROBYTE_CLIENT_ID, anotherChannel);
        MqttConnectMessage secondConnMsg = MqttMessageBuilders.connect().clientId(SessionRegistryTest.FAKE_CLIENT_ID).protocolVersion(MQTT_3_1).build();
        anotherConnection.processConnect(secondConnMsg);
        NettyChannelAssertions.assertEqualsConnAck(CONNECTION_ACCEPTED, anotherChannel.readOutbound());
        // Verify client session is clean false
        Session session = sut.retrieve(SessionRegistryTest.FAKE_CLIENT_ID);
        Assert.assertFalse(session.isClean());
    }
}

