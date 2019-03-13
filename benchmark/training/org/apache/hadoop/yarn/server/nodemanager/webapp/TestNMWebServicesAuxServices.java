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
import com.sun.jersey.guice.spi.container.servlet.GuiceContainer;
import com.sun.jersey.test.framework.WebAppDescriptor;
import java.io.File;
import java.io.StringReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
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
import org.apache.hadoop.yarn.server.nodemanager.ResourceView;
import org.apache.hadoop.yarn.server.nodemanager.containermanager.AuxServices;
import org.apache.hadoop.yarn.server.nodemanager.containermanager.records.AuxServiceRecord;
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
import org.mockito.Mockito;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;


/**
 * Basic sanity Tests for AuxServices.
 */
public class TestNMWebServicesAuxServices extends JerseyTestBase {
    private static final String AUX_SERVICES_PATH = "auxiliaryservices";

    private static Context nmContext;

    private static Configuration conf = new Configuration();

    private DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private static final File testRootDir = new File("target", TestNMWebServicesContainers.class.getSimpleName());

    private static final File testLogDir = new File("target", ((TestNMWebServicesContainers.class.getSimpleName()) + "LogDir"));

    private static class WebServletModule extends ServletModule {
        @Override
        protected void configureServlets() {
            ResourceView resourceView = new ResourceView() {
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
            TestNMWebServicesAuxServices.conf.set(NM_LOCAL_DIRS, TestNMWebServicesAuxServices.testRootDir.getAbsolutePath());
            TestNMWebServicesAuxServices.conf.set(NM_LOG_DIRS, TestNMWebServicesAuxServices.testLogDir.getAbsolutePath());
            LocalDirsHandlerService dirsHandler = new LocalDirsHandlerService();
            NodeHealthCheckerService healthChecker = new NodeHealthCheckerService(NodeManager.getNodeHealthScriptRunner(TestNMWebServicesAuxServices.conf), dirsHandler);
            healthChecker.init(TestNMWebServicesAuxServices.conf);
            dirsHandler = healthChecker.getDiskHandler();
            ApplicationACLsManager aclsManager = new ApplicationACLsManager(TestNMWebServicesAuxServices.conf);
            TestNMWebServicesAuxServices.nmContext = new NodeManager.NMContext(null, null, dirsHandler, aclsManager, null, false, TestNMWebServicesAuxServices.conf) {
                public NodeId getNodeId() {
                    return NodeId.newInstance("testhost.foo.com", 8042);
                }

                public int getHttpPort() {
                    return 1234;
                }
            };
            WebApp nmWebApp = new org.apache.hadoop.yarn.server.nodemanager.webapp.WebServer.NMWebApp(resourceView, aclsManager, dirsHandler);
            bind(JAXBContextResolver.class);
            bind(NMWebServices.class);
            bind(GenericExceptionHandler.class);
            bind(Context.class).toInstance(TestNMWebServicesAuxServices.nmContext);
            bind(WebApp.class).toInstance(nmWebApp);
            bind(ResourceView.class).toInstance(resourceView);
            bind(ApplicationACLsManager.class).toInstance(aclsManager);
            bind(LocalDirsHandlerService.class).toInstance(dirsHandler);
            serve("/*").with(GuiceContainer.class);
        }
    }

    static {
        GuiceServletConfig.setInjector(Guice.createInjector(new TestNMWebServicesAuxServices.WebServletModule()));
    }

    public TestNMWebServicesAuxServices() {
        super(new WebAppDescriptor.Builder("org.apache.hadoop.yarn.server.nodemanager.webapp").contextListenerClass(GuiceServletConfig.class).filterClass(GuiceFilter.class).contextPath("jersey-guice-filter").servletPath("/").build());
    }

