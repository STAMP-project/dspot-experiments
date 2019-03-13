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
package org.flowable.cmmn.test.history;


import PlanItemDefinitionType.HUMAN_TASK;
import PlanItemDefinitionType.STAGE;
import PlanItemDefinitionType.USER_EVENT_LISTENER;
import PlanItemInstanceState.ACTIVE;
import PlanItemInstanceState.ASYNC_ACTIVE;
import PlanItemInstanceState.AVAILABLE;
import PlanItemInstanceState.COMPLETED;
import PlanItemInstanceState.ENABLED;
import PlanItemInstanceState.END_STATES;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import org.flowable.cmmn.api.history.HistoricMilestoneInstance;
import org.flowable.cmmn.api.history.HistoricPlanItemInstance;
import org.flowable.cmmn.api.runtime.CaseInstance;
import org.flowable.cmmn.api.runtime.PlanItemDefinitionType;
import org.flowable.cmmn.api.runtime.PlanItemInstance;
import org.flowable.cmmn.engine.test.CmmnDeployment;
import org.flowable.cmmn.engine.test.FlowableCmmnTestCase;
import org.flowable.task.api.Task;
import org.flowable.variable.api.history.HistoricVariableInstance;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Dennis Federico
 */
public class PlanItemInstanceHistoryServiceTest extends FlowableCmmnTestCase {
    private static Consumer<HistoricPlanItemInstance> assertCreateTimeHistoricPlanItemInstance = ( h) -> {
        Assert.assertNotNull(h.getCreateTime());
        Assert.assertTrue(((h.getCreateTime().getTime()) >= 0L));
        // if (!PlanItemInstanceState.WAITING_FOR_REPETITION.equalsIgnoreCase(h.getState())) {
        // assertNotNull(h.getLastAvailableTime());
        // }
    };

    private static Consumer<HistoricPlanItemInstance> assertStartedTimeHistoricPlanItemInstance = ( h) -> {
        Assert.assertNotNull(h.getLastStartedTime());
        Assert.assertTrue(((h.getLastStartedTime().getTime()) >= (h.getCreateTime().getTime())));
    };

    private static Consumer<HistoricPlanItemInstance> assertEndedTimeHistoricPlanItemInstance = ( h) -> {
        Assert.assertNotNull(h.getEndedTime());
        Assert.assertTrue(((h.getEndedTime().getTime()) >= (h.getLastStartedTime().getTime())));
    };

    private static Consumer<HistoricPlanItemInstance> assertEndStateHistoricPlanItemInstance = ( h) -> {
        Assert.assertNotNull(h.getState());
        Assert.assertTrue(END_STATES.contains(h.getState()));
    };

    private static Consumer<HistoricPlanItemInstance> assertStartedStateHistoricPlanItemInstance = ( h) -> {
        Assert.assertNotNull(h.getState());
        Assert.assertTrue((((ACTIVE.contains(h.getState())) || (ENABLED.contains(h.getState()))) || (ASYNC_ACTIVE.contains(h.getState()))));
    };

