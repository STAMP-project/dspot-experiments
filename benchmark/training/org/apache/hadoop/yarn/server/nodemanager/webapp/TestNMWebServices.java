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
package org.apache.hadoop.yarn.server.nodemanager.webapp;


import MediaType.APPLICATION_JSON;
import MediaType.APPLICATION_XML;
import MediaType.TEXT_PLAIN;
import NodeManager.NMContext;
import YarnConfiguration.LOG_AGGREGATION_ENABLED;
import YarnConfiguration.NM_LOCAL_DIRS;
import YarnConfiguration.NM_LOG_DIRS;
import YarnConfiguration.NM_REMOTE_APP_LOG_DIR;
import YarnConfiguration.YARN_LOG_SERVER_WEBSERVICE_URL;
import YarnWebServiceParams.NM_ID;
import com.google.inject.Guice;
import com.google.inject.servlet.GuiceFilter;
import com.google.inject.servlet.ServletModule;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.guice.spi.container.servlet.GuiceContainer;
import com.sun.jersey.test.framework.WebAppDescriptor;
import java.io.File;
import java.io.StringReader;
import java.net.URI;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import javax.ws.rs.core.MediaType;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.http.JettyUtils;
import org.apache.hadoop.yarn.api.records.ApplicationAttemptId;
import org.apache.hadoop.yarn.api.records.ApplicationId;
import org.apache.hadoop.yarn.api.records.ContainerId;
import org.apache.hadoop.yarn.api.records.NodeId;
import org.apache.hadoop.yarn.exceptions.YarnException;
import org.apache.hadoop.yarn.server.nodemanager.Context;
import org.apache.hadoop.yarn.server.nodemanager.LocalDirsHandlerService;
import org.apache.hadoop.yarn.server.nodemanager.NodeHealthCheckerService;
import org.apache.hadoop.yarn.server.nodemanager.NodeManager;
import org.apache.hadoop.yarn.server.nodemanager.ResourceView;
import org.apache.hadoop.yarn.server.nodemanager.containermanager.resourceplugin.ResourcePlugin;
import org.apache.hadoop.yarn.server.nodemanager.containermanager.resourceplugin.ResourcePluginManager;
import org.apache.hadoop.yarn.server.nodemanager.containermanager.resourceplugin.gpu.GpuDevice;
import org.apache.hadoop.yarn.server.nodemanager.webapp.dao.NMResourceInfo;
import org.apache.hadoop.yarn.server.nodemanager.webapp.dao.gpu.GpuDeviceInformation;
import org.apache.hadoop.yarn.server.nodemanager.webapp.dao.gpu.PerGpuDeviceInformation;
import org.apache.hadoop.yarn.server.security.ApplicationACLsManager;
import org.apache.hadoop.yarn.server.utils.BuilderUtils;
import org.apache.hadoop.yarn.server.webapp.YarnWebServiceParams;
import org.apache.hadoop.yarn.webapp.GenericExceptionHandler;
import org.apache.hadoop.yarn.webapp.GuiceServletConfig;
import org.apache.hadoop.yarn.webapp.JerseyTestBase;
import org.apache.hadoop.yarn.webapp.WebApp;
import org.apache.hadoop.yarn.webapp.WebServicesTestUtils;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;


/**
 * Test the nodemanager node info web services api's
 */
public class TestNMWebServices extends JerseyTestBase {
    private static NMContext nmContext;

    private static ResourceView resourceView;

    private static ApplicationACLsManager aclsManager;

    private static LocalDirsHandlerService dirsHandler;

    private static WebApp nmWebApp;

    private static final String LOGSERVICEWSADDR = "test:1234";

    private static final File testRootDir = new File("target", TestNMWebServices.class.getSimpleName());

    private static File testLogDir = new File("target", ((TestNMWebServices.class.getSimpleName()) + "LogDir"));

    private static File testRemoteLogDir = new File("target", ((TestNMWebServices.class.getSimpleName()) + "remote-log-dir"));

