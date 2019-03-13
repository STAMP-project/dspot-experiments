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
package org.activiti.spring;


import WebApplicationType.NONE;
import WebApplicationType.SERVLET;
import java.io.ByteArrayInputStream;
import java.util.Arrays;
import java.util.List;
import org.activiti.api.process.model.events.ProcessDeployedEvent;
import org.activiti.api.process.runtime.events.listener.ProcessRuntimeEventListener;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.repository.ProcessDefinitionQuery;
import org.activiti.runtime.api.model.impl.APIProcessDefinitionConverter;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.context.ApplicationEventPublisher;


public class ProcessDeployedEventProducerTest {
    private ProcessDeployedEventProducer producer;

    @Mock
    private RepositoryService repositoryService;

    @Mock
    private APIProcessDefinitionConverter converter;

    @Mock
    private ProcessRuntimeEventListener<ProcessDeployedEvent> firstListener;

    @Mock
    private ProcessRuntimeEventListener<ProcessDeployedEvent> secondListener;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @Test
    public void shouldCallRegisteredListenersWhenWebApplicationTypeIsServlet() {
        // given
        ProcessDefinitionQuery definitionQuery = Mockito.mock(ProcessDefinitionQuery.class);
        BDDMockito.given(repositoryService.createProcessDefinitionQuery()).willReturn(definitionQuery);
        List<ProcessDefinition> internalProcessDefinitions = Arrays.asList(Mockito.mock(ProcessDefinition.class), Mockito.mock(ProcessDefinition.class));
        BDDMockito.given(definitionQuery.list()).willReturn(internalProcessDefinitions);
        List<org.activiti.api.process.model.ProcessDefinition> apiProcessDefinitions = Arrays.asList(buildAPIProcessDefinition("id1"), buildAPIProcessDefinition("id2"));
        BDDMockito.given(converter.from(internalProcessDefinitions)).willReturn(apiProcessDefinitions);
        BDDMockito.given(repositoryService.getProcessModel("id1")).willReturn(new ByteArrayInputStream("content1".getBytes()));
        BDDMockito.given(repositoryService.getProcessModel("id2")).willReturn(new ByteArrayInputStream("content2".getBytes()));
        // when
        producer.onApplicationEvent(buildApplicationReadyEvent(SERVLET));
        // then
        ArgumentCaptor<ProcessDeployedEvent> captor = ArgumentCaptor.forClass(ProcessDeployedEvent.class);
        Mockito.verify(firstListener, Mockito.times(2)).onEvent(captor.capture());
        Mockito.verify(secondListener, Mockito.times(2)).onEvent(captor.capture());
        List<ProcessDeployedEvent> allValues = captor.getAllValues();
        // firstListener
        // firstListener
        // secondListener
        assertThat(allValues).extracting(ProcessDeployedEvent::getEntity, ProcessDeployedEvent::getProcessModelContent).containsExactly(tuple(apiProcessDefinitions.get(0), "content1"), tuple(apiProcessDefinitions.get(1), "content2"), tuple(apiProcessDefinitions.get(0), "content1"), tuple(apiProcessDefinitions.get(1), "content2"));// secondListener

    }

    @Test
    public void shouldNotCallRegisteredListenerWhenApplicationTypeIsNone() {
        // when
        producer.onApplicationEvent(buildApplicationReadyEvent(NONE));
        // then
        Mockito.verifyZeroInteractions(firstListener, secondListener);
    }
}

