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
import FairSchedulerConfiguration.ALLOCATION_FILE;
import MediaType.APPLICATION_JSON;
import PseudoAuthenticationHandler.ANONYMOUS_ALLOWED;
import Status.BAD_REQUEST;
import Status.NOT_FOUND;
import YarnConfiguration.DEFAULT_RM_AM_MAX_ATTEMPTS;
import YarnConfiguration.RM_AM_MAX_ATTEMPTS;
import YarnConfiguration.RM_RESERVATION_SYSTEM_ENABLE;
import YarnConfiguration.RM_SCHEDULER;
import YarnConfiguration.YARN_ACL_ENABLE;
import YarnConfiguration.YARN_ADMIN_ACL;
import com.google.inject.Singleton;
import com.google.inject.servlet.GuiceFilter;
import com.google.inject.servlet.ServletModule;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.guice.spi.container.servlet.GuiceContainer;
import com.sun.jersey.test.framework.WebAppDescriptor;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.Properties;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.security.authentication.server.AuthenticationFilter;
import org.apache.hadoop.yarn.api.records.ReservationId;
import org.apache.hadoop.yarn.server.resourcemanager.MockNM;
import org.apache.hadoop.yarn.server.resourcemanager.MockRM;
import org.apache.hadoop.yarn.server.resourcemanager.ResourceManager;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.ResourceScheduler;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.capacity.CapacityScheduler;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.capacity.CapacitySchedulerConfiguration;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.fair.FairScheduler;
import org.apache.hadoop.yarn.util.Clock;
import org.apache.hadoop.yarn.util.UTCClock;
import org.apache.hadoop.yarn.webapp.GenericExceptionHandler;
import org.apache.hadoop.yarn.webapp.GuiceServletConfig;
import org.apache.hadoop.yarn.webapp.JerseyTestBase;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


@RunWith(Parameterized.class)
public class TestRMWebServicesReservation extends JerseyTestBase {
    private String webserviceUserName = "testuser";

    private static boolean setAuthFilter = false;

    private static boolean enableRecurrence = false;

    private static MockRM rm;

    private static final int MINIMUM_RESOURCE_DURATION = 100000;

    private static final Clock clock = new UTCClock();

    private static final int MAXIMUM_PERIOD = 86400000;

    private static final int DEFAULT_RECURRENCE = (TestRMWebServicesReservation.MAXIMUM_PERIOD) / 10;

    private static final String TEST_DIR = new File(System.getProperty("test.build.data", "/tmp")).getAbsolutePath();

    private static final String FS_ALLOC_FILE = new File(TestRMWebServicesReservation.TEST_DIR, "test-fs-queues.xml").getAbsolutePath();

    // This is what is used in the test resource files.
    private static final String DEFAULT_QUEUE = "dedicated";

    private static final String LIST_RESERVATION_PATH = "reservation/list";

    private static final String GET_NEW_RESERVATION_PATH = "reservation/new-reservation";

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

    private abstract static class TestServletModule extends ServletModule {
        public Configuration conf = new Configuration();

        public abstract void configureScheduler();

        @Override
        protected void configureServlets() {
            configureScheduler();
            bind(JAXBContextResolver.class);
            bind(RMWebServices.class);
            bind(GenericExceptionHandler.class);
            conf.setInt(RM_AM_MAX_ATTEMPTS, DEFAULT_RM_AM_MAX_ATTEMPTS);
            conf.setBoolean(RM_RESERVATION_SYSTEM_ENABLE, true);
            TestRMWebServicesReservation.rm = new MockRM(conf);
            bind(ResourceManager.class).toInstance(TestRMWebServicesReservation.rm);
            if (TestRMWebServicesReservation.setAuthFilter) {
                filter("/*").through(TestRMWebServicesReservation.TestRMCustomAuthFilter.class);
            }
            serve("/*").with(GuiceContainer.class);
        }
    }

