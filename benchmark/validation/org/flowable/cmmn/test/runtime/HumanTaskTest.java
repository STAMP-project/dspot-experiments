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


import EntityLinkType.CHILD;
import HierarchyType.ROOT;
import HistoryLevel.ACTIVITY;
import IdentityLinkType.PARTICIPANT;
import ScopeTypes.CMMN;
import ScopeTypes.TASK;
import java.util.List;
import org.flowable.cmmn.api.repository.CaseDefinition;
import org.flowable.cmmn.api.runtime.CaseInstance;
import org.flowable.cmmn.engine.test.CmmnDeployment;
import org.flowable.cmmn.engine.test.FlowableCmmnTestCase;
import org.flowable.common.engine.impl.identity.Authentication;
import org.flowable.entitylink.api.EntityLink;
import org.flowable.entitylink.api.history.HistoricEntityLink;
import org.flowable.task.api.Task;
import org.flowable.task.api.history.HistoricTaskInstance;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Tijs Rademakers
 * @author Joram Barrez
 */
public class HumanTaskTest extends FlowableCmmnTestCase {
    @Test
    @CmmnDeployment
    public void testHumanTask() {
        Authentication.setAuthenticatedUserId("JohnDoe");
        CaseInstance caseInstance = cmmnRuntimeService.createCaseInstanceBuilder().caseDefinitionKey("myCase").start();
        Assert.assertNotNull(caseInstance);
        Task task = cmmnTaskService.createTaskQuery().caseInstanceId(caseInstance.getId()).singleResult();
        Assert.assertEquals("Task 1", task.getName());
        Assert.assertEquals("JohnDoe", task.getAssignee());
        String task1Id = task.getId();
        List<EntityLink> entityLinks = cmmnRuntimeService.getEntityLinkChildrenForCaseInstance(caseInstance.getId());
        Assert.assertEquals(1, entityLinks.size());
        EntityLink entityLink = entityLinks.get(0);
        Assert.assertEquals(CHILD, entityLink.getLinkType());
        Assert.assertNotNull(entityLink.getCreateTime());
        Assert.assertEquals(caseInstance.getId(), entityLink.getScopeId());
        Assert.assertEquals(CMMN, entityLink.getScopeType());
        Assert.assertNull(entityLink.getScopeDefinitionId());
        Assert.assertEquals(task.getId(), entityLink.getReferenceScopeId());
        Assert.assertEquals(TASK, entityLink.getReferenceScopeType());
        Assert.assertNull(entityLink.getReferenceScopeDefinitionId());
        Assert.assertEquals(ROOT, entityLink.getHierarchyType());
        cmmnTaskService.complete(task.getId());
        task = cmmnTaskService.createTaskQuery().caseInstanceId(caseInstance.getId()).singleResult();
        Assert.assertEquals("Task 2", task.getName());
        Assert.assertNull(task.getAssignee());
        String task2Id = task.getId();
        task = cmmnTaskService.createTaskQuery().taskCandidateGroup("test").caseInstanceId(caseInstance.getId()).singleResult();
        Assert.assertEquals("Task 2", task.getName());
        task = cmmnTaskService.createTaskQuery().taskCandidateUser("test2").caseInstanceId(caseInstance.getId()).singleResult();
        Assert.assertEquals("Task 2", task.getName());
        cmmnTaskService.complete(task.getId());
        Assert.assertEquals(0, cmmnRuntimeService.createCaseInstanceQuery().count());
        if (cmmnEngineConfiguration.getHistoryLevel().isAtLeast(ACTIVITY)) {
            Assert.assertEquals("JohnDoe", cmmnHistoryService.createHistoricVariableInstanceQuery().caseInstanceId(caseInstance.getId()).variableName("var1").singleResult().getValue());
            List<HistoricEntityLink> historicEntityLinks = cmmnHistoryService.getHistoricEntityLinkChildrenForCaseInstance(caseInstance.getId());
            Assert.assertEquals(2, historicEntityLinks.size());
            boolean hasTask1 = false;
            boolean hasTask2 = false;
            for (HistoricEntityLink historicEntityLink : historicEntityLinks) {
                Assert.assertEquals(CHILD, historicEntityLink.getLinkType());
                Assert.assertNotNull(historicEntityLink.getCreateTime());
                Assert.assertEquals(caseInstance.getId(), historicEntityLink.getScopeId());
                Assert.assertEquals(CMMN, historicEntityLink.getScopeType());
                Assert.assertNull(historicEntityLink.getScopeDefinitionId());
                if (task1Id.equals(historicEntityLink.getReferenceScopeId())) {
                    hasTask1 = true;
                }
                if (task2Id.equals(historicEntityLink.getReferenceScopeId())) {
                    hasTask2 = true;
                }
                Assert.assertEquals(TASK, historicEntityLink.getReferenceScopeType());
                Assert.assertNull(historicEntityLink.getReferenceScopeDefinitionId());
                Assert.assertEquals(ROOT, entityLink.getHierarchyType());
            }
            Assert.assertTrue(hasTask1);
            Assert.assertTrue(hasTask2);
        }
        Authentication.setAuthenticatedUserId(null);
    }

