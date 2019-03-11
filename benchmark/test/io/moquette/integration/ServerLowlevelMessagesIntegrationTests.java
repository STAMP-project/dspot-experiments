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
import io.moquette.testclient.Client;
import io.netty.handler.codec.mqtt.MqttMessage;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;
import org.awaitility.Awaitility;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ServerLowlevelMessagesIntegrationTests {
    private static final Logger LOG = LoggerFactory.getLogger(ServerLowlevelMessagesIntegrationTests.class);

    static MqttClientPersistence s_dataStore;

    Server m_server;

    Client m_client;

    IMqttClient m_willSubscriber;

    MessageCollector m_messageCollector;

    IConfig m_config;

    MqttMessage receivedMsg;

    @Test
    public void elapseKeepAliveTime() {
        int keepAlive = 2;// secs

        MqttConnectMessage connectMessage = ServerLowlevelMessagesIntegrationTests.createConnectMessage("FAKECLNT", keepAlive);
        /* ConnectMessage connectMessage = new ConnectMessage();
        connectMessage.setProtocolVersion((byte) 3); connectMessage.setClientID("FAKECLNT");
        connectMessage.setKeepAlive(keepAlive);
         */
        m_client.sendMessage(connectMessage);
        // wait 3 times the keepAlive
        Awaitility.await().atMost((3 * keepAlive), TimeUnit.SECONDS).until(m_client::isConnectionLost);
    }

    @Test
    public void testWillMessageIsWiredOnClientKeepAliveExpiry() throws Exception {
        ServerLowlevelMessagesIntegrationTests.LOG.info("*** testWillMessageIsWiredOnClientKeepAliveExpiry ***");
        String willTestamentTopic = "/will/test";
        String willTestamentMsg = "Bye bye";
        m_willSubscriber.connect();
        m_willSubscriber.subscribe(willTestamentTopic, 0);
        m_client.clientId("FAKECLNT").connect(willTestamentTopic, willTestamentMsg);
        long connectTime = System.currentTimeMillis();
        Awaitility.await().atMost(7, TimeUnit.SECONDS).untilAsserted(() -> {
            // but after the 2 KEEP ALIVE timeout expires it gets fired,
            // NB it's 1,5 * KEEP_ALIVE so 3 secs and some millis to propagate the message
            org.eclipse.paho.client.mqttv3.MqttMessage msg = m_messageCollector.getMessageImmediate();
            assertNotNull("the will message should be fired after keep alive!", msg);
            // the will message hasn't to be received before the elapsing of Keep Alive timeout
            assertTrue((((System.currentTimeMillis()) - connectTime) > 3000));
            assertEquals(willTestamentMsg, new String(msg.getPayload(), StandardCharsets.UTF_8));
        });
        m_willSubscriber.disconnect();
    }

    @Test
    public void testRejectConnectWithEmptyClientID() throws InterruptedException {
        ServerLowlevelMessagesIntegrationTests.LOG.info("*** testRejectConnectWithEmptyClientID ***");
        m_client.clientId("").connect();
        this.receivedMsg = this.m_client.lastReceivedMessage();
        Assert.assertTrue(((receivedMsg) instanceof MqttConnAckMessage));
        MqttConnAckMessage connAck = ((MqttConnAckMessage) (receivedMsg));
        Assert.assertEquals(CONNECTION_REFUSED_IDENTIFIER_REJECTED, connAck.variableHeader().connectReturnCode());
    }

    @Test
    public void testWillMessageIsPublishedOnClientBadDisconnection() throws MqttException, InterruptedException {
        ServerLowlevelMessagesIntegrationTests.LOG.info("*** testWillMessageIsPublishedOnClientBadDisconnection ***");
        String willTestamentTopic = "/will/test";
        String willTestamentMsg = "Bye bye";
        m_willSubscriber.connect();
        m_willSubscriber.subscribe(willTestamentTopic, 0);
        m_client.clientId("FAKECLNT").connect(willTestamentTopic, willTestamentMsg);
        // kill will publisher
        m_client.close();
        // Verify will testament is published
        org.eclipse.paho.client.mqttv3.MqttMessage receivedTestament = m_messageCollector.waitMessage(1);
        Assert.assertEquals(willTestamentMsg, new String(receivedTestament.getPayload(), StandardCharsets.UTF_8));
        m_willSubscriber.disconnect();
    }
}

