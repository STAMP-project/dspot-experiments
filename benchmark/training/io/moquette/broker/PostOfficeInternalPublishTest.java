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


import io.moquette.broker.subscriptions.ISubscriptionsDirectory;
import io.netty.channel.embedded.EmbeddedChannel;
import org.junit.Assert;
import org.junit.Test;


public class PostOfficeInternalPublishTest {
    private static final String FAKE_CLIENT_ID = "FAKE_123";

    private static final String TEST_USER = "fakeuser";

    private static final String TEST_PWD = "fakepwd";

    private static final String PAYLOAD = "Hello MQTT World";

    private MQTTConnection connection;

    private EmbeddedChannel channel;

    private PostOffice sut;

    private ISubscriptionsDirectory subscriptions;

    private MqttConnectMessage connectMessage;

    private SessionRegistry sessionRegistry;

    private MockAuthenticator mockAuthenticator;

    private static final BrokerConfiguration ALLOW_ANONYMOUS_AND_ZERO_BYTES_CLID = new BrokerConfiguration(true, true, false);

    private MemoryRetainedRepository retainedRepository;

    private MemoryQueueRepository queueRepository;

    @Test
    public void testClientSubscribeAfterNotRetainedQoS0IsSent() {
        // connection.processConnect(connectMessage);
        // ConnectionTestUtils.assertConnectAccepted(channel);
        // Exercise
        final String topic = "/topic";
        internalPublishNotRetainedTo(topic);
        subscribe(AT_MOST_ONCE, topic, connection);
        // Verify
        verifyNoPublishIsReceived(channel);
    }

    @Test
    public void testClientSubscribeAfterRetainedQoS0IsSent() {
        // Exercise
        final String topic = "/topic";
        internalPublishRetainedTo(topic);
        subscribe(AT_MOST_ONCE, topic, connection);
        // Verify
        verifyNoPublishIsReceived(channel);
    }

    @Test
    public void testClientSubscribeBeforeNotRetainedQoS0IsSent() {
        subscribe(AT_MOST_ONCE, "/topic", connection);
        // Exercise
        internalPublishNotRetainedTo("/topic");
        // Verify
        ConnectionTestUtils.verifyPublishIsReceived(channel, AT_MOST_ONCE, PostOfficeInternalPublishTest.PAYLOAD);
    }

    @Test
    public void testClientSubscribeBeforeRetainedQoS0IsSent() {
        subscribe(AT_MOST_ONCE, "/topic", connection);
        // Exercise
        internalPublishRetainedTo("/topic");
        // Verify
        ConnectionTestUtils.verifyPublishIsReceived(channel, AT_MOST_ONCE, PostOfficeInternalPublishTest.PAYLOAD);
    }

    @Test
    public void testClientSubscribeBeforeNotRetainedQoS1IsSent() {
        subscribe(AT_LEAST_ONCE, "/topic", connection);
        // Exercise
        internalPublishTo("/topic", AT_LEAST_ONCE, false);
        // Verify
        ConnectionTestUtils.verifyPublishIsReceived(channel, AT_LEAST_ONCE, PostOfficeInternalPublishTest.PAYLOAD);
    }

    @Test
    public void testClientSubscribeAfterNotRetainedQoS1IsSent() {
        // Exercise
        internalPublishTo("/topic", AT_LEAST_ONCE, false);
        subscribe(AT_LEAST_ONCE, "/topic", connection);
        // Verify
        verifyNoPublishIsReceived(channel);
    }

    @Test
    public void testClientSubscribeBeforeRetainedQoS1IsSent() {
        subscribe(AT_LEAST_ONCE, "/topic", connection);
        // Exercise
        internalPublishTo("/topic", AT_LEAST_ONCE, true);
        // Verify
        ConnectionTestUtils.verifyPublishIsReceived(channel, AT_LEAST_ONCE, PostOfficeInternalPublishTest.PAYLOAD);
    }

    @Test
    public void testClientSubscribeAfterRetainedQoS1IsSent() {
        // Exercise
        internalPublishTo("/topic", AT_LEAST_ONCE, true);
        subscribe(AT_LEAST_ONCE, "/topic", connection);
        // Verify
        ConnectionTestUtils.verifyPublishIsReceived(channel, AT_LEAST_ONCE, PostOfficeInternalPublishTest.PAYLOAD);
    }

    @Test
    public void testClientSubscribeBeforeNotRetainedQoS2IsSent() {
        subscribe(EXACTLY_ONCE, "/topic", connection);
        // Exercise
        internalPublishTo("/topic", EXACTLY_ONCE, false);
        // Verify
        ConnectionTestUtils.verifyPublishIsReceived(channel, EXACTLY_ONCE, PostOfficeInternalPublishTest.PAYLOAD);
    }

    @Test
    public void testClientSubscribeAfterNotRetainedQoS2IsSent() {
        // Exercise
        internalPublishTo("/topic", EXACTLY_ONCE, false);
        subscribe(EXACTLY_ONCE, "/topic", connection);
        // Verify
        verifyNoPublishIsReceived(channel);
    }

    @Test
    public void testClientSubscribeBeforeRetainedQoS2IsSent() {
        subscribe(EXACTLY_ONCE, "/topic", connection);
        // Exercise
        internalPublishTo("/topic", EXACTLY_ONCE, true);
        // Verify
        ConnectionTestUtils.verifyPublishIsReceived(channel, EXACTLY_ONCE, PostOfficeInternalPublishTest.PAYLOAD);
    }

    @Test
    public void testClientSubscribeAfterRetainedQoS2IsSent() {
        // Exercise
        internalPublishTo("/topic", EXACTLY_ONCE, true);
        subscribe(EXACTLY_ONCE, "/topic", connection);
        // Verify
        ConnectionTestUtils.verifyPublishIsReceived(channel, EXACTLY_ONCE, PostOfficeInternalPublishTest.PAYLOAD);
    }

    @Test
    public void testClientSubscribeAfterDisconnected() {
        subscribe(AT_MOST_ONCE, "foo", connection);
        connection.processDisconnect(null);
        internalPublishTo("foo", AT_MOST_ONCE, false);
        verifyNoPublishIsReceived(channel);
    }

    @Test
    public void testClientSubscribeWithoutCleanSession() {
        subscribe(AT_MOST_ONCE, "foo", connection);
        connection.processDisconnect(null);
        Assert.assertEquals(1, subscriptions.size());
        MQTTConnection anotherConn = createMQTTConnection(PostOfficeUnsubscribeTest.CONFIG);
        MqttConnectMessage connectMessage = MqttMessageBuilders.connect().clientId(PostOfficeInternalPublishTest.FAKE_CLIENT_ID).cleanSession(false).build();
        anotherConn.processConnect(connectMessage);
        ConnectionTestUtils.assertConnectAccepted(((EmbeddedChannel) (anotherConn.channel)));
        Assert.assertEquals(1, subscriptions.size());
        internalPublishTo("foo", MqttQoS.AT_MOST_ONCE, false);
        ConnectionTestUtils.verifyPublishIsReceived(((EmbeddedChannel) (anotherConn.channel)), AT_MOST_ONCE, PostOfficeInternalPublishTest.PAYLOAD);
    }
}

