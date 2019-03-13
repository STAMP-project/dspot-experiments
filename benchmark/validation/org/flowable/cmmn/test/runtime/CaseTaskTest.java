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


import PlanItemDefinitionType.CASE_TASK;
import PlanItemDefinitionType.HUMAN_TASK;
import PlanItemDefinitionType.STAGE;
import PlanItemInstanceState.ACTIVE;
import java.util.ArrayList;
import java.util.List;
import org.flowable.cmmn.api.history.HistoricCaseInstance;
import org.flowable.cmmn.api.runtime.CaseInstance;
import org.flowable.cmmn.api.runtime.PlanItemInstance;
import org.flowable.cmmn.engine.test.CmmnDeployment;
import org.flowable.cmmn.engine.test.FlowableCmmnTestCase;
import org.flowable.common.engine.api.FlowableObjectNotFoundException;
import org.flowable.task.api.Task;
import org.flowable.task.api.history.HistoricTaskInstance;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


/**
 *
 *
 * @author Joram Barrez
 */
public class CaseTaskTest extends FlowableCmmnTestCase {
    protected String oneTaskCaseDeploymentId;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    @CmmnDeployment
    public void testBasicBlocking() {
        CaseInstance caseInstance = cmmnRuntimeService.createCaseInstanceBuilder().caseDefinitionKey("myCase").start();
        assertBlockingCaseTaskFlow(caseInstance);
    }

    @Test
    public void testBasicBlockingWithTenant() {
        cmmnRepositoryService.deleteDeployment(oneTaskCaseDeploymentId, true);
        oneTaskCaseDeploymentId = cmmnRepositoryService.createDeployment().tenantId("flowable").addClasspathResource("org/flowable/cmmn/test/runtime/CaseTaskTest.testBasicBlocking.cmmn").addClasspathResource("org/flowable/cmmn/test/runtime/oneTaskCase.cmmn").deploy().getId();
        CaseInstance caseInstance = cmmnRuntimeService.createCaseInstanceBuilder().caseDefinitionKey("myCase").tenantId("flowable").start();
        assertBlockingCaseTaskFlow(caseInstance);
    }

    @Test
    public void testBasicBlockingWithTenantAndGlobalDeployment() {
        cmmnRepositoryService.deleteDeployment(oneTaskCaseDeploymentId, true);
        oneTaskCaseDeploymentId = cmmnRepositoryService.createDeployment().addClasspathResource("org/flowable/cmmn/test/runtime/oneTaskCase.cmmn").deploy().getId();
        String parentCaseDeploymentId = cmmnRepositoryService.createDeployment().tenantId("flowable").addClasspathResource("org/flowable/cmmn/test/runtime/CaseTaskTest.testBasicBlocking.cmmn").deploy().getId();
        try {
            CaseInstance caseInstance = cmmnRuntimeService.createCaseInstanceBuilder().caseDefinitionKey("myCase").tenantId("flowable").start();
            this.expectedException.expect(FlowableObjectNotFoundException.class);
            this.expectedException.expectMessage("Case definition was not found by key 'oneTaskCase' and tenant 'flowable'");
            assertBlockingCaseTaskFlow(caseInstance);
        } finally {
            cmmnRepositoryService.deleteDeployment(parentCaseDeploymentId, true);
        }
    }

    @Test
    @CmmnDeployment(resources = { "org/flowable/cmmn/test/runtime/CaseTaskTest.testSimpleBlockingSubCase.cmmn", "org/flowable/cmmn/test/runtime/CaseTaskTest.testSimpleBlockingSubCaseChildCase.cmmn" })
    public void testSimpleBlockingSubCase() {
        CaseInstance caseInstance = cmmnRuntimeService.createCaseInstanceBuilder().caseDefinitionKey("endEndCase").start();
        // Verify case task plan item instance
        PlanItemInstance caseTaskPlanItemInstance = cmmnRuntimeService.createPlanItemInstanceQuery().planItemDefinitionType(CASE_TASK).singleResult();
        Assert.assertEquals(ACTIVE, caseTaskPlanItemInstance.getState());
        // Verify child case instance
        CaseInstance childCaseInstance = cmmnRuntimeService.createCaseInstanceQuery().caseInstanceParentId(caseInstance.getId()).singleResult();
        PlanItemInstance humanTaskPlanItemInstance = cmmnRuntimeService.createPlanItemInstanceQuery().caseInstanceId(childCaseInstance.getId()).planItemDefinitionType(HUMAN_TASK).singleResult();
        Assert.assertEquals(ACTIVE, humanTaskPlanItemInstance.getState());
        PlanItemInstance stagePlanItemInstance = cmmnRuntimeService.createPlanItemInstanceQuery().caseInstanceId(childCaseInstance.getId()).planItemDefinitionType(STAGE).singleResult();
        Assert.assertEquals(ACTIVE, stagePlanItemInstance.getState());
        // Completing the task should complete both case instances
        cmmnTaskService.complete(cmmnTaskService.createTaskQuery().caseInstanceId(childCaseInstance.getId()).singleResult().getId());
        assertCaseInstanceEnded(childCaseInstance);
        assertCaseInstanceEnded(caseInstance);
    }

