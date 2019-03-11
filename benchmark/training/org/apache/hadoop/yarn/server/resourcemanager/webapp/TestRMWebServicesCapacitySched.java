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


import MediaType.APPLICATION_JSON;
import MediaType.APPLICATION_XML;
import YarnConfiguration.RM_SCHEDULER;
import com.google.inject.Guice;
import com.google.inject.servlet.GuiceFilter;
import com.google.inject.servlet.ServletModule;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.guice.spi.container.servlet.GuiceContainer;
import com.sun.jersey.test.framework.WebAppDescriptor;
import java.io.StringReader;
import javax.ws.rs.core.MediaType;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.apache.hadoop.http.JettyUtils;
import org.apache.hadoop.yarn.api.records.Resource;
import org.apache.hadoop.yarn.conf.YarnConfiguration;
import org.apache.hadoop.yarn.server.resourcemanager.MockRM;
import org.apache.hadoop.yarn.server.resourcemanager.ResourceManager;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.ResourceScheduler;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.capacity.CapacityScheduler;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.capacity.CapacitySchedulerConfiguration;
import org.apache.hadoop.yarn.util.resource.Resources;
import org.apache.hadoop.yarn.webapp.GenericExceptionHandler;
import org.apache.hadoop.yarn.webapp.GuiceServletConfig;
import org.apache.hadoop.yarn.webapp.JerseyTestBase;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;


public class TestRMWebServicesCapacitySched extends JerseyTestBase {
    protected static MockRM rm;

    protected static CapacitySchedulerConfiguration csConf;

    protected static YarnConfiguration conf;

    private class QueueInfo {
        float capacity;

        float usedCapacity;

        float maxCapacity;

        float absoluteCapacity;

        float absoluteMaxCapacity;

        float absoluteUsedCapacity;

        int numApplications;

        String queueName;

        String state;
    }

    private class LeafQueueInfo extends TestRMWebServicesCapacitySched.QueueInfo {
        int numActiveApplications;

        int numPendingApplications;

        int numContainers;

        int maxApplications;

        int maxApplicationsPerUser;

        int userLimit;

        float userLimitFactor;
    }

    private static class WebServletModule extends ServletModule {
        @Override
        protected void configureServlets() {
            bind(JAXBContextResolver.class);
            bind(RMWebServices.class);
            bind(GenericExceptionHandler.class);
            TestRMWebServicesCapacitySched.csConf = new CapacitySchedulerConfiguration();
            TestRMWebServicesCapacitySched.setupQueueConfiguration(TestRMWebServicesCapacitySched.csConf);
            TestRMWebServicesCapacitySched.conf = new YarnConfiguration(TestRMWebServicesCapacitySched.csConf);
            TestRMWebServicesCapacitySched.conf.setClass(RM_SCHEDULER, CapacityScheduler.class, ResourceScheduler.class);
            TestRMWebServicesCapacitySched.rm = new MockRM(TestRMWebServicesCapacitySched.conf);
            bind(ResourceManager.class).toInstance(TestRMWebServicesCapacitySched.rm);
            serve("/*").with(GuiceContainer.class);
        }
    }

    static {
        GuiceServletConfig.setInjector(Guice.createInjector(new TestRMWebServicesCapacitySched.WebServletModule()));
    }

    public TestRMWebServicesCapacitySched() {
        super(new WebAppDescriptor.Builder("org.apache.hadoop.yarn.server.resourcemanager.webapp").contextListenerClass(GuiceServletConfig.class).filterClass(GuiceFilter.class).contextPath("jersey-guice-filter").servletPath("/").build());
    }

    @Test
    public void testClusterScheduler() throws Exception, JSONException {
        WebResource r = resource();
        ClientResponse response = r.path("ws").path("v1").path("cluster").path("scheduler").accept(APPLICATION_JSON).get(ClientResponse.class);
        Assert.assertEquals((((MediaType.APPLICATION_JSON_TYPE) + "; ") + (JettyUtils.UTF_8)), response.getType().toString());
        JSONObject json = response.getEntity(JSONObject.class);
        verifyClusterScheduler(json);
    }

    @Test
    public void testClusterSchedulerSlash() throws Exception, JSONException {
        WebResource r = resource();
        ClientResponse response = r.path("ws").path("v1").path("cluster").path("scheduler/").accept(APPLICATION_JSON).get(ClientResponse.class);
        Assert.assertEquals((((MediaType.APPLICATION_JSON_TYPE) + "; ") + (JettyUtils.UTF_8)), response.getType().toString());
        JSONObject json = response.getEntity(JSONObject.class);
        verifyClusterScheduler(json);
    }

    @Test
    public void testClusterSchedulerDefault() throws Exception, JSONException {
        WebResource r = resource();
        ClientResponse response = r.path("ws").path("v1").path("cluster").path("scheduler").get(ClientResponse.class);
        Assert.assertEquals((((MediaType.APPLICATION_JSON_TYPE) + "; ") + (JettyUtils.UTF_8)), response.getType().toString());
        JSONObject json = response.getEntity(JSONObject.class);
        verifyClusterScheduler(json);
    }

