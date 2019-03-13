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
package org.flowable.cmmn.test.runtime;


import PlanItemDefinitionType.HUMAN_TASK;
import PlanItemDefinitionType.STAGE;
import PlanItemDefinitionType.TIMER_EVENT_LISTENER;
import PlanItemDefinitionType.USER_EVENT_LISTENER;
import PlanItemInstanceState.ACTIVE;
import PlanItemInstanceState.AVAILABLE;
import PlanItemInstanceState.WAITING_FOR_REPETITION;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.flowable.cmmn.api.runtime.CaseInstance;
import org.flowable.cmmn.api.runtime.PlanItemInstance;
import org.flowable.cmmn.api.runtime.UserEventListenerInstance;
import org.flowable.cmmn.engine.test.CmmnDeployment;
import org.flowable.cmmn.engine.test.FlowableCmmnTestCase;
import org.flowable.task.api.Task;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;


/**
 *
 *
 * @author Dennis Federico
 * @author Joram Barrez
 */
public class UserEventListenerTest extends FlowableCmmnTestCase {
    @Rule
    public TestName name = new TestName();

    @Test
    @CmmnDeployment
    public void testSimpleEnableTask() {
        // Simple use of the UserEventListener as EntryCriteria of a Task
        CaseInstance caseInstance = cmmnRuntimeService.createCaseInstanceBuilder().caseDefinitionKey(name.getMethodName()).start();
        Assert.assertNotNull(caseInstance);
        Assert.assertEquals(1, cmmnRuntimeService.createCaseInstanceQuery().count());
        // 3 PlanItems reachable
        Assert.assertEquals(3, cmmnRuntimeService.createPlanItemInstanceQuery().count());
        // 1 User Event Listener
        PlanItemInstance listenerInstance = cmmnRuntimeService.createPlanItemInstanceQuery().planItemDefinitionType(USER_EVENT_LISTENER).singleResult();
        Assert.assertNotNull(listenerInstance);
        Assert.assertEquals("userEventListener", listenerInstance.getPlanItemDefinitionId());
        Assert.assertEquals(AVAILABLE, listenerInstance.getState());
        // Verify same result is returned from query
        UserEventListenerInstance userEventListenerInstance = cmmnRuntimeService.createUserEventListenerInstanceQuery().caseInstanceId(caseInstance.getId()).singleResult();
        Assert.assertNotNull(userEventListenerInstance);
        Assert.assertEquals(userEventListenerInstance.getId(), listenerInstance.getId());
        Assert.assertEquals(userEventListenerInstance.getCaseDefinitionId(), listenerInstance.getCaseDefinitionId());
        Assert.assertEquals(userEventListenerInstance.getCaseInstanceId(), listenerInstance.getCaseInstanceId());
        Assert.assertEquals(userEventListenerInstance.getElementId(), listenerInstance.getElementId());
        Assert.assertEquals(userEventListenerInstance.getName(), listenerInstance.getName());
        Assert.assertEquals(userEventListenerInstance.getPlanItemDefinitionId(), listenerInstance.getPlanItemDefinitionId());
        Assert.assertEquals(userEventListenerInstance.getStageInstanceId(), listenerInstance.getStageInstanceId());
        Assert.assertEquals(userEventListenerInstance.getState(), listenerInstance.getState());
        Assert.assertEquals(1, cmmnRuntimeService.createUserEventListenerInstanceQuery().count());
        Assert.assertEquals(1, cmmnRuntimeService.createUserEventListenerInstanceQuery().list().size());
        Assert.assertNotNull(cmmnRuntimeService.createUserEventListenerInstanceQuery().caseDefinitionId(listenerInstance.getCaseDefinitionId()).singleResult());
        Assert.assertNotNull(cmmnRuntimeService.createUserEventListenerInstanceQuery().caseDefinitionId(listenerInstance.getCaseDefinitionId()).singleResult());
        // 2 HumanTasks ... one active and other waiting (available)
        Assert.assertEquals(2, cmmnRuntimeService.createPlanItemInstanceQuery().planItemDefinitionType(HUMAN_TASK).count());
        PlanItemInstance active = cmmnRuntimeService.createPlanItemInstanceQuery().planItemDefinitionType(HUMAN_TASK).planItemInstanceStateActive().singleResult();
        Assert.assertNotNull(active);
        Assert.assertEquals("taskA", active.getPlanItemDefinitionId());
        PlanItemInstance available = cmmnRuntimeService.createPlanItemInstanceQuery().planItemDefinitionType(HUMAN_TASK).planItemInstanceStateAvailable().singleResult();
        Assert.assertNotNull(available);
        Assert.assertEquals("taskB", available.getPlanItemDefinitionId());
        // Trigger the listener
        cmmnRuntimeService.completeUserEventListenerInstance(listenerInstance.getId());
        // UserEventListener should be completed
        Assert.assertEquals(0, cmmnRuntimeService.createPlanItemInstanceQuery().planItemDefinitionType(USER_EVENT_LISTENER).count());
        // Only 2 PlanItems left
        Assert.assertEquals(2, cmmnRuntimeService.createPlanItemInstanceQuery().list().size());
        // Both Human task should be "active" now, as the sentry kicks on the UserEventListener transition
        Assert.assertEquals(2, cmmnRuntimeService.createPlanItemInstanceQuery().planItemDefinitionType(HUMAN_TASK).planItemInstanceStateActive().count());
        // Finish the caseInstance
        assertCaseInstanceNotEnded(caseInstance);
        cmmnTaskService.createTaskQuery().list().forEach(( t) -> cmmnTaskService.complete(t.getId()));
        assertCaseInstanceEnded(caseInstance);
    }

