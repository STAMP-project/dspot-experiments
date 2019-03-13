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
package org.apache.hadoop.yarn.server.resourcemanager.webapp;


import DeSelectFields.DeSelectType.AM_NODE_LABEL_EXPRESSION;
import DeSelectFields.DeSelectType.APP_NODE_LABEL_EXPRESSION;
import DeSelectFields.DeSelectType.RESOURCE_INFO;
import DeSelectFields.DeSelectType.RESOURCE_REQUESTS;
import DeSelectFields.DeSelectType.TIMEOUTS;
import FinalApplicationStatus.UNDEFINED;
import MediaType.APPLICATION_JSON;
import MediaType.APPLICATION_XML;
import Status.BAD_REQUEST;
import Status.NOT_FOUND;
import YarnApplicationState.ACCEPTED;
import YarnApplicationState.FINISHED;
import YarnApplicationState.KILLED;
import YarnApplicationState.RUNNING;
import YarnConfiguration.DEFAULT_RM_AM_MAX_ATTEMPTS;
import YarnConfiguration.RM_AM_MAX_ATTEMPTS;
import YarnConfiguration.RM_SCHEDULER;
import com.google.inject.Guice;
import com.google.inject.servlet.GuiceFilter;
import com.google.inject.servlet.ServletModule;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.core.util.MultivaluedMapImpl;
import com.sun.jersey.guice.spi.container.servlet.GuiceContainer;
import com.sun.jersey.test.framework.WebAppDescriptor;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Set;
import javax.ws.rs.core.MediaType;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.http.JettyUtils;
import org.apache.hadoop.security.UserGroupInformation;
import org.apache.hadoop.yarn.api.records.YarnApplicationState;
import org.apache.hadoop.yarn.server.resourcemanager.MockAM;
import org.apache.hadoop.yarn.server.resourcemanager.MockNM;
import org.apache.hadoop.yarn.server.resourcemanager.MockRM;
import org.apache.hadoop.yarn.server.resourcemanager.ResourceManager;
import org.apache.hadoop.yarn.server.resourcemanager.rmapp.RMApp;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.ResourceScheduler;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.fifo.FifoScheduler;
import org.apache.hadoop.yarn.server.resourcemanager.webapp.dao.AppInfo;
import org.apache.hadoop.yarn.webapp.GenericExceptionHandler;
import org.apache.hadoop.yarn.webapp.GuiceServletConfig;
import org.apache.hadoop.yarn.webapp.JerseyTestBase;
import org.apache.hadoop.yarn.webapp.WebServicesTestUtils;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;


