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


import CapacitySchedulerConfiguration.CAPACITY;
import CapacitySchedulerConfiguration.DEFAULT_MAXIMUM_APPLICATIONMASTERS_RESOURCE_PERCENT;
import CapacitySchedulerConfiguration.DEFAULT_MAXIMUM_SYSTEM_APPLICATIIONS;
import CapacitySchedulerConfiguration.MAXIMUM_AM_RESOURCE_SUFFIX;
import CapacitySchedulerConfiguration.MAXIMUM_CAPACITY;
import MediaType.APPLICATION_JSON;
import Status.OK;
import YarnConfiguration.MEMORY_CONFIGURATION_STORE;
import YarnConfiguration.RM_SCHEDULER;
import YarnConfiguration.SCHEDULER_CONFIGURATION_STORE_CLASS;
import YarnConfiguration.YARN_ADMIN_ACL;
import com.google.inject.servlet.GuiceFilter;
import com.google.inject.servlet.ServletModule;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.guice.spi.container.servlet.GuiceContainer;
import com.sun.jersey.test.framework.WebAppDescriptor;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.security.UserGroupInformation;
import org.apache.hadoop.yarn.conf.YarnConfiguration;
import org.apache.hadoop.yarn.server.resourcemanager.MockRM;
import org.apache.hadoop.yarn.server.resourcemanager.ResourceManager;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.ResourceScheduler;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.capacity.CapacityScheduler;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.capacity.CapacitySchedulerConfiguration;
import org.apache.hadoop.yarn.webapp.GenericExceptionHandler;
import org.apache.hadoop.yarn.webapp.GuiceServletConfig;
import org.apache.hadoop.yarn.webapp.JerseyTestBase;
import org.apache.hadoop.yarn.webapp.dao.QueueConfigInfo;
import org.apache.hadoop.yarn.webapp.dao.SchedConfUpdateInfo;
import org.apache.hadoop.yarn.webapp.util.YarnWebServiceUtils;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Test scheduler configuration mutation via REST API.
 */
public class TestRMWebServicesConfigurationMutation extends JerseyTestBase {
    private static final Logger LOG = LoggerFactory.getLogger(TestRMWebServicesConfigurationMutation.class);

    private static final File CONF_FILE = new File(new File("target", "test-classes"), YarnConfiguration.CS_CONFIGURATION_FILE);

    private static final File OLD_CONF_FILE = new File(new File("target", "test-classes"), ((YarnConfiguration.CS_CONFIGURATION_FILE) + ".tmp"));

    private static MockRM rm;

    private static String userName;

    private static CapacitySchedulerConfiguration csConf;

    private static YarnConfiguration conf;

    private static class WebServletModule extends ServletModule {
        @Override
        protected void configureServlets() {
            bind(JAXBContextResolver.class);
            bind(RMWebServices.class);
            bind(GenericExceptionHandler.class);
            try {
                TestRMWebServicesConfigurationMutation.userName = UserGroupInformation.getCurrentUser().getShortUserName();
            } catch (IOException ioe) {
                throw new RuntimeException(("Unable to get current user name " + (ioe.getMessage())), ioe);
            }
            TestRMWebServicesConfigurationMutation.csConf = new CapacitySchedulerConfiguration(new Configuration(false), false);
            TestRMWebServicesConfigurationMutation.setupQueueConfiguration(TestRMWebServicesConfigurationMutation.csConf);
            TestRMWebServicesConfigurationMutation.conf = new YarnConfiguration();
            TestRMWebServicesConfigurationMutation.conf.setClass(RM_SCHEDULER, CapacityScheduler.class, ResourceScheduler.class);
            TestRMWebServicesConfigurationMutation.conf.set(SCHEDULER_CONFIGURATION_STORE_CLASS, MEMORY_CONFIGURATION_STORE);
            TestRMWebServicesConfigurationMutation.conf.set(YARN_ADMIN_ACL, TestRMWebServicesConfigurationMutation.userName);
            try {
                if (TestRMWebServicesConfigurationMutation.CONF_FILE.exists()) {
                    if (!(TestRMWebServicesConfigurationMutation.CONF_FILE.renameTo(TestRMWebServicesConfigurationMutation.OLD_CONF_FILE))) {
                        throw new RuntimeException("Failed to rename conf file");
                    }
                }
                FileOutputStream out = new FileOutputStream(TestRMWebServicesConfigurationMutation.CONF_FILE);
                TestRMWebServicesConfigurationMutation.csConf.writeXml(out);
                out.close();
            } catch (IOException e) {
                throw new RuntimeException("Failed to write XML file", e);
            }
            TestRMWebServicesConfigurationMutation.rm = new MockRM(TestRMWebServicesConfigurationMutation.conf);
            bind(ResourceManager.class).toInstance(TestRMWebServicesConfigurationMutation.rm);
            serve("/*").with(GuiceContainer.class);
            filter("/*").through(TestRMWebServicesAppsModification.TestRMCustomAuthFilter.class);
        }
    }

