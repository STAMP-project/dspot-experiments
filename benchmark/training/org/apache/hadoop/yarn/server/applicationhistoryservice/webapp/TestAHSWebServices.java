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
package org.apache.hadoop.yarn.server.applicationhistoryservice.webapp;


import AuthenticationFilter.AUTH_TYPE;
import ContainerLogAggregationType.AGGREGATED;
import ContainerLogAggregationType.LOCAL;
import ContainerState.COMPLETE;
import FinalApplicationStatus.UNDEFINED;
import MediaType.APPLICATION_JSON;
import MediaType.TEXT_PLAIN;
import PseudoAuthenticationHandler.ANONYMOUS_ALLOWED;
import YarnApplicationState.FINISHED;
import YarnWebServiceParams.NM_ID;
import YarnWebServiceParams.REDIRECTED_FROM_NODE;
import com.google.inject.Singleton;
import com.google.inject.servlet.GuiceFilter;
import com.google.inject.servlet.ServletModule;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.guice.spi.container.servlet.GuiceContainer;
import com.sun.jersey.test.framework.WebAppDescriptor;
import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.ws.rs.core.MediaType;
import javax.xml.bind.JAXBContext;
import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.http.JettyUtils;
import org.apache.hadoop.security.authentication.server.AuthenticationFilter;
import org.apache.hadoop.yarn.api.ApplicationBaseProtocol;
import org.apache.hadoop.yarn.api.records.ApplicationAttemptId;
import org.apache.hadoop.yarn.api.records.ApplicationId;
import org.apache.hadoop.yarn.api.records.ContainerId;
import org.apache.hadoop.yarn.api.records.NodeId;
import org.apache.hadoop.yarn.api.records.timeline.TimelineAbout;
import org.apache.hadoop.yarn.conf.YarnConfiguration;
import org.apache.hadoop.yarn.logaggregation.ContainerLogAggregationType;
import org.apache.hadoop.yarn.logaggregation.ContainerLogFileInfo;
import org.apache.hadoop.yarn.logaggregation.TestContainerLogsUtils;
import org.apache.hadoop.yarn.server.applicationhistoryservice.ApplicationHistoryClientService;
import org.apache.hadoop.yarn.server.webapp.LogWebServiceUtils;
import org.apache.hadoop.yarn.server.webapp.dao.ContainerLogsInfo;
import org.apache.hadoop.yarn.util.timeline.TimelineUtils;
import org.apache.hadoop.yarn.webapp.GenericExceptionHandler;
import org.apache.hadoop.yarn.webapp.GuiceServletConfig;
import org.apache.hadoop.yarn.webapp.JerseyTestBase;
import org.apache.hadoop.yarn.webapp.WebServicesTestUtils;
import org.apache.hadoop.yarn.webapp.util.WebAppUtils;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


@RunWith(Parameterized.class)
public class TestAHSWebServices extends JerseyTestBase {
    private static ApplicationHistoryClientService historyClientService;

    private static AHSWebServices ahsWebservice;

    private static final String[] USERS = new String[]{ "foo", "bar" };

    private static final int MAX_APPS = 6;

    private static Configuration conf;

    private static FileSystem fs;

    private static final String remoteLogRootDir = "target/logs/";

    private static final String rootLogDir = "target/LocalLogs";

    private static final String NM_WEBADDRESS = "test-nm-web-address:9999";

    private static final String NM_ID = "test:1234";

    private static class WebServletModule extends ServletModule {
        @Override
        protected void configureServlets() {
            bind(JAXBContextResolver.class);
            bind(AHSWebServices.class).toInstance(TestAHSWebServices.ahsWebservice);
            bind(GenericExceptionHandler.class);
            bind(ApplicationBaseProtocol.class).toInstance(TestAHSWebServices.historyClientService);
            serve("/*").with(GuiceContainer.class);
            filter("/*").through(TestAHSWebServices.TestSimpleAuthFilter.class);
        }
    }

    @Singleton
    public static class TestSimpleAuthFilter extends AuthenticationFilter {
        @Override
        protected Properties getConfiguration(String configPrefix, FilterConfig filterConfig) throws ServletException {
            Properties properties = super.getConfiguration(configPrefix, filterConfig);
            properties.put(AUTH_TYPE, "simple");
            properties.put(ANONYMOUS_ALLOWED, "false");
            return properties;
        }
    }

    private int round;

    public TestAHSWebServices(int round) {
        super(new WebAppDescriptor.Builder("org.apache.hadoop.yarn.server.applicationhistoryservice.webapp").contextListenerClass(GuiceServletConfig.class).filterClass(GuiceFilter.class).contextPath("jersey-guice-filter").servletPath("/").build());
        this.round = round;
    }

    @Test
    public void testInvalidApp() {
        ApplicationId appId = ApplicationId.newInstance(0, ((TestAHSWebServices.MAX_APPS) + 1));
        WebResource r = resource();
        ClientResponse response = r.path("ws").path("v1").path("applicationhistory").path("apps").path(appId.toString()).queryParam("user.name", TestAHSWebServices.USERS[round]).accept(APPLICATION_JSON).get(ClientResponse.class);
        assertResponseStatusCode("404 not found expected", Status.NOT_FOUND, response.getStatusInfo());
    }

