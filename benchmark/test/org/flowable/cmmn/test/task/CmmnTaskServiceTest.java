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
package org.flowable.cmmn.test.task;


import HierarchyType.ROOT;
import HistoryLevel.ACTIVITY;
import ScopeTypes.CMMN;
import java.util.Collections;
import java.util.List;
import org.flowable.cmmn.api.history.HistoricCaseInstance;
import org.flowable.cmmn.api.runtime.CaseInstance;
import org.flowable.cmmn.api.runtime.PlanItemInstance;
import org.flowable.cmmn.engine.impl.util.CommandContextUtil;
import org.flowable.cmmn.engine.test.CmmnDeployment;
import org.flowable.cmmn.engine.test.FlowableCmmnTestCase;
import org.flowable.common.engine.api.FlowableException;
import org.flowable.common.engine.impl.interceptor.CommandExecutor;
import org.flowable.entitylink.api.EntityLink;
import org.flowable.entitylink.api.EntityLinkService;
import org.flowable.entitylink.api.history.HistoricEntityLink;
import org.flowable.entitylink.api.history.HistoricEntityLinkService;
import org.flowable.task.api.Task;
import org.flowable.task.api.history.HistoricTaskInstance;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


/**
 *
 *
 * @author Joram Barrez
 */
public class CmmnTaskServiceTest extends FlowableCmmnTestCase {
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    @CmmnDeployment
    public void testOneHumanTaskCase() {
        CaseInstance caseInstance = cmmnRuntimeService.createCaseInstanceBuilder().caseDefinitionKey("oneHumanTaskCase").start();
        Task task = cmmnTaskService.createTaskQuery().caseInstanceId(caseInstance.getId()).singleResult();
        Assert.assertNotNull(task);
        Assert.assertEquals("The Task", task.getName());
        Assert.assertEquals("This is a test documentation", task.getDescription());
        Assert.assertEquals("johnDoe", task.getAssignee());
        if (cmmnEngineConfiguration.getHistoryLevel().isAtLeast(ACTIVITY)) {
            HistoricTaskInstance historicTaskInstance = cmmnHistoryService.createHistoricTaskInstanceQuery().caseInstanceId(caseInstance.getId()).singleResult();
            Assert.assertNotNull(historicTaskInstance);
            Assert.assertNull(historicTaskInstance.getEndTime());
        }
        cmmnTaskService.complete(task.getId());
        assertCaseInstanceEnded(caseInstance);
        if (cmmnEngineConfiguration.getHistoryLevel().isAtLeast(ACTIVITY)) {
            HistoricTaskInstance historicTaskInstance = cmmnHistoryService.createHistoricTaskInstanceQuery().caseInstanceId(caseInstance.getId()).singleResult();
            Assert.assertNotNull(historicTaskInstance);
            Assert.assertEquals("The Task", historicTaskInstance.getName());
            Assert.assertEquals("This is a test documentation", historicTaskInstance.getDescription());
            Assert.assertNotNull(historicTaskInstance.getEndTime());
        }
    }

    @Test
    @CmmnDeployment
    public void testOneHumanTaskExpressionCase() {
        CaseInstance caseInstance = cmmnRuntimeService.createCaseInstanceBuilder().caseDefinitionKey("oneHumanTaskCase").variable("var1", "A").variable("var2", "YES").start();
        Task task = cmmnTaskService.createTaskQuery().caseInstanceId(caseInstance.getId()).singleResult();
        Assert.assertNotNull(task);
        Assert.assertEquals("The Task A", task.getName());
        Assert.assertEquals("This is a test YES", task.getDescription());
        Assert.assertEquals("johnDoe", task.getAssignee());
        cmmnTaskService.complete(task.getId());
        assertCaseInstanceEnded(caseInstance);
        if (cmmnEngineConfiguration.getHistoryLevel().isAtLeast(ACTIVITY)) {
            HistoricTaskInstance historicTaskInstance = cmmnHistoryService.createHistoricTaskInstanceQuery().caseInstanceId(caseInstance.getId()).singleResult();
            Assert.assertNotNull(historicTaskInstance);
            Assert.assertEquals("The Task A", historicTaskInstance.getName());
            Assert.assertEquals("This is a test YES", historicTaskInstance.getDescription());
            Assert.assertNotNull(historicTaskInstance.getEndTime());
        }
    }

