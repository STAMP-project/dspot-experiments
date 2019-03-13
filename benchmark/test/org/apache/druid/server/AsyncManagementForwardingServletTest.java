/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.druid.server;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;
import com.google.inject.Injector;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.function.BiConsumer;
import javax.annotation.Nullable;
import org.apache.druid.discovery.DruidLeaderSelector;
import org.apache.druid.guice.http.DruidHttpClientConfig;
import org.apache.druid.java.util.common.StringUtils;
import org.apache.druid.server.initialization.BaseJettyTest;
import org.apache.druid.server.initialization.jetty.JettyServerInitUtils;
import org.apache.druid.server.initialization.jetty.JettyServerInitializer;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.junit.Assert;
import org.junit.Test;


public class AsyncManagementForwardingServletTest extends BaseJettyTest {
    private static final AsyncManagementForwardingServletTest.ExpectedRequest coordinatorExpectedRequest = new AsyncManagementForwardingServletTest.ExpectedRequest();

    private static final AsyncManagementForwardingServletTest.ExpectedRequest overlordExpectedRequest = new AsyncManagementForwardingServletTest.ExpectedRequest();

    private static int coordinatorPort;

    private static int overlordPort;

    private Server coordinator;

    private Server overlord;

    private static class ExpectedRequest {
        private boolean called = false;

        private String path;

        private String query;

        private String method;

        private Map<String, String> headers;

        private String body;

        private void reset() {
            called = false;
            path = null;
            query = null;
            method = null;
            headers = null;
            body = null;
        }
    }

    @Test
    public void testCoordinatorDatasources() throws Exception {
        AsyncManagementForwardingServletTest.coordinatorExpectedRequest.path = "/druid/coordinator/v1/datasources";
        AsyncManagementForwardingServletTest.coordinatorExpectedRequest.method = "GET";
        AsyncManagementForwardingServletTest.coordinatorExpectedRequest.headers = ImmutableMap.of("Authorization", "Basic bXl1c2VyOm15cGFzc3dvcmQ=");
        HttpURLConnection connection = ((HttpURLConnection) (new URL(StringUtils.format("http://localhost:%d%s", port, AsyncManagementForwardingServletTest.coordinatorExpectedRequest.path)).openConnection()));
        connection.setRequestMethod(AsyncManagementForwardingServletTest.coordinatorExpectedRequest.method);
        AsyncManagementForwardingServletTest.coordinatorExpectedRequest.headers.forEach(connection::setRequestProperty);
        Assert.assertEquals(200, connection.getResponseCode());
        Assert.assertTrue("coordinator called", AsyncManagementForwardingServletTest.coordinatorExpectedRequest.called);
        Assert.assertFalse("overlord called", AsyncManagementForwardingServletTest.overlordExpectedRequest.called);
    }

    @Test
    public void testCoordinatorLoadStatus() throws Exception {
        AsyncManagementForwardingServletTest.coordinatorExpectedRequest.path = "/druid/coordinator/v1/loadstatus";
        AsyncManagementForwardingServletTest.coordinatorExpectedRequest.query = "full";
        AsyncManagementForwardingServletTest.coordinatorExpectedRequest.method = "GET";
        AsyncManagementForwardingServletTest.coordinatorExpectedRequest.headers = ImmutableMap.of("Authorization", "Basic bXl1c2VyOm15cGFzc3dvcmQ=");
        HttpURLConnection connection = ((HttpURLConnection) (new URL(StringUtils.format("http://localhost:%d%s?%s", port, AsyncManagementForwardingServletTest.coordinatorExpectedRequest.path, AsyncManagementForwardingServletTest.coordinatorExpectedRequest.query)).openConnection()));
        connection.setRequestMethod(AsyncManagementForwardingServletTest.coordinatorExpectedRequest.method);
        AsyncManagementForwardingServletTest.coordinatorExpectedRequest.headers.forEach(connection::setRequestProperty);
        Assert.assertEquals(200, connection.getResponseCode());
        Assert.assertTrue("coordinator called", AsyncManagementForwardingServletTest.coordinatorExpectedRequest.called);
        Assert.assertFalse("overlord called", AsyncManagementForwardingServletTest.overlordExpectedRequest.called);
    }