    @Test
    @CmmnDeployment
    public void testBasicSubHumanTask() {
        String oneHumanTaskDeploymentId = cmmnRepositoryService.createDeployment().addClasspathResource("org/flowable/cmmn/test/runtime/oneHumanTaskCase.cmmn").deploy().getId();
        try {
            CaseInstance caseInstance = cmmnRuntimeService.createCaseInstanceBuilder().caseDefinitionKey("myCase").start();
            Task taskBeforeSubTask = cmmnTaskService.createTaskQuery().caseInstanceId(caseInstance.getId()).singleResult();
            Assert.assertEquals("The Task", taskBeforeSubTask.getName());
            Task childTask = cmmnTaskService.createTaskQuery().caseInstanceIdWithChildren(caseInstance.getId()).singleResult();
            Assert.assertEquals(taskBeforeSubTask.getId(), childTask.getId());
            cmmnTaskService.complete(taskBeforeSubTask.getId());
            Task taskInSubTask = cmmnTaskService.createTaskQuery().singleResult();
            Assert.assertEquals("Sub task", taskInSubTask.getName());
            childTask = cmmnTaskService.createTaskQuery().caseInstanceIdWithChildren(caseInstance.getId()).singleResult();
            Assert.assertEquals(taskInSubTask.getId(), childTask.getId());
            List<HistoricTaskInstance> childTasks = cmmnHistoryService.createHistoricTaskInstanceQuery().caseInstanceIdWithChildren(caseInstance.getId()).list();
            Assert.assertEquals(2, childTasks.size());
            List<String> taskIds = new ArrayList<>();
            for (HistoricTaskInstance task : childTasks) {
                taskIds.add(task.getId());
            }
            Assert.assertTrue(taskIds.contains(taskBeforeSubTask.getId()));
            Assert.assertTrue(taskIds.contains(taskInSubTask.getId()));
            cmmnTaskService.complete(taskInSubTask.getId());
            Task taskAfterSubTask = cmmnTaskService.createTaskQuery().caseInstanceId(caseInstance.getId()).singleResult();
            Assert.assertEquals("The Task2", taskAfterSubTask.getName());
            childTask = cmmnTaskService.createTaskQuery().caseInstanceIdWithChildren(caseInstance.getId()).singleResult();
            Assert.assertEquals(taskAfterSubTask.getId(), childTask.getId());
            childTasks = cmmnHistoryService.createHistoricTaskInstanceQuery().caseInstanceIdWithChildren(caseInstance.getId()).list();
            Assert.assertEquals(3, childTasks.size());
            taskIds = new ArrayList<>();
            for (HistoricTaskInstance task : childTasks) {
                taskIds.add(task.getId());
            }
            Assert.assertTrue(taskIds.contains(taskBeforeSubTask.getId()));
            Assert.assertTrue(taskIds.contains(taskInSubTask.getId()));
            Assert.assertTrue(taskIds.contains(taskAfterSubTask.getId()));
        } finally {
            cmmnRepositoryService.deleteDeployment(oneHumanTaskDeploymentId, true);
        }
    }

