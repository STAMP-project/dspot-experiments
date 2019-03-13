/**
 * Copyright 2017 Netflix, Inc.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.netflix.conductor.core.events;


import ApplicationException.Code;
import EventExecution.Status.FAILED;
import EventExecution.Status.IN_PROGRESS;
import Type.complete_task;
import Type.start_workflow;
import com.google.common.util.concurrent.Uninterruptibles;
import com.netflix.conductor.common.metadata.events.EventExecution;
import com.netflix.conductor.common.metadata.events.EventHandler;
import com.netflix.conductor.common.metadata.events.EventHandler.Action;
import com.netflix.conductor.common.metadata.events.EventHandler.StartWorkflow;
import com.netflix.conductor.common.metadata.events.EventHandler.TaskDetails;
import com.netflix.conductor.common.metadata.tasks.Task;
import com.netflix.conductor.common.metadata.workflow.WorkflowDef;
import com.netflix.conductor.common.run.Workflow;
import com.netflix.conductor.core.events.queue.ObservableQueue;
import com.netflix.conductor.core.execution.ParametersUtils;
import com.netflix.conductor.core.execution.TestConfiguration;
import com.netflix.conductor.core.execution.WorkflowExecutor;
import com.netflix.conductor.core.utils.JsonUtils;
import com.netflix.conductor.service.ExecutionService;
import com.netflix.conductor.service.MetadataService;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;


/**
 *
 *
 * @author Viren
 */
public class TestEventProcessor {
    private String event;

    private String queueURI;

    private ObservableQueue queue;

    private MetadataService metadataService;

    private ExecutionService executionService;

    private WorkflowExecutor workflowExecutor;

    private ActionProcessor actionProcessor;

    private EventQueues eventQueues;

    private ParametersUtils parametersUtils;

    private JsonUtils jsonUtils;

