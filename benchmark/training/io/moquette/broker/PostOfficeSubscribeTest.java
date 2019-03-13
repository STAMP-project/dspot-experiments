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


import MqttQoS.AT_LEAST_ONCE;
import io.moquette.broker.security.IAuthenticator;
import io.moquette.broker.security.IAuthorizatorPolicy;
import io.moquette.broker.subscriptions.ISubscriptionsDirectory;
import io.moquette.broker.subscriptions.Subscription;
import io.moquette.broker.subscriptions.Topic;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.embedded.EmbeddedChannel;
import java.nio.charset.Charset;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

import static MqttQoS.AT_LEAST_ONCE;


public class PostOfficeSubscribeTest {
    private static final String FAKE_CLIENT_ID = "FAKE_123";

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

    private IAuthenticator mockAuthenticator;

    private SessionRegistry sessionRegistry;

    public static final BrokerConfiguration CONFIG = new BrokerConfiguration(true, true, false);

    private MemoryQueueRepository queueRepository;

    @Test
    public void testSubscribe() {
        connection.processConnect(connectMessage);
        ConnectionTestUtils.assertConnectAccepted(channel);
        // Exercise & verify
        subscribe(channel, PostOfficeSubscribeTest.NEWS_TOPIC, AT_MOST_ONCE);
    }

    @Test
    public void testSubscribedToNotAuthorizedTopic() {
        NettyUtils.userName(channel, PostOfficeSubscribeTest.FAKE_USER_NAME);
        IAuthorizatorPolicy prohibitReadOnNewsTopic = Mockito.mock(IAuthorizatorPolicy.class);
        Mockito.when(prohibitReadOnNewsTopic.canRead(ArgumentMatchers.eq(new Topic(PostOfficeSubscribeTest.NEWS_TOPIC)), ArgumentMatchers.eq(PostOfficeSubscribeTest.FAKE_USER_NAME), ArgumentMatchers.eq(PostOfficeSubscribeTest.FAKE_CLIENT_ID))).thenReturn(false);
        sut = new PostOffice(subscriptions, new MemoryRetainedRepository(), sessionRegistry, ConnectionTestUtils.NO_OBSERVERS_INTERCEPTOR, new Authorizator(prohibitReadOnNewsTopic));
        connection.processConnect(connectMessage);
        ConnectionTestUtils.assertConnectAccepted(channel);
        // Exercise
        MqttSubscribeMessage subscribe = MqttMessageBuilders.subscribe().addSubscription(AT_MOST_ONCE, PostOfficeSubscribeTest.NEWS_TOPIC).messageId(1).build();
        sut.subscribeClientToTopics(subscribe, PostOfficeSubscribeTest.FAKE_CLIENT_ID, PostOfficeSubscribeTest.FAKE_USER_NAME, connection);
        // Verify
        MqttSubAckMessage subAckMsg = channel.readOutbound();
        verifyFailureQos(subAckMsg);
    }

    @Test
    public void testDoubleSubscribe() {
        connection.processConnect(connectMessage);
        ConnectionTestUtils.assertConnectAccepted(channel);
        Assert.assertEquals("After CONNECT subscription MUST be empty", 0, subscriptions.size());
        subscribe(channel, PostOfficeSubscribeTest.NEWS_TOPIC, AT_MOST_ONCE);
        Assert.assertEquals("After /news subscribe, subscription MUST contain it", 1, subscriptions.size());
        // Exercise & verify
        subscribe(channel, PostOfficeSubscribeTest.NEWS_TOPIC, AT_MOST_ONCE);
    }

    @Test
    public void testSubscribeWithBadFormattedTopic() {
        connection.processConnect(connectMessage);
        ConnectionTestUtils.assertConnectAccepted(channel);
        Assert.assertEquals("After CONNECT subscription MUST be empty", 0, subscriptions.size());
        // Exercise
        MqttSubscribeMessage subscribe = MqttMessageBuilders.subscribe().addSubscription(AT_MOST_ONCE, PostOfficeSubscribeTest.BAD_FORMATTED_TOPIC).messageId(1).build();
        this.sut.subscribeClientToTopics(subscribe, PostOfficeSubscribeTest.FAKE_CLIENT_ID, PostOfficeSubscribeTest.FAKE_USER_NAME, connection);
        MqttSubAckMessage subAckMsg = channel.readOutbound();
        Assert.assertEquals("Bad topic CAN'T add any subscription", 0, subscriptions.size());
        verifyFailureQos(subAckMsg);
    }

    @Test
    public void testCleanSession_maintainClientSubscriptions() {
        connection.processConnect(connectMessage);
        ConnectionTestUtils.assertConnectAccepted(channel);
        Assert.assertEquals("After CONNECT subscription MUST be empty", 0, subscriptions.size());
        subscribe(channel, PostOfficeSubscribeTest.NEWS_TOPIC, AT_MOST_ONCE);
        Assert.assertEquals("Subscribe MUST contain one subscription", 1, subscriptions.size());
        connection.processDisconnect(null);
        Assert.assertEquals("Disconnection MUSTN'T clear subscriptions", 1, subscriptions.size());
        EmbeddedChannel anotherChannel = new EmbeddedChannel();
        MQTTConnection anotherConn = createMQTTConnection(PostOfficePublishTest.ALLOW_ANONYMOUS_AND_ZERO_BYTES_CLID, anotherChannel);
        anotherConn.processConnect(ConnectionTestUtils.buildConnect(PostOfficeSubscribeTest.FAKE_CLIENT_ID));
        ConnectionTestUtils.assertConnectAccepted(anotherChannel);
        Assert.assertEquals("After a reconnect, subscription MUST be still present", 1, subscriptions.size());
        final ByteBuf payload = Unpooled.copiedBuffer("Hello world!", Charset.defaultCharset());
        sut.receivedPublishQos0(new Topic(PostOfficeSubscribeTest.NEWS_TOPIC), PostOfficeSubscribeTest.TEST_USER, PostOfficeSubscribeTest.TEST_PWD, payload, false, MqttMessageBuilders.publish().payload(payload.retainedDuplicate()).qos(MqttQoS.AT_MOST_ONCE).retained(false).topicName(PostOfficeSubscribeTest.NEWS_TOPIC).build());
        ConnectionTestUtils.verifyPublishIsReceived(anotherChannel, AT_MOST_ONCE, "Hello world!");
    }

