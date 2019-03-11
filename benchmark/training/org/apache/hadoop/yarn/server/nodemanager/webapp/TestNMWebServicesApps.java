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


import ApplicationState.INITING;
import ApplicationState.RUNNING;
import MediaType.APPLICATION_JSON;
import MediaType.APPLICATION_XML;
import YarnConfiguration.FILTER_ENTITY_LIST_BY_USER;
import YarnConfiguration.NM_LOCAL_DIRS;
import YarnConfiguration.NM_LOG_DIRS;
import YarnConfiguration.YARN_ACL_ENABLE;
import YarnConfiguration.YARN_ADMIN_ACL;
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
import java.util.HashMap;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.MediaType;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.http.JettyUtils;
import org.apache.hadoop.yarn.api.records.NodeId;
import org.apache.hadoop.yarn.server.nodemanager.Context;
import org.apache.hadoop.yarn.server.nodemanager.LocalDirsHandlerService;
import org.apache.hadoop.yarn.server.nodemanager.NodeHealthCheckerService;
import org.apache.hadoop.yarn.server.nodemanager.NodeManager;
import org.apache.hadoop.yarn.server.nodemanager.NodeManager.NMContext;
import org.apache.hadoop.yarn.server.nodemanager.ResourceView;
import org.apache.hadoop.yarn.server.nodemanager.containermanager.application.Application;
import org.apache.hadoop.yarn.server.nodemanager.webapp.dao.AppsInfo;
import org.apache.hadoop.yarn.server.security.ApplicationACLsManager;
import org.apache.hadoop.yarn.webapp.GenericExceptionHandler;
import org.apache.hadoop.yarn.webapp.GuiceServletConfig;
import org.apache.hadoop.yarn.webapp.JerseyTestBase;
import org.apache.hadoop.yarn.webapp.WebApp;
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


public class TestNMWebServicesApps extends JerseyTestBase {
    private static Context nmContext;

    private static ResourceView resourceView;

    private static ApplicationACLsManager aclsManager;

    private static LocalDirsHandlerService dirsHandler;

    private static WebApp nmWebApp;

    private static Configuration conf = new Configuration();

    private static final File testRootDir = new File("target", TestNMWebServicesApps.class.getSimpleName());

    private static File testLogDir = new File("target", ((TestNMWebServicesApps.class.getSimpleName()) + "LogDir"));

    private static class WebServletModule extends ServletModule {
        @Override
        protected void configureServlets() {
            TestNMWebServicesApps.conf.set(NM_LOCAL_DIRS, TestNMWebServicesApps.testRootDir.getAbsolutePath());
            TestNMWebServicesApps.conf.set(NM_LOG_DIRS, TestNMWebServicesApps.testLogDir.getAbsolutePath());
            LocalDirsHandlerService dirsHandler = new LocalDirsHandlerService();
            NodeHealthCheckerService healthChecker = new NodeHealthCheckerService(NodeManager.getNodeHealthScriptRunner(TestNMWebServicesApps.conf), dirsHandler);
            healthChecker.init(TestNMWebServicesApps.conf);
            dirsHandler = healthChecker.getDiskHandler();
            TestNMWebServicesApps.aclsManager = new ApplicationACLsManager(TestNMWebServicesApps.conf);
            TestNMWebServicesApps.nmContext = new NodeManager.NMContext(null, null, dirsHandler, TestNMWebServicesApps.aclsManager, null, false, TestNMWebServicesApps.conf);
            NodeId nodeId = NodeId.newInstance("testhost.foo.com", 9999);
            ((NodeManager.NMContext) (TestNMWebServicesApps.nmContext)).setNodeId(nodeId);
            TestNMWebServicesApps.resourceView = new ResourceView() {
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
            TestNMWebServicesApps.nmWebApp = new org.apache.hadoop.yarn.server.nodemanager.webapp.WebServer.NMWebApp(TestNMWebServicesApps.resourceView, TestNMWebServicesApps.aclsManager, dirsHandler);
            bind(JAXBContextResolver.class);
            bind(NMWebServices.class);
            bind(GenericExceptionHandler.class);
            bind(Context.class).toInstance(TestNMWebServicesApps.nmContext);
            bind(WebApp.class).toInstance(TestNMWebServicesApps.nmWebApp);
            bind(ResourceView.class).toInstance(TestNMWebServicesApps.resourceView);
            bind(ApplicationACLsManager.class).toInstance(TestNMWebServicesApps.aclsManager);
            bind(LocalDirsHandlerService.class).toInstance(dirsHandler);
            serve("/*").with(GuiceContainer.class);
        }
    }

