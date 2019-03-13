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
import MediaType.TEXT_PLAIN;
import QueueACL.ADMINISTER_QUEUE;
import QueueACL.SUBMIT_APPLICATIONS;
import Status.INTERNAL_SERVER_ERROR;
import Status.NOT_FOUND;
import YarnConfiguration.RM_SCHEDULER;
import YarnConfiguration.YARN_ACL_ENABLE;
import YarnConfiguration.YARN_ADMIN_ACL;
import com.google.common.collect.ImmutableSet;
import com.google.inject.Guice;
import com.google.inject.servlet.GuiceFilter;
import com.google.inject.servlet.ServletModule;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.guice.spi.container.servlet.GuiceContainer;
import com.sun.jersey.test.framework.WebAppDescriptor;
import java.security.Principal;
import java.util.Arrays;
import java.util.Collections;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.MediaType;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.http.JettyUtils;
import org.apache.hadoop.security.UserGroupInformation;
import org.apache.hadoop.yarn.api.protocolrecords.GetApplicationsRequest;
import org.apache.hadoop.yarn.api.protocolrecords.GetApplicationsResponse;
import org.apache.hadoop.yarn.api.records.ApplicationId;
import org.apache.hadoop.yarn.api.records.ApplicationReport;
import org.apache.hadoop.yarn.api.records.QueueACL;
import org.apache.hadoop.yarn.conf.YarnConfiguration;
import org.apache.hadoop.yarn.server.resourcemanager.MockRM;
import org.apache.hadoop.yarn.server.resourcemanager.ResourceManager;
import org.apache.hadoop.yarn.server.resourcemanager.nodelabels.RMNodeLabelsManager;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.ResourceScheduler;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.fifo.FifoScheduler;
import org.apache.hadoop.yarn.server.resourcemanager.webapp.dao.AppsInfo;
import org.apache.hadoop.yarn.server.resourcemanager.webapp.dao.ClusterUserInfo;
import org.apache.hadoop.yarn.server.security.ApplicationACLsManager;
import org.apache.hadoop.yarn.webapp.BadRequestException;
import org.apache.hadoop.yarn.webapp.ForbiddenException;
import org.apache.hadoop.yarn.webapp.GenericExceptionHandler;
import org.apache.hadoop.yarn.webapp.GuiceServletConfig;
import org.apache.hadoop.yarn.webapp.JerseyTestBase;
import org.apache.hadoop.yarn.webapp.WebServicesTestUtils;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class TestRMWebServices extends JerseyTestBase {
    private static final Logger LOG = LoggerFactory.getLogger(TestRMWebServices.class);

    private static MockRM rm;

    private static class WebServletModule extends ServletModule {
        @Override
        protected void configureServlets() {
            bind(JAXBContextResolver.class);
            bind(RMWebServices.class);
            bind(GenericExceptionHandler.class);
            Configuration conf = new Configuration();
            conf.setClass(RM_SCHEDULER, FifoScheduler.class, ResourceScheduler.class);
            TestRMWebServices.rm = new MockRM(conf);
            bind(ResourceManager.class).toInstance(TestRMWebServices.rm);
            serve("/*").with(GuiceContainer.class);
        }
    }

    static {
        GuiceServletConfig.setInjector(Guice.createInjector(new TestRMWebServices.WebServletModule()));
    }

    public TestRMWebServices() {
        super(new WebAppDescriptor.Builder("org.apache.hadoop.yarn.server.resourcemanager.webapp").contextListenerClass(GuiceServletConfig.class).filterClass(GuiceFilter.class).contextPath("jersey-guice-filter").servletPath("/").build());
    }

    @Test
    public void testInfoXML() throws Exception, JSONException {
        WebResource r = resource();
        ClientResponse response = r.path("ws").path("v1").path("cluster").path("info").accept("application/xml").get(ClientResponse.class);
        Assert.assertEquals((((MediaType.APPLICATION_XML) + "; ") + (JettyUtils.UTF_8)), response.getType().toString());
        String xml = response.getEntity(String.class);
        verifyClusterInfoXML(xml);
    }

    @Test
    public void testInvalidUri() throws Exception, JSONException {
        WebResource r = resource();
        String responseStr = "";
        try {
            responseStr = r.path("ws").path("v1").path("cluster").path("bogus").accept(APPLICATION_JSON).get(String.class);
            Assert.fail("should have thrown exception on invalid uri");
        } catch (UniformInterfaceException ue) {
            ClientResponse response = ue.getResponse();
            WebServicesTestUtils.assertResponseStatusCode(NOT_FOUND, response.getStatusInfo());
            WebServicesTestUtils.checkStringMatch("error string exists and shouldn't", "", responseStr);
        }
    }

    @Test
    public void testInvalidUri2() throws Exception, JSONException {
        WebResource r = resource();
        String responseStr = "";
        try {
            responseStr = r.accept(APPLICATION_JSON).get(String.class);
            Assert.fail("should have thrown exception on invalid uri");
        } catch (UniformInterfaceException ue) {
            ClientResponse response = ue.getResponse();
            WebServicesTestUtils.assertResponseStatusCode(NOT_FOUND, response.getStatusInfo());
            WebServicesTestUtils.checkStringMatch("error string exists and shouldn't", "", responseStr);
        }
    }

    @Test
    public void testInvalidAccept() throws Exception, JSONException {
        WebResource r = resource();
        String responseStr = "";
        try {
            responseStr = r.path("ws").path("v1").path("cluster").accept(TEXT_PLAIN).get(String.class);
            Assert.fail("should have thrown exception on invalid uri");
        } catch (UniformInterfaceException ue) {
            ClientResponse response = ue.getResponse();
            WebServicesTestUtils.assertResponseStatusCode(INTERNAL_SERVER_ERROR, response.getStatusInfo());
            WebServicesTestUtils.checkStringMatch("error string exists and shouldn't", "", responseStr);
        }
    }

    @Test
    public void testCluster() throws Exception, JSONException {
        WebResource r = resource();
        ClientResponse response = r.path("ws").path("v1").path("cluster").accept(APPLICATION_JSON).get(ClientResponse.class);
        Assert.assertEquals((((MediaType.APPLICATION_JSON) + "; ") + (JettyUtils.UTF_8)), response.getType().toString());
        JSONObject json = response.getEntity(JSONObject.class);
        verifyClusterInfo(json);
    }

    @Test
    public void testClusterSlash() throws Exception, JSONException {
        WebResource r = resource();
        // test with trailing "/" to make sure acts same as without slash
        ClientResponse response = r.path("ws").path("v1").path("cluster/").accept(APPLICATION_JSON).get(ClientResponse.class);
        Assert.assertEquals((((MediaType.APPLICATION_JSON) + "; ") + (JettyUtils.UTF_8)), response.getType().toString());
        JSONObject json = response.getEntity(JSONObject.class);
        verifyClusterInfo(json);
    }

    @Test
    public void testClusterDefault() throws Exception, JSONException {
        WebResource r = resource();
        // test with trailing "/" to make sure acts same as without slash
        ClientResponse response = r.path("ws").path("v1").path("cluster").get(ClientResponse.class);
        Assert.assertEquals((((MediaType.APPLICATION_JSON) + "; ") + (JettyUtils.UTF_8)), response.getType().toString());
        JSONObject json = response.getEntity(JSONObject.class);
        verifyClusterInfo(json);
    }

    @Test
    public void testInfo() throws Exception, JSONException {
        WebResource r = resource();
        ClientResponse response = r.path("ws").path("v1").path("cluster").path("info").accept(APPLICATION_JSON).get(ClientResponse.class);
        Assert.assertEquals((((MediaType.APPLICATION_JSON) + "; ") + (JettyUtils.UTF_8)), response.getType().toString());
        JSONObject json = response.getEntity(JSONObject.class);
        verifyClusterInfo(json);
    }

    @Test
    public void testInfoSlash() throws Exception, JSONException {
        // test with trailing "/" to make sure acts same as without slash
        WebResource r = resource();
        ClientResponse response = r.path("ws").path("v1").path("cluster").path("info/").accept(APPLICATION_JSON).get(ClientResponse.class);
        Assert.assertEquals((((MediaType.APPLICATION_JSON) + "; ") + (JettyUtils.UTF_8)), response.getType().toString());
        JSONObject json = response.getEntity(JSONObject.class);
        verifyClusterInfo(json);
    }

    @Test
    public void testInfoDefault() throws Exception, JSONException {
        WebResource r = resource();
        ClientResponse response = r.path("ws").path("v1").path("cluster").path("info").get(ClientResponse.class);
        Assert.assertEquals((((MediaType.APPLICATION_JSON) + "; ") + (JettyUtils.UTF_8)), response.getType().toString());
        JSONObject json = response.getEntity(JSONObject.class);
        verifyClusterInfo(json);
    }

    @Test
    public void testClusterMetrics() throws Exception, JSONException {
        WebResource r = resource();
        ClientResponse response = r.path("ws").path("v1").path("cluster").path("metrics").accept(APPLICATION_JSON).get(ClientResponse.class);
        Assert.assertEquals((((MediaType.APPLICATION_JSON) + "; ") + (JettyUtils.UTF_8)), response.getType().toString());
        JSONObject json = response.getEntity(JSONObject.class);
        verifyClusterMetricsJSON(json);
    }

    @Test
    public void testClusterMetricsSlash() throws Exception, JSONException {
        WebResource r = resource();
        ClientResponse response = r.path("ws").path("v1").path("cluster").path("metrics/").accept(APPLICATION_JSON).get(ClientResponse.class);
        Assert.assertEquals((((MediaType.APPLICATION_JSON) + "; ") + (JettyUtils.UTF_8)), response.getType().toString());
        JSONObject json = response.getEntity(JSONObject.class);
        verifyClusterMetricsJSON(json);
    }

    @Test
    public void testClusterMetricsDefault() throws Exception, JSONException {
        WebResource r = resource();
        ClientResponse response = r.path("ws").path("v1").path("cluster").path("metrics").get(ClientResponse.class);
        Assert.assertEquals((((MediaType.APPLICATION_JSON) + "; ") + (JettyUtils.UTF_8)), response.getType().toString());
        JSONObject json = response.getEntity(JSONObject.class);
        verifyClusterMetricsJSON(json);
    }

    @Test
    public void testClusterMetricsXML() throws Exception, JSONException {
        WebResource r = resource();
        ClientResponse response = r.path("ws").path("v1").path("cluster").path("metrics").accept("application/xml").get(ClientResponse.class);
        Assert.assertEquals((((MediaType.APPLICATION_XML) + "; ") + (JettyUtils.UTF_8)), response.getType().toString());
        String xml = response.getEntity(String.class);
        verifyClusterMetricsXML(xml);
    }

    @Test
    public void testClusterSchedulerFifo() throws Exception, JSONException {
        WebResource r = resource();
        ClientResponse response = r.path("ws").path("v1").path("cluster").path("scheduler").accept(APPLICATION_JSON).get(ClientResponse.class);
        Assert.assertEquals((((MediaType.APPLICATION_JSON) + "; ") + (JettyUtils.UTF_8)), response.getType().toString());
        JSONObject json = response.getEntity(JSONObject.class);
        verifyClusterSchedulerFifo(json);
    }

    @Test
    public void testClusterSchedulerFifoSlash() throws Exception, JSONException {
        WebResource r = resource();
        ClientResponse response = r.path("ws").path("v1").path("cluster").path("scheduler/").accept(APPLICATION_JSON).get(ClientResponse.class);
        Assert.assertEquals((((MediaType.APPLICATION_JSON) + "; ") + (JettyUtils.UTF_8)), response.getType().toString());
        JSONObject json = response.getEntity(JSONObject.class);
        verifyClusterSchedulerFifo(json);
    }

    @Test
    public void testClusterSchedulerFifoDefault() throws Exception, JSONException {
        WebResource r = resource();
        ClientResponse response = r.path("ws").path("v1").path("cluster").path("scheduler").get(ClientResponse.class);
        Assert.assertEquals((((MediaType.APPLICATION_JSON) + "; ") + (JettyUtils.UTF_8)), response.getType().toString());
        JSONObject json = response.getEntity(JSONObject.class);
        verifyClusterSchedulerFifo(json);
    }

    @Test
    public void testClusterSchedulerFifoXML() throws Exception, JSONException {
        WebResource r = resource();
        ClientResponse response = r.path("ws").path("v1").path("cluster").path("scheduler").accept(APPLICATION_XML).get(ClientResponse.class);
        Assert.assertEquals((((MediaType.APPLICATION_XML) + "; ") + (JettyUtils.UTF_8)), response.getType().toString());
        String xml = response.getEntity(String.class);
        verifySchedulerFifoXML(xml);
    }

    // Test the scenario where the RM removes an app just as we try to
    // look at it in the apps list
    @Test
    public void testAppsRace() throws Exception {
        // mock up an RM that returns app reports for apps that don't exist
        // in the RMApps list
        ApplicationId appId = ApplicationId.newInstance(1, 1);
        ApplicationReport mockReport = Mockito.mock(ApplicationReport.class);
        Mockito.when(mockReport.getApplicationId()).thenReturn(appId);
        GetApplicationsResponse mockAppsResponse = Mockito.mock(GetApplicationsResponse.class);
        Mockito.when(mockAppsResponse.getApplicationList()).thenReturn(Arrays.asList(new ApplicationReport[]{ mockReport }));
        ClientRMService mockClientSvc = Mockito.mock(org.apache.hadoop.yarn.server.resourcemanager.ClientRMService.class);
        Mockito.when(mockClientSvc.getApplications(ArgumentMatchers.isA(GetApplicationsRequest.class))).thenReturn(mockAppsResponse);
        ResourceManager mockRM = Mockito.mock(ResourceManager.class);
        RMContextImpl rmContext = new RMContextImpl(null, null, null, null, null, null, null, null, null, null);
        Mockito.when(mockRM.getRMContext()).thenReturn(rmContext);
        Mockito.when(mockRM.getClientRMService()).thenReturn(mockClientSvc);
        rmContext.setNodeLabelManager(Mockito.mock(RMNodeLabelsManager.class));
        RMWebServices webSvc = new RMWebServices(mockRM, new Configuration(), Mockito.mock(HttpServletResponse.class));
        final Set<String> emptySet = Collections.unmodifiableSet(Collections.<String>emptySet());
        // verify we don't get any apps when querying
        HttpServletRequest mockHsr = Mockito.mock(HttpServletRequest.class);
        AppsInfo appsInfo = webSvc.getApps(mockHsr, null, emptySet, null, null, null, null, null, null, null, null, emptySet, emptySet, null);
        Assert.assertTrue(appsInfo.getApps().isEmpty());
        // verify we don't get an NPE when specifying a final status query
        appsInfo = webSvc.getApps(mockHsr, null, emptySet, "FAILED", null, null, null, null, null, null, null, emptySet, emptySet, null);
        Assert.assertTrue(appsInfo.getApps().isEmpty());
    }

    @Test
    public void testDumpingSchedulerLogs() throws Exception {
        ResourceManager mockRM = Mockito.mock(ResourceManager.class);
        Configuration conf = new YarnConfiguration();
        HttpServletRequest mockHsr = mockHttpServletRequestByUserName("non-admin");
        ApplicationACLsManager aclsManager = new ApplicationACLsManager(conf);
        Mockito.when(mockRM.getApplicationACLsManager()).thenReturn(aclsManager);
        RMWebServices webSvc = new RMWebServices(mockRM, conf, Mockito.mock(HttpServletResponse.class));
        // nothing should happen
        webSvc.dumpSchedulerLogs("1", mockHsr);
        waitforLogDump(50);
        checkSchedulerLogFileAndCleanup();
        conf.setBoolean(YARN_ACL_ENABLE, true);
        conf.setStrings(YARN_ADMIN_ACL, "admin");
        aclsManager = new ApplicationACLsManager(conf);
        Mockito.when(mockRM.getApplicationACLsManager()).thenReturn(aclsManager);
        webSvc = new RMWebServices(mockRM, conf, Mockito.mock(HttpServletResponse.class));
        boolean exceptionThrown = false;
        try {
            webSvc.dumpSchedulerLogs("1", mockHsr);
            Assert.fail("Dumping logs should fail");
        } catch (ForbiddenException ae) {
            exceptionThrown = true;
        }
        Assert.assertTrue("ForbiddenException expected", exceptionThrown);
        exceptionThrown = false;
        Mockito.when(mockHsr.getUserPrincipal()).thenReturn(new Principal() {
            @Override
            public String getName() {
                return "testuser";
            }
        });
        try {
            webSvc.dumpSchedulerLogs("1", mockHsr);
            Assert.fail("Dumping logs should fail");
        } catch (ForbiddenException ae) {
            exceptionThrown = true;
        }
        Assert.assertTrue("ForbiddenException expected", exceptionThrown);
        Mockito.when(mockHsr.getUserPrincipal()).thenReturn(new Principal() {
            @Override
            public String getName() {
                return "admin";
            }
        });
        webSvc.dumpSchedulerLogs("1", mockHsr);
        waitforLogDump(50);
        checkSchedulerLogFileAndCleanup();
    }

    @Test
    public void testCheckUserAccessToQueue() throws Exception {
        ResourceManager mockRM = Mockito.mock(ResourceManager.class);
        Configuration conf = new YarnConfiguration();
        // Inject a mock scheduler implementation.
        // Only admin user has ADMINISTER_QUEUE access.
        // For SUBMIT_APPLICATION ACL, both of admin/yarn user have acess
        ResourceScheduler mockScheduler = new FifoScheduler() {
            @Override
            public synchronized boolean checkAccess(UserGroupInformation callerUGI, QueueACL acl, String queueName) {
                if (acl == (QueueACL.ADMINISTER_QUEUE)) {
                    if (callerUGI.getUserName().equals("admin")) {
                        return true;
                    }
                } else {
                    if (ImmutableSet.of("admin", "yarn").contains(callerUGI.getUserName())) {
                        return true;
                    }
                }
                return false;
            }
        };
        Mockito.when(mockRM.getResourceScheduler()).thenReturn(mockScheduler);
        RMWebServices webSvc = new RMWebServices(mockRM, conf, Mockito.mock(HttpServletResponse.class));
        boolean caughtException = false;
        // Case 1: Only queue admin user can access other user's information
        HttpServletRequest mockHsr = mockHttpServletRequestByUserName("non-admin");
        try {
            webSvc.checkUserAccessToQueue("queue", "jack", SUBMIT_APPLICATIONS.name(), mockHsr);
        } catch (ForbiddenException e) {
            caughtException = true;
        }
        Assert.assertTrue(caughtException);
        // Case 2: request an unknown ACL causes BAD_REQUEST
        mockHsr = mockHttpServletRequestByUserName("admin");
        caughtException = false;
        try {
            webSvc.checkUserAccessToQueue("queue", "jack", "XYZ_ACL", mockHsr);
        } catch (BadRequestException e) {
            caughtException = true;
        }
        Assert.assertTrue(caughtException);
        // Case 3: get FORBIDDEN for rejected ACL
        mockHsr = mockHttpServletRequestByUserName("admin");
        Assert.assertFalse(webSvc.checkUserAccessToQueue("queue", "jack", SUBMIT_APPLICATIONS.name(), mockHsr).isAllowed());
        Assert.assertFalse(webSvc.checkUserAccessToQueue("queue", "jack", ADMINISTER_QUEUE.name(), mockHsr).isAllowed());
        // Case 4: get OK for listed ACLs
        mockHsr = mockHttpServletRequestByUserName("admin");
        Assert.assertTrue(webSvc.checkUserAccessToQueue("queue", "admin", SUBMIT_APPLICATIONS.name(), mockHsr).isAllowed());
        Assert.assertTrue(webSvc.checkUserAccessToQueue("queue", "admin", ADMINISTER_QUEUE.name(), mockHsr).isAllowed());
        // Case 5: get OK only for SUBMIT_APP acl for "yarn" user
        mockHsr = mockHttpServletRequestByUserName("admin");
        Assert.assertTrue(webSvc.checkUserAccessToQueue("queue", "yarn", SUBMIT_APPLICATIONS.name(), mockHsr).isAllowed());
        Assert.assertFalse(webSvc.checkUserAccessToQueue("queue", "yarn", ADMINISTER_QUEUE.name(), mockHsr).isAllowed());
    }

    @Test
    public void testClusterUserInfo() throws Exception, JSONException {
        ResourceManager mockRM = Mockito.mock(ResourceManager.class);
        Configuration conf = new YarnConfiguration();
        HttpServletRequest mockHsr = mockHttpServletRequestByUserName("admin");
        Mockito.when(mockRM.getRMLoginUser()).thenReturn("yarn");
        RMWebServices webSvc = new RMWebServices(mockRM, conf, Mockito.mock(HttpServletResponse.class));
        ClusterUserInfo userInfo = webSvc.getClusterUserInfo(mockHsr);
        verifyClusterUserInfo(userInfo, "yarn", "admin");
    }
}

