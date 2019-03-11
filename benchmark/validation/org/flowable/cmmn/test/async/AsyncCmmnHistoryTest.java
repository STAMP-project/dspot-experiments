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
package org.flowable.cmmn.test.async;


import CaseInstanceState.ACTIVE;
import CaseInstanceState.COMPLETED;
import HistoricTaskLogEntryType.USER_TASK_ASSIGNEE_CHANGED;
import HistoricTaskLogEntryType.USER_TASK_COMPLETED;
import HistoricTaskLogEntryType.USER_TASK_CREATED;
import HistoricTaskLogEntryType.USER_TASK_DUEDATE_CHANGED;
import HistoricTaskLogEntryType.USER_TASK_IDENTITY_LINK_ADDED;
import HistoricTaskLogEntryType.USER_TASK_IDENTITY_LINK_REMOVED;
import HistoricTaskLogEntryType.USER_TASK_NAME_CHANGED;
import HistoricTaskLogEntryType.USER_TASK_OWNER_CHANGED;
import HistoricTaskLogEntryType.USER_TASK_PRIORITY_CHANGED;
import IdentityLinkType.PARTICIPANT;
import PlanItemDefinitionType.HUMAN_TASK;
import PlanItemInstanceState.DISABLED;
import PlanItemInstanceState.ENABLED;
import ScopeTypes.CMMN;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import org.flowable.cmmn.api.history.HistoricCaseInstance;
import org.flowable.cmmn.api.history.HistoricMilestoneInstance;
import org.flowable.cmmn.api.history.HistoricPlanItemInstance;
import org.flowable.cmmn.api.runtime.CaseInstance;
import org.flowable.cmmn.api.runtime.PlanItemDefinitionType;
import org.flowable.cmmn.api.runtime.PlanItemInstance;
import org.flowable.cmmn.api.runtime.UserEventListenerInstance;
import org.flowable.cmmn.engine.impl.util.CommandContextUtil;
import org.flowable.cmmn.engine.test.CmmnDeployment;
import org.flowable.cmmn.test.impl.CustomCmmnConfigurationFlowableTestCase;
import org.flowable.common.engine.impl.interceptor.CommandExecutor;
import org.flowable.entitylink.api.history.HistoricEntityLink;
import org.flowable.entitylink.api.history.HistoricEntityLinkService;
import org.flowable.task.api.Task;
import org.flowable.task.api.history.HistoricTaskInstance;
import org.flowable.task.api.history.HistoricTaskLogEntry;
import org.flowable.task.api.history.HistoricTaskLogEntryBuilder;
import org.flowable.variable.api.history.HistoricVariableInstance;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Joram Barrez
 */
public class AsyncCmmnHistoryTest extends CustomCmmnConfigurationFlowableTestCase {
    @Test
    @CmmnDeployment
    public void testCaseInstanceStartAndEnd() {
        CaseInstance caseInstance = cmmnRuntimeService.createCaseInstanceBuilder().caseDefinitionKey("oneHumanTaskCase").name("someName").businessKey("someBusinessKey").start();
        Assert.assertEquals(0, cmmnHistoryService.createHistoricCaseInstanceQuery().count());
        waitForAsyncHistoryExecutorToProcessAllJobs();
        Assert.assertEquals(1, cmmnHistoryService.createHistoricCaseInstanceQuery().count());
        HistoricCaseInstance historicCaseInstance = cmmnHistoryService.createHistoricCaseInstanceQuery().singleResult();
        Assert.assertEquals(caseInstance.getId(), historicCaseInstance.getId());
        Assert.assertEquals("someName", historicCaseInstance.getName());
        Assert.assertNull(historicCaseInstance.getParentId());
        Assert.assertEquals("someBusinessKey", historicCaseInstance.getBusinessKey());
        Assert.assertEquals(caseInstance.getCaseDefinitionId(), historicCaseInstance.getCaseDefinitionId());
        Assert.assertEquals(ACTIVE, historicCaseInstance.getState());
        Assert.assertNotNull(historicCaseInstance.getStartTime());
        Assert.assertNull(historicCaseInstance.getEndTime());
        Task task = cmmnTaskService.createTaskQuery().caseInstanceId(caseInstance.getId()).singleResult();
        cmmnTaskService.complete(task.getId());
        waitForAsyncHistoryExecutorToProcessAllJobs();
        assertCaseInstanceEnded(caseInstance);
        historicCaseInstance = cmmnHistoryService.createHistoricCaseInstanceQuery().singleResult();
        Assert.assertEquals(caseInstance.getId(), historicCaseInstance.getId());
        Assert.assertEquals("someName", historicCaseInstance.getName());
        Assert.assertNull(historicCaseInstance.getParentId());
        Assert.assertEquals("someBusinessKey", historicCaseInstance.getBusinessKey());
        Assert.assertEquals(caseInstance.getCaseDefinitionId(), historicCaseInstance.getCaseDefinitionId());
        Assert.assertEquals(COMPLETED, historicCaseInstance.getState());
        Assert.assertNotNull(historicCaseInstance.getStartTime());
        Assert.assertNotNull(historicCaseInstance.getEndTime());
    }

