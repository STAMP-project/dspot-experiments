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


import MockNioServerFactory.IS_FAULTY_KEY;
import com.github.ambry.clustermap.ClusterMap;
import com.github.ambry.clustermap.MockClusterMap;
import com.github.ambry.commons.LoggingNotificationSystem;
import com.github.ambry.commons.SSLFactory;
import com.github.ambry.config.VerifiableProperties;
import com.github.ambry.notification.NotificationSystem;
import java.util.Properties;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test functionality of {@link RestServer}.
 */
public class RestServerTest {
    private static final SSLFactory SSL_FACTORY = RestTestUtils.getTestSSLFactory();

    /**
     * Tests {@link RestServer#start()} and {@link RestServer#shutdown()}.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void startShutdownTest() throws Exception {
        Properties properties = new Properties();
        VerifiableProperties verifiableProperties = getVProps(properties);
        ClusterMap clusterMap = new MockClusterMap();
        NotificationSystem notificationSystem = new LoggingNotificationSystem();
        RestServer server = new RestServer(verifiableProperties, clusterMap, notificationSystem, RestServerTest.SSL_FACTORY);
        server.start();
        server.shutdown();
        server.awaitShutdown();
    }

    /**
     * Tests for {@link RestServer#shutdown()} when {@link RestServer#start()} had not been called previously. This test
     * is for cases where {@link RestServer#start()} has failed and {@link RestServer#shutdown()} needs to be run.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void shutdownWithoutStartTest() throws Exception {
        Properties properties = new Properties();
        VerifiableProperties verifiableProperties = getVProps(properties);
        ClusterMap clusterMap = new MockClusterMap();
        NotificationSystem notificationSystem = new LoggingNotificationSystem();
        RestServer server = new RestServer(verifiableProperties, clusterMap, notificationSystem, RestServerTest.SSL_FACTORY);
        server.shutdown();
        server.awaitShutdown();
    }

    /**
     * Tests for correct exceptions thrown on {@link RestServer} instantiation/{@link RestServer#start()} with bad input.
     *
     * @throws Exception
     * 		
     * @throws IOException
     * 		
     */
    @Test
    public void serverCreationWithBadInputTest() throws Exception {
        badArgumentsTest();
        badFactoriesTest();
    }

    /**
     * Tests for correct exceptions thrown on {@link RestServer#start()}/{@link RestServer#shutdown()} with bad
     * components.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void startShutdownTestWithBadComponent() throws Exception {
        Properties properties = new Properties();
        properties.setProperty("rest.server.nio.server.factory", MockNioServerFactory.class.getCanonicalName());
        // makes MockNioServer throw exceptions.
        properties.setProperty(IS_FAULTY_KEY, "true");
        VerifiableProperties verifiableProperties = getVProps(properties);
        ClusterMap clusterMap = new MockClusterMap();
        NotificationSystem notificationSystem = new LoggingNotificationSystem();
        RestServer server = new RestServer(verifiableProperties, clusterMap, notificationSystem, RestServerTest.SSL_FACTORY);
        try {
            server.start();
            Assert.fail("start() should not be successful. MockNioServer::start() would have thrown InstantiationException");
        } catch (InstantiationException e) {
            // nothing to do. expected.
        } finally {
            try {
                server.shutdown();
                Assert.fail("RestServer shutdown should have failed.");
            } catch (RuntimeException e) {
                // nothing to do. expected.
            }
        }
    }
}