    @Test
    @CmmnDeployment
    public void testTerminateTask() {
        // Test case where the UserEventListener is used to complete (ExitCriteria) of a task
        CaseInstance caseInstance = cmmnRuntimeService.createCaseInstanceBuilder().caseDefinitionKey(name.getMethodName()).start();
        Assert.assertNotNull(caseInstance);
        Assert.assertEquals(1, cmmnRuntimeService.createCaseInstanceQuery().count());
        // 4 PlanItems reachable
        Assert.assertEquals(4, cmmnRuntimeService.createPlanItemInstanceQuery().list().size());
        // 1 Stage
        Assert.assertEquals(1, cmmnRuntimeService.createPlanItemInstanceQuery().planItemDefinitionType(STAGE).count());
        // 1 User Event Listener
        PlanItemInstance listenerInstance = cmmnRuntimeService.createPlanItemInstanceQuery().planItemDefinitionType(USER_EVENT_LISTENER).singleResult();
        Assert.assertNotNull(listenerInstance);
        Assert.assertEquals("userEventListener", listenerInstance.getPlanItemDefinitionId());
        Assert.assertEquals("userEventListenerPlanItem", listenerInstance.getElementId());
        Assert.assertEquals(AVAILABLE, listenerInstance.getState());
        // 2 Tasks on Active state
        List<PlanItemInstance> tasks = cmmnRuntimeService.createPlanItemInstanceQuery().planItemDefinitionType("task").list();
        Assert.assertEquals(2, tasks.size());
        tasks.forEach(( t) -> assertEquals(PlanItemInstanceState.ACTIVE, t.getState()));
        // Trigger the listener
        cmmnRuntimeService.triggerPlanItemInstance(listenerInstance.getId());
        // UserEventListener should be completed
        Assert.assertEquals(0, cmmnRuntimeService.createPlanItemInstanceQuery().planItemDefinitionType(USER_EVENT_LISTENER).count());
        // Only 2 PlanItems left (1 stage & 1 active task)
        Assert.assertEquals(2, cmmnRuntimeService.createPlanItemInstanceQuery().list().size());
        PlanItemInstance activeTask = cmmnRuntimeService.createPlanItemInstanceQuery().planItemDefinitionType("task").planItemInstanceStateActive().singleResult();
        Assert.assertNotNull(activeTask);
        Assert.assertEquals("taskB", activeTask.getPlanItemDefinitionId());
        // Finish the caseInstance
        assertCaseInstanceNotEnded(caseInstance);
        cmmnRuntimeService.triggerPlanItemInstance(activeTask.getId());
        assertCaseInstanceEnded(caseInstance);
    }

    @Test
    @CmmnDeployment
    public void testTerminateStage() {
        // Test case where the UserEventListener is used to complete (ExitCriteria) of a Stage
        CaseInstance caseInstance = cmmnRuntimeService.createCaseInstanceBuilder().caseDefinitionKey(name.getMethodName()).start();
        Assert.assertNotNull(caseInstance);
        Assert.assertEquals(1, cmmnRuntimeService.createCaseInstanceQuery().count());
        // 3 PlanItems reachable
        Assert.assertEquals(3, cmmnRuntimeService.createPlanItemInstanceQuery().list().size());
        // 1 Stage
        PlanItemInstance stage = cmmnRuntimeService.createPlanItemInstanceQuery().planItemDefinitionType(STAGE).singleResult();
        Assert.assertNotNull(stage);
        // 1 User Event Listener
        PlanItemInstance listenerInstance = cmmnRuntimeService.createPlanItemInstanceQuery().planItemDefinitionType(USER_EVENT_LISTENER).singleResult();
        Assert.assertNotNull(listenerInstance);
        Assert.assertEquals("userEventListener", listenerInstance.getPlanItemDefinitionId());
        Assert.assertEquals(AVAILABLE, listenerInstance.getState());
        // 1 Tasks on Active state
        PlanItemInstance task = cmmnRuntimeService.createPlanItemInstanceQuery().planItemDefinitionType("task").singleResult();
        Assert.assertNotNull(task);
        Assert.assertEquals(ACTIVE, task.getState());
        Assert.assertEquals(stage.getId(), task.getStageInstanceId());
        // Trigger the listener
        assertCaseInstanceNotEnded(caseInstance);
        cmmnRuntimeService.triggerPlanItemInstance(listenerInstance.getId());
        assertCaseInstanceEnded(caseInstance);
    }

