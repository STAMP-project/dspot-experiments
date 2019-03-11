/**
 * Copyright 2017 Netflix, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
/**
 *
 */
package com.netflix.conductor.core.execution.tasks;


import Status.IN_PROGRESS;
import Status.SCHEDULED;
import Task.Status.COMPLETED;
import Task.Status.FAILED;
import TaskType.EVENT;
import com.netflix.conductor.common.metadata.tasks.Task;
import com.netflix.conductor.common.metadata.workflow.WorkflowDef;
import com.netflix.conductor.common.run.Workflow;
import com.netflix.conductor.core.events.EventQueueProvider;
import com.netflix.conductor.core.events.EventQueues;
import com.netflix.conductor.core.events.queue.Message;
import com.netflix.conductor.core.events.queue.ObservableQueue;
import com.netflix.conductor.core.execution.ParametersUtils;
import com.netflix.conductor.core.execution.TestConfiguration;
import com.netflix.conductor.dao.QueueDAO;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
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
public class TestEvent {
    WorkflowDef testWorkflowDefinition;

    private EventQueues eventQueues;

    private ParametersUtils parametersUtils;

    @Test
    public void testEvent() {
        System.setProperty("QUEUE_NAME", "queue_name_001");
        String eventt = "queue_${QUEUE_NAME}";
        String event = parametersUtils.replace(eventt).toString();
        Assert.assertNotNull(event);
        Assert.assertEquals("queue_queue_name_001", event);
        eventt = "queue_9";
        event = parametersUtils.replace(eventt).toString();
        Assert.assertNotNull(event);
        Assert.assertEquals(eventt, event);
    }

