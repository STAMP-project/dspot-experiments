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
import io.moquette.broker.security.DBAuthenticatorTest;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ServerIntegrationDBAuthenticatorTest {
    private static final Logger LOG = LoggerFactory.getLogger(ServerIntegrationDBAuthenticatorTest.class);

    static MqttClientPersistence s_dataStore;

    static MqttClientPersistence s_pubDataStore;

    static DBAuthenticatorTest dbAuthenticatorTest;

    Server m_server;

    IMqttClient m_client;

    IMqttClient m_publisher;

    MessageCollector m_messagesCollector;

    IConfig m_config;

    @Test
    public void connectWithValidCredentials() throws Exception {
        ServerIntegrationDBAuthenticatorTest.LOG.info("*** connectWithCredentials ***");
        m_client = new MqttClient("tcp://localhost:1883", "Publisher", ServerIntegrationDBAuthenticatorTest.s_pubDataStore);
        MqttConnectOptions options = new MqttConnectOptions();
        options.setUserName("dbuser");
        options.setPassword("password".toCharArray());
        m_client.connect(options);
        Assert.assertTrue(true);
    }

    @Test
    public void connectWithWrongCredentials() {
        ServerIntegrationDBAuthenticatorTest.LOG.info("*** connectWithWrongCredentials ***");
        try {
            m_client = new MqttClient("tcp://localhost:1883", "Publisher", ServerIntegrationDBAuthenticatorTest.s_pubDataStore);
            MqttConnectOptions options = new MqttConnectOptions();
            options.setUserName("dbuser");
            options.setPassword("wrongPassword".toCharArray());
            m_client.connect(options);
        } catch (MqttException e) {
            if (e instanceof MqttSecurityException) {
                Assert.assertTrue(true);
                return;
            } else {
                Assert.assertTrue(e.getMessage(), false);
                return;
            }
        }
        Assert.assertTrue("must not be connected. cause : wrong password given to client", false);
    }
}

