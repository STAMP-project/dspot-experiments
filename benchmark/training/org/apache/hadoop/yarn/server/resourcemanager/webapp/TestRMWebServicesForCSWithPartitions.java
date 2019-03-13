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
import YarnConfiguration.RM_SCHEDULER;
import com.google.common.collect.ImmutableSet;
import com.google.inject.Guice;
import com.google.inject.servlet.GuiceFilter;
import com.google.inject.servlet.ServletModule;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.guice.spi.container.servlet.GuiceContainer;
import com.sun.jersey.test.framework.WebAppDescriptor;
import java.io.StringReader;
import java.util.HashSet;
import java.util.Set;
import javax.ws.rs.core.MediaType;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.apache.hadoop.http.JettyUtils;
import org.apache.hadoop.yarn.api.records.NodeLabel;
import org.apache.hadoop.yarn.conf.YarnConfiguration;
import org.apache.hadoop.yarn.server.resourcemanager.MockRM;
import org.apache.hadoop.yarn.server.resourcemanager.ResourceManager;
import org.apache.hadoop.yarn.server.resourcemanager.nodelabels.RMNodeLabelsManager;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.ResourceScheduler;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.capacity.CapacityScheduler;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.capacity.CapacitySchedulerConfiguration;
import org.apache.hadoop.yarn.webapp.GenericExceptionHandler;
import org.apache.hadoop.yarn.webapp.GuiceServletConfig;
import org.apache.hadoop.yarn.webapp.JerseyTestBase;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;


public class TestRMWebServicesForCSWithPartitions extends JerseyTestBase {
    private static final String DEFAULT_PARTITION = "";

    private static final String CAPACITIES = "capacities";

    private static final String RESOURCE_USAGES_BY_PARTITION = "resourceUsagesByPartition";

    private static final String QUEUE_CAPACITIES_BY_PARTITION = "queueCapacitiesByPartition";

    private static final String QUEUE_C = "Qc";

    private static final String LEAF_QUEUE_C1 = "Qc1";

    private static final String LEAF_QUEUE_C2 = "Qc2";

    private static final String QUEUE_B = "Qb";

    private static final String QUEUE_A = "Qa";

    private static final String LABEL_LY = "Ly";

    private static final String LABEL_LX = "Lx";

    private static final ImmutableSet<String> CLUSTER_LABELS = ImmutableSet.of(TestRMWebServicesForCSWithPartitions.LABEL_LX, TestRMWebServicesForCSWithPartitions.LABEL_LY, TestRMWebServicesForCSWithPartitions.DEFAULT_PARTITION);

    private static MockRM rm;

    private static CapacitySchedulerConfiguration csConf;

    private static YarnConfiguration conf;

    private static class WebServletModule extends ServletModule {
        @Override
        protected void configureServlets() {
            bind(JAXBContextResolver.class);
            bind(RMWebServices.class);
            bind(GenericExceptionHandler.class);
            TestRMWebServicesForCSWithPartitions.csConf = new CapacitySchedulerConfiguration();
            TestRMWebServicesForCSWithPartitions.setupQueueConfiguration(TestRMWebServicesForCSWithPartitions.csConf);
            TestRMWebServicesForCSWithPartitions.conf = new YarnConfiguration(TestRMWebServicesForCSWithPartitions.csConf);
            TestRMWebServicesForCSWithPartitions.conf.setClass(RM_SCHEDULER, CapacityScheduler.class, ResourceScheduler.class);
            TestRMWebServicesForCSWithPartitions.rm = new MockRM(TestRMWebServicesForCSWithPartitions.conf);
            Set<NodeLabel> labels = new HashSet<NodeLabel>();
            labels.add(NodeLabel.newInstance(TestRMWebServicesForCSWithPartitions.LABEL_LX));
            labels.add(NodeLabel.newInstance(TestRMWebServicesForCSWithPartitions.LABEL_LY));
            try {
                RMNodeLabelsManager nodeLabelManager = getRMContext().getNodeLabelManager();
                nodeLabelManager.addToCluserNodeLabels(labels);
            } catch (Exception e) {
                Assert.fail();
            }
            bind(ResourceManager.class).toInstance(TestRMWebServicesForCSWithPartitions.rm);
            serve("/*").with(GuiceContainer.class);
        }
    }

    static {
        GuiceServletConfig.setInjector(Guice.createInjector(new TestRMWebServicesForCSWithPartitions.WebServletModule()));
    }

    public TestRMWebServicesForCSWithPartitions() {
        super(new WebAppDescriptor.Builder("org.apache.hadoop.yarn.server.resourcemanager.webapp").contextListenerClass(GuiceServletConfig.class).filterClass(GuiceFilter.class).contextPath("jersey-guice-filter").servletPath("/").build());
    }

    @Test
    public void testSchedulerPartitions() throws Exception, JSONException {
        WebResource r = resource();
        ClientResponse response = r.path("ws").path("v1").path("cluster").path("scheduler").accept(APPLICATION_JSON).get(ClientResponse.class);
        Assert.assertEquals((((MediaType.APPLICATION_JSON_TYPE) + "; ") + (JettyUtils.UTF_8)), response.getType().toString());
        JSONObject json = response.getEntity(JSONObject.class);
        verifySchedulerInfoJson(json);
    }

    @Test
    public void testSchedulerPartitionsSlash() throws Exception, JSONException {
        WebResource r = resource();
        ClientResponse response = r.path("ws").path("v1").path("cluster").path("scheduler/").accept(APPLICATION_JSON).get(ClientResponse.class);
        Assert.assertEquals((((MediaType.APPLICATION_JSON_TYPE) + "; ") + (JettyUtils.UTF_8)), response.getType().toString());
        JSONObject json = response.getEntity(JSONObject.class);
        verifySchedulerInfoJson(json);
    }

    @Test
    public void testSchedulerPartitionsDefault() throws Exception, JSONException {
        WebResource r = resource();
        ClientResponse response = r.path("ws").path("v1").path("cluster").path("scheduler").get(ClientResponse.class);
        Assert.assertEquals((((MediaType.APPLICATION_JSON_TYPE) + "; ") + (JettyUtils.UTF_8)), response.getType().toString());
        JSONObject json = response.getEntity(JSONObject.class);
        verifySchedulerInfoJson(json);
    }

    @Test
    public void testSchedulerPartitionsXML() throws Exception, JSONException {
        WebResource r = resource();
        ClientResponse response = r.path("ws").path("v1").path("cluster").path("scheduler").accept(APPLICATION_XML).get(ClientResponse.class);
        Assert.assertEquals((((MediaType.APPLICATION_XML_TYPE) + "; ") + (JettyUtils.UTF_8)), response.getType().toString());
        String xml = response.getEntity(String.class);
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        InputSource is = new InputSource();
        is.setCharacterStream(new StringReader(xml));
        Document dom = db.parse(is);
        verifySchedulerInfoXML(dom);
    }
}

