/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hadoop.http;


import org.apache.hadoop.test.GenericTestUtils;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;


public class TestHttpServerLifecycle extends HttpServerFunctionalTest {
    /**
     * Test that the server is alive once started
     *
     * @throws Throwable
     * 		on failure
     */
    @Test
    public void testCreatedServerIsNotAlive() throws Throwable {
        HttpServer2 server = HttpServerFunctionalTest.createTestServer();
        assertNotLive(server);
    }

    @Test
    public void testStopUnstartedServer() throws Throwable {
        HttpServer2 server = HttpServerFunctionalTest.createTestServer();
        HttpServerFunctionalTest.stop(server);
    }

    /**
     * Test that the server is alive once started
     *
     * @throws Throwable
     * 		on failure
     */
    @Test
    public void testStartedServerIsAlive() throws Throwable {
        HttpServer2 server = null;
        server = HttpServerFunctionalTest.createTestServer();
        assertNotLive(server);
        server.start();
        assertAlive(server);
        HttpServerFunctionalTest.stop(server);
    }

    /**
     * Test that the server with request logging enabled
     *
     * @throws Throwable
     * 		on failure
     */
    @Test
    public void testStartedServerWithRequestLog() throws Throwable {
        HttpRequestLogAppender requestLogAppender = new HttpRequestLogAppender();
        requestLogAppender.setName("httprequestlog");
        requestLogAppender.setFilename(GenericTestUtils.getTempPath("jetty-name-yyyy_mm_dd.log"));
        Logger.getLogger(((HttpServer2.class.getName()) + ".test")).addAppender(requestLogAppender);
        HttpServer2 server = null;
        server = HttpServerFunctionalTest.createTestServer();
        assertNotLive(server);
        server.start();
        assertAlive(server);
        HttpServerFunctionalTest.stop(server);
        Logger.getLogger(((HttpServer2.class.getName()) + ".test")).removeAppender(requestLogAppender);
    }

    /**
     * Test that the server is not alive once stopped
     *
     * @throws Throwable
     * 		on failure
     */
    @Test
    public void testStoppedServerIsNotAlive() throws Throwable {
        HttpServer2 server = HttpServerFunctionalTest.createAndStartTestServer();
        assertAlive(server);
        HttpServerFunctionalTest.stop(server);
        assertNotLive(server);
    }

    /**
     * Test that the server is not alive once stopped
     *
     * @throws Throwable
     * 		on failure
     */
    @Test
    public void testStoppingTwiceServerIsAllowed() throws Throwable {
        HttpServer2 server = HttpServerFunctionalTest.createAndStartTestServer();
        assertAlive(server);
        HttpServerFunctionalTest.stop(server);
        assertNotLive(server);
        HttpServerFunctionalTest.stop(server);
        assertNotLive(server);
    }

    /**
     * Test that the server is alive once started
     *
     * @throws Throwable
     * 		on failure
     */
    @Test
    public void testWepAppContextAfterServerStop() throws Throwable {
        HttpServer2 server = null;
        String key = "test.attribute.key";
        String value = "test.attribute.value";
        server = HttpServerFunctionalTest.createTestServer();
        assertNotLive(server);
        server.start();
        server.setAttribute(key, value);
        assertAlive(server);
        Assert.assertEquals(value, server.getAttribute(key));
        HttpServerFunctionalTest.stop(server);
        Assert.assertNull("Server context should have cleared", server.getAttribute(key));
    }
}