    public TestRMWebServicesConfigurationMutation() {
        super(new WebAppDescriptor.Builder("org.apache.hadoop.yarn.server.resourcemanager.webapp").contextListenerClass(GuiceServletConfig.class).filterClass(GuiceFilter.class).contextPath("jersey-guice-filter").servletPath("/").build());
    }

    @Test
    public void testGetSchedulerConf() throws Exception {
        CapacitySchedulerConfiguration orgConf = getSchedulerConf();
        Assert.assertNotNull(orgConf);
        Assert.assertEquals(3, orgConf.getQueues("root").length);
    }

    @Test
    public void testAddNestedQueue() throws Exception {
        CapacitySchedulerConfiguration orgConf = getSchedulerConf();
        Assert.assertNotNull(orgConf);
        Assert.assertEquals(3, orgConf.getQueues("root").length);
        WebResource r = resource();
        ClientResponse response;
        // Add parent queue root.d with two children d1 and d2.
        SchedConfUpdateInfo updateInfo = new SchedConfUpdateInfo();
        Map<String, String> d1Capacity = new HashMap<>();
        d1Capacity.put(CAPACITY, "25");
        d1Capacity.put(MAXIMUM_CAPACITY, "25");
        Map<String, String> nearEmptyCapacity = new HashMap<>();
        nearEmptyCapacity.put(CAPACITY, "1E-4");
        nearEmptyCapacity.put(MAXIMUM_CAPACITY, "1E-4");
        Map<String, String> d2Capacity = new HashMap<>();
        d2Capacity.put(CAPACITY, "75");
        d2Capacity.put(MAXIMUM_CAPACITY, "75");
        QueueConfigInfo d1 = new QueueConfigInfo("root.d.d1", d1Capacity);
        QueueConfigInfo d2 = new QueueConfigInfo("root.d.d2", d2Capacity);
        QueueConfigInfo d = new QueueConfigInfo("root.d", nearEmptyCapacity);
        updateInfo.getAddQueueInfo().add(d1);
        updateInfo.getAddQueueInfo().add(d2);
        updateInfo.getAddQueueInfo().add(d);
        response = r.path("ws").path("v1").path("cluster").path("scheduler-conf").queryParam("user.name", TestRMWebServicesConfigurationMutation.userName).accept(APPLICATION_JSON).entity(YarnWebServiceUtils.toJson(updateInfo, SchedConfUpdateInfo.class), APPLICATION_JSON).put(ClientResponse.class);
        Assert.assertEquals(OK.getStatusCode(), response.getStatus());
        CapacitySchedulerConfiguration newCSConf = getConfiguration();
        Assert.assertEquals(4, newCSConf.getQueues("root").length);
        Assert.assertEquals(2, newCSConf.getQueues("root.d").length);
        Assert.assertEquals(25.0F, newCSConf.getNonLabeledQueueCapacity("root.d.d1"), 0.01F);
        Assert.assertEquals(75.0F, newCSConf.getNonLabeledQueueCapacity("root.d.d2"), 0.01F);
        CapacitySchedulerConfiguration newConf = getSchedulerConf();
        Assert.assertNotNull(newConf);
        Assert.assertEquals(4, newConf.getQueues("root").length);
    }