    @Test
    public void testClusterSchedulerXML() throws Exception, JSONException {
        WebResource r = resource();
        ClientResponse response = r.path("ws").path("v1").path("cluster").path("scheduler/").accept(APPLICATION_XML).get(ClientResponse.class);
        Assert.assertEquals((((MediaType.APPLICATION_XML_TYPE) + "; ") + (JettyUtils.UTF_8)), response.getType().toString());
        String xml = response.getEntity(String.class);
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        InputSource is = new InputSource();
        is.setCharacterStream(new StringReader(xml));
        Document dom = db.parse(is);
        NodeList scheduler = dom.getElementsByTagName("scheduler");
        Assert.assertEquals("incorrect number of elements", 1, scheduler.getLength());
        NodeList schedulerInfo = dom.getElementsByTagName("schedulerInfo");
        Assert.assertEquals("incorrect number of elements", 1, schedulerInfo.getLength());
        verifyClusterSchedulerXML(schedulerInfo);
    }

    /**
     * Test per user resources and resourcesUsed elements in the web services XML
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testPerUserResourcesXML() throws Exception {
        // Start RM so that it accepts app submissions
        start();
        try {
            TestRMWebServicesCapacitySched.rm.submitApp(10, "app1", "user1", null, "b1");
            TestRMWebServicesCapacitySched.rm.submitApp(20, "app2", "user2", null, "b1");
            // Get the XML from ws/v1/cluster/scheduler
            WebResource r = resource();
            ClientResponse response = r.path("ws/v1/cluster/scheduler").accept(APPLICATION_XML).get(ClientResponse.class);
            Assert.assertEquals((((MediaType.APPLICATION_XML_TYPE) + "; ") + (JettyUtils.UTF_8)), response.getType().toString());
            String xml = response.getEntity(String.class);
            DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            InputSource is = new InputSource();
            is.setCharacterStream(new StringReader(xml));
            // Parse the XML we got
            Document dom = db.parse(is);
            // Get all users elements (1 for each leaf queue)
            NodeList allUsers = dom.getElementsByTagName("users");
            for (int i = 0; i < (allUsers.getLength()); ++i) {
                Node perUserResources = allUsers.item(i);
                String queueName = getChildNodeByName(perUserResources.getParentNode(), "queueName").getTextContent();
                if (queueName.equals("b1")) {
                    // b1 should have two users (user1 and user2) which submitted jobs
                    Assert.assertEquals(2, perUserResources.getChildNodes().getLength());
                    NodeList users = perUserResources.getChildNodes();
                    for (int j = 0; j < (users.getLength()); ++j) {
                        Node user = users.item(j);
                        String username = getChildNodeByName(user, "username").getTextContent();
                        Assert.assertTrue(((username.equals("user1")) || (username.equals("user2"))));
                        // Should be a parsable integer
                        Integer.parseInt(getChildNodeByName(getChildNodeByName(user, "resourcesUsed"), "memory").getTextContent());
                        Integer.parseInt(getChildNodeByName(user, "numActiveApplications").getTextContent());
                        Integer.parseInt(getChildNodeByName(user, "numPendingApplications").getTextContent());
                    }
                } else {
                    // Queues other than b1 should have 0 users
                    Assert.assertEquals(0, perUserResources.getChildNodes().getLength());
                }
            }
            NodeList allResourcesUsed = dom.getElementsByTagName("resourcesUsed");
            for (int i = 0; i < (allResourcesUsed.getLength()); ++i) {
                Node resourcesUsed = allResourcesUsed.item(i);
                Integer.parseInt(getChildNodeByName(resourcesUsed, "memory").getTextContent());
                Integer.parseInt(getChildNodeByName(resourcesUsed, "vCores").getTextContent());
            }
        } finally {
            stop();
        }
    }

    @Test
    public void testPerUserResourcesJSON() throws Exception {
        // Start RM so that it accepts app submissions
        start();
        try {
            TestRMWebServicesCapacitySched.rm.submitApp(10, "app1", "user1", null, "b1");
            TestRMWebServicesCapacitySched.rm.submitApp(20, "app2", "user2", null, "b1");
            // Get JSON
            WebResource r = resource();
            ClientResponse response = r.path("ws").path("v1").path("cluster").path("scheduler/").accept(APPLICATION_JSON).get(ClientResponse.class);
            Assert.assertEquals((((MediaType.APPLICATION_JSON_TYPE) + "; ") + (JettyUtils.UTF_8)), response.getType().toString());
            JSONObject json = response.getEntity(JSONObject.class);
            JSONObject schedulerInfo = json.getJSONObject("scheduler").getJSONObject("schedulerInfo");
            JSONObject b1 = getSubQueue(getSubQueue(schedulerInfo, "b"), "b1");
            // Check users user1 and user2 exist in b1
            JSONArray users = b1.getJSONObject("users").getJSONArray("user");
            for (int i = 0; i < 2; ++i) {
                JSONObject user = users.getJSONObject(i);
                Assert.assertTrue("User isn't user1 or user2", ((user.getString("username").equals("user1")) || (user.getString("username").equals("user2"))));
                user.getInt("numActiveApplications");
                user.getInt("numPendingApplications");
                checkResourcesUsed(user);
            }
            // Verify 'queues' field is omitted from CapacitySchedulerLeafQueueInfo.
            try {
                b1.getJSONObject("queues");
                Assert.fail(("CapacitySchedulerQueueInfo should omit field 'queues'" + "if child queue is empty."));
            } catch (JSONException je) {
                Assert.assertEquals("JSONObject[\"queues\"] not found.", je.getMessage());
            }
        } finally {
            stop();
        }
    }

    @Test
    public void testResourceInfo() {
        Resource res = Resources.createResource(10, 1);
        // If we add a new resource (e.g disks), then
        // CapacitySchedulerPage and these RM WebServices + docs need to be updated
        // eg. ResourceInfo
        Assert.assertEquals("<memory:10, vCores:1>", res.toString());
    }
}