    @Test
    @CmmnDeployment(resources = "org/flowable/cmmn/test/task/CmmnTaskServiceTest.testOneHumanTaskCase.cmmn")
    public void testOneHumanTaskVariableScopeExpressionCase() {
        CaseInstance caseInstance = cmmnRuntimeService.createCaseInstanceBuilder().caseDefinitionKey("oneHumanTaskCase").start();
        Task task = cmmnTaskService.createTaskQuery().caseInstanceId(caseInstance.getId()).singleResult();
        this.expectedException.expect(FlowableException.class);
        this.expectedException.expectMessage("Error while evaluating expression: ${caseInstance.name}");
        cmmnTaskService.complete(task.getId(), Collections.singletonMap("${caseInstance.name}", "newCaseName"));
    }

    @Test
    @CmmnDeployment(resources = "org/flowable/cmmn/test/task/CmmnTaskServiceTest.testOneHumanTaskCase.cmmn")
    public void testOneHumanTaskCompleteSetCaseName() {
        CaseInstance caseInstance = cmmnRuntimeService.createCaseInstanceBuilder().caseDefinitionKey("oneHumanTaskCase").start();
        Task task = cmmnTaskService.createTaskQuery().caseInstanceId(caseInstance.getId()).singleResult();
        this.expectedException.expect(FlowableException.class);
        this.expectedException.expectMessage("Error while evaluating expression: ${name}");
        cmmnTaskService.complete(task.getId(), Collections.singletonMap("${name}", "newCaseName"));
    }

    @Test
    @CmmnDeployment(resources = "org/flowable/cmmn/test/task/CmmnTaskServiceTest.testOneHumanTaskCase.cmmn")
    public void testOneHumanTaskCaseScopeExpression() {
        CaseInstance caseInstance = cmmnRuntimeService.createCaseInstanceBuilder().caseDefinitionKey("oneHumanTaskCase").start();
        Task task = cmmnTaskService.createTaskQuery().caseInstanceId(caseInstance.getId()).singleResult();
        cmmnTaskService.setVariable(task.getId(), "variableToUpdate", "VariableValue");
        cmmnTaskService.complete(task.getId(), Collections.singletonMap("${variableToUpdate}", "updatedVariableValue"));
        HistoricCaseInstance historicCaseInstance = cmmnHistoryService.createHistoricCaseInstanceQuery().caseInstanceId(caseInstance.getId()).includeCaseVariables().singleResult();
        MatcherAssert.assertThat(historicCaseInstance.getCaseVariables().get("variableToUpdate"), CoreMatchers.is("updatedVariableValue"));
    }

    @Test
    @CmmnDeployment(resources = "org/flowable/cmmn/test/task/CmmnTaskServiceTest.testOneHumanTaskCase.cmmn")
    public void testOneHumanTaskTaskScopeExpression() {
        CaseInstance caseInstance = cmmnRuntimeService.createCaseInstanceBuilder().caseDefinitionKey("oneHumanTaskCase").start();
        Task task = cmmnTaskService.createTaskQuery().caseInstanceId(caseInstance.getId()).singleResult();
        cmmnTaskService.setVariableLocal(task.getId(), "variableToUpdate", "VariableValue");
        cmmnTaskService.complete(task.getId(), Collections.singletonMap("${variableToUpdate}", "updatedVariableValue"));
        HistoricTaskInstance historicTaskInstance = cmmnHistoryService.createHistoricTaskInstanceQuery().caseInstanceId(caseInstance.getId()).includeTaskLocalVariables().singleResult();
        MatcherAssert.assertThat(historicTaskInstance.getTaskLocalVariables().get("variableToUpdate"), CoreMatchers.is("updatedVariableValue"));
    }

    @Test
    @CmmnDeployment(resources = "org/flowable/cmmn/test/task/CmmnTaskServiceTest.testOneHumanTaskCase.cmmn")
    public void testSetCaseNameByExpression() {
        CaseInstance caseInstance = cmmnRuntimeService.createCaseInstanceBuilder().variable("varToUpdate", "initialValue").caseDefinitionKey("oneHumanTaskCase").start();
        cmmnRuntimeService.setVariable(caseInstance.getId(), "${varToUpdate}", "newValue");
        CaseInstance updatedCaseInstance = cmmnRuntimeService.createCaseInstanceQuery().caseInstanceId(caseInstance.getId()).includeCaseVariables().singleResult();
        MatcherAssert.assertThat(updatedCaseInstance.getCaseVariables().get("varToUpdate"), CoreMatchers.is("newValue"));
    }