    @Test
    public void testCoordinatorEnable() throws Exception {
        AsyncManagementForwardingServletTest.coordinatorExpectedRequest.path = "/druid/coordinator/v1/datasources/myDatasource";
        AsyncManagementForwardingServletTest.coordinatorExpectedRequest.method = "POST";
        HttpURLConnection connection = ((HttpURLConnection) (new URL(StringUtils.format("http://localhost:%d%s", port, AsyncManagementForwardingServletTest.coordinatorExpectedRequest.path)).openConnection()));
        connection.setRequestMethod(AsyncManagementForwardingServletTest.coordinatorExpectedRequest.method);
        Assert.assertEquals(200, connection.getResponseCode());
        Assert.assertTrue("coordinator called", AsyncManagementForwardingServletTest.coordinatorExpectedRequest.called);
        Assert.assertFalse("overlord called", AsyncManagementForwardingServletTest.overlordExpectedRequest.called);
    }

    @Test
    public void testCoordinatorDisable() throws Exception {
        AsyncManagementForwardingServletTest.coordinatorExpectedRequest.path = "/druid/coordinator/v1/datasources/myDatasource/intervals/2016-06-27_2016-06-28";
        AsyncManagementForwardingServletTest.coordinatorExpectedRequest.method = "DELETE";
        HttpURLConnection connection = ((HttpURLConnection) (new URL(StringUtils.format("http://localhost:%d%s", port, AsyncManagementForwardingServletTest.coordinatorExpectedRequest.path)).openConnection()));
        connection.setRequestMethod(AsyncManagementForwardingServletTest.coordinatorExpectedRequest.method);
        Assert.assertEquals(200, connection.getResponseCode());
        Assert.assertTrue("coordinator called", AsyncManagementForwardingServletTest.coordinatorExpectedRequest.called);
        Assert.assertFalse("overlord called", AsyncManagementForwardingServletTest.overlordExpectedRequest.called);
    }

    @Test
    public void testCoordinatorProxyStatus() throws Exception {
        AsyncManagementForwardingServletTest.coordinatorExpectedRequest.path = "/status";
        AsyncManagementForwardingServletTest.coordinatorExpectedRequest.method = "GET";
        AsyncManagementForwardingServletTest.coordinatorExpectedRequest.headers = ImmutableMap.of("Authorization", "Basic bXl1c2VyOm15cGFzc3dvcmQ=");
        HttpURLConnection connection = ((HttpURLConnection) (new URL(StringUtils.format("http://localhost:%d/proxy/coordinator%s", port, AsyncManagementForwardingServletTest.coordinatorExpectedRequest.path)).openConnection()));
        connection.setRequestMethod(AsyncManagementForwardingServletTest.coordinatorExpectedRequest.method);
        AsyncManagementForwardingServletTest.coordinatorExpectedRequest.headers.forEach(connection::setRequestProperty);
        Assert.assertEquals(200, connection.getResponseCode());
        Assert.assertTrue("coordinator called", AsyncManagementForwardingServletTest.coordinatorExpectedRequest.called);
        Assert.assertFalse("overlord called", AsyncManagementForwardingServletTest.overlordExpectedRequest.called);
    }

    @Test
    public void testCoordinatorProxySegments() throws Exception {
        AsyncManagementForwardingServletTest.coordinatorExpectedRequest.path = "/druid/coordinator/v1/metadata/datasources/myDatasource/segments";
        AsyncManagementForwardingServletTest.coordinatorExpectedRequest.method = "POST";
        AsyncManagementForwardingServletTest.coordinatorExpectedRequest.headers = ImmutableMap.of("Authorization", "Basic bXl1c2VyOm15cGFzc3dvcmQ=");
        AsyncManagementForwardingServletTest.coordinatorExpectedRequest.body = "[\"2012-01-01T00:00:00.000/2012-01-03T00:00:00.000\", \"2012-01-05T00:00:00.000/2012-01-07T00:00:00.000\"]";
        HttpURLConnection connection = ((HttpURLConnection) (new URL(StringUtils.format("http://localhost:%d/proxy/coordinator%s", port, AsyncManagementForwardingServletTest.coordinatorExpectedRequest.path)).openConnection()));
        connection.setRequestMethod(AsyncManagementForwardingServletTest.coordinatorExpectedRequest.method);
        AsyncManagementForwardingServletTest.coordinatorExpectedRequest.headers.forEach(connection::setRequestProperty);
        connection.setDoOutput(true);
        OutputStream os = connection.getOutputStream();
        os.write(AsyncManagementForwardingServletTest.coordinatorExpectedRequest.body.getBytes(StandardCharsets.UTF_8));
        os.close();
        Assert.assertEquals(200, connection.getResponseCode());
        Assert.assertTrue("coordinator called", AsyncManagementForwardingServletTest.coordinatorExpectedRequest.called);
        Assert.assertFalse("overlord called", AsyncManagementForwardingServletTest.overlordExpectedRequest.called);
    }