    private static class CapTestServletModule extends TestRMWebServicesReservation.TestServletModule {
        @Override
        public void configureScheduler() {
            conf.set(RM_SCHEDULER, CapacityScheduler.class.getName());
            conf.setClass(RM_SCHEDULER, CapacityScheduler.class, ResourceScheduler.class);
            CapacitySchedulerConfiguration csconf = new CapacitySchedulerConfiguration(conf);
            String[] queues = new String[]{ "default", "dedicated" };
            csconf.setQueues("root", queues);
            csconf.setCapacity("root.default", 50.0F);
            csconf.setCapacity("root.dedicated", 50.0F);
            csconf.setReservable("root.dedicated", true);
            conf = csconf;
        }
    }

    private static class FairTestServletModule extends TestRMWebServicesReservation.TestServletModule {
        @Override
        public void configureScheduler() {
            try {
                PrintWriter out = new PrintWriter(new FileWriter(TestRMWebServicesReservation.FS_ALLOC_FILE));
                out.println("<?xml version=\"1.0\"?>");
                out.println("<allocations>");
                out.println("<queue name=\"root\">");
                out.println("  <aclAdministerApps>someuser </aclAdministerApps>");
                out.println("  <queue name=\"default\">");
                out.println("    <aclAdministerApps>someuser </aclAdministerApps>");
                out.println("  </queue>");
                out.println("  <queue name=\"dedicated\">");
                out.println("    <reservation>");
                out.println("    </reservation>");
                out.println("    <aclAdministerApps>someuser </aclAdministerApps>");
                out.println("  </queue>");
                out.println("</queue>");
                out.println(("<defaultQueueSchedulingPolicy>drf" + "</defaultQueueSchedulingPolicy>"));
                out.println("</allocations>");
                out.close();
            } catch (IOException e) {
            }
            conf.set(ALLOCATION_FILE, TestRMWebServicesReservation.FS_ALLOC_FILE);
            conf.set(RM_SCHEDULER, FairScheduler.class.getName());
        }
    }

    private static class NoAuthServletModule extends TestRMWebServicesReservation.CapTestServletModule {
        @Override
        protected void configureServlets() {
            TestRMWebServicesReservation.setAuthFilter = false;
            super.configureServlets();
        }
    }

    private static class SimpleAuthServletModule extends TestRMWebServicesReservation.CapTestServletModule {
        @Override
        protected void configureServlets() {
            TestRMWebServicesReservation.setAuthFilter = true;
            conf.setBoolean(YARN_ACL_ENABLE, true);
            // set the admin acls otherwise all users are considered admins
            // and we can't test authorization
            conf.setStrings(YARN_ADMIN_ACL, "testuser1");
            super.configureServlets();
        }
    }

    private static class FairNoAuthServletModule extends TestRMWebServicesReservation.FairTestServletModule {
        @Override
        protected void configureServlets() {
            TestRMWebServicesReservation.setAuthFilter = false;
            super.configureServlets();
        }
    }

    private static class FairSimpleAuthServletModule extends TestRMWebServicesReservation.FairTestServletModule {
        @Override
        protected void configureServlets() {
            TestRMWebServicesReservation.setAuthFilter = true;
            conf.setBoolean(YARN_ACL_ENABLE, true);
            // set the admin acls otherwise all users are considered admins
            // and we can't test authorization
            conf.setStrings(YARN_ADMIN_ACL, "testuser1");
            super.configureServlets();
        }
    }