    @Test
    public void testInvalidAttempt() {
        ApplicationId appId = ApplicationId.newInstance(0, 1);
        ApplicationAttemptId appAttemptId = ApplicationAttemptId.newInstance(appId, ((TestAHSWebServices.MAX_APPS) + 1));
        WebResource r = resource();
        ClientResponse response = r.path("ws").path("v1").path("applicationhistory").path("apps").path(appId.toString()).path("appattempts").path(appAttemptId.toString()).queryParam("user.name", TestAHSWebServices.USERS[round]).accept(APPLICATION_JSON).get(ClientResponse.class);
        if ((round) == 1) {
            assertResponseStatusCode(Status.FORBIDDEN, response.getStatusInfo());
            return;
        }
        assertResponseStatusCode("404 not found expected", Status.NOT_FOUND, response.getStatusInfo());
    }

    @Test
    public void testInvalidContainer() throws Exception {
        ApplicationId appId = ApplicationId.newInstance(0, 1);
        ApplicationAttemptId appAttemptId = ApplicationAttemptId.newInstance(appId, 1);
        ContainerId containerId = ContainerId.newContainerId(appAttemptId, ((TestAHSWebServices.MAX_APPS) + 1));
        WebResource r = resource();
        ClientResponse response = r.path("ws").path("v1").path("applicationhistory").path("apps").path(appId.toString()).path("appattempts").path(appAttemptId.toString()).path("containers").path(containerId.toString()).queryParam("user.name", TestAHSWebServices.USERS[round]).accept(APPLICATION_JSON).get(ClientResponse.class);
        if ((round) == 1) {
            assertResponseStatusCode(Status.FORBIDDEN, response.getStatusInfo());
            return;
        }
        assertResponseStatusCode("404 not found expected", Status.NOT_FOUND, response.getStatusInfo());
    }

    @Test
    public void testInvalidUri() throws Exception, JSONException {
        WebResource r = resource();
        String responseStr = "";
        try {
            responseStr = r.path("ws").path("v1").path("applicationhistory").path("bogus").queryParam("user.name", TestAHSWebServices.USERS[round]).accept(APPLICATION_JSON).get(String.class);
            Assert.fail("should have thrown exception on invalid uri");
        } catch (UniformInterfaceException ue) {
            ClientResponse response = ue.getResponse();
            assertResponseStatusCode(Status.NOT_FOUND, response.getStatusInfo());
            WebServicesTestUtils.checkStringMatch("error string exists and shouldn't", "", responseStr);
        }
    }

    @Test
    public void testInvalidUri2() throws Exception, JSONException {
        WebResource r = resource();
        String responseStr = "";
        try {
            responseStr = r.queryParam("user.name", TestAHSWebServices.USERS[round]).accept(APPLICATION_JSON).get(String.class);
            Assert.fail("should have thrown exception on invalid uri");
        } catch (UniformInterfaceException ue) {
            ClientResponse response = ue.getResponse();
            assertResponseStatusCode(Status.NOT_FOUND, response.getStatusInfo());
            WebServicesTestUtils.checkStringMatch("error string exists and shouldn't", "", responseStr);
        }
    }

    @Test
    public void testInvalidAccept() throws Exception, JSONException {
        WebResource r = resource();
        String responseStr = "";
        try {
            responseStr = r.path("ws").path("v1").path("applicationhistory").queryParam("user.name", TestAHSWebServices.USERS[round]).accept(TEXT_PLAIN).get(String.class);
            Assert.fail("should have thrown exception on invalid uri");
        } catch (UniformInterfaceException ue) {
            ClientResponse response = ue.getResponse();
            assertResponseStatusCode(Status.INTERNAL_SERVER_ERROR, response.getStatusInfo());
            WebServicesTestUtils.checkStringMatch("error string exists and shouldn't", "", responseStr);
        }
    }

    @Test
    public void testAbout() throws Exception {
        WebResource r = resource();
        ClientResponse response = r.path("ws").path("v1").path("applicationhistory").path("about").queryParam("user.name", TestAHSWebServices.USERS[round]).accept(APPLICATION_JSON).get(ClientResponse.class);
        Assert.assertEquals((((MediaType.APPLICATION_JSON) + "; ") + (JettyUtils.UTF_8)), response.getType().toString());
        TimelineAbout actualAbout = response.getEntity(TimelineAbout.class);
        TimelineAbout expectedAbout = TimelineUtils.createTimelineAbout("Generic History Service API");
        Assert.assertNotNull("Timeline service about response is null", actualAbout);
        Assert.assertEquals(expectedAbout.getAbout(), actualAbout.getAbout());
        Assert.assertEquals(expectedAbout.getTimelineServiceVersion(), actualAbout.getTimelineServiceVersion());
        Assert.assertEquals(expectedAbout.getTimelineServiceBuildVersion(), actualAbout.getTimelineServiceBuildVersion());
        Assert.assertEquals(expectedAbout.getTimelineServiceVersionBuiltOn(), actualAbout.getTimelineServiceVersionBuiltOn());
        Assert.assertEquals(expectedAbout.getHadoopVersion(), actualAbout.getHadoopVersion());
        Assert.assertEquals(expectedAbout.getHadoopBuildVersion(), actualAbout.getHadoopBuildVersion());
        Assert.assertEquals(expectedAbout.getHadoopVersionBuiltOn(), actualAbout.getHadoopVersionBuiltOn());
    }

