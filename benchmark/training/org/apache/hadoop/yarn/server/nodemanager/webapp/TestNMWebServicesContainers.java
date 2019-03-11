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
import YarnConfiguration.NM_LOCAL_DIRS;
import YarnConfiguration.NM_LOG_DIRS;
import com.google.inject.Guice;
import com.google.inject.servlet.GuiceFilter;
import com.google.inject.servlet.ServletModule;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.filter.LoggingFilter;
import com.sun.jersey.guice.spi.container.servlet.GuiceContainer;
import com.sun.jersey.test.framework.WebAppDescriptor;
import java.io.File;
import java.io.StringReader;
import java.util.HashMap;
import javax.ws.rs.core.MediaType;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.http.JettyUtils;
import org.apache.hadoop.yarn.api.records.ContainerId;
import org.apache.hadoop.yarn.api.records.NodeId;
import org.apache.hadoop.yarn.server.nodemanager.Context;
import org.apache.hadoop.yarn.server.nodemanager.LocalDirsHandlerService;
import org.apache.hadoop.yarn.server.nodemanager.NodeHealthCheckerService;
import org.apache.hadoop.yarn.server.nodemanager.NodeManager;
import org.apache.hadoop.yarn.server.nodemanager.ResourceView;
import org.apache.hadoop.yarn.server.nodemanager.containermanager.application.Application;
import org.apache.hadoop.yarn.server.security.ApplicationACLsManager;
import org.apache.hadoop.yarn.webapp.GenericExceptionHandler;
import org.apache.hadoop.yarn.webapp.GuiceServletConfig;
import org.apache.hadoop.yarn.webapp.JerseyTestBase;
import org.apache.hadoop.yarn.webapp.WebApp;
import org.apache.hadoop.yarn.webapp.WebServicesTestUtils;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;


public class TestNMWebServicesContainers extends JerseyTestBase {
    private static Context nmContext;

    private static ResourceView resourceView;

    private static ApplicationACLsManager aclsManager;

    private static LocalDirsHandlerService dirsHandler;

    private static WebApp nmWebApp;

    private static Configuration conf = new Configuration();

    private static final File testRootDir = new File("target", TestNMWebServicesContainers.class.getSimpleName());

    private static File testLogDir = new File("target", ((TestNMWebServicesContainers.class.getSimpleName()) + "LogDir"));

    private static class WebServletModule extends ServletModule {
        @Override
        protected void configureServlets() {
            TestNMWebServicesContainers.resourceView = new ResourceView() {
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
            TestNMWebServicesContainers.conf.set(NM_LOCAL_DIRS, TestNMWebServicesContainers.testRootDir.getAbsolutePath());
            TestNMWebServicesContainers.conf.set(NM_LOG_DIRS, TestNMWebServicesContainers.testLogDir.getAbsolutePath());
            LocalDirsHandlerService dirsHandler = new LocalDirsHandlerService();
            NodeHealthCheckerService healthChecker = new NodeHealthCheckerService(NodeManager.getNodeHealthScriptRunner(TestNMWebServicesContainers.conf), dirsHandler);
            healthChecker.init(TestNMWebServicesContainers.conf);
            dirsHandler = healthChecker.getDiskHandler();
            TestNMWebServicesContainers.aclsManager = new ApplicationACLsManager(TestNMWebServicesContainers.conf);
            TestNMWebServicesContainers.nmContext = new NodeManager.NMContext(null, null, dirsHandler, TestNMWebServicesContainers.aclsManager, null, false, TestNMWebServicesContainers.conf) {
                public NodeId getNodeId() {
                    return NodeId.newInstance("testhost.foo.com", 8042);
                }

                public int getHttpPort() {
                    return 1234;
                }
            };
            TestNMWebServicesContainers.nmWebApp = new org.apache.hadoop.yarn.server.nodemanager.webapp.WebServer.NMWebApp(TestNMWebServicesContainers.resourceView, TestNMWebServicesContainers.aclsManager, dirsHandler);
            bind(JAXBContextResolver.class);
            bind(NMWebServices.class);
            bind(GenericExceptionHandler.class);
            bind(Context.class).toInstance(TestNMWebServicesContainers.nmContext);
            bind(WebApp.class).toInstance(TestNMWebServicesContainers.nmWebApp);
            bind(ResourceView.class).toInstance(TestNMWebServicesContainers.resourceView);
            bind(ApplicationACLsManager.class).toInstance(TestNMWebServicesContainers.aclsManager);
            bind(LocalDirsHandlerService.class).toInstance(dirsHandler);
            serve("/*").with(GuiceContainer.class);
        }
    }

