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


import io.moquette.broker.security.IAuthenticator;
import io.moquette.broker.subscriptions.ISubscriptionsDirectory;
import io.moquette.broker.subscriptions.Topic;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.embedded.EmbeddedChannel;
import java.nio.charset.Charset;
import java.util.Collections;
import org.junit.Assert;
import org.junit.Test;


public class PostOfficeUnsubscribeTest {
    private static final String FAKE_CLIENT_ID = "FAKE_123";

    private static final String TEST_USER = "fakeuser";

    private static final String TEST_PWD = "fakepwd";

    static final String NEWS_TOPIC = "/news";

    private static final String BAD_FORMATTED_TOPIC = "#MQTTClient";

    private MQTTConnection connection;

    private EmbeddedChannel channel;

    private PostOffice sut;

    private ISubscriptionsDirectory subscriptions;

    private MqttConnectMessage connectMessage;

    private IAuthenticator mockAuthenticator;

    private SessionRegistry sessionRegistry;

    public static final BrokerConfiguration CONFIG = new BrokerConfiguration(true, true, false);

    private MemoryQueueRepository queueRepository;

    @Test
    public void testUnsubscribeWithBadFormattedTopic() {
        connect(this.connection, PostOfficeUnsubscribeTest.FAKE_CLIENT_ID);
        // Exercise
        sut.unsubscribe(Collections.singletonList(PostOfficeUnsubscribeTest.BAD_FORMATTED_TOPIC), connection, 1);
        // Verify
        Assert.assertFalse("Unsubscribe with bad topic MUST close drop the connection, (issue 68)", channel.isOpen());
    }

    @Test
    public void testDontNotifyClientSubscribedToTopicAfterDisconnectedAndReconnectOnSameChannel() {
        connect(this.connection, PostOfficeUnsubscribeTest.FAKE_CLIENT_ID);
        subscribe(connection, PostOfficeUnsubscribeTest.NEWS_TOPIC, AT_MOST_ONCE);
        // publish on /news
        final ByteBuf payload = Unpooled.copiedBuffer("Hello world!", Charset.defaultCharset());
        sut.receivedPublishQos0(new Topic(PostOfficeUnsubscribeTest.NEWS_TOPIC), PostOfficeUnsubscribeTest.TEST_USER, PostOfficeUnsubscribeTest.TEST_PWD, payload, false, MqttMessageBuilders.publish().payload(payload.retainedDuplicate()).qos(MqttQoS.AT_MOST_ONCE).retained(false).topicName(PostOfficeUnsubscribeTest.NEWS_TOPIC).build());
        ConnectionTestUtils.verifyPublishIsReceived(channel, AT_MOST_ONCE, "Hello world!");
        unsubscribeAndVerifyAck(PostOfficeUnsubscribeTest.NEWS_TOPIC);
        // publish on /news
        final ByteBuf payload2 = Unpooled.copiedBuffer("Hello world!", Charset.defaultCharset());
        sut.receivedPublishQos0(new Topic(PostOfficeUnsubscribeTest.NEWS_TOPIC), PostOfficeUnsubscribeTest.TEST_USER, PostOfficeUnsubscribeTest.TEST_PWD, payload2, false, MqttMessageBuilders.publish().payload(payload).qos(MqttQoS.AT_MOST_ONCE).retained(false).topicName(PostOfficeUnsubscribeTest.NEWS_TOPIC).build());
        ConnectionTestUtils.verifyNoPublishIsReceived(channel);
    }

    @Test
    public void testDontNotifyClientSubscribedToTopicAfterDisconnectedAndReconnectOnNewChannel() {
        connect(this.connection, PostOfficeUnsubscribeTest.FAKE_CLIENT_ID);
        subscribe(connection, PostOfficeUnsubscribeTest.NEWS_TOPIC, AT_MOST_ONCE);
        // publish on /news
        final ByteBuf payload = Unpooled.copiedBuffer("Hello world!", Charset.defaultCharset());
        sut.receivedPublishQos0(new Topic(PostOfficeUnsubscribeTest.NEWS_TOPIC), PostOfficeUnsubscribeTest.TEST_USER, PostOfficeUnsubscribeTest.TEST_PWD, payload, false, MqttMessageBuilders.publish().payload(payload.retainedDuplicate()).qos(MqttQoS.AT_MOST_ONCE).retained(false).topicName(PostOfficeUnsubscribeTest.NEWS_TOPIC).build());
        ConnectionTestUtils.verifyPublishIsReceived(channel, AT_MOST_ONCE, "Hello world!");
        unsubscribeAndVerifyAck(PostOfficeUnsubscribeTest.NEWS_TOPIC);
        connection.processDisconnect(null);
        // connect on another channel
        EmbeddedChannel anotherChannel = new EmbeddedChannel();
        MQTTConnection anotherConnection = createMQTTConnection(PostOfficeUnsubscribeTest.CONFIG, anotherChannel);
        anotherConnection.processConnect(connectMessage);
        ConnectionTestUtils.assertConnectAccepted(anotherChannel);
        // publish on /news
        final ByteBuf payload2 = Unpooled.copiedBuffer("Hello world!", Charset.defaultCharset());
        sut.receivedPublishQos0(new Topic(PostOfficeUnsubscribeTest.NEWS_TOPIC), PostOfficeUnsubscribeTest.TEST_USER, PostOfficeUnsubscribeTest.TEST_PWD, payload2, false, MqttMessageBuilders.publish().payload(payload2).qos(MqttQoS.AT_MOST_ONCE).retained(false).topicName(PostOfficeUnsubscribeTest.NEWS_TOPIC).build());
        ConnectionTestUtils.verifyNoPublishIsReceived(anotherChannel);
    }