    public TestRMWebServicesReservation(int run, boolean recurrence) {
        super(new WebAppDescriptor.Builder("org.apache.hadoop.yarn.server.resourcemanager.webapp").contextListenerClass(GuiceServletConfig.class).filterClass(GuiceFilter.class).clientConfig(new DefaultClientConfig(JAXBContextResolver.class)).contextPath("jersey-guice-filter").servletPath("/").build());
        TestRMWebServicesReservation.enableRecurrence = recurrence;
        switch (run) {
            case 0 :
            default :
                // No Auth Capacity Scheduler
                initNoAuthInjectorCap();
                break;
            case 1 :
                // Simple Auth Capacity Scheduler
                initSimpleAuthInjectorCap();
                break;
            case 2 :
                // No Auth Fair Scheduler
                initNoAuthInjectorFair();
                break;
            case 3 :
                // Simple Auth Fair Scheduler
                initSimpleAuthInjectorFair();
                break;
        }
    }

    @Test
    public void testSubmitReservation() throws Exception {
        start();
        setupCluster(100);
        ReservationId rid = getReservationIdTestHelper(1);
        ClientResponse response = reservationSubmissionTestHelper("reservation/submit", APPLICATION_JSON, rid);
        if (this.isAuthenticationEnabled()) {
            Assert.assertTrue(isHttpSuccessResponse(response));
            verifyReservationCount(1);
        }
        stop();
    }

    @Test
    public void testSubmitDuplicateReservation() throws Exception {
        start();
        setupCluster(100);
        ReservationId rid = getReservationIdTestHelper(1);
        long currentTimestamp = (TestRMWebServicesReservation.clock.getTime()) + (TestRMWebServicesReservation.MINIMUM_RESOURCE_DURATION);
        ClientResponse response = reservationSubmissionTestHelper("reservation/submit", APPLICATION_JSON, currentTimestamp, "", rid);
        // Make sure that the first submission is successful
        if (this.isAuthenticationEnabled()) {
            Assert.assertTrue(isHttpSuccessResponse(response));
        }
        response = reservationSubmissionTestHelper("reservation/submit", APPLICATION_JSON, currentTimestamp, "", rid);
        // Make sure that the second submission is successful
        if (this.isAuthenticationEnabled()) {
            Assert.assertTrue(isHttpSuccessResponse(response));
            verifyReservationCount(1);
        }
        stop();
    }

    @Test
    public void testSubmitDifferentReservationWithSameId() throws Exception {
        start();
        setupCluster(100);
        ReservationId rid = getReservationIdTestHelper(1);
        long currentTimestamp = (TestRMWebServicesReservation.clock.getTime()) + (TestRMWebServicesReservation.MINIMUM_RESOURCE_DURATION);
        ClientResponse response = reservationSubmissionTestHelper("reservation/submit", APPLICATION_JSON, currentTimestamp, "res1", rid);
        // Make sure that the first submission is successful
        if (this.isAuthenticationEnabled()) {
            Assert.assertTrue(isHttpSuccessResponse(response));
        }
        // Change the reservation definition.
        response = reservationSubmissionTestHelper("reservation/submit", APPLICATION_JSON, (currentTimestamp + (TestRMWebServicesReservation.MINIMUM_RESOURCE_DURATION)), "res1", rid);
        // Make sure that the second submission is unsuccessful
        if (this.isAuthenticationEnabled()) {
            Assert.assertTrue((!(isHttpSuccessResponse(response))));
            verifyReservationCount(1);
        }
        stop();
    }

    @Test
    public void testFailedSubmitReservation() throws Exception {
        start();
        // setup a cluster too small to accept the reservation
        setupCluster(1);
        ReservationId rid = getReservationIdTestHelper(1);
        ClientResponse response = reservationSubmissionTestHelper("reservation/submit", APPLICATION_JSON, rid);
        Assert.assertTrue((!(isHttpSuccessResponse(response))));
        stop();
    }

    @Test
    public void testUpdateReservation() throws Exception, JSONException {
        start();
        setupCluster(100);
        ReservationId rid = getReservationIdTestHelper(1);
        ClientResponse response = reservationSubmissionTestHelper("reservation/submit", APPLICATION_JSON, rid);
        if (this.isAuthenticationEnabled()) {
            Assert.assertTrue(isHttpSuccessResponse(response));
        }
        updateReservationTestHelper("reservation/update", rid, APPLICATION_JSON);
        stop();
    }