    @Test
    @CmmnDeployment
    public void testSimpleCaseFlow() {
        CaseInstance caseInstance = cmmnRuntimeService.createCaseInstanceBuilder().caseDefinitionKey("testSimpleCaseFlow").start();
        // one Task, one Stage, one Milestone
        List<PlanItemInstance> currentPlanItems = cmmnRuntimeService.createPlanItemInstanceQuery().caseInstanceId(caseInstance.getId()).list();
        Assert.assertNotNull(currentPlanItems);
        Assert.assertEquals(3, currentPlanItems.size());
        Assert.assertTrue(currentPlanItems.stream().map(PlanItemInstance::getPlanItemDefinitionType).anyMatch(PlanItemDefinitionType.STAGE::equalsIgnoreCase));
        Assert.assertTrue(currentPlanItems.stream().map(PlanItemInstance::getPlanItemDefinitionType).anyMatch(PlanItemDefinitionType.MILESTONE::equalsIgnoreCase));
        Assert.assertTrue(currentPlanItems.stream().map(PlanItemInstance::getPlanItemDefinitionType).anyMatch("task"::equalsIgnoreCase));
        // Milestone are just another planItem too, so it will appear in the planItemInstance History
        List<HistoricPlanItemInstance> historicPlanItems = cmmnHistoryService.createHistoricPlanItemInstanceQuery().planItemInstanceCaseInstanceId(caseInstance.getId()).list();
        Assert.assertNotNull(historicPlanItems);
        Assert.assertEquals(3, historicPlanItems.size());
        Assert.assertTrue(historicPlanItems.stream().map(HistoricPlanItemInstance::getPlanItemDefinitionType).anyMatch(PlanItemDefinitionType.STAGE::equalsIgnoreCase));
        Assert.assertTrue(historicPlanItems.stream().map(HistoricPlanItemInstance::getPlanItemDefinitionType).anyMatch(PlanItemDefinitionType.MILESTONE::equalsIgnoreCase));
        Assert.assertTrue(historicPlanItems.stream().anyMatch(( h) -> ("task".equalsIgnoreCase(h.getPlanItemDefinitionType())) && ("planItemTaskA".equalsIgnoreCase(h.getElementId()))));
        // Check Start timeStamp within the second of its original creation
        historicPlanItems.forEach(PlanItemInstanceHistoryServiceTest.assertCreateTimeHistoricPlanItemInstance);
        checkHistoryCreateTimestamp(currentPlanItems, historicPlanItems, 1000L);
        // Check activation timestamp... for those "live" instances not in "Waiting" state (i.e. AVAILABLE)
        List<String> nonWaitingPlanInstanceIds = getIdsOfNonWaitingPlanItemInstances(currentPlanItems);
        Assert.assertFalse(nonWaitingPlanInstanceIds.isEmpty());
        List<HistoricPlanItemInstance> filteredHistoricPlanItemInstances = historicPlanItems.stream().filter(( h) -> nonWaitingPlanInstanceIds.contains(h.getId())).collect(Collectors.toList());
        Assert.assertFalse(filteredHistoricPlanItemInstances.isEmpty());
        filteredHistoricPlanItemInstances.forEach(PlanItemInstanceHistoryServiceTest.assertCreateTimeHistoricPlanItemInstance.andThen(PlanItemInstanceHistoryServiceTest.assertStartedTimeHistoricPlanItemInstance).andThen(PlanItemInstanceHistoryServiceTest.assertStartedTimeHistoricPlanItemInstance).andThen(PlanItemInstanceHistoryServiceTest.assertStartedStateHistoricPlanItemInstance));
        // No planItemInstance has "ended" yet, so no historicPlanItemInstance should have endTime timestamp
        historicPlanItems.forEach(( h) -> assertNull(h.getEndedTime()));
        // Milestone history is only filled when the milestone occurs
        List<HistoricMilestoneInstance> historicMilestones = cmmnHistoryService.createHistoricMilestoneInstanceQuery().milestoneInstanceCaseInstanceId(caseInstance.getId()).list();
        Assert.assertNotNull(historicMilestones);
        Assert.assertTrue(historicMilestones.isEmpty());
        // ////////////////////////////////////////////////////////////////
        // Trigger the task to reach the milestone and activate the stage//
        assertCaseInstanceNotEnded(caseInstance);
        PlanItemInstance task = cmmnRuntimeService.createPlanItemInstanceQuery().planItemInstanceElementId("planItemTaskA").singleResult();
        Assert.assertNotNull(task);
        cmmnRuntimeService.triggerPlanItemInstance(task.getId());
        assertCaseInstanceNotEnded(caseInstance);
        // Now there are 2 plan items in a non-final state, a Stage and its containing task (only 1 new planItem)
        currentPlanItems = cmmnRuntimeService.createPlanItemInstanceQuery().caseInstanceId(caseInstance.getId()).list();
        Assert.assertNotNull(currentPlanItems);
        Assert.assertEquals(2, currentPlanItems.size());
        Assert.assertTrue(currentPlanItems.stream().map(PlanItemInstance::getPlanItemDefinitionType).anyMatch(PlanItemDefinitionType.STAGE::equalsIgnoreCase));
        Assert.assertTrue(currentPlanItems.stream().anyMatch(( p) -> ("task".equalsIgnoreCase(p.getPlanItemDefinitionType())) && ("planItemTaskB".equalsIgnoreCase(p.getElementId()))));
        historicPlanItems = cmmnHistoryService.createHistoricPlanItemInstanceQuery().planItemInstanceCaseInstanceId(caseInstance.getId()).list();
        Assert.assertNotNull(historicPlanItems);
        Assert.assertEquals(4, historicPlanItems.size());
        Assert.assertEquals(1L, cmmnHistoryService.createHistoricPlanItemInstanceQuery().planItemInstanceId(historicPlanItems.get(0).getId()).planItemInstanceWithoutTenantId().list().size());
        Assert.assertEquals(1, cmmnHistoryService.createHistoricPlanItemInstanceQuery().planItemInstanceId(historicPlanItems.get(0).getId()).planItemInstanceWithoutTenantId().count());
        // Check start timestamps of newly added timeStamp within the second of its original creation
        checkHistoryCreateTimestamp(currentPlanItems, historicPlanItems, 1000L);
        // Check activationTime if applies and the endTime for not "live" instances
        List<String> livePlanItemInstanceIds = currentPlanItems.stream().map(PlanItemInstance::getId).collect(Collectors.toList());
        Assert.assertFalse(livePlanItemInstanceIds.isEmpty());
        filteredHistoricPlanItemInstances = historicPlanItems.stream().filter(( h) -> !(livePlanItemInstanceIds.contains(h.getId()))).collect(Collectors.toList());
        filteredHistoricPlanItemInstances.forEach(PlanItemInstanceHistoryServiceTest.assertCreateTimeHistoricPlanItemInstance.andThen(PlanItemInstanceHistoryServiceTest.assertStartedTimeHistoricPlanItemInstance).andThen(PlanItemInstanceHistoryServiceTest.assertEndedTimeHistoricPlanItemInstance).andThen(PlanItemInstanceHistoryServiceTest.assertEndStateHistoricPlanItemInstance));
        // Milestone appears now in the MilestoneHistory
        historicMilestones = cmmnHistoryService.createHistoricMilestoneInstanceQuery().milestoneInstanceCaseInstanceId(caseInstance.getId()).list();
        Assert.assertNotNull(historicMilestones);
        Assert.assertEquals(1, historicMilestones.size());
        // ////////////////////////////////////////////////
        // Trigger the last planItem to complete the Case//
        assertCaseInstanceNotEnded(caseInstance);
        task = cmmnRuntimeService.createPlanItemInstanceQuery().planItemInstanceElementId("planItemTaskB").singleResult();
        Assert.assertNotNull(task);
        cmmnRuntimeService.triggerPlanItemInstance(task.getId());
        assertCaseInstanceEnded(caseInstance);
        // History remains
        historicPlanItems = cmmnHistoryService.createHistoricPlanItemInstanceQuery().planItemInstanceCaseInstanceId(caseInstance.getId()).list();
        Assert.assertNotNull(historicPlanItems);
        Assert.assertEquals(4, historicPlanItems.size());
        historicPlanItems.forEach(PlanItemInstanceHistoryServiceTest.assertCreateTimeHistoricPlanItemInstance.andThen(PlanItemInstanceHistoryServiceTest.assertStartedTimeHistoricPlanItemInstance).andThen(PlanItemInstanceHistoryServiceTest.assertEndedTimeHistoricPlanItemInstance).andThen(PlanItemInstanceHistoryServiceTest.assertEndStateHistoricPlanItemInstance));
        historicMilestones = cmmnHistoryService.createHistoricMilestoneInstanceQuery().milestoneInstanceCaseInstanceId(caseInstance.getId()).list();
        Assert.assertNotNull(historicMilestones);
        Assert.assertEquals(1, historicMilestones.size());
    }

