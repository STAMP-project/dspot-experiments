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


import MqttQoS.EXACTLY_ONCE;
import UnpooledByteBufAllocator.DEFAULT;
import io.moquette.broker.subscriptions.ISubscriptionsDirectory;
import io.moquette.broker.subscriptions.Topic;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import io.netty.channel.embedded.EmbeddedChannel;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;


public class PostOfficePublishTest {
    private static final String FAKE_CLIENT_ID = "FAKE_123";

    private static final String FAKE_CLIENT_ID2 = "FAKE_456";

    static final String SUBSCRIBER_ID = "Subscriber";

    static final String PUBLISHER_ID = "Publisher";

    private static final String TEST_USER = "fakeuser";

    private static final String TEST_PWD = "fakepwd";

    private static final String NEWS_TOPIC = "/news";

    private static final String BAD_FORMATTED_TOPIC = "#MQTTClient";

    private MQTTConnection connection;

    private EmbeddedChannel channel;

    private PostOffice sut;

    private ISubscriptionsDirectory subscriptions;

    public static final String FAKE_USER_NAME = "UnAuthUser";

    private MqttConnectMessage connectMessage;

    private SessionRegistry sessionRegistry;

    private MockAuthenticator mockAuthenticator;

    static final BrokerConfiguration ALLOW_ANONYMOUS_AND_ZERO_BYTES_CLID = new BrokerConfiguration(true, true, false);

    private MemoryRetainedRepository retainedRepository;

    private MemoryQueueRepository queueRepository;

    @Test
    public void testPublishQoS0ToItself() {
        connection.processConnect(connectMessage);
        ConnectionTestUtils.assertConnectAccepted(channel);
        // subscribe
        subscribe(AT_MOST_ONCE, PostOfficePublishTest.NEWS_TOPIC, connection);
        // Exercise
        final ByteBuf payload = Unpooled.copiedBuffer("Hello world!", Charset.defaultCharset());
        sut.receivedPublishQos0(new Topic(PostOfficePublishTest.NEWS_TOPIC), PostOfficePublishTest.TEST_USER, PostOfficePublishTest.FAKE_CLIENT_ID, payload, false, MqttMessageBuilders.publish().payload(payload.retainedDuplicate()).qos(MqttQoS.AT_MOST_ONCE).retained(false).topicName(PostOfficePublishTest.NEWS_TOPIC).build());
        // Verify
        ConnectionTestUtils.verifyReceivePublish(channel, PostOfficePublishTest.NEWS_TOPIC, "Hello world!");
    }

    @Test
    public void testForceClientDisconnection_issue116() {
        final MQTTConnection clientXA = connectAs("subscriber");
        subscribe(clientXA, PostOfficePublishTest.NEWS_TOPIC, AT_MOST_ONCE);
        final MQTTConnection clientXB = connectAs("publisher");
        final ByteBuf anyPayload = Unpooled.copiedBuffer("Hello", Charset.defaultCharset());
        sut.receivedPublishQos2(clientXB, MqttMessageBuilders.publish().payload(anyPayload).qos(EXACTLY_ONCE).retained(false).topicName(PostOfficePublishTest.NEWS_TOPIC).build(), "username");
        final MQTTConnection clientYA = connectAs("subscriber");
        subscribe(clientYA, PostOfficePublishTest.NEWS_TOPIC, AT_MOST_ONCE);
        final MQTTConnection clientYB = connectAs("publisher");
        final ByteBuf anyPayload2 = Unpooled.copiedBuffer("Hello 2", Charset.defaultCharset());
        sut.receivedPublishQos2(clientYB, MqttMessageBuilders.publish().payload(anyPayload2).qos(EXACTLY_ONCE).retained(true).topicName(PostOfficePublishTest.NEWS_TOPIC).build(), "username");
        // Verify
        Assert.assertFalse("First 'subscriber' channel MUST be closed by the broker", clientXA.channel.isOpen());
        ConnectionTestUtils.verifyPublishIsReceived(((EmbeddedChannel) (clientYA.channel)), AT_MOST_ONCE, "Hello 2");
    }

