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


import AuthenticationFilter.AUTH_TYPE;
import MediaType.APPLICATION_JSON;
import MediaType.APPLICATION_XML;
import MediaType.APPLICATION_XML_TYPE;
import NodeAttribute.PREFIX_DISTRIBUTED;
import NodeAttributeType.STRING;
import NodeState.DECOMMISSIONED;
import NodeState.LOST;
import NodeState.NEW;
import NodeState.RUNNING;
import NodeState.UNHEALTHY;
import PseudoAuthenticationHandler.ANONYMOUS_ALLOWED;
import RMWSConsts.NODES;
import RMWSConsts.RM_WEB_SERVICE_PATH;
import Status.BAD_REQUEST;
import Status.NOT_FOUND;
import Status.OK;
import YarnConfiguration.YARN_ADMIN_ACL;
import com.google.common.base.Joiner;
import com.google.inject.Guice;
import com.google.inject.Singleton;
import com.google.inject.servlet.GuiceFilter;
import com.google.inject.servlet.ServletModule;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.guice.spi.container.servlet.GuiceContainer;
import com.sun.jersey.test.framework.WebAppDescriptor;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeMap;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.ws.rs.core.MediaType;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.apache.hadoop.http.JettyUtils;
import org.apache.hadoop.security.UserGroupInformation;
import org.apache.hadoop.security.authentication.server.AuthenticationFilter;
import org.apache.hadoop.yarn.api.records.NodeAttribute;
import org.apache.hadoop.yarn.api.records.NodeId;
import org.apache.hadoop.yarn.api.records.NodeState;
import org.apache.hadoop.yarn.api.records.Resource;
import org.apache.hadoop.yarn.api.records.ResourceOption;
import org.apache.hadoop.yarn.api.records.ResourceUtilization;
import org.apache.hadoop.yarn.conf.YarnConfiguration;
import org.apache.hadoop.yarn.server.api.protocolrecords.NodeHeartbeatRequest;
import org.apache.hadoop.yarn.server.api.protocolrecords.RegisterNodeManagerRequest;
import org.apache.hadoop.yarn.server.api.protocolrecords.RegisterNodeManagerResponse;
import org.apache.hadoop.yarn.server.api.records.NodeHealthStatus;
import org.apache.hadoop.yarn.server.api.records.NodeStatus;
import org.apache.hadoop.yarn.server.resourcemanager.MockRM;
import org.apache.hadoop.yarn.server.resourcemanager.ResourceManager;
import org.apache.hadoop.yarn.server.resourcemanager.ResourceTrackerService;
import org.apache.hadoop.yarn.server.resourcemanager.rmnode.RMNode;
import org.apache.hadoop.yarn.server.resourcemanager.rmnode.RMNodeImpl;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.constraint.AllocationTagsManager;
import org.apache.hadoop.yarn.server.resourcemanager.webapp.dao.NodeInfo;
import org.apache.hadoop.yarn.server.resourcemanager.webapp.dao.ResourceInfo;
import org.apache.hadoop.yarn.server.resourcemanager.webapp.dao.ResourceOptionInfo;
import org.apache.hadoop.yarn.server.utils.BuilderUtils;
import org.apache.hadoop.yarn.util.Records;
import org.apache.hadoop.yarn.util.YarnVersionInfo;
import org.apache.hadoop.yarn.webapp.GenericExceptionHandler;
import org.apache.hadoop.yarn.webapp.GuiceServletConfig;
import org.apache.hadoop.yarn.webapp.JerseyTestBase;
import org.apache.hadoop.yarn.webapp.WebServicesTestUtils;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;


public class TestRMWebServicesNodes extends JerseyTestBase {
    private static MockRM rm;

    private static YarnConfiguration conf;

    private static String userName;

