/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.flowable.engine.test.api.event;


import FlowableEngineEventType.ACTIVITY_CANCELLED;
import FlowableEngineEventType.ACTIVITY_COMPLETED;
import FlowableEngineEventType.ACTIVITY_STARTED;
import FlowableEngineEventType.PROCESS_CANCELLED;
import FlowableEngineEventType.PROCESS_COMPLETED;
import FlowableEngineEventType.PROCESS_COMPLETED_WITH_TERMINATE_END_EVENT;
import FlowableEngineEventType.PROCESS_STARTED;
import FlowableEngineEventType.TASK_COMPLETED;
import FlowableEngineEventType.TASK_CREATED;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.flowable.common.engine.api.delegate.event.FlowableEngineEntityEvent;
import org.flowable.common.engine.api.delegate.event.FlowableEntityEvent;
import org.flowable.common.engine.api.delegate.event.FlowableEvent;
import org.flowable.engine.delegate.event.AbstractFlowableEngineEventListener;
import org.flowable.engine.delegate.event.FlowableActivityCancelledEvent;
import org.flowable.engine.delegate.event.FlowableActivityEvent;
import org.flowable.engine.delegate.event.FlowableCancelledEvent;
import org.flowable.engine.delegate.event.FlowableProcessStartedEvent;
import org.flowable.engine.impl.persistence.entity.ExecutionEntity;
import org.flowable.engine.impl.test.PluggableFlowableTestCase;
import org.flowable.engine.runtime.Execution;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.engine.test.Deployment;
import org.flowable.task.api.Task;
import org.flowable.task.service.impl.persistence.entity.TaskEntity;
import org.junit.jupiter.api.Test;


public class CancelUserTaskTest extends PluggableFlowableTestCase {
    private CancelUserTaskTest.UserActivityEventListener testListener;

    /**
     * User task cancelled by terminate end event.
     */
    @Test
    @Deployment(resources = { "org/flowable/engine/test/api/event/CancelUserTaskEventsTest.bpmn20.xml" })
    public void testUserTaskCancelledWhenFlowToTerminateEnd() throws Exception {
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("cancelUserTaskEvents");
        assertNotNull(processInstance);
        Execution task1Execution = runtimeService.createExecutionQuery().activityId("task1").singleResult();
        Execution task2Execution = runtimeService.createExecutionQuery().activityId("task2").singleResult();
        Execution boundaryExecution = runtimeService.createExecutionQuery().activityId("cancelBoundaryEvent1").singleResult();
        int idx = 0;
        FlowableEvent flowableEvent = testListener.getEventsReceived().get((idx++));
        assertEquals(PROCESS_STARTED, flowableEvent.getType());
        ExecutionEntity executionEntity = ((ExecutionEntity) (getEntity()));
        assertEquals(processInstance.getId(), executionEntity.getProcessInstanceId());
        FlowableActivityEvent activityEvent = ((FlowableActivityEvent) (testListener.getEventsReceived().get((idx++))));
        assertEquals(ACTIVITY_STARTED, activityEvent.getType());
        assertEquals("startEvent", activityEvent.getActivityType());
        activityEvent = ((FlowableActivityEvent) (testListener.getEventsReceived().get((idx++))));
        assertEquals(ACTIVITY_COMPLETED, activityEvent.getType());
        assertEquals("startEvent", activityEvent.getActivityType());
        activityEvent = ((FlowableActivityEvent) (testListener.getEventsReceived().get((idx++))));
        assertEquals(ACTIVITY_STARTED, activityEvent.getType());
        assertEquals("task1", activityEvent.getActivityId());
        FlowableEntityEvent entityEvent = ((FlowableEntityEvent) (testListener.getEventsReceived().get((idx++))));
        assertEquals(TASK_CREATED, entityEvent.getType());
        TaskEntity taskEntity = ((TaskEntity) (entityEvent.getEntity()));
        assertEquals("User Task1", taskEntity.getName());
        activityEvent = ((FlowableActivityEvent) (testListener.getEventsReceived().get((idx++))));
        assertEquals(ACTIVITY_STARTED, activityEvent.getType());
        assertEquals("task2", activityEvent.getActivityId());
        entityEvent = ((FlowableEntityEvent) (testListener.getEventsReceived().get((idx++))));
        assertEquals(TASK_CREATED, entityEvent.getType());
        taskEntity = ((TaskEntity) (entityEvent.getEntity()));
        assertEquals("User Task2", taskEntity.getName());
        List<Task> tasks = taskService.createTaskQuery().processInstanceId(processInstance.getId()).list();
        assertEquals(2, tasks.size());
        Task userTask1 = null;
        for (Task task : tasks) {
            if ("User Task1".equals(task.getName())) {
                userTask1 = task;
                break;
            }
        }
        assertNotNull(userTask1);
        // complete task1 so we flow to terminate end
        taskService.complete(userTask1.getId());
        entityEvent = ((FlowableEntityEvent) (testListener.getEventsReceived().get((idx++))));
        assertEquals(TASK_COMPLETED, entityEvent.getType());
        taskEntity = ((TaskEntity) (entityEvent.getEntity()));
        assertEquals("User Task1", taskEntity.getName());
        activityEvent = ((FlowableActivityEvent) (testListener.getEventsReceived().get((idx++))));
        assertEquals(ACTIVITY_COMPLETED, activityEvent.getType());
        assertEquals("task1", activityEvent.getActivityId());
        assertEquals(task1Execution.getId(), activityEvent.getExecutionId());
        activityEvent = ((FlowableActivityEvent) (testListener.getEventsReceived().get((idx++))));
        assertEquals(ACTIVITY_STARTED, activityEvent.getType());
        assertEquals("endEvent", activityEvent.getActivityType());
        assertEquals("endEvent1", activityEvent.getActivityId());
        for (int i = 0; i < 2; i++) {
            activityEvent = ((FlowableActivityEvent) (testListener.getEventsReceived().get((idx++))));
            assertEquals(ACTIVITY_CANCELLED, activityEvent.getType());
            FlowableActivityCancelledEvent cancelledEvent = ((FlowableActivityCancelledEvent) (activityEvent));
            if ("task2".equals(cancelledEvent.getActivityId())) {
                assertEquals("task2", cancelledEvent.getActivityId());
                assertEquals("userTask", cancelledEvent.getActivityType());
                assertEquals("User Task2", cancelledEvent.getActivityName());
                assertEquals(task2Execution.getId(), cancelledEvent.getExecutionId());
            } else
                if ("cancelBoundaryEvent1".equals(cancelledEvent.getActivityId())) {
                    assertEquals(ACTIVITY_CANCELLED, activityEvent.getType());
                    cancelledEvent = ((FlowableActivityCancelledEvent) (activityEvent));
                    assertEquals("cancelBoundaryEvent1", cancelledEvent.getActivityId());
                    assertEquals(boundaryExecution.getId(), cancelledEvent.getExecutionId());
                }

        }
        entityEvent = ((FlowableEntityEvent) (testListener.getEventsReceived().get((idx++))));
        assertEquals(PROCESS_COMPLETED_WITH_TERMINATE_END_EVENT, entityEvent.getType());
        assertEquals(13, idx);
        assertEquals(13, testListener.getEventsReceived().size());
    }