    @Test
    public void testTimeIntervalRequestListReservation() throws Exception {
        start();
        setupCluster(100);
        long time = (TestRMWebServicesReservation.clock.getTime()) + (TestRMWebServicesReservation.MINIMUM_RESOURCE_DURATION);
        ReservationId id1 = getReservationIdTestHelper(1);
        ReservationId id2 = getReservationIdTestHelper(2);
        reservationSubmissionTestHelper("reservation/submit", APPLICATION_JSON, time, "res_1", id1);
        reservationSubmissionTestHelper("reservation/submit", APPLICATION_JSON, (time + (TestRMWebServicesReservation.MINIMUM_RESOURCE_DURATION)), "res_2", id2);
        WebResource resource = constructWebResource(TestRMWebServicesReservation.LIST_RESERVATION_PATH).queryParam("start-time", Long.toString(((long) (time * 0.9)))).queryParam("end-time", Long.toString((time + ((long) (0.9 * (TestRMWebServicesReservation.MINIMUM_RESOURCE_DURATION)))))).queryParam("include-resource-allocations", "true").queryParam("queue", TestRMWebServicesReservation.DEFAULT_QUEUE);
        JSONObject json = testListReservationHelper(resource);
        if ((!(this.isAuthenticationEnabled())) && (json == null)) {
            return;
        }
        JSONObject reservations = json.getJSONObject("reservations");
        testRDLHelper(reservations);
        String reservationName = reservations.getJSONObject("reservation-definition").getString("reservation-name");
        Assert.assertEquals(reservationName, "res_1");
        stop();
    }

    @Test
    public void testSameTimeIntervalRequestListReservation() throws Exception {
        start();
        setupCluster(100);
        long time = (TestRMWebServicesReservation.clock.getTime()) + (TestRMWebServicesReservation.MINIMUM_RESOURCE_DURATION);
        ReservationId id1 = getReservationIdTestHelper(1);
        ReservationId id2 = getReservationIdTestHelper(2);
        // If authentication is not enabled then id1 and id2 will be null
        if (((!(this.isAuthenticationEnabled())) && (id1 == null)) && (id2 == null)) {
            return;
        }
        reservationSubmissionTestHelper("reservation/submit", APPLICATION_JSON, time, "res_1", id1);
        reservationSubmissionTestHelper("reservation/submit", APPLICATION_JSON, (time + (TestRMWebServicesReservation.MINIMUM_RESOURCE_DURATION)), "res_2", id2);
        String timeParam = Long.toString((time + ((TestRMWebServicesReservation.MINIMUM_RESOURCE_DURATION) / 2)));
        WebResource resource = constructWebResource(TestRMWebServicesReservation.LIST_RESERVATION_PATH).queryParam("start-time", timeParam).queryParam("end-time", timeParam).queryParam("include-resource-allocations", "true").queryParam("queue", TestRMWebServicesReservation.DEFAULT_QUEUE);
        JSONObject json = testListReservationHelper(resource);
        if ((!(this.isAuthenticationEnabled())) && (json == null)) {
            return;
        }
        JSONObject reservations = json.getJSONObject("reservations");
        testRDLHelper(reservations);
        String reservationName = reservations.getJSONObject("reservation-definition").getString("reservation-name");
        Assert.assertEquals(reservationName, "res_1");
        stop();
    }

