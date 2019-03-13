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


import CaseInstanceState.ACTIVE;
import CaseInstanceState.COMPLETED;
import CaseInstanceState.TERMINATED;
import HistoryLevel.ACTIVITY;
import HistoryLevel.AUDIT;
import IdentityLinkType.STARTER;
import ScopeTypes.CMMN;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.flowable.cmmn.api.history.HistoricCaseInstance;
import org.flowable.cmmn.api.history.HistoricMilestoneInstance;
import org.flowable.cmmn.api.repository.CaseDefinition;
import org.flowable.cmmn.api.runtime.CaseInstance;
import org.flowable.cmmn.api.runtime.MilestoneInstance;
import org.flowable.cmmn.api.runtime.PlanItemInstance;
import org.flowable.cmmn.engine.test.CmmnDeployment;
import org.flowable.cmmn.engine.test.FlowableCmmnTestCase;
import org.flowable.common.engine.impl.identity.Authentication;
import org.flowable.identitylink.api.IdentityLink;
import org.flowable.identitylink.api.history.HistoricIdentityLink;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Joram Barrez
 */
public class RuntimeServiceTest extends FlowableCmmnTestCase {
    private static final Map<String, Object> VARIABLES = new HashMap<>();

    static {
        RuntimeServiceTest.VARIABLES.put("var", "test");
        RuntimeServiceTest.VARIABLES.put("numberVar", 10);
    }

    @Test
    @CmmnDeployment
    public void testStartSimplePassthroughCase() {
        CaseDefinition caseDefinition = cmmnRepositoryService.createCaseDefinitionQuery().singleResult();
        Assert.assertEquals("myCase", caseDefinition.getKey());
        Assert.assertNotNull(caseDefinition.getResourceName());
        Assert.assertNotNull(caseDefinition.getDeploymentId());
        Assert.assertTrue(((caseDefinition.getVersion()) > 0));
        CaseInstance caseInstance = cmmnRuntimeService.createCaseInstanceBuilder().caseDefinitionId(caseDefinition.getId()).start();
        Assert.assertNotNull(caseInstance);
        Assert.assertEquals(caseDefinition.getId(), caseInstance.getCaseDefinitionId());
        Assert.assertNotNull(caseInstance.getStartTime());
        Assert.assertEquals(COMPLETED, caseInstance.getState());
        Assert.assertEquals(0, cmmnRuntimeService.createCaseInstanceQuery().count());
        Assert.assertEquals(0, cmmnHistoryService.createHistoricCaseInstanceQuery().unfinished().count());
        Assert.assertEquals(1, cmmnHistoryService.createHistoricCaseInstanceQuery().finished().count());
        List<HistoricMilestoneInstance> milestoneInstances = cmmnHistoryService.createHistoricMilestoneInstanceQuery().milestoneInstanceCaseInstanceId(caseInstance.getId()).orderByMilestoneName().asc().list();
        Assert.assertEquals(2, milestoneInstances.size());
        Assert.assertEquals("PlanItem Milestone One", milestoneInstances.get(0).getName());
        Assert.assertEquals("PlanItem Milestone Two", milestoneInstances.get(1).getName());
    }

    @Test
    @CmmnDeployment
    public void testStartSimplePassthroughCaseWithBlockingTask() {
        CaseDefinition caseDefinition = cmmnRepositoryService.createCaseDefinitionQuery().singleResult();
        CaseInstance caseInstance = cmmnRuntimeService.createCaseInstanceBuilder().caseDefinitionId(caseDefinition.getId()).start();
        Assert.assertEquals(ACTIVE, caseInstance.getState());
        Assert.assertEquals(1, cmmnHistoryService.createHistoricCaseInstanceQuery().unfinished().count());
        Assert.assertEquals(0, cmmnHistoryService.createHistoricCaseInstanceQuery().finished().count());
        Assert.assertEquals(4, cmmnRuntimeService.createPlanItemInstanceQuery().caseInstanceId(caseInstance.getId()).count());
        PlanItemInstance planItemInstance = cmmnRuntimeService.createPlanItemInstanceQuery().caseInstanceId(caseInstance.getId()).planItemInstanceState(PlanItemInstanceState.ACTIVE).singleResult();
        Assert.assertNotNull(planItemInstance);
        Assert.assertEquals("Task A", planItemInstance.getName());
        cmmnRuntimeService.triggerPlanItemInstance(planItemInstance.getId());
        List<MilestoneInstance> mileStones = cmmnRuntimeService.createMilestoneInstanceQuery().milestoneInstanceCaseInstanceId(caseInstance.getId()).list();
        Assert.assertEquals(1, mileStones.size());
        Assert.assertEquals("PlanItem Milestone One", mileStones.get(0).getName());
        List<HistoricMilestoneInstance> historicMilestoneInstances = cmmnHistoryService.createHistoricMilestoneInstanceQuery().milestoneInstanceCaseInstanceId(caseInstance.getId()).list();
        Assert.assertEquals(1, historicMilestoneInstances.size());
        Assert.assertEquals("PlanItem Milestone One", historicMilestoneInstances.get(0).getName());
        planItemInstance = cmmnRuntimeService.createPlanItemInstanceQuery().caseInstanceId(caseInstance.getId()).planItemInstanceState(PlanItemInstanceState.ACTIVE).singleResult();
        Assert.assertNotNull(planItemInstance);
        Assert.assertEquals("Task B", planItemInstance.getName());
        cmmnRuntimeService.triggerPlanItemInstance(planItemInstance.getId());
        Assert.assertEquals(0, cmmnRuntimeService.createCaseInstanceQuery().count());
        Assert.assertEquals(0, cmmnHistoryService.createHistoricCaseInstanceQuery().unfinished().count());
        Assert.assertEquals(1, cmmnHistoryService.createHistoricCaseInstanceQuery().finished().count());
        Assert.assertEquals(2, cmmnHistoryService.createHistoricMilestoneInstanceQuery().milestoneInstanceCaseInstanceId(caseInstance.getId()).count());
        if (cmmnEngineConfiguration.getHistoryLevel().isAtLeast(ACTIVITY)) {
            HistoricCaseInstance historicCaseInstance = cmmnHistoryService.createHistoricCaseInstanceQuery().caseInstanceId(caseInstance.getId()).singleResult();
            Assert.assertNotNull(historicCaseInstance);
            Assert.assertNotNull(historicCaseInstance.getStartTime());
            Assert.assertNotNull(historicCaseInstance.getEndTime());
            Assert.assertEquals(COMPLETED, historicCaseInstance.getState());
        }
    }

