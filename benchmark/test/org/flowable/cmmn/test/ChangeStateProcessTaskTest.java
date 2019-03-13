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
package org.flowable.cmmn.test;


import PlanItemInstanceState.ACTIVE;
import PlanItemInstanceState.TERMINATED;
import java.util.List;
import org.flowable.cmmn.api.runtime.CaseInstance;
import org.flowable.cmmn.api.runtime.PlanItemInstance;
import org.flowable.cmmn.engine.test.CmmnDeployment;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Tijs Rademakers
 */
public class ChangeStateProcessTaskTest extends AbstractProcessEngineIntegrationTest {
    @Test
    @CmmnDeployment
    public void testActivateProcessTask() {
        CaseInstance caseInstance = startCaseInstanceWithOneTaskProcess("activateFirstTask", false);
        cmmnRuntimeService.createChangePlanItemStateBuilder().caseInstanceId(caseInstance.getId()).activatePlanItemDefinitionId("theProcess").changeState();
        ProcessInstance processInstance = processEngineRuntimeService.createProcessInstanceQuery().singleResult();
        Assert.assertNotNull(processInstance);
        Task task = processEngineTaskService.createTaskQuery().processInstanceId(processInstance.getId()).singleResult();
        Assert.assertEquals("my task", task.getName());
        processEngineTaskService.complete(task.getId());
        Assert.assertEquals(0, cmmnTaskService.createTaskQuery().count());
        Assert.assertEquals(0, processEngineRuntimeService.createProcessInstanceQuery().count());
        Assert.assertEquals(1, cmmnRuntimeService.createCaseInstanceQuery().count());
        cmmnRuntimeService.createChangePlanItemStateBuilder().caseInstanceId(caseInstance.getId()).activatePlanItemDefinitionId("theTask").changeState();
        List<PlanItemInstance> planItemInstances = cmmnRuntimeService.createPlanItemInstanceQuery().caseInstanceId(caseInstance.getId()).planItemInstanceState(ACTIVE).list();
        Assert.assertEquals(1, planItemInstances.size());
        cmmnRuntimeService.triggerPlanItemInstance(planItemInstances.get(0).getId());
        Assert.assertEquals(0, cmmnTaskService.createTaskQuery().count());
        Assert.assertEquals(0, processEngineRuntimeService.createProcessInstanceQuery().count());
        Assert.assertEquals(0, cmmnRuntimeService.createCaseInstanceQuery().count());
    }

    @Test
    @CmmnDeployment(resources = { "org/flowable/cmmn/test/ChangeStateProcessTaskTest.testActivateProcessTask.cmmn" })
    public void testMoveToProcessTask() {
        CaseInstance caseInstance = startCaseInstanceWithOneTaskProcess("activateFirstTask", true);
        cmmnRuntimeService.createChangePlanItemStateBuilder().caseInstanceId(caseInstance.getId()).movePlanItemDefinitionIdTo("theTask", "theProcess").changeState();
        ProcessInstance processInstance = processEngineRuntimeService.createProcessInstanceQuery().singleResult();
        Assert.assertNotNull(processInstance);
        Task task = processEngineTaskService.createTaskQuery().processInstanceId(processInstance.getId()).singleResult();
        Assert.assertEquals("my task", task.getName());
        processEngineTaskService.complete(task.getId());
        Assert.assertEquals(0, cmmnTaskService.createTaskQuery().count());
        Assert.assertEquals(0, processEngineRuntimeService.createProcessInstanceQuery().count());
        Assert.assertEquals(0, cmmnRuntimeService.createCaseInstanceQuery().count());
    }

    @Test
    @CmmnDeployment(resources = { "org/flowable/cmmn/test/ChangeStateProcessTaskTest.testActivateProcessTask.cmmn" })
    public void testActivateProcessTaskWithInitialTask() {
        CaseInstance caseInstance = startCaseInstanceWithOneTaskProcess("activateFirstTask", true);
        List<PlanItemInstance> planItemInstances = cmmnRuntimeService.createPlanItemInstanceQuery().caseInstanceId(caseInstance.getId()).planItemInstanceState(ACTIVE).list();
        Assert.assertEquals(1, planItemInstances.size());
        cmmnRuntimeService.createChangePlanItemStateBuilder().caseInstanceId(caseInstance.getId()).activatePlanItemDefinitionId("theProcess").changeState();
        ProcessInstance processInstance = processEngineRuntimeService.createProcessInstanceQuery().singleResult();
        Assert.assertNotNull(processInstance);
        Task task = processEngineTaskService.createTaskQuery().processInstanceId(processInstance.getId()).singleResult();
        Assert.assertEquals("my task", task.getName());
        cmmnRuntimeService.triggerPlanItemInstance(planItemInstances.get(0).getId());
        processEngineTaskService.complete(task.getId());
        Assert.assertEquals(0, cmmnTaskService.createTaskQuery().count());
        Assert.assertEquals(0, processEngineRuntimeService.createProcessInstanceQuery().count());
        Assert.assertEquals(0, cmmnRuntimeService.createCaseInstanceQuery().count());
    }