    @Test
    public void testAddWithUpdate() throws Exception {
        WebResource r = resource();
        ClientResponse response;
        // Add root.d with capacity 25, reducing root.b capacity from 75 to 50.
        SchedConfUpdateInfo updateInfo = new SchedConfUpdateInfo();
        Map<String, String> dCapacity = new HashMap<>();
        dCapacity.put(CAPACITY, "25");
        Map<String, String> bCapacity = new HashMap<>();
        bCapacity.put(CAPACITY, "50");
        QueueConfigInfo d = new QueueConfigInfo("root.d", dCapacity);
        QueueConfigInfo b = new QueueConfigInfo("root.b", bCapacity);
        updateInfo.getAddQueueInfo().add(d);
        updateInfo.getUpdateQueueInfo().add(b);
        response = r.path("ws").path("v1").path("cluster").path("scheduler-conf").queryParam("user.name", TestRMWebServicesConfigurationMutation.userName).accept(APPLICATION_JSON).entity(YarnWebServiceUtils.toJson(updateInfo, SchedConfUpdateInfo.class), APPLICATION_JSON).put(ClientResponse.class);
        Assert.assertEquals(OK.getStatusCode(), response.getStatus());
        CapacitySchedulerConfiguration newCSConf = getConfiguration();
        Assert.assertEquals(4, newCSConf.getQueues("root").length);
        Assert.assertEquals(25.0F, newCSConf.getNonLabeledQueueCapacity("root.d"), 0.01F);
        Assert.assertEquals(50.0F, newCSConf.getNonLabeledQueueCapacity("root.b"), 0.01F);
    }

    @Test
    public void testRemoveQueue() throws Exception {
        WebResource r = resource();
        ClientResponse response;
        stopQueue("root.a.a2");
        // Remove root.a.a2
        SchedConfUpdateInfo updateInfo = new SchedConfUpdateInfo();
        updateInfo.getRemoveQueueInfo().add("root.a.a2");
        response = r.path("ws").path("v1").path("cluster").path("scheduler-conf").queryParam("user.name", TestRMWebServicesConfigurationMutation.userName).accept(APPLICATION_JSON).entity(YarnWebServiceUtils.toJson(updateInfo, SchedConfUpdateInfo.class), APPLICATION_JSON).put(ClientResponse.class);
        Assert.assertEquals(OK.getStatusCode(), response.getStatus());
        CapacitySchedulerConfiguration newCSConf = getConfiguration();
        Assert.assertEquals(1, newCSConf.getQueues("root.a").length);
        Assert.assertEquals("a1", newCSConf.getQueues("root.a")[0]);
    }

    @Test
    public void testRemoveParentQueue() throws Exception {
        WebResource r = resource();
        ClientResponse response;
        stopQueue("root.c", "root.c.c1");
        // Remove root.c (parent queue)
        SchedConfUpdateInfo updateInfo = new SchedConfUpdateInfo();
        updateInfo.getRemoveQueueInfo().add("root.c");
        response = r.path("ws").path("v1").path("cluster").path("scheduler-conf").queryParam("user.name", TestRMWebServicesConfigurationMutation.userName).accept(APPLICATION_JSON).entity(YarnWebServiceUtils.toJson(updateInfo, SchedConfUpdateInfo.class), APPLICATION_JSON).put(ClientResponse.class);
        Assert.assertEquals(OK.getStatusCode(), response.getStatus());
        CapacitySchedulerConfiguration newCSConf = getConfiguration();
        Assert.assertEquals(2, newCSConf.getQueues("root").length);
        Assert.assertNull(newCSConf.getQueues("root.c"));
    }

