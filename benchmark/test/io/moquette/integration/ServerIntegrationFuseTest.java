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
import java.util.concurrent.TimeUnit;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static QoS.AT_MOST_ONCE;


public class ServerIntegrationFuseTest {
    private static final Logger LOG = LoggerFactory.getLogger(ServerIntegrationPahoTest.class);

    Server m_server;

    MQTT m_mqtt;

    BlockingConnection m_subscriber;

    BlockingConnection m_publisher;

    IConfig m_config;

    @Test
    public void checkWillTestamentIsPublishedOnConnectionKill_noRetain() throws Exception {
        ServerIntegrationFuseTest.LOG.info("checkWillTestamentIsPublishedOnConnectionKill_noRetain");
        String willTestamentTopic = "/will/test";
        String willTestamentMsg = "Bye bye";
        MQTT mqtt = new MQTT();
        mqtt.setHost("localhost", 1883);
        mqtt.setClientId("WillTestamentPublisher");
        mqtt.setWillRetain(false);
        mqtt.setWillMessage(willTestamentMsg);
        mqtt.setWillTopic(willTestamentTopic);
        m_publisher = mqtt.blockingConnection();
        m_publisher.connect();
        m_mqtt.setHost("localhost", 1883);
        m_mqtt.setCleanSession(false);
        m_mqtt.setClientId("Subscriber");
        m_subscriber = m_mqtt.blockingConnection();
        m_subscriber.connect();
        Topic[] topics = new Topic[]{ new Topic(willTestamentTopic, AT_MOST_ONCE) };
        m_subscriber.subscribe(topics);
        // Exercise, kill the publisher connection
        m_publisher.kill();
        // Verify, that the testament is fired
        Message msg = m_subscriber.receive(1, TimeUnit.SECONDS);// wait the flush interval (1 sec)

        Assert.assertNotNull("We should get notified with 'Will' message", msg);
        msg.ack();
        Assert.assertEquals(willTestamentMsg, new String(msg.getPayload(), StandardCharsets.UTF_8));
    }
}