    @Test
    @CmmnDeployment
    public void testHistoricCaseInstanceDeleted() {
        CaseInstance caseInstance = cmmnRuntimeService.createCaseInstanceBuilder().caseDefinitionKey("oneHumanTaskCase").name("someName").businessKey("someBusinessKey").variable("test", "test").start();
        Task task = cmmnTaskService.createTaskQuery().caseInstanceId(caseInstance.getId()).singleResult();
        cmmnTaskService.complete(task.getId());
        waitForAsyncHistoryExecutorToProcessAllJobs();
        Assert.assertEquals(0, cmmnRuntimeService.createPlanItemInstanceQuery().count());
        Assert.assertEquals(1, cmmnHistoryService.createHistoricCaseInstanceQuery().count());
        cmmnHistoryService.deleteHistoricCaseInstance(caseInstance.getId());
        waitForAsyncHistoryExecutorToProcessAllJobs();
        Assert.assertEquals(0, cmmnHistoryService.createHistoricCaseInstanceQuery().count());
    }

    @Test
    @CmmnDeployment
    public void testMilestoneReached() {
        CaseInstance caseInstance = cmmnRuntimeService.createCaseInstanceBuilder().caseDefinitionKey("caseWithOneMilestone").start();
        Assert.assertEquals(1, cmmnRuntimeService.createMilestoneInstanceQuery().milestoneInstanceCaseInstanceId(caseInstance.getId()).count());
        Assert.assertEquals(0, cmmnHistoryService.createHistoricMilestoneInstanceQuery().milestoneInstanceCaseInstanceId(caseInstance.getId()).count());
        waitForAsyncHistoryExecutorToProcessAllJobs();
        Assert.assertEquals(1, cmmnHistoryService.createHistoricMilestoneInstanceQuery().milestoneInstanceCaseInstanceId(caseInstance.getId()).count());
        HistoricMilestoneInstance historicMilestoneInstance = cmmnHistoryService.createHistoricMilestoneInstanceQuery().milestoneInstanceCaseInstanceId(caseInstance.getId()).singleResult();
        Assert.assertEquals("xyzMilestone", historicMilestoneInstance.getName());
        Assert.assertEquals("milestonePlanItem1", historicMilestoneInstance.getElementId());
        Assert.assertEquals(caseInstance.getId(), historicMilestoneInstance.getCaseInstanceId());
        Assert.assertEquals(caseInstance.getCaseDefinitionId(), historicMilestoneInstance.getCaseDefinitionId());
        Assert.assertNotNull(historicMilestoneInstance.getTimeStamp());
    }

    @Test
    @CmmnDeployment
    public void testIdentityLinks() {
        CaseInstance caseInstance = cmmnRuntimeService.createCaseInstanceBuilder().caseDefinitionKey("caseWithOneMilestone").start();
        cmmnRuntimeService.addUserIdentityLink(caseInstance.getId(), "someUser", PARTICIPANT);
        Assert.assertEquals(0, cmmnHistoryService.getHistoricIdentityLinksForCaseInstance(caseInstance.getId()).size());
        waitForAsyncHistoryExecutorToProcessAllJobs();
        Assert.assertEquals(1, cmmnHistoryService.getHistoricIdentityLinksForCaseInstance(caseInstance.getId()).size());
        cmmnRuntimeService.deleteUserIdentityLink(caseInstance.getId(), "someUser", PARTICIPANT);
        waitForAsyncHistoryExecutorToProcessAllJobs();
        Assert.assertEquals(0, cmmnHistoryService.getHistoricIdentityLinksForCaseInstance(caseInstance.getId()).size());
    }