    static {
        GuiceServletConfig.setInjector(Guice.createInjector(new TestNMWebServicesApps.WebServletModule()));
    }

    public TestNMWebServicesApps() {
        super(new WebAppDescriptor.Builder("org.apache.hadoop.yarn.server.nodemanager.webapp").contextListenerClass(GuiceServletConfig.class).filterClass(GuiceFilter.class).contextPath("jersey-guice-filter").servletPath("/").build());
    }

    @Test
    public void testNodeAppsNone() throws Exception, JSONException {
        WebResource r = resource();
        ClientResponse response = r.path("ws").path("v1").path("node").path("apps").accept(APPLICATION_JSON).get(ClientResponse.class);
        Assert.assertEquals((((MediaType.APPLICATION_JSON_TYPE) + "; ") + (JettyUtils.UTF_8)), response.getType().toString());
        JSONObject json = response.getEntity(JSONObject.class);
        Assert.assertEquals("apps isn't empty", new JSONObject().toString(), json.get("apps").toString());
    }

    @Test
    public void testNodeApps() throws Exception, JSONException {
        testNodeHelper("apps", APPLICATION_JSON);
    }

    @Test
    public void testNodeAppsSlash() throws Exception, JSONException {
        testNodeHelper("apps/", APPLICATION_JSON);
    }

    // make sure default is json output
    @Test
    public void testNodeAppsDefault() throws Exception, JSONException {
        testNodeHelper("apps/", "");
    }

    @Test
    public void testNodeAppsUser() throws Exception, JSONException {
        WebResource r = resource();
        Application app = new MockApp(1);
        TestNMWebServicesApps.nmContext.getApplications().put(app.getAppId(), app);
        HashMap<String, String> hash = addAppContainers(app);
        Application app2 = new MockApp("foo", 1234, 2);
        TestNMWebServicesApps.nmContext.getApplications().put(app2.getAppId(), app2);
        addAppContainers(app2);
        ClientResponse response = r.path("ws").path("v1").path("node").path("apps").queryParam("user", "mockUser").accept(APPLICATION_JSON).get(ClientResponse.class);
        Assert.assertEquals((((MediaType.APPLICATION_JSON_TYPE) + "; ") + (JettyUtils.UTF_8)), response.getType().toString());
        JSONObject json = response.getEntity(JSONObject.class);
        JSONObject info = json.getJSONObject("apps");
        Assert.assertEquals("incorrect number of elements", 1, info.length());
        JSONArray appInfo = info.getJSONArray("app");
        Assert.assertEquals("incorrect number of elements", 1, appInfo.length());
        verifyNodeAppInfo(appInfo.getJSONObject(0), app, hash);
    }

    @Test
    public void testNodeAppsUserNone() throws Exception, JSONException {
        WebResource r = resource();
        Application app = new MockApp(1);
        TestNMWebServicesApps.nmContext.getApplications().put(app.getAppId(), app);
        addAppContainers(app);
        Application app2 = new MockApp("foo", 1234, 2);
        TestNMWebServicesApps.nmContext.getApplications().put(app2.getAppId(), app2);
        addAppContainers(app2);
        ClientResponse response = r.path("ws").path("v1").path("node").path("apps").queryParam("user", "george").accept(APPLICATION_JSON).get(ClientResponse.class);
        Assert.assertEquals((((MediaType.APPLICATION_JSON_TYPE) + "; ") + (JettyUtils.UTF_8)), response.getType().toString());
        JSONObject json = response.getEntity(JSONObject.class);
        Assert.assertEquals("apps is not empty", new JSONObject().toString(), json.get("apps").toString());
    }