    @Test
    @CmmnDeployment
    public void testRepetitionWithUserEventExitCriteria() {
        // Test case where a repeating task is completed by its ExitCriteria fired by a UserEvent
        CaseInstance caseInstance = cmmnRuntimeService.createCaseInstanceBuilder().caseDefinitionKey(name.getMethodName()).variable("whileTrue", "true").start();
        Assert.assertNotNull(caseInstance);
        for (int i = 0; i < 3; i++) {
            Task taskA = cmmnTaskService.createTaskQuery().active().taskDefinitionKey("taskA").singleResult();
            cmmnTaskService.complete(taskA.getId());
            assertCaseInstanceNotEnded(caseInstance);
        }
        PlanItemInstance userEvent = cmmnRuntimeService.createPlanItemInstanceQuery().planItemDefinitionType(USER_EVENT_LISTENER).singleResult();
        cmmnRuntimeService.triggerPlanItemInstance(userEvent.getId());
        assertCaseInstanceEnded(caseInstance);
    }

    @Test
    @CmmnDeployment
    public void testRepetitionWithUserEventEntryCriteria() {
        // Test case that activates a repeating task (entryCriteria) with a UserEvent
        CaseInstance caseInstance = cmmnRuntimeService.createCaseInstanceBuilder().caseDefinitionKey(name.getMethodName()).variable("whileRepeatTaskA", "true").start();
        Assert.assertNotNull(caseInstance);
        // TaskA on available state until the entry criteria occurs
        PlanItemInstance taskA = cmmnRuntimeService.createPlanItemInstanceQuery().planItemDefinitionType(HUMAN_TASK).planItemDefinitionId("taskA").singleResult();
        Assert.assertEquals(AVAILABLE, taskA.getState());
        // Trigger the userEvent
        PlanItemInstance userEvent = cmmnRuntimeService.createPlanItemInstanceQuery().planItemDefinitionType(USER_EVENT_LISTENER).singleResult();
        cmmnRuntimeService.triggerPlanItemInstance(userEvent.getId());
        // UserEventListener is consumed
        Assert.assertEquals(0, cmmnRuntimeService.createPlanItemInstanceQuery().planItemDefinitionType(USER_EVENT_LISTENER).count());
        // Task is om Active state and a second task instance waiting for repetition
        Map<String, List<PlanItemInstance>> tasks = cmmnRuntimeService.createPlanItemInstanceQuery().planItemDefinitionType(HUMAN_TASK).planItemDefinitionId("taskA").list().stream().collect(Collectors.groupingBy(PlanItemInstance::getState));
        Assert.assertEquals(2, tasks.size());
        Assert.assertTrue(tasks.containsKey(ACTIVE));
        Assert.assertEquals(1, tasks.get(ACTIVE).size());
        Assert.assertTrue(tasks.containsKey(WAITING_FOR_REPETITION));
        Assert.assertEquals(1, tasks.get(WAITING_FOR_REPETITION).size());
        // Complete active task
        cmmnTaskService.complete(cmmnTaskService.createTaskQuery().taskDefinitionKey("taskA").active().singleResult().getId());
        // Only TaskB remains on Active state and one instance of TaskA waiting for repetition
        tasks = cmmnRuntimeService.createPlanItemInstanceQuery().planItemDefinitionType(HUMAN_TASK).list().stream().collect(Collectors.groupingBy(PlanItemInstance::getPlanItemDefinitionId));
        Assert.assertEquals(2, tasks.size());
        Assert.assertEquals(ACTIVE, tasks.get("taskB").get(0).getState());
        Assert.assertEquals(WAITING_FOR_REPETITION, tasks.get("taskA").get(0).getState());
        // Since WAITING_FOR_REPETITION is a "Semi-terminal", completing taskB should complete the stage and case
        cmmnTaskService.complete(cmmnTaskService.createTaskQuery().active().singleResult().getId());
        assertCaseInstanceEnded(caseInstance);
    }