    @Test
    public void testCreateHumanTaskUnderTenantByKey() {
        Authentication.setAuthenticatedUserId("JohnDoe");
        org.flowable.cmmn.api.repository.CmmnDeployment deployment = cmmnRepositoryService.createDeployment().tenantId("flowable").addClasspathResource("org/flowable/cmmn/test/runtime/HumanTaskTest.testHumanTask.cmmn").deploy();
        try {
            Assert.assertThat(deployment.getTenantId(), Is.is("flowable"));
            CaseInstance caseInstance = cmmnRuntimeService.createCaseInstanceBuilder().caseDefinitionKey("myCase").tenantId("flowable").start();
            Assert.assertNotNull(caseInstance);
            Assert.assertEquals("flowable", caseInstance.getTenantId());
            Task task = cmmnTaskService.createTaskQuery().caseInstanceId(caseInstance.getId()).singleResult();
            Assert.assertEquals("Task 1", task.getName());
            Assert.assertEquals("JohnDoe", task.getAssignee());
            Assert.assertEquals("flowable", task.getTenantId());
            cmmnTaskService.complete(task.getId());
            task = cmmnTaskService.createTaskQuery().caseInstanceId(caseInstance.getId()).singleResult();
            Assert.assertEquals("Task 2", task.getName());
            Assert.assertEquals("flowable", task.getTenantId());
            cmmnTaskService.complete(task.getId());
            Assert.assertEquals(0, cmmnRuntimeService.createCaseInstanceQuery().count());
        } finally {
            cmmnRepositoryService.deleteDeployment(deployment.getId(), true);
            Authentication.setAuthenticatedUserId(null);
        }
    }

    @Test
    public void testCreateHumanTaskUnderTenantById() {
        Authentication.setAuthenticatedUserId("JohnDoe");
        org.flowable.cmmn.api.repository.CmmnDeployment deployment = cmmnRepositoryService.createDeployment().tenantId("flowable").addClasspathResource("org/flowable/cmmn/test/runtime/HumanTaskTest.testHumanTask.cmmn").deploy();
        try {
            Assert.assertThat(deployment.getTenantId(), Is.is("flowable"));
            CaseDefinition caseDefinition = cmmnRepositoryService.createCaseDefinitionQuery().deploymentId(deployment.getId()).singleResult();
            Assert.assertThat(caseDefinition.getTenantId(), Is.is("flowable"));
            CaseInstance caseInstance = cmmnRuntimeService.createCaseInstanceBuilder().caseDefinitionId(caseDefinition.getId()).tenantId("flowable").start();
            Assert.assertNotNull(caseInstance);
            Assert.assertEquals("flowable", caseInstance.getTenantId());
            Task task = cmmnTaskService.createTaskQuery().caseInstanceId(caseInstance.getId()).singleResult();
            Assert.assertEquals("Task 1", task.getName());
            Assert.assertEquals("JohnDoe", task.getAssignee());
            Assert.assertEquals("flowable", task.getTenantId());
            cmmnTaskService.complete(task.getId());
            task = cmmnTaskService.createTaskQuery().caseInstanceId(caseInstance.getId()).singleResult();
            Assert.assertEquals("flowable", task.getTenantId());
            cmmnTaskService.complete(task.getId());
            Assert.assertEquals(0, cmmnRuntimeService.createCaseInstanceQuery().count());
        } finally {
            cmmnRepositoryService.deleteDeployment(deployment.getId(), true);
            Authentication.setAuthenticatedUserId(null);
        }
    }