    @Test
    @CmmnDeployment
    public void testVariables() {
        CaseInstance caseInstance = cmmnRuntimeService.createCaseInstanceBuilder().caseDefinitionKey("oneHumanTaskCase").start();
        Assert.assertEquals(0, cmmnHistoryService.createHistoricVariableInstanceQuery().caseInstanceId(caseInstance.getId()).count());
        cmmnRuntimeService.setVariable(caseInstance.getId(), "test", "hello world");
        cmmnRuntimeService.setVariable(caseInstance.getId(), "test2", 2);
        // Create
        waitForAsyncHistoryExecutorToProcessAllJobs();
        Assert.assertEquals(2, cmmnHistoryService.createHistoricVariableInstanceQuery().caseInstanceId(caseInstance.getId()).count());
        HistoricVariableInstance historicVariableInstance = cmmnHistoryService.createHistoricVariableInstanceQuery().caseInstanceId(caseInstance.getId()).variableName("test").singleResult();
        Assert.assertEquals("test", historicVariableInstance.getVariableName());
        Assert.assertEquals(caseInstance.getId(), historicVariableInstance.getScopeId());
        Assert.assertEquals(CMMN, historicVariableInstance.getScopeType());
        Assert.assertEquals("hello world", historicVariableInstance.getValue());
        Assert.assertNotNull(historicVariableInstance.getCreateTime());
        Assert.assertNotNull(historicVariableInstance.getLastUpdatedTime());
        historicVariableInstance = cmmnHistoryService.createHistoricVariableInstanceQuery().caseInstanceId(caseInstance.getId()).variableName("test2").singleResult();
        Assert.assertEquals("test2", historicVariableInstance.getVariableName());
        Assert.assertEquals(caseInstance.getId(), historicVariableInstance.getScopeId());
        Assert.assertNull(historicVariableInstance.getSubScopeId());
        Assert.assertEquals(CMMN, historicVariableInstance.getScopeType());
        Assert.assertEquals(2, historicVariableInstance.getValue());
        Assert.assertNotNull(historicVariableInstance.getCreateTime());
        Assert.assertNotNull(historicVariableInstance.getLastUpdatedTime());
        // Update
        try {
            Thread.sleep(16);// wait time for diff in last updated time

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        cmmnRuntimeService.setVariable(caseInstance.getId(), "test", "hello test");
        waitForAsyncHistoryExecutorToProcessAllJobs();
        HistoricVariableInstance updatedHistoricVariable = cmmnHistoryService.createHistoricVariableInstanceQuery().caseInstanceId(caseInstance.getId()).variableName("test").singleResult();
        Assert.assertEquals("test", updatedHistoricVariable.getVariableName());
        Assert.assertEquals(caseInstance.getId(), updatedHistoricVariable.getScopeId());
        Assert.assertNull(updatedHistoricVariable.getSubScopeId());
        Assert.assertEquals(CMMN, updatedHistoricVariable.getScopeType());
        Assert.assertEquals("hello test", updatedHistoricVariable.getValue());
        Assert.assertNotNull(updatedHistoricVariable.getCreateTime());
        Assert.assertNotNull(updatedHistoricVariable.getLastUpdatedTime());
        Assert.assertNotEquals(updatedHistoricVariable.getLastUpdatedTime(), historicVariableInstance.getLastUpdatedTime());
        // Delete
        cmmnRuntimeService.removeVariable(caseInstance.getId(), "test");
        waitForAsyncHistoryExecutorToProcessAllJobs();
        Assert.assertNull(cmmnHistoryService.createHistoricVariableInstanceQuery().caseInstanceId(caseInstance.getId()).variableName("test").singleResult());
    }

    @Test
    @CmmnDeployment
    public void testHumanTask() {
        CaseInstance caseInstance = cmmnRuntimeService.createCaseInstanceBuilder().caseDefinitionKey("oneHumanTaskCase").start();
        Assert.assertEquals(1, cmmnTaskService.createTaskQuery().caseInstanceId(caseInstance.getId()).count());
        Assert.assertEquals(0, cmmnHistoryService.createHistoricTaskInstanceQuery().caseInstanceId(caseInstance.getId()).count());
        waitForAsyncHistoryExecutorToProcessAllJobs();
        Assert.assertEquals(1, cmmnHistoryService.createHistoricTaskInstanceQuery().caseInstanceId(caseInstance.getId()).count());
        // Create
        HistoricTaskInstance historicTaskInstance = cmmnHistoryService.createHistoricTaskInstanceQuery().caseInstanceId(caseInstance.getId()).singleResult();
        Assert.assertEquals("The Task", historicTaskInstance.getName());
        Assert.assertEquals("johnDoe", historicTaskInstance.getAssignee());
        Assert.assertEquals(caseInstance.getId(), historicTaskInstance.getScopeId());
        Assert.assertEquals(caseInstance.getCaseDefinitionId(), historicTaskInstance.getScopeDefinitionId());
        Assert.assertEquals(CMMN, historicTaskInstance.getScopeType());
        Assert.assertNotNull(historicTaskInstance.getCreateTime());
        // Update
        cmmnTaskService.setAssignee(historicTaskInstance.getId(), "janeDoe");
        waitForAsyncHistoryExecutorToProcessAllJobs();
        historicTaskInstance = cmmnHistoryService.createHistoricTaskInstanceQuery().caseInstanceId(caseInstance.getId()).singleResult();
        Assert.assertEquals("The Task", historicTaskInstance.getName());
        Assert.assertEquals("janeDoe", historicTaskInstance.getAssignee());
        cmmnTaskService.setPriority(historicTaskInstance.getId(), 99);
        waitForAsyncHistoryExecutorToProcessAllJobs();
        historicTaskInstance = cmmnHistoryService.createHistoricTaskInstanceQuery().caseInstanceId(caseInstance.getId()).singleResult();
        Assert.assertEquals(99, historicTaskInstance.getPriority());
        Assert.assertNull(historicTaskInstance.getEndTime());
        List<PlanItemInstance> planItemInstances = cmmnRuntimeService.createPlanItemInstanceQuery().planItemDefinitionType(HUMAN_TASK).orderByName().asc().list();
        assertThat(planItemInstances).extracting(PlanItemInstance::getName).containsExactly("The Task");
        assertThat(planItemInstances).extracting(PlanItemInstance::getCreateTime).isNotNull();
        // Complete
        Task task = cmmnTaskService.createTaskQuery().caseInstanceId(caseInstance.getId()).singleResult();
        cmmnTaskService.complete(task.getId());
        Assert.assertEquals(0, cmmnTaskService.createTaskQuery().caseInstanceId(caseInstance.getId()).count());
        waitForAsyncHistoryExecutorToProcessAllJobs();
        historicTaskInstance = cmmnHistoryService.createHistoricTaskInstanceQuery().caseInstanceId(caseInstance.getId()).singleResult();
        Assert.assertNotNull(historicTaskInstance.getEndTime());
        List<HistoricPlanItemInstance> historicPlanItemInstances = cmmnHistoryService.createHistoricPlanItemInstanceQuery().planItemInstanceDefinitionType(HUMAN_TASK).list();
        assertThat(historicPlanItemInstances).extracting(HistoricPlanItemInstance::getName).containsExactly("The Task");
        assertThat(historicPlanItemInstances).extracting(HistoricPlanItemInstance::getCreateTime).isNotNull();
    }

    @Test
    @CmmnDeployment
    public void testPlanItemInstances() {
        CaseInstance caseInstance = cmmnRuntimeService.createCaseInstanceBuilder().caseDefinitionKey("testSimpleCaseFlow").start();
        List<PlanItemInstance> currentPlanItemInstances = cmmnRuntimeService.createPlanItemInstanceQuery().caseInstanceId(caseInstance.getId()).list();
        Assert.assertEquals(3, currentPlanItemInstances.size());
        Assert.assertEquals(0, cmmnHistoryService.createHistoricPlanItemInstanceQuery().planItemInstanceCaseInstanceId(caseInstance.getId()).count());
        waitForAsyncHistoryExecutorToProcessAllJobs();
        Assert.assertEquals(3, cmmnHistoryService.createHistoricPlanItemInstanceQuery().planItemInstanceCaseInstanceId(caseInstance.getId()).count());
        List<HistoricPlanItemInstance> historicPlanItemInstances = cmmnHistoryService.createHistoricPlanItemInstanceQuery().planItemInstanceCaseInstanceId(caseInstance.getId()).list();
        Assert.assertTrue(historicPlanItemInstances.stream().map(HistoricPlanItemInstance::getPlanItemDefinitionType).anyMatch(PlanItemDefinitionType.STAGE::equalsIgnoreCase));
        Assert.assertTrue(historicPlanItemInstances.stream().map(HistoricPlanItemInstance::getPlanItemDefinitionType).anyMatch(PlanItemDefinitionType.MILESTONE::equalsIgnoreCase));
        Assert.assertTrue(historicPlanItemInstances.stream().anyMatch(( h) -> ("task".equalsIgnoreCase(h.getPlanItemDefinitionType())) && ("planItemTaskA".equalsIgnoreCase(h.getElementId()))));
        for (HistoricPlanItemInstance historicPlanItemInstance : historicPlanItemInstances) {
            Assert.assertEquals(caseInstance.getId(), historicPlanItemInstance.getCaseInstanceId());
            Assert.assertEquals(caseInstance.getCaseDefinitionId(), historicPlanItemInstance.getCaseDefinitionId());
            Assert.assertNotNull(historicPlanItemInstance.getElementId());
            Assert.assertNotNull(historicPlanItemInstance.getCreateTime());
            Assert.assertNotNull(historicPlanItemInstance.getLastAvailableTime());
            Assert.assertNull(historicPlanItemInstance.getEndedTime());
            Assert.assertNull(historicPlanItemInstance.getLastDisabledTime());
            Assert.assertNull(historicPlanItemInstance.getLastSuspendedTime());
            Assert.assertNull(historicPlanItemInstance.getExitTime());
            Assert.assertNull(historicPlanItemInstance.getTerminatedTime());
            Assert.assertNull(historicPlanItemInstance.getEntryCriterionId());
            Assert.assertNull(historicPlanItemInstance.getExitCriterionId());
            if (historicPlanItemInstance.getElementId().equals("planItemTaskA")) {
                Assert.assertNotNull(historicPlanItemInstance.getLastEnabledTime());
            } else {
                Assert.assertNull(historicPlanItemInstance.getLastEnabledTime());
            }
        }
        // Disable task
        PlanItemInstance task = cmmnRuntimeService.createPlanItemInstanceQuery().planItemInstanceElementId("planItemTaskA").singleResult();
        Assert.assertNotNull(task);
        cmmnRuntimeService.disablePlanItemInstance(task.getId());
        waitForAsyncHistoryExecutorToProcessAllJobs();
        Assert.assertEquals(0, cmmnManagementService.createHistoryJobQuery().scopeType(CMMN).count());
        Assert.assertEquals(0, cmmnManagementService.createDeadLetterJobQuery().scopeType(CMMN).count());
        HistoricPlanItemInstance historicPlanItemInstance = cmmnHistoryService.createHistoricPlanItemInstanceQuery().planItemInstanceId(task.getId()).singleResult();
        Assert.assertEquals(DISABLED, historicPlanItemInstance.getState());
        Assert.assertNotNull(historicPlanItemInstance.getLastEnabledTime());
        Assert.assertNotNull(historicPlanItemInstance.getLastDisabledTime());
        Assert.assertNotNull(historicPlanItemInstance.getLastAvailableTime());
        Assert.assertNull(historicPlanItemInstance.getLastStartedTime());
        Assert.assertNull(historicPlanItemInstance.getEndedTime());
        Assert.assertNull(historicPlanItemInstance.getLastSuspendedTime());
        Assert.assertNull(historicPlanItemInstance.getExitTime());
        Assert.assertNull(historicPlanItemInstance.getTerminatedTime());
        Assert.assertNotNull(historicPlanItemInstance.getLastUpdatedTime());
        // Enable task
        cmmnRuntimeService.enablePlanItemInstance(task.getId());
        waitForAsyncHistoryExecutorToProcessAllJobs();
        historicPlanItemInstance = cmmnHistoryService.createHistoricPlanItemInstanceQuery().planItemInstanceId(task.getId()).singleResult();
        Assert.assertEquals(ENABLED, historicPlanItemInstance.getState());
        Assert.assertNotNull(historicPlanItemInstance.getLastEnabledTime());
        Assert.assertNotNull(historicPlanItemInstance.getLastAvailableTime());
        Assert.assertNotNull(historicPlanItemInstance.getLastDisabledTime());
        Assert.assertNull(historicPlanItemInstance.getLastStartedTime());
        Assert.assertNull(historicPlanItemInstance.getEndedTime());
        Assert.assertNull(historicPlanItemInstance.getLastSuspendedTime());
        Assert.assertNull(historicPlanItemInstance.getExitTime());
        Assert.assertNull(historicPlanItemInstance.getTerminatedTime());
        Assert.assertNotNull(historicPlanItemInstance.getLastUpdatedTime());
        // Manually enable
        cmmnRuntimeService.startPlanItemInstance(task.getId());
        waitForAsyncHistoryExecutorToProcessAllJobs();
        historicPlanItemInstance = cmmnHistoryService.createHistoricPlanItemInstanceQuery().planItemInstanceId(task.getId()).singleResult();
        Assert.assertNotNull(historicPlanItemInstance.getLastStartedTime());
        Assert.assertNull(historicPlanItemInstance.getEndedTime());
        // Complete task
        Calendar clockCal = cmmnEngineConfiguration.getClock().getCurrentCalendar();
        clockCal.add(Calendar.HOUR, 1);
        setClockTo(clockCal.getTime());
        cmmnRuntimeService.triggerPlanItemInstance(task.getId());
        waitForAsyncHistoryExecutorToProcessAllJobs();
        HistoricPlanItemInstance completedHistoricPlanItemInstance = cmmnHistoryService.createHistoricPlanItemInstanceQuery().planItemInstanceId(task.getId()).singleResult();
        Assert.assertNotNull(completedHistoricPlanItemInstance.getEndedTime());
        Assert.assertNotNull(completedHistoricPlanItemInstance.getLastEnabledTime());
        Assert.assertNotNull(completedHistoricPlanItemInstance.getLastDisabledTime());
        Assert.assertNotNull(completedHistoricPlanItemInstance.getLastAvailableTime());
        Assert.assertNotNull(completedHistoricPlanItemInstance.getLastStartedTime());
        Assert.assertNull(completedHistoricPlanItemInstance.getLastSuspendedTime());
        Assert.assertNull(completedHistoricPlanItemInstance.getExitTime());
        Assert.assertNull(completedHistoricPlanItemInstance.getTerminatedTime());
        Assert.assertNotNull(completedHistoricPlanItemInstance.getLastUpdatedTime());
        Assert.assertTrue(historicPlanItemInstance.getLastUpdatedTime().before(completedHistoricPlanItemInstance.getLastUpdatedTime()));
        cmmnEngineConfiguration.getClock().reset();
    }

    @Test
    @CmmnDeployment
    public void testCriterionStoredOnPlanItemInstance() {
        CaseInstance caseInstance = cmmnRuntimeService.createCaseInstanceBuilder().caseDefinitionKey("testCriterions").start();
        // Executing the tasks triggers the entry criterion
        Task taskB = cmmnTaskService.createTaskQuery().taskName("B").singleResult();
        cmmnTaskService.complete(taskB.getId());
        Assert.assertEquals("entryA2", cmmnRuntimeService.createPlanItemInstanceQuery().planItemInstanceName("C").singleResult().getEntryCriterionId());
        waitForAsyncHistoryExecutorToProcessAllJobs();
        HistoricPlanItemInstance planItemInstanceC = cmmnHistoryService.createHistoricPlanItemInstanceQuery().planItemInstanceName("C").singleResult();
        Assert.assertEquals("entryA2", planItemInstanceC.getEntryCriterionId());
        Assert.assertNull(planItemInstanceC.getExitCriterionId());
        // Completing  will set the exit criterion
        UserEventListenerInstance userEventListenerInstance = cmmnRuntimeService.createUserEventListenerInstanceQuery().caseInstanceId(caseInstance.getId()).singleResult();
        cmmnRuntimeService.completeUserEventListenerInstance(userEventListenerInstance.getId());
        waitForAsyncHistoryExecutorToProcessAllJobs();
        planItemInstanceC = cmmnHistoryService.createHistoricPlanItemInstanceQuery().planItemInstanceName("C").singleResult();
        Assert.assertEquals("entryA2", planItemInstanceC.getEntryCriterionId());
        Assert.assertEquals("stop", planItemInstanceC.getExitCriterionId());
    }

    @Test
    public void createUserTaskLogEntity() {
        HistoricTaskLogEntryBuilder historicTaskLogEntryBuilder = cmmnHistoryService.createHistoricTaskLogEntryBuilder();
        Date todayDate = new Date();
        historicTaskLogEntryBuilder.taskId("1");
        historicTaskLogEntryBuilder.type("testType");
        historicTaskLogEntryBuilder.userId("testUserId");
        historicTaskLogEntryBuilder.data("testData");
        historicTaskLogEntryBuilder.scopeId("testScopeId");
        historicTaskLogEntryBuilder.scopeType("testScopeType");
        historicTaskLogEntryBuilder.scopeDefinitionId("testDefinitionId");
        historicTaskLogEntryBuilder.subScopeId("testSubScopeId");
        historicTaskLogEntryBuilder.timeStamp(todayDate);
        historicTaskLogEntryBuilder.tenantId("testTenant");
        historicTaskLogEntryBuilder.create();
        HistoricTaskLogEntry historicTaskLogEntry = null;
        try {
            Assert.assertEquals(0L, cmmnHistoryService.createHistoricTaskLogEntryQuery().taskId("1").count());
            waitForAsyncHistoryExecutorToProcessAllJobs();
            Assert.assertEquals(1L, cmmnHistoryService.createHistoricTaskLogEntryQuery().taskId("1").count());
            historicTaskLogEntry = cmmnHistoryService.createHistoricTaskLogEntryQuery().taskId("1").singleResult();
            Assert.assertTrue(((historicTaskLogEntry.getLogNumber()) > 0));
            Assert.assertEquals("1", historicTaskLogEntry.getTaskId());
            Assert.assertEquals("testType", historicTaskLogEntry.getType());
            Assert.assertEquals("testUserId", historicTaskLogEntry.getUserId());
            Assert.assertEquals("testScopeId", historicTaskLogEntry.getScopeId());
            Assert.assertEquals("testScopeType", historicTaskLogEntry.getScopeType());
            Assert.assertEquals("testDefinitionId", historicTaskLogEntry.getScopeDefinitionId());
            Assert.assertEquals("testSubScopeId", historicTaskLogEntry.getSubScopeId());
            Assert.assertEquals("testData", historicTaskLogEntry.getData());
            Assert.assertTrue(((historicTaskLogEntry.getLogNumber()) > 0L));
            Assert.assertNotNull(historicTaskLogEntry.getTimeStamp());
            Assert.assertEquals("testTenant", historicTaskLogEntry.getTenantId());
        } finally {
            if (historicTaskLogEntry != null) {
                cmmnHistoryService.deleteHistoricTaskLogEntry(historicTaskLogEntry.getLogNumber());
                waitForAsyncHistoryExecutorToProcessAllJobs();
            }
        }
    }

    @Test
    public void createCmmnAsynchUserTaskLogEntries() {
        CaseInstance caseInstance = deployAndStartOneHumanTaskCaseModel();
        Task task = cmmnTaskService.createTaskQuery().caseInstanceId(caseInstance.getId()).singleResult();
        task.setName("newName");
        task.setPriority(0);
        cmmnTaskService.saveTask(task);
        cmmnTaskService.setAssignee(task.getId(), "newAssignee");
        cmmnTaskService.setOwner(task.getId(), "newOwner");
        cmmnTaskService.setDueDate(task.getId(), new Date());
        cmmnTaskService.addUserIdentityLink(task.getId(), "testUser", PARTICIPANT);
        cmmnTaskService.addGroupIdentityLink(task.getId(), "testGroup", PARTICIPANT);
        cmmnTaskService.deleteUserIdentityLink(task.getId(), "testUser", PARTICIPANT);
        cmmnTaskService.deleteGroupIdentityLink(task.getId(), "testGroup", PARTICIPANT);
        cmmnTaskService.complete(task.getId());
        Assert.assertEquals(0L, cmmnHistoryService.createHistoricTaskLogEntryQuery().count());
        Assert.assertEquals(10L, cmmnManagementService.createHistoryJobQuery().count());
        waitForAsyncHistoryExecutorToProcessAllJobs();
        Assert.assertEquals(11L, cmmnHistoryService.createHistoricTaskLogEntryQuery().taskId(task.getId()).count());
        Assert.assertEquals(1L, cmmnHistoryService.createHistoricTaskLogEntryQuery().taskId(task.getId()).type(USER_TASK_CREATED.name()).count());
        Assert.assertEquals(1L, cmmnHistoryService.createHistoricTaskLogEntryQuery().taskId(task.getId()).type(USER_TASK_NAME_CHANGED.name()).count());
        Assert.assertEquals(1L, cmmnHistoryService.createHistoricTaskLogEntryQuery().taskId(task.getId()).type(USER_TASK_PRIORITY_CHANGED.name()).count());
        Assert.assertEquals(1L, cmmnHistoryService.createHistoricTaskLogEntryQuery().taskId(task.getId()).type(USER_TASK_ASSIGNEE_CHANGED.name()).count());
        Assert.assertEquals(1L, cmmnHistoryService.createHistoricTaskLogEntryQuery().taskId(task.getId()).type(USER_TASK_OWNER_CHANGED.name()).count());
        Assert.assertEquals(1L, cmmnHistoryService.createHistoricTaskLogEntryQuery().taskId(task.getId()).type(USER_TASK_DUEDATE_CHANGED.name()).count());
        Assert.assertEquals(2L, cmmnHistoryService.createHistoricTaskLogEntryQuery().taskId(task.getId()).type(USER_TASK_IDENTITY_LINK_ADDED.name()).count());
        Assert.assertEquals(2L, cmmnHistoryService.createHistoricTaskLogEntryQuery().taskId(task.getId()).type(USER_TASK_IDENTITY_LINK_REMOVED.name()).count());
        Assert.assertEquals(1L, cmmnHistoryService.createHistoricTaskLogEntryQuery().taskId(task.getId()).type(USER_TASK_COMPLETED.name()).count());
    }

    @Test
    public void deleteAsynchUserTaskLogEntries() {
        CaseInstance caseInstance = deployAndStartOneHumanTaskCaseModel();
        Task task = cmmnTaskService.createTaskQuery().caseInstanceId(caseInstance.getId()).singleResult();
        Assert.assertEquals(0L, cmmnHistoryService.createHistoricTaskLogEntryQuery().count());
        Assert.assertEquals(1L, cmmnManagementService.createHistoryJobQuery().count());
        waitForAsyncHistoryExecutorToProcessAllJobs();
        List<HistoricTaskLogEntry> historicTaskLogEntries = cmmnHistoryService.createHistoricTaskLogEntryQuery().taskId(task.getId()).list();
        Assert.assertEquals(1L, historicTaskLogEntries.size());
        cmmnHistoryService.deleteHistoricTaskLogEntry(historicTaskLogEntries.get(0).getLogNumber());
        Assert.assertEquals(1L, cmmnManagementService.createHistoryJobQuery().count());
        waitForAsyncHistoryExecutorToProcessAllJobs();
        Assert.assertEquals(0L, cmmnHistoryService.createHistoricTaskLogEntryQuery().taskId(task.getId()).count());
    }

    @Test
    @CmmnDeployment
    public void createRootEntityLink() {
        CaseInstance caseInstance = cmmnRuntimeService.createCaseInstanceBuilder().caseDefinitionKey("oneHumanTaskCase").name("someName").businessKey("someBusinessKey").start();
        Assert.assertEquals(0, cmmnHistoryService.createHistoricCaseInstanceQuery().count());
        waitForAsyncHistoryExecutorToProcessAllJobs();
        Assert.assertEquals(1, cmmnHistoryService.createHistoricCaseInstanceQuery().count());
        Task task = cmmnTaskService.createTaskQuery().caseInstanceId(caseInstance.getId()).singleResult();
        cmmnTaskService.complete(task.getId());
        waitForAsyncHistoryExecutorToProcessAllJobs();
        assertCaseInstanceEnded(caseInstance);
        CommandExecutor commandExecutor = cmmnEngine.getCmmnEngineConfiguration().getCommandExecutor();
        List<HistoricEntityLink> entityLinksByScopeIdAndType = commandExecutor.execute(( commandContext) -> {
            HistoricEntityLinkService historicEntityLinkService = CommandContextUtil.getHistoricEntityLinkService(commandContext);
            return historicEntityLinkService.findHistoricEntityLinksByReferenceScopeIdAndType(task.getId(), ScopeTypes.TASK, EntityLinkType.CHILD);
        });
        Assert.assertEquals(1, entityLinksByScopeIdAndType.size());
        Assert.assertEquals("root", entityLinksByScopeIdAndType.get(0).getHierarchyType());
    }
}