    @Test
    public void testAppsQuery() throws Exception {
        WebResource r = resource();
        ClientResponse response = r.path("ws").path("v1").path("applicationhistory").path("apps").queryParam("state", FINISHED.toString()).queryParam("user.name", TestAHSWebServices.USERS[round]).accept(APPLICATION_JSON).get(ClientResponse.class);
        Assert.assertEquals((((MediaType.APPLICATION_JSON) + "; ") + (JettyUtils.UTF_8)), response.getType().toString());
        JSONObject json = response.getEntity(JSONObject.class);
        Assert.assertEquals("incorrect number of elements", 1, json.length());
        JSONObject apps = json.getJSONObject("apps");
        Assert.assertEquals("incorrect number of elements", 1, apps.length());
        JSONArray array = apps.getJSONArray("app");
        Assert.assertEquals("incorrect number of elements", TestAHSWebServices.MAX_APPS, array.length());
    }

    @Test
    public void testQueueQuery() throws Exception {
        WebResource r = resource();
        ClientResponse response = r.path("ws").path("v1").path("applicationhistory").path("apps").queryParam("queue", "test queue").queryParam("user.name", TestAHSWebServices.USERS[round]).accept(APPLICATION_JSON).get(ClientResponse.class);
        assertResponseStatusCode(Status.OK, response.getStatusInfo());
        Assert.assertEquals((((MediaType.APPLICATION_JSON) + "; ") + (JettyUtils.UTF_8)), response.getType().toString());
        JSONObject json = response.getEntity(JSONObject.class);
        Assert.assertEquals("incorrect number of elements", 1, json.length());
        JSONObject apps = json.getJSONObject("apps");
        Assert.assertEquals("incorrect number of elements", 1, apps.length());
        JSONArray array = apps.getJSONArray("app");
        Assert.assertEquals("incorrect number of elements", ((TestAHSWebServices.MAX_APPS) - 1), array.length());
    }

    @Test
    public void testSingleApp() throws Exception {
        ApplicationId appId = ApplicationId.newInstance(0, 1);
        WebResource r = resource();
        ClientResponse response = r.path("ws").path("v1").path("applicationhistory").path("apps").path(appId.toString()).queryParam("user.name", TestAHSWebServices.USERS[round]).accept(APPLICATION_JSON).get(ClientResponse.class);
        Assert.assertEquals((((MediaType.APPLICATION_JSON) + "; ") + (JettyUtils.UTF_8)), response.getType().toString());
        JSONObject json = response.getEntity(JSONObject.class);
        Assert.assertEquals("incorrect number of elements", 1, json.length());
        JSONObject app = json.getJSONObject("app");
        Assert.assertEquals(appId.toString(), app.getString("appId"));
        Assert.assertEquals("test app", app.get("name"));
        Assert.assertEquals(((round) == 0 ? "test diagnostics info" : ""), app.get("diagnosticsInfo"));
        Assert.assertEquals(((Integer.MAX_VALUE) + 1L), app.get("submittedTime"));
        Assert.assertEquals("test queue", app.get("queue"));
        Assert.assertEquals("user1", app.get("user"));
        Assert.assertEquals("test app type", app.get("type"));
        Assert.assertEquals(UNDEFINED.toString(), app.get("finalAppStatus"));
        Assert.assertEquals(FINISHED.toString(), app.get("appState"));
    }

    @Test
    public void testMultipleAttempts() throws Exception {
        ApplicationId appId = ApplicationId.newInstance(0, 1);
        WebResource r = resource();
        ClientResponse response = r.path("ws").path("v1").path("applicationhistory").path("apps").path(appId.toString()).path("appattempts").queryParam("user.name", TestAHSWebServices.USERS[round]).accept(APPLICATION_JSON).get(ClientResponse.class);
        if ((round) == 1) {
            assertResponseStatusCode(Status.FORBIDDEN, response.getStatusInfo());
            return;
        }
        Assert.assertEquals((((MediaType.APPLICATION_JSON) + "; ") + (JettyUtils.UTF_8)), response.getType().toString());
        JSONObject json = response.getEntity(JSONObject.class);
        Assert.assertEquals("incorrect number of elements", 1, json.length());
        JSONObject appAttempts = json.getJSONObject("appAttempts");
        Assert.assertEquals("incorrect number of elements", 1, appAttempts.length());
        JSONArray array = appAttempts.getJSONArray("appAttempt");
        Assert.assertEquals("incorrect number of elements", TestAHSWebServices.MAX_APPS, array.length());
    }

