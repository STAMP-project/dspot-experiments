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
package com.github.ambry.rest;


import NettyConfig.SSL_FACTORY_KEY;
import SSLFactory.Mode.SERVER;
import com.codahale.metrics.MetricRegistry;
import com.github.ambry.commons.SSLFactory;
import com.github.ambry.commons.TestSSLUtils;
import com.github.ambry.config.VerifiableProperties;
import java.io.File;
import java.util.Properties;
import org.junit.Test;


/**
 * Tests functionality of {@link NettyServerFactory}.
 */
public class NettyServerFactoryTest {
    // dud properties. server should pick up defaults
    private static final RestRequestHandler REST_REQUEST_HANDLER = new MockRestRequestResponseHandler();

    private static final PublicAccessLogger PUBLIC_ACCESS_LOGGER = new PublicAccessLogger(new String[]{  }, new String[]{  });

    private static final RestServerState REST_SERVER_STATE = new RestServerState("/healthCheck");

    private static final SSLFactory SSL_FACTORY = RestTestUtils.getTestSSLFactory();

    /**
     * Checks to see that getting the default {@link NioServer} (currently {@link NettyServer}) works.
     */
    @Test
    public void getNettyServerTest() throws Exception {
        Properties properties = new Properties();
        doGetNettyServerTest(properties, NettyServerFactoryTest.SSL_FACTORY);
        doGetNettyServerTest(properties, null);
        // test with ssl
        properties.setProperty("netty.server.enable.ssl", "true");
        doGetNettyServerTest(properties, NettyServerFactoryTest.SSL_FACTORY);
        // test overriding ssl factory
        File trustStoreFile = File.createTempFile("truststore", ".jks");
        trustStoreFile.deleteOnExit();
        TestSSLUtils.addSSLProperties(properties, "", SERVER, trustStoreFile, "frontend");
        properties.setProperty(SSL_FACTORY_KEY, NettySslFactory.class.getName());
        doGetNettyServerTest(properties, NettyServerFactoryTest.SSL_FACTORY);
    }

    /**
     * Tests instantiation of {@link NettyServerFactory} with bad input.
     */
    @Test
    public void getNettyServerFactoryWithBadInputTest() throws Exception {
        Properties properties = new Properties();
        properties.setProperty("netty.server.enable.ssl", "true");
        VerifiableProperties verifiableProperties = new VerifiableProperties(properties);
        MetricRegistry metricRegistry = new MetricRegistry();
        doConstructionFailureTest(null, metricRegistry, NettyServerFactoryTest.REST_REQUEST_HANDLER, NettyServerFactoryTest.PUBLIC_ACCESS_LOGGER, NettyServerFactoryTest.REST_SERVER_STATE, NettyServerFactoryTest.SSL_FACTORY);
        doConstructionFailureTest(verifiableProperties, null, NettyServerFactoryTest.REST_REQUEST_HANDLER, NettyServerFactoryTest.PUBLIC_ACCESS_LOGGER, NettyServerFactoryTest.REST_SERVER_STATE, NettyServerFactoryTest.SSL_FACTORY);
        doConstructionFailureTest(verifiableProperties, metricRegistry, null, NettyServerFactoryTest.PUBLIC_ACCESS_LOGGER, NettyServerFactoryTest.REST_SERVER_STATE, NettyServerFactoryTest.SSL_FACTORY);
        doConstructionFailureTest(verifiableProperties, metricRegistry, NettyServerFactoryTest.REST_REQUEST_HANDLER, null, NettyServerFactoryTest.REST_SERVER_STATE, NettyServerFactoryTest.SSL_FACTORY);
        doConstructionFailureTest(verifiableProperties, metricRegistry, NettyServerFactoryTest.REST_REQUEST_HANDLER, NettyServerFactoryTest.PUBLIC_ACCESS_LOGGER, null, NettyServerFactoryTest.SSL_FACTORY);
        doConstructionFailureTest(verifiableProperties, metricRegistry, NettyServerFactoryTest.REST_REQUEST_HANDLER, NettyServerFactoryTest.PUBLIC_ACCESS_LOGGER, NettyServerFactoryTest.REST_SERVER_STATE, null);
    }
}