    @Test
    public void avoidMultipleNotificationsAfterMultipleReconnection_cleanSessionFalseQoS1() {
        final MqttConnectMessage notCleanConnect = ConnectionTestUtils.buildConnectNotClean(PostOfficeUnsubscribeTest.FAKE_CLIENT_ID);
        connect(connection, notCleanConnect);
        subscribe(connection, PostOfficeUnsubscribeTest.NEWS_TOPIC, AT_LEAST_ONCE);
        connection.processDisconnect(null);
        // connect on another channel
        final String firstPayload = "Hello MQTT 1";
        connectPublishDisconnectFromAnotherClient(firstPayload, PostOfficeUnsubscribeTest.NEWS_TOPIC);
        // reconnect FAKE_CLIENT on another channel
        EmbeddedChannel anotherChannel2 = new EmbeddedChannel();
        MQTTConnection anotherConnection2 = createMQTTConnection(PostOfficeUnsubscribeTest.CONFIG, anotherChannel2);
        anotherConnection2.processConnect(notCleanConnect);
        ConnectionTestUtils.assertConnectAccepted(anotherChannel2);
        ConnectionTestUtils.verifyPublishIsReceived(anotherChannel2, MqttQoS.AT_LEAST_ONCE, firstPayload);
        anotherConnection2.processDisconnect(null);
        final String secondPayload = "Hello MQTT 2";
        connectPublishDisconnectFromAnotherClient(secondPayload, PostOfficeUnsubscribeTest.NEWS_TOPIC);
        EmbeddedChannel anotherChannel3 = new EmbeddedChannel();
        MQTTConnection anotherConnection3 = createMQTTConnection(PostOfficeUnsubscribeTest.CONFIG, anotherChannel3);
        anotherConnection3.processConnect(notCleanConnect);
        ConnectionTestUtils.assertConnectAccepted(anotherChannel3);
        ConnectionTestUtils.verifyPublishIsReceived(anotherChannel3, MqttQoS.AT_LEAST_ONCE, secondPayload);
    }

    @Test
    public void testConnectSubPub_cycle_getTimeout_on_second_disconnect_issue142() {
        connect(connection, PostOfficeUnsubscribeTest.FAKE_CLIENT_ID);
        subscribe(connection, PostOfficeUnsubscribeTest.NEWS_TOPIC, AT_MOST_ONCE);
        // publish on /news
        final ByteBuf payload = Unpooled.copiedBuffer("Hello world!", Charset.defaultCharset());
        sut.receivedPublishQos0(new Topic(PostOfficeUnsubscribeTest.NEWS_TOPIC), PostOfficeUnsubscribeTest.TEST_USER, PostOfficeUnsubscribeTest.TEST_PWD, payload, false, MqttMessageBuilders.publish().payload(payload.retainedDuplicate()).qos(MqttQoS.AT_MOST_ONCE).retained(false).topicName(PostOfficeUnsubscribeTest.NEWS_TOPIC).build());
        ConnectionTestUtils.verifyPublishIsReceived(((EmbeddedChannel) (connection.channel)), AT_MOST_ONCE, "Hello world!");
        connection.processDisconnect(null);
        final MqttConnectMessage notCleanConnect = ConnectionTestUtils.buildConnect(PostOfficeUnsubscribeTest.FAKE_CLIENT_ID);
        EmbeddedChannel subscriberChannel = new EmbeddedChannel();
        MQTTConnection subscriberConnection = createMQTTConnection(PostOfficeUnsubscribeTest.CONFIG, subscriberChannel);
        subscriberConnection.processConnect(notCleanConnect);
        ConnectionTestUtils.assertConnectAccepted(subscriberChannel);
        subscribe(subscriberConnection, PostOfficeUnsubscribeTest.NEWS_TOPIC, AT_MOST_ONCE);
        // publish on /news
        final ByteBuf payload2 = Unpooled.copiedBuffer("Hello world2!", Charset.defaultCharset());
        sut.receivedPublishQos0(new Topic(PostOfficeUnsubscribeTest.NEWS_TOPIC), PostOfficeUnsubscribeTest.TEST_USER, PostOfficeUnsubscribeTest.TEST_PWD, payload2, false, MqttMessageBuilders.publish().payload(payload2.retainedDuplicate()).qos(MqttQoS.AT_MOST_ONCE).retained(false).topicName(PostOfficeUnsubscribeTest.NEWS_TOPIC).build());
        ConnectionTestUtils.verifyPublishIsReceived(subscriberChannel, AT_MOST_ONCE, "Hello world2!");
        subscriberConnection.processDisconnect(null);
        Assert.assertFalse("after a disconnect the client should be disconnected", subscriberChannel.isOpen());
    }