    static {
        GuiceServletConfig.setInjector(Guice.createInjector(new TestNMWebServicesContainers.WebServletModule()));
    }

    public TestNMWebServicesContainers() {
        super(new WebAppDescriptor.Builder("org.apache.hadoop.yarn.server.nodemanager.webapp").contextListenerClass(GuiceServletConfig.class).filterClass(GuiceFilter.class).contextPath("jersey-guice-filter").servletPath("/").build());
    }

    @Test
    public void testNodeContainersNone() throws Exception, JSONException {
        WebResource r = resource();
        ClientResponse response = r.path("ws").path("v1").path("node").path("containers").accept(APPLICATION_JSON).get(ClientResponse.class);
        Assert.assertEquals((((MediaType.APPLICATION_JSON_TYPE) + "; ") + (JettyUtils.UTF_8)), response.getType().toString());
        JSONObject json = response.getEntity(JSONObject.class);
        Assert.assertEquals("apps isn't empty", new JSONObject().toString(), json.get("containers").toString());
    }

    @Test
    public void testNodeContainers() throws Exception, JSONException {
        testNodeHelper("containers", APPLICATION_JSON);
    }

    @Test
    public void testNodeContainersSlash() throws Exception, JSONException {
        testNodeHelper("containers/", APPLICATION_JSON);
    }

    // make sure default is json output
    @Test
    public void testNodeContainersDefault() throws Exception, JSONException {
        testNodeHelper("containers/", "");
    }

    @Test
    public void testNodeSingleContainers() throws Exception, JSONException {
        testNodeSingleContainersHelper(APPLICATION_JSON);
    }

    @Test
    public void testNodeSingleContainersSlash() throws Exception, JSONException {
        testNodeSingleContainersHelper(APPLICATION_JSON);
    }

    @Test
    public void testNodeSingleContainersDefault() throws Exception, JSONException {
        testNodeSingleContainersHelper("");
    }

    @Test
    public void testSingleContainerInvalid() throws Exception, JSONException {
        WebResource r = resource();
        Application app = new MockApp(1);
        TestNMWebServicesContainers.nmContext.getApplications().put(app.getAppId(), app);
        addAppContainers(app);
        Application app2 = new MockApp(2);
        TestNMWebServicesContainers.nmContext.getApplications().put(app2.getAppId(), app2);
        addAppContainers(app2);
        try {
            r.path("ws").path("v1").path("node").path("containers").path("container_foo_1234").accept(APPLICATION_JSON).get(JSONObject.class);
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
            WebServicesTestUtils.checkStringMatch("exception message", "java.lang.Exception: invalid container id, container_foo_1234", message);
            WebServicesTestUtils.checkStringMatch("exception type", "BadRequestException", type);
            WebServicesTestUtils.checkStringMatch("exception classname", "org.apache.hadoop.yarn.webapp.BadRequestException", classname);
        }
    }