    private static class WebServletModule extends ServletModule {
        @Override
        protected void configureServlets() {
            Configuration conf = new Configuration();
            conf.set(NM_LOCAL_DIRS, TestNMWebServices.testRootDir.getAbsolutePath());
            conf.set(NM_LOG_DIRS, TestNMWebServices.testLogDir.getAbsolutePath());
            conf.setBoolean(LOG_AGGREGATION_ENABLED, true);
            conf.set(NM_REMOTE_APP_LOG_DIR, TestNMWebServices.testRemoteLogDir.getAbsolutePath());
            conf.set(YARN_LOG_SERVER_WEBSERVICE_URL, TestNMWebServices.LOGSERVICEWSADDR);
            TestNMWebServices.dirsHandler = new LocalDirsHandlerService();
            NodeHealthCheckerService healthChecker = new NodeHealthCheckerService(NodeManager.getNodeHealthScriptRunner(conf), TestNMWebServices.dirsHandler);
            healthChecker.init(conf);
            TestNMWebServices.aclsManager = new ApplicationACLsManager(conf);
            TestNMWebServices.nmContext = new NodeManager.NMContext(null, null, TestNMWebServices.dirsHandler, TestNMWebServices.aclsManager, null, false, conf);
            NodeId nodeId = NodeId.newInstance("testhost.foo.com", 8042);
            ((NodeManager.NMContext) (TestNMWebServices.nmContext)).setNodeId(nodeId);
            TestNMWebServices.resourceView = new ResourceView() {
                @Override
                public long getVmemAllocatedForContainers() {
                    // 15.5G in bytes
                    return new Long("16642998272");
                }

                @Override
                public long getPmemAllocatedForContainers() {
                    // 16G in bytes
                    return new Long("17179869184");
                }

                @Override
                public long getVCoresAllocatedForContainers() {
                    return new Long("4000");
                }

                @Override
                public boolean isVmemCheckEnabled() {
                    return true;
                }

                @Override
                public boolean isPmemCheckEnabled() {
                    return true;
                }
            };
            TestNMWebServices.nmWebApp = new org.apache.hadoop.yarn.server.nodemanager.webapp.WebServer.NMWebApp(TestNMWebServices.resourceView, TestNMWebServices.aclsManager, TestNMWebServices.dirsHandler);
            bind(JAXBContextResolver.class);
            bind(NMWebServices.class);
            bind(GenericExceptionHandler.class);
            bind(Context.class).toInstance(TestNMWebServices.nmContext);
            bind(WebApp.class).toInstance(TestNMWebServices.nmWebApp);
            bind(ResourceView.class).toInstance(TestNMWebServices.resourceView);
            bind(ApplicationACLsManager.class).toInstance(TestNMWebServices.aclsManager);
            bind(LocalDirsHandlerService.class).toInstance(TestNMWebServices.dirsHandler);
            serve("/*").with(GuiceContainer.class);
        }
    }

    static {
        GuiceServletConfig.setInjector(Guice.createInjector(new TestNMWebServices.WebServletModule()));
    }

    public TestNMWebServices() {
        super(new WebAppDescriptor.Builder("org.apache.hadoop.yarn.server.nodemanager.webapp").contextListenerClass(GuiceServletConfig.class).filterClass(GuiceFilter.class).contextPath("jersey-guice-filter").servletPath("/").build());
    }

    @Test
    public void testInvalidUri() throws Exception, JSONException {
        WebResource r = resource();
        String responseStr = "";
        try {
            responseStr = r.path("ws").path("v1").path("node").path("bogus").accept(APPLICATION_JSON).get(String.class);
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
            responseStr = r.path("ws").path("v1").path("node").accept(TEXT_PLAIN).get(String.class);
            Assert.fail("should have thrown exception on invalid uri");
        } catch (UniformInterfaceException ue) {
            ClientResponse response = ue.getResponse();
            assertResponseStatusCode(Status.INTERNAL_SERVER_ERROR, response.getStatusInfo());
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
            assertResponseStatusCode(Status.NOT_FOUND, response.getStatusInfo());
            WebServicesTestUtils.checkStringMatch("error string exists and shouldn't", "", responseStr);
        }
    }

    @Test
    public void testNode() throws Exception, JSONException {
        WebResource r = resource();
        ClientResponse response = r.path("ws").path("v1").path("node").accept(APPLICATION_JSON).get(ClientResponse.class);
        Assert.assertEquals((((MediaType.APPLICATION_JSON) + "; ") + (JettyUtils.UTF_8)), response.getType().toString());
        JSONObject json = response.getEntity(JSONObject.class);
        verifyNodeInfo(json);
    }

