/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.nifi.processors.standard;


import HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
import HttpServletResponse.SC_NOT_FOUND;
import HttpServletResponse.SC_NO_CONTENT;
import Scope.LOCAL;
import TestServer.NEED_CLIENT_AUTH;
import java.net.URLEncoder;
import java.util.Map;
import org.apache.nifi.processors.standard.GetHTTP.ACCEPT_CONTENT_TYPE;
import org.apache.nifi.processors.standard.GetHTTP.CONNECTION_TIMEOUT;
import org.apache.nifi.processors.standard.GetHTTP.ETAG;
import org.apache.nifi.processors.standard.GetHTTP.FILENAME;
import org.apache.nifi.processors.standard.GetHTTP.FOLLOW_REDIRECTS;
import org.apache.nifi.processors.standard.GetHTTP.LAST_MODIFIED;
import org.apache.nifi.processors.standard.GetHTTP.REDIRECT_COOKIE_POLICY;
import org.apache.nifi.processors.standard.GetHTTP.REL_SUCCESS;
import org.apache.nifi.processors.standard.GetHTTP.STANDARD_COOKIE_POLICY_STR;
import org.apache.nifi.processors.standard.GetHTTP.URL;
import org.apache.nifi.processors.standard.GetHTTP.USER_AGENT;
import org.apache.nifi.util.MockFlowFile;
import org.apache.nifi.util.TestRunner;
import org.apache.nifi.util.TestRunners;
import org.apache.nifi.web.util.TestServer;
import org.eclipse.jetty.servlet.ServletHandler;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 */
@SuppressWarnings("deprecation")
public class TestGetHTTP {
    private TestRunner controller;

    @Test
    public final void testContentModified() throws Exception {
        // set up web service
        ServletHandler handler = new ServletHandler();
        handler.addServletWithMapping(RESTServiceContentModified.class, "/*");
        // create the service
        TestServer server = new TestServer();
        server.addHandler(handler);
        try {
            server.startServer();
            // this is the base url with the random port
            String destination = server.getUrl();
            // set up NiFi mock controller
            controller = TestRunners.newTestRunner(GetHTTP.class);
            controller.setProperty(CONNECTION_TIMEOUT, "5 secs");
            controller.setProperty(URL, destination);
            controller.setProperty(FILENAME, "testFile");
            controller.setProperty(ACCEPT_CONTENT_TYPE, "application/json");
            controller.getStateManager().assertStateNotSet(ETAG, LOCAL);
            controller.getStateManager().assertStateNotSet(LAST_MODIFIED, LOCAL);
            controller.run(2);
            // verify the lastModified and entityTag are updated
            controller.getStateManager().assertStateNotEquals((((GetHTTP.ETAG) + ":") + destination), "", LOCAL);
            controller.getStateManager().assertStateNotEquals((((GetHTTP.LAST_MODIFIED) + ":") + destination), "Thu, 01 Jan 1970 00:00:00 GMT", LOCAL);
            // ran twice, but got one...which is good
            controller.assertTransferCount(REL_SUCCESS, 1);
            // verify remote.source flowfile attribute
            controller.getFlowFilesForRelationship(REL_SUCCESS).get(0).assertAttributeEquals("gethttp.remote.source", "localhost");
            controller.clearTransferState();
            // turn off checking for etag and lastModified
            RESTServiceContentModified.IGNORE_ETAG = true;
            RESTServiceContentModified.IGNORE_LAST_MODIFIED = true;
            controller.run(2);
            // ran twice, got two...which is good
            controller.assertTransferCount(REL_SUCCESS, 2);
            controller.clearTransferState();
            // turn on checking for etag
            RESTServiceContentModified.IGNORE_ETAG = false;
            controller.run(2);
            // ran twice, got 0...which is good
            controller.assertTransferCount(REL_SUCCESS, 0);
            // turn on checking for lastModified, but off for etag
            RESTServiceContentModified.IGNORE_LAST_MODIFIED = false;
            RESTServiceContentModified.IGNORE_ETAG = true;
            controller.run(2);
            // ran twice, got 0...which is good
            controller.assertTransferCount(REL_SUCCESS, 0);
            // turn off checking for lastModified, turn on checking for etag, but change the value
            RESTServiceContentModified.IGNORE_LAST_MODIFIED = true;
            RESTServiceContentModified.IGNORE_ETAG = false;
            RESTServiceContentModified.ETAG = 1;
            controller.run(2);
            // ran twice, got 1...but should have new cached etag
            controller.assertTransferCount(REL_SUCCESS, 1);
            String eTagStateValue = controller.getStateManager().getState(LOCAL).get((((GetHTTP.ETAG) + ":") + destination));
            Assert.assertEquals("1", org.apache.nifi.processors.standard.GetHTTP.parseStateValue(eTagStateValue).getValue());
            controller.clearTransferState();
            // turn off checking for Etag, turn on checking for lastModified, but change value
            RESTServiceContentModified.IGNORE_LAST_MODIFIED = false;
            RESTServiceContentModified.IGNORE_ETAG = true;
            RESTServiceContentModified.modificationDate = (((System.currentTimeMillis()) / 1000) * 1000) + 5000;
            String lastMod = controller.getStateManager().getState(LOCAL).get((((GetHTTP.LAST_MODIFIED) + ":") + destination));
            controller.run(2);
            // ran twice, got 1...but should have new cached etag
            controller.assertTransferCount(REL_SUCCESS, 1);
            controller.getStateManager().assertStateNotEquals((((GetHTTP.LAST_MODIFIED) + ":") + destination), lastMod, LOCAL);
            controller.clearTransferState();
        } finally {
            // shutdown web service
            server.shutdownServer();
        }
    }