public class TestRMWebServicesApps extends JerseyTestBase {
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
            TestRMWebServicesApps.rm = new MockRM(conf);
            bind(ResourceManager.class).toInstance(TestRMWebServicesApps.rm);
            serve("/*").with(GuiceContainer.class);
        }
    }

    static {
        GuiceServletConfig.setInjector(Guice.createInjector(new TestRMWebServicesApps.WebServletModule()));
    }

    public TestRMWebServicesApps() {
        super(new WebAppDescriptor.Builder("org.apache.hadoop.yarn.server.resourcemanager.webapp").contextListenerClass(GuiceServletConfig.class).filterClass(GuiceFilter.class).contextPath("jersey-guice-filter").servletPath("/").build());
    }

    @Test
    public void testApps() throws Exception, JSONException {
        start();
        MockNM amNodeManager = TestRMWebServicesApps.rm.registerNode("127.0.0.1:1234", 2048);
        RMApp app1 = TestRMWebServicesApps.rm.submitApp(TestRMWebServicesApps.CONTAINER_MB);
        amNodeManager.nodeHeartbeat(true);
        testAppsHelper("apps", app1, APPLICATION_JSON);
        stop();
    }

    @Test
    public void testAppsSlash() throws Exception, JSONException {
        start();
        MockNM amNodeManager = TestRMWebServicesApps.rm.registerNode("127.0.0.1:1234", 2048);
        RMApp app1 = TestRMWebServicesApps.rm.submitApp(TestRMWebServicesApps.CONTAINER_MB);
        amNodeManager.nodeHeartbeat(true);
        testAppsHelper("apps/", app1, APPLICATION_JSON);
        stop();
    }

    @Test
    public void testAppsDefault() throws Exception, JSONException {
        start();
        MockNM amNodeManager = TestRMWebServicesApps.rm.registerNode("127.0.0.1:1234", 2048);
        RMApp app1 = TestRMWebServicesApps.rm.submitApp(TestRMWebServicesApps.CONTAINER_MB);
        amNodeManager.nodeHeartbeat(true);
        testAppsHelper("apps/", app1, "");
        stop();
    }

    @Test
    public void testAppsXML() throws Exception, JSONException {
        start();
        MockNM amNodeManager = TestRMWebServicesApps.rm.registerNode("127.0.0.1:1234", 2048);
        RMApp app1 = TestRMWebServicesApps.rm.submitApp(TestRMWebServicesApps.CONTAINER_MB, "testwordcount", "user1");
        amNodeManager.nodeHeartbeat(true);
        WebResource r = resource();
        ClientResponse response = r.path("ws").path("v1").path("cluster").path("apps").accept(APPLICATION_XML).get(ClientResponse.class);
        Assert.assertEquals((((MediaType.APPLICATION_XML_TYPE) + "; ") + (JettyUtils.UTF_8)), response.getType().toString());
        String xml = response.getEntity(String.class);
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        InputSource is = new InputSource();
        is.setCharacterStream(new StringReader(xml));
        Document dom = db.parse(is);
        NodeList nodesApps = dom.getElementsByTagName("apps");
        Assert.assertEquals("incorrect number of elements", 1, nodesApps.getLength());
        NodeList nodes = dom.getElementsByTagName("app");
        Assert.assertEquals("incorrect number of elements", 1, nodes.getLength());
        verifyAppsXML(nodes, app1, false);
        stop();
    }

    @Test
    public void testRunningApp() throws Exception, JSONException {
        start();
        MockNM amNodeManager = TestRMWebServicesApps.rm.registerNode("127.0.0.1:1234", 2048);
        RMApp app1 = TestRMWebServicesApps.rm.submitApp(TestRMWebServicesApps.CONTAINER_MB, "testwordcount", "user1");
        MockAM am1 = MockRM.launchAndRegisterAM(app1, TestRMWebServicesApps.rm, amNodeManager);
        am1.allocate("*", 4096, 1, new ArrayList<>());
        amNodeManager.nodeHeartbeat(true);
        WebResource r = resource();
        ClientResponse response = r.path("ws").path("v1").path("cluster").path("apps").accept(APPLICATION_XML).get(ClientResponse.class);
        Assert.assertEquals((((MediaType.APPLICATION_XML_TYPE) + "; ") + (JettyUtils.UTF_8)), response.getType().toString());
        String xml = response.getEntity(String.class);
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        InputSource is = new InputSource();
        is.setCharacterStream(new StringReader(xml));
        Document dom = db.parse(is);
        NodeList nodesApps = dom.getElementsByTagName("apps");
        Assert.assertEquals("incorrect number of elements", 1, nodesApps.getLength());
        NodeList nodes = dom.getElementsByTagName("app");
        Assert.assertEquals("incorrect number of elements", 1, nodes.getLength());
        verifyAppsXML(nodes, app1, true);
        testAppsHelper("apps/", app1, APPLICATION_JSON, true);
        stop();
    }

    @Test
    public void testAppsXMLMulti() throws Exception, JSONException {
        start();
        MockNM amNodeManager = TestRMWebServicesApps.rm.registerNode("127.0.0.1:1234", 2048);
        TestRMWebServicesApps.rm.submitApp(TestRMWebServicesApps.CONTAINER_MB, "testwordcount", "user1");
        TestRMWebServicesApps.rm.submitApp(2048, "testwordcount2", "user1");
        amNodeManager.nodeHeartbeat(true);
        WebResource r = resource();
        ClientResponse response = r.path("ws").path("v1").path("cluster").path("apps").accept(APPLICATION_XML).get(ClientResponse.class);
        Assert.assertEquals((((MediaType.APPLICATION_XML_TYPE) + "; ") + (JettyUtils.UTF_8)), response.getType().toString());
        String xml = response.getEntity(String.class);
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        InputSource is = new InputSource();
        is.setCharacterStream(new StringReader(xml));
        Document dom = db.parse(is);
        NodeList nodesApps = dom.getElementsByTagName("apps");
        Assert.assertEquals("incorrect number of elements", 1, nodesApps.getLength());
        NodeList nodes = dom.getElementsByTagName("app");
        Assert.assertEquals("incorrect number of elements", 2, nodes.getLength());
        stop();
    }

    @Test
    public void testAppsQueryState() throws Exception, JSONException {
        start();
        MockNM amNodeManager = TestRMWebServicesApps.rm.registerNode("127.0.0.1:1234", 2048);
        RMApp app1 = TestRMWebServicesApps.rm.submitApp(TestRMWebServicesApps.CONTAINER_MB);
        amNodeManager.nodeHeartbeat(true);
        WebResource r = resource();
        ClientResponse response = r.path("ws").path("v1").path("cluster").path("apps").queryParam("state", ACCEPTED.toString()).accept(APPLICATION_JSON).get(ClientResponse.class);
        Assert.assertEquals((((MediaType.APPLICATION_JSON_TYPE) + "; ") + (JettyUtils.UTF_8)), response.getType().toString());
        JSONObject json = response.getEntity(JSONObject.class);
        Assert.assertEquals("incorrect number of elements", 1, json.length());
        JSONObject apps = json.getJSONObject("apps");
        Assert.assertEquals("incorrect number of elements", 1, apps.length());
        JSONArray array = apps.getJSONArray("app");
        Assert.assertEquals("incorrect number of elements", 1, array.length());
        verifyAppInfo(array.getJSONObject(0), app1, false);
        stop();
    }

    @Test
    public void testAppsQueryStates() throws Exception, JSONException {
        start();
        MockNM amNodeManager = TestRMWebServicesApps.rm.registerNode("127.0.0.1:1234", 2048);
        TestRMWebServicesApps.rm.submitApp(TestRMWebServicesApps.CONTAINER_MB);
        RMApp killedApp = TestRMWebServicesApps.rm.submitApp(TestRMWebServicesApps.CONTAINER_MB);
        TestRMWebServicesApps.rm.killApp(killedApp.getApplicationId());
        amNodeManager.nodeHeartbeat(true);
        WebResource r = resource();
        MultivaluedMapImpl params = new MultivaluedMapImpl();
        params.add("states", ACCEPTED.toString());
        ClientResponse response = r.path("ws").path("v1").path("cluster").path("apps").queryParams(params).accept(APPLICATION_JSON).get(ClientResponse.class);
        Assert.assertEquals((((MediaType.APPLICATION_JSON_TYPE) + "; ") + (JettyUtils.UTF_8)), response.getType().toString());
        JSONObject json = response.getEntity(JSONObject.class);
        Assert.assertEquals("incorrect number of elements", 1, json.length());
        JSONObject apps = json.getJSONObject("apps");
        Assert.assertEquals("incorrect number of elements", 1, apps.length());
        JSONArray array = apps.getJSONArray("app");
        Assert.assertEquals("incorrect number of elements", 1, array.length());
        Assert.assertEquals("state not equal to ACCEPTED", "ACCEPTED", array.getJSONObject(0).getString("state"));
        r = resource();
        params = new MultivaluedMapImpl();
        params.add("states", ACCEPTED.toString());
        params.add("states", KILLED.toString());
        response = r.path("ws").path("v1").path("cluster").path("apps").queryParams(params).accept(APPLICATION_JSON).get(ClientResponse.class);
        Assert.assertEquals((((MediaType.APPLICATION_JSON_TYPE) + "; ") + (JettyUtils.UTF_8)), response.getType().toString());
        json = response.getEntity(JSONObject.class);
        Assert.assertEquals("incorrect number of elements", 1, json.length());
        apps = json.getJSONObject("apps");
        Assert.assertEquals("incorrect number of elements", 1, apps.length());
        array = apps.getJSONArray("app");
        Assert.assertEquals("incorrect number of elements", 2, array.length());
        Assert.assertTrue("both app states of ACCEPTED and KILLED are not present", (((array.getJSONObject(0).getString("state").equals("ACCEPTED")) && (array.getJSONObject(1).getString("state").equals("KILLED"))) || ((array.getJSONObject(0).getString("state").equals("KILLED")) && (array.getJSONObject(1).getString("state").equals("ACCEPTED")))));
        stop();
    }

    @Test
    public void testAppsQueryStatesComma() throws Exception, JSONException {
        start();
        MockNM amNodeManager = TestRMWebServicesApps.rm.registerNode("127.0.0.1:1234", 2048);
        TestRMWebServicesApps.rm.submitApp(TestRMWebServicesApps.CONTAINER_MB);
        RMApp killedApp = TestRMWebServicesApps.rm.submitApp(TestRMWebServicesApps.CONTAINER_MB);
        TestRMWebServicesApps.rm.killApp(killedApp.getApplicationId());
        amNodeManager.nodeHeartbeat(true);
        WebResource r = resource();
        MultivaluedMapImpl params = new MultivaluedMapImpl();
        params.add("states", ACCEPTED.toString());
        ClientResponse response = r.path("ws").path("v1").path("cluster").path("apps").queryParams(params).accept(APPLICATION_JSON).get(ClientResponse.class);
        Assert.assertEquals((((MediaType.APPLICATION_JSON_TYPE) + "; ") + (JettyUtils.UTF_8)), response.getType().toString());
        JSONObject json = response.getEntity(JSONObject.class);
        Assert.assertEquals("incorrect number of elements", 1, json.length());
        JSONObject apps = json.getJSONObject("apps");
        Assert.assertEquals("incorrect number of elements", 1, apps.length());
        JSONArray array = apps.getJSONArray("app");
        Assert.assertEquals("incorrect number of elements", 1, array.length());
        Assert.assertEquals("state not equal to ACCEPTED", "ACCEPTED", array.getJSONObject(0).getString("state"));
        r = resource();
        params = new MultivaluedMapImpl();
        params.add("states", (((ACCEPTED.toString()) + ",") + (KILLED.toString())));
        response = r.path("ws").path("v1").path("cluster").path("apps").queryParams(params).accept(APPLICATION_JSON).get(ClientResponse.class);
        Assert.assertEquals((((MediaType.APPLICATION_JSON_TYPE) + "; ") + (JettyUtils.UTF_8)), response.getType().toString());
        json = response.getEntity(JSONObject.class);
        Assert.assertEquals("incorrect number of elements", 1, json.length());
        apps = json.getJSONObject("apps");
        Assert.assertEquals("incorrect number of elements", 1, apps.length());
        array = apps.getJSONArray("app");
        Assert.assertEquals("incorrect number of elements", 2, array.length());
        Assert.assertTrue("both app states of ACCEPTED and KILLED are not present", (((array.getJSONObject(0).getString("state").equals("ACCEPTED")) && (array.getJSONObject(1).getString("state").equals("KILLED"))) || ((array.getJSONObject(0).getString("state").equals("KILLED")) && (array.getJSONObject(1).getString("state").equals("ACCEPTED")))));
        stop();
    }

    @Test
    public void testAppsQueryStatesNone() throws Exception, JSONException {
        start();
        MockNM amNodeManager = TestRMWebServicesApps.rm.registerNode("127.0.0.1:1234", 2048);
        TestRMWebServicesApps.rm.submitApp(TestRMWebServicesApps.CONTAINER_MB);
        amNodeManager.nodeHeartbeat(true);
        WebResource r = resource();
        ClientResponse response = r.path("ws").path("v1").path("cluster").path("apps").queryParam("states", RUNNING.toString()).accept(APPLICATION_JSON).get(ClientResponse.class);
        Assert.assertEquals((((MediaType.APPLICATION_JSON_TYPE) + "; ") + (JettyUtils.UTF_8)), response.getType().toString());
        JSONObject json = response.getEntity(JSONObject.class);
        Assert.assertEquals("incorrect number of elements", 1, json.length());
        Assert.assertEquals("apps is not empty", new JSONObject().toString(), json.get("apps").toString());
        stop();
    }

    @Test
    public void testAppsQueryStateNone() throws Exception, JSONException {
        start();
        MockNM amNodeManager = TestRMWebServicesApps.rm.registerNode("127.0.0.1:1234", 2048);
        TestRMWebServicesApps.rm.submitApp(TestRMWebServicesApps.CONTAINER_MB);
        amNodeManager.nodeHeartbeat(true);
        WebResource r = resource();
        ClientResponse response = r.path("ws").path("v1").path("cluster").path("apps").queryParam("state", RUNNING.toString()).accept(APPLICATION_JSON).get(ClientResponse.class);
        Assert.assertEquals((((MediaType.APPLICATION_JSON_TYPE) + "; ") + (JettyUtils.UTF_8)), response.getType().toString());
        JSONObject json = response.getEntity(JSONObject.class);
        Assert.assertEquals("incorrect number of elements", 1, json.length());
        Assert.assertEquals("apps is not empty", new JSONObject().toString(), json.get("apps").toString());
        stop();
    }

    @Test
    public void testAppsQueryStatesInvalid() throws Exception, JSONException {
        start();
        MockNM amNodeManager = TestRMWebServicesApps.rm.registerNode("127.0.0.1:1234", 2048);
        TestRMWebServicesApps.rm.submitApp(TestRMWebServicesApps.CONTAINER_MB);
        amNodeManager.nodeHeartbeat(true);
        WebResource r = resource();
        try {
            r.path("ws").path("v1").path("cluster").path("apps").queryParam("states", "INVALID_test").accept(APPLICATION_JSON).get(JSONObject.class);
            Assert.fail("should have thrown exception on invalid state query");
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
            WebServicesTestUtils.checkStringContains("exception message", "Invalid application-state INVALID_test", message);
            WebServicesTestUtils.checkStringMatch("exception type", "BadRequestException", type);
            WebServicesTestUtils.checkStringMatch("exception classname", "org.apache.hadoop.yarn.webapp.BadRequestException", classname);
        } finally {
            stop();
        }
    }

    @Test
    public void testAppsQueryStateInvalid() throws Exception, JSONException {
        start();
        MockNM amNodeManager = TestRMWebServicesApps.rm.registerNode("127.0.0.1:1234", 2048);
        TestRMWebServicesApps.rm.submitApp(TestRMWebServicesApps.CONTAINER_MB);
        amNodeManager.nodeHeartbeat(true);
        WebResource r = resource();
        try {
            r.path("ws").path("v1").path("cluster").path("apps").queryParam("state", "INVALID_test").accept(APPLICATION_JSON).get(JSONObject.class);
            Assert.fail("should have thrown exception on invalid state query");
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
            WebServicesTestUtils.checkStringContains("exception message", "Invalid application-state INVALID_test", message);
            WebServicesTestUtils.checkStringMatch("exception type", "BadRequestException", type);
            WebServicesTestUtils.checkStringMatch("exception classname", "org.apache.hadoop.yarn.webapp.BadRequestException", classname);
        } finally {
            stop();
        }
    }

    @Test
    public void testAppsQueryFinalStatus() throws Exception, JSONException {
        start();
        MockNM amNodeManager = TestRMWebServicesApps.rm.registerNode("127.0.0.1:1234", 2048);
        RMApp app1 = TestRMWebServicesApps.rm.submitApp(TestRMWebServicesApps.CONTAINER_MB);
        amNodeManager.nodeHeartbeat(true);
        WebResource r = resource();
        ClientResponse response = r.path("ws").path("v1").path("cluster").path("apps").queryParam("finalStatus", UNDEFINED.toString()).accept(APPLICATION_JSON).get(ClientResponse.class);
        Assert.assertEquals((((MediaType.APPLICATION_JSON_TYPE) + "; ") + (JettyUtils.UTF_8)), response.getType().toString());
        JSONObject json = response.getEntity(JSONObject.class);
        Assert.assertEquals("incorrect number of elements", 1, json.length());
        System.out.println(json.toString());
        JSONObject apps = json.getJSONObject("apps");
        Assert.assertEquals("incorrect number of elements", 1, apps.length());
        JSONArray array = apps.getJSONArray("app");
        Assert.assertEquals("incorrect number of elements", 1, array.length());
        verifyAppInfo(array.getJSONObject(0), app1, false);
        stop();
    }

    @Test
    public void testAppsQueryFinalStatusNone() throws Exception, JSONException {
        start();
        MockNM amNodeManager = TestRMWebServicesApps.rm.registerNode("127.0.0.1:1234", 2048);
        TestRMWebServicesApps.rm.submitApp(TestRMWebServicesApps.CONTAINER_MB);
        amNodeManager.nodeHeartbeat(true);
        WebResource r = resource();
        ClientResponse response = r.path("ws").path("v1").path("cluster").path("apps").queryParam("finalStatus", FinalApplicationStatus.KILLED.toString()).accept(APPLICATION_JSON).get(ClientResponse.class);
        Assert.assertEquals((((MediaType.APPLICATION_JSON_TYPE) + "; ") + (JettyUtils.UTF_8)), response.getType().toString());
        JSONObject json = response.getEntity(JSONObject.class);
        Assert.assertEquals("incorrect number of elements", 1, json.length());
        Assert.assertEquals("apps is not null", new JSONObject().toString(), json.get("apps").toString());
        stop();
    }

    @Test
    public void testAppsQueryFinalStatusInvalid() throws Exception, JSONException {
        start();
        MockNM amNodeManager = TestRMWebServicesApps.rm.registerNode("127.0.0.1:1234", 2048);
        TestRMWebServicesApps.rm.submitApp(TestRMWebServicesApps.CONTAINER_MB);
        amNodeManager.nodeHeartbeat(true);
        WebResource r = resource();
        try {
            r.path("ws").path("v1").path("cluster").path("apps").queryParam("finalStatus", "INVALID_test").accept(APPLICATION_JSON).get(JSONObject.class);
            Assert.fail("should have thrown exception on invalid state query");
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
            WebServicesTestUtils.checkStringContains("exception message", "org.apache.hadoop.yarn.api.records.FinalApplicationStatus.INVALID_test", message);
            WebServicesTestUtils.checkStringMatch("exception type", "IllegalArgumentException", type);
            WebServicesTestUtils.checkStringMatch("exception classname", "java.lang.IllegalArgumentException", classname);
        } finally {
            stop();
        }
    }

    @Test
    public void testAppsQueryUser() throws Exception, JSONException {
        start();
        MockNM amNodeManager = TestRMWebServicesApps.rm.registerNode("127.0.0.1:1234", 2048);
        TestRMWebServicesApps.rm.submitApp(TestRMWebServicesApps.CONTAINER_MB);
        TestRMWebServicesApps.rm.submitApp(TestRMWebServicesApps.CONTAINER_MB);
        amNodeManager.nodeHeartbeat(true);
        WebResource r = resource();
        ClientResponse response = r.path("ws").path("v1").path("cluster").path("apps").queryParam("user", UserGroupInformation.getCurrentUser().getShortUserName()).accept(APPLICATION_JSON).get(ClientResponse.class);
        Assert.assertEquals((((MediaType.APPLICATION_JSON_TYPE) + "; ") + (JettyUtils.UTF_8)), response.getType().toString());
        JSONObject json = response.getEntity(JSONObject.class);
        Assert.assertEquals("incorrect number of elements", 1, json.length());
        JSONObject apps = json.getJSONObject("apps");
        Assert.assertEquals("incorrect number of elements", 1, apps.length());
        JSONArray array = apps.getJSONArray("app");
        Assert.assertEquals("incorrect number of elements", 2, array.length());
        stop();
    }

    @Test
    public void testAppsQueryQueue() throws Exception, JSONException {
        start();
        MockNM amNodeManager = TestRMWebServicesApps.rm.registerNode("127.0.0.1:1234", 2048);
        TestRMWebServicesApps.rm.submitApp(TestRMWebServicesApps.CONTAINER_MB);
        TestRMWebServicesApps.rm.submitApp(TestRMWebServicesApps.CONTAINER_MB);
        amNodeManager.nodeHeartbeat(true);
        WebResource r = resource();
        ClientResponse response = r.path("ws").path("v1").path("cluster").path("apps").queryParam("queue", "default").accept(APPLICATION_JSON).get(ClientResponse.class);
        Assert.assertEquals((((MediaType.APPLICATION_JSON_TYPE) + "; ") + (JettyUtils.UTF_8)), response.getType().toString());
        JSONObject json = response.getEntity(JSONObject.class);
        Assert.assertEquals("incorrect number of elements", 1, json.length());
        JSONObject apps = json.getJSONObject("apps");
        Assert.assertEquals("incorrect number of elements", 1, apps.length());
        JSONArray array = apps.getJSONArray("app");
        Assert.assertEquals("incorrect number of elements", 2, array.length());
        stop();
    }

    @Test
    public void testAppsQueryQueueAndStateTwoFinishedApps() throws Exception {
        start();
        MockNM amNodeManager = TestRMWebServicesApps.rm.registerNode("127.0.0.1:1234", 2048);
        RMApp app1 = TestRMWebServicesApps.rm.submitApp(TestRMWebServicesApps.CONTAINER_MB);
        RMApp app2 = TestRMWebServicesApps.rm.submitApp(TestRMWebServicesApps.CONTAINER_MB);
        amNodeManager.nodeHeartbeat(true);
        finishApp(amNodeManager, app1);
        finishApp(amNodeManager, app2);
        WebResource r = resource();
        ClientResponse response = r.path("ws").path("v1").path("cluster").path("apps").queryParam("queue", "default").queryParam("state", FINISHED.toString()).accept(APPLICATION_JSON).get(ClientResponse.class);
        Assert.assertEquals((((MediaType.APPLICATION_JSON_TYPE) + "; ") + (JettyUtils.UTF_8)), response.getType().toString());
        JSONObject json = response.getEntity(JSONObject.class);
        Assert.assertEquals("incorrect number of elements", 1, json.length());
        JSONObject apps = json.getJSONObject("apps");
        Assert.assertEquals("incorrect number of elements", 1, apps.length());
        JSONArray array = apps.getJSONArray("app");
        Set<String> appIds = getApplicationIds(array);
        Assert.assertTrue("Finished app1 should be in the result list!", appIds.contains(app1.getApplicationId().toString()));
        Assert.assertTrue("Finished app2 should be in the result list!", appIds.contains(app2.getApplicationId().toString()));
        Assert.assertEquals("incorrect number of elements", 2, array.length());
        stop();
    }

    @Test
    public void testAppsQueryQueueAndStateOneFinishedApp() throws Exception {
        start();
        MockNM amNodeManager = TestRMWebServicesApps.rm.registerNode("127.0.0.1:1234", 2048);
        RMApp finishedApp = TestRMWebServicesApps.rm.submitApp(TestRMWebServicesApps.CONTAINER_MB);
        RMApp runningApp = TestRMWebServicesApps.rm.submitApp(TestRMWebServicesApps.CONTAINER_MB);
        amNodeManager.nodeHeartbeat(true);
        finishApp(amNodeManager, finishedApp);
        WebResource r = resource();
        ClientResponse response = r.path("ws").path("v1").path("cluster").path("apps").queryParam("queue", "default").queryParam("state", FINISHED.toString()).accept(APPLICATION_JSON).get(ClientResponse.class);
        Assert.assertEquals((((MediaType.APPLICATION_JSON_TYPE) + "; ") + (JettyUtils.UTF_8)), response.getType().toString());
        JSONObject json = response.getEntity(JSONObject.class);
        Assert.assertEquals("incorrect number of elements", 1, json.length());
        JSONObject apps = json.getJSONObject("apps");
        Assert.assertEquals("incorrect number of elements", 1, apps.length());
        JSONArray array = apps.getJSONArray("app");
        Set<String> appIds = getApplicationIds(array);
        Assert.assertFalse("Running app should not be in the result list!", appIds.contains(runningApp.getApplicationId().toString()));
        Assert.assertTrue("Finished app should be in the result list!", appIds.contains(finishedApp.getApplicationId().toString()));
        Assert.assertEquals("incorrect number of elements", 1, array.length());
        stop();
    }

    @Test
    public void testAppsQueryQueueOneFinishedApp() throws Exception {
        start();
        MockNM amNodeManager = TestRMWebServicesApps.rm.registerNode("127.0.0.1:1234", 2048);
        RMApp finishedApp = TestRMWebServicesApps.rm.submitApp(TestRMWebServicesApps.CONTAINER_MB);
        RMApp runningApp = TestRMWebServicesApps.rm.submitApp(TestRMWebServicesApps.CONTAINER_MB);
        amNodeManager.nodeHeartbeat(true);
        finishApp(amNodeManager, finishedApp);
        WebResource r = resource();
        ClientResponse response = r.path("ws").path("v1").path("cluster").path("apps").queryParam("queue", "default").accept(APPLICATION_JSON).get(ClientResponse.class);
        Assert.assertEquals((((MediaType.APPLICATION_JSON_TYPE) + "; ") + (JettyUtils.UTF_8)), response.getType().toString());
        JSONObject json = response.getEntity(JSONObject.class);
        Assert.assertEquals("incorrect number of elements", 1, json.length());
        JSONObject apps = json.getJSONObject("apps");
        Assert.assertEquals("incorrect number of elements", 1, apps.length());
        JSONArray array = apps.getJSONArray("app");
        Set<String> appIds = getApplicationIds(array);
        Assert.assertTrue("Running app should be in the result list!", appIds.contains(runningApp.getApplicationId().toString()));
        Assert.assertTrue("Finished app should be in the result list!", appIds.contains(finishedApp.getApplicationId().toString()));
        Assert.assertEquals("incorrect number of elements", 2, array.length());
        stop();
    }

    @Test
    public void testAppsQueryLimit() throws Exception, JSONException {
        start();
        TestRMWebServicesApps.rm.registerNode("127.0.0.1:1234", 2048);
        TestRMWebServicesApps.rm.submitApp(TestRMWebServicesApps.CONTAINER_MB);
        TestRMWebServicesApps.rm.submitApp(TestRMWebServicesApps.CONTAINER_MB);
        TestRMWebServicesApps.rm.submitApp(TestRMWebServicesApps.CONTAINER_MB);
        WebResource r = resource();
        ClientResponse response = r.path("ws").path("v1").path("cluster").path("apps").queryParam("limit", "2").accept(APPLICATION_JSON).get(ClientResponse.class);
        Assert.assertEquals((((MediaType.APPLICATION_JSON_TYPE) + "; ") + (JettyUtils.UTF_8)), response.getType().toString());
        JSONObject json = response.getEntity(JSONObject.class);
        Assert.assertEquals("incorrect number of elements", 1, json.length());
        JSONObject apps = json.getJSONObject("apps");
        Assert.assertEquals("incorrect number of elements", 1, apps.length());
        JSONArray array = apps.getJSONArray("app");
        Assert.assertEquals("incorrect number of elements", 2, array.length());
        stop();
    }

    @Test
    public void testAppsQueryStartBegin() throws Exception, JSONException {
        start();
        long start = System.currentTimeMillis();
        Thread.sleep(1);
        TestRMWebServicesApps.rm.registerNode("127.0.0.1:1234", 2048);
        TestRMWebServicesApps.rm.submitApp(TestRMWebServicesApps.CONTAINER_MB);
        TestRMWebServicesApps.rm.submitApp(TestRMWebServicesApps.CONTAINER_MB);
        TestRMWebServicesApps.rm.submitApp(TestRMWebServicesApps.CONTAINER_MB);
        WebResource r = resource();
        ClientResponse response = r.path("ws").path("v1").path("cluster").path("apps").queryParam("startedTimeBegin", String.valueOf(start)).accept(APPLICATION_JSON).get(ClientResponse.class);
        Assert.assertEquals((((MediaType.APPLICATION_JSON_TYPE) + "; ") + (JettyUtils.UTF_8)), response.getType().toString());
        JSONObject json = response.getEntity(JSONObject.class);
        Assert.assertEquals("incorrect number of elements", 1, json.length());
        JSONObject apps = json.getJSONObject("apps");
        Assert.assertEquals("incorrect number of elements", 1, apps.length());
        JSONArray array = apps.getJSONArray("app");
        Assert.assertEquals("incorrect number of elements", 3, array.length());
        stop();
    }

    @Test
    public void testAppsQueryStartBeginSome() throws Exception, JSONException {
        start();
        TestRMWebServicesApps.rm.registerNode("127.0.0.1:1234", 2048);
        TestRMWebServicesApps.rm.submitApp(TestRMWebServicesApps.CONTAINER_MB);
        TestRMWebServicesApps.rm.submitApp(TestRMWebServicesApps.CONTAINER_MB);
        long start = System.currentTimeMillis();
        Thread.sleep(1);
        TestRMWebServicesApps.rm.submitApp(TestRMWebServicesApps.CONTAINER_MB);
        WebResource r = resource();
        ClientResponse response = r.path("ws").path("v1").path("cluster").path("apps").queryParam("startedTimeBegin", String.valueOf(start)).accept(APPLICATION_JSON).get(ClientResponse.class);
        Assert.assertEquals((((MediaType.APPLICATION_JSON_TYPE) + "; ") + (JettyUtils.UTF_8)), response.getType().toString());
        JSONObject json = response.getEntity(JSONObject.class);
        Assert.assertEquals("incorrect number of elements", 1, json.length());
        JSONObject apps = json.getJSONObject("apps");
        Assert.assertEquals("incorrect number of elements", 1, apps.length());
        JSONArray array = apps.getJSONArray("app");
        Assert.assertEquals("incorrect number of elements", 1, array.length());
        stop();
    }

    @Test
    public void testAppsQueryStartEnd() throws Exception, JSONException {
        start();
        TestRMWebServicesApps.rm.registerNode("127.0.0.1:1234", 2048);
        long end = System.currentTimeMillis();
        Thread.sleep(1);
        TestRMWebServicesApps.rm.submitApp(TestRMWebServicesApps.CONTAINER_MB);
        TestRMWebServicesApps.rm.submitApp(TestRMWebServicesApps.CONTAINER_MB);
        TestRMWebServicesApps.rm.submitApp(TestRMWebServicesApps.CONTAINER_MB);
        WebResource r = resource();
        ClientResponse response = r.path("ws").path("v1").path("cluster").path("apps").queryParam("startedTimeEnd", String.valueOf(end)).accept(APPLICATION_JSON).get(ClientResponse.class);
        Assert.assertEquals((((MediaType.APPLICATION_JSON_TYPE) + "; ") + (JettyUtils.UTF_8)), response.getType().toString());
        JSONObject json = response.getEntity(JSONObject.class);
        Assert.assertEquals("incorrect number of elements", 1, json.length());
        Assert.assertEquals("apps is not empty", new JSONObject().toString(), json.get("apps").toString());
        stop();
    }

    @Test
    public void testAppsQueryStartBeginEnd() throws Exception, JSONException {
        start();
        TestRMWebServicesApps.rm.registerNode("127.0.0.1:1234", 2048);
        long start = System.currentTimeMillis();
        Thread.sleep(1);
        TestRMWebServicesApps.rm.submitApp(TestRMWebServicesApps.CONTAINER_MB);
        TestRMWebServicesApps.rm.submitApp(TestRMWebServicesApps.CONTAINER_MB);
        long end = System.currentTimeMillis();
        Thread.sleep(1);
        TestRMWebServicesApps.rm.submitApp(TestRMWebServicesApps.CONTAINER_MB);
        WebResource r = resource();
        ClientResponse response = r.path("ws").path("v1").path("cluster").path("apps").queryParam("startedTimeBegin", String.valueOf(start)).queryParam("startedTimeEnd", String.valueOf(end)).accept(APPLICATION_JSON).get(ClientResponse.class);
        Assert.assertEquals((((MediaType.APPLICATION_JSON_TYPE) + "; ") + (JettyUtils.UTF_8)), response.getType().toString());
        JSONObject json = response.getEntity(JSONObject.class);
        Assert.assertEquals("incorrect number of elements", 1, json.length());
        JSONObject apps = json.getJSONObject("apps");
        Assert.assertEquals("incorrect number of elements", 1, apps.length());
        JSONArray array = apps.getJSONArray("app");
        Assert.assertEquals("incorrect number of elements", 2, array.length());
        stop();
    }

    @Test
    public void testAppsQueryFinishBegin() throws Exception, JSONException {
        start();
        MockNM amNodeManager = TestRMWebServicesApps.rm.registerNode("127.0.0.1:1234", 2048);
        long start = System.currentTimeMillis();
        Thread.sleep(1);
        RMApp app1 = TestRMWebServicesApps.rm.submitApp(TestRMWebServicesApps.CONTAINER_MB);
        amNodeManager.nodeHeartbeat(true);
        finishApp(amNodeManager, app1);
        TestRMWebServicesApps.rm.submitApp(TestRMWebServicesApps.CONTAINER_MB);
        TestRMWebServicesApps.rm.submitApp(TestRMWebServicesApps.CONTAINER_MB);
        WebResource r = resource();
        ClientResponse response = r.path("ws").path("v1").path("cluster").path("apps").queryParam("finishedTimeBegin", String.valueOf(start)).accept(APPLICATION_JSON).get(ClientResponse.class);
        Assert.assertEquals((((MediaType.APPLICATION_JSON_TYPE) + "; ") + (JettyUtils.UTF_8)), response.getType().toString());
        JSONObject json = response.getEntity(JSONObject.class);
        Assert.assertEquals("incorrect number of elements", 1, json.length());
        JSONObject apps = json.getJSONObject("apps");
        Assert.assertEquals("incorrect number of elements", 1, apps.length());
        JSONArray array = apps.getJSONArray("app");
        Assert.assertEquals("incorrect number of elements", 1, array.length());
        stop();
    }

    @Test
    public void testAppsQueryFinishEnd() throws Exception, JSONException {
        start();
        MockNM amNodeManager = TestRMWebServicesApps.rm.registerNode("127.0.0.1:1234", 2048);
        RMApp app1 = TestRMWebServicesApps.rm.submitApp(TestRMWebServicesApps.CONTAINER_MB);
        amNodeManager.nodeHeartbeat(true);
        // finish App
        finishApp(amNodeManager, app1);
        TestRMWebServicesApps.rm.submitApp(TestRMWebServicesApps.CONTAINER_MB);
        TestRMWebServicesApps.rm.submitApp(TestRMWebServicesApps.CONTAINER_MB);
        long end = System.currentTimeMillis();
        WebResource r = resource();
        ClientResponse response = r.path("ws").path("v1").path("cluster").path("apps").queryParam("finishedTimeEnd", String.valueOf(end)).accept(APPLICATION_JSON).get(ClientResponse.class);
        Assert.assertEquals((((MediaType.APPLICATION_JSON_TYPE) + "; ") + (JettyUtils.UTF_8)), response.getType().toString());
        JSONObject json = response.getEntity(JSONObject.class);
        Assert.assertEquals("incorrect number of elements", 1, json.length());
        JSONObject apps = json.getJSONObject("apps");
        Assert.assertEquals("incorrect number of elements", 1, apps.length());
        JSONArray array = apps.getJSONArray("app");
        Assert.assertEquals("incorrect number of elements", 3, array.length());
        stop();
    }

    @Test
    public void testAppsQueryFinishBeginEnd() throws Exception, JSONException {
        start();
        MockNM amNodeManager = TestRMWebServicesApps.rm.registerNode("127.0.0.1:1234", 2048);
        long start = System.currentTimeMillis();
        Thread.sleep(1);
        RMApp app1 = TestRMWebServicesApps.rm.submitApp(TestRMWebServicesApps.CONTAINER_MB);
        amNodeManager.nodeHeartbeat(true);
        // finish App
        finishApp(amNodeManager, app1);
        TestRMWebServicesApps.rm.submitApp(TestRMWebServicesApps.CONTAINER_MB);
        TestRMWebServicesApps.rm.submitApp(TestRMWebServicesApps.CONTAINER_MB);
        long end = System.currentTimeMillis();
        WebResource r = resource();
        ClientResponse response = r.path("ws").path("v1").path("cluster").path("apps").queryParam("finishedTimeBegin", String.valueOf(start)).queryParam("finishedTimeEnd", String.valueOf(end)).accept(APPLICATION_JSON).get(ClientResponse.class);
        Assert.assertEquals((((MediaType.APPLICATION_JSON_TYPE) + "; ") + (JettyUtils.UTF_8)), response.getType().toString());
        JSONObject json = response.getEntity(JSONObject.class);
        Assert.assertEquals("incorrect number of elements", 1, json.length());
        JSONObject apps = json.getJSONObject("apps");
        Assert.assertEquals("incorrect number of elements", 1, apps.length());
        JSONArray array = apps.getJSONArray("app");
        Assert.assertEquals("incorrect number of elements", 1, array.length());
        stop();
    }

    @Test
    public void testAppsQueryAppTypes() throws Exception, JSONException {
        start();
        MockNM amNodeManager = TestRMWebServicesApps.rm.registerNode("127.0.0.1:1234", 2048);
        Thread.sleep(1);
        RMApp app1 = TestRMWebServicesApps.rm.submitApp(TestRMWebServicesApps.CONTAINER_MB);
        amNodeManager.nodeHeartbeat(true);
        // finish App
        finishApp(amNodeManager, app1);
        TestRMWebServicesApps.rm.submitApp(TestRMWebServicesApps.CONTAINER_MB, "", UserGroupInformation.getCurrentUser().getShortUserName(), null, false, null, 2, null, "MAPREDUCE");
        TestRMWebServicesApps.rm.submitApp(TestRMWebServicesApps.CONTAINER_MB, "", UserGroupInformation.getCurrentUser().getShortUserName(), null, false, null, 2, null, "NON-YARN");
        WebResource r = resource();
        ClientResponse response = r.path("ws").path("v1").path("cluster").path("apps").queryParam("applicationTypes", "MAPREDUCE").accept(APPLICATION_JSON).get(ClientResponse.class);
        Assert.assertEquals((((MediaType.APPLICATION_JSON_TYPE) + "; ") + (JettyUtils.UTF_8)), response.getType().toString());
        JSONObject json = response.getEntity(JSONObject.class);
        Assert.assertEquals("incorrect number of elements", 1, json.length());
        JSONObject apps = json.getJSONObject("apps");
        Assert.assertEquals("incorrect number of elements", 1, apps.length());
        JSONArray array = apps.getJSONArray("app");
        Assert.assertEquals("incorrect number of elements", 1, array.length());
        Assert.assertEquals("MAPREDUCE", array.getJSONObject(0).getString("applicationType"));
        r = resource();
        response = r.path("ws").path("v1").path("cluster").path("apps").queryParam("applicationTypes", "YARN").queryParam("applicationTypes", "MAPREDUCE").accept(APPLICATION_JSON).get(ClientResponse.class);
        Assert.assertEquals((((MediaType.APPLICATION_JSON_TYPE) + "; ") + (JettyUtils.UTF_8)), response.getType().toString());
        json = response.getEntity(JSONObject.class);
        Assert.assertEquals("incorrect number of elements", 1, json.length());
        apps = json.getJSONObject("apps");
        Assert.assertEquals("incorrect number of elements", 1, apps.length());
        array = apps.getJSONArray("app");
        Assert.assertEquals("incorrect number of elements", 2, array.length());
        Assert.assertTrue((((array.getJSONObject(0).getString("applicationType").equals("YARN")) && (array.getJSONObject(1).getString("applicationType").equals("MAPREDUCE"))) || ((array.getJSONObject(1).getString("applicationType").equals("YARN")) && (array.getJSONObject(0).getString("applicationType").equals("MAPREDUCE")))));
        r = resource();
        response = r.path("ws").path("v1").path("cluster").path("apps").queryParam("applicationTypes", "YARN,NON-YARN").accept(APPLICATION_JSON).get(ClientResponse.class);
        Assert.assertEquals((((MediaType.APPLICATION_JSON_TYPE) + "; ") + (JettyUtils.UTF_8)), response.getType().toString());
        json = response.getEntity(JSONObject.class);
        Assert.assertEquals("incorrect number of elements", 1, json.length());
        apps = json.getJSONObject("apps");
        Assert.assertEquals("incorrect number of elements", 1, apps.length());
        array = apps.getJSONArray("app");
        Assert.assertEquals("incorrect number of elements", 2, array.length());
        Assert.assertTrue((((array.getJSONObject(0).getString("applicationType").equals("YARN")) && (array.getJSONObject(1).getString("applicationType").equals("NON-YARN"))) || ((array.getJSONObject(1).getString("applicationType").equals("YARN")) && (array.getJSONObject(0).getString("applicationType").equals("NON-YARN")))));
        r = resource();
        response = r.path("ws").path("v1").path("cluster").path("apps").queryParam("applicationTypes", "").accept(APPLICATION_JSON).get(ClientResponse.class);
        Assert.assertEquals((((MediaType.APPLICATION_JSON_TYPE) + "; ") + (JettyUtils.UTF_8)), response.getType().toString());
        json = response.getEntity(JSONObject.class);
        Assert.assertEquals("incorrect number of elements", 1, json.length());
        apps = json.getJSONObject("apps");
        Assert.assertEquals("incorrect number of elements", 1, apps.length());
        array = apps.getJSONArray("app");
        Assert.assertEquals("incorrect number of elements", 3, array.length());
        r = resource();
        response = r.path("ws").path("v1").path("cluster").path("apps").queryParam("applicationTypes", "YARN,NON-YARN").queryParam("applicationTypes", "MAPREDUCE").accept(APPLICATION_JSON).get(ClientResponse.class);
        Assert.assertEquals((((MediaType.APPLICATION_JSON_TYPE) + "; ") + (JettyUtils.UTF_8)), response.getType().toString());
        json = response.getEntity(JSONObject.class);
        Assert.assertEquals("incorrect number of elements", 1, json.length());
        apps = json.getJSONObject("apps");
        Assert.assertEquals("incorrect number of elements", 1, apps.length());
        array = apps.getJSONArray("app");
        Assert.assertEquals("incorrect number of elements", 3, array.length());
        r = resource();
        response = r.path("ws").path("v1").path("cluster").path("apps").queryParam("applicationTypes", "YARN").queryParam("applicationTypes", "").accept(APPLICATION_JSON).get(ClientResponse.class);
        Assert.assertEquals((((MediaType.APPLICATION_JSON_TYPE) + "; ") + (JettyUtils.UTF_8)), response.getType().toString());
        json = response.getEntity(JSONObject.class);
        Assert.assertEquals("incorrect number of elements", 1, json.length());
        apps = json.getJSONObject("apps");
        Assert.assertEquals("incorrect number of elements", 1, apps.length());
        array = apps.getJSONArray("app");
        Assert.assertEquals("incorrect number of elements", 1, array.length());
        Assert.assertEquals("YARN", array.getJSONObject(0).getString("applicationType"));
        r = resource();
        response = r.path("ws").path("v1").path("cluster").path("apps").queryParam("applicationTypes", ",,, ,, YARN ,, ,").accept(APPLICATION_JSON).get(ClientResponse.class);
        Assert.assertEquals((((MediaType.APPLICATION_JSON_TYPE) + "; ") + (JettyUtils.UTF_8)), response.getType().toString());
        json = response.getEntity(JSONObject.class);
        Assert.assertEquals("incorrect number of elements", 1, json.length());
        apps = json.getJSONObject("apps");
        Assert.assertEquals("incorrect number of elements", 1, apps.length());
        array = apps.getJSONArray("app");
        Assert.assertEquals("incorrect number of elements", 1, array.length());
        Assert.assertEquals("YARN", array.getJSONObject(0).getString("applicationType"));
        r = resource();
        response = r.path("ws").path("v1").path("cluster").path("apps").queryParam("applicationTypes", ",,, ,,  ,, ,").accept(APPLICATION_JSON).get(ClientResponse.class);
        Assert.assertEquals((((MediaType.APPLICATION_JSON_TYPE) + "; ") + (JettyUtils.UTF_8)), response.getType().toString());
        json = response.getEntity(JSONObject.class);
        Assert.assertEquals("incorrect number of elements", 1, json.length());
        apps = json.getJSONObject("apps");
        Assert.assertEquals("incorrect number of elements", 1, apps.length());
        array = apps.getJSONArray("app");
        Assert.assertEquals("incorrect number of elements", 3, array.length());
        r = resource();
        response = r.path("ws").path("v1").path("cluster").path("apps").queryParam("applicationTypes", "YARN, ,NON-YARN, ,,").accept(APPLICATION_JSON).get(ClientResponse.class);
        Assert.assertEquals((((MediaType.APPLICATION_JSON_TYPE) + "; ") + (JettyUtils.UTF_8)), response.getType().toString());
        json = response.getEntity(JSONObject.class);
        Assert.assertEquals("incorrect number of elements", 1, json.length());
        apps = json.getJSONObject("apps");
        Assert.assertEquals("incorrect number of elements", 1, apps.length());
        array = apps.getJSONArray("app");
        Assert.assertEquals("incorrect number of elements", 2, array.length());
        Assert.assertTrue((((array.getJSONObject(0).getString("applicationType").equals("YARN")) && (array.getJSONObject(1).getString("applicationType").equals("NON-YARN"))) || ((array.getJSONObject(1).getString("applicationType").equals("YARN")) && (array.getJSONObject(0).getString("applicationType").equals("NON-YARN")))));
        r = resource();
        response = r.path("ws").path("v1").path("cluster").path("apps").queryParam("applicationTypes", " YARN, ,  ,,,").queryParam("applicationTypes", "MAPREDUCE , ,, ,").accept(APPLICATION_JSON).get(ClientResponse.class);
        Assert.assertEquals((((MediaType.APPLICATION_JSON_TYPE) + "; ") + (JettyUtils.UTF_8)), response.getType().toString());
        json = response.getEntity(JSONObject.class);
        Assert.assertEquals("incorrect number of elements", 1, json.length());
        apps = json.getJSONObject("apps");
        Assert.assertEquals("incorrect number of elements", 1, apps.length());
        array = apps.getJSONArray("app");
        Assert.assertEquals("incorrect number of elements", 2, array.length());
        Assert.assertTrue((((array.getJSONObject(0).getString("applicationType").equals("YARN")) && (array.getJSONObject(1).getString("applicationType").equals("MAPREDUCE"))) || ((array.getJSONObject(1).getString("applicationType").equals("YARN")) && (array.getJSONObject(0).getString("applicationType").equals("MAPREDUCE")))));
        stop();
    }

    @Test
    public void testAppsQueryWithInvaildDeselects() throws Exception, JSONException {
        try {
            start();
            MockNM amNodeManager = TestRMWebServicesApps.rm.registerNode("127.0.0.1:1234", 2048);
            TestRMWebServicesApps.rm.submitApp(TestRMWebServicesApps.CONTAINER_MB);
            amNodeManager.nodeHeartbeat(true);
            WebResource r = resource();
            ClientResponse response = r.path("ws").path("v1").path("cluster").path("apps").queryParam("deSelects", "INVALIED_deSelectsParam").accept(APPLICATION_JSON).get(ClientResponse.class);
            WebServicesTestUtils.assertResponseStatusCode(BAD_REQUEST, response.getStatusInfo());
            Assert.assertEquals((((MediaType.APPLICATION_JSON_TYPE) + "; ") + (JettyUtils.UTF_8)), response.getType().toString());
            JSONObject msg = response.getEntity(JSONObject.class);
            JSONObject exception = msg.getJSONObject("RemoteException");
            Assert.assertEquals("incorrect number of elements", 3, exception.length());
            String message = exception.getString("message");
            String type = exception.getString("exception");
            String classname = exception.getString("javaClassName");
            WebServicesTestUtils.checkStringContains("exception message", ("java.lang.Exception: Invalid deSelects string" + (" INVALIED_deSelectsParam " + "specified. It should be one of")), message);
            WebServicesTestUtils.checkStringEqual("exception type", "BadRequestException", type);
            WebServicesTestUtils.checkStringEqual("exception classname", "org.apache.hadoop.yarn.webapp.BadRequestException", classname);
        } finally {
            stop();
        }
    }

    @Test
    public void testAppsQueryWithDeselects() throws Exception, JSONException {
        start();
        MockNM amNodeManager = TestRMWebServicesApps.rm.registerNode("127.0.0.1:1234", 2048);
        TestRMWebServicesApps.rm.submitApp(TestRMWebServicesApps.CONTAINER_MB);
        amNodeManager.nodeHeartbeat(true);
        WebResource r = resource();
        MultivaluedMapImpl params = new MultivaluedMapImpl();
        params.add("deSelects", RESOURCE_REQUESTS.toString());
        ClientResponse response = r.path("ws").path("v1").path("cluster").path("apps").queryParams(params).accept(APPLICATION_JSON).get(ClientResponse.class);
        Assert.assertEquals((((MediaType.APPLICATION_JSON_TYPE) + "; ") + (JettyUtils.UTF_8)), response.getType().toString());
        JSONObject json = response.getEntity(JSONObject.class);
        Assert.assertEquals("incorrect number of elements", 1, json.length());
        JSONObject apps = json.getJSONObject("apps");
        Assert.assertEquals("incorrect number of elements", 1, apps.length());
        JSONArray array = apps.getJSONArray("app");
        Assert.assertEquals("incorrect number of elements", 1, array.length());
        JSONObject app = array.getJSONObject(0);
        Assert.assertTrue("resource requests shouldn't exist", (!(app.has("resourceRequests"))));
        params.clear();
        params.add("deSelects", AM_NODE_LABEL_EXPRESSION.toString());
        response = r.path("ws").path("v1").path("cluster").path("apps").queryParams(params).accept(APPLICATION_JSON).get(ClientResponse.class);
        Assert.assertEquals((((MediaType.APPLICATION_JSON_TYPE) + "; ") + (JettyUtils.UTF_8)), response.getType().toString());
        json = response.getEntity(JSONObject.class);
        Assert.assertEquals("incorrect number of elements", 1, json.length());
        apps = json.getJSONObject("apps");
        Assert.assertEquals("incorrect number of elements", 1, apps.length());
        array = apps.getJSONArray("app");
        Assert.assertEquals("incorrect number of elements", 1, array.length());
        app = array.getJSONObject(0);
        Assert.assertTrue("AMNodeLabelExpression shouldn't exist", (!(app.has("amNodeLabelExpression"))));
        params.clear();
        params.add("deSelects", TIMEOUTS.toString());
        response = r.path("ws").path("v1").path("cluster").path("apps").queryParams(params).accept(APPLICATION_JSON).get(ClientResponse.class);
        Assert.assertEquals((((MediaType.APPLICATION_JSON_TYPE) + "; ") + (JettyUtils.UTF_8)), response.getType().toString());
        json = response.getEntity(JSONObject.class);
        Assert.assertEquals("incorrect number of elements", 1, json.length());
        apps = json.getJSONObject("apps");
        Assert.assertEquals("incorrect number of elements", 1, apps.length());
        array = apps.getJSONArray("app");
        Assert.assertEquals("incorrect number of elements", 1, array.length());
        app = array.getJSONObject(0);
        Assert.assertTrue("Timeouts shouldn't exist", (!(app.has("timeouts"))));
        stop();
        params.clear();
        params.add("deSelects", APP_NODE_LABEL_EXPRESSION.toString());
        response = r.path("ws").path("v1").path("cluster").path("apps").queryParams(params).accept(APPLICATION_JSON).get(ClientResponse.class);
        Assert.assertEquals((((MediaType.APPLICATION_JSON_TYPE) + "; ") + (JettyUtils.UTF_8)), response.getType().toString());
        json = response.getEntity(JSONObject.class);
        Assert.assertEquals("incorrect number of elements", 1, json.length());
        apps = json.getJSONObject("apps");
        Assert.assertEquals("incorrect number of elements", 1, apps.length());
        array = apps.getJSONArray("app");
        Assert.assertEquals("incorrect number of elements", 1, array.length());
        app = array.getJSONObject(0);
        Assert.assertTrue("AppNodeLabelExpression shouldn't exist", (!(app.has("appNodeLabelExpression"))));
        stop();
        params.clear();
        params.add("deSelects", RESOURCE_INFO.toString());
        response = r.path("ws").path("v1").path("cluster").path("apps").queryParams(params).accept(APPLICATION_JSON).get(ClientResponse.class);
        Assert.assertEquals((((MediaType.APPLICATION_JSON_TYPE) + "; ") + (JettyUtils.UTF_8)), response.getType().toString());
        json = response.getEntity(JSONObject.class);
        Assert.assertEquals("incorrect number of elements", 1, json.length());
        apps = json.getJSONObject("apps");
        Assert.assertEquals("incorrect number of elements", 1, apps.length());
        array = apps.getJSONArray("app");
        Assert.assertEquals("incorrect number of elements", 1, array.length());
        app = array.getJSONObject(0);
        Assert.assertTrue("Resource info shouldn't exist", (!(app.has("resourceInfo"))));
        stop();
    }

    @Test
    public void testAppStatistics() throws Exception, JSONException {
        try {
            start();
            MockNM amNodeManager = TestRMWebServicesApps.rm.registerNode("127.0.0.1:1234", 4096);
            Thread.sleep(1);
            RMApp app1 = TestRMWebServicesApps.rm.submitApp(TestRMWebServicesApps.CONTAINER_MB, "", UserGroupInformation.getCurrentUser().getShortUserName(), null, false, null, 2, null, "MAPREDUCE");
            amNodeManager.nodeHeartbeat(true);
            finishApp(amNodeManager, app1);
            TestRMWebServicesApps.rm.submitApp(TestRMWebServicesApps.CONTAINER_MB, "", UserGroupInformation.getCurrentUser().getShortUserName(), null, false, null, 2, null, "MAPREDUCE");
            TestRMWebServicesApps.rm.submitApp(TestRMWebServicesApps.CONTAINER_MB, "", UserGroupInformation.getCurrentUser().getShortUserName(), null, false, null, 2, null, "OTHER");
            // zero type, zero state
            WebResource r = resource();
            ClientResponse response = r.path("ws").path("v1").path("cluster").path("appstatistics").accept(APPLICATION_JSON).get(ClientResponse.class);
            Assert.assertEquals((((MediaType.APPLICATION_JSON_TYPE) + "; ") + (JettyUtils.UTF_8)), response.getType().toString());
            JSONObject json = response.getEntity(JSONObject.class);
            Assert.assertEquals("incorrect number of elements", 1, json.length());
            JSONObject appsStatInfo = json.getJSONObject("appStatInfo");
            Assert.assertEquals("incorrect number of elements", 1, appsStatInfo.length());
            JSONArray statItems = appsStatInfo.getJSONArray("statItem");
            Assert.assertEquals("incorrect number of elements", YarnApplicationState.values().length, statItems.length());
            for (int i = 0; i < (YarnApplicationState.values().length); ++i) {
                Assert.assertEquals("*", statItems.getJSONObject(0).getString("type"));
                if (statItems.getJSONObject(0).getString("state").equals("ACCEPTED")) {
                    Assert.assertEquals("2", statItems.getJSONObject(0).getString("count"));
                } else
                    if (statItems.getJSONObject(0).getString("state").equals("FINISHED")) {
                        Assert.assertEquals("1", statItems.getJSONObject(0).getString("count"));
                    } else {
                        Assert.assertEquals("0", statItems.getJSONObject(0).getString("count"));
                    }

            }
            // zero type, one state
            r = resource();
            response = r.path("ws").path("v1").path("cluster").path("appstatistics").queryParam("states", ACCEPTED.toString()).accept(APPLICATION_JSON).get(ClientResponse.class);
            Assert.assertEquals((((MediaType.APPLICATION_JSON_TYPE) + "; ") + (JettyUtils.UTF_8)), response.getType().toString());
            json = response.getEntity(JSONObject.class);
            Assert.assertEquals("incorrect number of elements", 1, json.length());
            appsStatInfo = json.getJSONObject("appStatInfo");
            Assert.assertEquals("incorrect number of elements", 1, appsStatInfo.length());
            statItems = appsStatInfo.getJSONArray("statItem");
            Assert.assertEquals("incorrect number of elements", 1, statItems.length());
            Assert.assertEquals("ACCEPTED", statItems.getJSONObject(0).getString("state"));
            Assert.assertEquals("*", statItems.getJSONObject(0).getString("type"));
            Assert.assertEquals("2", statItems.getJSONObject(0).getString("count"));
            // one type, zero state
            r = resource();
            response = r.path("ws").path("v1").path("cluster").path("appstatistics").queryParam("applicationTypes", "MAPREDUCE").accept(APPLICATION_JSON).get(ClientResponse.class);
            Assert.assertEquals((((MediaType.APPLICATION_JSON_TYPE) + "; ") + (JettyUtils.UTF_8)), response.getType().toString());
            json = response.getEntity(JSONObject.class);
            Assert.assertEquals("incorrect number of elements", 1, json.length());
            appsStatInfo = json.getJSONObject("appStatInfo");
            Assert.assertEquals("incorrect number of elements", 1, appsStatInfo.length());
            statItems = appsStatInfo.getJSONArray("statItem");
            Assert.assertEquals("incorrect number of elements", YarnApplicationState.values().length, statItems.length());
            for (int i = 0; i < (YarnApplicationState.values().length); ++i) {
                Assert.assertEquals("mapreduce", statItems.getJSONObject(0).getString("type"));
                if (statItems.getJSONObject(0).getString("state").equals("ACCEPTED")) {
                    Assert.assertEquals("1", statItems.getJSONObject(0).getString("count"));
                } else
                    if (statItems.getJSONObject(0).getString("state").equals("FINISHED")) {
                        Assert.assertEquals("1", statItems.getJSONObject(0).getString("count"));
                    } else {
                        Assert.assertEquals("0", statItems.getJSONObject(0).getString("count"));
                    }

            }
            // two types, zero state
            r = resource();
            response = r.path("ws").path("v1").path("cluster").path("appstatistics").queryParam("applicationTypes", "MAPREDUCE,OTHER").accept(APPLICATION_JSON).get(ClientResponse.class);
            WebServicesTestUtils.assertResponseStatusCode(BAD_REQUEST, response.getStatusInfo());
            Assert.assertEquals((((MediaType.APPLICATION_JSON_TYPE) + "; ") + (JettyUtils.UTF_8)), response.getType().toString());
            json = response.getEntity(JSONObject.class);
            Assert.assertEquals("incorrect number of elements", 1, json.length());
            JSONObject exception = json.getJSONObject("RemoteException");
            Assert.assertEquals("incorrect number of elements", 3, exception.length());
            String message = exception.getString("message");
            String type = exception.getString("exception");
            String className = exception.getString("javaClassName");
            WebServicesTestUtils.checkStringContains("exception message", "we temporarily support at most one applicationType", message);
            WebServicesTestUtils.checkStringEqual("exception type", "BadRequestException", type);
            WebServicesTestUtils.checkStringEqual("exception className", "org.apache.hadoop.yarn.webapp.BadRequestException", className);
            // one type, two states
            r = resource();
            response = r.path("ws").path("v1").path("cluster").path("appstatistics").queryParam("states", (((FINISHED.toString()) + ",") + (ACCEPTED.toString()))).queryParam("applicationTypes", "MAPREDUCE").accept(APPLICATION_JSON).get(ClientResponse.class);
            Assert.assertEquals((((MediaType.APPLICATION_JSON_TYPE) + "; ") + (JettyUtils.UTF_8)), response.getType().toString());
            json = response.getEntity(JSONObject.class);
            Assert.assertEquals("incorrect number of elements", 1, json.length());
            appsStatInfo = json.getJSONObject("appStatInfo");
            Assert.assertEquals("incorrect number of elements", 1, appsStatInfo.length());
            statItems = appsStatInfo.getJSONArray("statItem");
            Assert.assertEquals("incorrect number of elements", 2, statItems.length());
            JSONObject statItem1 = statItems.getJSONObject(0);
            JSONObject statItem2 = statItems.getJSONObject(1);
            Assert.assertTrue((((statItem1.getString("state").equals("ACCEPTED")) && (statItem2.getString("state").equals("FINISHED"))) || ((statItem2.getString("state").equals("ACCEPTED")) && (statItem1.getString("state").equals("FINISHED")))));
            Assert.assertEquals("mapreduce", statItem1.getString("type"));
            Assert.assertEquals("1", statItem1.getString("count"));
            Assert.assertEquals("mapreduce", statItem2.getString("type"));
            Assert.assertEquals("1", statItem2.getString("count"));
            // invalid state
            r = resource();
            response = r.path("ws").path("v1").path("cluster").path("appstatistics").queryParam("states", "wrong_state").accept(APPLICATION_JSON).get(ClientResponse.class);
            WebServicesTestUtils.assertResponseStatusCode(BAD_REQUEST, response.getStatusInfo());
            Assert.assertEquals((((MediaType.APPLICATION_JSON_TYPE) + "; ") + (JettyUtils.UTF_8)), response.getType().toString());
            json = response.getEntity(JSONObject.class);
            Assert.assertEquals("incorrect number of elements", 1, json.length());
            exception = json.getJSONObject("RemoteException");
            Assert.assertEquals("incorrect number of elements", 3, exception.length());
            message = exception.getString("message");
            type = exception.getString("exception");
            className = exception.getString("javaClassName");
            WebServicesTestUtils.checkStringContains("exception message", "Invalid application-state wrong_state", message);
            WebServicesTestUtils.checkStringEqual("exception type", "BadRequestException", type);
            WebServicesTestUtils.checkStringEqual("exception className", "org.apache.hadoop.yarn.webapp.BadRequestException", className);
        } finally {
            stop();
        }
    }

    @Test
    public void testSingleApp() throws Exception, JSONException {
        start();
        MockNM amNodeManager = TestRMWebServicesApps.rm.registerNode("127.0.0.1:1234", 2048);
        RMApp app1 = TestRMWebServicesApps.rm.submitApp(TestRMWebServicesApps.CONTAINER_MB, "testwordcount", "user1");
        amNodeManager.nodeHeartbeat(true);
        testSingleAppsHelper(app1.getApplicationId().toString(), app1, APPLICATION_JSON);
        stop();
    }

    @Test
    public void testUnmarshalAppInfo() throws Exception, JSONException {
        start();
        MockNM amNodeManager = TestRMWebServicesApps.rm.registerNode("127.0.0.1:1234", 2048);
        RMApp app1 = TestRMWebServicesApps.rm.submitApp(TestRMWebServicesApps.CONTAINER_MB, "testwordcount", "user1");
        amNodeManager.nodeHeartbeat(true);
        WebResource r = resource();
        ClientResponse response = r.path("ws").path("v1").path("cluster").path("apps").path(app1.getApplicationId().toString()).accept(APPLICATION_XML).get(ClientResponse.class);
        AppInfo appInfo = response.getEntity(AppInfo.class);
        // Check only a few values; all are validated in testSingleApp.
        Assert.assertEquals(app1.getApplicationId().toString(), appInfo.getAppId());
        Assert.assertEquals(app1.getName(), appInfo.getName());
        Assert.assertEquals(app1.createApplicationState(), appInfo.getState());
        Assert.assertEquals(app1.getAMResourceRequests().get(0).getCapability().getMemorySize(), appInfo.getAllocatedMB());
        stop();
    }

    @Test
    public void testSingleAppsSlash() throws Exception, JSONException {
        start();
        MockNM amNodeManager = TestRMWebServicesApps.rm.registerNode("127.0.0.1:1234", 2048);
        RMApp app1 = TestRMWebServicesApps.rm.submitApp(TestRMWebServicesApps.CONTAINER_MB);
        amNodeManager.nodeHeartbeat(true);
        testSingleAppsHelper(((app1.getApplicationId().toString()) + "/"), app1, APPLICATION_JSON);
        stop();
    }

    @Test
    public void testSingleAppsDefault() throws Exception, JSONException {
        start();
        MockNM amNodeManager = TestRMWebServicesApps.rm.registerNode("127.0.0.1:1234", 2048);
        RMApp app1 = TestRMWebServicesApps.rm.submitApp(TestRMWebServicesApps.CONTAINER_MB);
        amNodeManager.nodeHeartbeat(true);
        testSingleAppsHelper(((app1.getApplicationId().toString()) + "/"), app1, "");
        stop();
    }

    @Test
    public void testInvalidApp() throws Exception, JSONException {
        start();
        MockNM amNodeManager = TestRMWebServicesApps.rm.registerNode("127.0.0.1:1234", 2048);
        TestRMWebServicesApps.rm.submitApp(TestRMWebServicesApps.CONTAINER_MB);
        amNodeManager.nodeHeartbeat(true);
        WebResource r = resource();
        try {
            r.path("ws").path("v1").path("cluster").path("apps").path("application_invalid_12").accept(APPLICATION_JSON).get(JSONObject.class);
            Assert.fail("should have thrown exception on invalid appid");
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
    public void testNonexistApp() throws Exception, JSONException {
        start();
        MockNM amNodeManager = TestRMWebServicesApps.rm.registerNode("127.0.0.1:1234", 2048);
        TestRMWebServicesApps.rm.submitApp(TestRMWebServicesApps.CONTAINER_MB, "testwordcount", "user1");
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
    public void testSingleAppsXML() throws Exception, JSONException {
        start();
        MockNM amNodeManager = TestRMWebServicesApps.rm.registerNode("127.0.0.1:1234", 2048);
        RMApp app1 = TestRMWebServicesApps.rm.submitApp(TestRMWebServicesApps.CONTAINER_MB, "testwordcount", "user1");
        amNodeManager.nodeHeartbeat(true);
        WebResource r = resource();
        ClientResponse response = r.path("ws").path("v1").path("cluster").path("apps").path(app1.getApplicationId().toString()).accept(APPLICATION_XML).get(ClientResponse.class);
        Assert.assertEquals((((MediaType.APPLICATION_XML_TYPE) + "; ") + (JettyUtils.UTF_8)), response.getType().toString());
        String xml = response.getEntity(String.class);
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        InputSource is = new InputSource();
        is.setCharacterStream(new StringReader(xml));
        Document dom = db.parse(is);
        NodeList nodes = dom.getElementsByTagName("app");
        Assert.assertEquals("incorrect number of elements", 1, nodes.getLength());
        verifyAppsXML(nodes, app1, false);
        stop();
    }
}