    @Test
    @CmmnDeployment(resources = { "org/flowable/cmmn/test/runtime/RuntimeServiceTest.testStartSimplePassthroughCaseWithBlockingTask.cmmn" })
    public void testVariableQueryWithBlockingTask() {
        CaseInstance caseInstance = cmmnRuntimeService.createCaseInstanceBuilder().caseDefinitionKey("myCase").variables(RuntimeServiceTest.VARIABLES).start();
        assertNonEmptyQueryIncludeVariables(1, RuntimeServiceTest.VARIABLES, cmmnRuntimeService.createCaseInstanceQuery().variableValueEquals("var", "test"), cmmnHistoryService.createHistoricCaseInstanceQuery().variableValueEquals("var", "test"));
        assertEmptyQuery(cmmnRuntimeService.createCaseInstanceQuery().variableValueEquals("var", "test2"), cmmnHistoryService.createHistoricCaseInstanceQuery().variableValueEquals("var", "test2"));
        assertNonEmptyQueryIncludeVariables(1, RuntimeServiceTest.VARIABLES, cmmnRuntimeService.createCaseInstanceQuery().variableValueEqualsIgnoreCase("var", "TEST"), cmmnHistoryService.createHistoricCaseInstanceQuery().variableValueEqualsIgnoreCase("var", "TEST"));
        assertEmptyQuery(cmmnRuntimeService.createCaseInstanceQuery().variableValueEqualsIgnoreCase("var", "TEST2"), cmmnHistoryService.createHistoricCaseInstanceQuery().variableValueEqualsIgnoreCase("var", "TEST2"));
        assertNonEmptyQueryIncludeVariables(1, RuntimeServiceTest.VARIABLES, cmmnRuntimeService.createCaseInstanceQuery().variableValueNotEquals("var", "test2"), cmmnHistoryService.createHistoricCaseInstanceQuery().variableValueNotEquals("var", "test2"));
        assertEmptyQuery(cmmnRuntimeService.createCaseInstanceQuery().variableValueNotEquals("var", "test"), cmmnHistoryService.createHistoricCaseInstanceQuery().variableValueNotEquals("var", "test"));
        assertNonEmptyQueryIncludeVariables(1, RuntimeServiceTest.VARIABLES, cmmnRuntimeService.createCaseInstanceQuery().variableValueNotEqualsIgnoreCase("var", "TEST2"), null);
        assertEmptyQuery(cmmnRuntimeService.createCaseInstanceQuery().variableValueNotEqualsIgnoreCase("var", "TEST"), null);
        assertNonEmptyQueryIncludeVariables(1, RuntimeServiceTest.VARIABLES, cmmnRuntimeService.createCaseInstanceQuery().variableValueLike("var", "te%"), cmmnHistoryService.createHistoricCaseInstanceQuery().variableValueLike("var", "te%"));
        assertEmptyQuery(cmmnRuntimeService.createCaseInstanceQuery().variableValueLike("var", "te2%"), cmmnHistoryService.createHistoricCaseInstanceQuery().variableValueLike("var", "te2%"));
        assertNonEmptyQueryIncludeVariables(1, RuntimeServiceTest.VARIABLES, cmmnRuntimeService.createCaseInstanceQuery().variableValueLikeIgnoreCase("var", "TE%"), cmmnHistoryService.createHistoricCaseInstanceQuery().variableValueLikeIgnoreCase("var", "TE%"));
        assertEmptyQuery(cmmnRuntimeService.createCaseInstanceQuery().variableValueLikeIgnoreCase("var", "TE2%"), cmmnHistoryService.createHistoricCaseInstanceQuery().variableValueLikeIgnoreCase("var", "TE2%"));
        assertNonEmptyQueryIncludeVariables(1, RuntimeServiceTest.VARIABLES, cmmnRuntimeService.createCaseInstanceQuery().variableValueGreaterThan("numberVar", 5), cmmnHistoryService.createHistoricCaseInstanceQuery().variableValueGreaterThan("numberVar", 5));
        assertEmptyQuery(cmmnRuntimeService.createCaseInstanceQuery().variableValueGreaterThan("numberVar", 11), cmmnHistoryService.createHistoricCaseInstanceQuery().variableValueGreaterThan("numberVar", 11));
        assertNonEmptyQueryIncludeVariables(1, RuntimeServiceTest.VARIABLES, cmmnRuntimeService.createCaseInstanceQuery().variableValueGreaterThanOrEqual("numberVar", 10), cmmnHistoryService.createHistoricCaseInstanceQuery().variableValueGreaterThanOrEqual("numberVar", 10));
        assertEmptyQuery(cmmnRuntimeService.createCaseInstanceQuery().variableValueGreaterThanOrEqual("numberVar", 11), cmmnHistoryService.createHistoricCaseInstanceQuery().variableValueGreaterThanOrEqual("numberVar", 11));
        assertNonEmptyQueryIncludeVariables(1, RuntimeServiceTest.VARIABLES, cmmnRuntimeService.createCaseInstanceQuery().variableValueLessThan("numberVar", 20), cmmnHistoryService.createHistoricCaseInstanceQuery().variableValueLessThan("numberVar", 20));
        assertEmptyQuery(cmmnRuntimeService.createCaseInstanceQuery().variableValueLessThan("numberVar", 5), cmmnHistoryService.createHistoricCaseInstanceQuery().variableValueLessThan("numberVar", 5));
        assertNonEmptyQueryIncludeVariables(1, RuntimeServiceTest.VARIABLES, cmmnRuntimeService.createCaseInstanceQuery().variableValueLessThanOrEqual("numberVar", 10), cmmnHistoryService.createHistoricCaseInstanceQuery().variableValueLessThanOrEqual("numberVar", 10));
        assertEmptyQuery(cmmnRuntimeService.createCaseInstanceQuery().variableValueLessThanOrEqual("numberVar", 9), cmmnHistoryService.createHistoricCaseInstanceQuery().variableValueLessThanOrEqual("numberVar", 9));
        PlanItemInstance planItemInstance = cmmnRuntimeService.createPlanItemInstanceQuery().caseInstanceId(caseInstance.getId()).planItemInstanceState(PlanItemInstanceState.ACTIVE).singleResult();
        Assert.assertNotNull(planItemInstance);
        Assert.assertEquals("Task A", planItemInstance.getName());
        Map<String, Object> varMap = new HashMap<>();
        varMap.put("numberVar", 11);
        cmmnRuntimeService.setVariables(caseInstance.getId(), varMap);
        Map<String, Object> localVarMap = new HashMap<>();
        localVarMap.put("localVar", "test");
        cmmnRuntimeService.setLocalVariables(planItemInstance.getId(), localVarMap);
        Map<String, Object> updatedVariables = new HashMap<>(RuntimeServiceTest.VARIABLES);
        updatedVariables.put("numberVar", 11);
        assertNonEmptyQueryIncludeVariables(1, updatedVariables, cmmnRuntimeService.createCaseInstanceQuery().variableValueGreaterThan("numberVar", 10), cmmnHistoryService.createHistoricCaseInstanceQuery().variableValueGreaterThan("numberVar", 10));
        assertEmptyQuery(cmmnRuntimeService.createCaseInstanceQuery().variableValueGreaterThan("numberVar", 11), cmmnHistoryService.createHistoricCaseInstanceQuery().variableValueGreaterThan("numberVar", 11));
        assertNonEmptyQueryIncludeVariables(1, updatedVariables, cmmnRuntimeService.createCaseInstanceQuery().variableValueGreaterThanOrEqual("numberVar", 11), cmmnHistoryService.createHistoricCaseInstanceQuery().variableValueGreaterThanOrEqual("numberVar", 11));
        assertEmptyQuery(cmmnRuntimeService.createCaseInstanceQuery().variableValueGreaterThanOrEqual("numberVar", 12), cmmnHistoryService.createHistoricCaseInstanceQuery().variableValueGreaterThanOrEqual("numberVar", 12));
        assertEmptyQuery(cmmnRuntimeService.createCaseInstanceQuery().variableValueEquals("localVar", "test"), cmmnHistoryService.createHistoricCaseInstanceQuery().variableValueEquals("localVar", "test"));
        cmmnRuntimeService.triggerPlanItemInstance(planItemInstance.getId());
        planItemInstance = cmmnRuntimeService.createPlanItemInstanceQuery().caseInstanceId(caseInstance.getId()).planItemInstanceState(PlanItemInstanceState.ACTIVE).singleResult();
        cmmnRuntimeService.triggerPlanItemInstance(planItemInstance.getId());
        assertEmptyQuery(cmmnRuntimeService.createCaseInstanceQuery(), cmmnHistoryService.createHistoricCaseInstanceQuery().unfinished());
    }