    @Test
    public void testRemoveParentQueueWithCapacity() throws Exception {
        WebResource r = resource();
        ClientResponse response;
        stopQueue("root.a", "root.a.a1", "root.a.a2");
        // Remove root.a (parent queue) with capacity 25
        SchedConfUpdateInfo updateInfo = new SchedConfUpdateInfo();
        updateInfo.getRemoveQueueInfo().add("root.a");
        // Set root.b capacity to 100
        Map<String, String> bCapacity = new HashMap<>();
        bCapacity.put(CAPACITY, "100");
        QueueConfigInfo b = new QueueConfigInfo("root.b", bCapacity);
        updateInfo.getUpdateQueueInfo().add(b);
        response = r.path("ws").path("v1").path("cluster").path("scheduler-conf").queryParam("user.name", TestRMWebServicesConfigurationMutation.userName).accept(APPLICATION_JSON).entity(YarnWebServiceUtils.toJson(updateInfo, SchedConfUpdateInfo.class), APPLICATION_JSON).put(ClientResponse.class);
        Assert.assertEquals(OK.getStatusCode(), response.getStatus());
        CapacitySchedulerConfiguration newCSConf = getConfiguration();
        Assert.assertEquals(2, newCSConf.getQueues("root").length);
        Assert.assertEquals(100.0F, newCSConf.getNonLabeledQueueCapacity("root.b"), 0.01F);
    }

    @Test
    public void testRemoveMultipleQueues() throws Exception {
        WebResource r = resource();
        ClientResponse response;
        stopQueue("root.b", "root.c", "root.c.c1");
        // Remove root.b and root.c
        SchedConfUpdateInfo updateInfo = new SchedConfUpdateInfo();
        updateInfo.getRemoveQueueInfo().add("root.b");
        updateInfo.getRemoveQueueInfo().add("root.c");
        Map<String, String> aCapacity = new HashMap<>();
        aCapacity.put(CAPACITY, "100");
        aCapacity.put(MAXIMUM_CAPACITY, "100");
        QueueConfigInfo configInfo = new QueueConfigInfo("root.a", aCapacity);
        updateInfo.getUpdateQueueInfo().add(configInfo);
        response = r.path("ws").path("v1").path("cluster").path("scheduler-conf").queryParam("user.name", TestRMWebServicesConfigurationMutation.userName).accept(APPLICATION_JSON).entity(YarnWebServiceUtils.toJson(updateInfo, SchedConfUpdateInfo.class), APPLICATION_JSON).put(ClientResponse.class);
        Assert.assertEquals(OK.getStatusCode(), response.getStatus());
        CapacitySchedulerConfiguration newCSConf = getConfiguration();
        Assert.assertEquals(1, newCSConf.getQueues("root").length);
    }

    @Test
    public void testUpdateQueue() throws Exception {
        WebResource r = resource();
        ClientResponse response;
        // Update config value.
        SchedConfUpdateInfo updateInfo = new SchedConfUpdateInfo();
        Map<String, String> updateParam = new HashMap<>();
        updateParam.put(MAXIMUM_AM_RESOURCE_SUFFIX, "0.2");
        QueueConfigInfo aUpdateInfo = new QueueConfigInfo("root.a", updateParam);
        updateInfo.getUpdateQueueInfo().add(aUpdateInfo);
        CapacityScheduler cs = ((CapacityScheduler) (getResourceScheduler()));
        Assert.assertEquals(DEFAULT_MAXIMUM_APPLICATIONMASTERS_RESOURCE_PERCENT, cs.getConfiguration().getMaximumApplicationMasterResourcePerQueuePercent("root.a"), 0.001F);
        response = r.path("ws").path("v1").path("cluster").path("scheduler-conf").queryParam("user.name", TestRMWebServicesConfigurationMutation.userName).accept(APPLICATION_JSON).entity(YarnWebServiceUtils.toJson(updateInfo, SchedConfUpdateInfo.class), APPLICATION_JSON).put(ClientResponse.class);
        TestRMWebServicesConfigurationMutation.LOG.debug(("Response headers: " + (response.getHeaders())));
        Assert.assertEquals(OK.getStatusCode(), response.getStatus());
        CapacitySchedulerConfiguration newCSConf = cs.getConfiguration();
        Assert.assertEquals(0.2F, newCSConf.getMaximumApplicationMasterResourcePerQueuePercent("root.a"), 0.001F);
        // Remove config. Config value should be reverted to default.
        updateParam.put(MAXIMUM_AM_RESOURCE_SUFFIX, null);
        aUpdateInfo = new QueueConfigInfo("root.a", updateParam);
        updateInfo.getUpdateQueueInfo().clear();
        updateInfo.getUpdateQueueInfo().add(aUpdateInfo);
        response = r.path("ws").path("v1").path("cluster").path("scheduler-conf").queryParam("user.name", TestRMWebServicesConfigurationMutation.userName).accept(APPLICATION_JSON).entity(YarnWebServiceUtils.toJson(updateInfo, SchedConfUpdateInfo.class), APPLICATION_JSON).put(ClientResponse.class);
        Assert.assertEquals(OK.getStatusCode(), response.getStatus());
        newCSConf = cs.getConfiguration();
        Assert.assertEquals(DEFAULT_MAXIMUM_APPLICATIONMASTERS_RESOURCE_PERCENT, newCSConf.getMaximumApplicationMasterResourcePerQueuePercent("root.a"), 0.001F);
    }

