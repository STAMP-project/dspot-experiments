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


import ScopeTypes.CMMN;
import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.flowable.cmmn.api.delegate.DelegatePlanItemInstance;
import org.flowable.cmmn.api.delegate.PlanItemJavaDelegate;
import org.flowable.cmmn.api.history.HistoricMilestoneInstance;
import org.flowable.cmmn.api.runtime.CaseInstance;
import org.flowable.cmmn.api.runtime.PlanItemInstance;
import org.flowable.cmmn.engine.test.CmmnDeployment;
import org.flowable.cmmn.engine.test.FlowableCmmnTestCase;
import org.flowable.common.engine.api.FlowableIllegalArgumentException;
import org.flowable.common.engine.api.FlowableObjectNotFoundException;
import org.flowable.common.engine.impl.util.CollectionUtil;
import org.flowable.variable.api.history.HistoricVariableInstance;
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
public class VariablesTest extends FlowableCmmnTestCase {
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    @CmmnDeployment
    public void testGetVariables() {
        Map<String, Object> variables = new HashMap<>();
        variables.put("stringVar", "Hello World");
        variables.put("intVar", 42);
        CaseInstance caseInstance = cmmnRuntimeService.createCaseInstanceBuilder().caseDefinitionKey("myCase").variables(variables).start();
        Map<String, Object> variablesFromGet = cmmnRuntimeService.getVariables(caseInstance.getId());
        Assert.assertTrue(variablesFromGet.containsKey("stringVar"));
        Assert.assertEquals("Hello World", ((String) (variablesFromGet.get("stringVar"))));
        Assert.assertTrue(variablesFromGet.containsKey("intVar"));
        Assert.assertEquals(42, ((Integer) (variablesFromGet.get("intVar"))).intValue());
        Assert.assertEquals("Hello World", ((String) (cmmnRuntimeService.getVariable(caseInstance.getId(), "stringVar"))));
        Assert.assertEquals(42, ((Integer) (cmmnRuntimeService.getVariable(caseInstance.getId(), "intVar"))).intValue());
        Assert.assertNull(cmmnRuntimeService.getVariable(caseInstance.getId(), "doesNotExist"));
    }

    @Test
    @CmmnDeployment
    public void testSetVariables() {
        CaseInstance caseInstance = cmmnRuntimeService.createCaseInstanceBuilder().caseDefinitionKey("myCase").start();
        Map<String, Object> variables = new HashMap<>();
        variables.put("stringVar", "Hello World");
        variables.put("intVar", 42);
        cmmnRuntimeService.setVariables(caseInstance.getId(), variables);
        Assert.assertEquals("Hello World", ((String) (cmmnRuntimeService.getVariable(caseInstance.getId(), "stringVar"))));
        Assert.assertEquals(42, ((Integer) (cmmnRuntimeService.getVariable(caseInstance.getId(), "intVar"))).intValue());
        Assert.assertNull(cmmnRuntimeService.getVariable(caseInstance.getId(), "doesNotExist"));
    }

    @Test
    @CmmnDeployment
    public void testRemoveVariables() {
        Map<String, Object> variables = new HashMap<>();
        variables.put("stringVar", "Hello World");
        variables.put("intVar", 42);
        CaseInstance caseInstance = cmmnRuntimeService.createCaseInstanceBuilder().caseDefinitionKey("myCase").variables(variables).start();
        Assert.assertEquals(2, cmmnRuntimeService.getVariables(caseInstance.getId()).size());
        cmmnRuntimeService.removeVariable(caseInstance.getId(), "stringVar");
        Assert.assertEquals(1, cmmnRuntimeService.getVariables(caseInstance.getId()).size());
        Assert.assertNull(cmmnRuntimeService.getVariable(caseInstance.getId(), "StringVar"));
        Assert.assertNotNull(cmmnRuntimeService.getVariable(caseInstance.getId(), "intVar"));
    }

    @Test
    @CmmnDeployment
    public void testSerializableVariable() {
        Map<String, Object> variables = new HashMap<>();
        variables.put("myVariable", new VariablesTest.MyVariable("Hello World"));
        CaseInstance caseInstance = cmmnRuntimeService.createCaseInstanceBuilder().caseDefinitionKey("myCase").variables(variables).start();
        VariablesTest.MyVariable myVariable = ((VariablesTest.MyVariable) (cmmnRuntimeService.getVariable(caseInstance.getId(), "myVariable")));
        Assert.assertEquals("Hello World", myVariable.value);
    }

