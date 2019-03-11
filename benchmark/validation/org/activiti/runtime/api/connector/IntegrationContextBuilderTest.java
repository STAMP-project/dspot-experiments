/**
 * Copyright 2018 Alfresco, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.activiti.runtime.api.connector;


import java.util.Collections;
import java.util.Map;
import org.activiti.api.process.model.IntegrationContext;
import org.activiti.bpmn.model.ServiceTask;
import org.activiti.core.common.model.connector.ActionDefinition;
import org.activiti.engine.impl.persistence.entity.ExecutionEntity;
import org.activiti.engine.impl.persistence.entity.integration.IntegrationContextEntityImpl;
import org.junit.Test;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;


public class IntegrationContextBuilderTest {
    private static final int PROCESS_DEFINITION_VERSION = 1;

    private static final String PARENT_PROCESS_INSTANCE_ID = "parentProcessInstanceId";

    private static final String PROCESS_DEFINITION_KEY = "processDefinitionKey";

    private static final String PROCESS_INSTANCE_BUSINESS_KEY = "processInstanceBusinessKey";

    private static final String CURRENT_ACTIVITY_ID = "currentActivityId";

    private static final String PROCESS_DEFINITION_ID = "processDefinitionId";

    private static final String PROCESS_INSTANCE_ID = "processInstanceId";

    private static final String IMPLEMENTATION = "implementation";

    private static final String SERVICE_TASK_NAME = "serviceTaskName";

    @InjectMocks
    private IntegrationContextBuilder builder;

    @Mock
    private InboundVariablesProvider inboundVariablesProvider;

    @Test
    public void shouldBuildIntegrationContextFromExecution() {
        // given
        ExecutionEntity execution = Mockito.mock(ExecutionEntity.class);
        ExecutionEntity processInstance = Mockito.mock(ExecutionEntity.class);
        ServiceTask serviceTask = Mockito.mock(ServiceTask.class);
        Map<String, Object> variables = Collections.singletonMap("key", "value");
        ActionDefinition actionDefinition = new ActionDefinition();
        BDDMockito.given(inboundVariablesProvider.calculateVariables(execution, actionDefinition)).willReturn(variables);
        BDDMockito.given(serviceTask.getImplementation()).willReturn(IntegrationContextBuilderTest.IMPLEMENTATION);
        BDDMockito.given(serviceTask.getName()).willReturn(IntegrationContextBuilderTest.SERVICE_TASK_NAME);
        BDDMockito.given(execution.getVariables()).willReturn(variables);
        BDDMockito.given(execution.getCurrentActivityId()).willReturn(IntegrationContextBuilderTest.CURRENT_ACTIVITY_ID);
        BDDMockito.given(execution.getCurrentFlowElement()).willReturn(serviceTask);
        BDDMockito.given(execution.getProcessInstanceId()).willReturn(IntegrationContextBuilderTest.PROCESS_INSTANCE_ID);
        BDDMockito.given(execution.getProcessDefinitionId()).willReturn(IntegrationContextBuilderTest.PROCESS_DEFINITION_ID);
        BDDMockito.given(execution.getCurrentActivityId()).willReturn(IntegrationContextBuilderTest.CURRENT_ACTIVITY_ID);
        BDDMockito.given(execution.getProcessInstanceBusinessKey()).willReturn(IntegrationContextBuilderTest.PROCESS_INSTANCE_BUSINESS_KEY);
        BDDMockito.given(execution.getProcessInstance()).willReturn(processInstance);
        BDDMockito.given(processInstance.getProcessDefinitionKey()).willReturn(IntegrationContextBuilderTest.PROCESS_DEFINITION_KEY);
        BDDMockito.given(processInstance.getProcessDefinitionVersion()).willReturn(IntegrationContextBuilderTest.PROCESS_DEFINITION_VERSION);
        BDDMockito.given(processInstance.getParentProcessInstanceId()).willReturn(IntegrationContextBuilderTest.PARENT_PROCESS_INSTANCE_ID);
        // when
        IntegrationContext integrationContext = builder.from(execution, actionDefinition);
        // then
        assertThat(integrationContext).isNotNull();
        assertThat(integrationContext.getConnectorType()).isEqualTo(IntegrationContextBuilderTest.IMPLEMENTATION);
        assertThat(integrationContext.getClientId()).isEqualTo(IntegrationContextBuilderTest.CURRENT_ACTIVITY_ID);
        assertThat(integrationContext.getClientName()).isEqualTo(IntegrationContextBuilderTest.SERVICE_TASK_NAME);
        assertThat(integrationContext.getClientType()).isEqualTo(ServiceTask.class.getSimpleName());
        assertThat(integrationContext.getBusinessKey()).isEqualTo(IntegrationContextBuilderTest.PROCESS_INSTANCE_BUSINESS_KEY);
        assertThat(integrationContext.getProcessDefinitionId()).isEqualTo(IntegrationContextBuilderTest.PROCESS_DEFINITION_ID);
        assertThat(integrationContext.getProcessInstanceId()).isEqualTo(IntegrationContextBuilderTest.PROCESS_INSTANCE_ID);
        assertThat(integrationContext.getProcessDefinitionKey()).isEqualTo(IntegrationContextBuilderTest.PROCESS_DEFINITION_KEY);
        assertThat(integrationContext.getProcessDefinitionVersion()).isEqualTo(IntegrationContextBuilderTest.PROCESS_DEFINITION_VERSION);
        assertThat(integrationContext.getParentProcessInstanceId()).isEqualTo(IntegrationContextBuilderTest.PARENT_PROCESS_INSTANCE_ID);
        assertThat(integrationContext.getInBoundVariables()).containsAllEntriesOf(variables);
    }

    @Test
    public void shouldSetIdWhenIntegrationContextEntityIsProvided() {
        // given
        ExecutionEntity execution = Mockito.mock(ExecutionEntity.class);
        ExecutionEntity processInstance = Mockito.mock(ExecutionEntity.class);
        ServiceTask serviceTask = Mockito.mock(ServiceTask.class);
        Map<String, Object> variables = Collections.singletonMap("key", "value");
        ActionDefinition actionDefinition = new ActionDefinition();
        BDDMockito.given(inboundVariablesProvider.calculateVariables(execution, actionDefinition)).willReturn(variables);
        BDDMockito.given(serviceTask.getImplementation()).willReturn(IntegrationContextBuilderTest.IMPLEMENTATION);
        BDDMockito.given(serviceTask.getName()).willReturn(IntegrationContextBuilderTest.SERVICE_TASK_NAME);
        BDDMockito.given(execution.getVariables()).willReturn(variables);
        BDDMockito.given(execution.getCurrentActivityId()).willReturn(IntegrationContextBuilderTest.CURRENT_ACTIVITY_ID);
        BDDMockito.given(execution.getCurrentFlowElement()).willReturn(serviceTask);
        BDDMockito.given(execution.getProcessInstanceId()).willReturn(IntegrationContextBuilderTest.PROCESS_INSTANCE_ID);
        BDDMockito.given(execution.getProcessDefinitionId()).willReturn(IntegrationContextBuilderTest.PROCESS_DEFINITION_ID);
        BDDMockito.given(execution.getCurrentActivityId()).willReturn(IntegrationContextBuilderTest.CURRENT_ACTIVITY_ID);
        BDDMockito.given(execution.getProcessInstanceBusinessKey()).willReturn(IntegrationContextBuilderTest.PROCESS_INSTANCE_BUSINESS_KEY);
        BDDMockito.given(execution.getProcessInstance()).willReturn(processInstance);
        BDDMockito.given(processInstance.getProcessDefinitionKey()).willReturn(IntegrationContextBuilderTest.PROCESS_DEFINITION_KEY);
        BDDMockito.given(processInstance.getProcessDefinitionVersion()).willReturn(IntegrationContextBuilderTest.PROCESS_DEFINITION_VERSION);
        BDDMockito.given(processInstance.getParentProcessInstanceId()).willReturn(IntegrationContextBuilderTest.PARENT_PROCESS_INSTANCE_ID);
        IntegrationContextEntityImpl integrationContextEntity = new IntegrationContextEntityImpl();
        integrationContextEntity.setId("entityId");
        // when
        IntegrationContext integrationContext = builder.from(integrationContextEntity, execution, actionDefinition);
        // then
        assertThat(integrationContext).isNotNull();
        assertThat(integrationContext.getId()).isEqualTo("entityId");
        assertThat(integrationContext.getConnectorType()).isEqualTo(IntegrationContextBuilderTest.IMPLEMENTATION);
        assertThat(integrationContext.getClientId()).isEqualTo(IntegrationContextBuilderTest.CURRENT_ACTIVITY_ID);
        assertThat(integrationContext.getClientName()).isEqualTo(IntegrationContextBuilderTest.SERVICE_TASK_NAME);
        assertThat(integrationContext.getClientType()).isEqualTo(ServiceTask.class.getSimpleName());
        assertThat(integrationContext.getBusinessKey()).isEqualTo(IntegrationContextBuilderTest.PROCESS_INSTANCE_BUSINESS_KEY);
        assertThat(integrationContext.getProcessDefinitionId()).isEqualTo(IntegrationContextBuilderTest.PROCESS_DEFINITION_ID);
        assertThat(integrationContext.getProcessInstanceId()).isEqualTo(IntegrationContextBuilderTest.PROCESS_INSTANCE_ID);
        assertThat(integrationContext.getProcessDefinitionKey()).isEqualTo(IntegrationContextBuilderTest.PROCESS_DEFINITION_KEY);
        assertThat(integrationContext.getProcessDefinitionVersion()).isEqualTo(IntegrationContextBuilderTest.PROCESS_DEFINITION_VERSION);
        assertThat(integrationContext.getParentProcessInstanceId()).isEqualTo(IntegrationContextBuilderTest.PARENT_PROCESS_INSTANCE_ID);
        assertThat(integrationContext.getInBoundVariables()).containsAllEntriesOf(variables);
    }
}