    private static class WebServletModule extends ServletModule {
        @Override
        protected void configureServlets() {
            bind(JAXBContextResolver.class);
            try {
                TestRMWebServicesNodes.userName = UserGroupInformation.getCurrentUser().getShortUserName();
            } catch (IOException ioe) {
                throw new RuntimeException(("Unable to get current user name " + (ioe.getMessage())), ioe);
            }
            TestRMWebServicesNodes.conf = new YarnConfiguration();
            TestRMWebServicesNodes.conf.set(YARN_ADMIN_ACL, TestRMWebServicesNodes.userName);
            bind(RMWebServices.class);
            bind(GenericExceptionHandler.class);
            TestRMWebServicesNodes.rm = new MockRM(TestRMWebServicesNodes.conf);
            getRMContext().getContainerTokenSecretManager().rollMasterKey();
            getRMContext().getNMTokenSecretManager().rollMasterKey();
            TestRMWebServicesNodes.rm.disableDrainEventsImplicitly();
            bind(ResourceManager.class).toInstance(TestRMWebServicesNodes.rm);
            filter("/*").through(TestRMWebServicesNodes.TestRMCustomAuthFilter.class);
            serve("/*").with(GuiceContainer.class);
        }
    }

    /**
     * Custom filter to be able to test auth methods and let the other ones go.
     */
    @Singleton
    public static class TestRMCustomAuthFilter extends AuthenticationFilter {
        @Override
        protected Properties getConfiguration(String configPrefix, FilterConfig filterConfig) throws ServletException {
            Properties props = new Properties();
            Enumeration<?> names = filterConfig.getInitParameterNames();
            while (names.hasMoreElements()) {
                String name = ((String) (names.nextElement()));
                if (name.startsWith(configPrefix)) {
                    String value = filterConfig.getInitParameter(name);
                    props.put(name.substring(configPrefix.length()), value);
                }
            } 
            props.put(AUTH_TYPE, "simple");
            props.put(ANONYMOUS_ALLOWED, "true");
            return props;
        }
    }

    static {
        GuiceServletConfig.setInjector(Guice.createInjector(new TestRMWebServicesNodes.WebServletModule()));
    }

    public TestRMWebServicesNodes() {
        super(new WebAppDescriptor.Builder("org.apache.hadoop.yarn.server.resourcemanager.webapp").contextListenerClass(GuiceServletConfig.class).filterClass(GuiceFilter.class).contextPath("jersey-guice-filter").servletPath("/").build());
    }

    @Test
    public void testNodes() throws Exception, JSONException {
        testNodesHelper("nodes", APPLICATION_JSON);
    }

    @Test
    public void testNodesSlash() throws Exception, JSONException {
        testNodesHelper("nodes/", APPLICATION_JSON);
    }

    @Test
    public void testNodesDefault() throws Exception, JSONException {
        testNodesHelper("nodes/", "");
    }

    @Test
    public void testNodesDefaultWithUnHealthyNode() throws Exception, JSONException {
        WebResource r = resource();
        getRunningRMNode("h1", 1234, 5120);
        // h2 will be in NEW state
        getNewRMNode("h2", 1235, 5121);
        RMNode node3 = getRunningRMNode("h3", 1236, 5122);
        NodeId nodeId3 = node3.getNodeID();
        RMNode node = getRMContext().getRMNodes().get(nodeId3);
        NodeHealthStatus nodeHealth = NodeHealthStatus.newInstance(false, "test health report", System.currentTimeMillis());
        NodeStatus nodeStatus = NodeStatus.newInstance(nodeId3, 1, new ArrayList<org.apache.hadoop.yarn.api.records.ContainerStatus>(), null, nodeHealth, null, null, null);
        handle(new org.apache.hadoop.yarn.server.resourcemanager.rmnode.RMNodeStatusEvent(nodeId3, nodeStatus, null));
        TestRMWebServicesNodes.rm.waitForState(nodeId3, UNHEALTHY);
        ClientResponse response = r.path("ws").path("v1").path("cluster").path("nodes").accept(APPLICATION_JSON).get(ClientResponse.class);
        Assert.assertEquals((((MediaType.APPLICATION_JSON_TYPE) + "; ") + (JettyUtils.UTF_8)), response.getType().toString());
        JSONObject json = response.getEntity(JSONObject.class);
        Assert.assertEquals("incorrect number of elements", 1, json.length());
        JSONObject nodes = json.getJSONObject("nodes");
        Assert.assertEquals("incorrect number of elements", 1, nodes.length());
        JSONArray nodeArray = nodes.getJSONArray("node");
        // 3 nodes, including the unhealthy node and the new node.
        Assert.assertEquals("incorrect number of elements", 3, nodeArray.length());
    }