    @Test
    public void testSingleAttempt() throws Exception {
        ApplicationId appId = ApplicationId.newInstance(0, 1);
        ApplicationAttemptId appAttemptId = ApplicationAttemptId.newInstance(appId, 1);
        WebResource r = resource();
        ClientResponse response = r.path("ws").path("v1").path("applicationhistory").path("apps").path(appId.toString()).path("appattempts").path(appAttemptId.toString()).queryParam("user.name", TestAHSWebServices.USERS[round]).accept(APPLICATION_JSON).get(ClientResponse.class);
        if ((round) == 1) {
            assertResponseStatusCode(Status.FORBIDDEN, response.getStatusInfo());
            return;
        }
        Assert.assertEquals((((MediaType.APPLICATION_JSON) + "; ") + (JettyUtils.UTF_8)), response.getType().toString());
        JSONObject json = response.getEntity(JSONObject.class);
        Assert.assertEquals("incorrect number of elements", 1, json.length());
        JSONObject appAttempt = json.getJSONObject("appAttempt");
        Assert.assertEquals(appAttemptId.toString(), appAttempt.getString("appAttemptId"));
        Assert.assertEquals("test host", appAttempt.getString("host"));
        Assert.assertEquals("test diagnostics info", appAttempt.getString("diagnosticsInfo"));
        Assert.assertEquals("test tracking url", appAttempt.getString("trackingUrl"));
        Assert.assertEquals(YarnApplicationAttemptState.FINISHED.toString(), appAttempt.get("appAttemptState"));
    }

    @Test
    public void testMultipleContainers() throws Exception {
        ApplicationId appId = ApplicationId.newInstance(0, 1);
        ApplicationAttemptId appAttemptId = ApplicationAttemptId.newInstance(appId, 1);
        WebResource r = resource();
        ClientResponse response = r.path("ws").path("v1").path("applicationhistory").path("apps").path(appId.toString()).path("appattempts").path(appAttemptId.toString()).path("containers").queryParam("user.name", TestAHSWebServices.USERS[round]).accept(APPLICATION_JSON).get(ClientResponse.class);
        if ((round) == 1) {
            assertResponseStatusCode(Status.FORBIDDEN, response.getStatusInfo());
            return;
        }
        Assert.assertEquals((((MediaType.APPLICATION_JSON) + "; ") + (JettyUtils.UTF_8)), response.getType().toString());
        JSONObject json = response.getEntity(JSONObject.class);
        Assert.assertEquals("incorrect number of elements", 1, json.length());
        JSONObject containers = json.getJSONObject("containers");
        Assert.assertEquals("incorrect number of elements", 1, containers.length());
        JSONArray array = containers.getJSONArray("container");
        Assert.assertEquals("incorrect number of elements", TestAHSWebServices.MAX_APPS, array.length());
    }

    @Test
    public void testSingleContainer() throws Exception {
        ApplicationId appId = ApplicationId.newInstance(0, 1);
        ApplicationAttemptId appAttemptId = ApplicationAttemptId.newInstance(appId, 1);
        ContainerId containerId = ContainerId.newContainerId(appAttemptId, 1);
        WebResource r = resource();
        ClientResponse response = r.path("ws").path("v1").path("applicationhistory").path("apps").path(appId.toString()).path("appattempts").path(appAttemptId.toString()).path("containers").path(containerId.toString()).queryParam("user.name", TestAHSWebServices.USERS[round]).accept(APPLICATION_JSON).get(ClientResponse.class);
        if ((round) == 1) {
            assertResponseStatusCode(Status.FORBIDDEN, response.getStatusInfo());
            return;
        }
        Assert.assertEquals((((MediaType.APPLICATION_JSON) + "; ") + (JettyUtils.UTF_8)), response.getType().toString());
        JSONObject json = response.getEntity(JSONObject.class);
        Assert.assertEquals("incorrect number of elements", 1, json.length());
        JSONObject container = json.getJSONObject("container");
        Assert.assertEquals(containerId.toString(), container.getString("containerId"));
        Assert.assertEquals("test diagnostics info", container.getString("diagnosticsInfo"));
        Assert.assertEquals("-1", container.getString("allocatedMB"));
        Assert.assertEquals("-1", container.getString("allocatedVCores"));
        Assert.assertEquals(NodeId.newInstance("test host", 100).toString(), container.getString("assignedNodeId"));
        Assert.assertEquals("-1", container.getString("priority"));
        Configuration conf = new YarnConfiguration();
        Assert.assertEquals(((((WebAppUtils.getHttpSchemePrefix(conf)) + (WebAppUtils.getAHSWebAppURLWithoutScheme(conf))) + "/applicationhistory/logs/test host:100/container_0_0001_01_000001/") + "container_0_0001_01_000001/user1"), container.getString("logUrl"));
        Assert.assertEquals(COMPLETE.toString(), container.getString("containerState"));
    }