    @Test
    public void testSingleContainerInvalid2() throws Exception, JSONException {
        WebResource r = resource();
        Application app = new MockApp(1);
        TestNMWebServicesContainers.nmContext.getApplications().put(app.getAppId(), app);
        addAppContainers(app);
        Application app2 = new MockApp(2);
        TestNMWebServicesContainers.nmContext.getApplications().put(app2.getAppId(), app2);
        addAppContainers(app2);
        try {
            r.path("ws").path("v1").path("node").path("containers").path("container_1234_0001").accept(APPLICATION_JSON).get(JSONObject.class);
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
            WebServicesTestUtils.checkStringMatch("exception message", "java.lang.Exception: invalid container id, container_1234_0001", message);
            WebServicesTestUtils.checkStringMatch("exception type", "BadRequestException", type);
            WebServicesTestUtils.checkStringMatch("exception classname", "org.apache.hadoop.yarn.webapp.BadRequestException", classname);
        }
    }

    @Test
    public void testSingleContainerWrong() throws Exception, JSONException {
        WebResource r = resource();
        Application app = new MockApp(1);
        TestNMWebServicesContainers.nmContext.getApplications().put(app.getAppId(), app);
        addAppContainers(app);
        Application app2 = new MockApp(2);
        TestNMWebServicesContainers.nmContext.getApplications().put(app2.getAppId(), app2);
        addAppContainers(app2);
        try {
            r.path("ws").path("v1").path("node").path("containers").path("container_1234_0001_01_000005").accept(APPLICATION_JSON).get(JSONObject.class);
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
            WebServicesTestUtils.checkStringMatch("exception message", "java.lang.Exception: container with id, container_1234_0001_01_000005, not found", message);
            WebServicesTestUtils.checkStringMatch("exception type", "NotFoundException", type);
            WebServicesTestUtils.checkStringMatch("exception classname", "org.apache.hadoop.yarn.webapp.NotFoundException", classname);
        }
    }

    @Test
    public void testNodeSingleContainerXML() throws Exception, JSONException {
        WebResource r = resource();
        Application app = new MockApp(1);
        TestNMWebServicesContainers.nmContext.getApplications().put(app.getAppId(), app);
        HashMap<String, String> hash = addAppContainers(app);
        Application app2 = new MockApp(2);
        TestNMWebServicesContainers.nmContext.getApplications().put(app2.getAppId(), app2);
        addAppContainers(app2);
        client().addFilter(new LoggingFilter(System.out));
        for (String id : hash.keySet()) {
            ClientResponse response = r.path("ws").path("v1").path("node").path("containers").path(id).accept(APPLICATION_XML).get(ClientResponse.class);
            Assert.assertEquals((((MediaType.APPLICATION_XML_TYPE) + "; ") + (JettyUtils.UTF_8)), response.getType().toString());
            String xml = response.getEntity(String.class);
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            InputSource is = new InputSource();
            is.setCharacterStream(new StringReader(xml));
            Document dom = db.parse(is);
            NodeList nodes = dom.getElementsByTagName("container");
            Assert.assertEquals("incorrect number of elements", 1, nodes.getLength());
            verifyContainersInfoXML(nodes, TestNMWebServicesContainers.nmContext.getContainers().get(ContainerId.fromString(id)));
        }
    }

    @Test
    public void testNodeContainerXML() throws Exception, JSONException {
        WebResource r = resource();
        Application app = new MockApp(1);
        TestNMWebServicesContainers.nmContext.getApplications().put(app.getAppId(), app);
        addAppContainers(app);
        Application app2 = new MockApp(2);
        TestNMWebServicesContainers.nmContext.getApplications().put(app2.getAppId(), app2);
        addAppContainers(app2);
        ClientResponse response = r.path("ws").path("v1").path("node").path("containers").accept(APPLICATION_XML).get(ClientResponse.class);
        Assert.assertEquals((((MediaType.APPLICATION_XML_TYPE) + "; ") + (JettyUtils.UTF_8)), response.getType().toString());
        String xml = response.getEntity(String.class);
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        InputSource is = new InputSource();
        is.setCharacterStream(new StringReader(xml));
        Document dom = db.parse(is);
        NodeList nodes = dom.getElementsByTagName("container");
        Assert.assertEquals("incorrect number of elements", 4, nodes.getLength());
    }
}

