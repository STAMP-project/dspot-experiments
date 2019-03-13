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
package org.apache.hadoop.yarn.server.resourcemanager.webapp.fairscheduler;


import MediaType.APPLICATION_JSON;
import MediaType.APPLICATION_XML;
import YarnConfiguration.RM_CONFIGURATION_PROVIDER_CLASS;
import YarnConfiguration.RM_SCHEDULER;
import com.google.inject.servlet.GuiceFilter;
import com.google.inject.servlet.ServletModule;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.guice.spi.container.servlet.GuiceContainer;
import com.sun.jersey.test.framework.WebAppDescriptor;
import org.apache.hadoop.yarn.conf.YarnConfiguration;
import org.apache.hadoop.yarn.server.resourcemanager.MockRM;
import org.apache.hadoop.yarn.server.resourcemanager.ResourceManager;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.ResourceScheduler;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.fair.FSLeafQueue;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.fair.FairScheduler;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.fair.QueueManager;
import org.apache.hadoop.yarn.server.resourcemanager.webapp.JAXBContextResolver;
import org.apache.hadoop.yarn.server.resourcemanager.webapp.RMWebServices;
import org.apache.hadoop.yarn.util.resource.CustomResourceTypesConfigurationProvider;
import org.apache.hadoop.yarn.util.resource.ResourceUtils;
import org.apache.hadoop.yarn.webapp.GenericExceptionHandler;
import org.apache.hadoop.yarn.webapp.GuiceServletConfig;
import org.apache.hadoop.yarn.webapp.JerseyTestBase;
import org.junit.Test;


/**
 * This class is to test response representations of queue resources,
 * explicitly setting custom resource types. with the help of
 * {@link CustomResourceTypesConfigurationProvider}
 */
public class TestRMWebServicesFairSchedulerCustomResourceTypes extends JerseyTestBase {
    private static MockRM rm;

    private static YarnConfiguration conf;

    private static class WebServletModule extends ServletModule {
        @Override
        protected void configureServlets() {
            bind(JAXBContextResolver.class);
            bind(RMWebServices.class);
            bind(GenericExceptionHandler.class);
            TestRMWebServicesFairSchedulerCustomResourceTypes.conf = new YarnConfiguration();
            TestRMWebServicesFairSchedulerCustomResourceTypes.conf.setClass(RM_SCHEDULER, FairScheduler.class, ResourceScheduler.class);
            initResourceTypes(TestRMWebServicesFairSchedulerCustomResourceTypes.conf);
            TestRMWebServicesFairSchedulerCustomResourceTypes.rm = new MockRM(TestRMWebServicesFairSchedulerCustomResourceTypes.conf);
            bind(ResourceManager.class).toInstance(TestRMWebServicesFairSchedulerCustomResourceTypes.rm);
            serve("/*").with(GuiceContainer.class);
        }

        private void initResourceTypes(YarnConfiguration conf) {
            conf.set(RM_CONFIGURATION_PROVIDER_CLASS, CustomResourceTypesConfigurationProvider.class.getName());
            ResourceUtils.resetResourceTypes(conf);
        }
    }

    public TestRMWebServicesFairSchedulerCustomResourceTypes() {
        super(new WebAppDescriptor.Builder("org.apache.hadoop.yarn.server.resourcemanager.webapp").contextListenerClass(GuiceServletConfig.class).filterClass(GuiceFilter.class).contextPath("jersey-guice-filter").servletPath("/").build());
    }

    @Test
    public void testClusterSchedulerWithCustomResourceTypesJson() {
        FairScheduler scheduler = ((FairScheduler) (getResourceScheduler()));
        QueueManager queueManager = scheduler.getQueueManager();
        // create LeafQueues
        queueManager.getLeafQueue("root.q.subqueue1", true);
        queueManager.getLeafQueue("root.q.subqueue2", true);
        FSLeafQueue subqueue1 = queueManager.getLeafQueue("root.q.subqueue1", false);
        incrementUsedResourcesOnQueue(subqueue1, 33L);
        WebResource path = resource().path("ws").path("v1").path("cluster").path("scheduler");
        ClientResponse response = path.accept(APPLICATION_JSON).get(ClientResponse.class);
        verifyJsonResponse(path, response, CustomResourceTypesConfigurationProvider.getCustomResourceTypes());
    }

    @Test
    public void testClusterSchedulerWithCustomResourceTypesXml() {
        FairScheduler scheduler = ((FairScheduler) (getResourceScheduler()));
        QueueManager queueManager = scheduler.getQueueManager();
        // create LeafQueues
        queueManager.getLeafQueue("root.q.subqueue1", true);
        queueManager.getLeafQueue("root.q.subqueue2", true);
        FSLeafQueue subqueue1 = queueManager.getLeafQueue("root.q.subqueue1", false);
        incrementUsedResourcesOnQueue(subqueue1, 33L);
        WebResource path = resource().path("ws").path("v1").path("cluster").path("scheduler");
        ClientResponse response = path.accept(APPLICATION_XML).get(ClientResponse.class);
        verifyXmlResponse(path, response, CustomResourceTypesConfigurationProvider.getCustomResourceTypes());
    }

    @Test
    public void testClusterSchedulerWithElevenCustomResourceTypesXml() {
        CustomResourceTypesConfigurationProvider.setResourceTypes(11, "k");
        createInjectorForWebServletModule();
        FairScheduler scheduler = ((FairScheduler) (getResourceScheduler()));
        QueueManager queueManager = scheduler.getQueueManager();
        // create LeafQueues
        queueManager.getLeafQueue("root.q.subqueue1", true);
        queueManager.getLeafQueue("root.q.subqueue2", true);
        FSLeafQueue subqueue1 = queueManager.getLeafQueue("root.q.subqueue1", false);
        incrementUsedResourcesOnQueue(subqueue1, 33L);
        WebResource path = resource().path("ws").path("v1").path("cluster").path("scheduler");
        ClientResponse response = path.accept(APPLICATION_XML).get(ClientResponse.class);
        verifyXmlResponse(path, response, CustomResourceTypesConfigurationProvider.getCustomResourceTypes());
    }

    @Test
    public void testClusterSchedulerElevenWithCustomResourceTypesJson() {
        CustomResourceTypesConfigurationProvider.setResourceTypes(11, "k");
        createInjectorForWebServletModule();
        FairScheduler scheduler = ((FairScheduler) (getResourceScheduler()));
        QueueManager queueManager = scheduler.getQueueManager();
        // create LeafQueues
        queueManager.getLeafQueue("root.q.subqueue1", true);
        queueManager.getLeafQueue("root.q.subqueue2", true);
        FSLeafQueue subqueue1 = queueManager.getLeafQueue("root.q.subqueue1", false);
        incrementUsedResourcesOnQueue(subqueue1, 33L);
        WebResource path = resource().path("ws").path("v1").path("cluster").path("scheduler");
        ClientResponse response = path.accept(APPLICATION_JSON).get(ClientResponse.class);
        verifyJsonResponse(path, response, CustomResourceTypesConfigurationProvider.getCustomResourceTypes());
    }
}

