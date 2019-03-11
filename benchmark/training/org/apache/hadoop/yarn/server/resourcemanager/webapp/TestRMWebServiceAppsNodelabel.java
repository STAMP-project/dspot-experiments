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
import YarnConfiguration.RM_SCHEDULER;
import com.google.common.collect.ImmutableMap;
import com.google.inject.servlet.GuiceFilter;
import com.google.inject.servlet.ServletModule;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.guice.spi.container.servlet.GuiceContainer;
import com.sun.jersey.test.framework.WebAppDescriptor;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import org.apache.hadoop.yarn.api.records.NodeId;
import org.apache.hadoop.yarn.api.records.NodeLabel;
import org.apache.hadoop.yarn.conf.YarnConfiguration;
import org.apache.hadoop.yarn.server.resourcemanager.MockAM;
import org.apache.hadoop.yarn.server.resourcemanager.MockNM;
import org.apache.hadoop.yarn.server.resourcemanager.MockRM;
import org.apache.hadoop.yarn.server.resourcemanager.ResourceManager;
import org.apache.hadoop.yarn.server.resourcemanager.nodelabels.RMNodeLabelsManager;
import org.apache.hadoop.yarn.server.resourcemanager.rmapp.RMApp;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.ResourceScheduler;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.capacity.CapacityScheduler;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.capacity.CapacitySchedulerConfiguration;
import org.apache.hadoop.yarn.webapp.GenericExceptionHandler;
import org.apache.hadoop.yarn.webapp.GuiceServletConfig;
import org.apache.hadoop.yarn.webapp.JerseyTestBase;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests partition resource usage per application.
 */
public class TestRMWebServiceAppsNodelabel extends JerseyTestBase {
    private static final int AM_CONTAINER_MB = 1024;

    private static RMNodeLabelsManager nodeLabelManager;

    private static MockRM rm;

    private static CapacitySchedulerConfiguration csConf;

    private static YarnConfiguration conf;

    private static class WebServletModule extends ServletModule {
        private static final String LABEL_X = "X";

        @Override
        protected void configureServlets() {
            bind(JAXBContextResolver.class);
            bind(RMWebServices.class);
            bind(GenericExceptionHandler.class);
            TestRMWebServiceAppsNodelabel.csConf = new CapacitySchedulerConfiguration();
            TestRMWebServiceAppsNodelabel.setupQueueConfiguration(TestRMWebServiceAppsNodelabel.csConf);
            TestRMWebServiceAppsNodelabel.conf = new YarnConfiguration(TestRMWebServiceAppsNodelabel.csConf);
            TestRMWebServiceAppsNodelabel.conf.setClass(RM_SCHEDULER, CapacityScheduler.class, ResourceScheduler.class);
            TestRMWebServiceAppsNodelabel.rm = new MockRM(TestRMWebServiceAppsNodelabel.conf);
            Set<NodeLabel> labels = new HashSet<NodeLabel>();
            labels.add(NodeLabel.newInstance(TestRMWebServiceAppsNodelabel.WebServletModule.LABEL_X));
            try {
                TestRMWebServiceAppsNodelabel.nodeLabelManager = getRMContext().getNodeLabelManager();
                TestRMWebServiceAppsNodelabel.nodeLabelManager.addToCluserNodeLabels(labels);
            } catch (Exception e) {
                Assert.fail();
            }
            bind(ResourceManager.class).toInstance(TestRMWebServiceAppsNodelabel.rm);
            serve("/*").with(GuiceContainer.class);
        }
    }

    public TestRMWebServiceAppsNodelabel() {
        super(new WebAppDescriptor.Builder("org.apache.hadoop.yarn.server.resourcemanager.webapp").contextListenerClass(GuiceServletConfig.class).filterClass(GuiceFilter.class).contextPath("jersey-guice-filter").servletPath("/").build());
    }

    @Test
    public void testAppsFinished() throws Exception, JSONException {
        start();
        MockNM amNodeManager = TestRMWebServiceAppsNodelabel.rm.registerNode("127.0.0.1:1234", 2048);
        amNodeManager.nodeHeartbeat(true);
        RMApp killedApp = TestRMWebServiceAppsNodelabel.rm.submitApp(TestRMWebServiceAppsNodelabel.AM_CONTAINER_MB);
        TestRMWebServiceAppsNodelabel.rm.killApp(killedApp.getApplicationId());
        WebResource r = resource();
        ClientResponse response = r.path("ws").path("v1").path("cluster").path("apps").accept(APPLICATION_JSON).get(ClientResponse.class);
        JSONObject json = response.getEntity(JSONObject.class);
        JSONObject apps = json.getJSONObject("apps");
        Assert.assertEquals("incorrect number of elements", 1, apps.length());
        try {
            apps.getJSONArray("app").getJSONObject(0).getJSONObject("resourceInfo");
            Assert.fail("resourceInfo object shouldn't be available for finished apps");
        } catch (Exception e) {
            Assert.assertTrue("resourceInfo shouldn't be available for finished apps", true);
        }
        stop();
    }

    @Test
    public void testAppsRunning() throws Exception, JSONException {
        start();
        MockNM nm1 = TestRMWebServiceAppsNodelabel.rm.registerNode("h1:1234", 2048);
        MockNM nm2 = TestRMWebServiceAppsNodelabel.rm.registerNode("h2:1235", 2048);
        TestRMWebServiceAppsNodelabel.nodeLabelManager.addLabelsToNode(ImmutableMap.of(NodeId.newInstance("h2", 1235), toSet("X")));
        RMApp app1 = TestRMWebServiceAppsNodelabel.rm.submitApp(TestRMWebServiceAppsNodelabel.AM_CONTAINER_MB, "app", "user", null, "default");
        MockAM am1 = MockRM.launchAndRegisterAM(app1, TestRMWebServiceAppsNodelabel.rm, nm1);
        nm1.nodeHeartbeat(true);
        // AM request for resource in partition X
        am1.allocate("*", 1024, 1, new ArrayList<org.apache.hadoop.yarn.api.records.ContainerId>(), "X");
        nm2.nodeHeartbeat(true);
        WebResource r = resource();
        ClientResponse response = r.path("ws").path("v1").path("cluster").path("apps").accept(APPLICATION_JSON).get(ClientResponse.class);
        JSONObject json = response.getEntity(JSONObject.class);
        // Verify apps resource
        JSONObject apps = json.getJSONObject("apps");
        Assert.assertEquals("incorrect number of elements", 1, apps.length());
        JSONObject jsonObject = apps.getJSONArray("app").getJSONObject(0).getJSONObject("resourceInfo");
        JSONArray jsonArray = jsonObject.getJSONArray("resourceUsagesByPartition");
        Assert.assertEquals("Partition expected is 2", 2, jsonArray.length());
        // Default partition resource
        JSONObject defaultPartition = jsonArray.getJSONObject(0);
        verifyResource(defaultPartition, "", getResource(1024, 1), getResource(1024, 1), getResource(0, 0));
        // verify resource used for parition x
        JSONObject paritionX = jsonArray.getJSONObject(1);
        verifyResource(paritionX, "X", getResource(0, 0), getResource(1024, 1), getResource(0, 0));
        stop();
    }
}

