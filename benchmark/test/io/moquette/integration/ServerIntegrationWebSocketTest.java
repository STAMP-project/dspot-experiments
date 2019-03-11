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


import io.moquette.BrokerConstants;
import io.moquette.broker.Server;
import io.moquette.broker.config.IConfig;
import java.net.URI;
import java.util.concurrent.TimeUnit;
import org.eclipse.jetty.websocket.client.ClientUpgradeRequest;
import org.eclipse.jetty.websocket.client.WebSocketClient;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Integration test to check the function of Moquette with a WebSocket channel.
 */
public class ServerIntegrationWebSocketTest {
    private static final Logger LOG = LoggerFactory.getLogger(ServerIntegrationWebSocketTest.class);

    Server m_server;

    WebSocketClient client;

    IConfig m_config;

    @SuppressWarnings("FutureReturnValueIgnored")
    @Test
    public void checkPlainConnect() throws Exception {
        ServerIntegrationWebSocketTest.LOG.info("*** checkPlainConnect ***");
        String destUri = ("ws://localhost:" + (BrokerConstants.WEBSOCKET_PORT)) + "/mqtt";
        MQTTWebSocket socket = new MQTTWebSocket();
        client.start();
        URI echoUri = new URI(destUri);
        ClientUpgradeRequest request = new ClientUpgradeRequest();
        client.connect(socket, echoUri, request);
        ServerIntegrationWebSocketTest.LOG.info("Connecting to : {}", echoUri);
        boolean connected = socket.awaitConnected(4, TimeUnit.SECONDS);
        ServerIntegrationWebSocketTest.LOG.info("Connected was : {}", connected);
        Assert.assertTrue(connected);
    }
}