    @Test
    @CmmnDeployment
    public void testActivateProcessTaskInStage() {
        CaseInstance caseInstance = startCaseInstanceWithOneTaskProcess("activateStage", false);
        Assert.assertEquals(0, processEngineRuntimeService.createProcessInstanceQuery().count());
        cmmnRuntimeService.createChangePlanItemStateBuilder().caseInstanceId(caseInstance.getId()).activatePlanItemDefinitionId("theProcess").changeState();
        ProcessInstance processInstance = processEngineRuntimeService.createProcessInstanceQuery().singleResult();
        Assert.assertNotNull(processInstance);
        Task task = processEngineTaskService.createTaskQuery().processInstanceId(processInstance.getId()).singleResult();
        Assert.assertEquals("my task", task.getName());
        cmmnRuntimeService.createChangePlanItemStateBuilder().caseInstanceId(caseInstance.getId()).activatePlanItemDefinitionId("theProcess2").changeState();
        Assert.assertEquals(2, processEngineRuntimeService.createProcessInstanceQuery().count());
        Assert.assertEquals(2, processEngineTaskService.createTaskQuery().count());
        processEngineTaskService.complete(task.getId());
        ProcessInstance processInstance2 = processEngineRuntimeService.createProcessInstanceQuery().singleResult();
        Assert.assertNotNull(processInstance2);
        Assert.assertNotEquals(processInstance.getId(), processInstance2.getId());
        task = processEngineTaskService.createTaskQuery().processInstanceId(processInstance2.getId()).singleResult();
        Assert.assertEquals("my task", task.getName());
        Assert.assertEquals(1, processEngineRuntimeService.createProcessInstanceQuery().count());
        Assert.assertEquals(1, processEngineTaskService.createTaskQuery().count());
        processEngineTaskService.complete(task.getId());
        Assert.assertEquals(0, cmmnTaskService.createTaskQuery().count());
        Assert.assertEquals(0, processEngineRuntimeService.createProcessInstanceQuery().count());
        Assert.assertEquals(0, cmmnRuntimeService.createCaseInstanceQuery().count());
    }

    @Test
    @CmmnDeployment(resources = { "org/flowable/cmmn/test/ChangeStateProcessTaskTest.testActivateProcessTaskInStage.cmmn" })
    public void testActivateProcessTaskInStageWithInitialStage() {
        CaseInstance caseInstance = startCaseInstanceWithOneTaskProcess("activateStage", true);
        Assert.assertEquals(1, processEngineRuntimeService.createProcessInstanceQuery().count());
        ProcessInstance processInstance = processEngineRuntimeService.createProcessInstanceQuery().singleResult();
        Assert.assertNotNull(processInstance);
        cmmnRuntimeService.createChangePlanItemStateBuilder().caseInstanceId(caseInstance.getId()).activatePlanItemDefinitionId("theProcess2").changeState();
        Assert.assertEquals(2, processEngineRuntimeService.createProcessInstanceQuery().count());
        Assert.assertEquals(2, processEngineTaskService.createTaskQuery().count());
        Task task = processEngineTaskService.createTaskQuery().processInstanceId(processInstance.getId()).singleResult();
        Assert.assertEquals("my task", task.getName());
        processEngineTaskService.complete(task.getId());
        ProcessInstance processInstance2 = processEngineRuntimeService.createProcessInstanceQuery().singleResult();
        Assert.assertNotNull(processInstance2);
        task = processEngineTaskService.createTaskQuery().processInstanceId(processInstance2.getId()).singleResult();
        Assert.assertEquals("my task", task.getName());
        processEngineTaskService.complete(task.getId());
        Assert.assertEquals(0, cmmnTaskService.createTaskQuery().count());
        Assert.assertEquals(0, processEngineRuntimeService.createProcessInstanceQuery().count());
        Assert.assertEquals(0, cmmnRuntimeService.createCaseInstanceQuery().count());
    }

