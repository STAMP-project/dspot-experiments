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
import YarnConfiguration.YARN_ADMIN_ACL;
import com.google.inject.servlet.GuiceFilter;
import com.google.inject.servlet.ServletModule;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.core.util.MultivaluedMapImpl;
import com.sun.jersey.guice.spi.container.servlet.GuiceContainer;
import com.sun.jersey.test.framework.WebAppDescriptor;
import java.io.IOException;
import java.util.ArrayList;
import javax.ws.rs.core.MediaType;
import org.apache.hadoop.http.JettyUtils;
import org.apache.hadoop.security.UserGroupInformation;
import org.apache.hadoop.yarn.conf.YarnConfiguration;
import org.apache.hadoop.yarn.server.resourcemanager.MockRM;
import org.apache.hadoop.yarn.server.resourcemanager.ResourceManager;
import org.apache.hadoop.yarn.server.resourcemanager.webapp.dao.LabelsToNodesInfo;
import org.apache.hadoop.yarn.server.resourcemanager.webapp.dao.NodeLabelInfo;
import org.apache.hadoop.yarn.server.resourcemanager.webapp.dao.NodeLabelsInfo;
import org.apache.hadoop.yarn.server.resourcemanager.webapp.dao.NodeToLabelsEntry;
import org.apache.hadoop.yarn.server.resourcemanager.webapp.dao.NodeToLabelsEntryList;
import org.apache.hadoop.yarn.server.resourcemanager.webapp.dao.NodeToLabelsInfo;
import org.apache.hadoop.yarn.webapp.GenericExceptionHandler;
import org.apache.hadoop.yarn.webapp.GuiceServletConfig;
import org.apache.hadoop.yarn.webapp.JerseyTestBase;
import org.codehaus.jettison.json.JSONException;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class TestRMWebServicesNodeLabels extends JerseyTestBase {
    private static final int BAD_REQUEST_CODE = 400;

    private static final Logger LOG = LoggerFactory.getLogger(TestRMWebServicesNodeLabels.class);

    private static MockRM rm;

    private static YarnConfiguration conf;

    private static String userName;

    private static String notUserName;

    private static RMWebServices rmWebService;

    private static class WebServletModule extends ServletModule {
        @Override
        protected void configureServlets() {
            bind(JAXBContextResolver.class);
            try {
                TestRMWebServicesNodeLabels.userName = UserGroupInformation.getCurrentUser().getShortUserName();
            } catch (IOException ioe) {
                throw new RuntimeException(("Unable to get current user name " + (ioe.getMessage())), ioe);
            }
            TestRMWebServicesNodeLabels.notUserName = (TestRMWebServicesNodeLabels.userName) + "abc123";
            TestRMWebServicesNodeLabels.conf = new YarnConfiguration();
            TestRMWebServicesNodeLabels.conf.set(YARN_ADMIN_ACL, TestRMWebServicesNodeLabels.userName);
            TestRMWebServicesNodeLabels.rm = new MockRM(TestRMWebServicesNodeLabels.conf);
            TestRMWebServicesNodeLabels.rmWebService = new RMWebServices(TestRMWebServicesNodeLabels.rm, TestRMWebServicesNodeLabels.conf);
            bind(RMWebServices.class).toInstance(TestRMWebServicesNodeLabels.rmWebService);
            bind(GenericExceptionHandler.class);
            bind(ResourceManager.class).toInstance(TestRMWebServicesNodeLabels.rm);
            filter("/*").through(TestRMWebServicesAppsModification.TestRMCustomAuthFilter.class);
            serve("/*").with(GuiceContainer.class);
        }
    }

    public TestRMWebServicesNodeLabels() {
        super(new WebAppDescriptor.Builder("org.apache.hadoop.yarn.server.resourcemanager.webapp").contextListenerClass(GuiceServletConfig.class).filterClass(GuiceFilter.class).contextPath("jersey-guice-filter").servletPath("/").build());
    }

    @Test
    public void testNodeLabels() throws Exception, JSONException {
        WebResource r = resource();
        ClientResponse response;
        // Add a label
        NodeLabelsInfo nlsifo = new NodeLabelsInfo();
        nlsifo.getNodeLabelsInfo().add(new NodeLabelInfo("a"));
        response = r.path("ws").path("v1").path("cluster").path("add-node-labels").queryParam("user.name", TestRMWebServicesNodeLabels.userName).accept(APPLICATION_JSON).entity(toJson(nlsifo, NodeLabelsInfo.class), APPLICATION_JSON).post(ClientResponse.class);
        // Verify
        response = r.path("ws").path("v1").path("cluster").path("get-node-labels").queryParam("user.name", TestRMWebServicesNodeLabels.userName).accept(APPLICATION_JSON).get(ClientResponse.class);
        Assert.assertEquals((((MediaType.APPLICATION_JSON_TYPE) + "; ") + (JettyUtils.UTF_8)), response.getType().toString());
        nlsifo = response.getEntity(NodeLabelsInfo.class);
        Assert.assertEquals(1, nlsifo.getNodeLabels().size());
        for (NodeLabelInfo nl : nlsifo.getNodeLabelsInfo()) {
            Assert.assertEquals("a", nl.getName());
            Assert.assertTrue(nl.getExclusivity());
        }
        // Add another
        nlsifo = new NodeLabelsInfo();
        nlsifo.getNodeLabelsInfo().add(new NodeLabelInfo("b", false));
        response = r.path("ws").path("v1").path("cluster").path("add-node-labels").queryParam("user.name", TestRMWebServicesNodeLabels.userName).accept(APPLICATION_JSON).entity(toJson(nlsifo, NodeLabelsInfo.class), APPLICATION_JSON).post(ClientResponse.class);
        // Verify
        response = r.path("ws").path("v1").path("cluster").path("get-node-labels").queryParam("user.name", TestRMWebServicesNodeLabels.userName).accept(APPLICATION_JSON).get(ClientResponse.class);
        Assert.assertEquals((((MediaType.APPLICATION_JSON_TYPE) + "; ") + (JettyUtils.UTF_8)), response.getType().toString());
        nlsifo = response.getEntity(NodeLabelsInfo.class);
        Assert.assertEquals(2, nlsifo.getNodeLabels().size());
        // Verify exclusivity for 'y' as false
        for (NodeLabelInfo nl : nlsifo.getNodeLabelsInfo()) {
            if (nl.getName().equals("b")) {
                Assert.assertFalse(nl.getExclusivity());
            }
        }
        // Add labels to a node
        MultivaluedMapImpl params = new MultivaluedMapImpl();
        params.add("labels", "a");
        response = r.path("ws").path("v1").path("cluster").path("nodes").path("nid:0").path("replace-labels").queryParam("user.name", TestRMWebServicesNodeLabels.userName).queryParams(params).accept(APPLICATION_JSON).post(ClientResponse.class);
        TestRMWebServicesNodeLabels.LOG.info("posted node nodelabel");
        // Add labels to another node
        params = new MultivaluedMapImpl();
        params.add("labels", "b");
        response = r.path("ws").path("v1").path("cluster").path("nodes").path("nid1:0").path("replace-labels").queryParam("user.name", TestRMWebServicesNodeLabels.userName).queryParams(params).accept(APPLICATION_JSON).post(ClientResponse.class);
        TestRMWebServicesNodeLabels.LOG.info("posted node nodelabel");
        // Add labels to another node
        params = new MultivaluedMapImpl();
        params.add("labels", "b");
        response = r.path("ws").path("v1").path("cluster").path("nodes").path("nid2:0").path("replace-labels").queryParam("user.name", TestRMWebServicesNodeLabels.userName).queryParams(params).accept(APPLICATION_JSON).post(ClientResponse.class);
        TestRMWebServicesNodeLabels.LOG.info("posted node nodelabel");
        // Verify, using get-labels-to-Nodes
        response = r.path("ws").path("v1").path("cluster").path("label-mappings").queryParam("user.name", TestRMWebServicesNodeLabels.userName).accept(APPLICATION_JSON).get(ClientResponse.class);
        Assert.assertEquals((((MediaType.APPLICATION_JSON_TYPE) + "; ") + (JettyUtils.UTF_8)), response.getType().toString());
        LabelsToNodesInfo ltni = response.getEntity(LabelsToNodesInfo.class);
        Assert.assertEquals(2, ltni.getLabelsToNodes().size());
        NodeIDsInfo nodes = ltni.getLabelsToNodes().get(new NodeLabelInfo("b", false));
        Assert.assertTrue(nodes.getNodeIDs().contains("nid2:0"));
        Assert.assertTrue(nodes.getNodeIDs().contains("nid1:0"));
        nodes = ltni.getLabelsToNodes().get(new NodeLabelInfo("a"));
        Assert.assertTrue(nodes.getNodeIDs().contains("nid:0"));
        // Verify, using get-labels-to-Nodes for specified set of labels
        params = new MultivaluedMapImpl();
        params.add("labels", "a");
        response = r.path("ws").path("v1").path("cluster").path("label-mappings").queryParam("user.name", TestRMWebServicesNodeLabels.userName).queryParams(params).accept(APPLICATION_JSON).get(ClientResponse.class);
        Assert.assertEquals((((MediaType.APPLICATION_JSON_TYPE) + "; ") + (JettyUtils.UTF_8)), response.getType().toString());
        ltni = response.getEntity(LabelsToNodesInfo.class);
        Assert.assertEquals(1, ltni.getLabelsToNodes().size());
        nodes = ltni.getLabelsToNodes().get(new NodeLabelInfo("a"));
        Assert.assertTrue(nodes.getNodeIDs().contains("nid:0"));
        // Verify
        response = r.path("ws").path("v1").path("cluster").path("nodes").path("nid:0").path("get-labels").queryParam("user.name", TestRMWebServicesNodeLabels.userName).accept(APPLICATION_JSON).get(ClientResponse.class);
        Assert.assertEquals((((MediaType.APPLICATION_JSON_TYPE) + "; ") + (JettyUtils.UTF_8)), response.getType().toString());
        nlsifo = response.getEntity(NodeLabelsInfo.class);
        Assert.assertTrue(nlsifo.getNodeLabelsInfo().contains(new NodeLabelInfo("a")));
        // Replace
        params = new MultivaluedMapImpl();
        params.add("labels", "b");
        response = r.path("ws").path("v1").path("cluster").path("nodes").path("nid:0").path("replace-labels").queryParam("user.name", TestRMWebServicesNodeLabels.userName).queryParams(params).accept(APPLICATION_JSON).post(ClientResponse.class);
        TestRMWebServicesNodeLabels.LOG.info("posted node nodelabel");
        // Verify
        response = r.path("ws").path("v1").path("cluster").path("nodes").path("nid:0").path("get-labels").queryParam("user.name", TestRMWebServicesNodeLabels.userName).accept(APPLICATION_JSON).get(ClientResponse.class);
        Assert.assertEquals((((MediaType.APPLICATION_JSON_TYPE) + "; ") + (JettyUtils.UTF_8)), response.getType().toString());
        nlsifo = response.getEntity(NodeLabelsInfo.class);
        Assert.assertTrue(nlsifo.getNodeLabelsInfo().contains(new NodeLabelInfo("b", false)));
        // Replace labels using node-to-labels
        NodeToLabelsEntryList ntli = new NodeToLabelsEntryList();
        ArrayList<String> labels = new ArrayList<String>();
        labels.add("a");
        NodeToLabelsEntry nli = new NodeToLabelsEntry("nid:0", labels);
        ntli.getNodeToLabels().add(nli);
        response = r.path("ws").path("v1").path("cluster").path("replace-node-to-labels").queryParam("user.name", TestRMWebServicesNodeLabels.userName).accept(APPLICATION_JSON).entity(toJson(ntli, NodeToLabelsEntryList.class), APPLICATION_JSON).post(ClientResponse.class);
        // Verify, using node-to-labels
        response = r.path("ws").path("v1").path("cluster").path("get-node-to-labels").queryParam("user.name", TestRMWebServicesNodeLabels.userName).accept(APPLICATION_JSON).get(ClientResponse.class);
        Assert.assertEquals((((MediaType.APPLICATION_JSON_TYPE) + "; ") + (JettyUtils.UTF_8)), response.getType().toString());
        NodeToLabelsInfo ntlinfo = response.getEntity(NodeToLabelsInfo.class);
        NodeLabelsInfo nlinfo = ntlinfo.getNodeToLabels().get("nid:0");
        Assert.assertEquals(1, nlinfo.getNodeLabels().size());
        Assert.assertTrue(nlinfo.getNodeLabelsInfo().contains(new NodeLabelInfo("a")));
        // Remove all
        params = new MultivaluedMapImpl();
        params.add("labels", "");
        response = r.path("ws").path("v1").path("cluster").path("nodes").path("nid:0").path("replace-labels").queryParam("user.name", TestRMWebServicesNodeLabels.userName).queryParams(params).accept(APPLICATION_JSON).post(ClientResponse.class);
        TestRMWebServicesNodeLabels.LOG.info("posted node nodelabel");
        // Verify
        response = r.path("ws").path("v1").path("cluster").path("nodes").path("nid:0").path("get-labels").queryParam("user.name", TestRMWebServicesNodeLabels.userName).accept(APPLICATION_JSON).get(ClientResponse.class);
        Assert.assertEquals((((MediaType.APPLICATION_JSON_TYPE) + "; ") + (JettyUtils.UTF_8)), response.getType().toString());
        nlsifo = response.getEntity(NodeLabelsInfo.class);
        Assert.assertTrue(nlsifo.getNodeLabelsInfo().isEmpty());
        // Add a label back for auth tests
        params = new MultivaluedMapImpl();
        params.add("labels", "a");
        response = r.path("ws").path("v1").path("cluster").path("nodes").path("nid:0").path("replace-labels").queryParam("user.name", TestRMWebServicesNodeLabels.userName).queryParams(params).accept(APPLICATION_JSON).post(ClientResponse.class);
        TestRMWebServicesNodeLabels.LOG.info("posted node nodelabel");
        // Verify
        response = r.path("ws").path("v1").path("cluster").path("nodes").path("nid:0").path("get-labels").queryParam("user.name", TestRMWebServicesNodeLabels.userName).accept(APPLICATION_JSON).get(ClientResponse.class);
        Assert.assertEquals((((MediaType.APPLICATION_JSON_TYPE) + "; ") + (JettyUtils.UTF_8)), response.getType().toString());
        nlsifo = response.getEntity(NodeLabelsInfo.class);
        Assert.assertTrue(nlsifo.getNodeLabelsInfo().contains(new NodeLabelInfo("a")));
        // Auth fail replace labels on node
        params = new MultivaluedMapImpl();
        params.add("labels", "b");
        response = r.path("ws").path("v1").path("cluster").path("nodes").path("nid:0").path("replace-labels").queryParam("user.name", TestRMWebServicesNodeLabels.notUserName).queryParams(params).accept(APPLICATION_JSON).post(ClientResponse.class);
        // Verify
        response = r.path("ws").path("v1").path("cluster").path("nodes").path("nid:0").path("get-labels").queryParam("user.name", TestRMWebServicesNodeLabels.userName).accept(APPLICATION_JSON).get(ClientResponse.class);
        Assert.assertEquals((((MediaType.APPLICATION_JSON_TYPE) + "; ") + (JettyUtils.UTF_8)), response.getType().toString());
        nlsifo = response.getEntity(NodeLabelsInfo.class);
        Assert.assertTrue(nlsifo.getNodeLabelsInfo().contains(new NodeLabelInfo("a")));
        // Fail to add a label with post
        response = r.path("ws").path("v1").path("cluster").path("add-node-labels").queryParam("user.name", TestRMWebServicesNodeLabels.notUserName).accept(APPLICATION_JSON).entity("{\"nodeLabels\":\"c\"}", APPLICATION_JSON).post(ClientResponse.class);
        // Verify
        response = r.path("ws").path("v1").path("cluster").path("get-node-labels").queryParam("user.name", TestRMWebServicesNodeLabels.userName).accept(APPLICATION_JSON).get(ClientResponse.class);
        Assert.assertEquals((((MediaType.APPLICATION_JSON_TYPE) + "; ") + (JettyUtils.UTF_8)), response.getType().toString());
        nlsifo = response.getEntity(NodeLabelsInfo.class);
        Assert.assertEquals(2, nlsifo.getNodeLabels().size());
        // Remove cluster label (succeed, we no longer need it)
        params = new MultivaluedMapImpl();
        params.add("labels", "b");
        response = r.path("ws").path("v1").path("cluster").path("remove-node-labels").queryParam("user.name", TestRMWebServicesNodeLabels.userName).queryParams(params).accept(APPLICATION_JSON).post(ClientResponse.class);
        // Verify
        response = r.path("ws").path("v1").path("cluster").path("get-node-labels").queryParam("user.name", TestRMWebServicesNodeLabels.userName).accept(APPLICATION_JSON).get(ClientResponse.class);
        Assert.assertEquals((((MediaType.APPLICATION_JSON_TYPE) + "; ") + (JettyUtils.UTF_8)), response.getType().toString());
        nlsifo = response.getEntity(NodeLabelsInfo.class);
        Assert.assertEquals(1, nlsifo.getNodeLabels().size());
        for (NodeLabelInfo nl : nlsifo.getNodeLabelsInfo()) {
            Assert.assertEquals("a", nl.getName());
            Assert.assertTrue(nl.getExclusivity());
        }
        // Remove cluster label with post
        params = new MultivaluedMapImpl();
        params.add("labels", "a");
        response = r.path("ws").path("v1").path("cluster").path("remove-node-labels").queryParam("user.name", TestRMWebServicesNodeLabels.userName).queryParams(params).accept(APPLICATION_JSON).post(ClientResponse.class);
        // Verify
        response = r.path("ws").path("v1").path("cluster").path("get-node-labels").queryParam("user.name", TestRMWebServicesNodeLabels.userName).accept(APPLICATION_JSON).get(ClientResponse.class);
        Assert.assertEquals((((MediaType.APPLICATION_JSON_TYPE) + "; ") + (JettyUtils.UTF_8)), response.getType().toString());
        nlsifo = response.getEntity(NodeLabelsInfo.class);
        Assert.assertEquals(0, nlsifo.getNodeLabels().size());
        // Following test cases are to test replace when distributed node label
        // configuration is on
        // Reset for testing : add cluster labels
        nlsifo = new NodeLabelsInfo();
        nlsifo.getNodeLabelsInfo().add(new NodeLabelInfo("x", false));
        nlsifo.getNodeLabelsInfo().add(new NodeLabelInfo("y", false));
        response = r.path("ws").path("v1").path("cluster").path("add-node-labels").queryParam("user.name", TestRMWebServicesNodeLabels.userName).accept(APPLICATION_JSON).entity(toJson(nlsifo, NodeLabelsInfo.class), APPLICATION_JSON).post(ClientResponse.class);
        // Reset for testing : Add labels to a node
        params = new MultivaluedMapImpl();
        params.add("labels", "y");
        response = r.path("ws").path("v1").path("cluster").path("nodes").path("nid:0").path("replace-labels").queryParam("user.name", TestRMWebServicesNodeLabels.userName).queryParams(params).accept(APPLICATION_JSON).post(ClientResponse.class);
        TestRMWebServicesNodeLabels.LOG.info("posted node nodelabel");
        // setting rmWebService for non Centralized NodeLabel Configuration
        TestRMWebServicesNodeLabels.rmWebService.isCentralizedNodeLabelConfiguration = false;
        // Case1 : Replace labels using node-to-labels
        ntli = new NodeToLabelsEntryList();
        labels = new ArrayList<String>();
        labels.add("x");
        nli = new NodeToLabelsEntry("nid:0", labels);
        ntli.getNodeToLabels().add(nli);
        response = r.path("ws").path("v1").path("cluster").path("replace-node-to-labels").queryParam("user.name", TestRMWebServicesNodeLabels.userName).accept(APPLICATION_JSON).entity(toJson(ntli, NodeToLabelsEntryList.class), APPLICATION_JSON).post(ClientResponse.class);
        // Verify, using node-to-labels that previous operation has failed
        response = r.path("ws").path("v1").path("cluster").path("get-node-to-labels").queryParam("user.name", TestRMWebServicesNodeLabels.userName).accept(APPLICATION_JSON).get(ClientResponse.class);
        Assert.assertEquals((((MediaType.APPLICATION_JSON_TYPE) + "; ") + (JettyUtils.UTF_8)), response.getType().toString());
        ntlinfo = response.getEntity(NodeToLabelsInfo.class);
        nlinfo = ntlinfo.getNodeToLabels().get("nid:0");
        Assert.assertEquals(1, nlinfo.getNodeLabels().size());
        Assert.assertFalse(nlinfo.getNodeLabelsInfo().contains(new NodeLabelInfo("x", false)));
        // Case2 : failure to Replace labels using replace-labels
        response = r.path("ws").path("v1").path("cluster").path("nodes").path("nid:0").path("replace-labels").queryParam("user.name", TestRMWebServicesNodeLabels.userName).accept(APPLICATION_JSON).entity("{\"nodeLabelName\": [\"x\"]}", APPLICATION_JSON).post(ClientResponse.class);
        TestRMWebServicesNodeLabels.LOG.info("posted node nodelabel");
        // Verify, using node-to-labels that previous operation has failed
        response = r.path("ws").path("v1").path("cluster").path("get-node-to-labels").queryParam("user.name", TestRMWebServicesNodeLabels.userName).accept(APPLICATION_JSON).get(ClientResponse.class);
        Assert.assertEquals((((MediaType.APPLICATION_JSON_TYPE) + "; ") + (JettyUtils.UTF_8)), response.getType().toString());
        ntlinfo = response.getEntity(NodeToLabelsInfo.class);
        nlinfo = ntlinfo.getNodeToLabels().get("nid:0");
        Assert.assertEquals(1, nlinfo.getNodeLabels().size());
        Assert.assertFalse(nlinfo.getNodeLabelsInfo().contains(new NodeLabelInfo("x", false)));
        // Case3 : Remove cluster label should be successful
        params = new MultivaluedMapImpl();
        params.add("labels", "x");
        response = r.path("ws").path("v1").path("cluster").path("remove-node-labels").queryParam("user.name", TestRMWebServicesNodeLabels.userName).queryParams(params).accept(APPLICATION_JSON).post(ClientResponse.class);
        // Verify
        response = r.path("ws").path("v1").path("cluster").path("get-node-labels").queryParam("user.name", TestRMWebServicesNodeLabels.userName).accept(APPLICATION_JSON).get(ClientResponse.class);
        Assert.assertEquals((((MediaType.APPLICATION_JSON_TYPE) + "; ") + (JettyUtils.UTF_8)), response.getType().toString());
        nlsifo = response.getEntity(NodeLabelsInfo.class);
        Assert.assertEquals(new NodeLabelInfo("y", false), nlsifo.getNodeLabelsInfo().get(0));
        Assert.assertEquals("y", nlsifo.getNodeLabelsInfo().get(0).getName());
        Assert.assertFalse(nlsifo.getNodeLabelsInfo().get(0).getExclusivity());
        // Remove y
        params = new MultivaluedMapImpl();
        params.add("labels", "y");
        response = r.path("ws").path("v1").path("cluster").path("remove-node-labels").queryParam("user.name", TestRMWebServicesNodeLabels.userName).queryParams(params).accept(APPLICATION_JSON).post(ClientResponse.class);
        // Verify
        response = r.path("ws").path("v1").path("cluster").path("get-node-labels").queryParam("user.name", TestRMWebServicesNodeLabels.userName).accept(APPLICATION_JSON).get(ClientResponse.class);
        Assert.assertEquals((((MediaType.APPLICATION_JSON_TYPE) + "; ") + (JettyUtils.UTF_8)), response.getType().toString());
        nlsifo = response.getEntity(NodeLabelsInfo.class);
        Assert.assertTrue(nlsifo.getNodeLabelsInfo().isEmpty());
        // add a new nodelabel with exclusity
        nlsifo = new NodeLabelsInfo();
        nlsifo.getNodeLabelsInfo().add(new NodeLabelInfo("z", false));
        response = r.path("ws").path("v1").path("cluster").path("add-node-labels").queryParam("user.name", TestRMWebServicesNodeLabels.userName).accept(APPLICATION_JSON).entity(toJson(nlsifo, NodeLabelsInfo.class), APPLICATION_JSON).post(ClientResponse.class);
        // Verify
        response = r.path("ws").path("v1").path("cluster").path("get-node-labels").queryParam("user.name", TestRMWebServicesNodeLabels.userName).accept(APPLICATION_JSON).get(ClientResponse.class);
        Assert.assertEquals((((MediaType.APPLICATION_JSON_TYPE) + "; ") + (JettyUtils.UTF_8)), response.getType().toString());
        nlsifo = response.getEntity(NodeLabelsInfo.class);
        Assert.assertEquals("z", nlsifo.getNodeLabelsInfo().get(0).getName());
        Assert.assertFalse(nlsifo.getNodeLabelsInfo().get(0).getExclusivity());
        Assert.assertEquals(1, nlsifo.getNodeLabels().size());
    }

    @Test
    public void testLabelInvalidAddition() throws UniformInterfaceException, Exception {
        WebResource r = resource();
        ClientResponse response;
        // Add a invalid label
        NodeLabelsInfo nlsifo = new NodeLabelsInfo();
        nlsifo.getNodeLabelsInfo().add(new NodeLabelInfo("a&"));
        response = r.path("ws").path("v1").path("cluster").path("add-node-labels").queryParam("user.name", TestRMWebServicesNodeLabels.userName).accept(APPLICATION_JSON).entity(toJson(nlsifo, NodeLabelsInfo.class), APPLICATION_JSON).post(ClientResponse.class);
        String expectedmessage = "java.io.IOException: label name should only contains" + (" {0-9, a-z, A-Z, -, _} and should not started with" + " {-,_}, now it is= a&");
        validateJsonExceptionContent(response, expectedmessage);
    }

    @Test
    public void testLabelChangeExclusivity() throws Exception, JSONException {
        WebResource r = resource();
        ClientResponse response;
        NodeLabelsInfo nlsifo = new NodeLabelsInfo();
        nlsifo.getNodeLabelsInfo().add(new NodeLabelInfo("newlabel", true));
        response = r.path("ws").path("v1").path("cluster").path("add-node-labels").queryParam("user.name", TestRMWebServicesNodeLabels.userName).accept(APPLICATION_JSON).entity(toJson(nlsifo, NodeLabelsInfo.class), APPLICATION_JSON).post(ClientResponse.class);
        // new info and change exclusivity
        NodeLabelsInfo nlsinfo2 = new NodeLabelsInfo();
        nlsinfo2.getNodeLabelsInfo().add(new NodeLabelInfo("newlabel", false));
        response = r.path("ws").path("v1").path("cluster").path("add-node-labels").queryParam("user.name", TestRMWebServicesNodeLabels.userName).accept(APPLICATION_JSON).entity(toJson(nlsinfo2, NodeLabelsInfo.class), APPLICATION_JSON).post(ClientResponse.class);
        String expectedmessage = "java.io.IOException: Exclusivity cannot be modified for an existing" + " label with : <newlabel:exclusivity=false>";
        validateJsonExceptionContent(response, expectedmessage);
    }

    @Test
    public void testLabelInvalidReplace() throws UniformInterfaceException, Exception {
        WebResource r = resource();
        ClientResponse response;
        // replace label which doesnt exist
        MultivaluedMapImpl params = new MultivaluedMapImpl();
        params.add("labels", "idontexist");
        response = r.path("ws").path("v1").path("cluster").path("nodes").path("nid:0").path("replace-labels").queryParam("user.name", TestRMWebServicesNodeLabels.userName).queryParams(params).accept(APPLICATION_JSON).post(ClientResponse.class);
        String expectedmessage = "Not all labels being replaced contained by known label" + " collections, please check, new labels=[idontexist]";
        validateJsonExceptionContent(response, expectedmessage);
    }

    @Test
    public void testLabelInvalidRemove() throws UniformInterfaceException, Exception {
        WebResource r = resource();
        ClientResponse response;
        MultivaluedMapImpl params = new MultivaluedMapImpl();
        params.add("labels", "irealldontexist");
        response = r.path("ws").path("v1").path("cluster").path("remove-node-labels").queryParam("user.name", TestRMWebServicesNodeLabels.userName).queryParams(params).accept(APPLICATION_JSON).post(ClientResponse.class);
        String expectedmessage = "java.io.IOException: Node label=irealldontexist to be" + (" removed doesn't existed in cluster node labels" + " collection.");
        validateJsonExceptionContent(response, expectedmessage);
    }
}

