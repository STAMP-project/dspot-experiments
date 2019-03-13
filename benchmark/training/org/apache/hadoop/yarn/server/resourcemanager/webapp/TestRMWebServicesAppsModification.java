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


import ApplicationTimeoutType.LIFETIME;
import AuthenticationFilter.AUTH_TYPE;
import FairSchedulerConfiguration.ALLOCATION_FILE;
import HttpHeaders.LOCATION;
import LocalResourceType.FILE;
import LocalResourceVisibility.APPLICATION;
import MediaType.APPLICATION_JSON;
import MediaType.APPLICATION_JSON_TYPE;
import MediaType.APPLICATION_XML;
import PseudoAuthenticationHandler.ANONYMOUS_ALLOWED;
import QueueACL.ADMINISTER_QUEUE;
import RMAppState.ACCEPTED;
import RMAppState.FINAL_SAVING;
import RMAppState.KILLING;
import Status.BAD_REQUEST;
import Status.FORBIDDEN;
import Status.NOT_FOUND;
import Status.OK;
import Status.UNAUTHORIZED;
import YarnApplicationState.FINISHED;
import YarnApplicationState.KILLED;
import YarnConfiguration.DEFAULT_RM_AM_MAX_ATTEMPTS;
import YarnConfiguration.MAX_CLUSTER_LEVEL_APPLICATION_PRIORITY;
import YarnConfiguration.RM_AM_MAX_ATTEMPTS;
import YarnConfiguration.RM_SCHEDULER;
import com.google.inject.Singleton;
import com.google.inject.servlet.GuiceFilter;
import com.google.inject.servlet.ServletModule;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.client.filter.LoggingFilter;
import com.sun.jersey.guice.spi.container.servlet.GuiceContainer;
import com.sun.jersey.test.framework.WebAppDescriptor;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URI;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Properties;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.ws.rs.core.MediaType;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.http.JettyUtils;
import org.apache.hadoop.security.authentication.server.AuthenticationFilter;
import org.apache.hadoop.yarn.server.resourcemanager.MockNM;
import org.apache.hadoop.yarn.server.resourcemanager.MockRM;
import org.apache.hadoop.yarn.server.resourcemanager.ResourceManager;
import org.apache.hadoop.yarn.server.resourcemanager.rmapp.RMApp;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.capacity.CapacityScheduler;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.capacity.CapacitySchedulerConfiguration;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.fair.FairScheduler;
import org.apache.hadoop.yarn.server.resourcemanager.webapp.dao.AppPriority;
import org.apache.hadoop.yarn.server.resourcemanager.webapp.dao.AppQueue;
import org.apache.hadoop.yarn.server.resourcemanager.webapp.dao.AppState;
import org.apache.hadoop.yarn.server.resourcemanager.webapp.dao.ApplicationSubmissionContextInfo;
import org.apache.hadoop.yarn.server.resourcemanager.webapp.dao.LocalResourceInfo;
import org.apache.hadoop.yarn.util.Times;
import org.apache.hadoop.yarn.webapp.GenericExceptionHandler;
import org.apache.hadoop.yarn.webapp.GuiceServletConfig;
import org.apache.hadoop.yarn.webapp.JerseyTestBase;
import org.apache.hadoop.yarn.webapp.WebServicesTestUtils;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


@RunWith(Parameterized.class)
public class TestRMWebServicesAppsModification extends JerseyTestBase {
    private static MockRM rm;

    private static final int CONTAINER_MB = 1024;

    private String webserviceUserName = "testuser";

    private boolean setAuthFilter = false;

    private static final String TEST_DIR = new File(System.getProperty("test.build.data", "/tmp")).getAbsolutePath();

    private static final String FS_ALLOC_FILE = new File(TestRMWebServicesAppsModification.TEST_DIR, "test-fs-queues.xml").getAbsolutePath();

