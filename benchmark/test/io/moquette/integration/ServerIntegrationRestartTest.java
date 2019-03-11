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
import org.junit.Assert;
import org.junit.Test;


public class ServerIntegrationRestartTest {
    static MqttClientPersistence s_dataStore;

    static MqttClientPersistence s_pubDataStore;

    static MqttConnectOptions CLEAN_SESSION_OPT = new MqttConnectOptions();

    Server m_server;

    IMqttClient m_subscriber;

    IMqttClient m_publisher;

    IConfig m_config;

    MessageCollector m_messageCollector;

    @Test
    public void checkRestartCleanSubscriptionTree() throws Exception {
        // subscribe to /topic
        m_subscriber.connect(ServerIntegrationRestartTest.CLEAN_SESSION_OPT);
        m_subscriber.subscribe("/topic", 1);
        m_subscriber.disconnect();
        // shutdown the integration
        m_server.stopServer();
        // restart the integration
        m_server.startServer(IntegrationUtils.prepareTestProperties());
        // reconnect the Subscriber subscribing to the same /topic but different QoS
        m_subscriber.connect(ServerIntegrationRestartTest.CLEAN_SESSION_OPT);
        m_subscriber.subscribe("/topic", 2);
        // should be just one registration so a publisher receive one notification
        m_publisher.connect(ServerIntegrationRestartTest.CLEAN_SESSION_OPT);
        m_publisher.publish("/topic", "Hello world MQTT!!".getBytes(StandardCharsets.UTF_8), 1, false);
        // read the messages
        MqttMessage msg = m_messageCollector.waitMessage(1);
        Assert.assertEquals("Hello world MQTT!!", new String(msg.getPayload(), StandardCharsets.UTF_8));
        // no more messages on the same topic will be received
        Assert.assertNull(m_messageCollector.waitMessage(1));
    }

    @Test
    public void checkDontPublishInactiveClientsAfterServerRestart() throws Exception {
        IMqttClient conn = subscribeAndPublish("/topic");
        conn.disconnect();
        // shutdown the integration
        m_server.stopServer();
        // restart the integration
        m_server.startServer(IntegrationUtils.prepareTestProperties());
        m_publisher.connect();
        m_publisher.publish("/topic", "Hello world MQTT!!".getBytes(StandardCharsets.UTF_8), 0, false);
    }

    @Test
    public void testClientDoesntRemainSubscribedAfterASubscriptionAndServerRestart() throws Exception {
        // subscribe to /topic
        m_subscriber.connect();
        // subscribe /topic
        m_subscriber.subscribe("/topic", 0);
        // unsubscribe from /topic
        m_subscriber.unsubscribe("/topic");
        m_subscriber.disconnect();
        // shutdown the integration
        m_server.stopServer();
        // restart the integration
        m_server.startServer(IntegrationUtils.prepareTestProperties());
        // subscriber reconnects
        m_subscriber = new MqttClient("tcp://localhost:1883", "Subscriber", ServerIntegrationRestartTest.s_dataStore);
        m_subscriber.setCallback(m_messageCollector);
        m_subscriber.connect();
        // publisher publishes on /topic
        m_publisher = new MqttClient("tcp://localhost:1883", "Publisher", ServerIntegrationRestartTest.s_pubDataStore);
        m_publisher.connect();
        m_publisher.publish("/topic", "Hello world MQTT!!".getBytes(StandardCharsets.UTF_8), 1, false);
        // Expected
        // the subscriber doesn't get notified (it's fully unsubscribed)
        Assert.assertNull(m_messageCollector.waitMessage(1));
    }
}