    @Test
    public void testPublishToMultipleSubscribers() {
        final Set<String> clientIds = new HashSet<>(Arrays.asList(PostOfficePublishTest.FAKE_CLIENT_ID, PostOfficePublishTest.FAKE_CLIENT_ID2));
        mockAuthenticator = new MockAuthenticator(clientIds, Collections.singletonMap(PostOfficePublishTest.TEST_USER, PostOfficePublishTest.TEST_PWD));
        EmbeddedChannel channel1 = new EmbeddedChannel();
        MQTTConnection connection1 = createMQTTConnection(PostOfficePublishTest.ALLOW_ANONYMOUS_AND_ZERO_BYTES_CLID, channel1);
        connection1.processConnect(ConnectionTestUtils.buildConnect(PostOfficePublishTest.FAKE_CLIENT_ID));
        ConnectionTestUtils.assertConnectAccepted(channel1);
        EmbeddedChannel channel2 = new EmbeddedChannel();
        MQTTConnection connection2 = createMQTTConnection(PostOfficePublishTest.ALLOW_ANONYMOUS_AND_ZERO_BYTES_CLID, channel2);
        connection2.processConnect(ConnectionTestUtils.buildConnect(PostOfficePublishTest.FAKE_CLIENT_ID2));
        ConnectionTestUtils.assertConnectAccepted(channel2);
        // subscribe
        final MqttQoS qos = AT_MOST_ONCE;
        final String newsTopic = PostOfficePublishTest.NEWS_TOPIC;
        subscribe(qos, newsTopic, connection1);
        subscribe(qos, newsTopic, connection2);
        // Exercise
        final ByteBuf payload = Unpooled.copiedBuffer("Hello world!", Charset.defaultCharset());
        sut.receivedPublishQos0(new Topic(PostOfficePublishTest.NEWS_TOPIC), PostOfficePublishTest.TEST_USER, PostOfficePublishTest.FAKE_CLIENT_ID, payload, false, MqttMessageBuilders.publish().payload(payload.retainedDuplicate()).qos(MqttQoS.AT_MOST_ONCE).retained(false).topicName(PostOfficePublishTest.NEWS_TOPIC).build());
        // Verify
        ConnectionTestUtils.verifyReceivePublish(channel1, PostOfficePublishTest.NEWS_TOPIC, "Hello world!");
        ConnectionTestUtils.verifyReceivePublish(channel2, PostOfficePublishTest.NEWS_TOPIC, "Hello world!");
    }

    @Test
    public void testPublishWithEmptyPayloadClearRetainedStore() {
        connection.processConnect(connectMessage);
        ConnectionTestUtils.assertConnectAccepted(channel);
        this.retainedRepository.retain(new Topic(PostOfficePublishTest.NEWS_TOPIC), MqttMessageBuilders.publish().payload(ByteBufUtil.writeAscii(DEFAULT, "Hello world!")).qos(AT_LEAST_ONCE).build());
        // Exercise
        final ByteBuf anyPayload = Unpooled.copiedBuffer("Any payload", Charset.defaultCharset());
        sut.receivedPublishQos0(new Topic(PostOfficePublishTest.NEWS_TOPIC), PostOfficePublishTest.TEST_USER, PostOfficePublishTest.FAKE_CLIENT_ID, anyPayload, true, MqttMessageBuilders.publish().payload(anyPayload).qos(MqttQoS.AT_MOST_ONCE).retained(false).topicName(PostOfficePublishTest.NEWS_TOPIC).build());
        // Verify
        Assert.assertTrue("QoS0 MUST clean retained message for topic", retainedRepository.isEmpty());
    }

    @Test
    public void testPublishWithQoS1() {
        connection.processConnect(connectMessage);
        ConnectionTestUtils.assertConnectAccepted(channel);
        subscribe(connection, PostOfficePublishTest.NEWS_TOPIC, AT_LEAST_ONCE);
        // Exercise
        final ByteBuf anyPayload = Unpooled.copiedBuffer("Any payload", Charset.defaultCharset());
        sut.receivedPublishQos1(connection, new Topic(PostOfficePublishTest.NEWS_TOPIC), PostOfficePublishTest.TEST_USER, anyPayload, 1, true, MqttMessageBuilders.publish().payload(Unpooled.copiedBuffer("Any payload", Charset.defaultCharset())).qos(MqttQoS.AT_LEAST_ONCE).retained(true).topicName(PostOfficePublishTest.NEWS_TOPIC).build());
        // Verify
        ConnectionTestUtils.verifyPublishIsReceived(channel, AT_LEAST_ONCE, "Any payload");
    }

