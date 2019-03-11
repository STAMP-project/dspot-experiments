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
package org.flowable.cmmn.test.dynamic;


import PlanItemInstanceState.ACTIVE;
import PlanItemInstanceState.AVAILABLE;
import PlanItemInstanceState.COMPLETED;
import PlanItemInstanceState.TERMINATED;
import java.util.List;
import org.flowable.cmmn.api.runtime.CaseInstance;
import org.flowable.cmmn.api.runtime.PlanItemInstance;
import org.flowable.cmmn.engine.test.CmmnDeployment;
import org.flowable.cmmn.engine.test.FlowableCmmnTestCase;
import org.flowable.task.api.Task;
import org.junit.Assert;
import org.junit.Test;


public class ChangeStateTest extends FlowableCmmnTestCase {
    protected String oneTaskCaseDeploymentId;

    @Test
    @CmmnDeployment
    public void testChangeHumanTask() {
        CaseInstance caseInstance = cmmnRuntimeService.createCaseInstanceBuilder().caseDefinitionKey("myCase").start();
        Task task = cmmnTaskService.createTaskQuery().caseInstanceId(caseInstance.getId()).singleResult();
        Assert.assertEquals("Task 1", task.getName());
        cmmnRuntimeService.createChangePlanItemStateBuilder().caseInstanceId(caseInstance.getId()).movePlanItemDefinitionIdTo("task1", "task2").changeState();
        List<PlanItemInstance> planItemInstances = cmmnRuntimeService.createPlanItemInstanceQuery().caseInstanceId(caseInstance.getId()).includeEnded().list();
        Assert.assertEquals(2, planItemInstances.size());
        boolean planItem1Found = false;
        boolean planItem2Found = false;
        for (PlanItemInstance planItemInstance : planItemInstances) {
            if ("planItem1".equals(planItemInstance.getElementId())) {
                planItem1Found = true;
                Assert.assertEquals(TERMINATED, planItemInstance.getState());
            } else
                if ("planItem2".equals(planItemInstance.getElementId())) {
                    planItem2Found = true;
                    Assert.assertEquals(ACTIVE, planItemInstance.getState());
                }

        }
        Assert.assertTrue(planItem1Found);
        Assert.assertTrue(planItem2Found);
        task = cmmnTaskService.createTaskQuery().caseInstanceId(caseInstance.getId()).singleResult();
        Assert.assertEquals("Task 2", task.getName());
        cmmnTaskService.complete(task.getId());
        assertCaseInstanceEnded(caseInstance);
    }

    @Test
    @CmmnDeployment
    public void testChangeHumanTaskInStage() {
        CaseInstance caseInstance = cmmnRuntimeService.createCaseInstanceBuilder().caseDefinitionKey("myCase").start();
        Task task = cmmnTaskService.createTaskQuery().caseInstanceId(caseInstance.getId()).singleResult();
        Assert.assertEquals("Task 1", task.getName());
        cmmnRuntimeService.createChangePlanItemStateBuilder().caseInstanceId(caseInstance.getId()).movePlanItemDefinitionIdTo("task1", "task2").changeState();
        List<PlanItemInstance> planItemInstances = cmmnRuntimeService.createPlanItemInstanceQuery().caseInstanceId(caseInstance.getId()).includeEnded().list();
        Assert.assertEquals(3, planItemInstances.size());
        boolean planItem1Found = false;
        boolean planItem2Found = false;
        boolean stagePlanItemFound = false;
        for (PlanItemInstance planItemInstance : planItemInstances) {
            if ("planItem1".equals(planItemInstance.getElementId())) {
                planItem1Found = true;
                Assert.assertEquals(TERMINATED, planItemInstance.getState());
            } else
                if ("planItem2".equals(planItemInstance.getElementId())) {
                    planItem2Found = true;
                    Assert.assertEquals(ACTIVE, planItemInstance.getState());
                } else
                    if ("planItemStage".equals(planItemInstance.getElementId())) {
                        stagePlanItemFound = true;
                        Assert.assertEquals(ACTIVE, planItemInstance.getState());
                    }


        }
        Assert.assertTrue(planItem1Found);
        Assert.assertTrue(planItem2Found);
        Assert.assertTrue(stagePlanItemFound);
        task = cmmnTaskService.createTaskQuery().caseInstanceId(caseInstance.getId()).singleResult();
        Assert.assertEquals("Task 2", task.getName());
        cmmnTaskService.complete(task.getId());
        assertCaseInstanceEnded(caseInstance);
    }

