/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hadoop.yarn.server.resourcemanager.webapp;


import ClientResponse.Status.BAD_REQUEST;
import ClientResponse.Status.NOT_FOUND;
import ContainerState.COMPLETE;
import MediaType.APPLICATION_JSON;
import MediaType.APPLICATION_XML;
import RMAppAttemptState.FAILED;
import RMAppState.ACCEPTED;
import YarnConfiguration.DEFAULT_RM_AM_MAX_ATTEMPTS;
import YarnConfiguration.RM_AM_MAX_ATTEMPTS;
import YarnConfiguration.RM_SCHEDULER;
import com.google.inject.Guice;
import com.google.inject.servlet.GuiceFilter;
import com.google.inject.servlet.ServletModule;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.guice.spi.container.servlet.GuiceContainer;
import com.sun.jersey.test.framework.WebAppDescriptor;
import java.io.StringReader;
import javax.ws.rs.core.MediaType;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.http.JettyUtils;
import org.apache.hadoop.yarn.server.resourcemanager.MockAM;
import org.apache.hadoop.yarn.server.resourcemanager.MockNM;
import org.apache.hadoop.yarn.server.resourcemanager.MockRM;
import org.apache.hadoop.yarn.server.resourcemanager.ResourceManager;
import org.apache.hadoop.yarn.server.resourcemanager.rmapp.RMApp;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.ResourceScheduler;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.fifo.FifoScheduler;
import org.apache.hadoop.yarn.webapp.GenericExceptionHandler;
import org.apache.hadoop.yarn.webapp.GuiceServletConfig;
import org.apache.hadoop.yarn.webapp.JerseyTestBase;
import org.apache.hadoop.yarn.webapp.WebServicesTestUtils;
import org.codehaus.jettison.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;


public class TestRMWebServicesAppAttempts extends JerseyTestBase {
    private static MockRM rm;

    private static final int CONTAINER_MB = 1024;

    private static class WebServletModule extends ServletModule {
        @Override
        protected void configureServlets() {
            bind(JAXBContextResolver.class);
            bind(RMWebServices.class);
            bind(GenericExceptionHandler.class);
            Configuration conf = new Configuration();
            conf.setInt(RM_AM_MAX_ATTEMPTS, DEFAULT_RM_AM_MAX_ATTEMPTS);
            conf.setClass(RM_SCHEDULER, FifoScheduler.class, ResourceScheduler.class);
            TestRMWebServicesAppAttempts.rm = new MockRM(conf);
            bind(ResourceManager.class).toInstance(TestRMWebServicesAppAttempts.rm);
            serve("/*").with(GuiceContainer.class);
        }
    }

    static {
        GuiceServletConfig.setInjector(Guice.createInjector(new TestRMWebServicesAppAttempts.WebServletModule()));
    }

    public TestRMWebServicesAppAttempts() {
        super(new WebAppDescriptor.Builder("org.apache.hadoop.yarn.server.resourcemanager.webapp").contextListenerClass(GuiceServletConfig.class).filterClass(GuiceFilter.class).contextPath("jersey-guice-filter").servletPath("/").build());
    }

    @Test
    public void testAppAttempts() throws Exception {
        start();
        MockNM amNodeManager = TestRMWebServicesAppAttempts.rm.registerNode("127.0.0.1:1234", 2048);
        RMApp app1 = TestRMWebServicesAppAttempts.rm.submitApp(TestRMWebServicesAppAttempts.CONTAINER_MB, "testwordcount", "user1");
        amNodeManager.nodeHeartbeat(true);
        testAppAttemptsHelper(app1.getApplicationId().toString(), app1, APPLICATION_JSON);
        stop();
    }