    @Test
    public final void testContentModifiedTwoServers() throws Exception {
        // set up web services
        ServletHandler handler1 = new ServletHandler();
        handler1.addServletWithMapping(RESTServiceContentModified.class, "/*");
        ServletHandler handler2 = new ServletHandler();
        handler2.addServletWithMapping(RESTServiceContentModified.class, "/*");
        // create the services
        TestServer server1 = new TestServer();
        server1.addHandler(handler1);
        TestServer server2 = new TestServer();
        server2.addHandler(handler2);
        try {
            server1.startServer();
            server2.startServer();
            // this is the base urls with the random ports
            String destination1 = server1.getUrl();
            String destination2 = server2.getUrl();
            // set up NiFi mock controller
            controller = TestRunners.newTestRunner(GetHTTP.class);
            controller.setProperty(CONNECTION_TIMEOUT, "5 secs");
            controller.setProperty(URL, destination1);
            controller.setProperty(FILENAME, "testFile");
            controller.setProperty(ACCEPT_CONTENT_TYPE, "application/json");
            controller.getStateManager().assertStateNotSet((((GetHTTP.ETAG) + ":") + destination1), LOCAL);
            controller.getStateManager().assertStateNotSet((((GetHTTP.LAST_MODIFIED) + ":") + destination1), LOCAL);
            controller.run(2);
            // verify the lastModified and entityTag are updated
            controller.getStateManager().assertStateNotEquals((((GetHTTP.ETAG) + ":") + destination1), "", LOCAL);
            controller.getStateManager().assertStateNotEquals((((GetHTTP.LAST_MODIFIED) + ":") + destination1), "Thu, 01 Jan 1970 00:00:00 GMT", LOCAL);
            // ran twice, but got one...which is good
            controller.assertTransferCount(REL_SUCCESS, 1);
            controller.clearTransferState();
            controller.setProperty(URL, destination2);
            controller.getStateManager().assertStateNotSet((((GetHTTP.ETAG) + ":") + destination2), LOCAL);
            controller.getStateManager().assertStateNotSet((((GetHTTP.LAST_MODIFIED) + ":") + destination2), LOCAL);
            controller.run(2);
            // ran twice, but got one...which is good
            controller.assertTransferCount(REL_SUCCESS, 1);
            // verify the lastModified's and entityTags are updated
            controller.getStateManager().assertStateNotEquals((((GetHTTP.ETAG) + ":") + destination1), "", LOCAL);
            controller.getStateManager().assertStateNotEquals((((GetHTTP.LAST_MODIFIED) + ":") + destination1), "Thu, 01 Jan 1970 00:00:00 GMT", LOCAL);
            controller.getStateManager().assertStateNotEquals((((GetHTTP.ETAG) + ":") + destination2), "", LOCAL);
            controller.getStateManager().assertStateNotEquals((((GetHTTP.LAST_MODIFIED) + ":") + destination2), "Thu, 01 Jan 1970 00:00:00 GMT", LOCAL);
        } finally {
            // shutdown web services
            server1.shutdownServer();
            server2.shutdownServer();
        }
    }