    @Test
    @CmmnDeployment(resources = { "org/flowable/cmmn/test/ChangeStateProcessTaskTest.testActivateProcessTask.cmmn" })
    public void testActivateProcessTaskWithVariables() {
        CaseInstance caseInstance = startCaseInstanceWithOneTaskProcess("activateFirstTask", true);
        cmmnRuntimeService.createChangePlanItemStateBuilder().caseInstanceId(caseInstance.getId()).activatePlanItemDefinitionId("theProcess").childInstanceTaskVariable("theProcess", "textVar", "Some text").childInstanceTaskVariable("theProcess", "numVar", 10).changeState();
        ProcessInstance processInstance = processEngineRuntimeService.createProcessInstanceQuery().singleResult();
        Assert.assertNotNull(processInstance);
        Assert.assertEquals("Some text", processEngineRuntimeService.getVariable(processInstance.getId(), "textVar"));
        Assert.assertEquals(10, processEngineRuntimeService.getVariable(processInstance.getId(), "numVar"));
        Assert.assertNull(cmmnRuntimeService.getVariable(caseInstance.getId(), "textVar"));
        Assert.assertNull(cmmnRuntimeService.getVariable(caseInstance.getId(), "numVar"));
        Task task = processEngineTaskService.createTaskQuery().processInstanceId(processInstance.getId()).singleResult();
        Assert.assertEquals("my task", task.getName());
        processEngineTaskService.complete(task.getId());
        Assert.assertEquals("Some text", processEngineHistoryService.createHistoricVariableInstanceQuery().processInstanceId(processInstance.getId()).variableName("textVar").singleResult().getValue());
        Assert.assertEquals(10, processEngineHistoryService.createHistoricVariableInstanceQuery().processInstanceId(processInstance.getId()).variableName("numVar").singleResult().getValue());
        Assert.assertNull(cmmnRuntimeService.getVariable(caseInstance.getId(), "textVar"));
        Assert.assertNull(cmmnRuntimeService.getVariable(caseInstance.getId(), "numVar"));
        List<PlanItemInstance> planItemInstances = cmmnRuntimeService.createPlanItemInstanceQuery().caseInstanceId(caseInstance.getId()).planItemInstanceState(ACTIVE).list();
        cmmnRuntimeService.triggerPlanItemInstance(planItemInstances.get(0).getId());
        Assert.assertEquals(0, cmmnTaskService.createTaskQuery().count());
        Assert.assertEquals(0, processEngineRuntimeService.createProcessInstanceQuery().count());
        Assert.assertEquals(0, cmmnRuntimeService.createCaseInstanceQuery().count());
    }

