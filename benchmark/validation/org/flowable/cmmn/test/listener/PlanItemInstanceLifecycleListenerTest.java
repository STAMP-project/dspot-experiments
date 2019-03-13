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
package org.flowable.cmmn.test.listener;


import PlanItemDefinitionType.HUMAN_TASK;
import PlanItemInstanceState.ACTIVE;
import PlanItemInstanceState.AVAILABLE;
import PlanItemInstanceState.COMPLETED;
import PlanItemInstanceState.DISABLED;
import PlanItemInstanceState.ENABLED;
import java.util.List;
import java.util.Map;
import org.flowable.cmmn.api.listener.PlanItemInstanceLifecycleListener;
import org.flowable.cmmn.api.runtime.PlanItemInstance;
import org.flowable.cmmn.api.runtime.PlanItemInstanceState;
import org.flowable.cmmn.engine.test.FlowableCmmnTestCase;
import org.flowable.task.api.Task;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Joram Barrez
 */
public class PlanItemInstanceLifecycleListenerTest extends FlowableCmmnTestCase {
    private Map<String, List<PlanItemInstanceLifecycleListener>> originalLifeCycleListeners;

    private String deploymentId;

    private AbstractTestLifecycleListener testLifeCycleListener;

    @Test
    public void testReceiveAllLifeCycleEvents() {
        setTestLifeCycleListener(null, new TestReceiveAllLifecycleListener());
        // Start case instance
        cmmnRuntimeService.createCaseInstanceBuilder().caseDefinitionKey("testLifeCycleListener").start();
        List<TestLifeCycleEvent> events = testLifeCycleListener.getEvents();
        Assert.assertEquals(7, events.size());
        assertEvent(events.get(0), "Stage one", null, AVAILABLE);
        assertEvent(events.get(1), "Stage two", null, AVAILABLE);
        assertEvent(events.get(2), "Stage one", AVAILABLE, ACTIVE);
        assertEvent(events.get(3), "A", null, AVAILABLE);
        assertEvent(events.get(4), "B", null, AVAILABLE);
        assertEvent(events.get(5), "A", AVAILABLE, ACTIVE);
        assertEvent(events.get(6), "B", AVAILABLE, ENABLED);
        testLifeCycleListener.clear();
        // Disable B
        PlanItemInstance planItemInstanceB = cmmnRuntimeService.createPlanItemInstanceQuery().planItemInstanceName("B").singleResult();
        cmmnRuntimeService.disablePlanItemInstance(planItemInstanceB.getId());
        events = testLifeCycleListener.getEvents();
        Assert.assertEquals(1, events.size());
        assertEvent(events.get(0), "B", ENABLED, DISABLED);
        testLifeCycleListener.clear();
        // Enable B
        cmmnRuntimeService.enablePlanItemInstance(planItemInstanceB.getId());
        events = testLifeCycleListener.getEvents();
        Assert.assertEquals(1, events.size());
        assertEvent(events.get(0), "B", DISABLED, ENABLED);
        testLifeCycleListener.clear();
        // Start B
        planItemInstanceB = cmmnRuntimeService.createPlanItemInstanceQuery().planItemInstanceName("B").singleResult();
        cmmnRuntimeService.startPlanItemInstance(planItemInstanceB.getId());
        events = testLifeCycleListener.getEvents();
        Assert.assertEquals(1, events.size());
        assertEvent(events.get(0), "B", ENABLED, ACTIVE);
        testLifeCycleListener.clear();
        // Complete A and B
        for (Task task : cmmnTaskService.createTaskQuery().orderByTaskName().asc().list()) {
            cmmnTaskService.complete(task.getId());
        }
        events = testLifeCycleListener.getEvents();
        Assert.assertEquals(10, events.size());
        assertEvent(events.get(0), "A", ACTIVE, COMPLETED);
        assertEvent(events.get(1), "B", ACTIVE, COMPLETED);
        assertEvent(events.get(2), "Stage one", ACTIVE, COMPLETED);
        assertEvent(events.get(3), "Stage two", AVAILABLE, ACTIVE);
        assertEvent(events.get(4), "timer", null, AVAILABLE);
        assertEvent(events.get(5), "M1", null, AVAILABLE);
        assertEvent(events.get(6), "C", null, AVAILABLE);
        assertEvent(events.get(8), "C", AVAILABLE, ACTIVE);
        assertEvent(events.get(7), "M1", AVAILABLE, ACTIVE);
        assertEvent(events.get(9), "M1", ACTIVE, COMPLETED);
    }