    @Test
    public final void testUserAgent() throws Exception {
        // set up web service
        ServletHandler handler = new ServletHandler();
        handler.addServletWithMapping(UserAgentTestingServlet.class, "/*");
        // create the service
        TestServer server = new TestServer();
        server.addHandler(handler);
        try {
            server.startServer();
            String destination = server.getUrl();
            // set up NiFi mock controller
            controller = TestRunners.newTestRunner(GetHTTP.class);
            controller.setProperty(CONNECTION_TIMEOUT, "5 secs");
            controller.setProperty(URL, destination);
            controller.setProperty(FILENAME, "testFile");
            controller.setProperty(ACCEPT_CONTENT_TYPE, "application/json");
            controller.run();
            controller.assertTransferCount(REL_SUCCESS, 0);
            controller.setProperty(USER_AGENT, "testUserAgent");
            controller.run();
            controller.assertTransferCount(REL_SUCCESS, 1);
            // shutdown web service
        } finally {
            server.shutdownServer();
        }
    }

    @Test
    public final void testDynamicHeaders() throws Exception {
        // set up web service
        ServletHandler handler = new ServletHandler();
        handler.addServletWithMapping(UserAgentTestingServlet.class, "/*");
        // create the service
        TestServer server = new TestServer();
        server.addHandler(handler);
        try {
            server.startServer();
            String destination = server.getUrl();
            // set up NiFi mock controller
            controller = TestRunners.newTestRunner(GetHTTP.class);
            controller.setProperty(CONNECTION_TIMEOUT, "5 secs");
            controller.setProperty(URL, destination);
            controller.setProperty(FILENAME, "testFile");
            controller.setProperty(ACCEPT_CONTENT_TYPE, "application/json");
            controller.setProperty(USER_AGENT, "testUserAgent");
            controller.setProperty("Static-Header", "StaticHeaderValue");
            controller.setProperty("EL-Header", "${now()}");
            controller.run();
            controller.assertTransferCount(REL_SUCCESS, 1);
            // shutdown web service
        } finally {
            server.shutdownServer();
        }
    }

    @Test
    public final void testExpressionLanguage() throws Exception {
        // set up web service
        ServletHandler handler = new ServletHandler();
        handler.addServletWithMapping(UserAgentTestingServlet.class, "/*");
        // create the service
        TestServer server = new TestServer();
        server.addHandler(handler);
        try {
            server.startServer();
            String destination = server.getUrl();
            // set up NiFi mock controller
            controller = TestRunners.newTestRunner(GetHTTP.class);
            controller.setProperty(CONNECTION_TIMEOUT, "5 secs");
            controller.setProperty(URL, (destination + "/test_${literal(1)}.pdf"));
            controller.setProperty(FILENAME, "test_${now():format('yyyy/MM/dd_HH:mm:ss')}");
            controller.setProperty(ACCEPT_CONTENT_TYPE, "application/json");
            controller.setProperty(USER_AGENT, "testUserAgent");
            controller.run();
            controller.assertTransferCount(REL_SUCCESS, 1);
            MockFlowFile response = controller.getFlowFilesForRelationship(REL_SUCCESS).get(0);
            response.assertAttributeEquals("gethttp.remote.source", "localhost");
            String fileName = response.getAttribute(CoreAttributes.FILENAME.key());
            Assert.assertTrue(fileName.matches("test_\\d\\d\\d\\d/\\d\\d/\\d\\d_\\d\\d:\\d\\d:\\d\\d"));
            // shutdown web service
        } finally {
            server.shutdownServer();
        }
    }