    @Test
    public void testInvalidTimeIntervalRequestListReservation() throws Exception {
        start();
        setupCluster(100);
        long time = (TestRMWebServicesReservation.clock.getTime()) + (TestRMWebServicesReservation.MINIMUM_RESOURCE_DURATION);
        ReservationId id1 = getReservationIdTestHelper(1);
        ReservationId id2 = getReservationIdTestHelper(2);
        reservationSubmissionTestHelper("reservation/submit", APPLICATION_JSON, time, "res_1", id1);
        reservationSubmissionTestHelper("reservation/submit", APPLICATION_JSON, (time + (TestRMWebServicesReservation.MINIMUM_RESOURCE_DURATION)), "res_2", id2);
        WebResource resource;
        resource = constructWebResource(TestRMWebServicesReservation.LIST_RESERVATION_PATH).queryParam("start-time", "-100").queryParam("end-time", "-100").queryParam("include-resource-allocations", "true").queryParam("queue", TestRMWebServicesReservation.DEFAULT_QUEUE);
        JSONObject json = testListReservationHelper(resource);
        if ((!(this.isAuthenticationEnabled())) && (json == null)) {
            return;
        }
        JSONArray reservations = json.getJSONArray("reservations");
        Assert.assertEquals(2, reservations.length());
        testRDLHelper(reservations.getJSONObject(0));
        testRDLHelper(reservations.getJSONObject(1));
        stop();
    }

    @Test
    public void testInvalidEndTimeRequestListReservation() throws Exception {
        start();
        setupCluster(100);
        long time = (TestRMWebServicesReservation.clock.getTime()) + (TestRMWebServicesReservation.MINIMUM_RESOURCE_DURATION);
        ReservationId id1 = getReservationIdTestHelper(1);
        ReservationId id2 = getReservationIdTestHelper(2);
        reservationSubmissionTestHelper("reservation/submit", APPLICATION_JSON, time, "res_1", id1);
        reservationSubmissionTestHelper("reservation/submit", APPLICATION_JSON, (time + (TestRMWebServicesReservation.MINIMUM_RESOURCE_DURATION)), "res_2", id2);
        WebResource resource = constructWebResource(TestRMWebServicesReservation.LIST_RESERVATION_PATH).queryParam("start-time", Long.toString(((long) (time + ((TestRMWebServicesReservation.MINIMUM_RESOURCE_DURATION) * 1.3))))).queryParam("end-time", "-1").queryParam("include-resource-allocations", "true").queryParam("queue", TestRMWebServicesReservation.DEFAULT_QUEUE);
        JSONObject json = testListReservationHelper(resource);
        if ((!(this.isAuthenticationEnabled())) && (json == null)) {
            return;
        }
        if (!(TestRMWebServicesReservation.enableRecurrence)) {
            JSONObject reservations = json.getJSONObject("reservations");
            testRDLHelper(reservations);
            String reservationName = reservations.getJSONObject("reservation-definition").getString("reservation-name");
            Assert.assertEquals("res_2", reservationName);
        } else {
            // In the case of recurring reservations, both reservations will be
            // picked up by the search interval since it is greater than the period
            // of the reservation.
            JSONArray reservations = json.getJSONArray("reservations");
            Assert.assertEquals(2, reservations.length());
        }
        stop();
    }

    @Test
    public void testEmptyEndTimeRequestListReservation() throws Exception {
        start();
        setupCluster(100);
        long time = (TestRMWebServicesReservation.clock.getTime()) + (TestRMWebServicesReservation.MINIMUM_RESOURCE_DURATION);
        ReservationId id1 = getReservationIdTestHelper(1);
        ReservationId id2 = getReservationIdTestHelper(2);
        reservationSubmissionTestHelper("reservation/submit", APPLICATION_JSON, time, "res_1", id1);
        reservationSubmissionTestHelper("reservation/submit", APPLICATION_JSON, (time + (TestRMWebServicesReservation.MINIMUM_RESOURCE_DURATION)), "res_2", id2);
        WebResource resource = constructWebResource(TestRMWebServicesReservation.LIST_RESERVATION_PATH).queryParam("start-time", new Long(((long) (time + ((TestRMWebServicesReservation.MINIMUM_RESOURCE_DURATION) * 1.3)))).toString()).queryParam("include-resource-allocations", "true").queryParam("queue", TestRMWebServicesReservation.DEFAULT_QUEUE);
        JSONObject json = testListReservationHelper(resource);
        if ((!(this.isAuthenticationEnabled())) && (json == null)) {
            return;
        }
        if (!(TestRMWebServicesReservation.enableRecurrence)) {
            JSONObject reservations = json.getJSONObject("reservations");
            testRDLHelper(reservations);
            String reservationName = reservations.getJSONObject("reservation-definition").getString("reservation-name");
            Assert.assertEquals("res_2", reservationName);
        } else {
            // In the case of recurring reservations, both reservations will be
            // picked up by the search interval since it is greater than the period
            // of the reservation.
            JSONArray reservations = json.getJSONArray("reservations");
            Assert.assertEquals(2, reservations.length());
        }
        stop();
    }