    @Test(timeout = 10000)
    public void testContainerLogsForFinishedApps() throws Exception {
        String fileName = "syslog";
        String user = "user1";
        NodeId nodeId = NodeId.newInstance("test host", 100);
        NodeId nodeId2 = NodeId.newInstance("host2", 1234);
        ApplicationId appId = ApplicationId.newInstance(0, 1);
        ApplicationAttemptId appAttemptId = ApplicationAttemptId.newInstance(appId, 1);
        ContainerId containerId1 = ContainerId.newContainerId(appAttemptId, 1);
        ContainerId containerId100 = ContainerId.newContainerId(appAttemptId, 100);
        TestContainerLogsUtils.createContainerLogFileInRemoteFS(TestAHSWebServices.conf, TestAHSWebServices.fs, TestAHSWebServices.rootLogDir, containerId1, nodeId, fileName, user, ("Hello." + containerId1), true);
        TestContainerLogsUtils.createContainerLogFileInRemoteFS(TestAHSWebServices.conf, TestAHSWebServices.fs, TestAHSWebServices.rootLogDir, containerId100, nodeId2, fileName, user, ("Hello." + containerId100), false);
        // test whether we can find container log from remote diretory if
        // the containerInfo for this container could be fetched from AHS.
        WebResource r = resource();
        ClientResponse response = r.path("ws").path("v1").path("applicationhistory").path("containerlogs").path(containerId1.toString()).path(fileName).queryParam("user.name", user).accept(TEXT_PLAIN).get(ClientResponse.class);
        String responseText = response.getEntity(String.class);
        Assert.assertTrue(responseText.contains(("Hello." + containerId1)));
        // Do the same test with new API
        r = resource();
        response = r.path("ws").path("v1").path("applicationhistory").path("containers").path(containerId1.toString()).path("logs").path(fileName).queryParam("user.name", user).accept(TEXT_PLAIN).get(ClientResponse.class);
        responseText = response.getEntity(String.class);
        Assert.assertTrue(responseText.contains(("Hello." + containerId1)));
        // test whether we can find container log from remote diretory if
        // the containerInfo for this container could not be fetched from AHS.
        r = resource();
        response = r.path("ws").path("v1").path("applicationhistory").path("containerlogs").path(containerId100.toString()).path(fileName).queryParam("user.name", user).accept(TEXT_PLAIN).get(ClientResponse.class);
        responseText = response.getEntity(String.class);
        Assert.assertTrue(responseText.contains(("Hello." + containerId100)));
        // Do the same test with new API
        r = resource();
        response = r.path("ws").path("v1").path("applicationhistory").path("containers").path(containerId100.toString()).path("logs").path(fileName).queryParam("user.name", user).accept(TEXT_PLAIN).get(ClientResponse.class);
        responseText = response.getEntity(String.class);
        Assert.assertTrue(responseText.contains(("Hello." + containerId100)));
        // create an application which can not be found from AHS
        ApplicationId appId100 = ApplicationId.newInstance(0, 100);
        ApplicationAttemptId appAttemptId100 = ApplicationAttemptId.newInstance(appId100, 1);
        ContainerId containerId1ForApp100 = ContainerId.newContainerId(appAttemptId100, 1);
        TestContainerLogsUtils.createContainerLogFileInRemoteFS(TestAHSWebServices.conf, TestAHSWebServices.fs, TestAHSWebServices.rootLogDir, containerId1ForApp100, nodeId, fileName, user, ("Hello." + containerId1ForApp100), true);
        r = resource();
        response = r.path("ws").path("v1").path("applicationhistory").path("containerlogs").path(containerId1ForApp100.toString()).path(fileName).queryParam("user.name", user).accept(TEXT_PLAIN).get(ClientResponse.class);
        responseText = response.getEntity(String.class);
        Assert.assertTrue(responseText.contains(("Hello." + containerId1ForApp100)));
        int fullTextSize = responseText.getBytes().length;
        String tailEndSeparator = (StringUtils.repeat("*", (("End of LogType:syslog".length()) + 50))) + "\n\n";
        int tailTextSize = ("\nEnd of LogType:syslog\n".getBytes().length) + (tailEndSeparator.getBytes().length);
        String logMessage = "Hello." + containerId1ForApp100;
        int fileContentSize = logMessage.getBytes().length;
        // specify how many bytes we should get from logs
        // if we specify a position number, it would get the first n bytes from
        // container log
        r = resource();
        response = r.path("ws").path("v1").path("applicationhistory").path("containerlogs").path(containerId1ForApp100.toString()).path(fileName).queryParam("user.name", user).queryParam("size", "5").accept(TEXT_PLAIN).get(ClientResponse.class);
        responseText = response.getEntity(String.class);
        Assert.assertEquals(responseText.getBytes().length, ((fullTextSize - fileContentSize) + 5));
        Assert.assertTrue((fullTextSize >= (responseText.getBytes().length)));
        Assert.assertEquals(new String(responseText.getBytes(), ((fullTextSize - fileContentSize) - tailTextSize), 5), new String(logMessage.getBytes(), 0, 5));
        // specify how many bytes we should get from logs
        // if we specify a negative number, it would get the last n bytes from
        // container log
        r = resource();
        response = r.path("ws").path("v1").path("applicationhistory").path("containerlogs").path(containerId1ForApp100.toString()).path(fileName).queryParam("user.name", user).queryParam("size", "-5").accept(TEXT_PLAIN).get(ClientResponse.class);
        responseText = response.getEntity(String.class);
        Assert.assertEquals(responseText.getBytes().length, ((fullTextSize - fileContentSize) + 5));
        Assert.assertTrue((fullTextSize >= (responseText.getBytes().length)));
        Assert.assertEquals(new String(responseText.getBytes(), ((fullTextSize - fileContentSize) - tailTextSize), 5), new String(logMessage.getBytes(), (fileContentSize - 5), 5));
        // specify the bytes which is larger than the actual file size,
        // we would get the full logs
        r = resource();
        response = r.path("ws").path("v1").path("applicationhistory").path("containerlogs").path(containerId1ForApp100.toString()).path(fileName).queryParam("user.name", user).queryParam("size", "10000").accept(TEXT_PLAIN).get(ClientResponse.class);
        responseText = response.getEntity(String.class);
        Assert.assertEquals(responseText.getBytes().length, fullTextSize);
        r = resource();
        response = r.path("ws").path("v1").path("applicationhistory").path("containerlogs").path(containerId1ForApp100.toString()).path(fileName).queryParam("user.name", user).queryParam("size", "-10000").accept(TEXT_PLAIN).get(ClientResponse.class);
        responseText = response.getEntity(String.class);
        Assert.assertEquals(responseText.getBytes().length, fullTextSize);
    }

