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
package io.moquette.integration;


import io.moquette.broker.Server;
import io.moquette.broker.config.IConfig;
import java.nio.charset.StandardCharsets;
import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ServerIntegrationQoSValidationTest {
    private static final Logger LOG = LoggerFactory.getLogger(ServerIntegrationPahoTest.class);

    Server m_server;

    IMqttClient m_subscriber;

    IMqttClient m_publisher;

    MessageCollector m_callback;

    IConfig m_config;

    @Test
    public void checkSubscriberQoS0ReceiveQoS0publishes() throws Exception {
        ServerIntegrationQoSValidationTest.LOG.info("*** checkSubscriberQoS0ReceiveQoS0publishes ***");
        m_subscriber.subscribe("/topic", 0);
        m_publisher.publish("/topic", "Hello world MQTT QoS0".getBytes(StandardCharsets.UTF_8), 0, false);
        MqttMessage message = m_callback.waitMessage(1);
        Assert.assertEquals("Hello world MQTT QoS0", message.toString());
        Assert.assertEquals(0, message.getQos());
    }

    @Test
    public void checkSubscriberQoS0ReceiveQoS1publishes_downgrade() throws Exception {
        ServerIntegrationQoSValidationTest.LOG.info("*** checkSubscriberQoS0ReceiveQoS1publishes_downgrade ***");
        m_subscriber.subscribe("/topic", 0);
        m_publisher.publish("/topic", "Hello world MQTT QoS1".getBytes(StandardCharsets.UTF_8), 1, false);
        MqttMessage message = m_callback.waitMessage(1);
        Assert.assertEquals("Hello world MQTT QoS1", message.toString());
        Assert.assertEquals(0, message.getQos());
    }

    @Test
    public void checkSubscriberQoS0ReceiveQoS2publishes_downgrade() throws Exception {
        ServerIntegrationQoSValidationTest.LOG.info("*** checkSubscriberQoS0ReceiveQoS2publishes_downgrade ***");
        m_subscriber.subscribe("/topic", 0);
        m_publisher.publish("/topic", "Hello world MQTT QoS2".getBytes(StandardCharsets.UTF_8), 2, false);
        MqttMessage message = m_callback.waitMessage(1);
        Assert.assertEquals("Hello world MQTT QoS2", message.toString());
        Assert.assertEquals(0, message.getQos());
    }

    @Test
    public void checkSubscriberQoS1ReceiveQoS0publishes() throws Exception {
        ServerIntegrationQoSValidationTest.LOG.info("*** checkSubscriberQoS1ReceiveQoS0publishes ***");
        m_subscriber.subscribe("/topic", 1);
        m_publisher.publish("/topic", "Hello world MQTT QoS0".getBytes(StandardCharsets.UTF_8), 0, false);
        MqttMessage message = m_callback.waitMessage(1);
        Assert.assertEquals("Hello world MQTT QoS0", message.toString());
        Assert.assertEquals(0, message.getQos());
    }

    @Test
    public void checkSubscriberQoS1ReceiveQoS1publishes() throws Exception {
        ServerIntegrationQoSValidationTest.LOG.info("*** checkSubscriberQoS1ReceiveQoS1publishes ***");
        m_subscriber.subscribe("/topic", 1);
        m_publisher.publish("/topic", "Hello world MQTT QoS1".getBytes(StandardCharsets.UTF_8), 1, false);
        MqttMessage message = m_callback.waitMessage(1);
        Assert.assertEquals("Hello world MQTT QoS1", message.toString());
        Assert.assertEquals(1, message.getQos());
    }

    @Test
    public void checkSubscriberQoS1ReceiveQoS2publishes_downgrade() throws Exception {
        ServerIntegrationQoSValidationTest.LOG.info("*** checkSubscriberQoS1ReceiveQoS2publishes_downgrade ***");
        m_subscriber.subscribe("/topic", 1);
        m_publisher.publish("/topic", "Hello world MQTT QoS2".getBytes(StandardCharsets.UTF_8), 2, false);
        MqttMessage message = m_callback.waitMessage(1);
        Assert.assertEquals("Hello world MQTT QoS2", message.toString());
        Assert.assertEquals(1, message.getQos());
    }

    @Test
    public void checkSubscriberQoS2ReceiveQoS0publishes() throws Exception {
        ServerIntegrationQoSValidationTest.LOG.info("*** checkSubscriberQoS2ReceiveQoS0publishes ***");
        m_subscriber.subscribe("/topic", 2);
        m_publisher.publish("/topic", "Hello world MQTT QoS2".getBytes(StandardCharsets.UTF_8), 0, false);
        MqttMessage message = m_callback.waitMessage(1);
        Assert.assertEquals("Hello world MQTT QoS2", message.toString());
        Assert.assertEquals(0, message.getQos());
    }

    @Test
    public void checkSubscriberQoS2ReceiveQoS1publishes() throws Exception {
        ServerIntegrationQoSValidationTest.LOG.info("*** checkSubscriberQoS2ReceiveQoS1publishes ***");
        m_subscriber.subscribe("/topic", 2);
        m_publisher.publish("/topic", "Hello world MQTT QoS2".getBytes(StandardCharsets.UTF_8), 1, false);
        MqttMessage message = m_callback.waitMessage(1);
        Assert.assertEquals("Hello world MQTT QoS2", message.toString());
        Assert.assertEquals(1, message.getQos());
    }

    @Test
    public void checkSubscriberQoS2ReceiveQoS2publishes() throws Exception {
        ServerIntegrationQoSValidationTest.LOG.info("*** checkSubscriberQoS2ReceiveQoS2publishes ***");
        m_subscriber.subscribe("/topic", 2);
        m_publisher.publish("/topic", "Hello world MQTT QoS2".getBytes(StandardCharsets.UTF_8), 2, false);
        MqttMessage message = m_callback.waitMessage(1);
        Assert.assertEquals("Hello world MQTT QoS2", message.toString());
        Assert.assertEquals(2, message.getQos());
    }
}