    // Same as testBasicBlocking(), but now with a non-blocking case task
    @Test
    @CmmnDeployment
    public void testBasicNonBlocking() {
        CaseInstance caseInstance = cmmnRuntimeService.createCaseInstanceBuilder().caseDefinitionKey("myCase").start();
        Assert.assertNotNull(caseInstance);
        Assert.assertEquals(1, cmmnRuntimeService.createCaseInstanceQuery().count());
        Assert.assertEquals(0, cmmnHistoryService.createHistoricCaseInstanceQuery().finished().count());
        Assert.assertEquals(1, cmmnHistoryService.createHistoricCaseInstanceQuery().unfinished().count());
        PlanItemInstance planItemInstance = cmmnRuntimeService.createPlanItemInstanceQuery().caseInstanceId(caseInstance.getId()).planItemInstanceState(ACTIVE).singleResult();
        Assert.assertNotNull(planItemInstance);
        // Triggering the task should start the case instance (which is non-blocking -> directly go to task two)
        cmmnRuntimeService.triggerPlanItemInstance(planItemInstance.getId());
        Assert.assertEquals(2, cmmnRuntimeService.createCaseInstanceQuery().count());
        Assert.assertEquals(0, cmmnHistoryService.createHistoricCaseInstanceQuery().finished().count());
        List<PlanItemInstance> planItemInstances = cmmnRuntimeService.createPlanItemInstanceQuery().planItemInstanceState(ACTIVE).orderByName().asc().list();
        Assert.assertEquals(2, planItemInstances.size());
        Assert.assertEquals("Task Two", planItemInstances.get(0).getName());
        Assert.assertEquals("The Task", planItemInstances.get(1).getName());
        cmmnRuntimeService.triggerPlanItemInstance(planItemInstances.get(0).getId());
        Assert.assertEquals(1, cmmnRuntimeService.createCaseInstanceQuery().count());
        Assert.assertEquals(1, cmmnHistoryService.createHistoricCaseInstanceQuery().unfinished().count());
        Assert.assertEquals(1, cmmnHistoryService.createHistoricCaseInstanceQuery().finished().count());
        planItemInstance = cmmnRuntimeService.createPlanItemInstanceQuery().planItemInstanceState(ACTIVE).singleResult();
        Assert.assertEquals("The Task", planItemInstance.getName());
        cmmnRuntimeService.triggerPlanItemInstance(planItemInstance.getId());
        Assert.assertEquals(0, cmmnRuntimeService.createCaseInstanceQuery().count());
        Assert.assertEquals(0, cmmnHistoryService.createHistoricCaseInstanceQuery().unfinished().count());
        Assert.assertEquals(2, cmmnHistoryService.createHistoricCaseInstanceQuery().finished().count());
    }

    @Test
    @CmmnDeployment
    public void testRuntimeServiceTriggerCasePlanItemInstance() {
        cmmnRuntimeService.createCaseInstanceBuilder().caseDefinitionKey("myCase").start();
        Assert.assertEquals(2, cmmnRuntimeService.createCaseInstanceQuery().count());
        Assert.assertEquals(0, cmmnHistoryService.createHistoricCaseInstanceQuery().finished().count());
        Assert.assertEquals(2, cmmnHistoryService.createHistoricCaseInstanceQuery().unfinished().count());
        List<PlanItemInstance> planItemInstances = cmmnRuntimeService.createPlanItemInstanceQuery().planItemInstanceState(ACTIVE).orderByName().asc().list();
        Assert.assertEquals(3, planItemInstances.size());
        Assert.assertEquals("Task One", planItemInstances.get(0).getName());
        Assert.assertEquals("The Case", planItemInstances.get(1).getName());
        Assert.assertEquals("The Task", planItemInstances.get(2).getName());
        // Triggering the planitem of the case should terminate the case and go to task two
        cmmnRuntimeService.triggerPlanItemInstance(planItemInstances.get(1).getId());
        Assert.assertEquals(1, cmmnRuntimeService.createCaseInstanceQuery().count());
        Assert.assertEquals(1, cmmnHistoryService.createHistoricCaseInstanceQuery().finished().count());
        Assert.assertEquals(1, cmmnHistoryService.createHistoricCaseInstanceQuery().unfinished().count());
        planItemInstances = cmmnRuntimeService.createPlanItemInstanceQuery().planItemInstanceState(ACTIVE).orderByName().asc().list();
        Assert.assertEquals(2, planItemInstances.size());
        Assert.assertEquals("Task One", planItemInstances.get(0).getName());
        Assert.assertEquals("Task Two", planItemInstances.get(1).getName());
    }

