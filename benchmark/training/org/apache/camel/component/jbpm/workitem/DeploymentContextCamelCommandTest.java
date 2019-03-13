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
import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.component.jbpm.JBPMConstants;
import org.drools.core.process.instance.impl.WorkItemImpl;
import org.jbpm.services.api.service.ServiceRegistry;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.api.executor.Command;
import org.kie.api.executor.CommandContext;
import org.kie.api.executor.ExecutionResults;
import org.kie.api.runtime.manager.RuntimeManager;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class DeploymentContextCamelCommandTest {
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

    @Mock
    CommandContext commandContext;

    @Test
    public void testExecuteCommandDeploymentCamelContext() throws Exception {
        String camelEndpointId = "testCamelRoute";
        String camelRouteUri = "direct:" + camelEndpointId;
        String testReponse = "testResponse";
        String deploymentId = "testDeployment";
        Mockito.when(producerTemplate.send(ArgumentMatchers.eq(camelRouteUri), ArgumentMatchers.any(Exchange.class))).thenReturn(outExchange);
        Mockito.when(producerTemplate.getCamelContext()).thenReturn(camelContext);
        Mockito.when(camelContext.createProducerTemplate()).thenReturn(producerTemplate);
        Mockito.when(outExchange.getOut()).thenReturn(outMessage);
        Mockito.when(outMessage.getBody()).thenReturn(testReponse);
        // Register the RuntimeManager bound camelcontext.
        try {
            ServiceRegistry.get().register((deploymentId + (JBPMConstants.DEPLOYMENT_CAMEL_CONTEXT_SERVICE_KEY_POSTFIX)), camelContext);
            WorkItemImpl workItem = new WorkItemImpl();
            workItem.setParameter(CAMEL_ENDPOINT_ID_WI_PARAM, camelEndpointId);
            workItem.setParameter("Request", "someRequest");
            Mockito.when(commandContext.getData("workItem")).thenReturn(workItem);
            Mockito.when(commandContext.getData("deploymentId")).thenReturn(deploymentId);
            Command command = new DeploymentContextCamelCommand();
            ExecutionResults results = command.execute(commandContext);
            Assert.assertNotNull(results);
            Assert.assertEquals(2, results.getData().size());
            Assert.assertEquals(testReponse, results.getData().get(RESPONSE_WI_PARAM));
        } finally {
            ServiceRegistry.get().remove((deploymentId + (JBPMConstants.DEPLOYMENT_CAMEL_CONTEXT_SERVICE_KEY_POSTFIX)));
        }
    }
}