    @Test
    @CmmnDeployment(resources = { "org/flowable/cmmn/test/runtime/RuntimeServiceTest.testStartSimplePassthroughCaseWithBlockingTask.cmmn" })
    public void testPlanItemVariableQueryWithBlockingTask() {
        CaseInstance caseInstance = cmmnRuntimeService.createCaseInstanceBuilder().caseDefinitionKey("myCase").variable("var", "test").variable("numberVar", 10).start();
        Assert.assertEquals(4, cmmnRuntimeService.createPlanItemInstanceQuery().caseVariableValueEquals("var", "test").count());
        Assert.assertEquals(0, cmmnRuntimeService.createPlanItemInstanceQuery().caseVariableValueEquals("var", "test2").count());
        Assert.assertEquals(4, cmmnRuntimeService.createPlanItemInstanceQuery().caseVariableValueEqualsIgnoreCase("var", "TEST").count());
        Assert.assertEquals(0, cmmnRuntimeService.createPlanItemInstanceQuery().caseVariableValueEqualsIgnoreCase("var", "TEST2").count());
        Assert.assertEquals(4, cmmnRuntimeService.createPlanItemInstanceQuery().caseVariableValueNotEquals("var", "test2").count());
        Assert.assertEquals(0, cmmnRuntimeService.createPlanItemInstanceQuery().caseVariableValueNotEquals("var", "test").count());
        Assert.assertEquals(4, cmmnRuntimeService.createPlanItemInstanceQuery().caseVariableValueNotEqualsIgnoreCase("var", "TEST2").count());
        Assert.assertEquals(0, cmmnRuntimeService.createPlanItemInstanceQuery().caseVariableValueNotEqualsIgnoreCase("var", "TEST").count());
        Assert.assertEquals(4, cmmnRuntimeService.createPlanItemInstanceQuery().caseVariableValueLike("var", "te%").count());
        Assert.assertEquals(0, cmmnRuntimeService.createPlanItemInstanceQuery().caseVariableValueLike("var", "te2%").count());
        Assert.assertEquals(4, cmmnRuntimeService.createPlanItemInstanceQuery().caseVariableValueLikeIgnoreCase("var", "TE%").count());
        Assert.assertEquals(0, cmmnRuntimeService.createPlanItemInstanceQuery().caseVariableValueLikeIgnoreCase("var", "TE2%").count());
        Assert.assertEquals(4, cmmnRuntimeService.createPlanItemInstanceQuery().caseVariableValueGreaterThan("numberVar", 5).count());
        Assert.assertEquals(0, cmmnRuntimeService.createPlanItemInstanceQuery().caseVariableValueGreaterThan("numberVar", 11).count());
        Assert.assertEquals(4, cmmnRuntimeService.createPlanItemInstanceQuery().caseVariableValueGreaterThanOrEqual("numberVar", 10).count());
        Assert.assertEquals(0, cmmnRuntimeService.createPlanItemInstanceQuery().caseVariableValueGreaterThanOrEqual("numberVar", 11).count());
        Assert.assertEquals(4, cmmnRuntimeService.createPlanItemInstanceQuery().caseVariableValueLessThan("numberVar", 20).count());
        Assert.assertEquals(0, cmmnRuntimeService.createPlanItemInstanceQuery().caseVariableValueLessThan("numberVar", 5).count());
        Assert.assertEquals(4, cmmnRuntimeService.createPlanItemInstanceQuery().caseVariableValueLessThanOrEqual("numberVar", 10).count());
        Assert.assertEquals(0, cmmnRuntimeService.createPlanItemInstanceQuery().caseVariableValueLessThanOrEqual("numberVar", 9).count());
        PlanItemInstance planItemInstance = cmmnRuntimeService.createPlanItemInstanceQuery().caseInstanceId(caseInstance.getId()).planItemInstanceState(PlanItemInstanceState.ACTIVE).singleResult();
        Assert.assertNotNull(planItemInstance);
        Assert.assertEquals("Task A", planItemInstance.getName());
        Map<String, Object> varMap = new HashMap<>();
        varMap.put("numberVar", 11);
        cmmnRuntimeService.setVariables(caseInstance.getId(), varMap);
        Map<String, Object> localVarMap = new HashMap<>();
        localVarMap.put("localVar", "test");
        localVarMap.put("localNumberVar", 15);
        cmmnRuntimeService.setLocalVariables(planItemInstance.getId(), localVarMap);
        Assert.assertEquals(4, cmmnRuntimeService.createPlanItemInstanceQuery().caseVariableValueGreaterThan("numberVar", 10).count());
        Assert.assertEquals(0, cmmnRuntimeService.createPlanItemInstanceQuery().caseVariableValueGreaterThan("numberVar", 11).count());
        Assert.assertEquals(4, cmmnRuntimeService.createPlanItemInstanceQuery().caseVariableValueGreaterThanOrEqual("numberVar", 11).count());
        Assert.assertEquals(0, cmmnRuntimeService.createPlanItemInstanceQuery().caseVariableValueGreaterThanOrEqual("numberVar", 12).count());
        Assert.assertEquals(0, cmmnRuntimeService.createPlanItemInstanceQuery().caseVariableValueEquals("localVar", "test").count());
        Assert.assertEquals(1, cmmnRuntimeService.createPlanItemInstanceQuery().variableValueEquals("localVar", "test").count());
        Assert.assertEquals(0, cmmnRuntimeService.createPlanItemInstanceQuery().variableValueEquals("localVar", "test2").count());
        Assert.assertEquals(1, cmmnRuntimeService.createPlanItemInstanceQuery().variableValueEqualsIgnoreCase("localVar", "TEST").count());
        Assert.assertEquals(0, cmmnRuntimeService.createPlanItemInstanceQuery().variableValueEqualsIgnoreCase("localVar", "TEST2").count());
        Assert.assertEquals(1, cmmnRuntimeService.createPlanItemInstanceQuery().variableValueNotEquals("localVar", "test2").count());
        Assert.assertEquals(0, cmmnRuntimeService.createPlanItemInstanceQuery().variableValueNotEquals("localVar", "test").count());
        Assert.assertEquals(1, cmmnRuntimeService.createPlanItemInstanceQuery().variableValueNotEqualsIgnoreCase("localVar", "TEST2").count());
        Assert.assertEquals(0, cmmnRuntimeService.createPlanItemInstanceQuery().variableValueNotEqualsIgnoreCase("localVar", "TEST").count());
        Assert.assertEquals(1, cmmnRuntimeService.createPlanItemInstanceQuery().variableValueLike("localVar", "te%").count());
        Assert.assertEquals(0, cmmnRuntimeService.createPlanItemInstanceQuery().variableValueLike("localVar", "te2%").count());
        Assert.assertEquals(1, cmmnRuntimeService.createPlanItemInstanceQuery().variableValueLikeIgnoreCase("localVar", "TE%").count());
        Assert.assertEquals(0, cmmnRuntimeService.createPlanItemInstanceQuery().variableValueLikeIgnoreCase("localVar", "TE2%").count());
        Assert.assertEquals(1, cmmnRuntimeService.createPlanItemInstanceQuery().variableValueGreaterThan("localNumberVar", 5).count());
        Assert.assertEquals(0, cmmnRuntimeService.createPlanItemInstanceQuery().variableValueGreaterThan("localNumberVar", 17).count());
        Assert.assertEquals(1, cmmnRuntimeService.createPlanItemInstanceQuery().variableValueGreaterThanOrEqual("localNumberVar", 15).count());
        Assert.assertEquals(0, cmmnRuntimeService.createPlanItemInstanceQuery().variableValueGreaterThanOrEqual("localNumberVar", 16).count());
        Assert.assertEquals(1, cmmnRuntimeService.createPlanItemInstanceQuery().variableValueLessThan("localNumberVar", 20).count());
        Assert.assertEquals(0, cmmnRuntimeService.createPlanItemInstanceQuery().variableValueLessThan("localNumberVar", 5).count());
        Assert.assertEquals(1, cmmnRuntimeService.createPlanItemInstanceQuery().variableValueLessThanOrEqual("localNumberVar", 15).count());
        Assert.assertEquals(0, cmmnRuntimeService.createPlanItemInstanceQuery().variableValueLessThanOrEqual("localNumberVar", 9).count());
        cmmnRuntimeService.triggerPlanItemInstance(planItemInstance.getId());
        planItemInstance = cmmnRuntimeService.createPlanItemInstanceQuery().caseInstanceId(caseInstance.getId()).planItemInstanceState(PlanItemInstanceState.ACTIVE).singleResult();
        cmmnRuntimeService.triggerPlanItemInstance(planItemInstance.getId());
        Assert.assertEquals(0, cmmnRuntimeService.createCaseInstanceQuery().count());
    }