    @Test
    public void testNodeSlash() throws Exception, JSONException {
        WebResource r = resource();
        ClientResponse response = r.path("ws").path("v1").path("node/").accept(APPLICATION_JSON).get(ClientResponse.class);
        Assert.assertEquals((((MediaType.APPLICATION_JSON) + "; ") + (JettyUtils.UTF_8)), response.getType().toString());
        JSONObject json = response.getEntity(JSONObject.class);
        verifyNodeInfo(json);
    }

    // make sure default is json output
    @Test
    public void testNodeDefault() throws Exception, JSONException {
        WebResource r = resource();
        ClientResponse response = r.path("ws").path("v1").path("node").get(ClientResponse.class);
        Assert.assertEquals((((MediaType.APPLICATION_JSON) + "; ") + (JettyUtils.UTF_8)), response.getType().toString());
        JSONObject json = response.getEntity(JSONObject.class);
        verifyNodeInfo(json);
    }

    @Test
    public void testNodeInfo() throws Exception, JSONException {
        WebResource r = resource();
        ClientResponse response = r.path("ws").path("v1").path("node").path("info").accept(APPLICATION_JSON).get(ClientResponse.class);
        Assert.assertEquals((((MediaType.APPLICATION_JSON) + "; ") + (JettyUtils.UTF_8)), response.getType().toString());
        JSONObject json = response.getEntity(JSONObject.class);
        verifyNodeInfo(json);
    }

    @Test
    public void testNodeInfoSlash() throws Exception, JSONException {
        WebResource r = resource();
        ClientResponse response = r.path("ws").path("v1").path("node").path("info/").accept(APPLICATION_JSON).get(ClientResponse.class);
        Assert.assertEquals((((MediaType.APPLICATION_JSON) + "; ") + (JettyUtils.UTF_8)), response.getType().toString());
        JSONObject json = response.getEntity(JSONObject.class);
        verifyNodeInfo(json);
    }

    // make sure default is json output
    @Test
    public void testNodeInfoDefault() throws Exception, JSONException {
        WebResource r = resource();
        ClientResponse response = r.path("ws").path("v1").path("node").path("info").get(ClientResponse.class);
        Assert.assertEquals((((MediaType.APPLICATION_JSON) + "; ") + (JettyUtils.UTF_8)), response.getType().toString());
        JSONObject json = response.getEntity(JSONObject.class);
        verifyNodeInfo(json);
    }

    @Test
    public void testSingleNodesXML() throws Exception, JSONException {
        WebResource r = resource();
        ClientResponse response = r.path("ws").path("v1").path("node").path("info/").accept(APPLICATION_XML).get(ClientResponse.class);
        Assert.assertEquals((((MediaType.APPLICATION_XML) + "; ") + (JettyUtils.UTF_8)), response.getType().toString());
        String xml = response.getEntity(String.class);
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        InputSource is = new InputSource();
        is.setCharacterStream(new StringReader(xml));
        Document dom = db.parse(is);
        NodeList nodes = dom.getElementsByTagName("nodeInfo");
        Assert.assertEquals("incorrect number of elements", 1, nodes.getLength());
        verifyNodesXML(nodes);
    }

    @Test(timeout = 5000)
    public void testContainerLogsWithNewAPI() throws Exception {
        final ContainerId containerId = BuilderUtils.newContainerId(0, 0, 0, 0);
        WebResource r = resource();
        r = r.path("ws").path("v1").path("node").path("containers").path(containerId.toString()).path("logs");
        testContainerLogs(r, containerId);
    }

    @Test(timeout = 5000)
    public void testContainerLogsWithOldAPI() throws Exception {
        final ContainerId containerId = BuilderUtils.newContainerId(1, 1, 0, 1);
        WebResource r = resource();
        r = r.path("ws").path("v1").path("node").path("containerlogs").path(containerId.toString());
        testContainerLogs(r, containerId);
    }