    /**
     * User task cancelled by message boundary event.
     */
    @Test
    @Deployment(resources = { "org/flowable/engine/test/api/event/CancelUserTaskEventsTest.bpmn20.xml" })
    public void testUserTaskCancelledByMessageBoundaryEvent() throws Exception {
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("cancelUserTaskEvents");
        assertNotNull(processInstance);
        Execution task1Execution = runtimeService.createExecutionQuery().activityId("task1").singleResult();
        Execution task2Execution = runtimeService.createExecutionQuery().activityId("task2").singleResult();
        Execution boundaryExecution = runtimeService.createExecutionQuery().activityId("cancelBoundaryEvent1").singleResult();
        int idx = 0;
        FlowableEvent flowableEvent = testListener.getEventsReceived().get((idx++));
        assertEquals(PROCESS_STARTED, flowableEvent.getType());
        ExecutionEntity executionEntity = ((ExecutionEntity) (getEntity()));
        assertEquals(processInstance.getId(), executionEntity.getProcessInstanceId());
        FlowableActivityEvent activityEvent = ((FlowableActivityEvent) (testListener.getEventsReceived().get((idx++))));
        assertEquals(ACTIVITY_STARTED, activityEvent.getType());
        assertEquals("startEvent", activityEvent.getActivityType());
        activityEvent = ((FlowableActivityEvent) (testListener.getEventsReceived().get((idx++))));
        assertEquals(ACTIVITY_COMPLETED, activityEvent.getType());
        assertEquals("startEvent", activityEvent.getActivityType());
        activityEvent = ((FlowableActivityEvent) (testListener.getEventsReceived().get((idx++))));
        assertEquals(ACTIVITY_STARTED, activityEvent.getType());
        assertEquals("task1", activityEvent.getActivityId());
        FlowableEntityEvent entityEvent = ((FlowableEntityEvent) (testListener.getEventsReceived().get((idx++))));
        assertEquals(TASK_CREATED, entityEvent.getType());
        TaskEntity taskEntity = ((TaskEntity) (entityEvent.getEntity()));
        assertEquals("User Task1", taskEntity.getName());
        activityEvent = ((FlowableActivityEvent) (testListener.getEventsReceived().get((idx++))));
        assertEquals(ACTIVITY_STARTED, activityEvent.getType());
        assertEquals("task2", activityEvent.getActivityId());
        entityEvent = ((FlowableEntityEvent) (testListener.getEventsReceived().get((idx++))));
        assertEquals(TASK_CREATED, entityEvent.getType());
        taskEntity = ((TaskEntity) (entityEvent.getEntity()));
        assertEquals("User Task2", taskEntity.getName());
        Execution cancelMessageExecution = runtimeService.createExecutionQuery().messageEventSubscriptionName("cancel").singleResult();
        assertNotNull(cancelMessageExecution);
        assertEquals("cancelBoundaryEvent1", cancelMessageExecution.getActivityId());
        // cancel the user task (task2)
        runtimeService.messageEventReceived("cancel", cancelMessageExecution.getId());
        activityEvent = ((FlowableActivityEvent) (testListener.getEventsReceived().get((idx++))));
        assertEquals(ACTIVITY_CANCELLED, activityEvent.getType());
        FlowableActivityCancelledEvent cancelledEvent = ((FlowableActivityCancelledEvent) (activityEvent));
        assertEquals("task2", cancelledEvent.getActivityId());
        assertEquals("userTask", cancelledEvent.getActivityType());
        assertEquals("User Task2", cancelledEvent.getActivityName());
        assertEquals(task2Execution.getId(), cancelledEvent.getExecutionId());
        activityEvent = ((FlowableActivityEvent) (testListener.getEventsReceived().get((idx++))));
        assertEquals(ACTIVITY_COMPLETED, activityEvent.getType());
        assertEquals("cancelBoundaryEvent1", activityEvent.getActivityId());
        assertEquals("boundaryEvent", activityEvent.getActivityType());
        assertEquals(boundaryExecution.getId(), activityEvent.getExecutionId());
        activityEvent = ((FlowableActivityEvent) (testListener.getEventsReceived().get((idx++))));
        assertEquals(ACTIVITY_STARTED, activityEvent.getType());
        assertEquals("endEvent1", activityEvent.getActivityId());
        activityEvent = ((FlowableActivityEvent) (testListener.getEventsReceived().get((idx++))));
        assertEquals(ACTIVITY_CANCELLED, activityEvent.getType());
        assertEquals("task1", activityEvent.getActivityId());
        assertEquals(task1Execution.getId(), activityEvent.getExecutionId());
        entityEvent = ((FlowableEntityEvent) (testListener.getEventsReceived().get((idx++))));
        assertEquals(PROCESS_COMPLETED_WITH_TERMINATE_END_EVENT, entityEvent.getType());
        assertEquals(12, idx);
        assertEquals(12, testListener.getEventsReceived().size());
    }