    /* Helper class to allow testing of RM web services which require
    authorization Add this class as a filter in the Guice injector for the
    MockRM
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
            props.put(ANONYMOUS_ALLOWED, "false");
            return props;
        }
    }

    private abstract class TestServletModule extends ServletModule {
        public Configuration conf = new Configuration();

        public abstract void configureScheduler();

        @Override
        protected void configureServlets() {
            configureScheduler();
            bind(JAXBContextResolver.class);
            bind(RMWebServices.class);
            bind(GenericExceptionHandler.class);
            conf.setInt(RM_AM_MAX_ATTEMPTS, DEFAULT_RM_AM_MAX_ATTEMPTS);
            TestRMWebServicesAppsModification.rm = new MockRM(conf);
            bind(ResourceManager.class).toInstance(TestRMWebServicesAppsModification.rm);
            if (setAuthFilter) {
                filter("/*").through(TestRMWebServicesAppsModification.TestRMCustomAuthFilter.class);
            }
            serve("/*").with(GuiceContainer.class);
        }
    }

    private class CapTestServletModule extends TestRMWebServicesAppsModification.TestServletModule {
        @Override
        public void configureScheduler() {
            conf.set(RM_SCHEDULER, CapacityScheduler.class.getName());
        }
    }

    private class FairTestServletModule extends TestRMWebServicesAppsModification.TestServletModule {
        @Override
        public void configureScheduler() {
            try {
                PrintWriter out = new PrintWriter(new FileWriter(TestRMWebServicesAppsModification.FS_ALLOC_FILE));
                out.println("<?xml version=\"1.0\"?>");
                out.println("<allocations>");
                out.println("<queue name=\"root\">");
                out.println("  <aclAdministerApps>someuser </aclAdministerApps>");
                out.println("  <queue name=\"default\">");
                out.println("    <aclAdministerApps>someuser </aclAdministerApps>");
                out.println("  </queue>");
                out.println("  <queue name=\"test\">");
                out.println("    <aclAdministerApps>someuser </aclAdministerApps>");
                out.println("  </queue>");
                out.println("</queue>");
                out.println("</allocations>");
                out.close();
            } catch (IOException e) {
            }
            conf.set(ALLOCATION_FILE, TestRMWebServicesAppsModification.FS_ALLOC_FILE);
            conf.set(RM_SCHEDULER, FairScheduler.class.getName());
        }
    }

    public TestRMWebServicesAppsModification(int run) {
        super(new WebAppDescriptor.Builder("org.apache.hadoop.yarn.server.resourcemanager.webapp").contextListenerClass(GuiceServletConfig.class).filterClass(GuiceFilter.class).clientConfig(new DefaultClientConfig(JAXBContextResolver.class)).contextPath("jersey-guice-filter").servletPath("/").build());
        switch (run) {
            case 0 :
            default :
                // No Auth Capacity Scheduler
                GuiceServletConfig.setInjector(getNoAuthInjectorCap());
                break;
            case 1 :
                // Simple Auth Capacity Scheduler
                GuiceServletConfig.setInjector(getSimpleAuthInjectorCap());
                break;
            case 2 :
                // No Auth Fair Scheduler
                GuiceServletConfig.setInjector(getNoAuthInjectorFair());
                break;
            case 3 :
                // Simple Auth Fair Scheduler
                GuiceServletConfig.setInjector(getSimpleAuthInjectorFair());
                break;
        }
    }

    @Test
    public void testSingleAppState() throws Exception {
        start();
        MockNM amNodeManager = TestRMWebServicesAppsModification.rm.registerNode("127.0.0.1:1234", 2048);
        String[] mediaTypes = new String[]{ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML };
        for (String mediaType : mediaTypes) {
            RMApp app = TestRMWebServicesAppsModification.rm.submitApp(TestRMWebServicesAppsModification.CONTAINER_MB, "", webserviceUserName);
            amNodeManager.nodeHeartbeat(true);
            ClientResponse response = this.constructWebResource("apps", app.getApplicationId().toString(), "state").accept(mediaType).get(ClientResponse.class);
            WebServicesTestUtils.assertResponseStatusCode(OK, response.getStatusInfo());
            if (mediaType.contains(APPLICATION_JSON)) {
                TestRMWebServicesAppsModification.verifyAppStateJson(response, ACCEPTED);
            } else
                if (mediaType.contains(APPLICATION_XML)) {
                    TestRMWebServicesAppsModification.verifyAppStateXML(response, ACCEPTED);
                }

        }
        stop();
    }

    @Test(timeout = 120000)
    public void testSingleAppKill() throws Exception {
        start();
        MockNM amNodeManager = TestRMWebServicesAppsModification.rm.registerNode("127.0.0.1:1234", 2048);
        String[] mediaTypes = new String[]{ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML };
        MediaType[] contentTypes = new MediaType[]{ MediaType.APPLICATION_JSON_TYPE, MediaType.APPLICATION_XML_TYPE };
        String diagnostic = "message1";
        for (String mediaType : mediaTypes) {
            for (MediaType contentType : contentTypes) {
                RMApp app = TestRMWebServicesAppsModification.rm.submitApp(TestRMWebServicesAppsModification.CONTAINER_MB, "", webserviceUserName);
                amNodeManager.nodeHeartbeat(true);
                AppState targetState = new AppState(KILLED.toString());
                targetState.setDiagnostics(diagnostic);
                Object entity;
                if (contentType.equals(APPLICATION_JSON_TYPE)) {
                    entity = TestRMWebServicesAppsModification.appStateToJSON(targetState);
                } else {
                    entity = targetState;
                }
                ClientResponse response = this.constructWebResource("apps", app.getApplicationId().toString(), "state").entity(entity, contentType).accept(mediaType).put(ClientResponse.class);
                if (!(isAuthenticationEnabled())) {
                    WebServicesTestUtils.assertResponseStatusCode(UNAUTHORIZED, response.getStatusInfo());
                    continue;
                }
                WebServicesTestUtils.assertResponseStatusCode(Status.ACCEPTED, response.getStatusInfo());
                if (mediaType.contains(APPLICATION_JSON)) {
                    TestRMWebServicesAppsModification.verifyAppStateJson(response, FINAL_SAVING, RMAppState.KILLED, KILLING, ACCEPTED);
                } else {
                    TestRMWebServicesAppsModification.verifyAppStateXML(response, FINAL_SAVING, RMAppState.KILLED, KILLING, ACCEPTED);
                }
                String locationHeaderValue = response.getHeaders().getFirst(LOCATION);
                Client c = Client.create();
                WebResource tmp = c.resource(locationHeaderValue);
                if (isAuthenticationEnabled()) {
                    tmp = tmp.queryParam("user.name", webserviceUserName);
                }
                response = tmp.get(ClientResponse.class);
                WebServicesTestUtils.assertResponseStatusCode(OK, response.getStatusInfo());
                Assert.assertTrue(locationHeaderValue.endsWith((("/ws/v1/cluster/apps/" + (app.getApplicationId().toString())) + "/state")));
                while (true) {
                    Thread.sleep(100);
                    response = this.constructWebResource("apps", app.getApplicationId().toString(), "state").accept(mediaType).entity(entity, contentType).put(ClientResponse.class);
                    Assert.assertTrue((((response.getStatusInfo().getStatusCode()) == (Status.ACCEPTED.getStatusCode())) || ((response.getStatusInfo().getStatusCode()) == (OK.getStatusCode()))));
                    if ((response.getStatusInfo().getStatusCode()) == (OK.getStatusCode())) {
                        Assert.assertEquals(RMAppState.KILLED, app.getState());
                        if (mediaType.equals(APPLICATION_JSON)) {
                            TestRMWebServicesAppsModification.verifyAppStateJson(response, RMAppState.KILLED);
                        } else {
                            TestRMWebServicesAppsModification.verifyAppStateXML(response, RMAppState.KILLED);
                        }
                        Assert.assertTrue("Diagnostic message is incorrect", app.getDiagnostics().toString().contains(diagnostic));
                        break;
                    }
                } 
            }
        }
        stop();
    }

    @Test
    public void testSingleAppKillInvalidState() throws Exception {
        start();
        MockNM amNodeManager = TestRMWebServicesAppsModification.rm.registerNode("127.0.0.1:1234", 2048);
        String[] mediaTypes = new String[]{ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML };
        MediaType[] contentTypes = new MediaType[]{ MediaType.APPLICATION_JSON_TYPE, MediaType.APPLICATION_XML_TYPE };
        String[] targetStates = new String[]{ FINISHED.toString(), "blah" };
        for (String mediaType : mediaTypes) {
            for (MediaType contentType : contentTypes) {
                for (String targetStateString : targetStates) {
                    RMApp app = TestRMWebServicesAppsModification.rm.submitApp(TestRMWebServicesAppsModification.CONTAINER_MB, "", webserviceUserName);
                    amNodeManager.nodeHeartbeat(true);
                    ClientResponse response;
                    AppState targetState = new AppState(targetStateString);
                    Object entity;
                    if (contentType.equals(APPLICATION_JSON_TYPE)) {
                        entity = TestRMWebServicesAppsModification.appStateToJSON(targetState);
                    } else {
                        entity = targetState;
                    }
                    response = this.constructWebResource("apps", app.getApplicationId().toString(), "state").entity(entity, contentType).accept(mediaType).put(ClientResponse.class);
                    if (!(isAuthenticationEnabled())) {
                        WebServicesTestUtils.assertResponseStatusCode(UNAUTHORIZED, response.getStatusInfo());
                        continue;
                    }
                    WebServicesTestUtils.assertResponseStatusCode(BAD_REQUEST, response.getStatusInfo());
                }
            }
        }
        stop();
    }

    @Test(timeout = 60000)
    public void testSingleAppKillUnauthorized() throws Exception {
        boolean isCapacityScheduler = (getResourceScheduler()) instanceof CapacityScheduler;
        boolean isFairScheduler = (getResourceScheduler()) instanceof FairScheduler;
        Assume.assumeTrue("This test is only supported on Capacity and Fair Scheduler", (isCapacityScheduler || isFairScheduler));
        // FairScheduler use ALLOCATION_FILE to configure ACL
        if (isCapacityScheduler) {
            // default root queue allows anyone to have admin acl
            CapacitySchedulerConfiguration csconf = new CapacitySchedulerConfiguration();
            csconf.setAcl("root", ADMINISTER_QUEUE, "someuser");
            csconf.setAcl("root.default", ADMINISTER_QUEUE, "someuser");
            getResourceScheduler().reinitialize(csconf, getRMContext());
        }
        start();
        MockNM amNodeManager = TestRMWebServicesAppsModification.rm.registerNode("127.0.0.1:1234", 2048);
        String[] mediaTypes = new String[]{ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML };
        for (String mediaType : mediaTypes) {
            RMApp app = TestRMWebServicesAppsModification.rm.submitApp(TestRMWebServicesAppsModification.CONTAINER_MB, "test", "someuser");
            amNodeManager.nodeHeartbeat(true);
            ClientResponse response = this.constructWebResource("apps", app.getApplicationId().toString(), "state").accept(mediaType).get(ClientResponse.class);
            AppState info = response.getEntity(AppState.class);
            info.setState(KILLED.toString());
            response = this.constructWebResource("apps", app.getApplicationId().toString(), "state").accept(mediaType).entity(info, APPLICATION_XML).put(ClientResponse.class);
            validateResponseStatus(response, FORBIDDEN);
        }
        stop();
    }

    @Test
    public void testSingleAppKillInvalidId() throws Exception {
        start();
        MockNM amNodeManager = TestRMWebServicesAppsModification.rm.registerNode("127.0.0.1:1234", 2048);
        amNodeManager.nodeHeartbeat(true);
        String[] testAppIds = new String[]{ "application_1391705042196_0001", "random_string" };
        for (int i = 0; i < (testAppIds.length); i++) {
            AppState info = new AppState("KILLED");
            ClientResponse response = this.constructWebResource("apps", testAppIds[i], "state").accept(APPLICATION_XML).entity(info, APPLICATION_XML).put(ClientResponse.class);
            if (!(isAuthenticationEnabled())) {
                WebServicesTestUtils.assertResponseStatusCode(UNAUTHORIZED, response.getStatusInfo());
                continue;
            }
            if (i == 0) {
                WebServicesTestUtils.assertResponseStatusCode(NOT_FOUND, response.getStatusInfo());
            } else {
                WebServicesTestUtils.assertResponseStatusCode(BAD_REQUEST, response.getStatusInfo());
            }
        }
        stop();
    }

    // Simple test - just post to /apps/new-application and validate the response
    @Test
    public void testGetNewApplication() throws Exception {
        client().addFilter(new LoggingFilter(System.out));
        start();
        String[] mediaTypes = new String[]{ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML };
        for (String acceptMedia : mediaTypes) {
            testGetNewApplication(acceptMedia);
        }
        stop();
    }

    // Test to validate the process of submitting apps - test for appropriate
    // errors as well
    @Test
    public void testGetNewApplicationAndSubmit() throws Exception {
        start();
        MockNM amNodeManager = TestRMWebServicesAppsModification.rm.registerNode("127.0.0.1:1234", 2048);
        amNodeManager.nodeHeartbeat(true);
        String[] mediaTypes = new String[]{ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML };
        for (String acceptMedia : mediaTypes) {
            for (String contentMedia : mediaTypes) {
                testAppSubmit(acceptMedia, contentMedia);
                testAppSubmitErrors(acceptMedia, contentMedia);
            }
        }
        stop();
    }

    @Test
    public void testAppSubmitBadJsonAndXML() throws Exception {
        // submit a bunch of bad XML and JSON via the
        // REST API and make sure we get error response codes
        String urlPath = "apps";
        start();
        MockNM amNodeManager = TestRMWebServicesAppsModification.rm.registerNode("127.0.0.1:1234", 2048);
        amNodeManager.nodeHeartbeat(true);
        ApplicationSubmissionContextInfo appInfo = new ApplicationSubmissionContextInfo();
        appInfo.setApplicationName("test");
        appInfo.setPriority(3);
        appInfo.setMaxAppAttempts(2);
        appInfo.setQueue("testqueue");
        appInfo.setApplicationType("test-type");
        HashMap<String, LocalResourceInfo> lr = new HashMap<>();
        LocalResourceInfo y = new LocalResourceInfo();
        y.setUrl(new URI("http://www.test.com/file.txt"));
        y.setSize(100);
        y.setTimestamp(System.currentTimeMillis());
        y.setType(FILE);
        y.setVisibility(APPLICATION);
        lr.put("example", y);
        appInfo.getContainerLaunchContextInfo().setResources(lr);
        appInfo.getResource().setMemory(1024);
        appInfo.getResource().setvCores(1);
        String body = "<?xml version=\"1.0\" encoding=\"UTF-8\" " + "standalone=\"yes\"?><blah/>";
        ClientResponse response = this.constructWebResource(urlPath).accept(APPLICATION_XML).entity(body, APPLICATION_XML).post(ClientResponse.class);
        WebServicesTestUtils.assertResponseStatusCode(BAD_REQUEST, response.getStatusInfo());
        body = "{\"a\" : \"b\"}";
        response = this.constructWebResource(urlPath).accept(APPLICATION_XML).entity(body, APPLICATION_JSON).post(ClientResponse.class);
        validateResponseStatus(response, BAD_REQUEST);
        stop();
    }

    @Test
    public void testGetAppQueue() throws Exception {
        client().addFilter(new LoggingFilter(System.out));
        boolean isCapacityScheduler = (getResourceScheduler()) instanceof CapacityScheduler;
        start();
        MockNM amNodeManager = TestRMWebServicesAppsModification.rm.registerNode("127.0.0.1:1234", 2048);
        String[] contentTypes = new String[]{ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML };
        for (String contentType : contentTypes) {
            RMApp app = TestRMWebServicesAppsModification.rm.submitApp(TestRMWebServicesAppsModification.CONTAINER_MB, "", webserviceUserName);
            amNodeManager.nodeHeartbeat(true);
            ClientResponse response = this.constructWebResource("apps", app.getApplicationId().toString(), "queue").accept(contentType).get(ClientResponse.class);
            WebServicesTestUtils.assertResponseStatusCode(OK, response.getStatusInfo());
            String expectedQueue = "default";
            if (!isCapacityScheduler) {
                expectedQueue = "root." + (webserviceUserName);
            }
            if (contentType.contains(APPLICATION_JSON)) {
                TestRMWebServicesAppsModification.verifyAppQueueJson(response, expectedQueue);
            } else {
                TestRMWebServicesAppsModification.verifyAppQueueXML(response, expectedQueue);
            }
        }
        stop();
    }

    @Test(timeout = 90000)
    public void testUpdateAppPriority() throws Exception {
        client().addFilter(new LoggingFilter(System.out));
        if (!((getResourceScheduler()) instanceof CapacityScheduler)) {
            // till the fair scheduler modifications for priority is completed
            return;
        }
        CapacityScheduler cs = ((CapacityScheduler) (getResourceScheduler()));
        Configuration conf = new Configuration();
        conf.setInt(MAX_CLUSTER_LEVEL_APPLICATION_PRIORITY, 10);
        cs.setClusterMaxPriority(conf);
        // default root queue allows anyone to have admin acl
        CapacitySchedulerConfiguration csconf = new CapacitySchedulerConfiguration();
        String[] queues = new String[]{ "default", "test" };
        csconf.setQueues("root", queues);
        csconf.setCapacity("root.default", 50.0F);
        csconf.setCapacity("root.test", 50.0F);
        csconf.setAcl("root", ADMINISTER_QUEUE, "someuser");
        csconf.setAcl("root.default", ADMINISTER_QUEUE, "someuser");
        csconf.setAcl("root.test", ADMINISTER_QUEUE, "someuser");
        getResourceScheduler().reinitialize(csconf, getRMContext());
        start();
        MockNM amNodeManager = TestRMWebServicesAppsModification.rm.registerNode("127.0.0.1:1234", 2048);
        String[] mediaTypes = new String[]{ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML };
        MediaType[] contentTypes = new MediaType[]{ MediaType.APPLICATION_JSON_TYPE, MediaType.APPLICATION_XML_TYPE };
        for (String mediaType : mediaTypes) {
            for (MediaType contentType : contentTypes) {
                RMApp app = TestRMWebServicesAppsModification.rm.submitApp(TestRMWebServicesAppsModification.CONTAINER_MB, "", webserviceUserName);
                amNodeManager.nodeHeartbeat(true);
                int modifiedPriority = 8;
                AppPriority priority = new AppPriority(modifiedPriority);
                Object entity;
                if (contentType.equals(APPLICATION_JSON_TYPE)) {
                    entity = TestRMWebServicesAppsModification.appPriorityToJSON(priority);
                } else {
                    entity = priority;
                }
                ClientResponse response = this.constructWebResource("apps", app.getApplicationId().toString(), "priority").entity(entity, contentType).accept(mediaType).put(ClientResponse.class);
                if (!(isAuthenticationEnabled())) {
                    WebServicesTestUtils.assertResponseStatusCode(UNAUTHORIZED, response.getStatusInfo());
                    continue;
                }
                WebServicesTestUtils.assertResponseStatusCode(OK, response.getStatusInfo());
                if (mediaType.contains(APPLICATION_JSON)) {
                    TestRMWebServicesAppsModification.verifyAppPriorityJson(response, modifiedPriority);
                } else {
                    TestRMWebServicesAppsModification.verifyAppPriorityXML(response, modifiedPriority);
                }
                response = this.constructWebResource("apps", app.getApplicationId().toString(), "priority").accept(mediaType).get(ClientResponse.class);
                WebServicesTestUtils.assertResponseStatusCode(OK, response.getStatusInfo());
                if (mediaType.contains(APPLICATION_JSON)) {
                    TestRMWebServicesAppsModification.verifyAppPriorityJson(response, modifiedPriority);
                } else {
                    TestRMWebServicesAppsModification.verifyAppPriorityXML(response, modifiedPriority);
                }
                // check unauthorized
                app = TestRMWebServicesAppsModification.rm.submitApp(TestRMWebServicesAppsModification.CONTAINER_MB, "", "someuser");
                amNodeManager.nodeHeartbeat(true);
                response = this.constructWebResource("apps", app.getApplicationId().toString(), "priority").entity(entity, contentType).accept(mediaType).put(ClientResponse.class);
                WebServicesTestUtils.assertResponseStatusCode(FORBIDDEN, response.getStatusInfo());
            }
        }
        stop();
    }

    @Test(timeout = 90000)
    public void testAppMove() throws Exception {
        client().addFilter(new LoggingFilter(System.out));
        boolean isCapacityScheduler = (getResourceScheduler()) instanceof CapacityScheduler;
        // default root queue allows anyone to have admin acl
        CapacitySchedulerConfiguration csconf = new CapacitySchedulerConfiguration();
        String[] queues = new String[]{ "default", "test" };
        csconf.setQueues("root", queues);
        csconf.setCapacity("root.default", 50.0F);
        csconf.setCapacity("root.test", 50.0F);
        csconf.setAcl("root", ADMINISTER_QUEUE, "someuser");
        csconf.setAcl("root.default", ADMINISTER_QUEUE, "someuser");
        csconf.setAcl("root.test", ADMINISTER_QUEUE, "someuser");
        getResourceScheduler().reinitialize(csconf, getRMContext());
        start();
        MockNM amNodeManager = TestRMWebServicesAppsModification.rm.registerNode("127.0.0.1:1234", 2048);
        String[] mediaTypes = new String[]{ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML };
        MediaType[] contentTypes = new MediaType[]{ MediaType.APPLICATION_JSON_TYPE, MediaType.APPLICATION_XML_TYPE };
        for (String mediaType : mediaTypes) {
            for (MediaType contentType : contentTypes) {
                RMApp app = TestRMWebServicesAppsModification.rm.submitApp(TestRMWebServicesAppsModification.CONTAINER_MB, "", webserviceUserName);
                amNodeManager.nodeHeartbeat(true);
                AppQueue targetQueue = new AppQueue("test");
                Object entity;
                if (contentType.equals(APPLICATION_JSON_TYPE)) {
                    entity = TestRMWebServicesAppsModification.appQueueToJSON(targetQueue);
                } else {
                    entity = targetQueue;
                }
                ClientResponse response = this.constructWebResource("apps", app.getApplicationId().toString(), "queue").entity(entity, contentType).accept(mediaType).put(ClientResponse.class);
                if (!(isAuthenticationEnabled())) {
                    WebServicesTestUtils.assertResponseStatusCode(UNAUTHORIZED, response.getStatusInfo());
                    continue;
                }
                WebServicesTestUtils.assertResponseStatusCode(OK, response.getStatusInfo());
                String expectedQueue = "test";
                if (!isCapacityScheduler) {
                    expectedQueue = "root.test";
                }
                if (mediaType.contains(APPLICATION_JSON)) {
                    TestRMWebServicesAppsModification.verifyAppQueueJson(response, expectedQueue);
                } else {
                    TestRMWebServicesAppsModification.verifyAppQueueXML(response, expectedQueue);
                }
                Assert.assertEquals(expectedQueue, app.getQueue());
                // check unauthorized
                app = TestRMWebServicesAppsModification.rm.submitApp(TestRMWebServicesAppsModification.CONTAINER_MB, "", "someuser");
                amNodeManager.nodeHeartbeat(true);
                response = this.constructWebResource("apps", app.getApplicationId().toString(), "queue").entity(entity, contentType).accept(mediaType).put(ClientResponse.class);
                WebServicesTestUtils.assertResponseStatusCode(FORBIDDEN, response.getStatusInfo());
                if (isCapacityScheduler) {
                    Assert.assertEquals("default", app.getQueue());
                } else {
                    Assert.assertEquals("root.someuser", app.getQueue());
                }
            }
        }
        stop();
    }

    @Test(timeout = 90000)
    public void testUpdateAppTimeout() throws Exception {
        client().addFilter(new LoggingFilter(System.out));
        start();
        TestRMWebServicesAppsModification.rm.registerNode("127.0.0.1:1234", 2048);
        String[] mediaTypes = new String[]{ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML };
        MediaType[] contentTypes = new MediaType[]{ MediaType.APPLICATION_JSON_TYPE, MediaType.APPLICATION_XML_TYPE };
        for (String mediaType : mediaTypes) {
            for (MediaType contentType : contentTypes) {
                // application submitted without timeout
                RMApp app = TestRMWebServicesAppsModification.rm.submitApp(TestRMWebServicesAppsModification.CONTAINER_MB, "", webserviceUserName);
                ClientResponse response = this.constructWebResource("apps", app.getApplicationId().toString(), "timeouts").accept(mediaType).get(ClientResponse.class);
                if (mediaType.contains(APPLICATION_JSON)) {
                    Assert.assertEquals((((MediaType.APPLICATION_JSON_TYPE) + "; ") + (JettyUtils.UTF_8)), response.getType().toString());
                    JSONObject js = response.getEntity(JSONObject.class).getJSONObject("timeouts");
                    JSONArray entity = js.getJSONArray("timeout");
                    TestRMWebServicesAppsModification.verifyAppTimeoutJson(entity.getJSONObject(0), LIFETIME, "UNLIMITED", (-1));
                }
                long timeOutFromNow = 60;
                String expireTime = Times.formatISO8601(((System.currentTimeMillis()) + (timeOutFromNow * 1000)));
                Object entity = getAppTimeoutInfoEntity(LIFETIME, contentType, expireTime);
                response = this.constructWebResource("apps", app.getApplicationId().toString(), "timeout").entity(entity, contentType).accept(mediaType).put(ClientResponse.class);
                if (!(isAuthenticationEnabled())) {
                    WebServicesTestUtils.assertResponseStatusCode(UNAUTHORIZED, response.getStatusInfo());
                    continue;
                }
                WebServicesTestUtils.assertResponseStatusCode(OK, response.getStatusInfo());
                if (mediaType.contains(APPLICATION_JSON)) {
                    TestRMWebServicesAppsModification.verifyAppTimeoutJson(response, LIFETIME, expireTime, timeOutFromNow);
                } else {
                    TestRMWebServicesAppsModification.verifyAppTimeoutXML(response, LIFETIME, expireTime, timeOutFromNow);
                }
                // verify for negative cases
                entity = getAppTimeoutInfoEntity(null, contentType, null);
                response = this.constructWebResource("apps", app.getApplicationId().toString(), "timeout").entity(entity, contentType).accept(mediaType).put(ClientResponse.class);
                WebServicesTestUtils.assertResponseStatusCode(BAD_REQUEST, response.getStatusInfo());
                // invoke get
                response = this.constructWebResource("apps", app.getApplicationId().toString(), "timeouts", LIFETIME.toString()).accept(mediaType).get(ClientResponse.class);
                WebServicesTestUtils.assertResponseStatusCode(OK, response.getStatusInfo());
                if (mediaType.contains(APPLICATION_JSON)) {
                    TestRMWebServicesAppsModification.verifyAppTimeoutJson(response, LIFETIME, expireTime, timeOutFromNow);
                }
            }
        }
        stop();
    }
}