    @Test
    @CmmnDeployment(resources = { "org/flowable/cmmn/test/runtime/RuntimeServiceTest.testStartSimplePassthroughCaseWithBlockingTask.cmmn" })
    public void testLocalVariablesWithBlockingTask() {
        CaseInstance caseInstance = cmmnRuntimeService.createCaseInstanceBuilder().caseDefinitionKey("myCase").start();
        PlanItemInstance planItemInstance = cmmnRuntimeService.createPlanItemInstanceQuery().caseInstanceId(caseInstance.getId()).planItemInstanceState(PlanItemInstanceState.ACTIVE).singleResult();
        Assert.assertNotNull(planItemInstance);
        Assert.assertEquals("Task A", planItemInstance.getName());
        Map<String, Object> varMap = new HashMap<>();
        varMap.put("localVar", "test");
        cmmnRuntimeService.setLocalVariables(planItemInstance.getId(), varMap);
        Assert.assertEquals("test", cmmnRuntimeService.getLocalVariable(planItemInstance.getId(), "localVar"));
        Assert.assertEquals(1, cmmnRuntimeService.getLocalVariables(planItemInstance.getId()).size());
        Assert.assertNull(cmmnRuntimeService.getVariable(caseInstance.getId(), "localVar"));
        cmmnRuntimeService.triggerPlanItemInstance(planItemInstance.getId());
        planItemInstance = cmmnRuntimeService.createPlanItemInstanceQuery().caseInstanceId(caseInstance.getId()).planItemInstanceState(PlanItemInstanceState.ACTIVE).singleResult();
        Assert.assertNotNull(planItemInstance);
        Assert.assertEquals("Task B", planItemInstance.getName());
        Assert.assertNull(cmmnRuntimeService.getLocalVariable(planItemInstance.getId(), "localVar"));
        cmmnRuntimeService.triggerPlanItemInstance(planItemInstance.getId());
        Assert.assertEquals(0, cmmnRuntimeService.createCaseInstanceQuery().count());
    }