    @Test
    @CmmnDeployment
    public void testStageWithoutFiringTheEvent() {
        // Test case where the only "standing" plainItem for a stage is a UserEventListener
        CaseInstance caseInstance = cmmnRuntimeService.createCaseInstanceBuilder().caseDefinitionKey(name.getMethodName()).start();
        Assert.assertNotNull(caseInstance);
        Assert.assertEquals(1, cmmnRuntimeService.createCaseInstanceQuery().count());
        Map<String, List<PlanItemInstance>> planItems = cmmnRuntimeService.createPlanItemInstanceQuery().list().stream().collect(Collectors.groupingBy(PlanItemInstance::getPlanItemDefinitionType));
        // 3 types of planItems
        Assert.assertEquals(3, planItems.size());
        // 1 User Event Listener
        Assert.assertEquals(1, planItems.getOrDefault(USER_EVENT_LISTENER, Collections.emptyList()).size());
        // 1 Stage
        Assert.assertEquals(1, planItems.getOrDefault(STAGE, Collections.emptyList()).size());
        PlanItemInstance stage = planItems.get(STAGE).get(0);
        // 1 Active Task (inside the stage)
        Assert.assertEquals(1, planItems.getOrDefault("task", Collections.emptyList()).size());
        PlanItemInstance task = planItems.get("task").get(0);
        Assert.assertEquals(ACTIVE, task.getState());
        Assert.assertEquals(stage.getId(), task.getStageInstanceId());
        // Complete the Task
        cmmnRuntimeService.triggerPlanItemInstance(task.getId());
        // Listener should be deleted as it's an orphan
        planItems = cmmnRuntimeService.createPlanItemInstanceQuery().list().stream().collect(Collectors.groupingBy(PlanItemInstance::getPlanItemDefinitionType));
        Assert.assertEquals(0, planItems.size());
        // Stage and case instance should have ended...
        assertCaseInstanceEnded(caseInstance);
    }

    @Test
    @CmmnDeployment
    public void testCaseWithoutFiringTheEvent() {
        // Test case where the only "standing" plainItem for a Case is a UserEventListener
        CaseInstance caseInstance = cmmnRuntimeService.createCaseInstanceBuilder().caseDefinitionKey(name.getMethodName()).start();
        Assert.assertNotNull(caseInstance);
        Assert.assertEquals(1, cmmnRuntimeService.createCaseInstanceQuery().count());
        Map<String, List<PlanItemInstance>> planItems = cmmnRuntimeService.createPlanItemInstanceQuery().list().stream().collect(Collectors.groupingBy(PlanItemInstance::getPlanItemDefinitionType));
        // 3 types of planItems
        Assert.assertEquals(3, planItems.size());
        // 1 Stage
        Assert.assertEquals(1, planItems.getOrDefault(STAGE, Collections.emptyList()).size());
        PlanItemInstance stage = planItems.get(STAGE).get(0);
        // 1 Active Task (inside the stage)
        Assert.assertEquals(1, planItems.getOrDefault("task", Collections.emptyList()).size());
        PlanItemInstance task = planItems.get("task").get(0);
        Assert.assertEquals(ACTIVE, task.getState());
        Assert.assertEquals(stage.getId(), task.getStageInstanceId());
        // 1 Available User Event Listener (outside the stage)
        Assert.assertEquals(1, planItems.getOrDefault(USER_EVENT_LISTENER, Collections.emptyList()).size());
        PlanItemInstance listener = planItems.get(USER_EVENT_LISTENER).get(0);
        Assert.assertNull(listener.getStageInstanceId());
        // Complete the Task
        cmmnRuntimeService.triggerPlanItemInstance(task.getId());
        // Listener should be terminated as it's orphaned
        planItems = cmmnRuntimeService.createPlanItemInstanceQuery().list().stream().collect(Collectors.groupingBy(PlanItemInstance::getPlanItemDefinitionType));
        Assert.assertEquals(0, planItems.size());
        assertCaseInstanceEnded(caseInstance);
    }

    @Test
    @CmmnDeployment
    public void testAutocompleteStageWithoutFiringTheEvent() {
        // Test case where the only "standing" plainItem for a autocomplete stage is a UserEventListener
        CaseInstance caseInstance = cmmnRuntimeService.createCaseInstanceBuilder().caseDefinitionKey(name.getMethodName()).start();
        Assert.assertNotNull(caseInstance);
        Assert.assertEquals(1, cmmnRuntimeService.createCaseInstanceQuery().count());
        // 3 PlanItems reachable
        Assert.assertEquals(3, cmmnRuntimeService.createPlanItemInstanceQuery().count());
        // 1 Stage
        PlanItemInstance stage = cmmnRuntimeService.createPlanItemInstanceQuery().planItemDefinitionType(STAGE).singleResult();
        Assert.assertNotNull(stage);
        // 1 User Event Listener
        List<PlanItemInstance> listeners = cmmnRuntimeService.createPlanItemInstanceQuery().planItemDefinitionType(USER_EVENT_LISTENER).list();
        Assert.assertEquals(1, listeners.size());
        listeners.forEach(( l) -> assertEquals(PlanItemInstanceState.AVAILABLE, l.getState()));
        // 1 Tasks on Active state
        PlanItemInstance task = cmmnRuntimeService.createPlanItemInstanceQuery().planItemDefinitionType("task").singleResult();
        Assert.assertNotNull(task);
        Assert.assertEquals(ACTIVE, task.getState());
        Assert.assertEquals(stage.getId(), task.getStageInstanceId());
        // Complete the Task
        cmmnRuntimeService.triggerPlanItemInstance(task.getId());
        // Stage and case instance should have ended...
        assertCaseInstanceEnded(caseInstance);
    }