    @Test
    @CmmnDeployment
    public void testSimpleStage() {
        setClockFixedToCurrentTime();
        Calendar beforeCaseCalendar = cmmnEngineConfiguration.getClock().getCurrentCalendar();
        beforeCaseCalendar.add(Calendar.HOUR, (-1));
        Date beforeCaseInstance = beforeCaseCalendar.getTime();
        CaseInstance caseInstance = cmmnRuntimeService.createCaseInstanceBuilder().caseDefinitionKey("testSimpleStage").start();
        Calendar afterCaseCalendar = cmmnEngineConfiguration.getClock().getCurrentCalendar();
        afterCaseCalendar.add(Calendar.HOUR, 1);
        Date afterCaseInstance = afterCaseCalendar.getTime();
        // Basic case setup check
        List<HistoricPlanItemInstance> historicPlanItemInstances = cmmnHistoryService.createHistoricPlanItemInstanceQuery().list();
        Assert.assertEquals(2, historicPlanItemInstances.size());
        Assert.assertEquals(1, historicPlanItemInstances.stream().filter(( h) -> PlanItemDefinitionType.STAGE.equals(h.getPlanItemDefinitionType())).count());
        Assert.assertEquals(1, historicPlanItemInstances.stream().filter(( h) -> PlanItemDefinitionType.HUMAN_TASK.equals(h.getPlanItemDefinitionType())).count());
        // Check by different criteria
        Assert.assertEquals(2, cmmnHistoryService.createHistoricPlanItemInstanceQuery().planItemInstanceCaseInstanceId(caseInstance.getId()).createdBefore(afterCaseInstance).createdAfter(beforeCaseInstance).lastAvailableBefore(afterCaseInstance).lastAvailableAfter(beforeCaseInstance).lastStartedBefore(afterCaseInstance).lastStartedAfter(beforeCaseInstance).planItemInstanceState(ACTIVE).count());
        Calendar beforeCompleteCalendar = cmmnEngineConfiguration.getClock().getCurrentCalendar();
        beforeCompleteCalendar.add(Calendar.HOUR, (-1));
        Date beforeComplete = beforeCompleteCalendar.getTime();
        PlanItemInstance planItemTask = cmmnRuntimeService.createPlanItemInstanceQuery().planItemInstanceElementId("planItemTaskA").singleResult();
        Task task = cmmnTaskService.createTaskQuery().subScopeId(planItemTask.getId()).singleResult();
        cmmnTaskService.complete(task.getId());
        Date afterComplete = forwardClock(60000L);
        historicPlanItemInstances = cmmnHistoryService.createHistoricPlanItemInstanceQuery().planItemInstanceCaseInstanceId(caseInstance.getId()).completedBefore(afterComplete).completedAfter(beforeComplete).list();
        Assert.assertEquals(2, historicPlanItemInstances.size());
        historicPlanItemInstances.forEach(( h) -> {
            assertNull(h.getExitTime());
            assertNull(h.getTerminatedTime());
            assertNull(h.getOccurredTime());
            assertNull(h.getLastDisabledTime());
            assertNull(h.getLastEnabledTime());
            assertNull(h.getLastSuspendedTime());
            assertNotNull(h.getCreateTime());
            assertTrue(((h.getCreateTime().getTime()) <= (h.getLastAvailableTime().getTime())));
            assertTrue(((h.getLastAvailableTime().getTime()) <= (h.getCompletedTime().getTime())));
            assertTrue(((h.getCompletedTime().getTime()) <= (h.getEndedTime().getTime())));
        });
        assertCaseInstanceEnded(caseInstance);
    }