    /**
     * Test for HTTP errors
     *
     * @throws Exception
     * 		exception
     */
    @Test
    public final void testHttpErrors() throws Exception {
        // set up web service
        ServletHandler handler = new ServletHandler();
        handler.addServletWithMapping(HttpErrorServlet.class, "/*");
        // create the service
        TestServer server = new TestServer();
        server.addHandler(handler);
        try {
            server.startServer();
            HttpErrorServlet servlet = ((HttpErrorServlet) (handler.getServlets()[0].getServlet()));
            String destination = server.getUrl();
            this.controller = TestRunners.newTestRunner(GetHTTP.class);
            this.controller.setProperty(CONNECTION_TIMEOUT, "5 secs");
            this.controller.setProperty(URL, (destination + "/test_${literal(1)}.pdf"));
            this.controller.setProperty(FILENAME, "test_${now():format('yyyy/MM/dd_HH:mm:ss')}");
            this.controller.setProperty(ACCEPT_CONTENT_TYPE, "application/json");
            this.controller.setProperty(USER_AGENT, "testUserAgent");
            // 204 - NO CONTENT
            servlet.setErrorToReturn(SC_NO_CONTENT);
            this.controller.run();
            this.controller.assertTransferCount(REL_SUCCESS, 0);
            // 404 - NOT FOUND
            servlet.setErrorToReturn(SC_NOT_FOUND);
            this.controller.run();
            this.controller.assertTransferCount(REL_SUCCESS, 0);
            // 500 - INTERNAL SERVER ERROR
            servlet.setErrorToReturn(SC_INTERNAL_SERVER_ERROR);
            this.controller.run();
            this.controller.assertTransferCount(REL_SUCCESS, 0);
        } finally {
            // shutdown web service
            server.shutdownServer();
        }
    }

    @Test
    public final void testSecure_oneWaySsl() throws Exception {
        // set up web service
        final ServletHandler handler = new ServletHandler();
        handler.addServletWithMapping(HelloWorldServlet.class, "/*");
        // create the service, disabling the need for client auth
        final Map<String, String> serverSslProperties = TestGetHTTP.getKeystoreProperties();
        serverSslProperties.put(NEED_CLIENT_AUTH, Boolean.toString(false));
        final TestServer server = new TestServer(serverSslProperties);
        server.addHandler(handler);
        try {
            server.startServer();
            final String destination = server.getSecureUrl();
            // set up NiFi mock controller
            controller = TestRunners.newTestRunner(GetHTTP.class);
            // Use context service with only a truststore
            useSSLContextService(TestGetHTTP.getTruststoreProperties());
            controller.setProperty(CONNECTION_TIMEOUT, "5 secs");
            controller.setProperty(URL, destination);
            controller.setProperty(FILENAME, "testFile");
            controller.setProperty(ACCEPT_CONTENT_TYPE, "application/json");
            controller.run();
            controller.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
            final MockFlowFile mff = controller.getFlowFilesForRelationship(REL_SUCCESS).get(0);
            mff.assertContentEquals("Hello, World!");
        } finally {
            server.shutdownServer();
        }
    }