    @Test
    @CmmnDeployment
    public void testUserEventListenerInstanceQuery() {
        // Test for UserEventListenerInstanceQuery
        CaseInstance caseInstance = cmmnRuntimeService.createCaseInstanceBuilder().caseDefinitionKey(name.getMethodName()).start();
        Assert.assertNotNull(caseInstance);
        Assert.assertEquals(1, cmmnRuntimeService.createCaseInstanceQuery().count());
        // All planItemInstances
        Assert.assertEquals(8, cmmnRuntimeService.createPlanItemInstanceQuery().count());
        // UserEventListenerIntances
        Assert.assertEquals(6, cmmnRuntimeService.createUserEventListenerInstanceQuery().stateAvailable().count());
        List<UserEventListenerInstance> events = cmmnRuntimeService.createUserEventListenerInstanceQuery().list();
        Assert.assertEquals(6, events.size());
        // All different Intances id's
        Assert.assertEquals(6, events.stream().map(UserEventListenerInstance::getId).distinct().count());
        // UserEventListenerIntances inside Stage1
        String stage1Id = cmmnRuntimeService.createPlanItemInstanceQuery().planItemDefinitionType(STAGE).planItemDefinitionId("stage1").singleResult().getId();
        Assert.assertEquals(2, cmmnRuntimeService.createUserEventListenerInstanceQuery().stageInstanceId(stage1Id).count());
        // UserEventListenerIntances inside Stage2
        String stage2Id = cmmnRuntimeService.createPlanItemInstanceQuery().planItemDefinitionType(STAGE).planItemDefinitionId("stage2").singleResult().getId();
        Assert.assertEquals(2, cmmnRuntimeService.createUserEventListenerInstanceQuery().stageInstanceId(stage2Id).count());
        // UserEventListenerIntances not in a Stage
        Assert.assertEquals(2, events.stream().filter(( e) -> (e.getStageInstanceId()) == null).count());
        // Test query by elementId
        Assert.assertNotNull(cmmnRuntimeService.createUserEventListenerInstanceQuery().elementId("caseUserEventListenerOne").singleResult());
        // Test query by planItemDefinitionId
        Assert.assertEquals(2, cmmnRuntimeService.createUserEventListenerInstanceQuery().planItemDefinitionId("caseUEL1").count());
        // Test sort Order - using the names because equals is not implemented in UserEventListenerInstance
        List<String> names = events.stream().map(UserEventListenerInstance::getName).sorted(Comparator.reverseOrder()).collect(Collectors.toList());
        List<String> namesDesc = cmmnRuntimeService.createUserEventListenerInstanceQuery().stateAvailable().orderByName().desc().list().stream().map(UserEventListenerInstance::getName).collect(Collectors.toList());
        assertThat(names).isEqualTo(namesDesc);
        // TODO suspended state query (need to suspend the parent stage)
    }

    @Test
    @CmmnDeployment
    public void testUserEventInstanceDeletedWhenNotReferencedByExitSentry() {
        cmmnRuntimeService.createCaseInstanceBuilder().caseDefinitionKey("testUserEvent").start();
        Assert.assertNotNull(cmmnRuntimeService.createUserEventListenerInstanceQuery().singleResult());
        // Completing task A and B completes Stage A.
        // This should also remove the user event listener, as nothing is referencing it anymore
        List<Task> tasks = cmmnTaskService.createTaskQuery().list();
        Assert.assertEquals(2, tasks.size());
        tasks.forEach(( t) -> cmmnTaskService.complete(t.getId()));
        Assert.assertNull(cmmnRuntimeService.createPlanItemInstanceQuery().planItemInstanceName("Stage A").singleResult());
        Assert.assertNotNull(cmmnRuntimeService.createPlanItemInstanceQuery().planItemInstanceName("Stage A").includeEnded().singleResult());
        Assert.assertNull(cmmnRuntimeService.createUserEventListenerInstanceQuery().singleResult());
    }