    @Test
    @CmmnDeployment
    public void testEntryAndExitPropagate() {
        CaseInstance caseInstance = cmmnRuntimeService.createCaseInstanceBuilder().caseDefinitionKey("testEntryAndExitPropagate").start();
        List<PlanItemInstance> currentPlanItems = cmmnRuntimeService.createPlanItemInstanceQuery().caseInstanceId(caseInstance.getId()).list();
        Assert.assertNotNull(currentPlanItems);
        Assert.assertEquals(3, currentPlanItems.size());
        Assert.assertEquals(1, currentPlanItems.stream().filter(( p) -> PlanItemDefinitionType.STAGE.equalsIgnoreCase(p.getPlanItemDefinitionType())).filter(( p) -> PlanItemInstanceState.AVAILABLE.equalsIgnoreCase(p.getState())).count());
        Assert.assertEquals(2, currentPlanItems.stream().filter(( p) -> PlanItemDefinitionType.USER_EVENT_LISTENER.equalsIgnoreCase(p.getPlanItemDefinitionType())).filter(( p) -> PlanItemInstanceState.AVAILABLE.equalsIgnoreCase(p.getState())).count());
        List<HistoricPlanItemInstance> historicPlanItems = cmmnHistoryService.createHistoricPlanItemInstanceQuery().planItemInstanceCaseInstanceId(caseInstance.getId()).list();
        Assert.assertNotNull(historicPlanItems);
        Assert.assertEquals(3, historicPlanItems.size());
        checkHistoryCreateTimestamp(currentPlanItems, historicPlanItems, 1000L);
        Assert.assertEquals(1, historicPlanItems.stream().filter(( h) -> PlanItemDefinitionType.STAGE.equalsIgnoreCase(h.getPlanItemDefinitionType())).filter(( h) -> PlanItemInstanceState.AVAILABLE.equalsIgnoreCase(h.getState())).filter(( h) -> ((h.getLastAvailableTime()) != null) && ((h.getCreateTime().getTime()) <= (h.getLastAvailableTime().getTime()))).count());
        // QUERY FOR STAGES
        historicPlanItems = cmmnHistoryService.createHistoricPlanItemInstanceQuery().planItemInstanceDefinitionType(STAGE).planItemInstanceState(AVAILABLE).list();
        Assert.assertEquals(1, historicPlanItems.size());
        historicPlanItems.forEach(( h) -> {
            assertNotNull(h.getLastAvailableTime());
            assertTrue(((h.getCreateTime().getTime()) <= (h.getLastAvailableTime().getTime())));
        });
        // QUERY FOR USER_EVENT_LISTENERS
        historicPlanItems = cmmnHistoryService.createHistoricPlanItemInstanceQuery().planItemInstanceDefinitionType(USER_EVENT_LISTENER).planItemInstanceState(AVAILABLE).list();
        Assert.assertEquals(2, historicPlanItems.size());
        historicPlanItems.forEach(( h) -> {
            assertNotNull(h.getLastAvailableTime());
            assertTrue(((h.getCreateTime().getTime()) <= (h.getLastAvailableTime().getTime())));
        });
        // Fulfill stages entryCriteria - keep date marks for query criteria
        Date occurredAfter = setClockFixedToCurrentTime();
        forwardClock(TimeUnit.MINUTES.toMillis(1));
        PlanItemInstance event = cmmnRuntimeService.createPlanItemInstanceQuery().planItemInstanceElementId("planItemStartStageOneEvent").singleResult();
        cmmnRuntimeService.triggerPlanItemInstance(event.getId());
        assertCaseInstanceNotEnded(caseInstance);
        forwardClock(TimeUnit.MINUTES.toMillis(1));
        Date occurredBefore = cmmnEngineConfiguration.getClock().getCurrentTime();
        // A userEventListeners is removed and two human task are instanced
        currentPlanItems = cmmnRuntimeService.createPlanItemInstanceQuery().caseInstanceId(caseInstance.getId()).list();
        Assert.assertNotNull(currentPlanItems);
        Assert.assertEquals(4, currentPlanItems.size());
        // Two more planItemInstances in the history
        historicPlanItems = cmmnHistoryService.createHistoricPlanItemInstanceQuery().planItemInstanceCaseInstanceId(caseInstance.getId()).list();
        Assert.assertNotNull(historicPlanItems);
        Assert.assertEquals(5, historicPlanItems.size());
        checkHistoryCreateTimestamp(currentPlanItems, historicPlanItems, 1000L);
        // Both stages should be active now
        historicPlanItems = cmmnHistoryService.createHistoricPlanItemInstanceQuery().planItemInstanceDefinitionType(STAGE).planItemInstanceState(ACTIVE).list();
        Assert.assertEquals(1, historicPlanItems.size());
        historicPlanItems.forEach(( h) -> {
            assertNotNull(h.getLastStartedTime());
            assertTrue(((h.getCreateTime().getTime()) <= (h.getLastAvailableTime().getTime())));
            assertTrue(((h.getLastAvailableTime().getTime()) <= (h.getLastStartedTime().getTime())));
        });
        // 3 new Human Tasks
        historicPlanItems = cmmnHistoryService.createHistoricPlanItemInstanceQuery().planItemInstanceState(ACTIVE).planItemInstanceDefinitionType(HUMAN_TASK).list();
        Assert.assertEquals(2, historicPlanItems.size());
        historicPlanItems.forEach(( h) -> {
            // These are already started/active, but before that should also have available timestamp
            assertEquals(PlanItemInstanceState.ACTIVE, h.getState());
            assertNotNull(h.getLastAvailableTime());
            assertNotNull(h.getLastStartedTime());
            assertTrue(((h.getCreateTime().getTime()) <= (h.getLastAvailableTime().getTime())));
            assertTrue(((h.getLastAvailableTime().getTime()) <= (h.getLastStartedTime().getTime())));
        });
        // There should be 3 eventListeners the history, two of them "occurred" and one should still be available
        Assert.assertEquals(2, cmmnHistoryService.createHistoricPlanItemInstanceQuery().planItemInstanceDefinitionType(USER_EVENT_LISTENER).count());
        Assert.assertEquals(1, cmmnHistoryService.createHistoricPlanItemInstanceQuery().planItemInstanceDefinitionType(USER_EVENT_LISTENER).planItemInstanceState(AVAILABLE).count());
        historicPlanItems = cmmnHistoryService.createHistoricPlanItemInstanceQuery().planItemInstanceDefinitionType(USER_EVENT_LISTENER).occurredAfter(occurredAfter).occurredBefore(occurredBefore).list();
        Assert.assertEquals(1, historicPlanItems.size());
        historicPlanItems.forEach(( h) -> {
            // These are "completed" planItemInstance with occurred timestamp and ended timestamp
            assertEquals(PlanItemInstanceState.COMPLETED, h.getState());
            assertNotNull(h.getLastAvailableTime());
            assertNotNull(h.getOccurredTime());
            assertNotNull(h.getEndedTime());
            assertTrue(((h.getCreateTime().getTime()) <= (h.getLastAvailableTime().getTime())));
            assertTrue(((h.getLastAvailableTime().getTime()) <= (h.getOccurredTime().getTime())));
            assertTrue(((h.getOccurredTime().getTime()) <= (h.getEndedTime().getTime())));
        });
        // Complete one of the Tasks on stageOne
        Date completedAfter = cmmnEngineConfiguration.getClock().getCurrentTime();
        forwardClock(TimeUnit.MINUTES.toMillis(1));
        PlanItemInstance planItemTask = cmmnRuntimeService.createPlanItemInstanceQuery().planItemInstanceElementId("planItemTaskA").singleResult();
        Task task = cmmnTaskService.createTaskQuery().subScopeId(planItemTask.getId()).singleResult();
        cmmnTaskService.complete(task.getId());
        forwardClock(TimeUnit.MINUTES.toMillis(1));
        Date completedBefore = cmmnEngineConfiguration.getClock().getCurrentTime();
        // one completed task, fetched with completeTime queryCriteria
        HistoricPlanItemInstance historicPlanItem = cmmnHistoryService.createHistoricPlanItemInstanceQuery().completedBefore(completedBefore).completedAfter(completedAfter).singleResult();
        Assert.assertNotNull(historicPlanItem);
        Assert.assertEquals("planItemTaskA", historicPlanItem.getElementId());
        Assert.assertEquals(COMPLETED, historicPlanItem.getState());
        Assert.assertNotNull(historicPlanItem.getLastAvailableTime());
        Assert.assertNotNull(historicPlanItem.getCompletedTime());
        Assert.assertNotNull(historicPlanItem.getEndedTime());
        Assert.assertTrue(((historicPlanItem.getCreateTime().getTime()) <= (historicPlanItem.getLastAvailableTime().getTime())));
        Assert.assertTrue(((historicPlanItem.getLastAvailableTime().getTime()) <= (historicPlanItem.getCompletedTime().getTime())));
        Assert.assertTrue(((historicPlanItem.getCompletedTime().getTime()) <= (historicPlanItem.getEndedTime().getTime())));
        // one task still active
        historicPlanItems = cmmnHistoryService.createHistoricPlanItemInstanceQuery().planItemInstanceState(ACTIVE).planItemInstanceDefinitionType(HUMAN_TASK).list();
        Assert.assertEquals(1, historicPlanItems.size());
        // Trigger exit criteria of stage one
        Date endedAfter = cmmnEngineConfiguration.getClock().getCurrentTime();
        forwardClock(TimeUnit.MINUTES.toMillis(1));
        event = cmmnRuntimeService.createPlanItemInstanceQuery().planItemInstanceElementId("planItemExitStageOneEvent").singleResult();
        cmmnRuntimeService.triggerPlanItemInstance(event.getId());
        forwardClock(TimeUnit.MINUTES.toMillis(1));
        Date endedBefore = cmmnEngineConfiguration.getClock().getCurrentTime();
        // Exit condition should have propagated to the remaining task
        historicPlanItems = cmmnHistoryService.createHistoricPlanItemInstanceQuery().exitBefore(endedBefore).exitAfter(endedAfter).list();
        // The stage and the remaining containing task
        Assert.assertEquals(2, historicPlanItems.size());
        Assert.assertTrue(historicPlanItems.stream().anyMatch(( h) -> PlanItemDefinitionType.STAGE.equalsIgnoreCase(h.getPlanItemDefinitionType())));
        Assert.assertTrue(historicPlanItems.stream().anyMatch(( h) -> PlanItemDefinitionType.HUMAN_TASK.equalsIgnoreCase(h.getPlanItemDefinitionType())));
        historicPlanItems.forEach(( h) -> {
            assertEquals(PlanItemInstanceState.TERMINATED, h.getState());
            assertNotNull(h.getLastAvailableTime());
            assertNotNull(h.getExitTime());
            assertNotNull(h.getEndedTime());
            assertTrue(((h.getCreateTime().getTime()) <= (h.getLastAvailableTime().getTime())));
            assertTrue(((h.getLastAvailableTime().getTime()) <= (h.getExitTime().getTime())));
            assertTrue(((h.getExitTime().getTime()) <= (h.getEndedTime().getTime())));
        });
        assertCaseInstanceEnded(caseInstance);
    }