    @Test
    public void testUpdateQueueCapacity() throws Exception {
        WebResource r = resource();
        ClientResponse response;
        // Update root.a and root.b capacity to 50.
        SchedConfUpdateInfo updateInfo = new SchedConfUpdateInfo();
        Map<String, String> updateParam = new HashMap<>();
        updateParam.put(CAPACITY, "50");
        QueueConfigInfo aUpdateInfo = new QueueConfigInfo("root.a", updateParam);
        QueueConfigInfo bUpdateInfo = new QueueConfigInfo("root.b", updateParam);
        updateInfo.getUpdateQueueInfo().add(aUpdateInfo);
        updateInfo.getUpdateQueueInfo().add(bUpdateInfo);
        response = r.path("ws").path("v1").path("cluster").path("scheduler-conf").queryParam("user.name", TestRMWebServicesConfigurationMutation.userName).accept(APPLICATION_JSON).entity(YarnWebServiceUtils.toJson(updateInfo, SchedConfUpdateInfo.class), APPLICATION_JSON).put(ClientResponse.class);
        Assert.assertEquals(OK.getStatusCode(), response.getStatus());
        CapacitySchedulerConfiguration newCSConf = getConfiguration();
        Assert.assertEquals(50.0F, newCSConf.getNonLabeledQueueCapacity("root.a"), 0.01F);
        Assert.assertEquals(50.0F, newCSConf.getNonLabeledQueueCapacity("root.b"), 0.01F);
    }

    @Test
    public void testGlobalConfChange() throws Exception {
        WebResource r = resource();
        ClientResponse response;
        // Set maximum-applications to 30000.
        SchedConfUpdateInfo updateInfo = new SchedConfUpdateInfo();
        updateInfo.getGlobalParams().put(((CapacitySchedulerConfiguration.PREFIX) + "maximum-applications"), "30000");
        response = r.path("ws").path("v1").path("cluster").path("scheduler-conf").queryParam("user.name", TestRMWebServicesConfigurationMutation.userName).accept(APPLICATION_JSON).entity(YarnWebServiceUtils.toJson(updateInfo, SchedConfUpdateInfo.class), APPLICATION_JSON).put(ClientResponse.class);
        Assert.assertEquals(OK.getStatusCode(), response.getStatus());
        CapacitySchedulerConfiguration newCSConf = getConfiguration();
        Assert.assertEquals(30000, newCSConf.getMaximumSystemApplications());
        updateInfo.getGlobalParams().put(((CapacitySchedulerConfiguration.PREFIX) + "maximum-applications"), null);
        // Unset maximum-applications. Should be set to default.
        response = r.path("ws").path("v1").path("cluster").path("scheduler-conf").queryParam("user.name", TestRMWebServicesConfigurationMutation.userName).accept(APPLICATION_JSON).entity(YarnWebServiceUtils.toJson(updateInfo, SchedConfUpdateInfo.class), APPLICATION_JSON).put(ClientResponse.class);
        Assert.assertEquals(OK.getStatusCode(), response.getStatus());
        newCSConf = ((CapacityScheduler) (getResourceScheduler())).getConfiguration();
        Assert.assertEquals(DEFAULT_MAXIMUM_SYSTEM_APPLICATIIONS, newCSConf.getMaximumSystemApplications());
    }
}