    @Test
    public void testSinkParam() {
        String sink = "sqs:queue_name";
        WorkflowDef def = new WorkflowDef();
        def.setName("wf0");
        Workflow workflow = new Workflow();
        workflow.setWorkflowDefinition(def);
        Task task1 = new Task();
        task1.setReferenceTaskName("t1");
        task1.getOutputData().put("q", "t1_queue");
        workflow.getTasks().add(task1);
        Task task2 = new Task();
        task2.setReferenceTaskName("t2");
        task2.getOutputData().put("q", "task2_queue");
        workflow.getTasks().add(task2);
        Task task = new Task();
        task.setReferenceTaskName("event");
        task.getInputData().put("sink", sink);
        task.setTaskType(EVENT.name());
        workflow.getTasks().add(task);
        Event event = new Event(eventQueues, parametersUtils);
        ObservableQueue queue = event.getQueue(workflow, task);
        Assert.assertNotNull(task.getReasonForIncompletion(), queue);
        Assert.assertEquals("queue_name", queue.getName());
        Assert.assertEquals("sqs", queue.getType());
        sink = "sqs:${t1.output.q}";
        task.getInputData().put("sink", sink);
        queue = event.getQueue(workflow, task);
        Assert.assertNotNull(queue);
        Assert.assertEquals("t1_queue", queue.getName());
        Assert.assertEquals("sqs", queue.getType());
        System.out.println(task.getOutputData().get("event_produced"));
        sink = "sqs:${t2.output.q}";
        task.getInputData().put("sink", sink);
        queue = event.getQueue(workflow, task);
        Assert.assertNotNull(queue);
        Assert.assertEquals("task2_queue", queue.getName());
        Assert.assertEquals("sqs", queue.getType());
        System.out.println(task.getOutputData().get("event_produced"));
        sink = "conductor";
        task.getInputData().put("sink", sink);
        queue = event.getQueue(workflow, task);
        Assert.assertNotNull(queue);
        Assert.assertEquals((((workflow.getWorkflowName()) + ":") + (task.getReferenceTaskName())), queue.getName());
        Assert.assertEquals("conductor", queue.getType());
        System.out.println(task.getOutputData().get("event_produced"));
        sink = "sqs:static_value";
        task.getInputData().put("sink", sink);
        queue = event.getQueue(workflow, task);
        Assert.assertNotNull(queue);
        Assert.assertEquals("static_value", queue.getName());
        Assert.assertEquals("sqs", queue.getType());
        Assert.assertEquals(sink, task.getOutputData().get("event_produced"));
        System.out.println(task.getOutputData().get("event_produced"));
        sink = "bad:queue";
        task.getInputData().put("sink", sink);
        queue = event.getQueue(workflow, task);
        Assert.assertNull(queue);
        Assert.assertEquals(FAILED, task.getStatus());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void test() throws Exception {
        Workflow workflow = new Workflow();
        workflow.setWorkflowDefinition(testWorkflowDefinition);
        Task task = new Task();
        task.getInputData().put("sink", "conductor");
        task.setReferenceTaskName("task0");
        task.setTaskId("task_id_0");
        QueueDAO dao = Mockito.mock(QueueDAO.class);
        String[] publishedQueue = new String[1];
        List<Message> publishedMessages = new LinkedList<>();
        Mockito.doAnswer(((Answer<Void>) (( invocation) -> {
            String queueName = getArgumentAt(0, String.class);
            System.out.println(queueName);
            publishedQueue[0] = queueName;
            List<Message> messages = invocation.getArgumentAt(1, List.class);
            publishedMessages.addAll(messages);
            return null;
        }))).when(dao).push(ArgumentMatchers.any(), ArgumentMatchers.any());
        Mockito.doAnswer(((Answer<List<String>>) (( invocation) -> {
            String messageId = getArgumentAt(1, String.class);
            if (publishedMessages.get(0).getId().equals(messageId)) {
                publishedMessages.remove(0);
                return Collections.singletonList(messageId);
            }
            return null;
        }))).when(dao).remove(ArgumentMatchers.any(), ArgumentMatchers.any());
        Map<String, EventQueueProvider> providers = new HashMap<>();
        providers.put("conductor", new com.netflix.conductor.core.events.queue.dyno.DynoEventQueueProvider(dao, new TestConfiguration()));
        eventQueues = new EventQueues(providers, parametersUtils);
        Event event = new Event(eventQueues, parametersUtils);
        event.start(workflow, task, null);
        Assert.assertEquals(COMPLETED, task.getStatus());
        Assert.assertNotNull(task.getOutputData());
        Assert.assertEquals(((("conductor:" + (workflow.getWorkflowName())) + ":") + (task.getReferenceTaskName())), task.getOutputData().get("event_produced"));
        Assert.assertEquals(task.getOutputData().get("event_produced"), ("conductor:" + (publishedQueue[0])));
        Assert.assertEquals(1, publishedMessages.size());
        Assert.assertEquals(task.getTaskId(), publishedMessages.get(0).getId());
        Assert.assertNotNull(publishedMessages.get(0).getPayload());
        event.cancel(workflow, task, null);
        Assert.assertTrue(publishedMessages.isEmpty());
    }

    @Test
    public void testFailures() {
        Event event = new Event(eventQueues, parametersUtils);
        Workflow workflow = new Workflow();
        workflow.setWorkflowDefinition(testWorkflowDefinition);
        Task task = new Task();
        task.setReferenceTaskName("task0");
        task.setTaskId("task_id_0");
        event.start(workflow, task, null);
        Assert.assertEquals(FAILED, task.getStatus());
        Assert.assertTrue(((task.getReasonForIncompletion()) != null));
        System.out.println(task.getReasonForIncompletion());
        task.getInputData().put("sink", "bad_sink");
        task.setStatus(SCHEDULED);
        event.start(workflow, task, null);
        Assert.assertEquals(FAILED, task.getStatus());
        Assert.assertTrue(((task.getReasonForIncompletion()) != null));
        System.out.println(task.getReasonForIncompletion());
        task.setStatus(SCHEDULED);
        task.setScheduledTime(System.currentTimeMillis());
        event.execute(workflow, task, null);
        Assert.assertEquals(Task.Status.SCHEDULED, task.getStatus());
        task.setScheduledTime(((System.currentTimeMillis()) - 610000));
        event.start(workflow, task, null);
        Assert.assertEquals(FAILED, task.getStatus());
    }

    @Test
    public void testDynamicSinks() {
        Event event = new Event(eventQueues, parametersUtils);
        Workflow workflow = new Workflow();
        workflow.setWorkflowDefinition(testWorkflowDefinition);
        Task task = new Task();
        task.setReferenceTaskName("task0");
        task.setTaskId("task_id_0");
        task.setStatus(IN_PROGRESS);
        task.getInputData().put("sink", "conductor:some_arbitary_queue");
        ObservableQueue queue = event.getQueue(workflow, task);
        Assert.assertEquals(Task.Status.IN_PROGRESS, task.getStatus());
        Assert.assertNotNull(queue);
        Assert.assertEquals("testWorkflow:some_arbitary_queue", queue.getName());
        Assert.assertEquals("testWorkflow:some_arbitary_queue", queue.getURI());
        Assert.assertEquals("conductor", queue.getType());
        Assert.assertEquals("conductor:testWorkflow:some_arbitary_queue", task.getOutputData().get("event_produced"));
        task.getInputData().put("sink", "conductor");
        queue = event.getQueue(workflow, task);
        Assert.assertEquals(("not in progress: " + (task.getReasonForIncompletion())), Task.Status.IN_PROGRESS, task.getStatus());
        Assert.assertNotNull(queue);
        Assert.assertEquals("testWorkflow:task0", queue.getName());
        task.getInputData().put("sink", "sqs:my_sqs_queue_name");
        queue = event.getQueue(workflow, task);
        Assert.assertEquals(("not in progress: " + (task.getReasonForIncompletion())), Task.Status.IN_PROGRESS, task.getStatus());
        Assert.assertNotNull(queue);
        Assert.assertEquals("my_sqs_queue_name", queue.getName());
        Assert.assertEquals("sqs", queue.getType());
        task.getInputData().put("sink", "sns:my_sqs_queue_name");
        queue = event.getQueue(workflow, task);
        Assert.assertEquals(FAILED, task.getStatus());
    }
}