    @Test
    @CmmnDeployment(resources = { "org/flowable/cmmn/test/twoTasksWithProcessTask.cmmn" })
    public void testActivateProcessTaskAndMoveStateWithVariables() {
        CaseInstance caseInstance = startCaseInstanceWithOneTaskProcess("activateFirstTask", true);
        List<PlanItemInstance> planItemInstances = cmmnRuntimeService.createPlanItemInstanceQuery().caseInstanceId(caseInstance.getId()).planItemInstanceState(ACTIVE).list();
        Assert.assertEquals(1, planItemInstances.size());
        Assert.assertEquals("theTask", planItemInstances.get(0).getPlanItemDefinitionId());
        cmmnRuntimeService.createChangePlanItemStateBuilder().caseInstanceId(caseInstance.getId()).movePlanItemDefinitionIdTo("theTask", "theTask2").activatePlanItemDefinitionId("theProcess").childInstanceTaskVariable("theProcess", "textVar", "Some text").childInstanceTaskVariable("theProcess", "numVar", 10).changeState();
        planItemInstances = cmmnRuntimeService.createPlanItemInstanceQuery().caseInstanceId(caseInstance.getId()).planItemInstanceState(ACTIVE).list();
        Assert.assertEquals(2, planItemInstances.size());
        planItemInstances = cmmnRuntimeService.createPlanItemInstanceQuery().caseInstanceId(caseInstance.getId()).planItemInstanceState(ACTIVE).planItemDefinitionId("theProcess").list();
        Assert.assertEquals(1, planItemInstances.size());
        Assert.assertEquals("theProcess", planItemInstances.get(0).getPlanItemDefinitionId());
        planItemInstances = cmmnRuntimeService.createPlanItemInstanceQuery().caseInstanceId(caseInstance.getId()).planItemInstanceState(ACTIVE).planItemDefinitionId("theTask2").list();
        Assert.assertEquals(1, planItemInstances.size());
        Assert.assertEquals("theTask2", planItemInstances.get(0).getPlanItemDefinitionId());
        planItemInstances = cmmnRuntimeService.createPlanItemInstanceQuery().caseInstanceId(caseInstance.getId()).planItemInstanceState(TERMINATED).planItemDefinitionId("theTask").includeEnded().list();
        Assert.assertEquals(1, planItemInstances.size());
        Assert.assertEquals("theTask", planItemInstances.get(0).getPlanItemDefinitionId());
        ProcessInstance processInstance = processEngineRuntimeService.createProcessInstanceQuery().singleResult();
        Assert.assertNotNull(processInstance);
        Assert.assertEquals("Some text", processEngineRuntimeService.getVariable(processInstance.getId(), "textVar"));
        Assert.assertEquals(10, processEngineRuntimeService.getVariable(processInstance.getId(), "numVar"));
        Assert.assertNull(cmmnRuntimeService.getVariable(caseInstance.getId(), "textVar"));
        Assert.assertNull(cmmnRuntimeService.getVariable(caseInstance.getId(), "numVar"));
        Task task = processEngineTaskService.createTaskQuery().processInstanceId(processInstance.getId()).singleResult();
        Assert.assertEquals("my task", task.getName());
        processEngineTaskService.complete(task.getId());
        Assert.assertEquals("Some text", processEngineHistoryService.createHistoricVariableInstanceQuery().processInstanceId(processInstance.getId()).variableName("textVar").singleResult().getValue());
        Assert.assertEquals(10, processEngineHistoryService.createHistoricVariableInstanceQuery().processInstanceId(processInstance.getId()).variableName("numVar").singleResult().getValue());
        Assert.assertNull(cmmnRuntimeService.getVariable(caseInstance.getId(), "textVar"));
        Assert.assertNull(cmmnRuntimeService.getVariable(caseInstance.getId(), "numVar"));
        planItemInstances = cmmnRuntimeService.createPlanItemInstanceQuery().caseInstanceId(caseInstance.getId()).planItemInstanceState(ACTIVE).list();
        Assert.assertEquals(1, planItemInstances.size());
        cmmnRuntimeService.triggerPlanItemInstance(planItemInstances.get(0).getId());
        Assert.assertEquals(0, cmmnTaskService.createTaskQuery().count());
        Assert.assertEquals(0, processEngineRuntimeService.createProcessInstanceQuery().count());
        Assert.assertEquals(0, cmmnRuntimeService.createCaseInstanceQuery().count());
    }

    @Test
    @CmmnDeployment(resources = { "org/flowable/cmmn/test/ChangeStateProcessTaskTest.testActivateProcessTask.cmmn" })
    public void testMoveProcessTaskWithVariables() {
        CaseInstance caseInstance = startCaseInstanceWithOneTaskProcess("activateFirstTask", true);
        cmmnRuntimeService.createChangePlanItemStateBuilder().caseInstanceId(caseInstance.getId()).movePlanItemDefinitionIdTo("theTask", "theProcess").childInstanceTaskVariable("theProcess", "textVar", "Some text").childInstanceTaskVariable("theProcess", "numVar", 10).changeState();
        ProcessInstance processInstance = processEngineRuntimeService.createProcessInstanceQuery().singleResult();
        Assert.assertNotNull(processInstance);
        Assert.assertEquals("Some text", processEngineRuntimeService.getVariable(processInstance.getId(), "textVar"));
        Assert.assertEquals(10, processEngineRuntimeService.getVariable(processInstance.getId(), "numVar"));
        Assert.assertNull(cmmnRuntimeService.getVariable(caseInstance.getId(), "textVar"));
        Assert.assertNull(cmmnRuntimeService.getVariable(caseInstance.getId(), "numVar"));
        Task task = processEngineTaskService.createTaskQuery().processInstanceId(processInstance.getId()).singleResult();
        Assert.assertEquals("my task", task.getName());
        processEngineTaskService.complete(task.getId());
        Assert.assertEquals("Some text", processEngineHistoryService.createHistoricVariableInstanceQuery().processInstanceId(processInstance.getId()).variableName("textVar").singleResult().getValue());
        Assert.assertEquals(10, processEngineHistoryService.createHistoricVariableInstanceQuery().processInstanceId(processInstance.getId()).variableName("numVar").singleResult().getValue());
        Assert.assertEquals(0, cmmnTaskService.createTaskQuery().count());
        Assert.assertEquals(0, processEngineRuntimeService.createProcessInstanceQuery().count());
        Assert.assertEquals(0, cmmnRuntimeService.createCaseInstanceQuery().count());
    }
}