    @Test
    @CmmnDeployment
    public void testChangeHumanTaskToStage() {
        CaseInstance caseInstance = cmmnRuntimeService.createCaseInstanceBuilder().caseDefinitionKey("myCase").start();
        Task task = cmmnTaskService.createTaskQuery().caseInstanceId(caseInstance.getId()).singleResult();
        Assert.assertEquals("Task 1", task.getName());
        cmmnRuntimeService.createChangePlanItemStateBuilder().caseInstanceId(caseInstance.getId()).movePlanItemDefinitionIdTo("task1", "subTask1").changeState();
        List<PlanItemInstance> planItemInstances = cmmnRuntimeService.createPlanItemInstanceQuery().caseInstanceId(caseInstance.getId()).includeEnded().list();
        Assert.assertEquals(3, planItemInstances.size());
        boolean planItem1Found = false;
        boolean subPlanItem1Found = false;
        boolean stagePlanItemFound = false;
        for (PlanItemInstance planItemInstance : planItemInstances) {
            if ("planItem1".equals(planItemInstance.getElementId())) {
                planItem1Found = true;
                Assert.assertEquals(TERMINATED, planItemInstance.getState());
            } else
                if ("subPlanItem1".equals(planItemInstance.getElementId())) {
                    subPlanItem1Found = true;
                    Assert.assertEquals(ACTIVE, planItemInstance.getState());
                } else
                    if ("planItemStage".equals(planItemInstance.getElementId())) {
                        stagePlanItemFound = true;
                        Assert.assertEquals(ACTIVE, planItemInstance.getState());
                    }


        }
        Assert.assertTrue(planItem1Found);
        Assert.assertTrue(subPlanItem1Found);
        Assert.assertTrue(stagePlanItemFound);
        task = cmmnTaskService.createTaskQuery().caseInstanceId(caseInstance.getId()).singleResult();
        Assert.assertEquals("Sub task 1", task.getName());
        cmmnTaskService.complete(task.getId());
        assertCaseInstanceEnded(caseInstance);
    }

    @Test
    @CmmnDeployment
    public void testChangeHumanTaskFromStage() {
        CaseInstance caseInstance = cmmnRuntimeService.createCaseInstanceBuilder().caseDefinitionKey("myCase").start();
        Task task = cmmnTaskService.createTaskQuery().caseInstanceId(caseInstance.getId()).singleResult();
        cmmnTaskService.complete(task.getId());
        task = cmmnTaskService.createTaskQuery().caseInstanceId(caseInstance.getId()).singleResult();
        Assert.assertEquals("Sub task 1", task.getName());
        cmmnRuntimeService.createChangePlanItemStateBuilder().caseInstanceId(caseInstance.getId()).movePlanItemDefinitionIdTo("subTask1", "task1").changeState();
        List<PlanItemInstance> planItemInstances = cmmnRuntimeService.createPlanItemInstanceQuery().caseInstanceId(caseInstance.getId()).includeEnded().list();
        Assert.assertEquals(5, planItemInstances.size());
        boolean planItem1CompletedFound = false;
        boolean planItem1ActiveFound = false;
        boolean subPlanItem1Found = false;
        boolean stagePlanItemTerminatedFound = false;
        boolean stagePlanItemAvailableFound = false;
        for (PlanItemInstance planItemInstance : planItemInstances) {
            if ("planItem1".equals(planItemInstance.getElementId())) {
                if (COMPLETED.equals(planItemInstance.getState())) {
                    planItem1CompletedFound = true;
                } else {
                    Assert.assertEquals(ACTIVE, planItemInstance.getState());
                    planItem1ActiveFound = true;
                }
            } else
                if ("subPlanItem1".equals(planItemInstance.getElementId())) {
                    subPlanItem1Found = true;
                    Assert.assertEquals(TERMINATED, planItemInstance.getState());
                } else
                    if ("planItemStage".equals(planItemInstance.getElementId())) {
                        if (TERMINATED.equals(planItemInstance.getState())) {
                            stagePlanItemTerminatedFound = true;
                        } else {
                            Assert.assertEquals(AVAILABLE, planItemInstance.getState());
                            stagePlanItemAvailableFound = true;
                        }
                    }


        }
        Assert.assertTrue(planItem1CompletedFound);
        Assert.assertTrue(planItem1ActiveFound);
        Assert.assertTrue(subPlanItem1Found);
        Assert.assertTrue(stagePlanItemTerminatedFound);
        Assert.assertTrue(stagePlanItemAvailableFound);
        task = cmmnTaskService.createTaskQuery().caseInstanceId(caseInstance.getId()).singleResult();
        Assert.assertEquals("Task 1", task.getName());
        cmmnTaskService.complete(task.getId());
        planItemInstances = cmmnRuntimeService.createPlanItemInstanceQuery().caseInstanceId(caseInstance.getId()).includeEnded().list();
        Assert.assertEquals(6, planItemInstances.size());
        planItemInstances = cmmnRuntimeService.createPlanItemInstanceQuery().caseInstanceId(caseInstance.getId()).list();
        Assert.assertEquals(2, planItemInstances.size());
        task = cmmnTaskService.createTaskQuery().caseInstanceId(caseInstance.getId()).singleResult();
        Assert.assertEquals("Sub task 1", task.getName());
        cmmnTaskService.complete(task.getId());
        assertCaseInstanceEnded(caseInstance);
    }
}