    @Test
    public void testNodesQueryNew() throws Exception, JSONException {
        WebResource r = resource();
        getRunningRMNode("h1", 1234, 5120);
        // h2 will be in NEW state
        RMNode rmnode2 = getNewRMNode("h2", 1235, 5121);
        ClientResponse response = r.path("ws").path("v1").path("cluster").path("nodes").queryParam("states", NEW.toString()).accept(APPLICATION_JSON).get(ClientResponse.class);
        Assert.assertEquals((((MediaType.APPLICATION_JSON_TYPE) + "; ") + (JettyUtils.UTF_8)), response.getType().toString());
        JSONObject json = response.getEntity(JSONObject.class);
        Assert.assertEquals("incorrect number of elements", 1, json.length());
        JSONObject nodes = json.getJSONObject("nodes");
        Assert.assertEquals("incorrect number of elements", 1, nodes.length());
        JSONArray nodeArray = nodes.getJSONArray("node");
        Assert.assertEquals("incorrect number of elements", 1, nodeArray.length());
        JSONObject info = nodeArray.getJSONObject(0);
        verifyNodeInfo(info, rmnode2);
    }

    @Test
    public void testNodesQueryStateNone() throws Exception, JSONException {
        WebResource r = resource();
        getNewRMNode("h1", 1234, 5120);
        getNewRMNode("h2", 1235, 5121);
        ClientResponse response = r.path("ws").path("v1").path("cluster").path("nodes").queryParam("states", DECOMMISSIONED.toString()).accept(APPLICATION_JSON).get(ClientResponse.class);
        Assert.assertEquals((((MediaType.APPLICATION_JSON_TYPE) + "; ") + (JettyUtils.UTF_8)), response.getType().toString());
        JSONObject json = response.getEntity(JSONObject.class);
        Assert.assertEquals("incorrect number of elements", 1, json.length());
        Assert.assertEquals("nodes is not empty", new JSONObject().toString(), json.get("nodes").toString());
    }

    @Test
    public void testNodesQueryStateInvalid() throws Exception, JSONException {
        WebResource r = resource();
        getNewRMNode("h1", 1234, 5120);
        getNewRMNode("h2", 1235, 5121);
        try {
            r.path("ws").path("v1").path("cluster").path("nodes").queryParam("states", "BOGUSSTATE").accept(APPLICATION_JSON).get(JSONObject.class);
            Assert.fail("should have thrown exception querying invalid state");
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
            WebServicesTestUtils.checkStringContains("exception message", "org.apache.hadoop.yarn.api.records.NodeState.BOGUSSTATE", message);
            WebServicesTestUtils.checkStringMatch("exception type", "IllegalArgumentException", type);
            WebServicesTestUtils.checkStringMatch("exception classname", "java.lang.IllegalArgumentException", classname);
        }
    }

    @Test
    public void testNodesQueryStateLost() throws Exception, JSONException {
        WebResource r = resource();
        RMNode rmnode1 = getRunningRMNode("h1", 1234, 5120);
        sendLostEvent(rmnode1);
        RMNode rmnode2 = getRunningRMNode("h2", 1235, 5121);
        sendLostEvent(rmnode2);
        ClientResponse response = r.path("ws").path("v1").path("cluster").path("nodes").queryParam("states", LOST.toString()).accept(APPLICATION_JSON).get(ClientResponse.class);
        Assert.assertEquals((((MediaType.APPLICATION_JSON_TYPE) + "; ") + (JettyUtils.UTF_8)), response.getType().toString());
        JSONObject json = response.getEntity(JSONObject.class);
        JSONObject nodes = json.getJSONObject("nodes");
        Assert.assertEquals("incorrect number of elements", 1, nodes.length());
        JSONArray nodeArray = nodes.getJSONArray("node");
        Assert.assertEquals("incorrect number of elements", 2, nodeArray.length());
        for (int i = 0; i < (nodeArray.length()); ++i) {
            JSONObject info = nodeArray.getJSONObject(i);
            String[] node = info.get("id").toString().split(":");
            NodeId nodeId = NodeId.newInstance(node[0], Integer.parseInt(node[1]));
            RMNode rmNode = getRMContext().getInactiveRMNodes().get(nodeId);
            WebServicesTestUtils.checkStringMatch("nodeHTTPAddress", "", info.getString("nodeHTTPAddress"));
            if (rmNode != null) {
                WebServicesTestUtils.checkStringMatch("state", rmNode.getState().toString(), info.getString("state"));
            }
        }
    }

