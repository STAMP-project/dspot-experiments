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


import java.io.Serializable;
import java.util.List;
import java.util.Map;
import org.flowable.cmmn.api.runtime.CaseInstance;
import org.flowable.cmmn.api.runtime.PlanItemInstance;
import org.flowable.cmmn.engine.test.CmmnDeployment;
import org.flowable.cmmn.engine.test.FlowableCmmnTestCase;
import org.flowable.variable.api.history.HistoricVariableInstance;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Dennis Federico
 */
public class CmmnScriptTaskTest extends FlowableCmmnTestCase {
    @Test
    @CmmnDeployment
    public void testSimpleScript() {
        CaseInstance caseInstance = cmmnRuntimeService.createCaseInstanceBuilder().caseDefinitionKey("scriptCase").start();
        Assert.assertNotNull(caseInstance);
        PlanItemInstance planItemInstance = cmmnRuntimeService.createPlanItemInstanceQuery().planItemInstanceElementId("planItemTaskA").singleResult();
        Assert.assertNotNull(planItemInstance);
        Assert.assertEquals("Plan Item One", planItemInstance.getName());
        Assert.assertEquals("taskA", planItemInstance.getPlanItemDefinitionId());
        assertCaseInstanceNotEnded(caseInstance);
        planItemInstance = cmmnRuntimeService.createPlanItemInstanceQuery().planItemInstanceElementId("blockerPlanItem").singleResult();
        Assert.assertNotNull(planItemInstance);
        cmmnRuntimeService.triggerPlanItemInstance(planItemInstance.getId());
        assertCaseInstanceEnded(caseInstance);
        Map<String, Object> variables = cmmnRuntimeService.getVariables(caseInstance.getId());
        Assert.assertNotNull(variables);
        Assert.assertTrue(variables.isEmpty());
    }

    @Test
    @CmmnDeployment
    public void testPlanItemInstanceVarScopeAndVarHistory() {
        CaseInstance caseInstance = cmmnRuntimeService.createCaseInstanceBuilder().caseDefinitionKey("scriptCase").start();
        Assert.assertNotNull(caseInstance);
        // Keep the ScriptTaskPlanItem id to check the historic scope later
        PlanItemInstance planItemInstance = cmmnRuntimeService.createPlanItemInstanceQuery().planItemInstanceElementId("planItemTaskA").singleResult();
        Assert.assertNotNull(planItemInstance);
        String scriptTaskPlanInstanceId = planItemInstance.getId();
        // No variables set and no variables created yet by the script, because it has not run
        Assert.assertTrue(cmmnRuntimeService.getVariables(caseInstance.getId()).isEmpty());
        Assert.assertTrue(cmmnRuntimeService.getLocalVariables(scriptTaskPlanInstanceId).isEmpty());
        // Initialize one of the script variables to set its scope local to the planItemInstance, otherwise it goes up in the hierarchy up to the case by default
        cmmnRuntimeService.setLocalVariable(scriptTaskPlanInstanceId, "aString", "VALUE TO OVERWRITE");
        Assert.assertEquals(1, cmmnRuntimeService.getLocalVariables(scriptTaskPlanInstanceId).size());
        Assert.assertTrue(cmmnRuntimeService.getVariables(caseInstance.getId()).isEmpty());
        // Trigger the task entry event
        PlanItemInstance blocker = cmmnRuntimeService.createPlanItemInstanceQuery().planItemInstanceElementId("taskBlocker").singleResult();
        Assert.assertNotNull(blocker);
        cmmnRuntimeService.triggerPlanItemInstance(blocker.getId());
        // The case has not ended yet
        assertCaseInstanceNotEnded(caseInstance);
        // Check the case variables, one will be created by the script execution
        Map<String, Object> caseVariables = cmmnRuntimeService.getVariables(caseInstance.getId());
        Assert.assertNotNull(caseVariables);
        Assert.assertEquals(1, caseVariables.size());
        Assert.assertTrue(cmmnRuntimeService.hasVariable(caseInstance.getId(), "aInt"));
        Object integer = cmmnRuntimeService.getVariable(caseInstance.getId(), "aInt");
        Assert.assertThat(integer, CoreMatchers.instanceOf(Integer.class));
        Assert.assertEquals(5, integer);
        // The planItemInstance scope variable is available on the history service
        List<HistoricVariableInstance> historicVariables = cmmnHistoryService.createHistoricVariableInstanceQuery().caseInstanceId(caseInstance.getId()).planItemInstanceId(scriptTaskPlanInstanceId).list();
        Assert.assertNotNull(historicVariables);
        Assert.assertEquals(1, historicVariables.size());
        HistoricVariableInstance planItemInstanceVariable = historicVariables.get(0);
        Assert.assertNotNull(planItemInstanceVariable);
        Assert.assertEquals("aString", planItemInstanceVariable.getVariableName());
        Assert.assertEquals("string", planItemInstanceVariable.getVariableTypeName());
        Assert.assertEquals("value set in the script", planItemInstanceVariable.getValue());
        Assert.assertEquals(scriptTaskPlanInstanceId, planItemInstanceVariable.getSubScopeId());
        endTestCase();
        assertCaseInstanceEnded(caseInstance);
        // Both variables are still in the history
        historicVariables = cmmnHistoryService.createHistoricVariableInstanceQuery().caseInstanceId(caseInstance.getId()).list();
        Assert.assertNotNull(historicVariables);
        Assert.assertEquals(2, historicVariables.size());
        HistoricVariableInstance caseScopeVariable = historicVariables.stream().filter(( v) -> (v.getSubScopeId()) == null).findFirst().get();
        Assert.assertEquals("aInt", caseScopeVariable.getVariableName());
        Assert.assertEquals("integer", caseScopeVariable.getVariableTypeName());
        Assert.assertEquals(5, caseScopeVariable.getValue());
        HistoricVariableInstance planItemScopeVariable = historicVariables.stream().filter(( v) -> (v.getSubScopeId()) != null).findFirst().get();
        Assert.assertEquals("aString", planItemScopeVariable.getVariableName());
        Assert.assertEquals("string", planItemScopeVariable.getVariableTypeName());
        Assert.assertEquals("value set in the script", planItemScopeVariable.getValue());
    }