    @Test
    public void checkReplayofStoredPublishResumeAfter_a_disconnect_cleanSessionFalseQoS1() {
        final MQTTConnection publisher = connectAs("Publisher");
        connect(this.connection, PostOfficeUnsubscribeTest.FAKE_CLIENT_ID);
        subscribe(connection, PostOfficeUnsubscribeTest.NEWS_TOPIC, AT_LEAST_ONCE);
        // publish from another channel
        publishQos1(publisher, PostOfficeUnsubscribeTest.NEWS_TOPIC, "Hello world MQTT!!-1", 99);
        ConnectionTestUtils.verifyPublishIsReceived(channel, AT_LEAST_ONCE, "Hello world MQTT!!-1");
        connection.processDisconnect(null);
        publishQos1(publisher, PostOfficeUnsubscribeTest.NEWS_TOPIC, "Hello world MQTT!!-2", 100);
        publishQos1(publisher, PostOfficeUnsubscribeTest.NEWS_TOPIC, "Hello world MQTT!!-3", 101);
        createMQTTConnection(PostOfficeUnsubscribeTest.CONFIG);
        connect(this.connection, PostOfficeUnsubscribeTest.FAKE_CLIENT_ID);
        ConnectionTestUtils.verifyPublishIsReceived(channel, AT_LEAST_ONCE, "Hello world MQTT!!-2");
        ConnectionTestUtils.verifyPublishIsReceived(channel, AT_LEAST_ONCE, "Hello world MQTT!!-3");
    }

    /**
     * subscriber connect and subscribe on "topic" subscriber disconnects publisher connects and
     * send two message "hello1" "hello2" to "topic" subscriber connects again and receive "hello1"
     * "hello2"
     */
    @Test
    public void checkQoS2SubscriberDisconnectReceivePersistedPublishes() {
        connect(this.connection, PostOfficeUnsubscribeTest.FAKE_CLIENT_ID);
        subscribe(connection, PostOfficeUnsubscribeTest.NEWS_TOPIC, EXACTLY_ONCE);
        connection.processDisconnect(null);
        final MQTTConnection publisher = connectAs("Publisher");
        publishQos2(publisher, PostOfficeUnsubscribeTest.NEWS_TOPIC, "Hello world MQTT!!-1");
        publishQos2(publisher, PostOfficeUnsubscribeTest.NEWS_TOPIC, "Hello world MQTT!!-2");
        createMQTTConnection(PostOfficeUnsubscribeTest.CONFIG);
        connect(this.connection, PostOfficeUnsubscribeTest.FAKE_CLIENT_ID);
        ConnectionTestUtils.verifyPublishIsReceived(channel, EXACTLY_ONCE, "Hello world MQTT!!-1");
        ConnectionTestUtils.verifyPublishIsReceived(channel, EXACTLY_ONCE, "Hello world MQTT!!-2");
    }

    /**
     * subscriber connect and subscribe on "a/b" QoS 1 and "a/+" QoS 2 publisher connects and send a
     * message "hello" on "a/b" subscriber must receive only a single message not twice
     */
    @Test
    public void checkSinglePublishOnOverlappingSubscriptions() {
        final MQTTConnection publisher = connectAs("Publisher");
        connect(this.connection, PostOfficeUnsubscribeTest.FAKE_CLIENT_ID);
        subscribe(connection, "a/b", AT_LEAST_ONCE);
        subscribe(connection, "a/+", EXACTLY_ONCE);
        // force the publisher to send
        publishQos1(publisher, "a/b", "Hello world MQTT!!", 60);
        ConnectionTestUtils.verifyPublishIsReceived(channel, AT_LEAST_ONCE, "Hello world MQTT!!");
        ConnectionTestUtils.verifyNoPublishIsReceived(channel);
    }
}

