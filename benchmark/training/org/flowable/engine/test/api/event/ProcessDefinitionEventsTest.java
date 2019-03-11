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


import FlowableEngineEventType.ENTITY_CREATED;
import org.flowable.common.engine.api.delegate.event.FlowableEngineEventType;
import org.flowable.common.engine.api.delegate.event.FlowableEntityEvent;
import org.flowable.common.engine.api.delegate.event.FlowableEvent;
import org.flowable.engine.delegate.event.impl.FlowableEventBuilder;
import org.flowable.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.flowable.engine.impl.test.PluggableFlowableTestCase;
import org.flowable.engine.impl.util.ProcessDefinitionUtil;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.job.service.impl.persistence.entity.TimerJobEntity;
import org.junit.jupiter.api.Test;


/**
 * Test case for all {@link FlowableEvent}s related to process definitions.
 *
 * @author Frederik Heremans
 */
public class ProcessDefinitionEventsTest extends PluggableFlowableTestCase {
    /**
     * Test create, update and delete events of process definitions.
     */
    @Test
    public void testProcessDefinitionEvents() throws Exception {
        repositoryService.createDeployment().addClasspathResource("org/flowable/engine/test/api/runtime/oneTaskProcess.bpmn20.xml").deploy();
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().processDefinitionKey("oneTaskProcess").singleResult();
        assertNotNull(processDefinition);
        // Check create-event
        assertEquals(2, listener.getEventsReceived().size());
        assertTrue(((listener.getEventsReceived().get(0)) instanceof FlowableEntityEvent));
        FlowableEntityEvent event = ((FlowableEntityEvent) (listener.getEventsReceived().get(0)));
        assertEquals(ENTITY_CREATED, event.getType());
        assertEquals(processDefinition.getId(), getId());
        event = ((FlowableEntityEvent) (listener.getEventsReceived().get(1)));
        assertEquals(FlowableEngineEventType.ENTITY_INITIALIZED, event.getType());
        assertEquals(processDefinition.getId(), getId());
        listener.clearEventsReceived();
        // Check update event when category is updated
        repositoryService.setProcessDefinitionCategory(processDefinition.getId(), "test");
        assertEquals(1, listener.getEventsReceived().size());
        assertTrue(((listener.getEventsReceived().get(0)) instanceof FlowableEntityEvent));
        event = ((FlowableEntityEvent) (listener.getEventsReceived().get(0)));
        assertEquals(FlowableEngineEventType.ENTITY_UPDATED, event.getType());
        assertEquals(processDefinition.getId(), getId());
        assertEquals("test", getCategory());
        listener.clearEventsReceived();
        // Check update event when suspended/activated
        repositoryService.suspendProcessDefinitionById(processDefinition.getId());
        repositoryService.activateProcessDefinitionById(processDefinition.getId());
        assertEquals(2, listener.getEventsReceived().size());
        event = ((FlowableEntityEvent) (listener.getEventsReceived().get(0)));
        assertEquals(processDefinition.getId(), getId());
        assertEquals(FlowableEngineEventType.ENTITY_SUSPENDED, event.getType());
        event = ((FlowableEntityEvent) (listener.getEventsReceived().get(1)));
        assertEquals(FlowableEngineEventType.ENTITY_ACTIVATED, event.getType());
        assertEquals(processDefinition.getId(), getId());
        listener.clearEventsReceived();
        // Check delete event when category is updated
        repositoryService.deleteDeployment(processDefinition.getDeploymentId(), true);
        assertEquals(1, listener.getEventsReceived().size());
        assertTrue(((listener.getEventsReceived().get(0)) instanceof FlowableEntityEvent));
        event = ((FlowableEntityEvent) (listener.getEventsReceived().get(0)));
        assertEquals(FlowableEngineEventType.ENTITY_DELETED, event.getType());
        assertEquals(processDefinition.getId(), getId());
        listener.clearEventsReceived();
    }

    private TestMultipleFlowableEventListener listener;

    /**
     * test sequence of events for process definition with timer start event
     */
    @Test
    public void testTimerStartEventDeployment() {
        deploymentIdsForAutoCleanup.add(getId());
        ProcessDefinitionEntity processDefinition = ((ProcessDefinitionEntity) (repositoryService.createProcessDefinitionQuery().processDefinitionKey("startTimerEventExample").singleResult()));
        FlowableEntityEvent processDefinitionCreated = FlowableEventBuilder.createEntityEvent(ENTITY_CREATED, processDefinition);
        TimerJobEntity timer = ((TimerJobEntity) (managementService.createTimerJobQuery().singleResult()));
        FlowableEntityEvent timerCreated = FlowableEventBuilder.createEntityEvent(ENTITY_CREATED, timer);
        assertSequence(processDefinitionCreated, timerCreated);
        listener.clearEventsReceived();
    }

    private static class ProcessDefinitionEventsListener extends TestMultipleFlowableEventListener {
        @Override
        public void onEvent(FlowableEvent event) {
            super.onEvent(event);
            if (event instanceof FlowableEntityEvent) {
                Object entity = getEntity();
                switch (((FlowableEngineEventType) (event.getType()))) {
                    case ENTITY_CREATED :
                        if (entity instanceof ProcessDefinitionEntity) {
                            // It is necessary to have process already present on the ProcessDefinitionEntity CREATE event
                            ProcessDefinitionUtil.getProcess(getId());
                        }
                    default :
                        break;
                }
            }
        }
    }
}