    @Test
    @CmmnDeployment
    public void testSimpleResult() {
        CaseInstance caseInstance = cmmnRuntimeService.createCaseInstanceBuilder().caseDefinitionKey("scriptCase").start();
        Assert.assertNotNull(caseInstance);
        Assert.assertTrue(cmmnRuntimeService.hasVariable(caseInstance.getId(), "scriptResult"));
        Object result = cmmnRuntimeService.getVariable(caseInstance.getId(), "scriptResult");
        Assert.assertThat(result, CoreMatchers.instanceOf(Number.class));
        Assert.assertEquals(7, ((Number) (result)).intValue());
        assertCaseInstanceNotEnded(caseInstance);
        endTestCase();
        assertCaseInstanceEnded(caseInstance);
    }

    @Test
    @CmmnDeployment
    public void testVariableResult() {
        CaseInstance caseInstance = cmmnRuntimeService.createCaseInstanceBuilder().caseDefinitionKey("scriptCase").variable("a", 3).variable("b", 7).start();
        Assert.assertNotNull(caseInstance);
        Assert.assertTrue(cmmnRuntimeService.hasVariable(caseInstance.getId(), "scriptResult"));
        Object result = cmmnRuntimeService.getVariable(caseInstance.getId(), "scriptResult");
        Assert.assertThat(result, CoreMatchers.instanceOf(Number.class));
        Assert.assertEquals(10, ((Number) (result)).intValue());
        assertCaseInstanceNotEnded(caseInstance);
        endTestCase();
        assertCaseInstanceEnded(caseInstance);
    }