    @Test
    public void testSingleNodeQueryStateLost() throws Exception, JSONException {
        WebResource r = resource();
        getRunningRMNode("h1", 1234, 5120);
        RMNode rmnode2 = getRunningRMNode("h2", 1234, 5121);
        sendLostEvent(rmnode2);
        ClientResponse response = r.path("ws").path("v1").path("cluster").path("nodes").path("h2:1234").accept(APPLICATION_JSON).get(ClientResponse.class);
        Assert.assertEquals((((MediaType.APPLICATION_JSON_TYPE) + "; ") + (JettyUtils.UTF_8)), response.getType().toString());
        JSONObject json = response.getEntity(JSONObject.class);
        JSONObject info = json.getJSONObject("node");
        String id = info.get("id").toString();
        Assert.assertEquals("Incorrect Node Information.", "h2:1234", id);
        RMNode rmNode = getRMContext().getInactiveRMNodes().get(rmnode2.getNodeID());
        WebServicesTestUtils.checkStringMatch("nodeHTTPAddress", "", info.getString("nodeHTTPAddress"));
        if (rmNode != null) {
            WebServicesTestUtils.checkStringMatch("state", rmNode.getState().toString(), info.getString("state"));
        }
    }

    @Test
    public void testNodesQueryRunning() throws Exception, JSONException {
        WebResource r = resource();
        getRunningRMNode("h1", 1234, 5120);
        // h2 will be in NEW state
        getNewRMNode("h2", 1235, 5121);
        ClientResponse response = r.path("ws").path("v1").path("cluster").path("nodes").queryParam("states", "running").accept(APPLICATION_JSON).get(ClientResponse.class);
        Assert.assertEquals((((MediaType.APPLICATION_JSON_TYPE) + "; ") + (JettyUtils.UTF_8)), response.getType().toString());
        JSONObject json = response.getEntity(JSONObject.class);
        Assert.assertEquals("incorrect number of elements", 1, json.length());
        JSONObject nodes = json.getJSONObject("nodes");
        Assert.assertEquals("incorrect number of elements", 1, nodes.length());
        JSONArray nodeArray = nodes.getJSONArray("node");
        Assert.assertEquals("incorrect number of elements", 1, nodeArray.length());
    }

    @Test
    public void testNodesQueryHealthyFalse() throws Exception, JSONException {
        WebResource r = resource();
        getRunningRMNode("h1", 1234, 5120);
        // h2 will be in NEW state
        getNewRMNode("h2", 1235, 5121);
        ClientResponse response = r.path("ws").path("v1").path("cluster").path("nodes").queryParam("states", "UNHEALTHY").accept(APPLICATION_JSON).get(ClientResponse.class);
        Assert.assertEquals((((MediaType.APPLICATION_JSON_TYPE) + "; ") + (JettyUtils.UTF_8)), response.getType().toString());
        JSONObject json = response.getEntity(JSONObject.class);
        Assert.assertEquals("incorrect number of elements", 1, json.length());
        Assert.assertEquals("nodes is not empty", new JSONObject().toString(), json.get("nodes").toString());
    }

    @Test
    public void testSingleNode() throws Exception, JSONException {
        getRunningRMNode("h1", 1234, 5120);
        RMNode rmnode2 = getRunningRMNode("h2", 1235, 5121);
        testSingleNodeHelper("h2:1235", rmnode2, APPLICATION_JSON);
    }

    @Test
    public void testSingleNodeSlash() throws Exception, JSONException {
        RMNode rmnode1 = getRunningRMNode("h1", 1234, 5120);
        getRunningRMNode("h2", 1235, 5121);
        testSingleNodeHelper("h1:1234/", rmnode1, APPLICATION_JSON);
    }

    @Test
    public void testSingleNodeDefault() throws Exception, JSONException {
        RMNode rmnode1 = getRunningRMNode("h1", 1234, 5120);
        getRunningRMNode("h2", 1235, 5121);
        testSingleNodeHelper("h1:1234/", rmnode1, "");
    }

    @Test
    public void testNonexistNode() throws Exception, JSONException {
        // add h1 node in NEW state
        getNewRMNode("h1", 1234, 5120);
        // add h2 node in NEW state
        getNewRMNode("h2", 1235, 5121);
        WebResource r = resource();
        try {
            r.path("ws").path("v1").path("cluster").path("nodes").path("node_invalid:99").accept(APPLICATION_JSON).get(JSONObject.class);
            Assert.fail("should have thrown exception on non-existent nodeid");
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
            verifyNonexistNodeException(message, type, classname);
        }
    }