    @Test
    public void testInvalidStartTimeRequestListReservation() throws Exception {
        start();
        setupCluster(100);
        long time = (TestRMWebServicesReservation.clock.getTime()) + (TestRMWebServicesReservation.MINIMUM_RESOURCE_DURATION);
        ReservationId id1 = getReservationIdTestHelper(1);
        ReservationId id2 = getReservationIdTestHelper(2);
        reservationSubmissionTestHelper("reservation/submit", APPLICATION_JSON, time, "res_1", id1);
        reservationSubmissionTestHelper("reservation/submit", APPLICATION_JSON, (time + (TestRMWebServicesReservation.MINIMUM_RESOURCE_DURATION)), "res_2", id2);
        WebResource resource = constructWebResource(TestRMWebServicesReservation.LIST_RESERVATION_PATH).queryParam("start-time", "-1").queryParam("end-time", new Long(((long) (time + ((TestRMWebServicesReservation.MINIMUM_RESOURCE_DURATION) * 0.9)))).toString()).queryParam("include-resource-allocations", "true").queryParam("queue", TestRMWebServicesReservation.DEFAULT_QUEUE);
        JSONObject json = testListReservationHelper(resource);
        if ((!(this.isAuthenticationEnabled())) && (json == null)) {
            return;
        }
        JSONObject reservations = json.getJSONObject("reservations");
        testRDLHelper(reservations);
        // only res_1 should fall into the time interval given in the request json.
        String reservationName = reservations.getJSONObject("reservation-definition").getString("reservation-name");
        Assert.assertEquals(reservationName, "res_1");
        stop();
    }

    @Test
    public void testEmptyStartTimeRequestListReservation() throws Exception {
        start();
        setupCluster(100);
        ReservationId id1 = getReservationIdTestHelper(1);
        ReservationId id2 = getReservationIdTestHelper(2);
        long time = (TestRMWebServicesReservation.clock.getTime()) + (TestRMWebServicesReservation.MINIMUM_RESOURCE_DURATION);
        reservationSubmissionTestHelper("reservation/submit", APPLICATION_JSON, time, "res_1", id1);
        reservationSubmissionTestHelper("reservation/submit", APPLICATION_JSON, (time + (TestRMWebServicesReservation.MINIMUM_RESOURCE_DURATION)), "res_2", id2);
        WebResource resource = constructWebResource(TestRMWebServicesReservation.LIST_RESERVATION_PATH).queryParam("end-time", new Long(((long) (time + ((TestRMWebServicesReservation.MINIMUM_RESOURCE_DURATION) * 0.9)))).toString()).queryParam("include-resource-allocations", "true").queryParam("queue", TestRMWebServicesReservation.DEFAULT_QUEUE);
        JSONObject json = testListReservationHelper(resource);
        if ((!(this.isAuthenticationEnabled())) && (json == null)) {
            return;
        }
        JSONObject reservations = json.getJSONObject("reservations");
        testRDLHelper(reservations);
        // only res_1 should fall into the time interval given in the request json.
        String reservationName = reservations.getJSONObject("reservation-definition").getString("reservation-name");
        Assert.assertEquals(reservationName, "res_1");
        stop();
    }