    @Test
    public void testNodeAppsUserEmpty() throws Exception, JSONException {
        WebResource r = resource();
        Application app = new MockApp(1);
        TestNMWebServicesApps.nmContext.getApplications().put(app.getAppId(), app);
        addAppContainers(app);
        Application app2 = new MockApp("foo", 1234, 2);
        TestNMWebServicesApps.nmContext.getApplications().put(app2.getAppId(), app2);
        addAppContainers(app2);
        try {
            r.path("ws").path("v1").path("node").path("apps").queryParam("user", "").accept(APPLICATION_JSON).get(JSONObject.class);
            Assert.fail("should have thrown exception on invalid user query");
        } catch (UniformInterfaceException ue) {
            ClientResponse response = ue.getResponse();
            assertResponseStatusCode(Status.BAD_REQUEST, response.getStatusInfo());
            Assert.assertEquals((((MediaType.APPLICATION_JSON_TYPE) + "; ") + (JettyUtils.UTF_8)), response.getType().toString());
            JSONObject msg = response.getEntity(JSONObject.class);
            JSONObject exception = msg.getJSONObject("RemoteException");
            Assert.assertEquals("incorrect number of elements", 3, exception.length());
            String message = exception.getString("message");
            String type = exception.getString("exception");
            String classname = exception.getString("javaClassName");
            WebServicesTestUtils.checkStringMatch("exception message", "java.lang.Exception: Error: You must specify a non-empty string for the user", message);
            WebServicesTestUtils.checkStringMatch("exception type", "BadRequestException", type);
            WebServicesTestUtils.checkStringMatch("exception classname", "org.apache.hadoop.yarn.webapp.BadRequestException", classname);
        }
    }

    @Test
    public void testNodeAppsState() throws Exception, JSONException {
        WebResource r = resource();
        Application app = new MockApp(1);
        TestNMWebServicesApps.nmContext.getApplications().put(app.getAppId(), app);
        addAppContainers(app);
        MockApp app2 = new MockApp("foo", 1234, 2);
        TestNMWebServicesApps.nmContext.getApplications().put(app2.getAppId(), app2);
        HashMap<String, String> hash2 = addAppContainers(app2);
        app2.setState(RUNNING);
        ClientResponse response = r.path("ws").path("v1").path("node").path("apps").queryParam("state", RUNNING.toString()).accept(APPLICATION_JSON).get(ClientResponse.class);
        Assert.assertEquals((((MediaType.APPLICATION_JSON_TYPE) + "; ") + (JettyUtils.UTF_8)), response.getType().toString());
        JSONObject json = response.getEntity(JSONObject.class);
        JSONObject info = json.getJSONObject("apps");
        Assert.assertEquals("incorrect number of elements", 1, info.length());
        JSONArray appInfo = info.getJSONArray("app");
        Assert.assertEquals("incorrect number of elements", 1, appInfo.length());
        verifyNodeAppInfo(appInfo.getJSONObject(0), app2, hash2);
    }

    @Test
    public void testNodeAppsStateNone() throws Exception, JSONException {
        WebResource r = resource();
        Application app = new MockApp(1);
        TestNMWebServicesApps.nmContext.getApplications().put(app.getAppId(), app);
        addAppContainers(app);
        Application app2 = new MockApp("foo", 1234, 2);
        TestNMWebServicesApps.nmContext.getApplications().put(app2.getAppId(), app2);
        addAppContainers(app2);
        ClientResponse response = r.path("ws").path("v1").path("node").path("apps").queryParam("state", INITING.toString()).accept(APPLICATION_JSON).get(ClientResponse.class);
        Assert.assertEquals((((MediaType.APPLICATION_JSON_TYPE) + "; ") + (JettyUtils.UTF_8)), response.getType().toString());
        JSONObject json = response.getEntity(JSONObject.class);
        Assert.assertEquals("apps is not empty", new JSONObject().toString(), json.get("apps").toString());
    }