    // test that the exception output defaults to JSON
    @Test
    public void testNonexistNodeDefault() throws Exception, JSONException {
        getNewRMNode("h1", 1234, 5120);
        getNewRMNode("h2", 1235, 5121);
        WebResource r = resource();
        try {
            r.path("ws").path("v1").path("cluster").path("nodes").path("node_invalid:99").get(JSONObject.class);
            Assert.fail("should have thrown exception on non-existent nodeid");
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
            verifyNonexistNodeException(message, type, classname);
        }
    }

    // test that the exception output works in XML
    @Test
    public void testNonexistNodeXML() throws Exception, JSONException {
        getNewRMNode("h1", 1234, 5120);
        getNewRMNode("h2", 1235, 5121);
        WebResource r = resource();
        try {
            r.path("ws").path("v1").path("cluster").path("nodes").path("node_invalid:99").accept(APPLICATION_XML).get(JSONObject.class);
            Assert.fail("should have thrown exception on non-existent nodeid");
        } catch (UniformInterfaceException ue) {
            ClientResponse response = ue.getResponse();
            WebServicesTestUtils.assertResponseStatusCode(NOT_FOUND, response.getStatusInfo());
            Assert.assertEquals((((MediaType.APPLICATION_XML_TYPE) + "; ") + (JettyUtils.UTF_8)), response.getType().toString());
            String msg = response.getEntity(String.class);
            System.out.println(msg);
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            InputSource is = new InputSource();
            is.setCharacterStream(new StringReader(msg));
            Document dom = db.parse(is);
            NodeList nodes = dom.getElementsByTagName("RemoteException");
            Element element = ((Element) (nodes.item(0)));
            String message = WebServicesTestUtils.getXmlString(element, "message");
            String type = WebServicesTestUtils.getXmlString(element, "exception");
            String classname = WebServicesTestUtils.getXmlString(element, "javaClassName");
            verifyNonexistNodeException(message, type, classname);
        }
    }

    @Test
    public void testInvalidNode() throws Exception, JSONException {
        getNewRMNode("h1", 1234, 5120);
        getNewRMNode("h2", 1235, 5121);
        WebResource r = resource();
        try {
            r.path("ws").path("v1").path("cluster").path("nodes").path("node_invalid_foo").accept(APPLICATION_JSON).get(JSONObject.class);
            Assert.fail("should have thrown exception on non-existent nodeid");
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
            WebServicesTestUtils.checkStringMatch("exception message", "Invalid NodeId \\[node_invalid_foo\\]. Expected host:port", message);
            WebServicesTestUtils.checkStringMatch("exception type", "IllegalArgumentException", type);
            WebServicesTestUtils.checkStringMatch("exception classname", "java.lang.IllegalArgumentException", classname);
        }
    }

    @Test
    public void testNodesXML() throws Exception, JSONException {
        WebResource r = resource();
        RMNodeImpl rmnode1 = getNewRMNode("h1", 1234, 5120);
        // MockNM nm2 = rm.registerNode("h2:1235", 5121);
        ClientResponse response = r.path("ws").path("v1").path("cluster").path("nodes").accept(APPLICATION_XML).get(ClientResponse.class);
        Assert.assertEquals((((MediaType.APPLICATION_XML_TYPE) + "; ") + (JettyUtils.UTF_8)), response.getType().toString());
        String xml = response.getEntity(String.class);
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        InputSource is = new InputSource();
        is.setCharacterStream(new StringReader(xml));
        Document dom = db.parse(is);
        NodeList nodesApps = dom.getElementsByTagName("nodes");
        Assert.assertEquals("incorrect number of elements", 1, nodesApps.getLength());
        NodeList nodes = dom.getElementsByTagName("node");
        Assert.assertEquals("incorrect number of elements", 1, nodes.getLength());
        verifyNodesXML(nodes, rmnode1);
    }