    @Test(timeout = 10000)
    public void testNMRedirect() {
        ApplicationId noExistAppId = ApplicationId.newInstance(System.currentTimeMillis(), 2000);
        ApplicationAttemptId noExistAttemptId = ApplicationAttemptId.newInstance(noExistAppId, 150);
        ContainerId noExistContainerId = ContainerId.newContainerId(noExistAttemptId, 250);
        String fileName = "syslog";
        WebResource r = resource();
        // check the old api
        URI requestURI = r.path("ws").path("v1").path("node").path("containerlogs").path(noExistContainerId.toString()).path(fileName).queryParam("user.name", "user").queryParam(NM_ID, "localhost:1111").getURI();
        String redirectURL = TestNMWebServices.getRedirectURL(requestURI.toString());
        Assert.assertTrue((redirectURL != null));
        Assert.assertTrue(redirectURL.contains(TestNMWebServices.LOGSERVICEWSADDR));
        Assert.assertTrue(redirectURL.contains(noExistContainerId.toString()));
        Assert.assertTrue(redirectURL.contains(("/logs/" + fileName)));
        Assert.assertTrue(redirectURL.contains(("user.name=" + "user")));
        Assert.assertTrue(redirectURL.contains(((YarnWebServiceParams.REDIRECTED_FROM_NODE) + "=true")));
        Assert.assertFalse(redirectURL.contains(NM_ID));
        // check the new api
        requestURI = r.path("ws").path("v1").path("node").path("containers").path(noExistContainerId.toString()).path("logs").path(fileName).queryParam("user.name", "user").queryParam(NM_ID, "localhost:1111").getURI();
        redirectURL = TestNMWebServices.getRedirectURL(requestURI.toString());
        Assert.assertTrue((redirectURL != null));
        Assert.assertTrue(redirectURL.contains(TestNMWebServices.LOGSERVICEWSADDR));
        Assert.assertTrue(redirectURL.contains(noExistContainerId.toString()));
        Assert.assertTrue(redirectURL.contains(("/logs/" + fileName)));
        Assert.assertTrue(redirectURL.contains(("user.name=" + "user")));
        Assert.assertTrue(redirectURL.contains(((YarnWebServiceParams.REDIRECTED_FROM_NODE) + "=true")));
        Assert.assertFalse(redirectURL.contains(NM_ID));
        requestURI = r.path("ws").path("v1").path("node").path("containers").path(noExistContainerId.toString()).path("logs").queryParam("user.name", "user").queryParam(NM_ID, "localhost:1111").getURI();
        redirectURL = TestNMWebServices.getRedirectURL(requestURI.toString());
        Assert.assertTrue((redirectURL != null));
        Assert.assertTrue(redirectURL.contains(TestNMWebServices.LOGSERVICEWSADDR));
        Assert.assertTrue(redirectURL.contains(noExistContainerId.toString()));
        Assert.assertTrue(redirectURL.contains(("user.name=" + "user")));
        Assert.assertTrue(redirectURL.contains(((YarnWebServiceParams.REDIRECTED_FROM_NODE) + "=true")));
        Assert.assertFalse(redirectURL.contains(NM_ID));
    }

