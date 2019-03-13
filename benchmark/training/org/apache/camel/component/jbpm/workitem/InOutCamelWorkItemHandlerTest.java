/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.camel.component.jbpm.workitem;


import JBPMConstants.CAMEL_ENDPOINT_ID_WI_PARAM;
import JBPMConstants.RESPONSE_WI_PARAM;
import java.util.Map;
import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.impl.DefaultHeadersMapFactory;
import org.apache.camel.spi.HeadersMapFactory;
import org.drools.core.process.instance.impl.WorkItemImpl;
import org.hamcrest.CoreMatchers;
import org.jbpm.process.workitem.core.TestWorkItemManager;
import org.jbpm.services.api.service.ServiceRegistry;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.api.runtime.manager.RuntimeManager;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class InOutCamelWorkItemHandlerTest {
    @Mock
    ProducerTemplate producerTemplate;

    @Mock
    Exchange outExchange;

    @Mock
    Message outMessage;

    @Mock
    CamelContext camelContext;

    @Mock
    RuntimeManager runtimeManager;

    @Test
    public void testExecuteInOutGlobalCamelContext() throws Exception {
        String camelEndpointId = "testCamelRoute";
        String camelRouteUri = "direct:" + camelEndpointId;
        String testReponse = "testResponse";
        Mockito.when(producerTemplate.send(ArgumentMatchers.eq(camelRouteUri), ArgumentMatchers.any(Exchange.class))).thenReturn(outExchange);
        Mockito.when(producerTemplate.getCamelContext()).thenReturn(camelContext);
        Mockito.when(camelContext.createProducerTemplate()).thenReturn(producerTemplate);
        HeadersMapFactory hmf = new DefaultHeadersMapFactory();
        Mockito.when(camelContext.getHeadersMapFactory()).thenReturn(hmf);
        Mockito.when(outExchange.getOut()).thenReturn(outMessage);
        Mockito.when(outMessage.getBody()).thenReturn(testReponse);
        try {
            ServiceRegistry.get().register("GlobalCamelService", camelContext);
            TestWorkItemManager manager = new TestWorkItemManager();
            WorkItemImpl workItem = new WorkItemImpl();
            workItem.setParameter("CamelEndpointId", camelEndpointId);
            workItem.setParameter("Request", "someRequest");
            workItem.setDeploymentId("testDeploymentId");
            workItem.setProcessInstanceId(1L);
            workItem.setId(1L);
            AbstractCamelWorkItemHandler handler = new InOutCamelWorkItemHandler();
            handler.executeWorkItem(workItem, manager);
            Assert.assertThat(manager.getResults(), CoreMatchers.is(CoreMatchers.notNullValue()));
            Assert.assertThat(manager.getResults().size(), CoreMatchers.equalTo(1));
            Assert.assertThat(manager.getResults().containsKey(workItem.getId()), CoreMatchers.is(true));
            Map<String, Object> results = manager.getResults(workItem.getId());
            Assert.assertThat(results.size(), CoreMatchers.equalTo(2));
            Assert.assertThat(results.get("Response"), CoreMatchers.equalTo(testReponse));
        } finally {
            ServiceRegistry.get().remove("GlobalCamelService");
        }
    }

    @Test
    public void testExecuteInOutLocalCamelContext() throws Exception {
        String camelEndpointId = "testCamelRoute";
        String camelRouteUri = "direct:" + camelEndpointId;
        String testReponse = "testResponse";
        String runtimeManagerId = "testRuntimeManager";
        Mockito.when(runtimeManager.getIdentifier()).thenReturn(runtimeManagerId);
        Mockito.when(producerTemplate.send(ArgumentMatchers.eq(camelRouteUri), ArgumentMatchers.any(Exchange.class))).thenReturn(outExchange);
        Mockito.when(producerTemplate.getCamelContext()).thenReturn(camelContext);
        Mockito.when(camelContext.createProducerTemplate()).thenReturn(producerTemplate);
        HeadersMapFactory hmf = new DefaultHeadersMapFactory();
        Mockito.when(camelContext.getHeadersMapFactory()).thenReturn(hmf);
        Mockito.when(outExchange.getOut()).thenReturn(outMessage);
        Mockito.when(outMessage.getBody()).thenReturn(testReponse);
        // Register the RuntimeManager bound camelcontext.
        try {
            ServiceRegistry.get().register((runtimeManagerId + "_CamelService"), camelContext);
            WorkItemImpl workItem = new WorkItemImpl();
            workItem.setParameter(CAMEL_ENDPOINT_ID_WI_PARAM, camelEndpointId);
            workItem.setParameter("Request", "someRequest");
            workItem.setDeploymentId("testDeploymentId");
            workItem.setProcessInstanceId(1L);
            workItem.setId(1L);
            AbstractCamelWorkItemHandler handler = new InOutCamelWorkItemHandler(runtimeManager);
            TestWorkItemManager manager = new TestWorkItemManager();
            handler.executeWorkItem(workItem, manager);
            Assert.assertThat(manager.getResults(), CoreMatchers.is(CoreMatchers.notNullValue()));
            Assert.assertThat(manager.getResults().size(), CoreMatchers.equalTo(1));
            Assert.assertThat(manager.getResults().containsKey(workItem.getId()), CoreMatchers.is(true));
            Map<String, Object> results = manager.getResults(workItem.getId());
            Assert.assertThat(results.size(), CoreMatchers.equalTo(2));
            Assert.assertThat(results.get(RESPONSE_WI_PARAM), CoreMatchers.equalTo(testReponse));
        } finally {
            ServiceRegistry.get().remove((runtimeManagerId + "_CamelService"));
        }
    }

    @Test
    public void testExecuteInOutLocalCamelContextLazyInit() throws Exception {
        String camelEndpointId = "testCamelRoute";
        String camelRouteUri = "direct:" + camelEndpointId;
        String testReponse = "testResponse";
        String runtimeManagerId = "testRuntimeManager";
        Mockito.when(runtimeManager.getIdentifier()).thenReturn(runtimeManagerId);
        Mockito.when(producerTemplate.send(ArgumentMatchers.eq(camelRouteUri), ArgumentMatchers.any(Exchange.class))).thenReturn(outExchange);
        Mockito.when(producerTemplate.getCamelContext()).thenReturn(camelContext);
        Mockito.when(camelContext.createProducerTemplate()).thenReturn(producerTemplate);
        HeadersMapFactory hmf = new DefaultHeadersMapFactory();
        Mockito.when(camelContext.getHeadersMapFactory()).thenReturn(hmf);
        Mockito.when(outExchange.getOut()).thenReturn(outMessage);
        Mockito.when(outMessage.getBody()).thenReturn(testReponse);
        WorkItemImpl workItem = new WorkItemImpl();
        workItem.setParameter(CAMEL_ENDPOINT_ID_WI_PARAM, camelEndpointId);
        workItem.setParameter("Request", "someRequest");
        workItem.setDeploymentId("testDeploymentId");
        workItem.setProcessInstanceId(1L);
        workItem.setId(1L);
        AbstractCamelWorkItemHandler handler = new InOutCamelWorkItemHandler(runtimeManager);
        // Register the context after we've created the WIH to test lazy-init.
        try {
            ServiceRegistry.get().register((runtimeManagerId + "_CamelService"), camelContext);
            TestWorkItemManager manager = new TestWorkItemManager();
            handler.executeWorkItem(workItem, manager);
            Assert.assertThat(manager.getResults(), CoreMatchers.is(CoreMatchers.notNullValue()));
            Assert.assertThat(manager.getResults().size(), CoreMatchers.equalTo(1));
            Assert.assertThat(manager.getResults().containsKey(workItem.getId()), CoreMatchers.is(true));
            Map<String, Object> results = manager.getResults(workItem.getId());
            Assert.assertThat(results.size(), CoreMatchers.equalTo(2));
            Assert.assertThat(results.get(RESPONSE_WI_PARAM), CoreMatchers.equalTo(testReponse));
        } finally {
            ServiceRegistry.get().remove((runtimeManagerId + "_CamelService"));
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void testExecuteInOutLocalCamelContextLazyInitFail() throws Exception {
        String camelEndpointId = "testCamelRoute";
        String camelRouteUri = "direct:" + camelEndpointId;
        String testReponse = "testResponse";
        String runtimeManagerId = "testRuntimeManager";
        Mockito.when(runtimeManager.getIdentifier()).thenReturn(runtimeManagerId);
        WorkItemImpl workItem = new WorkItemImpl();
        workItem.setParameter(CAMEL_ENDPOINT_ID_WI_PARAM, camelEndpointId);
        workItem.setParameter("Request", "someRequest");
        workItem.setDeploymentId("testDeploymentId");
        workItem.setProcessInstanceId(1L);
        workItem.setId(1L);
        AbstractCamelWorkItemHandler handler = new InOutCamelWorkItemHandler(runtimeManager);
        TestWorkItemManager manager = new TestWorkItemManager();
        // This is expected to throw an exception.
        handler.executeWorkItem(workItem, manager);
    }
}