    @Test
    @CmmnDeployment
    public void testObjectVariables() {
        CaseInstance caseInstance = cmmnRuntimeService.createCaseInstanceBuilder().caseDefinitionKey("scriptCase").variable("a", new CmmnScriptTaskTest.IntValueHolder(3)).variable("b", 7).start();
        Assert.assertNotNull(caseInstance);
        Assert.assertTrue(cmmnRuntimeService.hasVariable(caseInstance.getId(), "scriptResult"));
        Object result = cmmnRuntimeService.getVariable(caseInstance.getId(), "scriptResult");
        Assert.assertThat(result, CoreMatchers.instanceOf(Number.class));
        Assert.assertEquals(12, ((Number) (result)).intValue());
        Assert.assertTrue(cmmnRuntimeService.hasVariable(caseInstance.getId(), "a"));
        Object a = cmmnRuntimeService.getVariable(caseInstance.getId(), "a");
        Assert.assertThat(a, CoreMatchers.instanceOf(CmmnScriptTaskTest.IntValueHolder.class));
        Assert.assertEquals(5, ((CmmnScriptTaskTest.IntValueHolder) (a)).getValue());
        assertCaseInstanceNotEnded(caseInstance);
        endTestCase();
        assertCaseInstanceEnded(caseInstance);
    }

    @Test
    @CmmnDeployment
    public void testReturnJavaObject() {
        CaseInstance caseInstance = cmmnRuntimeService.createCaseInstanceBuilder().caseDefinitionKey("scriptCase").variable("a", new CmmnScriptTaskTest.IntValueHolder(3)).variable("b", 7).start();
        Assert.assertNotNull(caseInstance);
        Assert.assertTrue(cmmnRuntimeService.hasVariable(caseInstance.getId(), "scriptResult"));
        Object result = cmmnRuntimeService.getVariable(caseInstance.getId(), "scriptResult");
        Assert.assertThat(result, CoreMatchers.instanceOf(CmmnScriptTaskTest.IntValueHolder.class));
        Assert.assertEquals(10, ((CmmnScriptTaskTest.IntValueHolder) (result)).getValue());
        assertCaseInstanceNotEnded(caseInstance);
        endTestCase();
        assertCaseInstanceEnded(caseInstance);
    }

    @Test
    @CmmnDeployment
    public void testGroovyAutoStoreVariables() {
        CaseInstance caseInstance = cmmnRuntimeService.createCaseInstanceBuilder().caseDefinitionKey("scriptCase").variable("inputArray", new int[]{ 1, 2, 3, 4, 5 }).start();
        Assert.assertNotNull(caseInstance);
        Map<String, Object> variables = cmmnRuntimeService.getVariables(caseInstance.getId());
        Assert.assertNotNull(variables);
        Assert.assertEquals(2, variables.size());
        Assert.assertTrue(cmmnRuntimeService.hasVariable(caseInstance.getId(), "sum"));
        Object result = cmmnRuntimeService.getVariable(caseInstance.getId(), "sum");
        Assert.assertThat(result, CoreMatchers.instanceOf(Integer.class));
        Assert.assertEquals(15, ((Number) (result)).intValue());
        assertCaseInstanceNotEnded(caseInstance);
        endTestCase();
        assertCaseInstanceEnded(caseInstance);
    }

    @Test
    @CmmnDeployment
    public void testGroovyNoAutoStoreVariables() {
        CaseInstance caseInstance = cmmnRuntimeService.createCaseInstanceBuilder().caseDefinitionKey("scriptCase").variable("inputArray", new int[]{ 1, 2, 3, 4, 5 }).start();
        Assert.assertNotNull(caseInstance);
        Map<String, Object> variables = cmmnRuntimeService.getVariables(caseInstance.getId());
        Assert.assertNotNull(variables);
        Assert.assertEquals(1, variables.size());
        Assert.assertFalse(cmmnRuntimeService.hasVariable(caseInstance.getId(), "sum"));
        assertCaseInstanceNotEnded(caseInstance);
        endTestCase();
        assertCaseInstanceEnded(caseInstance);
    }

    public static class IntValueHolder implements Serializable {
        private int value;

        public IntValueHolder(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }

        public void setValue(int value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return (("IntValueHolder{" + "value=") + (value)) + '}';
        }
    }
}