    @Test
    @CmmnDeployment
    public void testOrphanEventListenerMultipleSentries() {
        // The model here has one user event listener that will trigger the exit of two tasks (A and C) and trigger the activation of task B
        // Verify the model setup
        CaseInstance caseInstance = cmmnRuntimeService.createCaseInstanceBuilder().caseDefinitionKey("testOrphanEventListenerMultipleSentries").start();
        Assert.assertNotNull(cmmnRuntimeService.createUserEventListenerInstanceQuery().caseInstanceId(caseInstance.getId()).singleResult());
        List<Task> tasks = cmmnTaskService.createTaskQuery().caseInstanceId(caseInstance.getId()).orderByTaskName().asc().list();
        Assert.assertEquals(2, tasks.size());
        Assert.assertEquals("A", tasks.get(0).getName());
        Assert.assertEquals("C", tasks.get(1).getName());
        // Completing one tasks should not have impact on the user event listener
        cmmnTaskService.complete(tasks.get(0).getId());
        Assert.assertNotNull(cmmnRuntimeService.createUserEventListenerInstanceQuery().caseInstanceId(caseInstance.getId()).singleResult());
        tasks = cmmnTaskService.createTaskQuery().caseInstanceId(caseInstance.getId()).orderByTaskName().asc().list();
        Assert.assertEquals(1, tasks.size());
        Assert.assertEquals("C", tasks.get(0).getName());
        // Completing the other task with the exit sentry should still keep the user event, as it's reference by the entry of B
        cmmnTaskService.complete(tasks.get(0).getId());
        Assert.assertNotNull(cmmnRuntimeService.createUserEventListenerInstanceQuery().caseInstanceId(caseInstance.getId()).singleResult());
        Assert.assertEquals(0, cmmnTaskService.createTaskQuery().caseInstanceId(caseInstance.getId()).count());
        // Firing the user event listener should start B
        cmmnRuntimeService.completeUserEventListenerInstance(cmmnRuntimeService.createUserEventListenerInstanceQuery().caseInstanceId(caseInstance.getId()).singleResult().getId());
        Assert.assertNull(cmmnRuntimeService.createUserEventListenerInstanceQuery().caseInstanceId(caseInstance.getId()).singleResult());
        Assert.assertEquals(1, cmmnTaskService.createTaskQuery().caseInstanceId(caseInstance.getId()).count());
    }

    @Test
    @CmmnDeployment
    public void testOrphanEventListenerMultipleSentries2() {
        // Firing the event listener should exit A and C, and activate B
        CaseInstance caseInstance = cmmnRuntimeService.createCaseInstanceBuilder().caseDefinitionKey("testOrphanEventListenerMultipleSentries").start();
        cmmnRuntimeService.completeUserEventListenerInstance(cmmnRuntimeService.createUserEventListenerInstanceQuery().caseInstanceId(caseInstance.getId()).singleResult().getId());
        Task task = cmmnTaskService.createTaskQuery().caseInstanceId(caseInstance.getId()).singleResult();
        Assert.assertEquals("B", task.getName());
        cmmnTaskService.complete(task.getId());
        assertCaseInstanceEnded(caseInstance);
    }

    @Test
    @CmmnDeployment
    public void testMultipleEventListenersAsEntry() {
        CaseInstance caseInstance = cmmnRuntimeService.createCaseInstanceBuilder().caseDefinitionKey("testMultipleEventListenersAsEntry").start();
        Assert.assertEquals(0, cmmnTaskService.createTaskQuery().caseInstanceId(caseInstance.getId()).count());
        List<UserEventListenerInstance> userEventListenerInstances = cmmnRuntimeService.createUserEventListenerInstanceQuery().caseInstanceId(caseInstance.getId()).orderByName().asc().list();
        assertThat(userEventListenerInstances).extracting(UserEventListenerInstance::getName).containsExactly("A", "B", "C");
        // Completing A should change nothing
        cmmnRuntimeService.completeUserEventListenerInstance(userEventListenerInstances.get(0).getId());
        Assert.assertEquals(0, cmmnTaskService.createTaskQuery().caseInstanceId(caseInstance.getId()).count());
        userEventListenerInstances = cmmnRuntimeService.createUserEventListenerInstanceQuery().caseInstanceId(caseInstance.getId()).orderByName().asc().list();
        assertThat(userEventListenerInstances).extracting(UserEventListenerInstance::getName).containsExactly("B", "C");
        // Completing B should activate the stage and remove the orphan event listener C
        cmmnRuntimeService.completeUserEventListenerInstance(userEventListenerInstances.get(0).getId());
        Assert.assertEquals(1, cmmnTaskService.createTaskQuery().caseInstanceId(caseInstance.getId()).count());
        Assert.assertEquals(0, cmmnRuntimeService.createUserEventListenerInstanceQuery().caseInstanceId(caseInstance.getId()).count());
    }

    @Test
    @CmmnDeployment
    public void testMultipleEventListenersAsExit() {
        CaseInstance caseInstance = cmmnRuntimeService.createCaseInstanceBuilder().caseDefinitionKey("testMultipleEventListenersAsExit").start();
        Assert.assertEquals(2, cmmnTaskService.createTaskQuery().caseInstanceId(caseInstance.getId()).count());
        List<UserEventListenerInstance> userEventListenerInstances = cmmnRuntimeService.createUserEventListenerInstanceQuery().caseInstanceId(caseInstance.getId()).orderByName().asc().list();
        assertThat(userEventListenerInstances).extracting(UserEventListenerInstance::getName).containsExactly("A", "B", "C");
        // Completing C should also remove A and B
        cmmnRuntimeService.completeUserEventListenerInstance(userEventListenerInstances.get(2).getId());
        Assert.assertEquals(0, cmmnRuntimeService.createUserEventListenerInstanceQuery().caseInstanceId(caseInstance.getId()).count());
        Assert.assertEquals("Outside stage", cmmnTaskService.createTaskQuery().caseInstanceId(caseInstance.getId()).singleResult().getName());
    }

