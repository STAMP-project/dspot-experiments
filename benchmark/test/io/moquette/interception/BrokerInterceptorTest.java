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
package io.moquette.interception;


import MqttQoS.AT_MOST_ONCE;
import io.moquette.broker.subscriptions.Subscription;
import io.moquette.broker.subscriptions.Topic;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.mqtt.MqttMessageBuilders;
import io.netty.handler.codec.mqtt.MqttQoS;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import static InterceptHandler.ALL_MESSAGE_TYPES;
import static org.mockito.ArgumentMatchers.refEq;


public class BrokerInterceptorTest {
    // value to check for changes after every notification
    private static final AtomicInteger n = new AtomicInteger(0);

    // Interceptor loaded with a custom InterceptHandler special for the tests
    private static final class MockObserver implements InterceptHandler {
        @Override
        public String getID() {
            return "MockObserver";
        }

        @Override
        public Class<?>[] getInterceptedMessageTypes() {
            return ALL_MESSAGE_TYPES;
        }

        @Override
        public void onConnect(InterceptConnectMessage msg) {
            BrokerInterceptorTest.n.set(40);
        }

        @Override
        public void onDisconnect(InterceptDisconnectMessage msg) {
            BrokerInterceptorTest.n.set(50);
        }

        @Override
        public void onConnectionLost(InterceptConnectionLostMessage msg) {
            BrokerInterceptorTest.n.set(30);
        }

        @Override
        public void onPublish(InterceptPublishMessage msg) {
            BrokerInterceptorTest.n.set(60);
        }

        @Override
        public void onSubscribe(InterceptSubscribeMessage msg) {
            BrokerInterceptorTest.n.set(70);
        }

        @Override
        public void onUnsubscribe(InterceptUnsubscribeMessage msg) {
            BrokerInterceptorTest.n.set(80);
        }

        @Override
        public void onMessageAcknowledged(InterceptAcknowledgedMessage msg) {
            BrokerInterceptorTest.n.set(90);
        }
    }

    private static final BrokerInterceptor interceptor = new BrokerInterceptor(Collections.<InterceptHandler>singletonList(new BrokerInterceptorTest.MockObserver()));

    @Test
    public void testNotifyClientConnected() throws Exception {
        BrokerInterceptorTest.interceptor.notifyClientConnected(MqttMessageBuilders.connect().build());
        BrokerInterceptorTest.interval();
        Assert.assertEquals(40, BrokerInterceptorTest.n.get());
    }

    @Test
    public void testNotifyClientDisconnected() throws Exception {
        BrokerInterceptorTest.interceptor.notifyClientDisconnected("cli1234", "cli1234");
        BrokerInterceptorTest.interval();
        Assert.assertEquals(50, BrokerInterceptorTest.n.get());
    }

    @Test
    public void testNotifyTopicPublished() throws Exception {
        BrokerInterceptorTest.interceptor.notifyTopicPublished(MqttMessageBuilders.publish().qos(AT_MOST_ONCE).payload(Unpooled.copiedBuffer("Hello".getBytes(StandardCharsets.UTF_8))).build(), "cli1234", "cli1234");
        BrokerInterceptorTest.interval();
        Assert.assertEquals(60, BrokerInterceptorTest.n.get());
    }

    @Test
    public void testNotifyTopicSubscribed() throws Exception {
        BrokerInterceptorTest.interceptor.notifyTopicSubscribed(new Subscription("cli1", new Topic("o2"), MqttQoS.AT_MOST_ONCE), "cli1234");
        BrokerInterceptorTest.interval();
        Assert.assertEquals(70, BrokerInterceptorTest.n.get());
    }

    @Test
    public void testNotifyTopicUnsubscribed() throws Exception {
        BrokerInterceptorTest.interceptor.notifyTopicUnsubscribed("o2", "cli1234", "cli1234");
        BrokerInterceptorTest.interval();
        Assert.assertEquals(80, BrokerInterceptorTest.n.get());
    }

    @Test
    public void testAddAndRemoveInterceptHandler() throws Exception {
        InterceptHandler interceptHandlerMock1 = Mockito.mock(InterceptHandler.class);
        InterceptHandler interceptHandlerMock2 = Mockito.mock(InterceptHandler.class);
        // add
        BrokerInterceptorTest.interceptor.addInterceptHandler(interceptHandlerMock1);
        BrokerInterceptorTest.interceptor.addInterceptHandler(interceptHandlerMock2);
        Subscription subscription = new Subscription("cli1", new Topic("o2"), MqttQoS.AT_MOST_ONCE);
        BrokerInterceptorTest.interceptor.notifyTopicSubscribed(subscription, "cli1234");
        BrokerInterceptorTest.interval();
        Mockito.verify(interceptHandlerMock1).onSubscribe(refEq(new InterceptSubscribeMessage(subscription, "cli1234")));
        Mockito.verify(interceptHandlerMock2).onSubscribe(refEq(new InterceptSubscribeMessage(subscription, "cli1234")));
        // remove
        BrokerInterceptorTest.interceptor.removeInterceptHandler(interceptHandlerMock1);
        BrokerInterceptorTest.interceptor.notifyTopicSubscribed(subscription, "cli1235");
        BrokerInterceptorTest.interval();
        // removeInterceptHandler() performs another interaction
        // TODO: fix this
        // verifyNoMoreInteractions(interceptHandlerMock1);
        Mockito.verify(interceptHandlerMock2).onSubscribe(refEq(new InterceptSubscribeMessage(subscription, "cli1235")));
    }
}