    @Test
    public void testSingleNodesXML() throws Exception, JSONException {
        WebResource r = resource();
        // add h2 node in NEW state
        RMNodeImpl rmnode1 = getNewRMNode("h1", 1234, 5120);
        // MockNM nm2 = rm.registerNode("h2:1235", 5121);
        ClientResponse response = r.path("ws").path("v1").path("cluster").path("nodes").path("h1:1234").accept(APPLICATION_XML).get(ClientResponse.class);
        Assert.assertEquals((((MediaType.APPLICATION_XML_TYPE) + "; ") + (JettyUtils.UTF_8)), response.getType().toString());
        String xml = response.getEntity(String.class);
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        InputSource is = new InputSource();
        is.setCharacterStream(new StringReader(xml));
        Document dom = db.parse(is);
        NodeList nodes = dom.getElementsByTagName("node");
        Assert.assertEquals("incorrect number of elements", 1, nodes.getLength());
        verifyNodesXML(nodes, rmnode1);
    }

    @Test
    public void testNodes2XML() throws Exception, JSONException {
        WebResource r = resource();
        getNewRMNode("h1", 1234, 5120);
        getNewRMNode("h2", 1235, 5121);
        ClientResponse response = r.path("ws").path("v1").path("cluster").path("nodes").accept(APPLICATION_XML).get(ClientResponse.class);
        Assert.assertEquals((((MediaType.APPLICATION_XML_TYPE) + "; ") + (JettyUtils.UTF_8)), response.getType().toString());
        String xml = response.getEntity(String.class);
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        InputSource is = new InputSource();
        is.setCharacterStream(new StringReader(xml));
        Document dom = db.parse(is);
        NodeList nodesApps = dom.getElementsByTagName("nodes");
        Assert.assertEquals("incorrect number of elements", 1, nodesApps.getLength());
        NodeList nodes = dom.getElementsByTagName("node");
        Assert.assertEquals("incorrect number of elements", 2, nodes.getLength());
    }

    @Test
    public void testQueryAll() throws Exception {
        WebResource r = resource();
        getRunningRMNode("h1", 1234, 5120);
        // add h2 node in NEW state
        getNewRMNode("h2", 1235, 5121);
        // add lost node
        RMNode nm3 = getRunningRMNode("h3", 1236, 5122);
        sendLostEvent(nm3);
        ClientResponse response = r.path("ws").path("v1").path("cluster").path("nodes").queryParam("states", Joiner.on(',').join(EnumSet.allOf(NodeState.class))).accept(APPLICATION_JSON).get(ClientResponse.class);
        Assert.assertEquals((((MediaType.APPLICATION_JSON_TYPE) + "; ") + (JettyUtils.UTF_8)), response.getType().toString());
        JSONObject json = response.getEntity(JSONObject.class);
        JSONObject nodes = json.getJSONObject("nodes");
        Assert.assertEquals("incorrect number of elements", 1, nodes.length());
        JSONArray nodeArray = nodes.getJSONArray("node");
        Assert.assertEquals("incorrect number of elements", 3, nodeArray.length());
    }

    @Test
    public void testNodesResourceUtilization() throws Exception, JSONException {
        WebResource r = resource();
        RMNode rmnode1 = getRunningRMNode("h1", 1234, 5120);
        NodeId nodeId1 = rmnode1.getNodeID();
        RMNodeImpl node = ((RMNodeImpl) (getRMContext().getRMNodes().get(nodeId1)));
        NodeHealthStatus nodeHealth = NodeHealthStatus.newInstance(true, "test health report", System.currentTimeMillis());
        ResourceUtilization nodeResource = ResourceUtilization.newInstance(4096, 0, ((float) (10.5)));
        ResourceUtilization containerResource = ResourceUtilization.newInstance(2048, 0, ((float) (5.05)));
        NodeStatus nodeStatus = NodeStatus.newInstance(nodeId1, 0, new ArrayList<org.apache.hadoop.yarn.api.records.ContainerStatus>(), null, nodeHealth, containerResource, nodeResource, null);
        node.handle(new org.apache.hadoop.yarn.server.resourcemanager.rmnode.RMNodeStatusEvent(nodeId1, nodeStatus, null));
        TestRMWebServicesNodes.rm.waitForState(nodeId1, RUNNING);
        ClientResponse response = r.path("ws").path("v1").path("cluster").path("nodes").accept(APPLICATION_JSON).get(ClientResponse.class);
        Assert.assertEquals((((MediaType.APPLICATION_JSON_TYPE) + "; ") + (JettyUtils.UTF_8)), response.getType().toString());
        JSONObject json = response.getEntity(JSONObject.class);
        Assert.assertEquals("incorrect number of elements", 1, json.length());
        JSONObject nodes = json.getJSONObject("nodes");
        Assert.assertEquals("incorrect number of elements", 1, nodes.length());
        JSONArray nodeArray = nodes.getJSONArray("node");
        Assert.assertEquals("incorrect number of elements", 1, nodeArray.length());
        JSONObject info = nodeArray.getJSONObject(0);
        // verify the resource utilization
        verifyNodeInfo(info, rmnode1);
    }