    @Test
    @CmmnDeployment
    public void testMultipleEventListenersMixed() {
        CaseInstance caseInstance = cmmnRuntimeService.createCaseInstanceBuilder().caseDefinitionKey("testMultipleEventListenersMixed").start();
        List<UserEventListenerInstance> userEventListenerInstances = cmmnRuntimeService.createUserEventListenerInstanceQuery().caseInstanceId(caseInstance.getId()).orderByName().asc().list();
        assertThat(userEventListenerInstances).extracting(UserEventListenerInstance::getName).containsExactly("A", "B", "C");
        Assert.assertNull(cmmnTaskService.createTaskQuery().caseInstanceId(caseInstance.getId()).taskName("The task").singleResult());
        // Completing C should make The task active
        cmmnRuntimeService.completeUserEventListenerInstance(userEventListenerInstances.get(2).getId());
        Assert.assertNotNull(cmmnTaskService.createTaskQuery().caseInstanceId(caseInstance.getId()).taskName("The task").singleResult());
        userEventListenerInstances = cmmnRuntimeService.createUserEventListenerInstanceQuery().caseInstanceId(caseInstance.getId()).orderByName().asc().list();
        assertThat(userEventListenerInstances).extracting(UserEventListenerInstance::getName).containsExactly("A", "B");
    }

    @Test
    @CmmnDeployment
    public void testTimerAndUserEventListenerForEntry() {
        CaseInstance caseInstance = cmmnRuntimeService.createCaseInstanceBuilder().caseDefinitionKey("testTimerAndUserEventListenerForEntry").start();
        Assert.assertEquals(4, cmmnRuntimeService.createPlanItemInstanceQuery().caseInstanceId(caseInstance.getId()).count());
        Assert.assertNotNull(cmmnRuntimeService.createPlanItemInstanceQuery().planItemDefinitionType(TIMER_EVENT_LISTENER).singleResult());
        Assert.assertNull(cmmnTaskService.createTaskQuery().taskName("A").singleResult());
        Assert.assertNotNull(cmmnManagementService.createTimerJobQuery().caseInstanceId(caseInstance.getId()).singleResult());
        // Completing the user event listener should terminate the timer event listener
        UserEventListenerInstance userEventListenerInstance = cmmnRuntimeService.createUserEventListenerInstanceQuery().caseInstanceId(caseInstance.getId()).singleResult();
        cmmnRuntimeService.completeUserEventListenerInstance(userEventListenerInstance.getId());
        Assert.assertEquals(2, cmmnRuntimeService.createPlanItemInstanceQuery().caseInstanceId(caseInstance.getId()).count());
        Assert.assertNotNull(cmmnTaskService.createTaskQuery().taskName("A").singleResult());
        Assert.assertNull(cmmnRuntimeService.createPlanItemInstanceQuery().planItemDefinitionType(USER_EVENT_LISTENER).singleResult());
        Assert.assertNull(cmmnRuntimeService.createPlanItemInstanceQuery().planItemDefinitionType(TIMER_EVENT_LISTENER).singleResult());
        Assert.assertNull(cmmnManagementService.createTimerJobQuery().caseInstanceId(caseInstance.getId()).singleResult());
    }

    @Test
    @CmmnDeployment
    public void testUserEventNotUsedForExit() {
        // This case definition has a stage with two tasks. An exit sentry with a user event exists for that stage.
        // This tests checks that, if the stage completes normally, the user event listener is removed as it becomes an orphan
        CaseInstance caseInstance = cmmnRuntimeService.createCaseInstanceBuilder().caseDefinitionKey("testUserEventNotUsedForExit").start();
        assertThat(cmmnRuntimeService.createUserEventListenerInstanceQuery().caseInstanceId(caseInstance.getId()).count()).isEqualTo(1);
        List<Task> tasks = cmmnTaskService.createTaskQuery().caseInstanceId(caseInstance.getId()).orderByTaskName().asc().list();
        assertThat(tasks).extracting(Task::getName).containsExactly("A", "B");
        tasks.forEach(( task) -> cmmnTaskService.complete(task.getId()));
        Task taskC = cmmnTaskService.createTaskQuery().caseInstanceId(caseInstance.getId()).singleResult();
        assertThat(taskC.getName()).isEqualTo("C");
        assertThat(cmmnRuntimeService.createUserEventListenerInstanceQuery().caseInstanceId(caseInstance.getId()).count()).isEqualTo(0);
    }