    @Test
    @CmmnDeployment
    @SuppressWarnings("unchecked")
    public void testSimpleRepetitionHistory() {
        int totalRepetitions = 5;
        CaseInstance caseInstance = cmmnRuntimeService.createCaseInstanceBuilder().caseDefinitionKey("testSimpleRepetitionHistory").variable("totalRepetitions", totalRepetitions).start();
        for (int i = 1; i <= totalRepetitions; i++) {
            PlanItemInstance repeatingTaskPlanItemInstance = cmmnRuntimeService.createPlanItemInstanceQuery().planItemInstanceElementId("repeatingTaskPlanItem").singleResult();
            Assert.assertNotNull(repeatingTaskPlanItemInstance);
            Assert.assertEquals(ACTIVE, repeatingTaskPlanItemInstance.getState());
            // History Before task execution
            List<HistoricPlanItemInstance> historyBefore = cmmnHistoryService.createHistoricPlanItemInstanceQuery().list();
            Map<String, List<HistoricPlanItemInstance>> historyBeforeByState = historyBefore.stream().collect(Collectors.groupingBy(HistoricPlanItemInstance::getState));
            Assert.assertEquals(1, historyBeforeByState.get(ACTIVE).size());
            Assert.assertEquals((i - 1), historyBeforeByState.getOrDefault(COMPLETED, Collections.EMPTY_LIST).size());
            // Sanity check Active planItemInstance
            Assert.assertEquals(repeatingTaskPlanItemInstance.getId(), historyBeforeByState.get(ACTIVE).get(0).getId());
            // Sanity check repetition counter
            HistoricVariableInstance historicRepetitionCounter = cmmnHistoryService.createHistoricVariableInstanceQuery().planItemInstanceId(repeatingTaskPlanItemInstance.getId()).singleResult();
            Assert.assertNotNull(historicRepetitionCounter);
            Assert.assertEquals(i, historicRepetitionCounter.getValue());
            // Execute the repetition
            Task task = cmmnTaskService.createTaskQuery().caseInstanceId(caseInstance.getId()).subScopeId(repeatingTaskPlanItemInstance.getId()).singleResult();
            Assert.assertNotNull(task);
            cmmnTaskService.complete(task.getId());
            // History Before task execution
            List<HistoricPlanItemInstance> historyAfter = cmmnHistoryService.createHistoricPlanItemInstanceQuery().list();
            Map<String, List<HistoricPlanItemInstance>> historyAfterByState = historyAfter.stream().collect(Collectors.groupingBy(HistoricPlanItemInstance::getState));
            Assert.assertEquals((i == totalRepetitions ? 0 : 1), historyAfterByState.getOrDefault(ACTIVE, Collections.EMPTY_LIST).size());
            Assert.assertEquals(i, historyAfterByState.getOrDefault(COMPLETED, Collections.EMPTY_LIST).size());
        }
        // Check history in sequence
        List<HistoricPlanItemInstance> history = cmmnHistoryService.createHistoricPlanItemInstanceQuery().list();
        history.sort(( o1, o2) -> {
            int order1 = ((int) (cmmnHistoryService.createHistoricVariableInstanceQuery().planItemInstanceId(o1.getId()).singleResult().getValue()));
            int order2 = ((int) (cmmnHistoryService.createHistoricVariableInstanceQuery().planItemInstanceId(o2.getId()).singleResult().getValue()));
            return Integer.compare(order1, order2);
        });
        long previousCreateTime = 0L;
        long previousActivateTime = 0L;
        long previousEndTime = 0L;
        for (HistoricPlanItemInstance h : history) {
            PlanItemInstanceHistoryServiceTest.assertCreateTimeHistoricPlanItemInstance.andThen(PlanItemInstanceHistoryServiceTest.assertStartedTimeHistoricPlanItemInstance).andThen(PlanItemInstanceHistoryServiceTest.assertEndedTimeHistoricPlanItemInstance).andThen(PlanItemInstanceHistoryServiceTest.assertEndStateHistoricPlanItemInstance).accept(h);
            Assert.assertTrue((previousCreateTime <= (h.getCreateTime().getTime())));
            Assert.assertTrue((previousActivateTime <= (h.getLastStartedTime().getTime())));
            Assert.assertTrue((previousEndTime <= (h.getEndedTime().getTime())));
            previousCreateTime = h.getCreateTime().getTime();
            previousActivateTime = h.getLastStartedTime().getTime();
            previousEndTime = h.getEndedTime().getTime();
        }
        assertCaseInstanceEnded(caseInstance);
    }
}

