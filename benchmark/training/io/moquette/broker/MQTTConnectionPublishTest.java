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
import MqttQoS.AT_MOST_ONCE;
import io.netty.buffer.Unpooled;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.mqtt.MqttMessageBuilders;
import io.netty.handler.codec.mqtt.MqttPublishMessage;
import java.nio.charset.StandardCharsets;
import org.junit.Assert;
import org.junit.Test;


public class MQTTConnectionPublishTest {
    private static final String FAKE_CLIENT_ID = "FAKE_123";

    private static final String TEST_USER = "fakeuser";

    private static final String TEST_PWD = "fakepwd";

    private MQTTConnection sut;

    private EmbeddedChannel channel;

    private SessionRegistry sessionRegistry;

    private ConnectBuilder connMsg;

    private MemoryQueueRepository queueRepository;

    @Test
    public void dropConnectionOnPublishWithInvalidTopicFormat() {
        // Connect message with clean session set to true and client id is null.
        MqttPublishMessage publish = MqttMessageBuilders.publish().topicName("").retained(false).qos(AT_MOST_ONCE).payload(Unpooled.copiedBuffer("Hello MQTT world!".getBytes(StandardCharsets.UTF_8))).build();
        sut.processPublish(publish);
        // Verify
        Assert.assertFalse("Connection should be closed by the broker", channel.isOpen());
    }
}

