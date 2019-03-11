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


import com.codahale.metrics.MetricRegistry;
import com.github.ambry.commons.SSLFactory;
import java.io.IOException;
import java.util.Properties;
import org.junit.Test;


/**
 * Tests basic functionality of {@link NettyServer}.
 */
public class NettyServerTest {
    private static final NettyMetrics NETTY_METRICS = new NettyMetrics(new MetricRegistry());

    private static final RestRequestHandler REQUEST_HANDLER = new MockRestRequestResponseHandler();

    private static final PublicAccessLogger PUBLIC_ACCESS_LOGGER = new PublicAccessLogger(new String[]{  }, new String[]{  });

    private static final RestServerState REST_SERVER_STATE = new RestServerState("/healthCheck");

    private static final ConnectionStatsHandler CONNECTION_STATS_HANDLER = new ConnectionStatsHandler(NettyServerTest.NETTY_METRICS);

    private static final SSLFactory SSL_FACTORY = RestTestUtils.getTestSSLFactory();

    /**
     * Tests {@link NettyServer#start()} and {@link NettyServer#shutdown()} given good input.
     *
     * @throws InstantiationException
     * 		
     * @throws IOException
     * 		
     */
    @Test
    public void startShutdownTest() throws IOException, InstantiationException {
        NioServer nioServer = getNettyServer(null);
        nioServer.start();
        nioServer.shutdown();
    }

    /**
     * Tests for {@link NettyServer#shutdown()} when {@link NettyServer#start()} has not been called previously.
     * This test is for cases where {@link NettyServer#start()} has failed and {@link NettyServer#shutdown()} needs to be
     * run.
     *
     * @throws InstantiationException
     * 		
     * @throws IOException
     * 		
     */
    @Test
    public void shutdownWithoutStartTest() throws IOException, InstantiationException {
        NioServer nioServer = getNettyServer(null);
        nioServer.shutdown();
    }

    /**
     * Tests for correct exceptions are thrown on {@link NettyServer} instantiation/{@link NettyServer#start()} with bad
     * input.
     *
     * @throws InstantiationException
     * 		
     * @throws IOException
     * 		
     */
    @Test
    public void startWithBadInputTest() throws IOException, InstantiationException {
        Properties properties = new Properties();
        // Should be > 0. So will throw at start().
        properties.setProperty("netty.server.port", "-1");
        doStartFailureTest(properties);
        properties = new Properties();
        // Should be > 0. So will throw at start().
        properties.setProperty("netty.server.ssl.port", "-1");
        doStartFailureTest(properties);
    }
}