    @Test(timeout = 20000)
    public void testMultipleAppAttempts() throws Exception {
        start();
        MockNM amNodeManager = TestRMWebServicesAppAttempts.rm.registerNode("127.0.0.1:1234", 8192);
        RMApp app1 = TestRMWebServicesAppAttempts.rm.submitApp(TestRMWebServicesAppAttempts.CONTAINER_MB, "testwordcount", "user1");
        MockAM am = MockRM.launchAndRegisterAM(app1, TestRMWebServicesAppAttempts.rm, amNodeManager);
        int maxAppAttempts = getConfig().getInt(RM_AM_MAX_ATTEMPTS, DEFAULT_RM_AM_MAX_ATTEMPTS);
        Assert.assertTrue((maxAppAttempts > 1));
        int numAttempt = 1;
        while (true) {
            // fail the AM by sending CONTAINER_FINISHED event without registering.
            amNodeManager.nodeHeartbeat(am.getApplicationAttemptId(), 1, COMPLETE);
            TestRMWebServicesAppAttempts.rm.waitForState(am.getApplicationAttemptId(), FAILED);
            if (numAttempt == maxAppAttempts) {
                TestRMWebServicesAppAttempts.rm.waitForState(app1.getApplicationId(), RMAppState.FAILED);
                break;
            }
            // wait for app to start a new attempt.
            TestRMWebServicesAppAttempts.rm.waitForState(app1.getApplicationId(), ACCEPTED);
            am = MockRM.launchAndRegisterAM(app1, TestRMWebServicesAppAttempts.rm, amNodeManager);
            numAttempt++;
        } 
        Assert.assertEquals("incorrect number of attempts", maxAppAttempts, app1.getAppAttempts().values().size());
        testAppAttemptsHelper(app1.getApplicationId().toString(), app1, APPLICATION_JSON);
        stop();
    }

    @Test
    public void testAppAttemptsSlash() throws Exception {
        start();
        MockNM amNodeManager = TestRMWebServicesAppAttempts.rm.registerNode("127.0.0.1:1234", 2048);
        RMApp app1 = TestRMWebServicesAppAttempts.rm.submitApp(TestRMWebServicesAppAttempts.CONTAINER_MB);
        amNodeManager.nodeHeartbeat(true);
        testAppAttemptsHelper(((app1.getApplicationId().toString()) + "/"), app1, APPLICATION_JSON);
        stop();
    }

    @Test
    public void testAppAttemptsDefault() throws Exception {
        start();
        MockNM amNodeManager = TestRMWebServicesAppAttempts.rm.registerNode("127.0.0.1:1234", 2048);
        RMApp app1 = TestRMWebServicesAppAttempts.rm.submitApp(TestRMWebServicesAppAttempts.CONTAINER_MB);
        amNodeManager.nodeHeartbeat(true);
        testAppAttemptsHelper(((app1.getApplicationId().toString()) + "/"), app1, "");
        stop();
    }

    @Test
    public void testInvalidAppIdGetAttempts() throws Exception {
        start();
        MockNM amNodeManager = TestRMWebServicesAppAttempts.rm.registerNode("127.0.0.1:1234", 2048);
        RMApp app = TestRMWebServicesAppAttempts.rm.submitApp(TestRMWebServicesAppAttempts.CONTAINER_MB);
        amNodeManager.nodeHeartbeat(true);
        WebResource r = resource();
        try {
            r.path("ws").path("v1").path("cluster").path("apps").path("application_invalid_12").path("appattempts").accept(APPLICATION_JSON).get(JSONObject.class);
            Assert.fail("should have thrown exception on invalid appAttempt");
        } catch (UniformInterfaceException ue) {
            ClientResponse response = ue.getResponse();
            WebServicesTestUtils.assertResponseStatusCode(BAD_REQUEST, response.getStatusInfo());
            Assert.assertEquals((((MediaType.APPLICATION_JSON_TYPE) + "; ") + (JettyUtils.UTF_8)), response.getType().toString());
            JSONObject msg = response.getEntity(JSONObject.class);
            JSONObject exception = msg.getJSONObject("RemoteException");
            Assert.assertEquals("incorrect number of elements", 3, exception.length());
            String message = exception.getString("message");
            String type = exception.getString("exception");
            String classname = exception.getString("javaClassName");
            WebServicesTestUtils.checkStringMatch("exception message", ("java.lang.IllegalArgumentException: Invalid ApplicationId:" + " application_invalid_12"), message);
            WebServicesTestUtils.checkStringMatch("exception type", "BadRequestException", type);
            WebServicesTestUtils.checkStringMatch("exception classname", "org.apache.hadoop.yarn.webapp.BadRequestException", classname);
        } finally {
            stop();
        }
    }

