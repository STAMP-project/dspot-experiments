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
import java.nio.charset.StandardCharsets;
import javax.net.ssl.SSLSocketFactory;
import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttClientPersistence;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Check that Moquette could also handle SSL.
 */
public class ServerIntegrationSSLTest {
    private static final Logger LOG = LoggerFactory.getLogger(ServerIntegrationSSLTest.class);

    Server m_server;

    static MqttClientPersistence s_dataStore;

    IMqttClient m_client;

    MessageCollector m_callback;

    static String backup;

    @Test
    public void checkSupportSSL() throws Exception {
        ServerIntegrationSSLTest.LOG.info("*** checkSupportSSL ***");
        SSLSocketFactory ssf = configureSSLSocketFactory();
        MqttConnectOptions options = new MqttConnectOptions();
        options.setSocketFactory(ssf);
        m_client.connect(options);
        m_client.subscribe("/topic", 0);
        m_client.disconnect();
    }

    @Test
    public void checkSupportSSLForMultipleClient() throws Exception {
        ServerIntegrationSSLTest.LOG.info("*** checkSupportSSLForMultipleClient ***");
        SSLSocketFactory ssf = configureSSLSocketFactory();
        MqttConnectOptions options = new MqttConnectOptions();
        options.setSocketFactory(ssf);
        m_client.connect(options);
        m_client.subscribe("/topic", 0);
        MqttClient secondClient = new MqttClient("ssl://localhost:8883", "secondTestClient", new MemoryPersistence());
        MqttConnectOptions secondClientOptions = new MqttConnectOptions();
        secondClientOptions.setSocketFactory(ssf);
        secondClient.connect(secondClientOptions);
        secondClient.publish("/topic", new MqttMessage("message".getBytes(StandardCharsets.UTF_8)));
        secondClient.disconnect();
        m_client.disconnect();
    }
}