    @Test
    @CmmnDeployment(resources = { "org/flowable/cmmn/test/runtime/RuntimeServiceTest.testStartSimplePassthroughCaseWithBlockingTask.cmmn" })
    public void testRemoveLocalVariablesWithBlockingTask() {
        CaseInstance caseInstance = cmmnRuntimeService.createCaseInstanceBuilder().caseDefinitionKey("myCase").start();
        PlanItemInstance planItemInstance = cmmnRuntimeService.createPlanItemInstanceQuery().caseInstanceId(caseInstance.getId()).planItemInstanceState(PlanItemInstanceState.ACTIVE).singleResult();
        Assert.assertNotNull(planItemInstance);
        Assert.assertEquals("Task A", planItemInstance.getName());
        Map<String, Object> localVarMap = new HashMap<>();
        localVarMap.put("localVar", "test");
        cmmnRuntimeService.setLocalVariables(planItemInstance.getId(), localVarMap);
        Map<String, Object> varMap = new HashMap<>();
        varMap.put("var", "test");
        cmmnRuntimeService.setVariables(caseInstance.getId(), varMap);
        Assert.assertEquals("test", cmmnRuntimeService.getLocalVariable(planItemInstance.getId(), "localVar"));
        Assert.assertEquals(1, cmmnRuntimeService.getLocalVariables(planItemInstance.getId()).size());
        Assert.assertEquals("test", cmmnRuntimeService.getVariable(caseInstance.getId(), "var"));
        Assert.assertEquals(1, cmmnRuntimeService.getVariables(caseInstance.getId()).size());
        cmmnRuntimeService.removeLocalVariable(planItemInstance.getId(), "localVar");
        Assert.assertNull(cmmnRuntimeService.getLocalVariable(planItemInstance.getId(), "localVar"));
        cmmnRuntimeService.triggerPlanItemInstance(planItemInstance.getId());
        planItemInstance = cmmnRuntimeService.createPlanItemInstanceQuery().caseInstanceId(caseInstance.getId()).planItemInstanceState(PlanItemInstanceState.ACTIVE).singleResult();
        Assert.assertNotNull(planItemInstance);
        Assert.assertEquals("Task B", planItemInstance.getName());
        Assert.assertEquals("test", cmmnRuntimeService.getVariable(caseInstance.getId(), "var"));
        Assert.assertEquals(1, cmmnRuntimeService.getVariables(caseInstance.getId()).size());
        cmmnRuntimeService.removeVariable(caseInstance.getId(), "var");
        Assert.assertNull(cmmnRuntimeService.getVariable(caseInstance.getId(), "var"));
        cmmnRuntimeService.triggerPlanItemInstance(planItemInstance.getId());
        Assert.assertEquals(0, cmmnRuntimeService.createCaseInstanceQuery().count());
        Assert.assertEquals(0, cmmnHistoryService.createHistoricCaseInstanceQuery().unfinished().count());
        Assert.assertEquals(1, cmmnHistoryService.createHistoricCaseInstanceQuery().finished().count());
    }

    @Test
    @CmmnDeployment(resources = { "org/flowable/cmmn/test/runtime/RuntimeServiceTest.testStartSimplePassthroughCaseWithBlockingTask.cmmn" })
    public void testIncludeVariableSingleResultQueryWithBlockingTask() {
        CaseInstance caseInstance = cmmnRuntimeService.createCaseInstanceBuilder().caseDefinitionKey("myCase").variables(RuntimeServiceTest.VARIABLES).start();
        assertNonEmptyQueryIncludeVariables(1, RuntimeServiceTest.VARIABLES, cmmnRuntimeService.createCaseInstanceQuery().includeCaseVariables(), cmmnHistoryService.createHistoricCaseInstanceQuery().includeCaseVariables());
        Map<String, Object> updatedVariables = new HashMap<>(RuntimeServiceTest.VARIABLES);
        updatedVariables.put("newVar", 14.2);
        cmmnRuntimeService.setVariables(caseInstance.getId(), updatedVariables);
        assertNonEmptyQueryIncludeVariables(1, updatedVariables, cmmnRuntimeService.createCaseInstanceQuery().includeCaseVariables(), cmmnHistoryService.createHistoricCaseInstanceQuery().includeCaseVariables());
    }