    @Test
    public final void testSecure_twoWaySsl() throws Exception {
        // set up web service
        final ServletHandler handler = new ServletHandler();
        handler.addServletWithMapping(HelloWorldServlet.class, "/*");
        // create the service, providing both truststore and keystore properties, requiring client auth (default)
        final Map<String, String> twoWaySslProperties = TestGetHTTP.getKeystoreProperties();
        twoWaySslProperties.putAll(TestGetHTTP.getTruststoreProperties());
        final TestServer server = new TestServer(twoWaySslProperties);
        server.addHandler(handler);
        try {
            server.startServer();
            final String destination = server.getSecureUrl();
            // set up NiFi mock controller
            controller = TestRunners.newTestRunner(GetHTTP.class);
            // Use context service with a keystore and a truststore
            useSSLContextService(twoWaySslProperties);
            controller.setProperty(CONNECTION_TIMEOUT, "10 secs");
            controller.setProperty(URL, destination);
            controller.setProperty(FILENAME, "testFile");
            controller.setProperty(ACCEPT_CONTENT_TYPE, "application/json");
            controller.run();
            controller.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
            final MockFlowFile mff = controller.getFlowFilesForRelationship(REL_SUCCESS).get(0);
            mff.assertContentEquals("Hello, World!");
        } finally {
            server.shutdownServer();
        }
    }

    @Test
    public final void testCookiePolicy() throws Exception {
        // set up web services
        ServletHandler handler1 = new ServletHandler();
        handler1.addServletWithMapping(CookieTestingServlet.class, "/*");
        ServletHandler handler2 = new ServletHandler();
        handler2.addServletWithMapping(CookieVerificationTestingServlet.class, "/*");
        // create the services
        TestServer server1 = new TestServer();
        server1.addHandler(handler1);
        TestServer server2 = new TestServer();
        server2.addHandler(handler2);
        try {
            server1.startServer();
            server2.startServer();
            // this is the base urls with the random ports
            String destination1 = server1.getUrl();
            String destination2 = server2.getUrl();
            // set up NiFi mock controller
            controller = TestRunners.newTestRunner(GetHTTP.class);
            controller.setProperty(CONNECTION_TIMEOUT, "5 secs");
            controller.setProperty(URL, ((((destination1 + "/?redirect=") + (URLEncoder.encode(destination2, "UTF-8"))) + "&datemode=") + (CookieTestingServlet.DATEMODE_COOKIE_DEFAULT)));
            controller.setProperty(FILENAME, "testFile");
            controller.setProperty(FOLLOW_REDIRECTS, "true");
            controller.run(1);
            // verify default cookie data does successful redirect
            controller.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
            MockFlowFile ff = controller.getFlowFilesForRelationship(REL_SUCCESS).get(0);
            ff.assertContentEquals("Hello, World!");
            controller.clearTransferState();
            // verify NON-standard cookie data fails with default redirect_cookie_policy
            controller.setProperty(URL, ((((destination1 + "/?redirect=") + (URLEncoder.encode(destination2, "UTF-8"))) + "&datemode=") + (CookieTestingServlet.DATEMODE_COOKIE_NOT_TYPICAL)));
            controller.run(1);
            controller.assertAllFlowFilesTransferred(REL_SUCCESS, 0);
            controller.clearTransferState();
            // change GetHTTP to place it in STANDARD cookie policy mode
            controller.setProperty(REDIRECT_COOKIE_POLICY, STANDARD_COOKIE_POLICY_STR);
            controller.setProperty(URL, ((((destination1 + "/?redirect=") + (URLEncoder.encode(destination2, "UTF-8"))) + "&datemode=") + (CookieTestingServlet.DATEMODE_COOKIE_NOT_TYPICAL)));
            controller.run(1);
            // verify NON-standard cookie data does successful redirect
            controller.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
            ff = controller.getFlowFilesForRelationship(REL_SUCCESS).get(0);
            ff.assertContentEquals("Hello, World!");
        } finally {
            // shutdown web services
            server1.shutdownServer();
            server2.shutdownServer();
        }
    }
}