    @Test
    public void testNodeAppsStateInvalid() throws Exception, JSONException {
        WebResource r = resource();
        Application app = new MockApp(1);
        TestNMWebServicesApps.nmContext.getApplications().put(app.getAppId(), app);
        addAppContainers(app);
        Application app2 = new MockApp("foo", 1234, 2);
        TestNMWebServicesApps.nmContext.getApplications().put(app2.getAppId(), app2);
        addAppContainers(app2);
        try {
            r.path("ws").path("v1").path("node").path("apps").queryParam("state", "FOO_STATE").accept(APPLICATION_JSON).get(JSONObject.class);
            Assert.fail("should have thrown exception on invalid user query");
        } catch (UniformInterfaceException ue) {
            ClientResponse response = ue.getResponse();
            assertResponseStatusCode(Status.BAD_REQUEST, response.getStatusInfo());
            Assert.assertEquals((((MediaType.APPLICATION_JSON_TYPE) + "; ") + (JettyUtils.UTF_8)), response.getType().toString());
            JSONObject msg = response.getEntity(JSONObject.class);
            JSONObject exception = msg.getJSONObject("RemoteException");
            Assert.assertEquals("incorrect number of elements", 3, exception.length());
            String message = exception.getString("message");
            String type = exception.getString("exception");
            String classname = exception.getString("javaClassName");
            verifyStateInvalidException(message, type, classname);
        }
    }

    // verify the exception object default format is JSON
    @Test
    public void testNodeAppsStateInvalidDefault() throws Exception, JSONException {
        WebResource r = resource();
        Application app = new MockApp(1);
        TestNMWebServicesApps.nmContext.getApplications().put(app.getAppId(), app);
        addAppContainers(app);
        Application app2 = new MockApp("foo", 1234, 2);
        TestNMWebServicesApps.nmContext.getApplications().put(app2.getAppId(), app2);
        addAppContainers(app2);
        try {
            r.path("ws").path("v1").path("node").path("apps").queryParam("state", "FOO_STATE").get(JSONObject.class);
            Assert.fail("should have thrown exception on invalid user query");
        } catch (UniformInterfaceException ue) {
            ClientResponse response = ue.getResponse();
            assertResponseStatusCode(Status.BAD_REQUEST, response.getStatusInfo());
            Assert.assertEquals((((MediaType.APPLICATION_JSON_TYPE) + "; ") + (JettyUtils.UTF_8)), response.getType().toString());
            JSONObject msg = response.getEntity(JSONObject.class);
            JSONObject exception = msg.getJSONObject("RemoteException");
            Assert.assertEquals("incorrect number of elements", 3, exception.length());
            String message = exception.getString("message");
            String type = exception.getString("exception");
            String classname = exception.getString("javaClassName");
            verifyStateInvalidException(message, type, classname);
        }
    }

    // test that the exception output also returns XML
    @Test
    public void testNodeAppsStateInvalidXML() throws Exception, JSONException {
        WebResource r = resource();
        Application app = new MockApp(1);
        TestNMWebServicesApps.nmContext.getApplications().put(app.getAppId(), app);
        addAppContainers(app);
        Application app2 = new MockApp("foo", 1234, 2);
        TestNMWebServicesApps.nmContext.getApplications().put(app2.getAppId(), app2);
        addAppContainers(app2);
        try {
            r.path("ws").path("v1").path("node").path("apps").queryParam("state", "FOO_STATE").accept(APPLICATION_XML).get(JSONObject.class);
            Assert.fail("should have thrown exception on invalid user query");
        } catch (UniformInterfaceException ue) {
            ClientResponse response = ue.getResponse();
            assertResponseStatusCode(Status.BAD_REQUEST, response.getStatusInfo());
            Assert.assertEquals((((MediaType.APPLICATION_XML_TYPE) + "; ") + (JettyUtils.UTF_8)), response.getType().toString());
            String msg = response.getEntity(String.class);
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
            verifyStateInvalidException(message, type, classname);
        }
    }

    @Test
    public void testNodeSingleApps() throws Exception, JSONException {
        testNodeSingleAppHelper(APPLICATION_JSON);
    }

    // make sure default is json output
    @Test
    public void testNodeSingleAppsDefault() throws Exception, JSONException {
        testNodeSingleAppHelper("");
    }

