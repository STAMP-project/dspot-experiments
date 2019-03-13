/**
 * Copyright (c) 2010-2019 Contributors to the openHAB project
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.openhab.binding.mqtt.internal;


import java.util.concurrent.ScheduledExecutorService;
import org.eclipse.smarthome.core.thing.Bridge;
import org.eclipse.smarthome.core.thing.binding.ThingHandlerCallback;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.openhab.binding.mqtt.discovery.MQTTTopicDiscoveryParticipant;
import org.openhab.binding.mqtt.handler.BrokerHandler;
import org.openhab.binding.mqtt.handler.BrokerHandlerEx;
import org.openhab.binding.mqtt.handler.MqttBrokerConnectionEx;


/**
 * Test cases for the {@link MQTTTopicDiscoveryService} service.
 *
 * @author David Graeff - Initial contribution
 */
public class MQTTTopicDiscoveryServiceTest {
    private ScheduledExecutorService scheduler;

    private MqttBrokerHandlerFactory subject;

    @Mock
    private Bridge thing;

    @Mock
    private ThingHandlerCallback callback;

    @Mock
    MQTTTopicDiscoveryParticipant listener;

    private MqttBrokerConnectionEx connection;

    private BrokerHandler handler;

    @Test
    public void firstSubscribeThenHandler() {
        handler.initialize();
        BrokerHandlerEx.verifyCreateBrokerConnection(handler, 1);
        subject.subscribe(listener, "topic");
        subject.createdHandler(handler);
        Assert.assertThat(subject.subscriber.get(listener).topic, CoreMatchers.is("topic"));
        // Simulate receiving
        final byte[] bytes = "TEST".getBytes();
        subject.subscriber.get(listener).observedBrokerHandlers.get(thing.getUID()).processMessage("topic", bytes);
        Mockito.verify(listener).receivedMessage(ArgumentMatchers.eq(thing.getUID()), ArgumentMatchers.eq(connection), ArgumentMatchers.eq("topic"), ArgumentMatchers.eq(bytes));
    }

    @Test
    public void firstHandlerThanSubscribe() {
        handler.initialize();
        BrokerHandlerEx.verifyCreateBrokerConnection(handler, 1);
        subject.createdHandler(handler);
        subject.subscribe(listener, "topic");
        Assert.assertThat(subject.subscriber.get(listener).topic, CoreMatchers.is("topic"));
        // Simulate receiving
        final byte[] bytes = "TEST".getBytes();
        subject.subscriber.get(listener).observedBrokerHandlers.get(thing.getUID()).processMessage("topic", bytes);
        Mockito.verify(listener).receivedMessage(ArgumentMatchers.eq(thing.getUID()), ArgumentMatchers.eq(connection), ArgumentMatchers.eq("topic"), ArgumentMatchers.eq(bytes));
    }

    @Test
    public void handlerInitializeAfterSubscribe() {
        subject.createdHandler(handler);
        subject.subscribe(listener, "topic");
        Assert.assertThat(subject.subscriber.get(listener).topic, CoreMatchers.is("topic"));
        // No observed broker handler, because no connection created yet within the handler
        Assert.assertThat(subject.subscriber.get(listener).observedBrokerHandlers.size(), CoreMatchers.is(0));
        // Init handler -> create connection
        handler.initialize();
        BrokerHandlerEx.verifyCreateBrokerConnection(handler, 1);
        // Simulate receiving
        final byte[] bytes = "TEST".getBytes();
        subject.subscriber.get(listener).observedBrokerHandlers.get(thing.getUID()).processMessage("topic", bytes);
        Mockito.verify(listener).receivedMessage(ArgumentMatchers.eq(thing.getUID()), ArgumentMatchers.eq(connection), ArgumentMatchers.eq("topic"), ArgumentMatchers.eq(bytes));
    }

    @Test
    public void topicVanished() {
        handler.initialize();
        BrokerHandlerEx.verifyCreateBrokerConnection(handler, 1);
        subject.createdHandler(handler);
        subject.subscribe(listener, "topic");
        Assert.assertThat(subject.subscriber.get(listener).topic, CoreMatchers.is("topic"));
        // Simulate receiving
        final byte[] bytes = "".getBytes();
        subject.subscriber.get(listener).observedBrokerHandlers.get(thing.getUID()).processMessage("topic", bytes);
        Mockito.verify(listener).topicVanished(ArgumentMatchers.eq(thing.getUID()), ArgumentMatchers.eq(connection), ArgumentMatchers.eq("topic"));
    }
}

