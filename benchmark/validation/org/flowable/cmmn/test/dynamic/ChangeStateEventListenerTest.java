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


public class ChangeStateEventListenerTest extends FlowableCmmnTestCase {
    protected String oneTaskCaseDeploymentId;

    @Test
    @CmmnDeployment
    public void testChangeHumanTask() {
        CaseInstance caseInstance = cmmnRuntimeService.createCaseInstanceBuilder().caseDefinitionKey("myCase").start();
        Task task = cmmnTaskService.createTaskQuery().caseInstanceId(caseInstance.getId()).singleResult();
        Assert.assertEquals("Task 1", task.getName());
        cmmnRuntimeService.createChangePlanItemStateBuilder().caseInstanceId(caseInstance.getId()).movePlanItemDefinitionIdTo("task1", "task2").changeState();
        List<PlanItemInstance> planItemInstances = cmmnRuntimeService.createPlanItemInstanceQuery().caseInstanceId(caseInstance.getId()).includeEnded().list();
        Assert.assertEquals(3, planItemInstances.size());
        boolean planItem1Found = false;
        boolean planItem2Found = false;
        boolean planItem3Found = false;
        for (PlanItemInstance planItemInstance : planItemInstances) {
            if ("planItem1".equals(planItemInstance.getElementId())) {
                planItem1Found = true;
                Assert.assertEquals(TERMINATED, planItemInstance.getState());
            } else
                if ("planItem2".equals(planItemInstance.getElementId())) {
                    planItem2Found = true;
                    Assert.assertEquals(ACTIVE, planItemInstance.getState());
                } else
                    if ("planItem3".equals(planItemInstance.getElementId())) {
                        planItem3Found = true;
                        Assert.assertEquals(TERMINATED, planItemInstance.getState());
                    }


        }
        Assert.assertTrue(planItem1Found);
        Assert.assertTrue(planItem2Found);
        Assert.assertTrue(planItem3Found);
        task = cmmnTaskService.createTaskQuery().caseInstanceId(caseInstance.getId()).singleResult();
        Assert.assertEquals("Task 2", task.getName());
        cmmnTaskService.complete(task.getId());
        assertCaseInstanceEnded(caseInstance);
    }

    @Test
    @CmmnDeployment
    public void testChangeHumanTaskAndListener() {
        CaseInstance caseInstance = cmmnRuntimeService.createCaseInstanceBuilder().caseDefinitionKey("myCase").start();
        Task task = cmmnTaskService.createTaskQuery().caseInstanceId(caseInstance.getId()).singleResult();
        Assert.assertEquals("Task 1", task.getName());
        PlanItemInstance singlePlanItemInstance = cmmnRuntimeService.createPlanItemInstanceQuery().caseInstanceId(caseInstance.getId()).planItemDefinitionId("eventListener").singleResult();
        Assert.assertNotNull(singlePlanItemInstance);
        Assert.assertEquals(AVAILABLE, singlePlanItemInstance.getState());
        cmmnRuntimeService.completeUserEventListenerInstance(singlePlanItemInstance.getId());
        List<Task> tasks = cmmnTaskService.createTaskQuery().caseInstanceId(caseInstance.getId()).list();
        Assert.assertEquals(2, tasks.size());
        tasks = cmmnTaskService.createTaskQuery().caseInstanceId(caseInstance.getId()).taskDefinitionKey("task1").list();
        Assert.assertEquals(1, tasks.size());
        tasks = cmmnTaskService.createTaskQuery().caseInstanceId(caseInstance.getId()).taskDefinitionKey("task2").list();
        Assert.assertEquals(1, tasks.size());
        cmmnRuntimeService.createChangePlanItemStateBuilder().caseInstanceId(caseInstance.getId()).movePlanItemDefinitionIdTo("task2", "task1").changeState();
        tasks = cmmnTaskService.createTaskQuery().caseInstanceId(caseInstance.getId()).list();
        Assert.assertEquals(2, tasks.size());
        tasks = cmmnTaskService.createTaskQuery().caseInstanceId(caseInstance.getId()).taskDefinitionKey("task1").list();
        Assert.assertEquals(2, tasks.size());
        List<PlanItemInstance> planItemInstances = cmmnRuntimeService.createPlanItemInstanceQuery().caseInstanceId(caseInstance.getId()).includeEnded().list();
        Assert.assertEquals(6, planItemInstances.size());
        int planItem1Found = 0;
        boolean planItem2TerminatedFound = false;
        boolean planItem2AvailableFound = false;
        boolean planItem3CompletedFound = false;
        boolean planItem3AvailableFound = false;
        for (PlanItemInstance planItemInstance : planItemInstances) {
            if ("planItem1".equals(planItemInstance.getElementId())) {
                planItem1Found++;
                Assert.assertEquals(ACTIVE, planItemInstance.getState());
            } else
                if ("planItem2".equals(planItemInstance.getElementId())) {
                    if (TERMINATED.equals(planItemInstance.getState())) {
                        planItem2TerminatedFound = true;
                    } else {
                        Assert.assertEquals(AVAILABLE, planItemInstance.getState());
                        planItem2AvailableFound = true;
                    }
                } else
                    if ("planItem3".equals(planItemInstance.getElementId())) {
                        if (COMPLETED.equals(planItemInstance.getState())) {
                            planItem3CompletedFound = true;
                        } else {
                            Assert.assertEquals(AVAILABLE, planItemInstance.getState());
                            planItem3AvailableFound = true;
                        }
                    }


        }
        Assert.assertEquals(2, planItem1Found);
        Assert.assertTrue(planItem2TerminatedFound);
        Assert.assertTrue(planItem2AvailableFound);
        Assert.assertTrue(planItem3CompletedFound);
        Assert.assertTrue(planItem3AvailableFound);
        // complete task 1 instances
        cmmnTaskService.complete(tasks.get(0).getId());
        cmmnTaskService.complete(tasks.get(1).getId());
        tasks = cmmnTaskService.createTaskQuery().caseInstanceId(caseInstance.getId()).list();
        Assert.assertEquals(1, tasks.size());
        tasks = cmmnTaskService.createTaskQuery().caseInstanceId(caseInstance.getId()).taskDefinitionKey("task2").list();
        Assert.assertEquals(1, tasks.size());
        // complete task 2 instances
        cmmnTaskService.complete(tasks.get(0).getId());
        assertCaseInstanceEnded(caseInstance);
    }
}