    @Test
    public void testGetNMResourceInfo() throws InterruptedException, YarnException, JSONException {
        ResourcePluginManager rpm = Mockito.mock(ResourcePluginManager.class);
        Map<String, ResourcePlugin> namesToPlugins = new HashMap<>();
        ResourcePlugin mockPlugin1 = Mockito.mock(ResourcePlugin.class);
        NMResourceInfo nmResourceInfo1 = new NMResourceInfo() {
            public long a = 1000L;
        };
        Mockito.when(mockPlugin1.getNMResourceInfo()).thenReturn(nmResourceInfo1);
        namesToPlugins.put("resource-1", mockPlugin1);
        namesToPlugins.put("yarn.io/resource-1", mockPlugin1);
        ResourcePlugin mockPlugin2 = Mockito.mock(ResourcePlugin.class);
        namesToPlugins.put("resource-2", mockPlugin2);
        Mockito.when(rpm.getNameToPlugins()).thenReturn(namesToPlugins);
        TestNMWebServices.nmContext.setResourcePluginManager(rpm);
        WebResource r = resource();
        ClientResponse response = r.path("ws").path("v1").path("node").path("resources").path("resource-2").accept(APPLICATION_JSON).get(ClientResponse.class);
        Assert.assertEquals((((MediaType.APPLICATION_JSON) + "; ") + (JettyUtils.UTF_8)), response.getType().toString());
        // Access resource-2 should fail (empty NMResourceInfo returned).
        JSONObject json = response.getEntity(JSONObject.class);
        Assert.assertEquals(0, json.length());
        // Access resource-3 should fail (unknown plugin)
        response = r.path("ws").path("v1").path("node").path("resources").path("resource-3").accept(APPLICATION_JSON).get(ClientResponse.class);
        Assert.assertEquals((((MediaType.APPLICATION_JSON) + "; ") + (JettyUtils.UTF_8)), response.getType().toString());
        json = response.getEntity(JSONObject.class);
        Assert.assertEquals(0, json.length());
        // Access resource-1 should success
        response = r.path("ws").path("v1").path("node").path("resources").path("resource-1").accept(APPLICATION_JSON).get(ClientResponse.class);
        Assert.assertEquals((((MediaType.APPLICATION_JSON) + "; ") + (JettyUtils.UTF_8)), response.getType().toString());
        json = response.getEntity(JSONObject.class);
        Assert.assertEquals(1000, json.get("a"));
        // Access resource-1 should success (encoded yarn.io/Fresource-1).
        response = r.path("ws").path("v1").path("node").path("resources").path("yarn.io%2Fresource-1").accept(APPLICATION_JSON).get(ClientResponse.class);
        Assert.assertEquals((((MediaType.APPLICATION_JSON) + "; ") + (JettyUtils.UTF_8)), response.getType().toString());
        json = response.getEntity(JSONObject.class);
        Assert.assertEquals(1000, json.get("a"));
    }

    @Test
    public void testGetYarnGpuResourceInfo() throws InterruptedException, YarnException, JSONException {
        ResourcePluginManager rpm = Mockito.mock(ResourcePluginManager.class);
        Map<String, ResourcePlugin> namesToPlugins = new HashMap<>();
        ResourcePlugin mockPlugin1 = Mockito.mock(ResourcePlugin.class);
        GpuDeviceInformation gpuDeviceInformation = new GpuDeviceInformation();
        gpuDeviceInformation.setDriverVersion("1.2.3");
        gpuDeviceInformation.setGpus(Arrays.asList(new PerGpuDeviceInformation()));
        NMResourceInfo nmResourceInfo1 = new org.apache.hadoop.yarn.server.nodemanager.webapp.dao.gpu.NMGpuResourceInfo(gpuDeviceInformation, Arrays.asList(new GpuDevice(1, 1), new GpuDevice(2, 2), new GpuDevice(3, 3)), Arrays.asList(new org.apache.hadoop.yarn.server.nodemanager.containermanager.resourceplugin.gpu.AssignedGpuDevice(2, 2, createContainerId(1)), new org.apache.hadoop.yarn.server.nodemanager.containermanager.resourceplugin.gpu.AssignedGpuDevice(3, 3, createContainerId(2))));
        Mockito.when(mockPlugin1.getNMResourceInfo()).thenReturn(nmResourceInfo1);
        namesToPlugins.put("resource-1", mockPlugin1);
        namesToPlugins.put("yarn.io/resource-1", mockPlugin1);
        ResourcePlugin mockPlugin2 = Mockito.mock(ResourcePlugin.class);
        namesToPlugins.put("resource-2", mockPlugin2);
        Mockito.when(rpm.getNameToPlugins()).thenReturn(namesToPlugins);
        TestNMWebServices.nmContext.setResourcePluginManager(rpm);
        WebResource r = resource();
        ClientResponse response;
        JSONObject json;
        // Access resource-1 should success
        response = r.path("ws").path("v1").path("node").path("resources").path("resource-1").accept(APPLICATION_JSON).get(ClientResponse.class);
        Assert.assertEquals((((MediaType.APPLICATION_JSON) + "; ") + (JettyUtils.UTF_8)), response.getType().toString());
        json = response.getEntity(JSONObject.class);
        Assert.assertEquals("1.2.3", json.getJSONObject("gpuDeviceInformation").get("driverVersion"));
        Assert.assertEquals(3, json.getJSONArray("totalGpuDevices").length());
        Assert.assertEquals(2, json.getJSONArray("assignedGpuDevices").length());
        Assert.assertEquals(2, json.getJSONArray("assignedGpuDevices").length());
    }
}