    @Test
    public void testFilterOnType() {
        setTestLifeCycleListener(HUMAN_TASK, new TestFilterTypesLifecycleListener());
        // Start case instance
        cmmnRuntimeService.createCaseInstanceBuilder().caseDefinitionKey("testLifeCycleListener").start();
        List<TestLifeCycleEvent> events = testLifeCycleListener.getEvents();
        Assert.assertEquals(4, events.size());
        assertEvent(events.get(0), "A", null, AVAILABLE);
        assertEvent(events.get(1), "B", null, AVAILABLE);
        assertEvent(events.get(2), "A", AVAILABLE, ACTIVE);
        assertEvent(events.get(3), "B", AVAILABLE, ENABLED);
        testLifeCycleListener.clear();
        // Disable B
        PlanItemInstance planItemInstanceB = cmmnRuntimeService.createPlanItemInstanceQuery().planItemInstanceName("B").singleResult();
        cmmnRuntimeService.disablePlanItemInstance(planItemInstanceB.getId());
        events = testLifeCycleListener.getEvents();
        Assert.assertEquals(1, events.size());
        assertEvent(events.get(0), "B", ENABLED, DISABLED);
        testLifeCycleListener.clear();
        // Enable B
        cmmnRuntimeService.enablePlanItemInstance(planItemInstanceB.getId());
        events = testLifeCycleListener.getEvents();
        Assert.assertEquals(1, events.size());
        assertEvent(events.get(0), "B", DISABLED, ENABLED);
        testLifeCycleListener.clear();
        // Start B
        planItemInstanceB = cmmnRuntimeService.createPlanItemInstanceQuery().planItemInstanceName("B").singleResult();
        cmmnRuntimeService.startPlanItemInstance(planItemInstanceB.getId());
        events = testLifeCycleListener.getEvents();
        Assert.assertEquals(1, events.size());
        assertEvent(events.get(0), "B", ENABLED, ACTIVE);
        testLifeCycleListener.clear();
        // Complete A and B
        for (Task task : cmmnTaskService.createTaskQuery().orderByTaskName().asc().list()) {
            cmmnTaskService.complete(task.getId());
        }
        events = testLifeCycleListener.getEvents();
        Assert.assertEquals(4, events.size());
        assertEvent(events.get(0), "A", ACTIVE, COMPLETED);
        assertEvent(events.get(1), "B", ACTIVE, COMPLETED);
        assertEvent(events.get(2), "C", null, AVAILABLE);
        assertEvent(events.get(3), "C", AVAILABLE, ACTIVE);
    }

    @Test
    public void testFilterBySourceState() {
        setTestLifeCycleListener(null, new TestFilterSourceStateLifecycleListener(PlanItemInstanceState.AVAILABLE));
        // Start case instance
        cmmnRuntimeService.createCaseInstanceBuilder().caseDefinitionKey("testLifeCycleListener").start();
        List<TestLifeCycleEvent> events = testLifeCycleListener.getEvents();
        Assert.assertEquals(3, events.size());
        assertEvent(events.get(0), "Stage one", AVAILABLE, ACTIVE);
        assertEvent(events.get(1), "A", AVAILABLE, ACTIVE);
        assertEvent(events.get(2), "B", AVAILABLE, ENABLED);
        testLifeCycleListener.clear();
        // Disable B
        PlanItemInstance planItemInstanceB = cmmnRuntimeService.createPlanItemInstanceQuery().planItemInstanceName("B").singleResult();
        cmmnRuntimeService.disablePlanItemInstance(planItemInstanceB.getId());
        events = testLifeCycleListener.getEvents();
        Assert.assertEquals(0, events.size());
        // Enable B
        cmmnRuntimeService.enablePlanItemInstance(planItemInstanceB.getId());
        events = testLifeCycleListener.getEvents();
        Assert.assertEquals(0, events.size());
        // Start B
        planItemInstanceB = cmmnRuntimeService.createPlanItemInstanceQuery().planItemInstanceName("B").singleResult();
        cmmnRuntimeService.startPlanItemInstance(planItemInstanceB.getId());
        events = testLifeCycleListener.getEvents();
        Assert.assertEquals(0, events.size());
        testLifeCycleListener.clear();
        // Complete A and B
        for (Task task : cmmnTaskService.createTaskQuery().orderByTaskName().asc().list()) {
            cmmnTaskService.complete(task.getId());
        }
        events = testLifeCycleListener.getEvents();
        Assert.assertEquals(3, events.size());
        assertEvent(events.get(0), "Stage two", AVAILABLE, ACTIVE);
        assertEvent(events.get(1), "M1", AVAILABLE, ACTIVE);
        assertEvent(events.get(2), "C", AVAILABLE, ACTIVE);
    }

    @Test
    public void testFilterByTargetState() {
        setTestLifeCycleListener(null, new TestFilterTargetStateLifecycleListener(PlanItemInstanceState.ACTIVE));
        // Start case instance
        cmmnRuntimeService.createCaseInstanceBuilder().caseDefinitionKey("testLifeCycleListener").start();
        List<TestLifeCycleEvent> events = testLifeCycleListener.getEvents();
        Assert.assertEquals(2, events.size());
        assertEvent(events.get(0), "Stage one", AVAILABLE, ACTIVE);
        assertEvent(events.get(1), "A", AVAILABLE, ACTIVE);
        testLifeCycleListener.clear();
        // Disable B
        PlanItemInstance planItemInstanceB = cmmnRuntimeService.createPlanItemInstanceQuery().planItemInstanceName("B").singleResult();
        cmmnRuntimeService.disablePlanItemInstance(planItemInstanceB.getId());
        events = testLifeCycleListener.getEvents();
        Assert.assertEquals(0, events.size());
        // Enable B
        cmmnRuntimeService.enablePlanItemInstance(planItemInstanceB.getId());
        events = testLifeCycleListener.getEvents();
        Assert.assertEquals(0, events.size());
        // Start B
        planItemInstanceB = cmmnRuntimeService.createPlanItemInstanceQuery().planItemInstanceName("B").singleResult();
        cmmnRuntimeService.startPlanItemInstance(planItemInstanceB.getId());
        events = testLifeCycleListener.getEvents();
        Assert.assertEquals(1, events.size());
        assertEvent(events.get(0), "B", ENABLED, ACTIVE);
        testLifeCycleListener.clear();
        // Complete A and B
        for (Task task : cmmnTaskService.createTaskQuery().orderByTaskName().asc().list()) {
            cmmnTaskService.complete(task.getId());
        }
        events = testLifeCycleListener.getEvents();
        Assert.assertEquals(3, events.size());
        assertEvent(events.get(0), "Stage two", AVAILABLE, ACTIVE);
        assertEvent(events.get(1), "M1", AVAILABLE, ACTIVE);
        assertEvent(events.get(2), "C", AVAILABLE, ACTIVE);
    }
}