    @Test
    @CmmnDeployment
    public void testResolveMilestoneNameAsExpression() {
        Map<String, Object> variables = new HashMap<>();
        variables.put("myVariable", "Hello from test");
        CaseInstance caseInstance = cmmnRuntimeService.createCaseInstanceBuilder().caseDefinitionKey("myCase").variables(variables).start();
        assertCaseInstanceEnded(caseInstance);
        HistoricMilestoneInstance historicMilestoneInstance = cmmnHistoryService.createHistoricMilestoneInstanceQuery().milestoneInstanceCaseInstanceId(caseInstance.getId()).singleResult();
        Assert.assertEquals("Milestone Hello from test and delegate", historicMilestoneInstance.getName());
    }

    @Test
    @CmmnDeployment
    public void testHistoricVariables() {
        Map<String, Object> variables = new HashMap<>();
        variables.put("stringVar", "test");
        variables.put("intVar", 123);
        variables.put("doubleVar", 123.123);
        CaseInstance caseInstance = cmmnRuntimeService.createCaseInstanceBuilder().caseDefinitionKey("myCase").variables(variables).start();
        // verify variables
        Assert.assertEquals("test", cmmnRuntimeService.getVariable(caseInstance.getId(), "stringVar"));
        HistoricVariableInstance historicVariableInstance = cmmnHistoryService.createHistoricVariableInstanceQuery().variableName("stringVar").singleResult();
        Assert.assertEquals(caseInstance.getId(), historicVariableInstance.getScopeId());
        Assert.assertEquals(CMMN, historicVariableInstance.getScopeType());
        Assert.assertEquals("test", historicVariableInstance.getValue());
        Assert.assertNull(historicVariableInstance.getSubScopeId());
        Assert.assertEquals(123, cmmnRuntimeService.getVariable(caseInstance.getId(), "intVar"));
        historicVariableInstance = cmmnHistoryService.createHistoricVariableInstanceQuery().variableName("intVar").singleResult();
        Assert.assertEquals(caseInstance.getId(), historicVariableInstance.getScopeId());
        Assert.assertEquals(CMMN, historicVariableInstance.getScopeType());
        Assert.assertEquals(123, historicVariableInstance.getValue());
        Assert.assertNull(historicVariableInstance.getSubScopeId());
        Assert.assertEquals(123.123, cmmnRuntimeService.getVariable(caseInstance.getId(), "doubleVar"));
        historicVariableInstance = cmmnHistoryService.createHistoricVariableInstanceQuery().variableName("doubleVar").singleResult();
        Assert.assertEquals(caseInstance.getId(), historicVariableInstance.getScopeId());
        Assert.assertEquals(CMMN, historicVariableInstance.getScopeType());
        Assert.assertEquals(123.123, historicVariableInstance.getValue());
        Assert.assertNull(historicVariableInstance.getSubScopeId());
        // Update variables
        Map<String, Object> newVariables = new HashMap<>();
        newVariables.put("stringVar", "newValue");
        newVariables.put("otherStringVar", "test number 2");
        cmmnRuntimeService.setVariables(caseInstance.getId(), newVariables);
        Assert.assertEquals("newValue", cmmnRuntimeService.getVariable(caseInstance.getId(), "stringVar"));
        Assert.assertEquals("newValue", cmmnHistoryService.createHistoricVariableInstanceQuery().variableName("stringVar").singleResult().getValue());
        Assert.assertEquals("test number 2", cmmnHistoryService.createHistoricVariableInstanceQuery().variableName("otherStringVar").singleResult().getValue());
        // Delete variables
        cmmnRuntimeService.removeVariable(caseInstance.getId(), "stringVar");
        Assert.assertNull(cmmnRuntimeService.getVariable(caseInstance.getId(), "stringVar"));
        Assert.assertNull(cmmnHistoryService.createHistoricVariableInstanceQuery().variableName("stringVar").singleResult());
        Assert.assertNotNull(cmmnHistoryService.createHistoricVariableInstanceQuery().variableName("otherStringVar").singleResult());
    }

    @Test
    @CmmnDeployment
    public void testTransientVariables() {
        Map<String, Object> variables = new HashMap<>();
        variables.put("transientStartVar", "Hello from test");
        CaseInstance caseInstance = cmmnRuntimeService.createCaseInstanceBuilder().caseDefinitionKey("myCase").transientVariables(variables).start();
        HistoricMilestoneInstance historicMilestoneInstance = cmmnHistoryService.createHistoricMilestoneInstanceQuery().milestoneInstanceCaseInstanceId(caseInstance.getId()).singleResult();
        Assert.assertEquals("Milestone Hello from test and delegate", historicMilestoneInstance.getName());
        // Variables should not be persisted
        Assert.assertEquals(0, cmmnRuntimeService.getVariables(caseInstance.getId()).size());
        Assert.assertEquals(0, cmmnHistoryService.createHistoricVariableInstanceQuery().caseInstanceId(caseInstance.getId()).count());
    }