    @Test
    @CmmnDeployment
    public void testRuntimeServiceTriggerNonBlockingCasePlanItem() {
        CaseInstance caseInstance = cmmnRuntimeService.createCaseInstanceBuilder().caseDefinitionKey("myCase").start();
        Assert.assertEquals(2, cmmnRuntimeService.createCaseInstanceQuery().count());
        Assert.assertEquals(0, cmmnHistoryService.createHistoricCaseInstanceQuery().finished().count());
        Assert.assertEquals(2, cmmnHistoryService.createHistoricCaseInstanceQuery().unfinished().count());
        PlanItemInstance planItemInstance = cmmnRuntimeService.createPlanItemInstanceQuery().caseInstanceId(caseInstance.getId()).planItemInstanceState(ACTIVE).orderByName().asc().singleResult();
        Assert.assertEquals("Task One", planItemInstance.getName());
        // Triggering the task plan item completes the parent case, but the child case remains
        cmmnRuntimeService.triggerPlanItemInstance(planItemInstance.getId());
        Assert.assertEquals(1, cmmnRuntimeService.createCaseInstanceQuery().count());
        Assert.assertEquals(1, cmmnHistoryService.createHistoricCaseInstanceQuery().finished().count());
        Assert.assertEquals(1, cmmnHistoryService.createHistoricCaseInstanceQuery().unfinished().count());
        planItemInstance = cmmnRuntimeService.createPlanItemInstanceQuery().planItemInstanceState(ACTIVE).orderByName().asc().singleResult();
        Assert.assertEquals("The Task", planItemInstance.getName());
        cmmnRuntimeService.triggerPlanItemInstance(planItemInstance.getId());
        Assert.assertEquals(0, cmmnRuntimeService.createCaseInstanceQuery().count());
        Assert.assertEquals(2, cmmnHistoryService.createHistoricCaseInstanceQuery().finished().count());
        Assert.assertEquals(0, cmmnHistoryService.createHistoricCaseInstanceQuery().unfinished().count());
    }

    @Test
    @CmmnDeployment
    public void testTerminateCaseInstanceWithNonBlockingCaseTask() {
        CaseInstance caseInstance = cmmnRuntimeService.createCaseInstanceBuilder().caseDefinitionKey("myCase").start();
        Assert.assertEquals(0, cmmnHistoryService.createHistoricCaseInstanceQuery().finished().count());
        Assert.assertEquals(2, cmmnHistoryService.createHistoricCaseInstanceQuery().unfinished().count());
        PlanItemInstance planItemInstance = cmmnRuntimeService.createPlanItemInstanceQuery().caseInstanceId(caseInstance.getId()).planItemInstanceState(ACTIVE).singleResult();
        Assert.assertEquals("Task One", planItemInstance.getName());
        // Terminating the parent case instance should not terminate the child (it's non-blocking)
        cmmnRuntimeService.terminateCaseInstance(caseInstance.getId());
        Assert.assertEquals(0, cmmnRuntimeService.createCaseInstanceQuery().caseInstanceId(caseInstance.getId()).count());
        Assert.assertEquals(1, cmmnHistoryService.createHistoricCaseInstanceQuery().finished().count());
        Assert.assertEquals(1, cmmnHistoryService.createHistoricCaseInstanceQuery().unfinished().count());
        // Terminate child
        CaseInstance childCaseInstance = cmmnRuntimeService.createCaseInstanceQuery().caseInstanceParentId(caseInstance.getId()).singleResult();
        Assert.assertNotNull(childCaseInstance);
        HistoricCaseInstance historicChildCaseInstance = cmmnHistoryService.createHistoricCaseInstanceQuery().caseInstanceParentId(caseInstance.getId()).singleResult();
        Assert.assertNotNull(historicChildCaseInstance);
        cmmnRuntimeService.terminateCaseInstance(childCaseInstance.getId());
        Assert.assertEquals(2, cmmnHistoryService.createHistoricCaseInstanceQuery().finished().count());
        Assert.assertEquals(0, cmmnHistoryService.createHistoricCaseInstanceQuery().unfinished().count());
    }

    @Test
    @CmmnDeployment
    public void testTerminateCaseInstanceWithNestedCaseTasks() {
        CaseInstance caseInstance = cmmnRuntimeService.createCaseInstanceBuilder().caseDefinitionKey("myCase").start();
        Assert.assertEquals(0, cmmnHistoryService.createHistoricCaseInstanceQuery().finished().count());
        Assert.assertEquals(4, cmmnHistoryService.createHistoricCaseInstanceQuery().unfinished().count());
        cmmnRuntimeService.terminateCaseInstance(caseInstance.getId());
        Assert.assertEquals(4, cmmnHistoryService.createHistoricCaseInstanceQuery().finished().count());
        Assert.assertEquals(0, cmmnHistoryService.createHistoricCaseInstanceQuery().unfinished().count());
    }

    @Test
    @CmmnDeployment
    public void testFallbackToDefaultTenant() {
        CaseInstance caseInstance = cmmnRuntimeService.createCaseInstanceBuilder().caseDefinitionKey("myCase").tenantId("flowable").overrideCaseDefinitionTenantId("flowable").fallbackToDefaultTenant().start();
        assertBlockingCaseTaskFlow(caseInstance);
        Assert.assertEquals("flowable", caseInstance.getTenantId());
    }