    class UserActivityEventListener extends AbstractFlowableEngineEventListener {
        private List<FlowableEvent> eventsReceived;

        public UserActivityEventListener() {
            super(new java.util.HashSet(Arrays.asList(ACTIVITY_STARTED, ACTIVITY_COMPLETED, ACTIVITY_CANCELLED, TASK_CREATED, TASK_COMPLETED, PROCESS_STARTED, PROCESS_COMPLETED, PROCESS_CANCELLED, PROCESS_COMPLETED_WITH_TERMINATE_END_EVENT)));
            eventsReceived = new ArrayList();
        }

        public List<FlowableEvent> getEventsReceived() {
            return eventsReceived;
        }

        public void clearEventsReceived() {
            eventsReceived.clear();
        }

        @Override
        protected void activityStarted(FlowableActivityEvent event) {
            eventsReceived.add(event);
        }

        @Override
        protected void activityCompleted(FlowableActivityEvent event) {
            eventsReceived.add(event);
        }

        @Override
        protected void activityCancelled(FlowableActivityCancelledEvent event) {
            eventsReceived.add(event);
        }

        @Override
        protected void taskCreated(FlowableEngineEntityEvent event) {
            eventsReceived.add(event);
        }

        @Override
        protected void taskCompleted(FlowableEngineEntityEvent event) {
            eventsReceived.add(event);
        }

        @Override
        protected void processStarted(FlowableProcessStartedEvent event) {
            eventsReceived.add(event);
        }

        @Override
        protected void processCompleted(FlowableEngineEntityEvent event) {
            eventsReceived.add(event);
        }

        @Override
        protected void processCompletedWithTerminateEnd(FlowableEngineEntityEvent event) {
            eventsReceived.add(event);
        }

        @Override
        protected void processCancelled(FlowableCancelledEvent event) {
            eventsReceived.add(event);
        }

        @Override
        public boolean isFailOnException() {
            return false;
        }
    }
}