    @Test
    public void testPublishWithQoS2() {
        connection.processConnect(connectMessage);
        ConnectionTestUtils.assertConnectAccepted(channel);
        subscribe(connection, PostOfficePublishTest.NEWS_TOPIC, EXACTLY_ONCE);
        // Exercise
        final ByteBuf anyPayload = Unpooled.copiedBuffer("Any payload", Charset.defaultCharset());
        sut.receivedPublishQos2(connection, MqttMessageBuilders.publish().payload(anyPayload).qos(EXACTLY_ONCE).retained(true).topicName(PostOfficePublishTest.NEWS_TOPIC).build(), "username");
        // Verify
        ConnectionTestUtils.verifyPublishIsReceived(channel, EXACTLY_ONCE, "Any payload");
    }

    // aka testPublishWithQoS1_notCleanSession
    @Test
    public void forwardQoS1PublishesWhenNotCleanSessionReconnects() {
        connection.processConnect(ConnectionTestUtils.buildConnectNotClean(PostOfficePublishTest.FAKE_CLIENT_ID));
        ConnectionTestUtils.assertConnectAccepted(channel);
        subscribe(connection, PostOfficePublishTest.NEWS_TOPIC, AT_LEAST_ONCE);
        connection.processDisconnect(null);
        // publish a QoS 1 message from another client publish a message on the topic
        EmbeddedChannel pubChannel = new EmbeddedChannel();
        MQTTConnection pubConn = createMQTTConnection(PostOfficePublishTest.ALLOW_ANONYMOUS_AND_ZERO_BYTES_CLID, pubChannel);
        pubConn.processConnect(ConnectionTestUtils.buildConnect(PostOfficePublishTest.PUBLISHER_ID));
        ConnectionTestUtils.assertConnectAccepted(pubChannel);
        final ByteBuf anyPayload = Unpooled.copiedBuffer("Any payload", Charset.defaultCharset());
        sut.receivedPublishQos1(pubConn, new Topic(PostOfficePublishTest.NEWS_TOPIC), PostOfficePublishTest.TEST_USER, anyPayload, 1, true, MqttMessageBuilders.publish().payload(anyPayload.retainedDuplicate()).qos(MqttQoS.AT_LEAST_ONCE).topicName(PostOfficePublishTest.NEWS_TOPIC).build());
        // simulate a reconnection from the other client
        connection = createMQTTConnection(PostOfficePublishTest.ALLOW_ANONYMOUS_AND_ZERO_BYTES_CLID);
        connectMessage = ConnectionTestUtils.buildConnectNotClean(PostOfficePublishTest.FAKE_CLIENT_ID);
        connection.processConnect(connectMessage);
        ConnectionTestUtils.assertConnectAccepted(channel);
        // Verify
        ConnectionTestUtils.verifyPublishIsReceived(channel, AT_LEAST_ONCE, "Any payload");
    }

    @Test
    public void checkReceivePublishedMessage_after_a_reconnect_with_notCleanSession() {
        // first connect - subscribe -disconnect
        connection.processConnect(ConnectionTestUtils.buildConnectNotClean(PostOfficePublishTest.FAKE_CLIENT_ID));
        ConnectionTestUtils.assertConnectAccepted(channel);
        subscribe(connection, PostOfficePublishTest.NEWS_TOPIC, AT_LEAST_ONCE);
        connection.processDisconnect(null);
        // connect - subscribe from another connection but with same ClientID
        EmbeddedChannel secondChannel = new EmbeddedChannel();
        MQTTConnection secondConn = createMQTTConnection(PostOfficePublishTest.ALLOW_ANONYMOUS_AND_ZERO_BYTES_CLID, secondChannel);
        secondConn.processConnect(ConnectionTestUtils.buildConnect(PostOfficePublishTest.FAKE_CLIENT_ID));
        ConnectionTestUtils.assertConnectAccepted(secondChannel);
        subscribe(secondConn, PostOfficePublishTest.NEWS_TOPIC, AT_LEAST_ONCE);
        // publish a QoS 1 message another client publish a message on the topic
        EmbeddedChannel pubChannel = new EmbeddedChannel();
        MQTTConnection pubConn = createMQTTConnection(PostOfficePublishTest.ALLOW_ANONYMOUS_AND_ZERO_BYTES_CLID, pubChannel);
        pubConn.processConnect(ConnectionTestUtils.buildConnect(PostOfficePublishTest.PUBLISHER_ID));
        ConnectionTestUtils.assertConnectAccepted(pubChannel);
        final ByteBuf anyPayload = Unpooled.copiedBuffer("Any payload", Charset.defaultCharset());
        sut.receivedPublishQos1(pubConn, new Topic(PostOfficePublishTest.NEWS_TOPIC), PostOfficePublishTest.TEST_USER, anyPayload, 1, true, MqttMessageBuilders.publish().payload(anyPayload.retainedDuplicate()).qos(MqttQoS.AT_LEAST_ONCE).topicName(PostOfficePublishTest.NEWS_TOPIC).build());
        // Verify that after a reconnection the client receive the message
        ConnectionTestUtils.verifyPublishIsReceived(secondChannel, AT_LEAST_ONCE, "Any payload");
    }