    @Test
    @CmmnDeployment
    public void testNestedUserEventListener() {
        // This case definition has user event listener connected to a task on the same level and a deeply nested task.
        // The deeply nested task hasn't been made available yet, but the event listener should not be deleted as it could still be made active later
        CaseInstance caseInstance = cmmnRuntimeService.createCaseInstanceBuilder().caseDefinitionKey("testNestedUserEventListener").start();
        assertThat(cmmnRuntimeService.createUserEventListenerInstanceQuery().caseInstanceId(caseInstance.getId()).count()).isEqualTo(1);
        List<Task> tasks = cmmnTaskService.createTaskQuery().caseInstanceId(caseInstance.getId()).orderByTaskName().asc().list();
        assertThat(tasks).extracting(Task::getName).containsExactly("D");
        // Complete the task on the same level should not trigger the deletion of the user event listener
        cmmnTaskService.complete(tasks.get(0).getId());
        assertThat(cmmnRuntimeService.createUserEventListenerInstanceQuery().caseInstanceId(caseInstance.getId()).count()).isEqualTo(1);
        // Setting the variable that guards the stage will make the nested task active
        cmmnRuntimeService.setVariable(caseInstance.getId(), "activateStage", true);
        tasks = cmmnTaskService.createTaskQuery().caseInstanceId(caseInstance.getId()).orderByTaskName().asc().list();
        assertThat(tasks).extracting(Task::getName).containsExactly("A", "B", "C");
        // Completing task C should remove the user event listener
        cmmnTaskService.complete(tasks.get(2).getId());
        assertThat(cmmnRuntimeService.createUserEventListenerInstanceQuery().caseInstanceId(caseInstance.getId()).count()).isEqualTo(0);
    }

    @Test
    @CmmnDeployment
    public void testUserEventListenerForEntryAndExit() {
        CaseInstance caseInstance = cmmnRuntimeService.createCaseInstanceBuilder().caseDefinitionKey("testUserEventListenerForEntryAndExit").start();
        List<UserEventListenerInstance> userEventListenerInstances = cmmnRuntimeService.createUserEventListenerInstanceQuery().caseInstanceId(caseInstance.getId()).orderByName().asc().list();
        assertThat(userEventListenerInstances).extracting(UserEventListenerInstance::getName).containsExactly("EventListenerA", "EventListenerB");
        List<Task> tasks = cmmnTaskService.createTaskQuery().caseInstanceId(caseInstance.getId()).orderByTaskName().asc().list();
        assertThat(tasks).isEmpty();
        // Completing event listener A will activate task A and task B. User event listener B becomes obsolete is gets removed.
        cmmnRuntimeService.completeUserEventListenerInstance(userEventListenerInstances.get(0).getId());
        tasks = cmmnTaskService.createTaskQuery().caseInstanceId(caseInstance.getId()).orderByTaskName().asc().list();
        assertThat(tasks).extracting(Task::getName).containsExactly("Task A", "Task B");
        assertThat(cmmnRuntimeService.createUserEventListenerInstanceQuery().caseInstanceId(caseInstance.getId()).list()).isEmpty();
    }

    @Test
    @CmmnDeployment
    public void testUserEventListenerInNonAutoCompletableStage() {
        CaseInstance caseInstance = cmmnRuntimeService.createCaseInstanceBuilder().caseDefinitionKey("tesUserEventListenerInNonAutoCompletableStage").start();
        UserEventListenerInstance userEventListenerInstance = cmmnRuntimeService.createUserEventListenerInstanceQuery().caseInstanceId(caseInstance.getId()).singleResult();
        cmmnRuntimeService.completeUserEventListenerInstance(userEventListenerInstance.getId());
        List<Task> tasks = cmmnTaskService.createTaskQuery().caseInstanceId(caseInstance.getId()).list();
        for (Task task : tasks) {
            cmmnTaskService.complete(task.getId());
        }
        assertCaseInstanceEnded(caseInstance);
    }

    @Test
    @CmmnDeployment
    public void testUserEventListenerInAutoCompletableStage() {
        CaseInstance caseInstance = cmmnRuntimeService.createCaseInstanceBuilder().caseDefinitionKey("testUserEventListenerInAutoCompletableStage").start();
        // B is the only required now. So completing it, should delete the event listener
        Task task = cmmnTaskService.createTaskQuery().caseInstanceId(caseInstance.getId()).singleResult();
        cmmnTaskService.complete(task.getId());
        assertCaseInstanceEnded(caseInstance);
    }

    @Test
    @CmmnDeployment
    public void testRequiredUserEventListenerInAutoCompletableStage() {
        CaseInstance caseInstance = cmmnRuntimeService.createCaseInstanceBuilder().caseDefinitionKey("testRequiredUserEventListenerInAutoCompletableStage").start();
        UserEventListenerInstance userEventListenerInstance = cmmnRuntimeService.createUserEventListenerInstanceQuery().caseInstanceId(caseInstance.getId()).singleResult();
        cmmnRuntimeService.completeUserEventListenerInstance(userEventListenerInstance.getId());
        assertCaseInstanceEnded(caseInstance);
    }
}