    @Test(timeout = 10000)
    public void testContainerLogsForRunningApps() throws Exception {
        String fileName = "syslog";
        String user = "user1";
        ApplicationId appId = ApplicationId.newInstance(1234, 1);
        ApplicationAttemptId appAttemptId = ApplicationAttemptId.newInstance(appId, 1);
        ContainerId containerId1 = ContainerId.newContainerId(appAttemptId, 1);
        WebResource r = resource();
        URI requestURI = r.path("ws").path("v1").path("applicationhistory").path("containerlogs").path(containerId1.toString()).path(fileName).queryParam("user.name", user).getURI();
        String redirectURL = TestAHSWebServices.getRedirectURL(requestURI.toString());
        Assert.assertTrue((redirectURL != null));
        Assert.assertTrue(redirectURL.contains("test:1234"));
        Assert.assertTrue(redirectURL.contains("ws/v1/node/containers"));
        Assert.assertTrue(redirectURL.contains(containerId1.toString()));
        Assert.assertTrue(redirectURL.contains(("/logs/" + fileName)));
        Assert.assertTrue(redirectURL.contains(("user.name=" + user)));
        // If we specify NM id, we would re-direct the request
        // to this NM's Web Address.
        requestURI = r.path("ws").path("v1").path("applicationhistory").path("containerlogs").path(containerId1.toString()).path(fileName).queryParam("user.name", user).queryParam(YarnWebServiceParams.NM_ID, TestAHSWebServices.NM_ID).getURI();
        redirectURL = TestAHSWebServices.getRedirectURL(requestURI.toString());
        Assert.assertTrue((redirectURL != null));
        Assert.assertTrue(redirectURL.contains(TestAHSWebServices.NM_WEBADDRESS));
        Assert.assertTrue(redirectURL.contains("ws/v1/node/containers"));
        Assert.assertTrue(redirectURL.contains(containerId1.toString()));
        Assert.assertTrue(redirectURL.contains(("/logs/" + fileName)));
        Assert.assertTrue(redirectURL.contains(("user.name=" + user)));
        // Test with new API
        requestURI = r.path("ws").path("v1").path("applicationhistory").path("containers").path(containerId1.toString()).path("logs").path(fileName).queryParam("user.name", user).getURI();
        redirectURL = TestAHSWebServices.getRedirectURL(requestURI.toString());
        Assert.assertTrue((redirectURL != null));
        Assert.assertTrue(redirectURL.contains("test:1234"));
        Assert.assertTrue(redirectURL.contains("ws/v1/node/containers"));
        Assert.assertTrue(redirectURL.contains(containerId1.toString()));
        Assert.assertTrue(redirectURL.contains(("/logs/" + fileName)));
        Assert.assertTrue(redirectURL.contains(("user.name=" + user)));
        requestURI = r.path("ws").path("v1").path("applicationhistory").path("containers").path(containerId1.toString()).path("logs").path(fileName).queryParam("user.name", user).queryParam(YarnWebServiceParams.NM_ID, TestAHSWebServices.NM_ID).getURI();
        redirectURL = TestAHSWebServices.getRedirectURL(requestURI.toString());
        Assert.assertTrue((redirectURL != null));
        Assert.assertTrue(redirectURL.contains(TestAHSWebServices.NM_WEBADDRESS));
        Assert.assertTrue(redirectURL.contains("ws/v1/node/containers"));
        Assert.assertTrue(redirectURL.contains(containerId1.toString()));
        Assert.assertTrue(redirectURL.contains(("/logs/" + fileName)));
        Assert.assertTrue(redirectURL.contains(("user.name=" + user)));
        // If we can not container information from ATS, we would try to
        // get aggregated log from remote FileSystem.
        ContainerId containerId1000 = ContainerId.newContainerId(appAttemptId, 1000);
        String content = "Hello." + containerId1000;
        NodeId nodeId = NodeId.newInstance("test host", 100);
        TestContainerLogsUtils.createContainerLogFileInRemoteFS(TestAHSWebServices.conf, TestAHSWebServices.fs, TestAHSWebServices.rootLogDir, containerId1000, nodeId, fileName, user, content, true);
        r = resource();
        ClientResponse response = r.path("ws").path("v1").path("applicationhistory").path("containerlogs").path(containerId1000.toString()).path(fileName).queryParam("user.name", user).accept(TEXT_PLAIN).get(ClientResponse.class);
        String responseText = response.getEntity(String.class);
        Assert.assertTrue(responseText.contains(content));
        // Also test whether we output the empty local container log, and give
        // the warning message.
        Assert.assertTrue(responseText.contains(("LogAggregationType: " + (ContainerLogAggregationType.LOCAL))));
        Assert.assertTrue(responseText.contains(LogWebServiceUtils.getNoRedirectWarning()));
        // If we can not container information from ATS, and we specify the NM id,
        // but we can not get nm web address, we would still try to
        // get aggregated log from remote FileSystem.
        response = r.path("ws").path("v1").path("applicationhistory").path("containerlogs").path(containerId1000.toString()).path(fileName).queryParam(YarnWebServiceParams.NM_ID, "invalid-nm:1234").queryParam("user.name", user).accept(TEXT_PLAIN).get(ClientResponse.class);
        responseText = response.getEntity(String.class);
        Assert.assertTrue(responseText.contains(content));
        Assert.assertTrue(responseText.contains(("LogAggregationType: " + (ContainerLogAggregationType.LOCAL))));
        Assert.assertTrue(responseText.contains(LogWebServiceUtils.getNoRedirectWarning()));
        // If this is the redirect request, we would not re-direct the request
        // back and get the aggregated logs.
        String content1 = "Hello." + containerId1;
        NodeId nodeId1 = NodeId.fromString(TestAHSWebServices.NM_ID);
        TestContainerLogsUtils.createContainerLogFileInRemoteFS(TestAHSWebServices.conf, TestAHSWebServices.fs, TestAHSWebServices.rootLogDir, containerId1, nodeId1, fileName, user, content1, true);
        response = r.path("ws").path("v1").path("applicationhistory").path("containers").path(containerId1.toString()).path("logs").path(fileName).queryParam("user.name", user).queryParam(REDIRECTED_FROM_NODE, "true").accept(TEXT_PLAIN).get(ClientResponse.class);
        responseText = response.getEntity(String.class);
        Assert.assertTrue(responseText.contains(content1));
        Assert.assertTrue(responseText.contains(("LogAggregationType: " + (ContainerLogAggregationType.AGGREGATED))));
    }