    @Test
    public void testNodeAuxServicesNone() throws Exception {
        addAuxServices();
        WebResource r = resource();
        ClientResponse response = r.path("ws").path("v1").path("node").path(TestNMWebServicesAuxServices.AUX_SERVICES_PATH).accept(APPLICATION_JSON).get(ClientResponse.class);
        Assert.assertEquals((((MediaType.APPLICATION_JSON_TYPE) + "; ") + (JettyUtils.UTF_8)), response.getType().toString());
        JSONObject json = response.getEntity(JSONObject.class);
        Assert.assertEquals("aux services isn't empty", new JSONObject().toString(), json.get("services").toString());
    }

    @Test
    public void testNodeAuxServices() throws Exception {
        testNodeHelper(TestNMWebServicesAuxServices.AUX_SERVICES_PATH, APPLICATION_JSON);
    }

    @Test
    public void testNodeAuxServicesSlash() throws Exception {
        testNodeHelper(((TestNMWebServicesAuxServices.AUX_SERVICES_PATH) + "/"), APPLICATION_JSON);
    }

    // make sure default is json output
    @Test
    public void testNodeAuxServicesDefault() throws Exception {
        testNodeHelper(((TestNMWebServicesAuxServices.AUX_SERVICES_PATH) + "/"), "");
    }

    @Test
    public void testNodeAuxServicesXML() throws Exception {
        AuxServiceRecord r1 = new AuxServiceRecord().name("name1").launchTime(new Date(123L)).version("1");
        AuxServiceRecord r2 = new AuxServiceRecord().name("name2").launchTime(new Date(456L));
        addAuxServices(r1, r2);
        WebResource r = resource();
        ClientResponse response = r.path("ws").path("v1").path("node").path(TestNMWebServicesAuxServices.AUX_SERVICES_PATH).accept(APPLICATION_XML).get(ClientResponse.class);
        Assert.assertEquals((((MediaType.APPLICATION_XML_TYPE) + "; ") + (JettyUtils.UTF_8)), response.getType().toString());
        String xml = response.getEntity(String.class);
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        InputSource is = new InputSource();
        is.setCharacterStream(new StringReader(xml));
        Document dom = db.parse(is);
        NodeList nodes = dom.getElementsByTagName("service");
        Assert.assertEquals("incorrect number of elements", 2, nodes.getLength());
        verifyAuxServicesInfoXML(nodes, r1, r2);
    }

    @Test
    public void testAuxServicesDisabled() throws Exception, JSONException {
        AuxServices auxServices = Mockito.mock(AuxServices.class);
        Mockito.when(auxServices.isManifestEnabled()).thenReturn(false);
        TestNMWebServicesAuxServices.nmContext.setAuxServices(auxServices);
        WebResource r = resource();
        try {
            r.path("ws").path("v1").path("node").path(TestNMWebServicesAuxServices.AUX_SERVICES_PATH).accept(APPLICATION_JSON).get(JSONObject.class);
            Assert.fail("should have thrown exception on invalid user query");
        } catch (UniformInterfaceException ue) {
            ClientResponse response = ue.getResponse();
            assertResponseStatusCode(ClientResponse.Status.BAD_REQUEST, response.getStatusInfo());
            Assert.assertEquals((((MediaType.APPLICATION_JSON_TYPE) + "; ") + (JettyUtils.UTF_8)), response.getType().toString());
            JSONObject msg = response.getEntity(JSONObject.class);
            JSONObject exception = msg.getJSONObject("RemoteException");
            Assert.assertEquals("incorrect number of elements", 3, exception.length());
            String message = exception.getString("message");
            String type = exception.getString("exception");
            String classname = exception.getString("javaClassName");
            WebServicesTestUtils.checkStringMatch("exception message", "java.lang.Exception: Auxiliary services manifest is not enabled", message);
            WebServicesTestUtils.checkStringMatch("exception type", "BadRequestException", type);
            WebServicesTestUtils.checkStringMatch("exception classname", "org.apache.hadoop.yarn.webapp.BadRequestException", classname);
        }
    }
}