    @Test
    public void testUpdateNodeResource() throws Exception {
        WebResource r = resource().path(RM_WEB_SERVICE_PATH);
        r = r.queryParam("user.name", TestRMWebServicesNodes.userName);
        RMNode rmnode = getRunningRMNode("h1", 1234, 5120);
        String rmnodeId = rmnode.getNodeID().toString();
        Assert.assertEquals("h1:1234", rmnodeId);
        // assert memory and default vcores
        ClientResponse response = r.path(NODES).path(rmnodeId).accept(APPLICATION_XML).get(ClientResponse.class);
        NodeInfo nodeInfo0 = response.getEntity(NodeInfo.class);
        ResourceInfo nodeResource0 = nodeInfo0.getTotalResource();
        Assert.assertEquals(5120, nodeResource0.getMemorySize());
        Assert.assertEquals(4, nodeResource0.getvCores());
        // the RM needs to be running to process the resource update
        start();
        // update memory to 8192MB and 5 cores
        Resource resource = Resource.newInstance(8192, 5);
        ResourceOptionInfo resourceOption = new ResourceOptionInfo(ResourceOption.newInstance(resource, 1000));
        response = r.path(NODES).path(rmnodeId).path("resource").entity(resourceOption, APPLICATION_XML_TYPE).accept(APPLICATION_XML).post(ClientResponse.class);
        WebServicesTestUtils.assertResponseStatusCode(OK, response.getStatusInfo());
        ResourceInfo updatedResource = response.getEntity(ResourceInfo.class);
        Assert.assertEquals(8192, updatedResource.getMemorySize());
        Assert.assertEquals(5, updatedResource.getvCores());
        // assert updated memory and cores
        response = r.path(NODES).path(rmnodeId).accept(APPLICATION_XML).get(ClientResponse.class);
        NodeInfo nodeInfo1 = response.getEntity(NodeInfo.class);
        ResourceInfo nodeResource1 = nodeInfo1.getTotalResource();
        Assert.assertEquals(8192, nodeResource1.getMemorySize());
        Assert.assertEquals(5, nodeResource1.getvCores());
        // test non existing node
        response = r.path(NODES).path("badnode").path("resource").entity(resourceOption, APPLICATION_XML_TYPE).accept(APPLICATION_JSON).post(ClientResponse.class);
        WebServicesTestUtils.assertResponseStatusCode(BAD_REQUEST, response.getStatusInfo());
        JSONObject json = response.getEntity(JSONObject.class);
        JSONObject exception = json.getJSONObject("RemoteException");
        Assert.assertEquals("IllegalArgumentException", exception.getString("exception"));
        String msg = exception.getString("message");
        Assert.assertTrue(("Wrong message: " + msg), msg.startsWith("Invalid NodeId"));
        stop();
    }

    @Test
    public void testNodesAllocationTags() throws Exception {
        NodeId nm1 = NodeId.newInstance("host1", 1234);
        NodeId nm2 = NodeId.newInstance("host2", 2345);
        AllocationTagsManager atm = Mockito.mock(AllocationTagsManager.class);
        Map<String, Map<String, Long>> expectedAllocationTags = new TreeMap<>();
        Map<String, Long> nm1Tags = new TreeMap<>();
        nm1Tags.put("A", 1L);
        nm1Tags.put("B", 2L);
        Map<String, Long> nm2Tags = new TreeMap<>();
        nm2Tags.put("C", 1L);
        nm2Tags.put("D", 2L);
        expectedAllocationTags.put(nm1.toString(), nm1Tags);
        expectedAllocationTags.put(nm2.toString(), nm2Tags);
        Mockito.when(atm.getAllocationTagsWithCount(nm1)).thenReturn(nm1Tags);
        Mockito.when(atm.getAllocationTagsWithCount(nm2)).thenReturn(nm2Tags);
        getRMContext().setAllocationTagsManager(atm);
        start();
        TestRMWebServicesNodes.rm.registerNode(nm1.toString(), 1024);
        TestRMWebServicesNodes.rm.registerNode(nm2.toString(), 1024);
        WebResource r = resource();
        ClientResponse response = r.path("ws").path("v1").path("cluster").path("nodes").accept("application/json").get(ClientResponse.class);
        Assert.assertEquals((((MediaType.APPLICATION_JSON) + "; ") + (JettyUtils.UTF_8)), response.getType().toString());
        JSONObject nodesInfoJson = response.getEntity(JSONObject.class);
        verifyNodeAllocationTag(nodesInfoJson, expectedAllocationTags);
        stop();
    }