    @Test(timeout = 10000)
    public void testContainerLogsMetaForRunningApps() throws Exception {
        String user = "user1";
        ApplicationId appId = ApplicationId.newInstance(1234, 1);
        ApplicationAttemptId appAttemptId = ApplicationAttemptId.newInstance(appId, 1);
        ContainerId containerId1 = ContainerId.newContainerId(appAttemptId, 1);
        WebResource r = resource();
        // If we specify the NMID, we re-direct the request by using
        // the NM's web address
        URI requestURI = r.path("ws").path("v1").path("applicationhistory").path("containers").path(containerId1.toString()).path("logs").queryParam("user.name", user).queryParam(YarnWebServiceParams.NM_ID, TestAHSWebServices.NM_ID).getURI();
        String redirectURL = TestAHSWebServices.getRedirectURL(requestURI.toString());
        Assert.assertTrue((redirectURL != null));
        Assert.assertTrue(redirectURL.contains(TestAHSWebServices.NM_WEBADDRESS));
        Assert.assertTrue(redirectURL.contains("ws/v1/node/containers"));
        Assert.assertTrue(redirectURL.contains(containerId1.toString()));
        Assert.assertTrue(redirectURL.contains("/logs"));
        // If we do not specify the NodeId but can get Container information
        // from ATS, we re-direct the request to the node manager
        // who runs the container.
        requestURI = r.path("ws").path("v1").path("applicationhistory").path("containers").path(containerId1.toString()).path("logs").queryParam("user.name", user).getURI();
        redirectURL = TestAHSWebServices.getRedirectURL(requestURI.toString());
        Assert.assertTrue((redirectURL != null));
        Assert.assertTrue(redirectURL.contains("test:1234"));
        Assert.assertTrue(redirectURL.contains("ws/v1/node/containers"));
        Assert.assertTrue(redirectURL.contains(containerId1.toString()));
        Assert.assertTrue(redirectURL.contains("/logs"));
        // If we can not container information from ATS,
        // and not specify nodeId,
        // we would try to get aggregated log meta from remote FileSystem.
        ContainerId containerId1000 = ContainerId.newContainerId(appAttemptId, 1000);
        String fileName = "syslog";
        String content = "Hello." + containerId1000;
        NodeId nodeId = NodeId.newInstance("test host", 100);
        TestContainerLogsUtils.createContainerLogFileInRemoteFS(TestAHSWebServices.conf, TestAHSWebServices.fs, TestAHSWebServices.rootLogDir, containerId1000, nodeId, fileName, user, content, true);
        ClientResponse response = r.path("ws").path("v1").path("applicationhistory").path("containers").path(containerId1000.toString()).path("logs").queryParam("user.name", user).accept(APPLICATION_JSON).get(ClientResponse.class);
        List<ContainerLogsInfo> responseText = response.getEntity(new com.sun.jersey.api.client.GenericType<List<ContainerLogsInfo>>() {});
        Assert.assertTrue(((responseText.size()) == 2));
        for (ContainerLogsInfo logInfo : responseText) {
            if (logInfo.getLogType().equals(AGGREGATED.toString())) {
                List<ContainerLogFileInfo> logMeta = logInfo.getContainerLogsInfo();
                Assert.assertTrue(((logMeta.size()) == 1));
                Assert.assertEquals(logMeta.get(0).getFileName(), fileName);
                Assert.assertEquals(logMeta.get(0).getFileSize(), String.valueOf(content.length()));
            } else {
                Assert.assertEquals(logInfo.getLogType(), LOCAL.toString());
            }
        }
        // If we can not container information from ATS,
        // and we specify NM id, but can not find NM WebAddress for this nodeId,
        // we would still try to get aggregated log meta from remote FileSystem.
        response = r.path("ws").path("v1").path("applicationhistory").path("containers").path(containerId1000.toString()).path("logs").queryParam(YarnWebServiceParams.NM_ID, "invalid-nm:1234").queryParam("user.name", user).accept(APPLICATION_JSON).get(ClientResponse.class);
        responseText = response.getEntity(new com.sun.jersey.api.client.GenericType<List<ContainerLogsInfo>>() {});
        Assert.assertTrue(((responseText.size()) == 2));
        for (ContainerLogsInfo logInfo : responseText) {
            if (logInfo.getLogType().equals(AGGREGATED.toString())) {
                List<ContainerLogFileInfo> logMeta = logInfo.getContainerLogsInfo();
                Assert.assertTrue(((logMeta.size()) == 1));
                Assert.assertEquals(logMeta.get(0).getFileName(), fileName);
                Assert.assertEquals(logMeta.get(0).getFileSize(), String.valueOf(content.length()));
            } else {
                Assert.assertEquals(logInfo.getLogType(), LOCAL.toString());
            }
        }
    }

