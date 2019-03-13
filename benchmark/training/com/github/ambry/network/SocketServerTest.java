/**
 * Copyright 2016 LinkedIn Corp. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 */
package com.github.ambry.network;


import com.codahale.metrics.MetricRegistry;
import com.github.ambry.commons.SSLFactory;
import com.github.ambry.config.NetworkConfig;
import com.github.ambry.config.SSLConfig;
import com.github.ambry.config.VerifiableProperties;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;
import javax.net.ssl.SSLSocketFactory;
import org.junit.Test;

import static PortType.PLAINTEXT;
import static PortType.SSL;


public class SocketServerTest {
    private static SSLFactory clientSSLFactory;

    private static SSLSocketFactory clientSSLSocketFactory;

    private static SSLConfig clientSSLConfig;

    private static SSLConfig serverSSLConfig;

    private SocketServer server = null;

    public SocketServerTest() throws Exception {
        Properties props = new Properties();
        VerifiableProperties propverify = new VerifiableProperties(props);
        NetworkConfig config = new NetworkConfig(propverify);
        ArrayList<Port> ports = new ArrayList<Port>();
        ports.add(new Port(config.port, PLAINTEXT));
        ports.add(new Port(((config.port) + 1000), SSL));
        server = new SocketServer(config, SocketServerTest.serverSSLConfig, new MetricRegistry(), ports);
        server.start();
    }

    @Test
    public void simpleRequest() throws IOException, InterruptedException {
        simpleRequest(new Port(server.getPort(), PLAINTEXT));
    }

    @Test
    public void simpleSSLRequest() throws IOException, InterruptedException {
        simpleRequest(new Port(server.getSSLPort(), SSL));
    }
}