    @Test
    public void testNodeSingleAppsSlash() throws Exception, JSONException {
        WebResource r = resource();
        Application app = new MockApp(1);
        TestNMWebServicesApps.nmContext.getApplications().put(app.getAppId(), app);
        HashMap<String, String> hash = addAppContainers(app);
        Application app2 = new MockApp(2);
        TestNMWebServicesApps.nmContext.getApplications().put(app2.getAppId(), app2);
        addAppContainers(app2);
        ClientResponse response = r.path("ws").path("v1").path("node").path("apps").path(((app.getAppId().toString()) + "/")).accept(APPLICATION_JSON).get(ClientResponse.class);
        Assert.assertEquals((((MediaType.APPLICATION_JSON_TYPE) + "; ") + (JettyUtils.UTF_8)), response.getType().toString());
        JSONObject json = response.getEntity(JSONObject.class);
        verifyNodeAppInfo(json.getJSONObject("app"), app, hash);
    }

    @Test
    public void testNodeSingleAppsInvalid() throws Exception, JSONException {
        WebResource r = resource();
        Application app = new MockApp(1);
        TestNMWebServicesApps.nmContext.getApplications().put(app.getAppId(), app);
        addAppContainers(app);
        Application app2 = new MockApp(2);
        TestNMWebServicesApps.nmContext.getApplications().put(app2.getAppId(), app2);
        addAppContainers(app2);
        try {
            r.path("ws").path("v1").path("node").path("apps").path("app_foo_0000").accept(APPLICATION_JSON).get(JSONObject.class);
            Assert.fail("should have thrown exception on invalid user query");
        } catch (UniformInterfaceException ue) {
            ClientResponse response = ue.getResponse();
            assertResponseStatusCode(Status.BAD_REQUEST, response.getStatusInfo());
            Assert.assertEquals((((MediaType.APPLICATION_JSON_TYPE) + "; ") + (JettyUtils.UTF_8)), response.getType().toString());
            JSONObject msg = response.getEntity(JSONObject.class);
            JSONObject exception = msg.getJSONObject("RemoteException");
            Assert.assertEquals("incorrect number of elements", 3, exception.length());
            String message = exception.getString("message");
            String type = exception.getString("exception");
            String classname = exception.getString("javaClassName");
            WebServicesTestUtils.checkStringMatch("exception message", ("java.lang.IllegalArgumentException: Invalid ApplicationId prefix: " + ("app_foo_0000. The valid ApplicationId should start with prefix" + " application")), message);
            WebServicesTestUtils.checkStringMatch("exception type", "BadRequestException", type);
            WebServicesTestUtils.checkStringMatch("exception classname", "org.apache.hadoop.yarn.webapp.BadRequestException", classname);
        }
    }

    @Test
    public void testNodeSingleAppsMissing() throws Exception, JSONException {
        WebResource r = resource();
        Application app = new MockApp(1);
        TestNMWebServicesApps.nmContext.getApplications().put(app.getAppId(), app);
        addAppContainers(app);
        Application app2 = new MockApp(2);
        TestNMWebServicesApps.nmContext.getApplications().put(app2.getAppId(), app2);
        addAppContainers(app2);
        try {
            r.path("ws").path("v1").path("node").path("apps").path("application_1234_0009").accept(APPLICATION_JSON).get(JSONObject.class);
            Assert.fail("should have thrown exception on invalid user query");
        } catch (UniformInterfaceException ue) {
            ClientResponse response = ue.getResponse();
            assertResponseStatusCode(Status.NOT_FOUND, response.getStatusInfo());
            Assert.assertEquals((((MediaType.APPLICATION_JSON_TYPE) + "; ") + (JettyUtils.UTF_8)), response.getType().toString());
            JSONObject msg = response.getEntity(JSONObject.class);
            JSONObject exception = msg.getJSONObject("RemoteException");
            Assert.assertEquals("incorrect number of elements", 3, exception.length());
            String message = exception.getString("message");
            String type = exception.getString("exception");
            String classname = exception.getString("javaClassName");
            WebServicesTestUtils.checkStringMatch("exception message", "java.lang.Exception: app with id application_1234_0009 not found", message);
            WebServicesTestUtils.checkStringMatch("exception type", "NotFoundException", type);
            WebServicesTestUtils.checkStringMatch("exception classname", "org.apache.hadoop.yarn.webapp.NotFoundException", classname);
        }
    }