    @Test
    public void testOverlordPostTask() throws Exception {
        AsyncManagementForwardingServletTest.overlordExpectedRequest.path = "/druid/indexer/v1/task";
        AsyncManagementForwardingServletTest.overlordExpectedRequest.method = "POST";
        AsyncManagementForwardingServletTest.overlordExpectedRequest.headers = ImmutableMap.of("Authorization", "Basic bXl1c2VyOm15cGFzc3dvcmQ=", "Content-Type", "application/json");
        AsyncManagementForwardingServletTest.overlordExpectedRequest.body = "{\"type\": \"index\", \"spec\": \"stuffGoesHere\"}";
        HttpURLConnection connection = ((HttpURLConnection) (new URL(StringUtils.format("http://localhost:%d%s", port, AsyncManagementForwardingServletTest.overlordExpectedRequest.path)).openConnection()));
        connection.setRequestMethod(AsyncManagementForwardingServletTest.overlordExpectedRequest.method);
        AsyncManagementForwardingServletTest.overlordExpectedRequest.headers.forEach(connection::setRequestProperty);
        connection.setDoOutput(true);
        OutputStream os = connection.getOutputStream();
        os.write(AsyncManagementForwardingServletTest.overlordExpectedRequest.body.getBytes(StandardCharsets.UTF_8));
        os.close();
        Assert.assertEquals(200, connection.getResponseCode());
        Assert.assertFalse("coordinator called", AsyncManagementForwardingServletTest.coordinatorExpectedRequest.called);
        Assert.assertTrue("overlord called", AsyncManagementForwardingServletTest.overlordExpectedRequest.called);
    }

    @Test
    public void testOverlordTaskStatus() throws Exception {
        AsyncManagementForwardingServletTest.overlordExpectedRequest.path = "/druid/indexer/v1/task/myTaskId/status";
        AsyncManagementForwardingServletTest.overlordExpectedRequest.method = "GET";
        AsyncManagementForwardingServletTest.overlordExpectedRequest.headers = ImmutableMap.of("Authorization", "Basic bXl1c2VyOm15cGFzc3dvcmQ=");
        HttpURLConnection connection = ((HttpURLConnection) (new URL(StringUtils.format("http://localhost:%d%s", port, AsyncManagementForwardingServletTest.overlordExpectedRequest.path)).openConnection()));
        connection.setRequestMethod(AsyncManagementForwardingServletTest.overlordExpectedRequest.method);
        AsyncManagementForwardingServletTest.overlordExpectedRequest.headers.forEach(connection::setRequestProperty);
        Assert.assertEquals(200, connection.getResponseCode());
        Assert.assertFalse("coordinator called", AsyncManagementForwardingServletTest.coordinatorExpectedRequest.called);
        Assert.assertTrue("overlord called", AsyncManagementForwardingServletTest.overlordExpectedRequest.called);
    }

