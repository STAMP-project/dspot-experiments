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


import MqttQoS.AT_MOST_ONCE;
import io.moquette.broker.Server;
import io.moquette.broker.config.IConfig;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.mqtt.MqttMessageBuilders;
import io.netty.handler.codec.mqtt.MqttPublishMessage;
import java.nio.charset.StandardCharsets;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ServerIntegrationPahoCanPublishOnReadBlockedTopicTest {
    private static final Logger LOG = LoggerFactory.getLogger(ServerIntegrationPahoCanPublishOnReadBlockedTopicTest.class);

    static MqttClientPersistence s_dataStore;

    static MqttClientPersistence s_pubDataStore;

    Server m_server;

    IMqttClient m_client;

    IMqttClient m_publisher;

    MessageCollector m_messagesCollector;

    IConfig m_config;

    private boolean canRead;

    // TODO move this functional test into unit/integration
    @Test
    public void shouldNotInternalPublishOnReadBlockedSubscriptionTopic() throws Exception {
        ServerIntegrationPahoCanPublishOnReadBlockedTopicTest.LOG.info("*** shouldNotInternalPublishOnReadBlockedSubscriptionTopic ***");
        MqttConnectOptions options = new MqttConnectOptions();
        options.setCleanSession(false);
        m_client.connect(options);
        m_client.subscribe("/topic", 0);
        // Exercise
        MqttPublishMessage message = MqttMessageBuilders.publish().topicName("/topic").retained(true).qos(AT_MOST_ONCE).payload(Unpooled.copiedBuffer("Hello World!!".getBytes(StandardCharsets.UTF_8))).build();
        m_server.internalPublish(message, "INTRLPUB");
        final MqttMessage mqttMessage = m_messagesCollector.waitMessage(1);
        Assert.assertNotNull(mqttMessage);
        m_client.disconnect();
        // switch the authorizator
        canRead = false;
        // Exercise 2
        m_client.connect(options);
        try {
            m_client.subscribe("/topic", 0);
            Assert.fail();
        } catch (MqttException mex) {
            // it's OK, the subscribed should fail with error code 128
        }
        m_server.internalPublish(message, "INTRLPUB");
        // verify the message is not published
        final MqttMessage mqttMessage2 = m_messagesCollector.waitMessage(1);
        Assert.assertNull("No message MUST be received", mqttMessage2);
    }
}

