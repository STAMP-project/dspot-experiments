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
import Session.Will;
import io.moquette.broker.security.IAuthenticator;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.mqtt.MqttConnectMessage;
import io.netty.handler.codec.mqtt.MqttMessageBuilders;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class MQTTConnectionConnectTest {
    private static final String FAKE_CLIENT_ID = "FAKE_123";

    private static final String TEST_USER = "fakeuser";

    private static final String TEST_PWD = "fakepwd";

    private static final String EVIL_TEST_USER = "eviluser";

    private static final String EVIL_TEST_PWD = "unsecret";

    private MQTTConnection sut;

    private EmbeddedChannel channel;

    private SessionRegistry sessionRegistry;

    private ConnectBuilder connMsg;

    private static final BrokerConfiguration CONFIG = new BrokerConfiguration(true, true, false);

    private IAuthenticator mockAuthenticator;

    private PostOffice postOffice;

    private MemoryQueueRepository queueRepository;

    @Test
    public void testZeroByteClientIdWithCleanSession() {
        // Connect message with clean session set to true and client id is null.
        MqttConnectMessage msg = MqttMessageBuilders.connect().protocolVersion(MQTT_3_1_1).clientId(null).cleanSession(true).build();
        sut.processConnect(msg);
        NettyChannelAssertions.assertEqualsConnAck("Connection must be accepted", CONNECTION_ACCEPTED, channel.readOutbound());
        Assert.assertNotNull("unique clientid must be generated", sut.getClientId());
        Assert.assertTrue("clean session flag must be true", sessionRegistry.retrieve(sut.getClientId()).isClean());
        Assert.assertTrue("Connection must be open", channel.isOpen());
    }

    @Test
    public void invalidAuthentication() {
        MqttConnectMessage msg = connMsg.clientId(MQTTConnectionConnectTest.FAKE_CLIENT_ID).username(((MQTTConnectionConnectTest.TEST_USER) + "_fake")).password(MQTTConnectionConnectTest.TEST_PWD).build();
        // Exercise
        sut.processConnect(msg);
        // Verify
        NettyChannelAssertions.assertEqualsConnAck(CONNECTION_REFUSED_BAD_USER_NAME_OR_PASSWORD, channel.readOutbound());
        Assert.assertFalse("Connection should be closed by the broker.", channel.isOpen());
    }

    @Test
    public void testConnect_badClientID() {
        connMsg.clientId("extremely_long_clientID_greater_than_23").build();
        // Exercise
        sut.processConnect(connMsg.clientId("extremely_long_clientID_greater_than_23").build());
        // Verify
        NettyChannelAssertions.assertEqualsConnAck(CONNECTION_ACCEPTED, channel.readOutbound());
    }

    @Test
    public void testWillIsAccepted() {
        MqttConnectMessage msg = connMsg.clientId(MQTTConnectionConnectTest.FAKE_CLIENT_ID).willFlag(true).willTopic("topic").willMessage("Topic message").build();
        // Exercise
        // m_handler.setMessaging(mockedMessaging);
        sut.processConnect(msg);
        // Verify
        NettyChannelAssertions.assertEqualsConnAck(CONNECTION_ACCEPTED, channel.readOutbound());
        Assert.assertTrue("Connection is accepted and therefore should remain open", channel.isOpen());
    }

    @Test
    public void testWillIsFired() {
        final PostOffice postOfficeMock = Mockito.mock(PostOffice.class);
        sut = createMQTTConnectionWithPostOffice(MQTTConnectionConnectTest.CONFIG, postOfficeMock);
        channel = ((EmbeddedChannel) (sut.channel));
        MqttConnectMessage msg = connMsg.clientId(MQTTConnectionConnectTest.FAKE_CLIENT_ID).willFlag(true).willTopic("topic").willMessage("Topic message").build();
        sut.processConnect(msg);
        // Exercise
        sut.handleConnectionLost();
        // Verify
        Mockito.verify(postOfficeMock).fireWill(ArgumentMatchers.any(Will.class));
        Assert.assertFalse("Connection MUST be disconnected", sut.isConnected());
    }

    @Test
    public void acceptAnonymousClient() {
        MqttConnectMessage msg = connMsg.clientId(MQTTConnectionConnectTest.FAKE_CLIENT_ID).build();
        // Exercise
        sut.processConnect(msg);
        // Verify
        NettyChannelAssertions.assertEqualsConnAck(CONNECTION_ACCEPTED, channel.readOutbound());
        Assert.assertTrue("Connection is accepted and therefore must remain open", channel.isOpen());
    }

    @Test
    public void validAuthentication() {
        MqttConnectMessage msg = connMsg.clientId(MQTTConnectionConnectTest.FAKE_CLIENT_ID).username(MQTTConnectionConnectTest.TEST_USER).password(MQTTConnectionConnectTest.TEST_PWD).build();
        // Exercise
        sut.processConnect(msg);
        // Verify
        NettyChannelAssertions.assertEqualsConnAck(CONNECTION_ACCEPTED, channel.readOutbound());
        Assert.assertTrue("Connection is accepted and therefore must remain open", channel.isOpen());
    }

    @Test
    public void noPasswdAuthentication() {
        MqttConnectMessage msg = connMsg.clientId(MQTTConnectionConnectTest.FAKE_CLIENT_ID).username(MQTTConnectionConnectTest.TEST_USER).build();
        // Exercise
        sut.processConnect(msg);
        // Verify
        NettyChannelAssertions.assertEqualsConnAck(CONNECTION_REFUSED_BAD_USER_NAME_OR_PASSWORD, channel.readOutbound());
        Assert.assertFalse("Connection must be closed by the broker", channel.isOpen());
    }

    @Test
    public void prohibitAnonymousClient() {
        MqttConnectMessage msg = connMsg.clientId(MQTTConnectionConnectTest.FAKE_CLIENT_ID).build();
        BrokerConfiguration config = new BrokerConfiguration(false, true, false);
        sut = createMQTTConnection(config);
        channel = ((EmbeddedChannel) (sut.channel));
        // Exercise
        sut.processConnect(msg);
        // Verify
        NettyChannelAssertions.assertEqualsConnAck(CONNECTION_REFUSED_BAD_USER_NAME_OR_PASSWORD, channel.readOutbound());
        Assert.assertFalse("Connection must be closed by the broker", channel.isOpen());
    }

    @Test
    public void prohibitAnonymousClient_providingUsername() {
        MqttConnectMessage msg = connMsg.clientId(MQTTConnectionConnectTest.FAKE_CLIENT_ID).username(((MQTTConnectionConnectTest.TEST_USER) + "_fake")).build();
        BrokerConfiguration config = new BrokerConfiguration(false, true, false);
        createMQTTConnection(config);
        // Exercise
        sut.processConnect(msg);
        // Verify
        NettyChannelAssertions.assertEqualsConnAck(CONNECTION_REFUSED_BAD_USER_NAME_OR_PASSWORD, channel.readOutbound());
        Assert.assertFalse("Connection should be closed by the broker.", channel.isOpen());
    }

    @Test
    public void testZeroByteClientIdNotAllowed() {
        BrokerConfiguration config = new BrokerConfiguration(false, false, false);
        sut = createMQTTConnection(config);
        channel = ((EmbeddedChannel) (sut.channel));
        // Connect message with clean session set to true and client id is null.
        MqttConnectMessage msg = connMsg.clientId(null).protocolVersion(MQTT_3_1_1).cleanSession(true).build();
        sut.processConnect(msg);
        NettyChannelAssertions.assertEqualsConnAck("Zero byte client identifiers are not allowed", CONNECTION_REFUSED_IDENTIFIER_REJECTED, channel.readOutbound());
        Assert.assertFalse("Connection must closed", channel.isOpen());
    }

    @Test
    public void testZeroByteClientIdWithoutCleanSession() {
        // Allow zero byte client ids
        // Connect message without clean session set to true but client id is still null
        MqttConnectMessage msg = MqttMessageBuilders.connect().clientId(null).protocolVersion(MQTT_3_1_1).build();
        sut.processConnect(msg);
        NettyChannelAssertions.assertEqualsConnAck("Identifier must be rejected due to having clean session set to false", CONNECTION_REFUSED_IDENTIFIER_REJECTED, channel.readOutbound());
        Assert.assertFalse("Connection must be closed by the broker", channel.isOpen());
    }

    @Test
    public void testBindWithSameClientIDBadCredentialsDoesntDropExistingClient() {
        // Connect a client1
        MqttConnectMessage msg = connMsg.clientId(MQTTConnectionConnectTest.FAKE_CLIENT_ID).username(MQTTConnectionConnectTest.TEST_USER).password(MQTTConnectionConnectTest.TEST_PWD).build();
        sut.processConnect(msg);
        NettyChannelAssertions.assertEqualsConnAck(CONNECTION_ACCEPTED, channel.readOutbound());
        // create another connect same clientID but with bad credentials
        MqttConnectMessage evilClientConnMsg = MqttMessageBuilders.connect().protocolVersion(MQTT_3_1).clientId(MQTTConnectionConnectTest.FAKE_CLIENT_ID).username(MQTTConnectionConnectTest.EVIL_TEST_USER).password(MQTTConnectionConnectTest.EVIL_TEST_PWD).build();
        EmbeddedChannel evilChannel = new EmbeddedChannel();
        // Exercise
        BrokerConfiguration config = new BrokerConfiguration(true, true, false);
        final MQTTConnection evilConnection = createMQTTConnection(config, evilChannel, postOffice);
        evilConnection.processConnect(evilClientConnMsg);
        // Verify
        // the evil client gets a not auth notification
        NettyChannelAssertions.assertEqualsConnAck(CONNECTION_REFUSED_BAD_USER_NAME_OR_PASSWORD, evilChannel.readOutbound());
        // the good client remains connected
        Assert.assertTrue("Original connected client must remain connected", channel.isOpen());
        Assert.assertFalse("Channel trying to connect with bad credentials must be closed", evilChannel.isOpen());
    }

    @Test
    public void testForceClientDisconnection_issue116() {
        MqttConnectMessage msg = connMsg.clientId(MQTTConnectionConnectTest.FAKE_CLIENT_ID).username(MQTTConnectionConnectTest.TEST_USER).password(MQTTConnectionConnectTest.TEST_PWD).build();
        sut.processConnect(msg);
        NettyChannelAssertions.assertEqualsConnAck(CONNECTION_ACCEPTED, channel.readOutbound());
        // now create another connection and check the new one closes the older
        MQTTConnection anotherConnection = createMQTTConnection(MQTTConnectionConnectTest.CONFIG);
        anotherConnection.processConnect(msg);
        EmbeddedChannel anotherChannel = ((EmbeddedChannel) (anotherConnection.channel));
        NettyChannelAssertions.assertEqualsConnAck(CONNECTION_ACCEPTED, anotherChannel.readOutbound());
        // Verify
        Assert.assertFalse("First 'FAKE_CLIENT_ID' channel MUST be closed by the broker", channel.isOpen());
        Assert.assertTrue("Second 'FAKE_CLIENT_ID' channel MUST be still open", anotherChannel.isOpen());
    }
}

