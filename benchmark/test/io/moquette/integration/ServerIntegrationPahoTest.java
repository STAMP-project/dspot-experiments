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
import java.io.File;
import java.nio.charset.StandardCharsets;
import org.eclipse.paho.client.mqttv3.persist.MqttDefaultFilePersistence;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ServerIntegrationPahoTest {
    private static final Logger LOG = LoggerFactory.getLogger(ServerIntegrationPahoTest.class);

    static MqttClientPersistence s_dataStore;

    static MqttClientPersistence s_pubDataStore;

    Server m_server;

    IMqttClient m_client;

    IMqttClient m_publisher;

    MessageCollector m_messagesCollector;

    IConfig m_config;

    /**
     * subscriber A connect and subscribe on "a/b" QoS 1 subscriber B connect and subscribe on "a/+"
     * BUT with QoS 2 publisher connects and send a message "hello" on "a/b" subscriber A must
     * receive a notification with QoS1 subscriber B must receive a notification with QoS2
     */
    @Test
    public void checkSubscribersGetCorrectQosNotifications() throws Exception {
        ServerIntegrationPahoTest.LOG.info("*** checkSubscribersGetCorrectQosNotifications ***");
        String tmpDir = System.getProperty("java.io.tmpdir");
        MqttClientPersistence dsSubscriberA = new MqttDefaultFilePersistence(((tmpDir + (File.separator)) + "subscriberA"));
        MqttClient subscriberA = new MqttClient("tcp://localhost:1883", "SubscriberA", dsSubscriberA);
        MessageCollector cbSubscriberA = new MessageCollector();
        subscriberA.setCallback(cbSubscriberA);
        subscriberA.connect();
        subscriberA.subscribe("a/b", 1);
        MqttClientPersistence dsSubscriberB = new MqttDefaultFilePersistence(((tmpDir + (File.separator)) + "subscriberB"));
        MqttClient subscriberB = new MqttClient("tcp://localhost:1883", "SubscriberB", dsSubscriberB);
        MessageCollector cbSubscriberB = new MessageCollector();
        subscriberB.setCallback(cbSubscriberB);
        subscriberB.connect();
        subscriberB.subscribe("a/+", 2);
        m_client.connect();
        m_client.publish("a/b", "Hello world MQTT!!".getBytes(StandardCharsets.UTF_8), 2, false);
        MqttMessage messageOnA = cbSubscriberA.waitMessage(1);
        Assert.assertEquals("Hello world MQTT!!", new String(messageOnA.getPayload(), StandardCharsets.UTF_8));
        Assert.assertEquals(1, messageOnA.getQos());
        subscriberA.disconnect();
        MqttMessage messageOnB = cbSubscriberB.waitMessage(1);
        Assert.assertNotNull("MUST be a received message", messageOnB);
        Assert.assertEquals("Hello world MQTT!!", new String(messageOnB.getPayload(), StandardCharsets.UTF_8));
        Assert.assertEquals(2, messageOnB.getQos());
        subscriberB.disconnect();
    }

    @Test
    public void testSubcriptionDoesntStayActiveAfterARestart() throws Exception {
        ServerIntegrationPahoTest.LOG.info("*** testSubcriptionDoesntStayActiveAfterARestart ***");
        String tmpDir = System.getProperty("java.io.tmpdir");
        // clientForSubscribe1 connect and subscribe to /topic QoS2
        MqttClientPersistence dsSubscriberA = new MqttDefaultFilePersistence(((tmpDir + (File.separator)) + "clientForSubscribe1"));
        MqttClient clientForSubscribe1 = new MqttClient("tcp://localhost:1883", "clientForSubscribe1", dsSubscriberA);
        MessageCollector cbSubscriber1 = new MessageCollector();
        clientForSubscribe1.setCallback(cbSubscriber1);
        clientForSubscribe1.connect();
        clientForSubscribe1.subscribe("topic", 0);
        // integration stop
        m_server.stopServer();
        System.out.println("\n\n SEVER REBOOTING \n\n");
        // integration start
        startServer();
        // clientForSubscribe2 connect and subscribe to /topic QoS2
        MqttClientPersistence dsSubscriberB = new MqttDefaultFilePersistence(((tmpDir + (File.separator)) + "clientForSubscribe2"));
        MqttClient clientForSubscribe2 = new MqttClient("tcp://localhost:1883", "clientForSubscribe2", dsSubscriberB);
        MessageCollector cbSubscriber2 = new MessageCollector();
        clientForSubscribe2.setCallback(cbSubscriber2);
        clientForSubscribe2.connect();
        clientForSubscribe2.subscribe("topic", 0);
        // clientForPublish publish on /topic with QoS2 a message
        MqttClientPersistence dsSubscriberPUB = new MqttDefaultFilePersistence(((tmpDir + (File.separator)) + "clientForPublish"));
        MqttClient clientForPublish = new MqttClient("tcp://localhost:1883", "clientForPublish", dsSubscriberPUB);
        clientForPublish.connect();
        clientForPublish.publish("topic", "Hello".getBytes(StandardCharsets.UTF_8), 2, true);
        // verify clientForSubscribe1 doesn't receive a notification but clientForSubscribe2 yes
        ServerIntegrationPahoTest.LOG.info("Before waiting to receive 1 sec from {}", clientForSubscribe1.getClientId());
        Assert.assertFalse(clientForSubscribe1.isConnected());
        Assert.assertTrue(clientForSubscribe2.isConnected());
        ServerIntegrationPahoTest.LOG.info("Waiting to receive 1 sec from {}", clientForSubscribe2.getClientId());
        MqttMessage messageOnB = cbSubscriber2.waitMessage(1);
        Assert.assertEquals("Hello", new String(messageOnB.getPayload(), StandardCharsets.UTF_8));
    }
}