    @Test
    @CmmnDeployment(resources = { "org/flowable/cmmn/test/runtime/RuntimeServiceTest.testStartSimplePassthroughCaseWithBlockingTask.cmmn" })
    public void testIncludeVariableListQueryWithBlockingTask() {
        Map<String, Object> variablesA = new HashMap<>();
        variablesA.put("var", "test");
        variablesA.put("numberVar", 10);
        Map<String, Object> variablesB = new HashMap<>();
        variablesB.put("var", "test");
        variablesB.put("numberVar", 10);
        variablesB.put("floatVar", 10.1);
        cmmnRuntimeService.createCaseInstanceBuilder().caseDefinitionKey("myCase").name("A").variables(variablesA).start();
        cmmnRuntimeService.createCaseInstanceBuilder().caseDefinitionKey("myCase").name("B").variables(variablesB).start();
        cmmnRuntimeService.createCaseInstanceBuilder().caseDefinitionKey("myCase").name("C").start();
        List<CaseInstance> caseInstancesWithVariables = cmmnRuntimeService.createCaseInstanceQuery().includeCaseVariables().orderByStartTime().list();
        Assert.assertThat(caseInstancesWithVariables.size(), CoreMatchers.is(3));
        Assert.assertThat(caseInstancesWithVariables.get(0).getCaseVariables(), CoreMatchers.is(variablesA));
        Assert.assertThat(caseInstancesWithVariables.get(1).getCaseVariables(), CoreMatchers.is(variablesB));
        Assert.assertThat(caseInstancesWithVariables.get(2).getCaseVariables(), CoreMatchers.is(Collections.EMPTY_MAP));
        Assert.assertThat(cmmnRuntimeService.createCaseInstanceQuery().includeCaseVariables().orderByStartTime().count(), CoreMatchers.is(3L));
        List<HistoricCaseInstance> historicCaseInstancesWithVariables = cmmnHistoryService.createHistoricCaseInstanceQuery().includeCaseVariables().orderByStartTime().list();
        Assert.assertThat(historicCaseInstancesWithVariables.size(), CoreMatchers.is(3));
        Assert.assertThat(historicCaseInstancesWithVariables.get(0).getCaseVariables(), CoreMatchers.is(variablesA));
        Assert.assertThat(historicCaseInstancesWithVariables.get(1).getCaseVariables(), CoreMatchers.is(variablesB));
        Assert.assertThat(historicCaseInstancesWithVariables.get(2).getCaseVariables(), CoreMatchers.is(Collections.EMPTY_MAP));
        Assert.assertThat(cmmnHistoryService.createHistoricCaseInstanceQuery().includeCaseVariables().orderByStartTime().count(), CoreMatchers.is(3L));
    }

    @Test
    @CmmnDeployment(resources = { "org/flowable/cmmn/test/runtime/RuntimeServiceTest.testStartSimplePassthroughCaseWithBlockingTask.cmmn" })
    public void testIncludeSameVariableListQueryWithBlockingTask() {
        cmmnRuntimeService.createCaseInstanceBuilder().caseDefinitionKey("myCase").name("A").variables(RuntimeServiceTest.VARIABLES).start();
        cmmnRuntimeService.createCaseInstanceBuilder().caseDefinitionKey("myCase").name("B").variables(RuntimeServiceTest.VARIABLES).start();
        cmmnRuntimeService.createCaseInstanceBuilder().caseDefinitionKey("myCase").variables(RuntimeServiceTest.VARIABLES).name("C").start();
        assertNonEmptyQueryIncludeVariables(3, RuntimeServiceTest.VARIABLES, cmmnRuntimeService.createCaseInstanceQuery().orderByStartTime(), cmmnHistoryService.createHistoricCaseInstanceQuery().orderByStartTime());
    }

