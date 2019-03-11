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
package org.apache.hadoop.yarn.server.nodemanager.webapp;


import HttpServletResponse.SC_TEMPORARY_REDIRECT;
import YarnConfiguration.LOG_AGGREGATION_ENABLED;
import YarnConfiguration.YARN_LOG_SERVER_URL;
import com.google.inject.Injector;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.yarn.api.records.ApplicationAttemptId;
import org.apache.hadoop.yarn.api.records.ApplicationId;
import org.apache.hadoop.yarn.api.records.ContainerId;
import org.apache.hadoop.yarn.api.records.NodeId;
import org.apache.hadoop.yarn.server.nodemanager.LocalDirsHandlerService;
import org.apache.hadoop.yarn.server.nodemanager.NodeManager.NMContext;
import org.apache.hadoop.yarn.server.nodemanager.containermanager.application.Application;
import org.glassfish.grizzly.servlet.HttpServletResponseImpl;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


/**
 * Basic sanity Tests for NMWebFilter.
 */
public class TestNMWebFilter {
    private static final String LOG_SERVER_URI = "log-server:1999/logs";

    private static final String USER = "testUser";

    @Test(timeout = 5000)
    public void testRedirection() throws Exception {
        ApplicationId appId = ApplicationId.newInstance(System.currentTimeMillis(), 1);
        ApplicationAttemptId attemptId = ApplicationAttemptId.newInstance(appId, 1);
        ContainerId containerId = ContainerId.newContainerId(attemptId, 1);
        NMContext mockNMContext = Mockito.mock(NMContext.class);
        ConcurrentMap<ApplicationId, Application> applications = new ConcurrentHashMap<>();
        Mockito.when(mockNMContext.getApplications()).thenReturn(applications);
        LocalDirsHandlerService mockLocalDirsHandlerService = Mockito.mock(LocalDirsHandlerService.class);
        Configuration conf = new Configuration();
        conf.setBoolean(LOG_AGGREGATION_ENABLED, true);
        conf.set(YARN_LOG_SERVER_URL, ("http://" + (TestNMWebFilter.LOG_SERVER_URI)));
        Mockito.when(mockLocalDirsHandlerService.getConfig()).thenReturn(conf);
        Mockito.when(mockNMContext.getLocalDirsHandler()).thenReturn(mockLocalDirsHandlerService);
        NodeId nodeId = NodeId.newInstance("testNM", 9999);
        Mockito.when(mockNMContext.getNodeId()).thenReturn(nodeId);
        Injector mockInjector = Mockito.mock(Injector.class);
        NMWebAppFilter testFilter = new NMWebAppFilter(mockInjector, mockNMContext);
        TestNMWebFilter.HttpServletResponseForTest response = new TestNMWebFilter.HttpServletResponseForTest();
        // dummy filter
        FilterChain chain = new FilterChain() {
            @Override
            public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse) throws IOException, ServletException {
                // Do Nothing
            }
        };
        String uri = (("testNM:8042/node/containerlogs/" + (containerId.toString())) + "/") + (TestNMWebFilter.USER);
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        Mockito.when(request.getRequestURI()).thenReturn(uri);
        testFilter.doFilter(request, response, chain);
        Assert.assertEquals(SC_TEMPORARY_REDIRECT, response.status);
        String redirect = response.getHeader("Location");
        Assert.assertTrue(redirect.contains(TestNMWebFilter.LOG_SERVER_URI));
        Assert.assertTrue(redirect.contains(nodeId.toString()));
        Assert.assertTrue(redirect.contains(containerId.toString()));
        Assert.assertTrue(redirect.contains(TestNMWebFilter.USER));
        String logType = "syslog";
        uri = ((((("testNM:8042/node/containerlogs/" + (containerId.toString())) + "/") + (TestNMWebFilter.USER)) + "/") + logType) + "/?start=10";
        HttpServletRequest request2 = Mockito.mock(HttpServletRequest.class);
        Mockito.when(request2.getRequestURI()).thenReturn(uri);
        Mockito.when(request2.getQueryString()).thenReturn("start=10");
        testFilter.doFilter(request2, response, chain);
        Assert.assertEquals(SC_TEMPORARY_REDIRECT, response.status);
        redirect = response.getHeader("Location");
        Assert.assertTrue(redirect.contains(TestNMWebFilter.LOG_SERVER_URI));
        Assert.assertTrue(redirect.contains(nodeId.toString()));
        Assert.assertTrue(redirect.contains(containerId.toString()));
        Assert.assertTrue(redirect.contains(TestNMWebFilter.USER));
        Assert.assertTrue(redirect.contains(logType));
        Assert.assertTrue(redirect.contains("start=10"));
    }

    private class HttpServletResponseForTest extends HttpServletResponseImpl {
        String redirectLocation = "";

        int status;

        private String contentType;

        private final Map<String, String> headers = new HashMap<>(1);

        private StringWriter body;

        public String getRedirect() {
            return redirectLocation;
        }

        @Override
        public void sendRedirect(String location) throws IOException {
            redirectLocation = location;
        }

        @Override
        public String encodeRedirectURL(String url) {
            return url;
        }

        @Override
        public void setStatus(int status) {
            this.status = status;
        }

        @Override
        public void setContentType(String type) {
            this.contentType = type;
        }

        @Override
        public void setHeader(String name, String value) {
            headers.put(name, value);
        }

        public String getHeader(String name) {
            return headers.get(name);
        }

        @Override
        public PrintWriter getWriter() throws IOException {
            body = new StringWriter();
            return new PrintWriter(body);
        }
    }
}