    @Test
    public void testInvalidAppAttemptId() throws Exception {
        start();
        MockNM amNodeManager = TestRMWebServicesAppAttempts.rm.registerNode("127.0.0.1:1234", 2048);
        RMApp app = TestRMWebServicesAppAttempts.rm.submitApp(TestRMWebServicesAppAttempts.CONTAINER_MB);
        amNodeManager.nodeHeartbeat(true);
        WebResource r = resource();
        try {
            r.path("ws").path("v1").path("cluster").path("apps").path(app.getApplicationId().toString()).path("appattempts").path("appattempt_invalid_12_000001").accept(APPLICATION_JSON).get(JSONObject.class);
            Assert.fail("should have thrown exception on invalid appAttempt");
        } catch (UniformInterfaceException ue) {
            ClientResponse response = ue.getResponse();
            WebServicesTestUtils.assertResponseStatusCode(BAD_REQUEST, response.getStatusInfo());
            Assert.assertEquals((((MediaType.APPLICATION_JSON_TYPE) + "; ") + (JettyUtils.UTF_8)), response.getType().toString());
            JSONObject msg = response.getEntity(JSONObject.class);
            JSONObject exception = msg.getJSONObject("RemoteException");
            Assert.assertEquals("incorrect number of elements", 3, exception.length());
            String message = exception.getString("message");
            String type = exception.getString("exception");
            String classname = exception.getString("javaClassName");
            WebServicesTestUtils.checkStringMatch("exception message", ("java.lang.IllegalArgumentException: Invalid AppAttemptId:" + " appattempt_invalid_12_000001"), message);
            WebServicesTestUtils.checkStringMatch("exception type", "BadRequestException", type);
            WebServicesTestUtils.checkStringMatch("exception classname", "org.apache.hadoop.yarn.webapp.BadRequestException", classname);
        } finally {
            stop();
        }
    }

    @Test
    public void testNonexistAppAttempts() throws Exception {
        start();
        MockNM amNodeManager = TestRMWebServicesAppAttempts.rm.registerNode("127.0.0.1:1234", 2048);
        TestRMWebServicesAppAttempts.rm.submitApp(TestRMWebServicesAppAttempts.CONTAINER_MB, "testwordcount", "user1");
        amNodeManager.nodeHeartbeat(true);
        WebResource r = resource();
        try {
            r.path("ws").path("v1").path("cluster").path("apps").path("application_00000_0099").accept(APPLICATION_JSON).get(JSONObject.class);
            Assert.fail("should have thrown exception on invalid appid");
        } catch (UniformInterfaceException ue) {
            ClientResponse response = ue.getResponse();
            WebServicesTestUtils.assertResponseStatusCode(NOT_FOUND, response.getStatusInfo());
            Assert.assertEquals((((MediaType.APPLICATION_JSON_TYPE) + "; ") + (JettyUtils.UTF_8)), response.getType().toString());
            JSONObject msg = response.getEntity(JSONObject.class);
            JSONObject exception = msg.getJSONObject("RemoteException");
            Assert.assertEquals("incorrect number of elements", 3, exception.length());
            String message = exception.getString("message");
            String type = exception.getString("exception");
            String classname = exception.getString("javaClassName");
            WebServicesTestUtils.checkStringMatch("exception message", "java.lang.Exception: app with id: application_00000_0099 not found", message);
            WebServicesTestUtils.checkStringMatch("exception type", "NotFoundException", type);
            WebServicesTestUtils.checkStringMatch("exception classname", "org.apache.hadoop.yarn.webapp.NotFoundException", classname);
        } finally {
            stop();
        }
    }

    @Test
    public void testAppAttemptsXML() throws Exception {
        start();
        String user = "user1";
        MockNM amNodeManager = TestRMWebServicesAppAttempts.rm.registerNode("127.0.0.1:1234", 2048);
        RMApp app1 = TestRMWebServicesAppAttempts.rm.submitApp(TestRMWebServicesAppAttempts.CONTAINER_MB, "testwordcount", user);
        amNodeManager.nodeHeartbeat(true);
        WebResource r = resource();
        ClientResponse response = r.path("ws").path("v1").path("cluster").path("apps").path(app1.getApplicationId().toString()).path("appattempts").accept(APPLICATION_XML).get(ClientResponse.class);
        Assert.assertEquals((((MediaType.APPLICATION_XML_TYPE) + "; ") + (JettyUtils.UTF_8)), response.getType().toString());
        String xml = response.getEntity(String.class);
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        InputSource is = new InputSource();
        is.setCharacterStream(new StringReader(xml));
        Document dom = db.parse(is);
        NodeList nodes = dom.getElementsByTagName("appAttempts");
        Assert.assertEquals("incorrect number of elements", 1, nodes.getLength());
        NodeList attempt = dom.getElementsByTagName("appAttempt");
        Assert.assertEquals("incorrect number of elements", 1, attempt.getLength());
        verifyAppAttemptsXML(attempt, app1.getCurrentAppAttempt(), user);
        stop();
    }
}