    @Test
    @CmmnDeployment(resources = { "org/flowable/cmmn/test/runtime/RuntimeServiceTest.testStartSimplePassthroughCaseWithBlockingTask.cmmn" })
    public void includeVariablesWithPaginationQueries() {
        createCaseInstances();
        testIncludeVariablesWithPagination(cmmnRuntimeService.createCaseInstanceQuery().includeCaseVariables().orderByStartTime());
        testIncludeVariablesWithPagination(cmmnRuntimeService.createCaseInstanceQuery().variableValueEquals("var", "test").includeCaseVariables().orderByStartTime());
        testIncludeVariablesWithPagination(cmmnRuntimeService.createCaseInstanceQuery().variableValueEqualsIgnoreCase("var", "TEST").includeCaseVariables().orderByStartTime());
        testIncludeVariablesWithPagination(cmmnRuntimeService.createCaseInstanceQuery().variableValueNotEquals("var", "test2").includeCaseVariables().orderByStartTime());
        testIncludeVariablesWithPagination(cmmnRuntimeService.createCaseInstanceQuery().variableValueNotEqualsIgnoreCase("var", "TEST2").includeCaseVariables().orderByStartTime());
        testIncludeVariablesWithPagination(cmmnRuntimeService.createCaseInstanceQuery().variableValueLike("var", "te%").includeCaseVariables().orderByStartTime());
        testIncludeVariablesWithPagination(cmmnRuntimeService.createCaseInstanceQuery().variableValueLikeIgnoreCase("var", "TE%").includeCaseVariables().orderByStartTime());
        testIncludeVariablesWithPagination(cmmnRuntimeService.createCaseInstanceQuery().variableValueGreaterThan("numberVar", 5).includeCaseVariables().orderByStartTime());
        testIncludeVariablesWithPagination(cmmnRuntimeService.createCaseInstanceQuery().variableValueGreaterThanOrEqual("numberVar", 10).includeCaseVariables().orderByStartTime());
        testIncludeVariablesWithPagination(cmmnRuntimeService.createCaseInstanceQuery().variableValueLessThan("numberVar", 20).includeCaseVariables().orderByStartTime());
        testIncludeVariablesWithPagination(cmmnRuntimeService.createCaseInstanceQuery().variableValueLessThanOrEqual("numberVar", 10).includeCaseVariables().orderByStartTime());
        testIncludeVariablesWithPagination(cmmnHistoryService.createHistoricCaseInstanceQuery().includeCaseVariables().orderByStartTime());
        testIncludeVariablesWithPagination(cmmnHistoryService.createHistoricCaseInstanceQuery().variableValueEquals("var", "test").includeCaseVariables().orderByStartTime());
        testIncludeVariablesWithPagination(cmmnHistoryService.createHistoricCaseInstanceQuery().variableValueEqualsIgnoreCase("var", "TEST").includeCaseVariables().orderByStartTime());
        testIncludeVariablesWithPagination(cmmnHistoryService.createHistoricCaseInstanceQuery().variableValueNotEquals("var", "test2").includeCaseVariables().orderByStartTime());
        testIncludeVariablesWithPagination(cmmnHistoryService.createHistoricCaseInstanceQuery().variableValueLike("var", "te%").includeCaseVariables().orderByStartTime());
        testIncludeVariablesWithPagination(cmmnHistoryService.createHistoricCaseInstanceQuery().variableValueLikeIgnoreCase("var", "TE%").includeCaseVariables().orderByStartTime());
        testIncludeVariablesWithPagination(cmmnHistoryService.createHistoricCaseInstanceQuery().variableValueGreaterThan("numberVar", 5).includeCaseVariables().orderByStartTime());
        testIncludeVariablesWithPagination(cmmnHistoryService.createHistoricCaseInstanceQuery().variableValueGreaterThanOrEqual("numberVar", 10).includeCaseVariables().orderByStartTime());
        testIncludeVariablesWithPagination(cmmnHistoryService.createHistoricCaseInstanceQuery().variableValueLessThan("numberVar", 20).includeCaseVariables().orderByStartTime());
        testIncludeVariablesWithPagination(cmmnHistoryService.createHistoricCaseInstanceQuery().variableValueLessThanOrEqual("numberVar", 10).includeCaseVariables().orderByStartTime());
    }

    @Test
    @CmmnDeployment(resources = { "org/flowable/cmmn/test/runtime/RuntimeServiceTest.testStartSimplePassthroughCaseWithBlockingTask.cmmn" })
    public void includeVariablesWithEmptyPaginationQueries() {
        createCaseInstances();
        testIncludeVariablesOnEmptyQueryWithPagination(cmmnRuntimeService.createCaseInstanceQuery().variableValueEquals("var", "test2").includeCaseVariables().orderByStartTime());
        testIncludeVariablesOnEmptyQueryWithPagination(cmmnRuntimeService.createCaseInstanceQuery().variableValueEqualsIgnoreCase("var", "TEST2").includeCaseVariables().orderByStartTime());
        testIncludeVariablesOnEmptyQueryWithPagination(cmmnRuntimeService.createCaseInstanceQuery().variableValueNotEquals("var", "test").includeCaseVariables().orderByStartTime());
        testIncludeVariablesOnEmptyQueryWithPagination(cmmnRuntimeService.createCaseInstanceQuery().variableValueNotEqualsIgnoreCase("var", "TEST").includeCaseVariables().orderByStartTime());
        testIncludeVariablesOnEmptyQueryWithPagination(cmmnRuntimeService.createCaseInstanceQuery().variableValueLike("var", "te2%").includeCaseVariables().orderByStartTime());
        testIncludeVariablesOnEmptyQueryWithPagination(cmmnRuntimeService.createCaseInstanceQuery().variableValueLikeIgnoreCase("var", "TE2%").includeCaseVariables().orderByStartTime());
        testIncludeVariablesOnEmptyQueryWithPagination(cmmnRuntimeService.createCaseInstanceQuery().variableValueGreaterThan("numberVar", 11).includeCaseVariables().orderByStartTime());
        testIncludeVariablesOnEmptyQueryWithPagination(cmmnRuntimeService.createCaseInstanceQuery().variableValueGreaterThanOrEqual("numberVar", 11).includeCaseVariables().orderByStartTime());
        testIncludeVariablesOnEmptyQueryWithPagination(cmmnRuntimeService.createCaseInstanceQuery().variableValueLessThan("numberVar", 5).includeCaseVariables().orderByStartTime());
        testIncludeVariablesOnEmptyQueryWithPagination(cmmnRuntimeService.createCaseInstanceQuery().variableValueLessThanOrEqual("numberVar", 9).includeCaseVariables().orderByStartTime());
        testIncludeVariablesOnEmptyQueryWithPagination(cmmnHistoryService.createHistoricCaseInstanceQuery().variableValueEquals("var", "test2").includeCaseVariables().orderByStartTime());
        testIncludeVariablesOnEmptyQueryWithPagination(cmmnHistoryService.createHistoricCaseInstanceQuery().variableValueEqualsIgnoreCase("var", "TEST2").includeCaseVariables().orderByStartTime());
        testIncludeVariablesOnEmptyQueryWithPagination(cmmnHistoryService.createHistoricCaseInstanceQuery().variableValueNotEquals("var", "test").includeCaseVariables().orderByStartTime());
        testIncludeVariablesOnEmptyQueryWithPagination(cmmnHistoryService.createHistoricCaseInstanceQuery().variableValueLike("var", "te2%").includeCaseVariables().orderByStartTime());
        testIncludeVariablesOnEmptyQueryWithPagination(cmmnHistoryService.createHistoricCaseInstanceQuery().variableValueLikeIgnoreCase("var", "TE2%").includeCaseVariables().orderByStartTime());
        testIncludeVariablesOnEmptyQueryWithPagination(cmmnHistoryService.createHistoricCaseInstanceQuery().variableValueGreaterThan("numberVar", 11).includeCaseVariables().orderByStartTime());
        testIncludeVariablesOnEmptyQueryWithPagination(cmmnHistoryService.createHistoricCaseInstanceQuery().variableValueGreaterThanOrEqual("numberVar", 11).includeCaseVariables().orderByStartTime());
        testIncludeVariablesOnEmptyQueryWithPagination(cmmnHistoryService.createHistoricCaseInstanceQuery().variableValueLessThan("numberVar", 5).includeCaseVariables().orderByStartTime());
        testIncludeVariablesOnEmptyQueryWithPagination(cmmnHistoryService.createHistoricCaseInstanceQuery().variableValueLessThanOrEqual("numberVar", 9).includeCaseVariables().orderByStartTime());
    }