    @Test
    public void testNodeAttributesInfo() throws Exception {
        ResourceTrackerService resourceTrackerService = getResourceTrackerService();
        RegisterNodeManagerRequest registerReq = Records.newRecord(RegisterNodeManagerRequest.class);
        NodeId nodeId = NodeId.newInstance("host1", 1234);
        Resource capability = BuilderUtils.newResource(1024, 1);
        registerReq.setResource(capability);
        registerReq.setNodeId(nodeId);
        registerReq.setHttpPort(1234);
        registerReq.setNMVersion(YarnVersionInfo.getVersion());
        RegisterNodeManagerResponse registerResponse = resourceTrackerService.registerNodeManager(registerReq);
        Set<NodeAttribute> nodeAttributes = new HashSet<>();
        nodeAttributes.add(NodeAttribute.newInstance(PREFIX_DISTRIBUTED, "host", STRING, "host1"));
        nodeAttributes.add(NodeAttribute.newInstance(PREFIX_DISTRIBUTED, "rack", STRING, "rack1"));
        NodeHeartbeatRequest heartbeatReq = Records.newRecord(NodeHeartbeatRequest.class);
        NodeStatus nodeStatus = NodeStatus.newInstance(nodeId, 0, new ArrayList<org.apache.hadoop.yarn.api.records.ContainerStatus>(), null, null, null, null, null);
        heartbeatReq.setNodeStatus(nodeStatus);
        heartbeatReq.setLastKnownNMTokenMasterKey(registerResponse.getNMTokenMasterKey());
        heartbeatReq.setLastKnownContainerTokenMasterKey(registerResponse.getContainerTokenMasterKey());
        heartbeatReq.setNodeAttributes(nodeAttributes);
        resourceTrackerService.nodeHeartbeat(heartbeatReq);
        WebResource r = resource();
        ClientResponse response = r.path("ws").path("v1").path("cluster").path("nodes").accept("application/json").get(ClientResponse.class);
        JSONObject nodesInfoJson = response.getEntity(JSONObject.class);
        JSONArray nodes = nodesInfoJson.getJSONObject("nodes").getJSONArray("node");
        JSONObject nodeJson = nodes.getJSONObject(0);
        JSONArray nodeAttributesInfo = nodeJson.getJSONObject("nodeAttributesInfo").getJSONArray("nodeAttributeInfo");
        Assert.assertEquals(nodeAttributes.size(), nodeAttributesInfo.length());
        Iterator<NodeAttribute> it = nodeAttributes.iterator();
        for (int j = 0; j < (nodeAttributesInfo.length()); j++) {
            JSONObject nodeAttributeInfo = nodeAttributesInfo.getJSONObject(j);
            NodeAttribute expectedNodeAttribute = it.next();
            String expectedPrefix = expectedNodeAttribute.getAttributeKey().getAttributePrefix();
            String expectedName = expectedNodeAttribute.getAttributeKey().getAttributeName();
            String expectedType = expectedNodeAttribute.getAttributeType().toString();
            String expectedValue = expectedNodeAttribute.getAttributeValue();
            Assert.assertEquals(expectedPrefix, nodeAttributeInfo.getString("prefix"));
            Assert.assertEquals(expectedName, nodeAttributeInfo.getString("name"));
            Assert.assertEquals(expectedType, nodeAttributeInfo.getString("type"));
            Assert.assertEquals(expectedValue, nodeAttributeInfo.getString("value"));
        }
    }
}