    /**
     * Check that after a client has connected with clean session false, subscribed to some topic
     * and exited, if it reconnects with clean session true, the broker correctly cleanup every
     * previous subscription
     */
    @Test
    public void testCleanSession_correctlyClientSubscriptions() {
        connection.processConnect(connectMessage);
        ConnectionTestUtils.assertConnectAccepted(channel);
        Assert.assertEquals("After CONNECT subscription MUST be empty", 0, subscriptions.size());
        // subscribe(channel, NEWS_TOPIC, AT_MOST_ONCE);
        final MqttSubscribeMessage subscribeMsg = MqttMessageBuilders.subscribe().addSubscription(AT_MOST_ONCE, PostOfficeSubscribeTest.NEWS_TOPIC).messageId(1).build();
        connection.processSubscribe(subscribeMsg);
        Assert.assertEquals("Subscribe MUST contain one subscription", 1, subscriptions.size());
        connection.processDisconnect(null);
        Assert.assertEquals("Disconnection MUSTN'T clear subscriptions", 1, subscriptions.size());
        connectMessage = MqttMessageBuilders.connect().clientId(PostOfficeSubscribeTest.FAKE_CLIENT_ID).cleanSession(true).build();
        channel = new EmbeddedChannel();
        connection = createMQTTConnection(PostOfficeSubscribeTest.CONFIG, channel);
        connection.processConnect(connectMessage);
        ConnectionTestUtils.assertConnectAccepted(channel);
        Assert.assertEquals("After CONNECT with clean, subscription MUST be empty", 0, subscriptions.size());
        // publish on /news
        final ByteBuf payload = Unpooled.copiedBuffer("Hello world!", Charset.defaultCharset());
        sut.receivedPublishQos0(new Topic(PostOfficeSubscribeTest.NEWS_TOPIC), PostOfficeSubscribeTest.TEST_USER, PostOfficeSubscribeTest.TEST_PWD, payload, false, MqttMessageBuilders.publish().payload(payload).qos(MqttQoS.AT_MOST_ONCE).retained(false).topicName(PostOfficeSubscribeTest.NEWS_TOPIC).build());
        // verify no publish is fired
        ConnectionTestUtils.verifyNoPublishIsReceived(channel);
    }

    @Test
    public void testReceiveRetainedPublishRespectingSubscriptionQoSAndNotPublisher() {
        // publisher publish a retained message on topic /news
        connection.processConnect(connectMessage);
        ConnectionTestUtils.assertConnectAccepted(channel);
        final ByteBuf payload = Unpooled.copiedBuffer("Hello world!", Charset.defaultCharset());
        final MqttPublishMessage retainedPubQoS1Msg = MqttMessageBuilders.publish().payload(payload.retainedDuplicate()).qos(AT_LEAST_ONCE).topicName(PostOfficeSubscribeTest.NEWS_TOPIC).build();
        sut.receivedPublishQos1(connection, new Topic(PostOfficeSubscribeTest.NEWS_TOPIC), PostOfficeSubscribeTest.TEST_USER, payload, 1, true, retainedPubQoS1Msg);
        // subscriber connects subscribe to topic /news and receive the last retained message
        EmbeddedChannel subChannel = new EmbeddedChannel();
        MQTTConnection subConn = createMQTTConnection(PostOfficePublishTest.ALLOW_ANONYMOUS_AND_ZERO_BYTES_CLID, subChannel);
        subConn.processConnect(ConnectionTestUtils.buildConnect(PostOfficePublishTest.SUBSCRIBER_ID));
        ConnectionTestUtils.assertConnectAccepted(subChannel);
        subscribe(subConn, PostOfficeSubscribeTest.NEWS_TOPIC, MqttQoS.AT_MOST_ONCE);
        // Verify publish is received
        ConnectionTestUtils.verifyReceiveRetainedPublish(subChannel, PostOfficeSubscribeTest.NEWS_TOPIC, "Hello world!", MqttQoS.AT_MOST_ONCE);
    }

    @Test
    public void testLowerTheQosToTheRequestedBySubscription() {
        Subscription subQos1 = new Subscription("Sub A", new Topic("a/b"), AT_LEAST_ONCE);
        Assert.assertEquals(AT_LEAST_ONCE, PostOffice.lowerQosToTheSubscriptionDesired(subQos1, EXACTLY_ONCE));
        Subscription subQos2 = new Subscription("Sub B", new Topic("a/+"), EXACTLY_ONCE);
        Assert.assertEquals(EXACTLY_ONCE, PostOffice.lowerQosToTheSubscriptionDesired(subQos2, EXACTLY_ONCE));
    }
}