    @Test
    public void noPublishToInactiveSession() {
        // create an inactive session for Subscriber
        connection.processConnect(ConnectionTestUtils.buildConnectNotClean(PostOfficePublishTest.SUBSCRIBER_ID));
        ConnectionTestUtils.assertConnectAccepted(channel);
        subscribe(connection, PostOfficePublishTest.NEWS_TOPIC, AT_LEAST_ONCE);
        connection.processDisconnect(null);
        // Exercise
        EmbeddedChannel pubChannel = new EmbeddedChannel();
        MQTTConnection pubConn = createMQTTConnection(PostOfficePublishTest.ALLOW_ANONYMOUS_AND_ZERO_BYTES_CLID, pubChannel);
        pubConn.processConnect(ConnectionTestUtils.buildConnect(PostOfficePublishTest.PUBLISHER_ID));
        ConnectionTestUtils.assertConnectAccepted(pubChannel);
        final ByteBuf anyPayload = Unpooled.copiedBuffer("Any payload", Charset.defaultCharset());
        sut.receivedPublishQos1(pubConn, new Topic(PostOfficePublishTest.NEWS_TOPIC), PostOfficePublishTest.TEST_USER, anyPayload, 1, true, MqttMessageBuilders.publish().payload(anyPayload).qos(MqttQoS.AT_LEAST_ONCE).retained(true).topicName(PostOfficePublishTest.NEWS_TOPIC).build());
        verifyNoPublishIsReceived(channel);
    }

    @Test
    public void cleanRetainedMessageStoreWhenPublishWithRetainedQos0IsReceived() {
        connection.processConnect(connectMessage);
        ConnectionTestUtils.assertConnectAccepted(channel);
        // publish a QoS1 retained message
        final ByteBuf anyPayload = Unpooled.copiedBuffer("Any payload", Charset.defaultCharset());
        final MqttPublishMessage publishMsg = MqttMessageBuilders.publish().payload(Unpooled.copiedBuffer("Any payload", Charset.defaultCharset())).qos(MqttQoS.AT_LEAST_ONCE).retained(true).topicName(PostOfficePublishTest.NEWS_TOPIC).build();
        sut.receivedPublishQos1(connection, new Topic(PostOfficePublishTest.NEWS_TOPIC), PostOfficePublishTest.TEST_USER, anyPayload, 1, true, publishMsg);
        assertMessageIsRetained(PostOfficePublishTest.NEWS_TOPIC, anyPayload);
        // publish a QoS0 retained message
        // Exercise
        final ByteBuf qos0Payload = Unpooled.copiedBuffer("QoS0 payload", Charset.defaultCharset());
        sut.receivedPublishQos0(new Topic(PostOfficePublishTest.NEWS_TOPIC), PostOfficePublishTest.TEST_USER, connection.getClientId(), qos0Payload, true, MqttMessageBuilders.publish().payload(qos0Payload).qos(MqttQoS.AT_MOST_ONCE).retained(false).topicName(PostOfficePublishTest.NEWS_TOPIC).build());
        // Verify
        Assert.assertTrue("Retained message for topic /news must be cleared", retainedRepository.isEmpty());
    }
}