    @Test
    public void testEventProcessor() {
        // setup event handler
        EventHandler eventHandler = new EventHandler();
        eventHandler.setName(UUID.randomUUID().toString());
        eventHandler.setActive(true);
        Map<String, String> taskToDomain = new HashMap<>();
        taskToDomain.put("*", "dev");
        Action startWorkflowAction = new Action();
        startWorkflowAction.setAction(start_workflow);
        startWorkflowAction.setStart_workflow(new StartWorkflow());
        startWorkflowAction.getStart_workflow().setName("workflow_x");
        startWorkflowAction.getStart_workflow().setVersion(1);
        startWorkflowAction.getStart_workflow().setTaskToDomain(taskToDomain);
        eventHandler.getActions().add(startWorkflowAction);
        Action completeTaskAction = new Action();
        completeTaskAction.setAction(complete_task);
        completeTaskAction.setComplete_task(new TaskDetails());
        completeTaskAction.getComplete_task().setTaskRefName("task_x");
        completeTaskAction.getComplete_task().setWorkflowId(UUID.randomUUID().toString());
        completeTaskAction.getComplete_task().setOutput(new HashMap());
        eventHandler.getActions().add(completeTaskAction);
        eventHandler.setEvent(event);
        Mockito.when(metadataService.getEventHandlers()).thenReturn(Collections.singletonList(eventHandler));
        Mockito.when(metadataService.getEventHandlersForEvent(event, true)).thenReturn(Collections.singletonList(eventHandler));
        Mockito.when(executionService.addEventExecution(ArgumentMatchers.any())).thenReturn(true);
        Mockito.when(queue.rePublishIfNoAck()).thenReturn(false);
        String id = UUID.randomUUID().toString();
        AtomicBoolean started = new AtomicBoolean(false);
        Mockito.doAnswer(((Answer<String>) (( invocation) -> {
            started.set(true);
            return id;
        }))).when(workflowExecutor).startWorkflow(startWorkflowAction.getStart_workflow().getName(), startWorkflowAction.getStart_workflow().getVersion(), startWorkflowAction.getStart_workflow().getCorrelationId(), startWorkflowAction.getStart_workflow().getInput(), null, event, taskToDomain);
        AtomicBoolean completed = new AtomicBoolean(false);
        Mockito.doAnswer(((Answer<String>) (( invocation) -> {
            completed.set(true);
            return null;
        }))).when(workflowExecutor).updateTask(ArgumentMatchers.any());
        Task task = new Task();
        task.setReferenceTaskName(completeTaskAction.getComplete_task().getTaskRefName());
        Workflow workflow = new Workflow();
        workflow.setTasks(Collections.singletonList(task));
        Mockito.when(workflowExecutor.getWorkflow(completeTaskAction.getComplete_task().getWorkflowId(), true)).thenReturn(workflow);
        WorkflowDef workflowDef = new WorkflowDef();
        workflowDef.setVersion(startWorkflowAction.getStart_workflow().getVersion());
        workflowDef.setName(startWorkflowAction.getStart_workflow().getName());
        Mockito.when(metadataService.getWorkflowDef(ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn(workflowDef);
        ActionProcessor actionProcessor = new ActionProcessor(workflowExecutor, parametersUtils, jsonUtils);
        EventProcessor eventProcessor = new EventProcessor(executionService, metadataService, actionProcessor, eventQueues, jsonUtils, new TestConfiguration());
        Assert.assertNotNull(eventProcessor.getQueues());
        Assert.assertEquals(1, eventProcessor.getQueues().size());
        String queueEvent = eventProcessor.getQueues().keySet().iterator().next();
        Assert.assertEquals(eventHandler.getEvent(), queueEvent);
        String eventProcessorQueue = eventProcessor.getQueues().values().iterator().next();
        Assert.assertEquals(queueURI, eventProcessorQueue);
        Uninterruptibles.sleepUninterruptibly(1, TimeUnit.SECONDS);
        Assert.assertTrue(started.get());
        Assert.assertTrue(completed.get());
        Mockito.verify(queue, Mockito.atMost(1)).ack(ArgumentMatchers.any());
        Mockito.verify(queue, Mockito.never()).publish(ArgumentMatchers.any());
    }

    @Test
    public void testEventHandlerWithCondition() {
        EventHandler eventHandler = new EventHandler();
        eventHandler.setName("cms_intermediate_video_ingest_handler");
        eventHandler.setActive(true);
        eventHandler.setEvent("sqs:dev_cms_asset_ingest_queue");
        eventHandler.setCondition("$.Message.testKey1 == 'level1' && $.Message.metadata.testKey2 == 123456");
        Map<String, Object> startWorkflowInput = new LinkedHashMap<>();
        startWorkflowInput.put("param1", "${Message.metadata.testKey2}");
        startWorkflowInput.put("param2", "SQS-${MessageId}");
        Action startWorkflowAction = new Action();
        startWorkflowAction.setAction(start_workflow);
        startWorkflowAction.setStart_workflow(new StartWorkflow());
        startWorkflowAction.getStart_workflow().setName("cms_artwork_automation");
        startWorkflowAction.getStart_workflow().setVersion(1);
        startWorkflowAction.getStart_workflow().setInput(startWorkflowInput);
        startWorkflowAction.setExpandInlineJSON(true);
        eventHandler.getActions().add(startWorkflowAction);
        eventHandler.setEvent(event);
        Mockito.when(metadataService.getEventHandlers()).thenReturn(Collections.singletonList(eventHandler));
        Mockito.when(metadataService.getEventHandlersForEvent(event, true)).thenReturn(Collections.singletonList(eventHandler));
        Mockito.when(executionService.addEventExecution(ArgumentMatchers.any())).thenReturn(true);
        Mockito.when(queue.rePublishIfNoAck()).thenReturn(false);
        String id = UUID.randomUUID().toString();
        AtomicBoolean started = new AtomicBoolean(false);
        Mockito.doAnswer(((Answer<String>) (( invocation) -> {
            started.set(true);
            return id;
        }))).when(workflowExecutor).startWorkflow(startWorkflowAction.getStart_workflow().getName(), startWorkflowAction.getStart_workflow().getVersion(), startWorkflowAction.getStart_workflow().getCorrelationId(), startWorkflowAction.getStart_workflow().getInput(), null, event, null);
        WorkflowDef workflowDef = new WorkflowDef();
        workflowDef.setName(startWorkflowAction.getStart_workflow().getName());
        Mockito.when(metadataService.getWorkflowDef(ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn(workflowDef);
        ActionProcessor actionProcessor = new ActionProcessor(workflowExecutor, parametersUtils, jsonUtils);
        EventProcessor eventProcessor = new EventProcessor(executionService, metadataService, actionProcessor, eventQueues, jsonUtils, new TestConfiguration());
        Assert.assertNotNull(eventProcessor.getQueues());
        Assert.assertEquals(1, eventProcessor.getQueues().size());
        Uninterruptibles.sleepUninterruptibly(1, TimeUnit.SECONDS);
        Assert.assertTrue(started.get());
    }

    @Test
    public void testEventProcessorWithRetriableError() {
        EventHandler eventHandler = new EventHandler();
        eventHandler.setName(UUID.randomUUID().toString());
        eventHandler.setActive(true);
        eventHandler.setEvent(event);
        Action completeTaskAction = new Action();
        completeTaskAction.setAction(complete_task);
        completeTaskAction.setComplete_task(new TaskDetails());
        completeTaskAction.getComplete_task().setTaskRefName("task_x");
        completeTaskAction.getComplete_task().setWorkflowId(UUID.randomUUID().toString());
        completeTaskAction.getComplete_task().setOutput(new HashMap());
        eventHandler.getActions().add(completeTaskAction);
        Mockito.when(queue.rePublishIfNoAck()).thenReturn(false);
        Mockito.when(metadataService.getEventHandlers()).thenReturn(Collections.singletonList(eventHandler));
        Mockito.when(metadataService.getEventHandlersForEvent(event, true)).thenReturn(Collections.singletonList(eventHandler));
        Mockito.when(executionService.addEventExecution(ArgumentMatchers.any())).thenReturn(true);
        Mockito.when(actionProcessor.execute(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any())).thenThrow(new com.netflix.conductor.core.execution.ApplicationException(Code.BACKEND_ERROR, "some retriable error"));
        EventProcessor eventProcessor = new EventProcessor(executionService, metadataService, actionProcessor, eventQueues, jsonUtils, new TestConfiguration());
        Assert.assertNotNull(eventProcessor.getQueues());
        Assert.assertEquals(1, eventProcessor.getQueues().size());
        Uninterruptibles.sleepUninterruptibly(1, TimeUnit.SECONDS);
        Mockito.verify(queue, Mockito.never()).ack(ArgumentMatchers.any());
        Mockito.verify(queue, Mockito.never()).publish(ArgumentMatchers.any());
    }

    @Test
    public void testEventProcessorWithNonRetriableError() {
        EventHandler eventHandler = new EventHandler();
        eventHandler.setName(UUID.randomUUID().toString());
        eventHandler.setActive(true);
        eventHandler.setEvent(event);
        Action completeTaskAction = new Action();
        completeTaskAction.setAction(complete_task);
        completeTaskAction.setComplete_task(new TaskDetails());
        completeTaskAction.getComplete_task().setTaskRefName("task_x");
        completeTaskAction.getComplete_task().setWorkflowId(UUID.randomUUID().toString());
        completeTaskAction.getComplete_task().setOutput(new HashMap());
        eventHandler.getActions().add(completeTaskAction);
        Mockito.when(metadataService.getEventHandlers()).thenReturn(Collections.singletonList(eventHandler));
        Mockito.when(metadataService.getEventHandlersForEvent(event, true)).thenReturn(Collections.singletonList(eventHandler));
        Mockito.when(executionService.addEventExecution(ArgumentMatchers.any())).thenReturn(true);
        Mockito.when(actionProcessor.execute(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any())).thenThrow(new com.netflix.conductor.core.execution.ApplicationException(Code.INVALID_INPUT, "some non-retriable error"));
        EventProcessor eventProcessor = new EventProcessor(executionService, metadataService, actionProcessor, eventQueues, jsonUtils, new TestConfiguration());
        Assert.assertNotNull(eventProcessor.getQueues());
        Assert.assertEquals(1, eventProcessor.getQueues().size());
        Uninterruptibles.sleepUninterruptibly(1, TimeUnit.SECONDS);
        Mockito.verify(queue, Mockito.atMost(1)).ack(ArgumentMatchers.any());
        Mockito.verify(queue, Mockito.never()).publish(ArgumentMatchers.any());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testExecuteInvalidAction() {
        AtomicInteger executeInvoked = new AtomicInteger(0);
        Mockito.doAnswer(((Answer<Map<String, Object>>) (( invocation) -> {
            executeInvoked.incrementAndGet();
            throw new UnsupportedOperationException("error");
        }))).when(actionProcessor).execute(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any());
        EventProcessor eventProcessor = new EventProcessor(executionService, metadataService, actionProcessor, eventQueues, jsonUtils, new TestConfiguration());
        EventExecution eventExecution = new EventExecution("id", "messageId");
        eventExecution.setStatus(IN_PROGRESS);
        eventExecution.setEvent("event");
        Action action = new Action();
        eventProcessor.execute(eventExecution, action, "payload");
        Assert.assertEquals(1, executeInvoked.get());
        Assert.assertEquals(FAILED, eventExecution.getStatus());
        Assert.assertNotNull(eventExecution.getOutput().get("exception"));
    }

    @Test
    public void testExecuteNonRetriableApplicationException() {
        AtomicInteger executeInvoked = new AtomicInteger(0);
        Mockito.doAnswer(((Answer<Map<String, Object>>) (( invocation) -> {
            executeInvoked.incrementAndGet();
            throw new com.netflix.conductor.core.execution.ApplicationException(Code.INVALID_INPUT, "some non-retriable error");
        }))).when(actionProcessor).execute(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any());
        EventProcessor eventProcessor = new EventProcessor(executionService, metadataService, actionProcessor, eventQueues, jsonUtils, new TestConfiguration());
        EventExecution eventExecution = new EventExecution("id", "messageId");
        eventExecution.setStatus(IN_PROGRESS);
        eventExecution.setEvent("event");
        Action action = new Action();
        action.setAction(start_workflow);
        eventProcessor.execute(eventExecution, action, "payload");
        Assert.assertEquals(1, executeInvoked.get());
        Assert.assertEquals(FAILED, eventExecution.getStatus());
        Assert.assertNotNull(eventExecution.getOutput().get("exception"));
    }

    @Test
    public void testExecuteRetriableApplicationException() {
        AtomicInteger executeInvoked = new AtomicInteger(0);
        Mockito.doAnswer(((Answer<Map<String, Object>>) (( invocation) -> {
            executeInvoked.incrementAndGet();
            throw new com.netflix.conductor.core.execution.ApplicationException(Code.BACKEND_ERROR, "some retriable error");
        }))).when(actionProcessor).execute(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any());
        EventProcessor eventProcessor = new EventProcessor(executionService, metadataService, actionProcessor, eventQueues, jsonUtils, new TestConfiguration());
        EventExecution eventExecution = new EventExecution("id", "messageId");
        eventExecution.setStatus(IN_PROGRESS);
        eventExecution.setEvent("event");
        Action action = new Action();
        action.setAction(start_workflow);
        eventProcessor.execute(eventExecution, action, "payload");
        Assert.assertEquals(3, executeInvoked.get());
        Assert.assertNull(eventExecution.getOutput().get("exception"));
    }
}