    @Test
    @CmmnDeployment
    public void testTaskCompletionExitsStage() {
        CaseInstance caseInstance = cmmnRuntimeService.createCaseInstanceBuilder().caseDefinitionKey("humanTaskCompletionExits").start();
        Assert.assertNotNull(caseInstance);
        List<Task> tasks = cmmnTaskService.createTaskQuery().caseInstanceId(caseInstance.getId()).orderByTaskName().asc().list();
        Assert.assertEquals("A", tasks.get(0).getName());
        Assert.assertEquals("B", tasks.get(1).getName());
        Assert.assertEquals("C", tasks.get(2).getName());
        // Completing A should delete B and C
        cmmnTaskService.complete(tasks.get(0).getId());
        Assert.assertEquals(0, cmmnTaskService.createTaskQuery().caseInstanceId(caseInstance.getId()).count());
        assertCaseInstanceEnded(caseInstance);
        List<HistoricTaskInstance> historicTaskInstances = cmmnHistoryService.createHistoricTaskInstanceQuery().list();
        Assert.assertEquals(3, historicTaskInstances.size());
        for (HistoricTaskInstance historicTaskInstance : historicTaskInstances) {
            Assert.assertNotNull(historicTaskInstance.getStartTime());
            Assert.assertNotNull(historicTaskInstance.getEndTime());
            if (!(historicTaskInstance.getName().equals("A"))) {
                Assert.assertEquals("cmmn-state-transition-terminate-case", historicTaskInstance.getDeleteReason());
            }
        }
        // Completing C should delete B
        CaseInstance caseInstance2 = cmmnRuntimeService.createCaseInstanceBuilder().caseDefinitionKey("humanTaskCompletionExits").start();
        Assert.assertNotNull(caseInstance2);
        tasks = cmmnTaskService.createTaskQuery().caseInstanceId(caseInstance2.getId()).orderByTaskName().asc().list();
        cmmnTaskService.complete(tasks.get(2).getId());
        Task taskA = cmmnTaskService.createTaskQuery().caseInstanceId(caseInstance2.getId()).orderByTaskName().asc().singleResult();
        Assert.assertNotNull(taskA);
        cmmnTaskService.complete(taskA.getId());
        assertCaseInstanceEnded(caseInstance2);
        historicTaskInstances = cmmnHistoryService.createHistoricTaskInstanceQuery().caseInstanceId(caseInstance2.getId()).list();
        Assert.assertEquals(3, historicTaskInstances.size());
        for (HistoricTaskInstance historicTaskInstance : historicTaskInstances) {
            Assert.assertNotNull(historicTaskInstance.getStartTime());
            Assert.assertNotNull(historicTaskInstance.getEndTime());
            if (historicTaskInstance.getName().equals("B")) {
                Assert.assertEquals("cmmn-state-transition-exit", historicTaskInstance.getDeleteReason());
            }
        }
    }

    @Test
    @CmmnDeployment(resources = "org/flowable/cmmn/test/runtime/HumanTaskTest.testHumanTask.cmmn")
    public void addCompleteAuthenticatedUserAsParticipantToParentCase() {
        CaseInstance caseInstance = cmmnRuntimeService.createCaseInstanceBuilder().caseDefinitionKey("myCase").start();
        Assert.assertNotNull(caseInstance);
        Task task = cmmnTaskService.createTaskQuery().caseInstanceId(caseInstance.getId()).singleResult();
        Assert.assertEquals("Task 1", task.getName());
        Assert.assertNull(task.getAssignee());
        Assert.assertEquals(0, cmmnRuntimeService.getIdentityLinksForCaseInstance(caseInstance.getId()).size());
        String prevUserId = Authentication.getAuthenticatedUserId();
        Authentication.setAuthenticatedUserId("JohnDoe");
        try {
            cmmnTaskService.complete(task.getId());
        } finally {
            Authentication.setAuthenticatedUserId(prevUserId);
        }
        Assert.assertEquals(1, cmmnRuntimeService.getIdentityLinksForCaseInstance(caseInstance.getId()).size());
        Assert.assertEquals("JohnDoe", cmmnRuntimeService.getIdentityLinksForCaseInstance(caseInstance.getId()).get(0).getUserId());
        Assert.assertEquals(PARTICIPANT, cmmnRuntimeService.getIdentityLinksForCaseInstance(caseInstance.getId()).get(0).getType());
    }
}