    @Test
    @CmmnDeployment
    public void testBlockingExpressionBasedOnVariable() {
        // Blocking
        CaseInstance caseInstance = cmmnRuntimeService.createCaseInstanceBuilder().caseDefinitionKey("testBlockingExpression").variable("nameVar", "First Task").variable("blockB", true).start();
        List<PlanItemInstance> planItemInstances = cmmnRuntimeService.createPlanItemInstanceQuery().caseInstanceId(caseInstance.getId()).planItemInstanceStateActive().orderByName().asc().list();
        Assert.assertEquals(2, planItemInstances.size());
        Assert.assertEquals("B", planItemInstances.get(0).getName());
        Assert.assertEquals("First Task", planItemInstances.get(1).getName());
        // Non-blocking
        caseInstance = cmmnRuntimeService.createCaseInstanceBuilder().caseDefinitionKey("testBlockingExpression").variable("nameVar", "Second Task").variable("blockB", false).start();
        planItemInstances = cmmnRuntimeService.createPlanItemInstanceQuery().caseInstanceId(caseInstance.getId()).planItemInstanceStateActive().list();
        Assert.assertEquals(1, planItemInstances.size());
        Assert.assertEquals("Second Task", planItemInstances.get(0).getName());
    }

    @Test
    @CmmnDeployment(resources = "org/flowable/cmmn/test/task/CmmnTaskServiceTest.testOneHumanTaskCase.cmmn")
    public void setVariableOnRootCase() {
        CaseInstance caseInstance = cmmnRuntimeService.createCaseInstanceBuilder().variable("varToUpdate", "initialValue").caseDefinitionKey("oneHumanTaskCase").start();
        cmmnRuntimeService.setVariable(caseInstance.getId(), "varToUpdate", "newValue");
        CaseInstance updatedCaseInstance = cmmnRuntimeService.createCaseInstanceQuery().caseInstanceId(caseInstance.getId()).includeCaseVariables().singleResult();
        MatcherAssert.assertThat(updatedCaseInstance.getCaseVariables().get("varToUpdate"), CoreMatchers.is("newValue"));
    }

    @Test
    @CmmnDeployment(resources = "org/flowable/cmmn/test/task/CmmnTaskServiceTest.testOneHumanTaskCase.cmmn")
    public void setVariableOnNonExistingCase() {
        this.expectedException.expect(FlowableObjectNotFoundException.class);
        this.expectedException.expectMessage("No case instance found for id NON-EXISTING-CASE");
        cmmnRuntimeService.setVariable("NON-EXISTING-CASE", "varToUpdate", "newValue");
    }

    @Test
    @CmmnDeployment(resources = "org/flowable/cmmn/test/task/CmmnTaskServiceTest.testOneHumanTaskCase.cmmn")
    public void setVariableWithoutName() {
        this.expectedException.expect(FlowableIllegalArgumentException.class);
        this.expectedException.expectMessage("variable name is null");
        cmmnRuntimeService.setVariable("NON-EXISTING-CASE", null, "newValue");
    }

    @Test
    @CmmnDeployment(resources = "org/flowable/cmmn/test/task/CmmnTaskServiceTest.testOneHumanTaskCase.cmmn")
    public void setVariableOnRootCaseWithExpression() {
        CaseInstance caseInstance = cmmnRuntimeService.createCaseInstanceBuilder().variable("varToUpdate", "initialValue").caseDefinitionKey("oneHumanTaskCase").start();
        cmmnRuntimeService.setVariable(caseInstance.getId(), "${varToUpdate}", "newValue");
        CaseInstance updatedCaseInstance = cmmnRuntimeService.createCaseInstanceQuery().caseInstanceId(caseInstance.getId()).includeCaseVariables().singleResult();
        MatcherAssert.assertThat("resolving variable name expressions does not make sense when it is set locally", updatedCaseInstance.getCaseVariables().get("varToUpdate"), CoreMatchers.is("newValue"));
    }

    @Test
    @CmmnDeployment(resources = { "org/flowable/cmmn/test/task/CmmnTaskServiceTest.testOneHumanTaskCase.cmmn", "org/flowable/cmmn/test/runtime/VariablesTest.rootProcess.cmmn" })
    public void setVariableOnSubCase() {
        cmmnRuntimeService.createCaseInstanceBuilder().caseDefinitionKey("rootCase").start();
        CaseInstance subCaseInstance = cmmnRuntimeService.createCaseInstanceQuery().caseDefinitionKey("oneHumanTaskCase").singleResult();
        cmmnRuntimeService.setVariable(subCaseInstance.getId(), "varToUpdate", "newValue");
        CaseInstance updatedCaseInstance = cmmnRuntimeService.createCaseInstanceQuery().caseInstanceId(subCaseInstance.getId()).includeCaseVariables().singleResult();
        MatcherAssert.assertThat(updatedCaseInstance.getCaseVariables().get("varToUpdate"), CoreMatchers.is("newValue"));
    }