    @Test
    public void testQueueOnlyRequestListReservation() throws Exception {
        start();
        setupCluster(100);
        ReservationId id1 = getReservationIdTestHelper(1);
        ReservationId id2 = getReservationIdTestHelper(2);
        reservationSubmissionTestHelper("reservation/submit", APPLICATION_JSON, TestRMWebServicesReservation.clock.getTime(), "res_1", id1);
        reservationSubmissionTestHelper("reservation/submit", APPLICATION_JSON, TestRMWebServicesReservation.clock.getTime(), "res_2", id2);
        WebResource resource = constructWebResource(TestRMWebServicesReservation.LIST_RESERVATION_PATH).queryParam("queue", TestRMWebServicesReservation.DEFAULT_QUEUE);
        JSONObject json = testListReservationHelper(resource);
        if ((!(this.isAuthenticationEnabled())) && (json == null)) {
            return;
        }
        Assert.assertEquals(json.getJSONArray("reservations").length(), 2);
        testRDLHelper(json.getJSONArray("reservations").getJSONObject(0));
        testRDLHelper(json.getJSONArray("reservations").getJSONObject(1));
        stop();
    }

    @Test
    public void testEmptyQueueRequestListReservation() throws Exception {
        start();
        setupCluster(100);
        ReservationId id1 = getReservationIdTestHelper(1);
        ReservationId id2 = getReservationIdTestHelper(2);
        reservationSubmissionTestHelper("reservation/submit", APPLICATION_JSON, TestRMWebServicesReservation.clock.getTime(), "res_1", id1);
        reservationSubmissionTestHelper("reservation/submit", APPLICATION_JSON, TestRMWebServicesReservation.clock.getTime(), "res_2", id2);
        WebResource resource = constructWebResource(TestRMWebServicesReservation.LIST_RESERVATION_PATH);
        testListReservationHelper(resource, BAD_REQUEST);
        stop();
    }

    @Test
    public void testNonExistentQueueRequestListReservation() throws Exception {
        start();
        setupCluster(100);
        ReservationId id1 = getReservationIdTestHelper(1);
        ReservationId id2 = getReservationIdTestHelper(2);
        reservationSubmissionTestHelper("reservation/submit", APPLICATION_JSON, TestRMWebServicesReservation.clock.getTime(), "res_1", id1);
        reservationSubmissionTestHelper("reservation/submit", APPLICATION_JSON, TestRMWebServicesReservation.clock.getTime(), "res_2", id2);
        WebResource resource = constructWebResource(TestRMWebServicesReservation.LIST_RESERVATION_PATH).queryParam("queue", ((TestRMWebServicesReservation.DEFAULT_QUEUE) + "_invalid"));
        testListReservationHelper(resource, BAD_REQUEST);
        stop();
    }

    @Test
    public void testReservationIdRequestListReservation() throws Exception {
        start();
        setupCluster(100);
        ReservationId id1 = getReservationIdTestHelper(1);
        ReservationId id2 = getReservationIdTestHelper(2);
        reservationSubmissionTestHelper("reservation/submit", APPLICATION_JSON, TestRMWebServicesReservation.clock.getTime(), "res_1", id1);
        reservationSubmissionTestHelper("reservation/submit", APPLICATION_JSON, TestRMWebServicesReservation.clock.getTime(), "res_1", id1);
        reservationSubmissionTestHelper("reservation/submit", APPLICATION_JSON, TestRMWebServicesReservation.clock.getTime(), "res_2", id2);
        WebResource resource = constructWebResource(TestRMWebServicesReservation.LIST_RESERVATION_PATH).queryParam("include-resource-allocations", "true").queryParam("queue", TestRMWebServicesReservation.DEFAULT_QUEUE);
        if (id1 != null) {
            resource = resource.queryParam("reservation-id", id1.toString());
        }
        JSONObject json = testListReservationHelper(resource);
        if ((!(this.isAuthenticationEnabled())) && (json == null)) {
            return;
        }
        JSONObject reservations = json.getJSONObject("reservations");
        testRDLHelper(reservations);
        String reservationId = reservations.getString("reservation-id");
        Assert.assertEquals(id1.toString(), reservationId);
        stop();
    }