    @Test
    @CmmnDeployment
    public void testTriggerOneHumanTaskCaseProgrammatically() {
        CaseInstance caseInstance = cmmnRuntimeService.createCaseInstanceBuilder().caseDefinitionKey("oneHumanTaskCase").start();
        Task task = cmmnTaskService.createTaskQuery().caseInstanceId(caseInstance.getId()).singleResult();
        PlanItemInstance planItemInstance = cmmnRuntimeService.createPlanItemInstanceQuery().planItemInstanceStateActive().singleResult();
        Assert.assertEquals(planItemInstance.getId(), task.getSubScopeId());
        Assert.assertEquals(planItemInstance.getCaseInstanceId(), task.getScopeId());
        Assert.assertEquals(planItemInstance.getCaseDefinitionId(), task.getScopeDefinitionId());
        Assert.assertEquals(CMMN, task.getScopeType());
        cmmnRuntimeService.triggerPlanItemInstance(planItemInstance.getId());
        Assert.assertEquals(0, cmmnTaskService.createTaskQuery().count());
        assertCaseInstanceEnded(caseInstance);
    }

    @Test
    public void testCreateTaskWithBuilderAndScopes() {
        Task task = cmmnTaskService.createTaskBuilder().name("builderTask").scopeId("testScopeId").scopeType("testScopeType").create();
        try {
            Task taskFromQuery = cmmnTaskService.createTaskQuery().taskId(task.getId()).singleResult();
            MatcherAssert.assertThat(taskFromQuery.getScopeId(), CoreMatchers.is("testScopeId"));
            MatcherAssert.assertThat(taskFromQuery.getScopeType(), CoreMatchers.is("testScopeType"));
        } finally {
            cmmnTaskService.deleteTask(task.getId(), true);
        }
    }

    @Test
    public void testCreateTaskWithBuilderWithoutScopes() {
        Task task = cmmnTaskService.createTaskBuilder().name("builderTask").create();
        try {
            Task taskFromQuery = cmmnTaskService.createTaskQuery().taskId(task.getId()).singleResult();
            MatcherAssert.assertThat(taskFromQuery.getScopeId(), CoreMatchers.nullValue());
            MatcherAssert.assertThat(taskFromQuery.getScopeType(), CoreMatchers.nullValue());
        } finally {
            cmmnTaskService.deleteTask(task.getId(), true);
        }
    }

    @Test
    @CmmnDeployment
    public void testEntityLinkCreation() {
        CaseInstance caseInstance = cmmnRuntimeService.createCaseInstanceBuilder().caseDefinitionKey("entityLinkCreation").start();
        Task task = cmmnTaskService.createTaskQuery().caseInstanceId(caseInstance.getId()).singleResult();
        Assert.assertNotNull(task);
        CommandExecutor commandExecutor = cmmnEngine.getCmmnEngineConfiguration().getCommandExecutor();
        List<EntityLink> entityLinks = commandExecutor.execute(( commandContext) -> {
            EntityLinkService entityLinkService = CommandContextUtil.getEntityLinkService(commandContext);
            return entityLinkService.findEntityLinksByScopeIdAndType(caseInstance.getId(), ScopeTypes.CMMN, EntityLinkType.CHILD);
        });
        Assert.assertEquals(1, entityLinks.size());
        Assert.assertEquals(ROOT, entityLinks.get(0).getHierarchyType());
        cmmnTaskService.complete(task.getId());
        assertCaseInstanceEnded(caseInstance);
        List<HistoricEntityLink> entityLinksByScopeIdAndType = commandExecutor.execute(( commandContext) -> {
            HistoricEntityLinkService historicEntityLinkService = CommandContextUtil.getHistoricEntityLinkService(commandContext);
            return historicEntityLinkService.findHistoricEntityLinksByScopeIdAndScopeType(caseInstance.getId(), ScopeTypes.CMMN, EntityLinkType.CHILD);
        });
        Assert.assertEquals(1, entityLinksByScopeIdAndType.size());
        Assert.assertEquals(ROOT, entityLinksByScopeIdAndType.get(0).getHierarchyType());
    }
}