    @Test
    public void testOverlordProxyLeader() throws Exception {
        AsyncManagementForwardingServletTest.overlordExpectedRequest.path = "/druid/indexer/v1/leader";
        AsyncManagementForwardingServletTest.overlordExpectedRequest.method = "GET";
        AsyncManagementForwardingServletTest.overlordExpectedRequest.headers = ImmutableMap.of("Authorization", "Basic bXl1c2VyOm15cGFzc3dvcmQ=");
        HttpURLConnection connection = ((HttpURLConnection) (new URL(StringUtils.format("http://localhost:%d/proxy/overlord%s", port, AsyncManagementForwardingServletTest.overlordExpectedRequest.path)).openConnection()));
        connection.setRequestMethod(AsyncManagementForwardingServletTest.overlordExpectedRequest.method);
        AsyncManagementForwardingServletTest.overlordExpectedRequest.headers.forEach(connection::setRequestProperty);
        Assert.assertEquals(200, connection.getResponseCode());
        Assert.assertFalse("coordinator called", AsyncManagementForwardingServletTest.coordinatorExpectedRequest.called);
        Assert.assertTrue("overlord called", AsyncManagementForwardingServletTest.overlordExpectedRequest.called);
    }

    @Test
    public void testBadProxyDestination() throws Exception {
        HttpURLConnection connection = ((HttpURLConnection) (new URL(StringUtils.format("http://localhost:%d/proxy/other/status", port)).openConnection()));
        connection.setRequestMethod("GET");
        Assert.assertEquals(400, connection.getResponseCode());
        Assert.assertFalse("coordinator called", AsyncManagementForwardingServletTest.coordinatorExpectedRequest.called);
        Assert.assertFalse("overlord called", AsyncManagementForwardingServletTest.overlordExpectedRequest.called);
    }

    @Test
    public void testLocalRequest() throws Exception {
        HttpURLConnection connection = ((HttpURLConnection) (new URL(StringUtils.format("http://localhost:%d/status", port)).openConnection()));
        connection.setRequestMethod("GET");
        Assert.assertEquals(404, connection.getResponseCode());
        Assert.assertFalse("coordinator called", AsyncManagementForwardingServletTest.coordinatorExpectedRequest.called);
        Assert.assertFalse("overlord called", AsyncManagementForwardingServletTest.overlordExpectedRequest.called);
    }

    public static class ProxyJettyServerInit implements JettyServerInitializer {
        @Override
        public void initialize(Server server, Injector injector) {
            final ServletContextHandler root = new ServletContextHandler(ServletContextHandler.SESSIONS);
            root.addServlet(new ServletHolder(new DefaultServlet()), "/*");
            final DruidLeaderSelector coordinatorLeaderSelector = new AsyncManagementForwardingServletTest.TestDruidLeaderSelector() {
                @Override
                public String getCurrentLeader() {
                    return StringUtils.format("http://localhost:%d", AsyncManagementForwardingServletTest.coordinatorPort);
                }
            };
            final DruidLeaderSelector overlordLeaderSelector = new AsyncManagementForwardingServletTest.TestDruidLeaderSelector() {
                @Override
                public String getCurrentLeader() {
                    return StringUtils.format("http://localhost:%d", AsyncManagementForwardingServletTest.overlordPort);
                }
            };
            ServletHolder holder = new ServletHolder(new AsyncManagementForwardingServlet(injector.getInstance(ObjectMapper.class), injector.getProvider(HttpClient.class), injector.getInstance(DruidHttpClientConfig.class), coordinatorLeaderSelector, overlordLeaderSelector));
            // NOTE: explicit maxThreads to workaround https://tickets.puppetlabs.com/browse/TK-152
            holder.setInitParameter("maxThreads", "256");
            root.addServlet(holder, "/druid/coordinator/*");
            root.addServlet(holder, "/druid/indexer/*");
            root.addServlet(holder, "/proxy/*");
            JettyServerInitUtils.addExtensionFilters(root, injector);
            final HandlerList handlerList = new HandlerList();
            handlerList.setHandlers(new Handler[]{ JettyServerInitUtils.wrapWithDefaultGzipHandler(root, 4096, (-1)) });
            server.setHandler(handlerList);
        }
    }

    private static class TestDruidLeaderSelector implements DruidLeaderSelector {
        @Nullable
        @Override
        public String getCurrentLeader() {
            return null;
        }

        @Override
        public boolean isLeader() {
            return false;
        }

        @Override
        public int localTerm() {
            return 0;
        }

        @Override
        public void registerListener(Listener listener) {
        }

        @Override
        public void unregisterListener() {
        }
    }
}