    @Test
    public void testInvalidReservationIdRequestListReservation() throws Exception {
        start();
        setupCluster(100);
        ReservationId id1 = getReservationIdTestHelper(1);
        reservationSubmissionTestHelper("reservation/submit", APPLICATION_JSON, TestRMWebServicesReservation.clock.getTime(), "res_1", id1);
        WebResource resource = constructWebResource(TestRMWebServicesReservation.LIST_RESERVATION_PATH).queryParam("queue", TestRMWebServicesReservation.DEFAULT_QUEUE);
        if (id1 != null) {
            resource = resource.queryParam("reservation-id", ("invalid" + (id1.toString())));
        }
        JSONObject response = testListReservationHelper(resource, NOT_FOUND);
        stop();
    }

    @Test
    public void testIncludeResourceAllocations() throws Exception {
        start();
        setupCluster(100);
        ReservationId id1 = getReservationIdTestHelper(1);
        reservationSubmissionTestHelper("reservation/submit", APPLICATION_JSON, TestRMWebServicesReservation.clock.getTime(), "res_1", id1);
        WebResource resource = constructWebResource(TestRMWebServicesReservation.LIST_RESERVATION_PATH).queryParam("include-resource-allocations", "true").queryParam("queue", TestRMWebServicesReservation.DEFAULT_QUEUE);
        if (id1 != null) {
            resource = resource.queryParam("reservation-id", id1.toString());
        }
        JSONObject json = testListReservationHelper(resource);
        if ((!(this.isAuthenticationEnabled())) && (json == null)) {
            return;
        }
        JSONObject reservations = json.getJSONObject("reservations");
        testRDLHelper(reservations);
        String reservationId = reservations.getString("reservation-id");
        Assert.assertEquals(id1.toString(), reservationId);
        Assert.assertTrue(reservations.has("resource-allocations"));
        stop();
    }

    @Test
    public void testExcludeResourceAllocations() throws Exception {
        start();
        setupCluster(100);
        ReservationId id1 = getReservationIdTestHelper(1);
        reservationSubmissionTestHelper("reservation/submit", APPLICATION_JSON, TestRMWebServicesReservation.clock.getTime(), "res_1", id1);
        WebResource resource = constructWebResource(TestRMWebServicesReservation.LIST_RESERVATION_PATH).queryParam("include-resource-allocations", "false").queryParam("queue", TestRMWebServicesReservation.DEFAULT_QUEUE);
        if (id1 != null) {
            resource = resource.queryParam("reservation-id", id1.toString());
        }
        JSONObject json = testListReservationHelper(resource);
        if ((!(this.isAuthenticationEnabled())) && (json == null)) {
            return;
        }
        JSONObject reservations = json.getJSONObject("reservations");
        testRDLHelper(reservations);
        String reservationId = reservations.getString("reservation-id");
        Assert.assertEquals(id1.toString(), reservationId);
        Assert.assertTrue((!(reservations.has("resource-allocations"))));
        stop();
    }

    @Test
    public void testDeleteReservation() throws Exception, JSONException {
        start();
        for (int i = 0; i < 100; i++) {
            MockNM amNodeManager = TestRMWebServicesReservation.rm.registerNode((("127.0.0." + i) + ":1234"), (100 * 1024));
            amNodeManager.nodeHeartbeat(true);
        }
        ReservationId rid = getReservationIdTestHelper(1);
        reservationSubmissionTestHelper("reservation/submit", APPLICATION_JSON, rid);
        testDeleteReservationHelper("reservation/delete", rid, APPLICATION_JSON);
        stop();
    }
}