    @Test
    @CmmnDeployment(resources = "org/flowable/cmmn/test/runtime/CaseTaskTest.testGlobalFallbackToDefaultTenant.cmmn", tenantId = "defaultFlowable")
    public void testGlobalFallbackToDefaultTenant() {
        String tenantDeploymentId = cmmnRepositoryService.createDeployment().addClasspathResource("org/flowable/cmmn/test/runtime/oneTaskCase.cmmn").tenantId("defaultFlowable").deploy().getId();
        String originalDefaultTenantValue = cmmnEngineConfiguration.getDefaultTenantValue();
        cmmnEngineConfiguration.setFallbackToDefaultTenant(true);
        cmmnEngineConfiguration.setDefaultTenantValue("defaultFlowable");
        try {
            CaseInstance caseInstance = cmmnRuntimeService.createCaseInstanceBuilder().caseDefinitionKey("myCase").tenantId("flowable").overrideCaseDefinitionTenantId("flowable").fallbackToDefaultTenant().start();
            assertBlockingCaseTaskFlow(caseInstance);
            Assert.assertEquals("flowable", caseInstance.getTenantId());
        } finally {
            cmmnEngineConfiguration.setFallbackToDefaultTenant(false);
            cmmnEngineConfiguration.setDefaultTenantValue(originalDefaultTenantValue);
            cmmnRepositoryService.deleteDeployment(tenantDeploymentId, true);
        }
    }

    @Test
    @CmmnDeployment(resources = "org/flowable/cmmn/test/runtime/CaseTaskTest.testGlobalFallbackToDefaultTenant.cmmn", tenantId = "defaultFlowable")
    public void testGlobalFallbackToDefaultTenantNoDefinition() {
        String originalDefaultTenantValue = cmmnEngineConfiguration.getDefaultTenantValue();
        cmmnEngineConfiguration.setFallbackToDefaultTenant(true);
        cmmnEngineConfiguration.setDefaultTenantValue("defaultFlowable");
        try {
            CaseInstance caseInstance = cmmnRuntimeService.createCaseInstanceBuilder().caseDefinitionKey("myCase").tenantId("flowable").overrideCaseDefinitionTenantId("flowable").fallbackToDefaultTenant().start();
            this.expectedException.expect(FlowableObjectNotFoundException.class);
            assertBlockingCaseTaskFlow(caseInstance);
        } finally {
            cmmnEngineConfiguration.setFallbackToDefaultTenant(false);
            cmmnEngineConfiguration.setDefaultTenantValue(originalDefaultTenantValue);
        }
    }

    @Test
    @CmmnDeployment
    public void testFallbackToDefaultTenantFalse() {
        CaseInstance caseInstance = cmmnRuntimeService.createCaseInstanceBuilder().caseDefinitionKey("myCase").tenantId("flowable").overrideCaseDefinitionTenantId("flowable").fallbackToDefaultTenant().start();
        this.expectedException.expect(FlowableObjectNotFoundException.class);
        this.expectedException.expectMessage("Case definition was not found by key 'oneTaskCase' and tenant 'flowable'");
        assertBlockingCaseTaskFlow(caseInstance);
    }

    @Test
    public void testEntityLinksAreDeleted() {
        String deploymentId = cmmnRepositoryService.createDeployment().addClasspathResource("org/flowable/cmmn/test/runtime/oneHumanTaskCase.cmmn").deploy().getId();
        CaseInstance caseInstance = cmmnRuntimeService.createCaseInstanceBuilder().caseDefinitionKey("oneHumanTaskCase").start();
        try {
            Task task = cmmnTaskService.createTaskQuery().caseInstanceId(caseInstance.getId()).singleResult();
            Assert.assertEquals("Sub task", task.getName());
            Assert.assertEquals(1, cmmnRuntimeService.getEntityLinkChildrenForCaseInstance(caseInstance.getId()).size());
            Assert.assertEquals(1, cmmnHistoryService.getHistoricEntityLinkChildrenForCaseInstance(caseInstance.getId()).size());
            cmmnTaskService.complete(task.getId());
            Assert.assertEquals(1, cmmnHistoryService.getHistoricEntityLinkChildrenForCaseInstance(caseInstance.getId()).size());
        } finally {
            cmmnRepositoryService.deleteDeployment(deploymentId, true);
        }
        Assert.assertEquals(0, cmmnHistoryService.getHistoricEntityLinkChildrenForCaseInstance(caseInstance.getId()).size());
    }
}