    @Test(timeout = 10000)
    public void testContainerLogsMetaForFinishedApps() throws Exception {
        ApplicationId appId = ApplicationId.newInstance(0, 1);
        ApplicationAttemptId appAttemptId = ApplicationAttemptId.newInstance(appId, 1);
        ContainerId containerId1 = ContainerId.newContainerId(appAttemptId, 1);
        String fileName = "syslog";
        String user = "user1";
        String content = "Hello." + containerId1;
        NodeId nodeId = NodeId.newInstance("test host", 100);
        TestContainerLogsUtils.createContainerLogFileInRemoteFS(TestAHSWebServices.conf, TestAHSWebServices.fs, TestAHSWebServices.rootLogDir, containerId1, nodeId, fileName, user, content, true);
        WebResource r = resource();
        ClientResponse response = r.path("ws").path("v1").path("applicationhistory").path("containers").path(containerId1.toString()).path("logs").queryParam("user.name", user).accept(APPLICATION_JSON).get(ClientResponse.class);
        List<ContainerLogsInfo> responseText = response.getEntity(new com.sun.jersey.api.client.GenericType<List<ContainerLogsInfo>>() {});
        Assert.assertTrue(((responseText.size()) == 1));
        Assert.assertEquals(responseText.get(0).getLogType(), AGGREGATED.toString());
        List<ContainerLogFileInfo> logMeta = responseText.get(0).getContainerLogsInfo();
        Assert.assertTrue(((logMeta.size()) == 1));
        Assert.assertEquals(logMeta.get(0).getFileName(), fileName);
        Assert.assertEquals(logMeta.get(0).getFileSize(), String.valueOf(content.length()));
    }

    @Test
    public void testContextFactory() throws Exception {
        JAXBContext jaxbContext1 = ContextFactory.createContext(new Class[]{  }, Collections.EMPTY_MAP);
        JAXBContext jaxbContext2 = ContextFactory.createContext(new Class[]{  }, Collections.EMPTY_MAP);
        Assert.assertEquals(jaxbContext1, jaxbContext2);
    }
}

