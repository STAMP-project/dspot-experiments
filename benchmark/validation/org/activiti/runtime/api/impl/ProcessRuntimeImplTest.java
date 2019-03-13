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
package org.activiti.runtime.api.impl;


import org.activiti.api.process.model.builders.ProcessPayloadBuilder;
import org.activiti.api.process.model.payloads.UpdateProcessPayload;
import org.activiti.api.runtime.model.impl.ProcessInstanceImpl;
import org.activiti.core.common.spring.security.policies.ProcessSecurityPoliciesManager;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.runtime.ProcessInstanceQuery;
import org.activiti.runtime.api.model.impl.APIProcessInstanceConverter;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;


public class ProcessRuntimeImplTest {
    private ProcessRuntimeImpl processRuntime;

    @Mock
    ProcessSecurityPoliciesManager securityPoliciesManager;

    @Mock
    private RepositoryService repositoryService;

    @Mock
    private RuntimeService runtimeService;

    @Mock
    private APIProcessInstanceConverter processInstanceConverter;

    @Test
    public void updateShouldBeAbleToUpdateNameBusinessKey() {
        // given
        UpdateProcessPayload updateProcessPayload = ProcessPayloadBuilder.update().withProcessInstanceId("processId").withBusinessKey("businessKey").withName("name").build();
        ProcessInstanceImpl process = new ProcessInstanceImpl();
        process.setId("processId");
        process.setProcessDefinitionKey("processDefinitionKey");
        Mockito.doReturn(process).when(processRuntime).processInstance("processId");
        ProcessInstanceQuery processQuery = Mockito.mock(ProcessInstanceQuery.class);
        Mockito.doReturn(processQuery).when(processQuery).processInstanceId("processId");
        Mockito.doReturn(processQuery).when(runtimeService).createProcessInstanceQuery();
        ProcessInstance internalProcess = Mockito.mock(org.activiti.api.process.model.ProcessInstance.class);
        Mockito.doReturn(internalProcess).when(processQuery).singleResult();
        // when
        org.activiti.api.process.model.ProcessInstance updatedProcess = processRuntime.update(updateProcessPayload);
        // then
        Mockito.verify(runtimeService).updateBusinessKey("processId", "businessKey");
        Mockito.verifyNoMoreInteractions(internalProcess);
    }
}