    @Test
    @CmmnDeployment(resources = "org/flowable/cmmn/test/task/CmmnTaskServiceTest.testOneHumanTaskCase.cmmn")
    public void setVariablesOnRootCase() {
        CaseInstance caseInstance = cmmnRuntimeService.createCaseInstanceBuilder().variable("varToUpdate", "initialValue").caseDefinitionKey("oneHumanTaskCase").start();
        Map<String, Object> variables = Stream.of(new ImmutablePair<String, Object>("varToUpdate", "newValue")).collect(Collectors.toMap(Pair::getKey, Pair::getValue));
        cmmnRuntimeService.setVariables(caseInstance.getId(), variables);
        CaseInstance updatedCaseInstance = cmmnRuntimeService.createCaseInstanceQuery().caseInstanceId(caseInstance.getId()).includeCaseVariables().singleResult();
        MatcherAssert.assertThat(updatedCaseInstance.getCaseVariables(), CoreMatchers.is(variables));
    }

    @Test
    @CmmnDeployment(resources = "org/flowable/cmmn/test/task/CmmnTaskServiceTest.testOneHumanTaskCase.cmmn")
    public void setVariablesOnNonExistingCase() {
        this.expectedException.expect(FlowableObjectNotFoundException.class);
        this.expectedException.expectMessage("No case instance found for id NON-EXISTING-CASE");
        Map<String, Object> variables = Stream.of(new ImmutablePair<String, Object>("varToUpdate", "newValue")).collect(Collectors.toMap(Pair::getKey, Pair::getValue));
        cmmnRuntimeService.setVariables("NON-EXISTING-CASE", variables);
    }

    @SuppressWarnings("unchecked")
    @Test
    @CmmnDeployment(resources = "org/flowable/cmmn/test/task/CmmnTaskServiceTest.testOneHumanTaskCase.cmmn")
    public void setVariablesWithEmptyMap() {
        CaseInstance caseInstance = cmmnRuntimeService.createCaseInstanceBuilder().variable("varToUpdate", "initialValue").caseDefinitionKey("oneHumanTaskCase").start();
        this.expectedException.expect(FlowableIllegalArgumentException.class);
        this.expectedException.expectMessage("variables is empty");
        cmmnRuntimeService.setVariables(caseInstance.getId(), Collections.EMPTY_MAP);
    }

    @Test
    @CmmnDeployment(resources = { "org/flowable/cmmn/test/task/CmmnTaskServiceTest.testOneHumanTaskCase.cmmn", "org/flowable/cmmn/test/runtime/VariablesTest.rootProcess.cmmn" })
    public void setVariablesOnSubCase() {
        cmmnRuntimeService.createCaseInstanceBuilder().caseDefinitionKey("rootCase").start();
        CaseInstance subCaseInstance = cmmnRuntimeService.createCaseInstanceQuery().caseDefinitionKey("oneHumanTaskCase").singleResult();
        Map<String, Object> variables = CollectionUtil.singletonMap("varToUpdate", "newValue");
        cmmnRuntimeService.setVariables(subCaseInstance.getId(), variables);
        CaseInstance updatedCaseInstance = cmmnRuntimeService.createCaseInstanceQuery().caseInstanceId(subCaseInstance.getId()).includeCaseVariables().singleResult();
        MatcherAssert.assertThat(updatedCaseInstance.getCaseVariables(), CoreMatchers.is(variables));
    }

    // Test helper classes
    public static class MyVariable implements Serializable {
        private static final long serialVersionUID = 1L;

        private String value;

        public MyVariable(String value) {
            this.value = value;
        }
    }

    public static class SetVariableDelegate implements PlanItemJavaDelegate {
        @Override
        public void execute(DelegatePlanItemInstance planItemInstance) {
            String variableValue = ((String) (planItemInstance.getVariable("myVariable")));
            planItemInstance.setVariable("myVariable", (variableValue + " and delegate"));
        }
    }

    public static class SetTransientVariableDelegate implements PlanItemJavaDelegate {
        @Override
        public void execute(DelegatePlanItemInstance planItemInstance) {
            String variableValue = ((String) (planItemInstance.getVariable("transientStartVar")));
            planItemInstance.setTransientVariable("transientVar", (variableValue + " and delegate"));
        }
    }
}