    @Test
    @CmmnDeployment
    public void testTerminateCaseInstance() {
        // Task A active
        CaseInstance caseInstance = cmmnRuntimeService.createCaseInstanceBuilder().caseDefinitionKey("myCase").start();
        cmmnRuntimeService.terminateCaseInstance(caseInstance.getId());
        assertCaseInstanceEnded(caseInstance, 0);
        // Task B active
        caseInstance = cmmnRuntimeService.createCaseInstanceBuilder().caseDefinitionKey("myCase").start();
        cmmnRuntimeService.triggerPlanItemInstance(cmmnRuntimeService.createPlanItemInstanceQuery().caseInstanceId(caseInstance.getId()).planItemInstanceState(PlanItemInstanceState.ACTIVE).singleResult().getId());
        cmmnRuntimeService.terminateCaseInstance(caseInstance.getId());
        assertCaseInstanceEnded(caseInstance, 1);
        if (cmmnEngineConfiguration.getHistoryLevel().isAtLeast(ACTIVITY)) {
            HistoricCaseInstance historicCaseInstance = cmmnHistoryService.createHistoricCaseInstanceQuery().caseInstanceId(caseInstance.getId()).singleResult();
            Assert.assertNotNull(historicCaseInstance);
            Assert.assertNotNull(historicCaseInstance.getStartTime());
            Assert.assertNotNull(historicCaseInstance.getEndTime());
            Assert.assertEquals(TERMINATED, historicCaseInstance.getState());
        }
    }

    @Test
    @CmmnDeployment
    public void testTerminateCaseInstanceWithNestedStages() {
        CaseInstance caseInstance = cmmnRuntimeService.createCaseInstanceBuilder().caseDefinitionKey("myCase").start();
        Assert.assertEquals(8, cmmnRuntimeService.createPlanItemInstanceQuery().caseInstanceId(caseInstance.getId()).count());
        cmmnRuntimeService.terminateCaseInstance(caseInstance.getId());
        assertCaseInstanceEnded(caseInstance, 0);
        if (cmmnEngineConfiguration.getHistoryLevel().isAtLeast(ACTIVITY)) {
            HistoricCaseInstance historicCaseInstance = cmmnHistoryService.createHistoricCaseInstanceQuery().caseInstanceId(caseInstance.getId()).singleResult();
            Assert.assertNotNull(historicCaseInstance);
            Assert.assertNotNull(historicCaseInstance.getStartTime());
            Assert.assertNotNull(historicCaseInstance.getEndTime());
            Assert.assertEquals(TERMINATED, historicCaseInstance.getState());
        }
    }

    @Test
    @CmmnDeployment
    public void testCaseInstanceProperties() {
        CaseInstance caseInstance = cmmnRuntimeService.createCaseInstanceBuilder().caseDefinitionKey("myCase").name("test name").businessKey("test business key").start();
        caseInstance = cmmnRuntimeService.createCaseInstanceQuery().caseInstanceId(caseInstance.getId()).singleResult();
        Assert.assertEquals("test name", caseInstance.getName());
        Assert.assertEquals("test business key", caseInstance.getBusinessKey());
    }

    @Test
    @CmmnDeployment
    public void testCaseInstanceStarterIdentityLink() {
        Authentication.setAuthenticatedUserId("testUser");
        CaseInstance caseInstance = cmmnRuntimeService.createCaseInstanceBuilder().caseDefinitionKey("myCase").start();
        Authentication.setAuthenticatedUserId(null);
        List<IdentityLink> caseIdentityLinks = cmmnRuntimeService.getIdentityLinksForCaseInstance(caseInstance.getId());
        Assert.assertEquals(1, caseIdentityLinks.size());
        Assert.assertEquals(caseInstance.getId(), caseIdentityLinks.get(0).getScopeId());
        Assert.assertEquals(CMMN, caseIdentityLinks.get(0).getScopeType());
        Assert.assertEquals(STARTER, caseIdentityLinks.get(0).getType());
        Assert.assertEquals("testUser", caseIdentityLinks.get(0).getUserId());
        if (cmmnEngineConfiguration.getHistoryLevel().isAtLeast(AUDIT)) {
            List<HistoricIdentityLink> historicIdentityLinks = cmmnHistoryService.getHistoricIdentityLinksForCaseInstance(caseInstance.getId());
            Assert.assertEquals(1, historicIdentityLinks.size());
            Assert.assertEquals(caseInstance.getId(), historicIdentityLinks.get(0).getScopeId());
            Assert.assertEquals(CMMN, historicIdentityLinks.get(0).getScopeType());
            Assert.assertEquals(STARTER, historicIdentityLinks.get(0).getType());
            Assert.assertEquals("testUser", historicIdentityLinks.get(0).getUserId());
        }
    }

    @Test
    @CmmnDeployment(resources = { "org/flowable/cmmn/test/runtime/RuntimeServiceTest.testStartSimplePassthroughCaseWithBlockingTask.cmmn" })
    public void planItemQueryWithoutTenant() {
        cmmnRuntimeService.createCaseInstanceBuilder().caseDefinitionKey("myCase").variable("var", "test").variable("numberVar", 10).start();
        Assert.assertEquals(1, cmmnRuntimeService.createPlanItemInstanceQuery().planItemInstanceName("Task A").planItemInstanceWithoutTenantId().count());
        Assert.assertEquals(1, cmmnRuntimeService.createPlanItemInstanceQuery().planItemInstanceName("Task A").planItemInstanceWithoutTenantId().list().size());
    }
}