    @Test
    public void testNodeAppsXML() throws Exception, JSONException {
        WebResource r = resource();
        Application app = new MockApp(1);
        TestNMWebServicesApps.nmContext.getApplications().put(app.getAppId(), app);
        addAppContainers(app);
        Application app2 = new MockApp(2);
        TestNMWebServicesApps.nmContext.getApplications().put(app2.getAppId(), app2);
        addAppContainers(app2);
        ClientResponse response = r.path("ws").path("v1").path("node").path("apps").accept(APPLICATION_XML).get(ClientResponse.class);
        Assert.assertEquals((((MediaType.APPLICATION_XML_TYPE) + "; ") + (JettyUtils.UTF_8)), response.getType().toString());
        String xml = response.getEntity(String.class);
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        InputSource is = new InputSource();
        is.setCharacterStream(new StringReader(xml));
        Document dom = db.parse(is);
        NodeList nodes = dom.getElementsByTagName("app");
        Assert.assertEquals("incorrect number of elements", 2, nodes.getLength());
    }

    @Test
    public void testNodeSingleAppsXML() throws Exception, JSONException {
        WebResource r = resource();
        Application app = new MockApp(1);
        TestNMWebServicesApps.nmContext.getApplications().put(app.getAppId(), app);
        HashMap<String, String> hash = addAppContainers(app);
        Application app2 = new MockApp(2);
        TestNMWebServicesApps.nmContext.getApplications().put(app2.getAppId(), app2);
        addAppContainers(app2);
        ClientResponse response = r.path("ws").path("v1").path("node").path("apps").path(((app.getAppId().toString()) + "/")).accept(APPLICATION_XML).get(ClientResponse.class);
        Assert.assertEquals((((MediaType.APPLICATION_XML_TYPE) + "; ") + (JettyUtils.UTF_8)), response.getType().toString());
        String xml = response.getEntity(String.class);
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        InputSource is = new InputSource();
        is.setCharacterStream(new StringReader(xml));
        Document dom = db.parse(is);
        NodeList nodes = dom.getElementsByTagName("app");
        Assert.assertEquals("incorrect number of elements", 1, nodes.getLength());
        verifyNodeAppInfoXML(nodes, app, hash);
    }

    @Test
    public void testNodeAppsUserFiltering() throws Exception, JSONException {
        Configuration yarnConf = new Configuration();
        yarnConf.setBoolean(FILTER_ENTITY_LIST_BY_USER, true);
        yarnConf.setBoolean(YARN_ACL_ENABLE, true);
        yarnConf.setStrings(YARN_ADMIN_ACL, "admin");
        ApplicationACLsManager aclManager = new ApplicationACLsManager(yarnConf);
        NMContext context = new NodeManager.NMContext(null, null, TestNMWebServicesApps.dirsHandler, aclManager, null, false, yarnConf);
        Application app = new MockApp(1);
        context.getApplications().put(app.getAppId(), app);
        addAppContainers(app, context);
        Application app2 = new MockApp("foo", 1234, 2);
        context.getApplications().put(app2.getAppId(), app2);
        addAppContainers(app2, context);
        // User "foo" could only see its own apps/containers.
        NMWebServices webSvc = new NMWebServices(context, null, TestNMWebServicesApps.nmWebApp, Mockito.mock(HttpServletResponse.class));
        HttpServletRequest mockHsr = mockHttpServletRequestByUserName("foo");
        AppsInfo appsInfo = webSvc.getNodeApps(mockHsr, null, null);
        Assert.assertEquals(1, appsInfo.getApps().size());
        // Admin could see all apps and containers.
        HttpServletRequest mockHsrAdmin = mockHttpServletRequestByUserName("admin");
        AppsInfo appsInfo2 = webSvc.getNodeApps(mockHsrAdmin, null, null);
        Assert.assertEquals(2, appsInfo2.getApps().size());
    }
}

